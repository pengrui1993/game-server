#!/bin/bash

LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/lib/
export LD_LIBRARY_PATH
./write $@