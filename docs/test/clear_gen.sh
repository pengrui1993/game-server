#!/bin/bash
ARGV=$@
ARGC=$#
nodes=$(cat ./nodes)

function clear_all_files(){
  for str in ${nodes[@]};
  do
    rm -f $str.log $str.pid $str.in
  done
}

clear_all_files