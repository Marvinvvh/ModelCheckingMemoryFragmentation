#!/bin/bash

# For each benchmark (and the example trace) we create a dir to put the binary and the directory (dump) for the traces.
# See https://sourceware.org/binutils/docs-2.38/ld.html for additional information on wrapping functions.

echo "--- BUILDING --- "
dump_location=$(dirname "$0")/dump_traces
mkdir -p ${dump_location}

echo "Dumping traces at $(readlink -f $dump_location)"

# simple_trace example, not used in benchmark
simple_dir="build/simple_trace"
simple_trace_dir="${dump_location}/simple_trace"
mkdir -p ${simple_dir}
mkdir -p ${simple_trace_dir}
gcc -o "${simple_dir}/wrapped" -DTRACE_NAME="\"simple_trace.trace\"" -DTRACE_DUMP_LOCATION="\"${simple_trace_dir}\"" \
    -I./include -I./lib/tree/include -I./lib/libjansson/include src/pt_to_at.c  src/at_logger.c benchmarks/simple_trace.c ./lib/libjansson/libjansson.a \
    -Wl,--wrap=malloc,--wrap=realloc,--wrap=calloc,--wrap=free

# --- These require external sources! Comment out if not retrieved ---
# larson
larson_dir="build/larson"
larson_trace_dir="${dump_location}/larson"
mkdir -p ${larson_dir}
mkdir -p ${larson_trace_dir}
gcc -o "${larson_dir}/wrapped" -DTRACE_NAME="\"larson.trace\"" -DTRACE_DUMP_LOCATION="\"${larson_trace_dir}\"" \
    -I./include -I./lib/tree/include -I./lib/libjansson/include src/pt_to_at.c  src/at_logger.c benchmarks/external/larson/larson.cpp ./lib/libjansson/libjansson.a \
    -Wl,--wrap=malloc,--wrap=realloc,--wrap=calloc,--wrap=free

# cfrac (important to add -lm as it requires math lib)
cfrac_dir="build/cfrac"
cfrac_trace_dir="${dump_location}/cfrac"
mkdir -p ${cfrac_dir}
mkdir -p ${cfrac_trace_dir}
gcc -o "${cfrac_dir}/wrapped" -DTRACE_NAME="\"cfrac.trace\"" -DTRACE_DUMP_LOCATION="\"${cfrac_trace_dir}\"" \
    -I./include -I./lib/tree/include -I./lib/libjansson/include -I./lib/benchmarks/external/cfrac/*.h src/pt_to_at.c src/at_logger.c benchmarks/external/cfrac/*.c ./lib/libjansson/libjansson.a \
    -Wl,--wrap=malloc,--wrap=realloc,--wrap=calloc,--wrap=free -lm