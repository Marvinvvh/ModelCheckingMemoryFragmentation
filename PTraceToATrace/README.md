# Program Trace to Allocation Trace (PTracetoATrace)
This program is used to create and convert program traces into allocations traces, which are used in the benchmark. The inputs and traces are included under inputs and dump_traces, respectively. But the dependencies, source code of external benchmarks, and the executables are excluded. Note we only use trace 0-2. We will use ${ROOT} to refer to the root workspace.

## Architecture overview
The workings of the program are based around wrapping malloc/free calls, live converting the program traces into allocations traces, and then logging these traces. The wrapping occurs during linking, -Wl, activates the wrapper, and --wrap=FN starts a process where the FN call is instead linked to __real_FN, and a __wrap_FN call is created. The __wrap_FN call can be redefined using your own definition, capturing the original function call.  Using wraps we capture the malloc/free calls, and convert them into an allocation trace by tracking the addresses of the malloc/free calls. Mallocs without allocated addresses are added into a red-black tree, for fast lookups, and logged as allocation action. If a free is wrapped with the same address, we know that the free is associated with an allocation, retrieve its pointer from the RB-tree, and log the free allocation action. 

There are two groups of headers and source files:
* at_logger: The allocation trace logger. Its a logging interface for malloc/free program calls, and the alloc/free allocation actions.
* pt_to_at: Contains the wrapper calls, and allocation trace logic.

## Build
The process of building PTraceToATrace is done using the build.sh script. It sets up executables under build/BENCHMARK_HERE. 
Dependencies and external benchmarks are not included. Only the simple trace is buildable without the external benchmarks.

### Tooling
Compiled and tested with GCC 11.2 on a 7.2 Linux kernel and 32 GB RAM. Other platforms have not been tested, nor is the setup meant to be multi-platform.

### External benchmarks
External benchmarks should be placed under benchmarks/external/BENCHMARK_HERE.

There are two benchmarks, both provided in the mimalloc-benchmark by Daan Leijen, Ben Zorn, and Leonardo de Moura(https://www.microsoft.com/en-us/research/publication/mimalloc-free-list-sharding-in-action/):
* Larson. A simulation program of multiple threads by Paul Larson. Retrievable from 'https://github.com/daanx/mimalloc-bench/tree/master/bench/barnes'.
* cfrac. A simulation program of the continued fraction factoring algorithm by Dave Barrett. Retrievable from 'https://github.com/daanx/mimalloc-bench/tree/master/bench/cfrac'.

### Expected libs
PTtoAT has three library dependencies to function:
* libjansson, by Petri Lehtinen, for JSON logging: https://github.com/akheron/jansson
* tree/null, from OpenBSD(tree: Niels Provos and David Gwynne, null: Todd C. Miller), for an RB tree structure: https://github.com/openbsd/src/blob/master/sys/sys/tree.h, https://github.com/openbsd/src/blob/master/sys/sys/_null.h

The libjansson headers are expected under "${ROOT}/lib/libjansson/include", and ${ROOT}/lib/libjansson/libjansson.a for the binary.
The tree headers are expected under ${ROOT}/lib/tree/include.

### Building
Building the benchmark is performed by using the ${ROOT}/build.sh script. It builds the benchmark programs and an example trace. Without the external benchmarks, only the simple trace is buildable.

## Run
Running the benchmark can be done using the ${ROOT}/run.sh script. The build should be executed beforehand. 
