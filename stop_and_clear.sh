#!/bin/bash
ARGV=$@
ARGC=$#

function fifo_clear(){
  cd ./docs/test && ./all_fifo_op.sh "clear"
}
fifo_clear
./clear_gen.sh
./stop_all_jar_suffix.sh