#!/bin/bash
ARGV=$@
ARGC=$#
nodes=$(cat ./docs/test/nodes)

#./all_fifo_op.sh clear def
#echo ${CMD}
./docs/test/all_fifo_op.sh "clear" "$nodes"
sleep 1
./docs/test/clear_gen.sh $PWD "$nodes"
./docs/test/stop_all_jar_suffix.sh
