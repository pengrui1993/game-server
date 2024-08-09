#!/bin/bash

ARGV=$@
ARGC=$#
nodes=$(cat ./docs/test/nodes)
ROOT_PWD=$(pwd)
REF_DIR=$ROOT_PWD/docs/test

function fifo_clear(){
  cd $REF_DIR
  ./all_fifo_op.sh "clear"
  cd $ROOT_PWD
}
function fifo_create(){
  cd $REF_DIR
  ./all_fifo_op.sh "create"
  cd $ROOT_PWD
}
function bus_preparing(){
  while test 1
  do
    local ret=$(curl http://localhost:12999/api/http/bus/prepared)
    if [[ $ret -eq 1 ]] ; then
      echo 'call prepared not 1'
      return 1;
    fi
    echo 'try prepared again'
    sleep 1
  done
  #echo ",1 is ok"

}
function check(){
  if [[ ! $ARGC -eq 1 ]];
  then
    echo "require one of:"
    for str in ${nodes[@]}; do
      if [[ "bus" != $str ]]; then
        echo $str
      fi
    done
    exit 0;
  fi
}
function start_nodes(){
  fifo_clear
  fifo_create
  for str in ${nodes[@]};
  do
    local fifo_path=/tmp/fifo_$str
    echo $str" start_nodes"
    rm -f $str.log $str.pid
    #rm -f /tmp/fifo_bus && mkfifo /tmp/fifo_bus
    # java -Xss1m -Xms32m -jar ./bus/target/bus-1.0-SNAPSHOT.jar 0</tmp/fifo_bus
    nohup java -Xss1m -Xms32m -jar $(ls ./$str/target/*.jar) $fifo_path 2>&1 1>$str.log 0<$fifo_path &
    #echo '\n' > /tmp/fifo_bus
    echo '\n' > $fifo_path
    #echo 'quit' > /tmp/fifo_bus
    if [[ $str == "bus" ]];then
      echo "try to check bus server running"
      bus_preparing
    fi
    echo "started "$str
  done
}
function stop_nodes_by_ps(){
  for str in ${nodes[@]};
  do
    local in_file=$str".pid";
    if [[ -f $in_file ]];then
      local pid=$(cat $in_file)
      local ps_line=$(ps aux |grep $pid|grep java)
      local get_pid=$(echo $ps_line|awk '{print $2}')
#        echo 'abc/def/gh '|cut -d/ -f2     #got def
      if [[ ""!="$get_pid" ]];then
        echo $get_pid
        kill -9 $get_pid
      fi
    fi
  done
}

function bus_check(){
  if [[ "bus" == $1 ]]; then
    echo 'bus cannot be debug'
    exit 0
  fi
}
check
bus_check $1
start_nodes
tail -f $PWD/bus.log #1>/tmp/fifo_bus #no working , tail output not under control
#./docs/test/stop_all_jar_suffix.sh
