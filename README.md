# Dataset and Source Code MODEL CHECKING MEMORY FRAGMENTATION CAUSED BY ALLOCATOR POLICIES USING TIMED AUTOMATA
This repository contains the dataset and source code of the thesis as mentioned above. Both are created by Marvin van Heese for this thesis.
The following items are in this directory, which we will discuss separately, if applicable:
1. Benchmark: Contains the data related to the evaluation benchmark. This includes traces, and the datasets used in the analysis. Note that the configurations of TraceVerifier are placed under 'Configurations'.
2. Model: Contains the base model used during simulation, and the bisim_model, which is used during verification. Note that these are UPPAAL 5 models. The models themselves contain additional information under 'Declarations' how to set up the model, and will therefore not be expanded upon. A preconfigured example model is also provided, which contains an FFAO-64 configuration, and is directly simulatable using UPPAAL. We recommend disabling diagnostic traces due to memory usage. 
3. PTraceToATrace: Contains PTraceToATrace, which builds the benchmark programs for cfrac and larson, and generates allocation traces when ran. It contains the source code, inputs for the programs, its intermediate allocation traces, and a simple benchmark. Note that we do not include the external benchmarks or their executables due to licensing. Not expanded upon, contains an additional README.
4. Testing: Contains the fragverif and verification datasets. The fragverif dataset is used in verifying the points and fragmentation values in TraceAnalyzer, and the verification dataset contains the verification queries that have been executed and successfully passed.
5. TraceAnalyzer: Contains TraceAnalyzer, which performs preparing datasets for analysis, policy comparisons, simulation performance analysis, similarity comparisons between datasets, point and fragmentation verification with fragverif, and point scaling tests. It contains the source code and tests. The datasets are retrieved from the other folders. Not expanded upon, contains an additional README.
6. TraceVerifier: Contains TraceVerifier, which generates allocation traces, performs the simulations of the benchmark and verification, and creates the datasets for analysis. It contains the source code, and scripts. Not expanded upon, contains an additional README.
7. Configurations: The different configurations used for TraceVerifier to generate the different traces, pipelines, and datasets.

Some of these directories contain an execution.log file, which shows the queries that have been executed, and their status.

Both benchmark and testing directories contain *_raw folders. These contain data from TraceVerifier. In general, these directories contain an execution.log at the top (except verification in test, explained later), and a run_0 directory. The general layout in the run directory is in order:
1. trace-group, which contains all traces for that group.
2. trace, and a trace-group json, which is an unused fragment.
3. trace*.json file, which contains the trace, and the model directories.
4. model, which contains a merged model (has the trace, and config used, simulatable!), model config, model variables, and the query group.
5. query group, which contains all queries for that group in json files. This contains the query results.

## Benchmark
The benchmark_datasets directory contains the raw and parsed dataset of the benchmark. The raw dataset is the results of TraceVerifier, and the parsed data is created using TraceAnalyzer. It is the parsed dataset which is analyzed from the policy comparison.

The memtrack directory contains the mem_track_full.txt file that is used to analyze the simulation performance in TraceAnalyzer. Note that it is the combination of the FFAO, BFAO, and NFAO 'mem_track.txt' files in their associated directories: mem_track_FFAO, mem_track_BFAO*, and mem_track_NFAO directories. We also put a copy of each mem_track in the mem_track_composition directory. These also have been prepared, in numbered order, as to how they form mem_track_full.txt. Which includes removing the crash data, and the headers. Note that not all models are included in the results. TraceVerifier only stores the latest successful one as the mem_track files are the main artefact.

The traces folder contains the allocation traces for the benchmark (allocation_traces_256KB), stored within synthetic and program directories. The program traces used to create those allocation traces are in the 'program_traces_256KB' directory. Lastly, the fragverif traces are placed in the 'allocation_traces'frag_verif' directory. 

Note that the configurations are stored separately in 'Configurations'.

## Testing
The testing directory contains the fragverif benchmarks. The fragverif_raw and fragverif__rerun datasets are used for similarity comparisons in TraceAnalyzer, with the raw variant also being used to create the parsed variant. The parsed variant is used in point/fragmentation analysis in TraceAnalyzer.

The verification dataset holds all verification queries and models, created during the verification pipeline in TraceVerifier. To view the actual queries we recommend using/accessing TraceVerifier. The execution logs are not stored per type of verification for each merged model, e.g., 'run_0/merged-model_0_BFAO-1-size-multiple/BFAO/execution.log'.

## Configurations
The pipeline_config directory contains the pipeline configurations required to start-up different pipelines in TraceVerifier. These configurations use absolute paths for the output and UPPAAL server locations. Therefore, their default value is invalid and must be replaced before attempting to run TraceVerifier. This is explained in its own README.

The trace_gen and trace_gen_fragverif folders contains the synthetic trace settings for the benchmark and fragverif simulation, respectively. These are not generally reused within TraceVerifier (possible though), and are artefacts to verify whether the seed is correct.

The allocators directory contains examples of allocator configurations. These are placed alongside the models in the result output.
