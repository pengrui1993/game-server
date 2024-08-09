#!/bin/bash

function force_stop_nodes_jar(){
  for pid in $(jps|grep jar|cut -d' ' -f1);
  do
    kill -9 $pid
  done
  ./clear_gen.sh
}

force_stop_nodes_jar