#!/bin/bash

# Run this script to generate the mem_track_full.txt file for TraceAnalyzer.
# Note that N/A's are handled in TraceAnalyzer.

cat 1_prep_mem_track_nfao.txt >> mem_track_full.txt
cat 2_prep_mem_track_ffao.txt >> mem_track_full.txt
cat 3_prep_mem_track_bfao_384CRASH.txt >> mem_track_full.txt
cat 4_prep_mem_track_bfao_384NO1024.txt >> mem_track_full.txt