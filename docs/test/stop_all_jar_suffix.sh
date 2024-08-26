#!/bin/bash

function force_stop_nodes_jar(){
  for pid in $(jps|grep jar|cut -d' ' -f1);
  do
    kill -9 $pid
  done
}

force_stop_nodes_jar