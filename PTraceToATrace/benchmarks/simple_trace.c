#include "stdlib.h"
#include "assert.h"
#include "stdio.h"

int main()
{
    size_t element_size = 400;
    size_t num_of_elements = 10;
    size_t resize_factor = 2;

    // --- Malloc tests ---
    printf("==== MALLOC TESTS START ====\n");

    void* ptr_malloc_a = malloc(element_size * num_of_elements);
    assert(ptr_malloc_a != NULL);
    
    void* ptr_malloc_b = malloc(element_size * num_of_elements);
    assert(ptr_malloc_b != NULL);

    free(ptr_malloc_a);
    free(ptr_malloc_b);

    printf("==== MALLOC TESTS END ====\n");
    printf("\n");

    // // --- Realloc tests ---
    // printf("==== REALLOC TESTS START ====\n");

    // void* ptr_realloc = malloc(element_size * num_of_elements);
    // assert(ptr_realloc != NULL);
    
    // ptr_realloc = realloc(ptr_realloc, element_size * num_of_elements * resize_factor);
    // assert(ptr_realloc != NULL);
    // free(ptr_realloc);

    // printf("==== REALLOC TESTS END ====\n");
    // printf("\n");

    // // --- Calloc tests ---
    // printf("==== CALLOC TESTS START ====\n");

    // void* ptr_calloc = calloc(num_of_elements, element_size);
    // assert(ptr_calloc != NULL);
    // free(ptr_calloc);

    // printf("==== CALLOC TESTS END ====\n");
    // printf("\n");
}