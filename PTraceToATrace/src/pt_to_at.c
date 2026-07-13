#include <stdbool.h>
#include <assert.h>

#include "pt_to_at.h"
#include "at_logger.h"

// Defines 
#define PRINT_TREES false           // Print entire tree structure. Watch out for big traces!
#define PRINT_ACTIONS true          // Print all actions called.
#define OUTPUT_TO_FILE true         // Output traces to file.
#define GENERATE_PROG_TRACE false   // If we want to trace all calls

// This define should always be here, since we don't include non-malloc/free actions anymore.
#ifndef ASSERT_ONLY_MALLOC_FREE
#define ASSERT_ONLY_MALLOC_FREE
#endif

#ifndef TRACE_NAME
#define TRACE_NAME "trace.txt"
#endif

#ifndef PROG_TRACE_NAME
#define PROG_TRACE_NAME "prog_trace.txt"
#endif

#ifndef TRACE_DUMP_LOCATION
#define TRACE_DUMP_LOCATION NULL
#endif

// Extern defs to use the original functions 
extern void* __real_malloc(size_t __size);
extern void __real_free(void *__ptr);
extern void* __real_calloc(size_t __nmemb, size_t __size);
extern void* __real_realloc(void *__ptr, size_t __size);

// Private defs
struct alloc_node_t
{
    RB_ENTRY(alloc_node_t) entry;
    void *address;
    struct alloc_action_t allocAction;
};

// Prototypes
void _initialize();
void _process_alloc(void *ptr, size_t size, bool printTree);
void _process_free(void *ptr, bool printTree);
int _alloc_node_cmp(struct alloc_node_t *nodeA, struct alloc_node_t *nodeB);
void _print_alloc_tree(struct alloc_node_t *root);
void _print_alloc_node(struct alloc_node_t *node);

// Alloc tree-related macros
RB_HEAD(alloc_tree, alloc_node_t) s_allocTree = RB_INITIALIZER(&s_allocTree);   // Root of tree
RB_PROTOTYPE(alloc_tree, alloc_node_t, entry, _alloc_node_cmp)                  // Macro to setup functions for RB tree
RB_GENERATE(alloc_tree, alloc_node_t, entry, _alloc_node_cmp)                   // Generates the RB tree

// Statics for processing allocation actions
static bool s_initialized = false;
static void *s_logger = NULL;
static struct malloc_params_t s_lastMallocCall;
static struct free_params_t s_lastFreeCall;

static size_t s_nextID = 0;
static size_t s_nextPointer = 0;
static size_t s_nextEntry = 0;

// Keep track of function calls processed.
static size_t s_callCount; // Total calls
static size_t s_mallocCallCount;
static size_t s_freeCallCount;

// Keep track of actions processed.
static size_t s_actionCount;
static size_t s_allocActionCount;
static size_t s_freeActionCount;

// Compare nodes in tree. We use this to search the tree for an exact address match.
int _alloc_node_cmp(struct alloc_node_t *nodeA, struct alloc_node_t *nodeB)
{
    if(nodeA->address > nodeB->address) return 1;
    if(nodeA->address < nodeB->address) return -1;
    return 0;
}

// Wrap Implementations
void* __wrap_malloc(size_t __size)
{
    s_mallocCallCount++;
    s_callCount++;

    if(!s_initialized)
    {
        _initialize();
    }

    void *ptr = __real_malloc(__size);
    bool error = ptr == NULL;

    #if PRINT_ACTIONS
    printf("malloc called: size(%zu), ret_ptr(%p), error(%d)\n", __size, ptr, error);
    #endif

    struct malloc_params_t params = {
        .id = s_mallocCallCount,
        .address = (size_t)ptr,
        .size = __size
    };

    #if GENERATE_PROG_TRACE
    log_program_malloc(s_logger, params);
    #endif
    s_lastMallocCall = params;

    _process_alloc(ptr, __size, PRINT_TREES);
    return ptr;
}

void __wrap_free(void *__ptr)
{
    s_freeCallCount++;
    s_callCount++;

    if(!s_initialized)
    {
        _initialize();
    }

    #if PRINT_ACTIONS
    printf("free called: given_ptr(%p)\n", __ptr);
    #endif

    struct free_params_t params = {
        .id = s_freeCallCount,
        .address = (size_t)__ptr
    };
    #if GENERATE_PROG_TRACE
    log_program_free(s_logger, params);
    #endif
    s_lastFreeCall = params;

    _process_free(__ptr, PRINT_TREES);
    return __real_free(__ptr);
}

void* __wrap_calloc(size_t __nmemb, size_t __size)
{
    #ifdef ASSERT_ONLY_MALLOC_FREE
    assert(false);
    #endif
    
    return NULL;
    // Uncomment this when reintegrating calloc
    // if(!s_initialized)
    // {
    //     _initialize();
    // }

    // void *ptr = __real_calloc(__nmemb, __size);

    // bool error = ptr == NULL;
    // #if PRINT_ACTIONS
    // printf("calloc called: nmemb(%zu), size(%zu), ret_ptr(%p), error(%d)\n", __nmemb, __size, ptr, error);
    // #endif

    // log_calloc(s_logger, (size_t) ptr, __size, __nmemb);
    // _process_alloc(ptr, __nmemb * __size, PRINT_TREES);
    // return ptr;
}

