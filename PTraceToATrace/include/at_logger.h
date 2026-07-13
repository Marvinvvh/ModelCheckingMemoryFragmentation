#pragma once

#include <stdint.h>
#include <stddef.h>
#include <stdio.h>
#include <stdbool.h>

#include "pt_to_at.h"
#include "linux/limits.h"

// The allocation trace (AT) logger is used to log the alloccation trace (although it also can log the program trace).
struct log_settings_t
{
    char allocTraceLocation[PATH_MAX];      // Locatino of allocation trace dump
    char programTraceLocation[PATH_MAX];    // Location of program trace dump
    bool createIfNonExistent;               // Create location if non-existent
    bool clearFileInit;                     // Clear the file. If false it appends.
};

// Call parameters
struct malloc_params_t
{
    size_t id;
    uintptr_t address;
    size_t size;
};

struct free_params_t
{
    size_t id;
    uintptr_t address;
};

// Allocation action parameters
struct alloc_action_t
{
    size_t id;
    size_t pointer;
    size_t size;
};

struct free_action_t
{
    size_t id;
    size_t pointer;
};

enum log_errors
{
    eLogError_None = 0,
    eLogError_FileError = 1,
    eLogError_MultipleInitCalls = 2,
    eLogError_MultipleCloseCalls = 3,
    eLogError_InvalidCall = 4
};

// Initializes all statics and settings. Should only be called once.
size_t log_init(struct log_settings_t settings, void **logger);

// Closes the log. Should only be called once.
size_t log_close(void* logger);

// Logging allocation trace
size_t log_alloc_action(void *logger, struct alloc_action_t alloc);
size_t log_free_action(void *logger, struct free_action_t free);

// Logging program trace
size_t log_program_malloc(void* loggerRef, struct malloc_params_t params);
size_t log_program_free(void* loggerRef, struct free_params_t params);