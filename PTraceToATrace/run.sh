#!/bin/bash

# For each benchmark (and the example trace) we run the binary. For all benchmarks we use several configurations.
dump_location=$(dirname "$0")/dump_traces
mkdir -p ${dump_location}
echo "Dumping traces at $(readlink -f $dump_location)"

echo "--- RUNNING EXAMPLE TRACE ---"
# simple_trace example, not used in benchmark
./build/simple_trace/wrapped

# --- These require external sources! Comment out if not retrieved ---
echo "--- RUNNING LARSON ---" 
./build/larson/wrapped < inputs/larson_0.txt
mv "$dump_location/larson/larson.trace" "$dump_location/larson/larson_0.trace"

./build/larson/wrapped < inputs/larson_1.txt
mv "$dump_location/larson/larson.trace" "$dump_location/larson/larson_1.trace"

./build/larson/wrapped < inputs/larson_2.txt
mv "$dump_location/larson/larson.trace" "$dump_location/larson/larson_2.trace"

echo "--- RUNNING CFRAC ---" 
./build/cfrac/wrapped $(<inputs/cfrac_0.txt)
mv "$dump_location/cfrac/cfrac.trace" "$dump_location/cfrac/cfrac_0.trace"

./build/cfrac/wrapped $(<inputs/cfrac_1.txt)
mv "$dump_location/cfrac/cfrac.trace" "$dump_location/cfrac/cfrac_1.trace"

./build/cfrac/wrapped $(<inputs/cfrac_2.txt)
mv "$dump_location/cfrac/cfrac.trace" "$dump_location/cfrac/cfrac_2.trace"