// Realloc's definition has been altered. Due to the model not supporting any resizing will now:
// - Always attempt to reallocate a block at a different location.
// - If able to reallocate: returns a new pointer, and frees the given pointer.
// - Else: it returns the original pointer.
void* __wrap_realloc(void *__ptr, size_t __size)
{
    #ifdef ASSERT_ONLY_MALLOC_FREE
    assert(false);
    #endif
    
    return NULL;
    // Uncomment this when reintegrating realloc
    // if(!s_initialized)
    // {
    //     _initialize();
    // }

    // #if PRINT_ACTIONS
    // printf("! realloc called: given_ptr(%p), size(%zu)\n", __ptr, __size);
    // #endif
    // void *newPtr = __wrap_malloc(__size); 
    // bool error = newPtr == NULL;

    // // Freeing should only be done when a fitting block has been found.
    // if(!error)
    // {
    //     __wrap_free(__ptr); 
    // }

    // void *retPtr = error ? __ptr : newPtr;

    // #if PRINT_ACTIONS
    // printf("! realloc ret: given_ptr(%p), size(%zu), ret_ptr(%p), error(%d)\n", __ptr, __size, retPtr, error);
    // #endif

    // return retPtr;
}

void _initialize()
{
    s_initialized = true;

    struct log_settings_t logSettings = {
        .allocTraceLocation = 0,
        .programTraceLocation = 0,
        .createIfNonExistent = true,
        .clearFileInit = true,
    };

    // Note: Paths are not windows friendly!
    sprintf(logSettings.allocTraceLocation, "%s/%s", TRACE_DUMP_LOCATION, TRACE_NAME);
    sprintf(logSettings.programTraceLocation, "%s/%s", TRACE_DUMP_LOCATION, PROG_TRACE_NAME);

    size_t errLog = log_init(logSettings, &s_logger);
    errLog = log_init(logSettings, &s_logger);
    assert(errLog == eLogError_None);

    #if PRINT_ACTIONS
    printf("--- INIT DONE ---\n");
    #endif
}

void _cleanup()
{
   size_t errLog = log_close(s_logger);
    assert(errLog == eLogError_None);

    #if PRINT_ACTIONS
    printf("--- CLEAN-UP DONE ---\n");
    #endif
}

void _process_alloc(void *ptr, size_t size, bool printTree)
{
    s_allocActionCount++;
    s_actionCount++;

    // Ensure we process the right malloc call.
    assert(size == s_lastMallocCall.size);
    assert((uintptr_t)ptr == s_lastMallocCall.address);

    // Ensure that actions are in sync with trace
    assert(s_mallocCallCount == s_allocActionCount);
    assert(s_actionCount == s_callCount);

    // Generate new allocator action 
    struct alloc_action_t allocAction = 
    {
        .id = s_nextID,
        .pointer = s_nextPointer,
        .size = size,
    };

    s_nextID++;
    s_nextPointer++;

    // Register allocator action
    struct alloc_node_t *allocNode = __real_malloc(sizeof(struct alloc_node_t));
    assert(allocNode != NULL); // Can't have the converter cause any memory issues
    allocNode->allocAction = allocAction;
    allocNode->address = ptr;
    struct alloc_node_t *allocPtr = RB_INSERT(alloc_tree, &s_allocTree, allocNode);

    // For complete verification also check if there is any overlap. Either:
    // - quit on overlap (bad benchmark) 
    // - generate the same pointer so model can check
    // - ignore it.
    assert(allocPtr == NULL); // NULL on successful insertion (no duplicates)


    #if OUTPUT_TO_FILE
    size_t errLog = log_alloc_action(s_logger, allocAction);
    assert(errLog == eLogError_None);
    #endif

    if(printTree)
    {
        _print_alloc_tree(RB_ROOT(&s_allocTree));
    }
}

void _process_free(void *ptr, bool printTree)
{
    s_freeActionCount++;
    s_actionCount++;
    // Ensure we process the right free call.
    assert((uintptr_t)ptr == s_lastFreeCall.address);

    // Ensure that actions are in sync with trace
    assert(s_freeActionCount == s_freeCallCount);
    assert(s_actionCount == s_callCount);

    // Check for double free (costs more memory though)
    bool error_double_free = false; 
    assert(!error_double_free);

    struct alloc_node_t nodeMatch = {
        .address = ptr,
    };

    // Retrieve alloc action
    struct alloc_node_t *ptrAlloc = RB_FIND(alloc_tree, &s_allocTree, &nodeMatch);

    // Options for handling unalloced action:
    // - reserve a pointer and AllocAction
    // - assert failure
    assert(ptrAlloc != NULL && ptrAlloc->address == ptr);

    // Generate new free action
    struct free_action_t freeAction = 
    {
        .id = s_nextID,
        .pointer = ptrAlloc->allocAction.pointer,
    };

    s_nextID++;

    #if OUTPUT_TO_FILE
    size_t errLog = log_free_action(s_logger, freeAction);
    assert(errLog == eLogError_None);
    #endif

    // Clean up Alloc Action
    // - Add to previously freed tree if applicable
    // - Free up alloc action node
    ptrAlloc = RB_REMOVE(alloc_tree, &s_allocTree, ptrAlloc);
    __real_free(ptrAlloc);
    ptrAlloc = NULL;
    
    if(printTree)
    {
        _print_alloc_tree(RB_ROOT(&s_allocTree));
    }
}

void _print_alloc_tree(struct alloc_node_t *root)
{
    printf(">>>TREE: ");
    _print_alloc_node(root);
    printf(" <<<\n");
}

// Print function based on example in https://man.openbsd.org/tree#EXAMPLES
void _print_alloc_node(struct alloc_node_t *node)
{
    struct alloc_node_t *left = NULL, *right = NULL;

    if (node == NULL) {
        printf("nil");
        return;
    }

    left = RB_LEFT(node, entry);
    right = RB_RIGHT(node, entry);
    if (left == NULL && right == NULL)
        printf("%p", node->address);
    else {
        printf("%p(", node->address);
        _print_alloc_node(left);
        printf(",");
        _print_alloc_node(right);
        printf(")");
    }
}