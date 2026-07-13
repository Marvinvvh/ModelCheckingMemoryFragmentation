#pragma once

#include <stddef.h>
#include <stdio.h>

#include "tree.h"
#include "pt_to_at.h"
#include "at_logger.h"

// Program trace to allocation trace wrappers. These definitions are used by the function wrapper of gcc.
// See https://sourceware.org/binutils/docs-2.38/ld.html for additional information on wrapping functions.

void *__wrap_malloc(size_t __size);
void __wrap_free (void *__ptr);
void *__wrap_calloc(size_t __nmemb, size_t __size);
void *__wrap_realloc (void *__ptr, size_t __size);