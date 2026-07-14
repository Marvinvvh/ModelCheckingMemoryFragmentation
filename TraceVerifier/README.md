# TraceVerifier
The TraceVerifier program is responsible for generating synthetic traces, converting program traces, simulation, and verification. 

## Dependencies
The project has several dependencies.
* gson-2.13.1 by Google: Used to read and write from json. Retrievable from https://github.com/google/gson
* model.jar by the UPPAAL team(https://uppaal.org/): The UPPAAL Java API. See also https://docs.uppaal.org/toolsandapi/javaapi/
* picocli-4.7.7 (https://picocli.info/): Used to run the project through the command line.
* UPPAAL: A valid UPPAAL license is required to run UPPAAL. See https://uppaal.veriaal.dk/register.html for the registration process.

This project has been used on Linux and Java 24. We did not validate any other platforms, or other versions of Java.

## UPPAAL Resources
To run UPPAAL 5.0 through the API, several resources are required, as per UPPAAL v5.0.0. These include dtd files, and 'properties' files. However, with release 5.0 the files are not delivered alongside the jar file. Instead, these must be retrieved by either requesting these files directly from the UPPAAL team (https://uppaal.org/) until they are delivered alongside the jar file. 

## Overview Architecture
We provide an overview of the most important architecture aspects.

TraceVerifier has certain "runnables", i.e., ways to start-up a simulation or trace generation. These are positioned under TraceVerifier.Runnables, and split into two groups: PreFab (prefabricated), and TraceGen. Running a Runnable requires PicoCLI, and is explained in more detail later. We'll discuss the trace generation, simulation, and verification aspects of the project.

### Trace Generation
All trace generations falls under the 'TraceGen' runnables. For the thesis, we use TraceGenThesis256KB to generate the benchmark traces, and TraceGenFragVerif to generate the fragmentation verification traces. Trace generation, and general trace-related classes such as trace actions (i.e. alloc/free), traces, and trace groups, are placed under TraceVerifier.Trace. In short, a TraceAction represents the allocation actions, a trace is an ordered collection of these actions, and a trace group is a grouping of traces.

For synthetic trace generation TraceVerifier uses the generators under TraceVerifier.Trace.Generators. These generators are called in the runnables to create a trace according to some specification. Each generator has a configuration containing the settings for setting up the generator, which is saved later on during simulation for traceability. The configuration, e.g., seed, allocation sizes, ..., allows us to create a traceable benchmark. Trace generators also use more fundamental trace generators internally. For example, the weighted trace generator a synthetic trace where the allocation sizes, and chance of a certain action to occur is randomized using weights. 

The intermediate allocation traces, which we'll refer to as program traces, are parsed directly when reading a JSONL file containing a program trace. All program trace related classes are placed under TraceVerifier.Trace.ProgramTrace.

### Simulation
Each simulation runnable has the same overall structure: setup the models, select which queries to include, which traces to include, and then runs the StandardTracePipeline. Which queries are selected depends on the runnable. 

The StandardTracePipeline class performs the simulation. Once called it creates a merged model for each trace-model combination. A merged model is the base model, injected with the model settings and trace. After merging, it iterates over the queries and simulates the merged model and queries with TraceVerifier.Simulator.TraceSimulator.  During simulation a 'server' process launches, which is the UPPAAL simulation server. To get the results from a simulation you must pass a trace generator configuration, which contains the required paths for UPPAAL and the "base path". The results from the simulation are saved on-the-fly with the following structure:

In short, the results are as follows:
Run
|_ Trace Group: trace_group info (json; empty)
   |_ Trace: allocation trace (json)
      |_ Merged Model: model, model configuration, model variables
         |_ QueryCollection: Root Query Collection (json)
            |_ Queries (in-file): results
               |_ Data points from simulation queries.

To simulate we use different queries placed under TraceVerifier.Query. Each query has query data (i.e., the data executed on the server), and an optional comment, placed under the query in UPPAAL. To simplify simulation, we have several queries available such as pass-fail queries, which check some specification, and simulation queries, which track a variable over time.

The following simulation runnables are available:
* PrefabThesis256KBStripped, the main benchmark. Stripped as it is "stripped" to only fundamental traces.
* PrefabThesisFragVerif, the fragverif benchmark. Used in validating whether the fragmentation are correct using TraceAnalyzer.
* PrefabThesisMemCheck, the memcheck benchmark. It produces a mem_track.txt file, alongside the latest tested model. The mem_track file contains a log of the peak memory usage of the VM running the runnable, and the UPPAAL server during simulation. See the mem_track.sh script under resources.

### Verification
Besides simulation, TraceVerifier supports model verification with the PrefabThesis256KBVerif runnable. Instead of a StandardTracePipeline it uses a VerificationPipeline, which uses the mergers under TraceVerifier.Verification.Mergers to test different properties for whatever model is being included in the verification. The same concepts as the simulation apply for simulating and communicating with UPPAAL. Additional information of the properties, queries and traces, is placed within these classes and the thesis itself.

## Running a Runnable
To run the project we use PicoCLI to create several runnables. Running the program passes it through TraceVerifier.TraceVerifier.java. Here, the various commands that are supported by TraceVerifier are shown with their parameters. We'll provide some examples for calling TraceVerifier:

To generate the benchmark traces: trace-gen Thesis "PATH_DATASET_HERE/Benchmark/traces/program_traces_256KB" "PATH_DATASET_HERE/Benchmark/traces/allocation_traces_256KB" --seed 123 --gen-config-output-dir "PATH_DATASET_HERE/Configurations/trace_gen" --export-gen-config --clean-alloc-dirs --clean-config-dirs

To start the simulation benchmark: trace-gen Thesis "PATH_DATASET_HERE/Benchmark/traces/program_traces_256KB" "PATH_DATASET_HERE/Benchmark/traces/allocation_traces_256KB" --gen-config-output-dir "PATH_DATASET_HERE/Configurations/trace_gen" --export-gen-config --clean-alloc-dirs --clean-config-dirs

To launch the verification: pipeline-prefab -c "PATH_DATASET_HERE/Configurations/pipeline_config/pipeline-config-verif.json" ThesisVerif "PATH_DATASET_HERE/Model/base_model.xml" -t "PATH_DATASET_HERE/Benchmark/traces/allocation_traces_256KB"

