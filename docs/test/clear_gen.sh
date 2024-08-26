#!/bin/bash
ARGV=$@
ARGC=$#
nodes=$2
dir0=$1
function clear_all_files(){
  for node in ${nodes[@]};
  do
    local str=$dir0/$node
    rm -f $str.log $str.pid $str.in
  done
}

clear_all_files