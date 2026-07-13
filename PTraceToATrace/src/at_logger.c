#include <fcntl.h>
#include <string.h>
#include <unistd.h>
#include <assert.h>

#include "at_logger.h"
#include "jansson.h"
#include "unistd.h"
#include "pthread.h"

// Extern defs to use original functions 
extern void* __real_malloc(size_t __size);
extern void __real_free(void *__ptr);
extern void* __real_calloc(size_t __nmemb, size_t __size);
extern void* __real_realloc(void *__ptr, size_t __size);

struct logger_t
{
    pthread_mutex_t logMutex;
    struct log_settings_t logSettings;
    int fd_alloc_trace;
    int fd_program_trace;

    bool open;
};

size_t log_init(struct log_settings_t givenSettings, void **loggerRRef)
{
    struct logger_t *logger = (struct logger_t*) __real_malloc(sizeof(struct logger_t));
    assert(logger != NULL);
    pthread_mutex_lock(&logger->logMutex);
    if(logger->open == true)
    {
        pthread_mutex_unlock(&logger->logMutex);
        return eLogError_MultipleInitCalls;
    }

    // We start with no file descriptors
    logger->fd_alloc_trace = 0; 
    logger->fd_program_trace = 0;

    struct log_settings_t *settings = &logger->logSettings;
    memcpy(settings, &givenSettings, sizeof(givenSettings));

    if(access(settings->allocTraceLocation, F_OK) != 0 && !settings->createIfNonExistent)
    {
        *loggerRRef = NULL;
        pthread_mutex_unlock(&logger->logMutex);
        return eLogError_FileError;
    }
    
    #if GENERATE_PROG_TRACE
    if(access(settings->programTraceLocation, F_OK) != 0 && !settings->createIfNonExistent)
    {
        *loggerRRef = NULL;
        pthread_mutex_unlock(&logger->logMutex);
        return eLogError_FileError;
    }
    #endif

    int openFlags = O_WRONLY | O_TRUNC;
    if(settings->createIfNonExistent)
    {
        openFlags |= O_CREAT;
    }
    
    int fd = open(settings->allocTraceLocation, openFlags, 0644); // RW permissions, exec not required.
    logger->fd_alloc_trace = fd;

    // Check if we can log
    if(fd < 0)
    {
        *loggerRRef = NULL;
        pthread_mutex_unlock(&logger->logMutex);
        return eLogError_FileError;
    }

    #if GENERATE_PROG_TRACE
    fd = open(settings->programTraceLocation, openFlags, 0644); // RW permissions, exec not required.
    logger->fd_program_trace = fd;

    // Check if we can log
    if(fd < 0)
    {
        *loggerRRef = NULL;
        pthread_mutex_unlock(&logger->logMutex);
        return eLogError_FileError;
    }
    #endif

    // Ensure that there's no linking to malloc/free, which are to be wrapped. 
    // Jansson lib has undefined references for malloc/free/realloc so this would cause calls to __wrap_*.
    json_set_alloc_funcs(__real_malloc, __real_free);

    logger->open = true;
    pthread_mutex_unlock(&logger->logMutex);

    *loggerRRef = logger;
    return eLogError_None;
}

size_t log_close(void* loggerRef)
{
    struct logger_t *logger = (struct logger_t*)loggerRef;
    pthread_mutex_lock(&logger->logMutex);

    if(!logger->open)
    {
        pthread_mutex_unlock(&logger->logMutex);
        return eLogError_MultipleCloseCalls;
    }

    // Flush to file
    if(fsync(logger->fd_alloc_trace) != 0)
    {
        pthread_mutex_unlock(&logger->logMutex);
        return eLogError_FileError;
    }

    size_t err_close = close(logger->fd_alloc_trace) || close(logger->fd_program_trace);
    if(err_close == 0)
    {
        logger->open = false;
    }

    pthread_mutex_unlock(&logger->logMutex);
    return err_close == 0 ? eLogError_None : eLogError_FileError;
}

size_t log_program_malloc(void* loggerRef, struct malloc_params_t params)
{
    // Create json string to log
    struct logger_t *logger = (struct logger_t*)loggerRef;
    json_t* jsonLine = json_pack("{s:s, s:i, s:i, s:i}", 
        "type", "Malloc",
        "id", params.id,
        "address", params.address, 
        "size", params.size);
    char *jsonStr = json_dumps(jsonLine, 0);

    // Write to file
    pthread_mutex_lock(&logger->logMutex);
    write(logger->fd_program_trace, jsonStr, strlen(jsonStr));
    write(logger->fd_program_trace, "\n", 1);
    pthread_mutex_unlock(&logger->logMutex);

    // Prevent __wrap_* calls
    __real_free(jsonStr);
    json_decref(jsonLine);
    return eLogError_None;       
}

size_t log_program_free(void* loggerRef, struct free_params_t params)
{
    // Create json string to log
    struct logger_t *logger = (struct logger_t*)loggerRef;
    json_t* jsonLine = json_pack("{s:s, s:i, s:i}", 
        "type", "Free",
        "id", params.id,
        "address", params.address);
    char *jsonStr = json_dumps(jsonLine, 0);

    // Write to file
    pthread_mutex_lock(&logger->logMutex);
    write(logger->fd_program_trace, jsonStr, strlen(jsonStr));
    write(logger->fd_program_trace, "\n", 1);
    pthread_mutex_unlock(&logger->logMutex);

    // Prevent __wrap_* calls
    __real_free(jsonStr);
    json_decref(jsonLine);
    return eLogError_None;

}

size_t log_program_calloc(void* loggerRef, int address, int size, int amount)
{
    // Not supported.
    assert(false);
    return eLogError_InvalidCall;
}

size_t log_program_realloc(void* loggerRef, int address, int size)
{
    // Not supported.
    assert(false);
    return eLogError_InvalidCall;
}

size_t log_alloc_action(void *loggerRef, struct alloc_action_t alloc)
{
    // Create json string to log
    struct logger_t *logger = (struct logger_t*)loggerRef;
    json_t* jsonLine = json_pack("{s:s, s:i, s:i, s:i}", 
        "type", "Alloc",
        "id", alloc.id, 
        "pointer", alloc.pointer, 
        "size", alloc.size);
    char *jsonStr = json_dumps(jsonLine, 0);

    // Write to file
    pthread_mutex_lock(&logger->logMutex);
    write(logger->fd_alloc_trace, jsonStr, strlen(jsonStr));
    write(logger->fd_alloc_trace, "\n", 1);
    pthread_mutex_unlock(&logger->logMutex);

    // Prevent __wrap_* calls
    __real_free(jsonStr);
    json_decref(jsonLine);
    return eLogError_None;
}

size_t log_free_action(void *loggerRef, struct free_action_t free)
{
    // Create json string to log
    struct logger_t *logger = (struct logger_t*)loggerRef;
    json_t* jsonLine = json_pack("{s:s, s:i, s:i}", 
        "type", "Free",
        "id", free.id, 
        "pointer", free.pointer);
    char *jsonStr = json_dumps(jsonLine, 0);

    // Write to file
    pthread_mutex_lock(&logger->logMutex);
    write(logger->fd_alloc_trace, jsonStr, strlen(jsonStr));
    write(logger->fd_alloc_trace, "\n", 1);
    pthread_mutex_unlock(&logger->logMutex);

    // Prevent __wrap_* calls
    __real_free(jsonStr);
    json_decref(jsonLine);
    return eLogError_None;
}