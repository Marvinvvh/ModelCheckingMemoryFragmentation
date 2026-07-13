# TraceAnalyzer

This tool has several purposes:

1. Preparing raw datasets into parsed datasets, usable for point verification and policy comparisons.
2. Policy comparisons, where the benchmark dataset is visualized.
3. Point verification, where a known trace is parsed to check whether the fragmentation values are as expected. We’ll refer to this as fragverif.
4. Similarity comparisons, where the content in two directories is compared. Specifically, we compare the JSON files, as these contain the data.
5. Memory analysis, which analyses the mem_track.txt files that contain the peak memory physical RAM usage for the server and VM during simulation.
6. Some pytest files to verify the memory fragmentation score scaling.

Except for #1 and #6, these all use jupyer notebooks (or equivalent) to visualize while executing the code.

We’ll discuss these purposes separately. We will use ${ROOT} to refer to the root workspace.

## Prerequisites

The requirements.txt file contains the pip dependencies. You can import these using python -m pip -s requirements.txt.

The tooling has been used with Python 3.10.12, ipykernel 6.30.1, IPython 8.37.0, Jupyter core 5.8.1, Jupyter vscode plugin v2025.9.1, on a Linux Machine (Pop!_OS 22.04 LTS). Other versions or operating systems have not been tested. We recommend using vscode or a derivative to reuse the launch options and the workspace file.

All notebooks and scripts use relative paths. In general, these are used to get to ${ROOT}/dataset, i.e., where this file lives, to get access to a dataset. We’ll discuss which datasets are required for which function later on.

## Preparing datasets

To prepare a dataset, such as the benchmark or fragverif, place the *_raw dataset into ${ROOT}/dataset.
Starting a prepare action requires calling src/prep_dataset.py, which has two modes. We’ll discuss the parameters to activate each:

1. To parse the benchmark: pass the absolute raw benchmark path and the absolute parsed benchmark path (i.e., where you want to store it).
2. To parse fragverif: pass the absolute raw fragverif path and the absolute parsed benchmark path (i.e., where you want to store it).

After parsing, place these datasets in ${ROOT}/data to use them in other functions of TraceAnalyzer.

If using vscode, you can use the two different launch options in ${ROOT}/.vscode/launch.json to perform the discussed actions. These will use the relative path to the dataset. So you only really have to place the *_raw datasets into ${ROOT}/dataset.

1. Debug Prep Dataset Benchmark creates the parsed version of the benchmark.
2. Debug Prep Dataset Comparison creates the parsed version of fragverif.

## Policy Comparison

If benchmark_parsed is available in the dataset directory, a policy comparison can be executed in ‘src/analysis_policy_comparison.ipynb’. This will visualize the plots of the benchmark evaluation.

## Point Verification

If the fragverif dataset is available in the dataset directory, the fragmentation/point verification can be executed using ‘src/analysis_point_verification.ipynb’.  The verification contains expected values from the parsed dataset. If these values do not match, an assert will fail, which will stop the execution and raise an error. As such, if there are no errors during execution, the verification was successful.

## Similarity Comparisons

To ensure that TraceVerifier is capable of reproducing traces and simulation results, we verify the program and synthetic allocation traces used in the benchmark, the benchmark results, and the fragverif results. The comparison can be performed using 'src/analysis_similarity.py' Comparisons are performed on the JSON files between two instances of the aforementioned directories, such as benchmark_raw and benchmark_raw_rerun. The notebook itself shows which directories are required. As we consistently store the data that is of importance within JSON files, if there are mismatches, we are notified of a failure as the results are printed out. If there is an interest in matched files too, these can be changed using the ‘show_matches’ (to show all matches), and ‘show_mismatches’ to show all mismatched files. Note that we perform a non-shallow comparison, so we compare file contents too, not only metadata. All the direcotires should be placed in the dataset directory.

## Memory analysis
The memory analysis of the simulation can be performed using 'src/analysis_memory.ipynb', which use the mem_track.txt files. It expects benchmark_memtrack in the dataset directory, where it reads the mem_track_full.txt file. If ran, the notebook parses the mem_track file into peak memory usage, and time spent, which is visualized.

## Point scaling tests
To verify some additional aspects of the point scaling functions, which are crucial for the comparison, we perform some unit tests. The unit tests are available in ${ROOT}/test. Pytest must be used to run the tests, which can be done by running pytest in the ${ROOT} directory. The test folder contains two test groups:
1. test_linear_point_scaling, which tests the linear point scaling.
2. test_step_point_scaling, which tests step-based point scaling.