#!/bin/bash

ARGV=$@
ARGC=$#
CMD=$1
nodes=$(cat ./nodes)
function clear_fifo(){
  for str in ${nodes[@]};
  do
    echo $str
    fifo_path=/tmp/fifo_$str
    if [[ -p $fifo_path ]];then
      echo "send quit command for "$fifo_path
      echo "quit" > $fifo_path &
      pid_file=$str.pid
      sleep 1
      if [[ -f $pid_file ]];then
        pid_data=$(cat $pid_file)
        if [[ $pid_data != "" ]];then
          kill $pid_data
        fi
      fi
      rm -f $fifo_path
    fi
  done
  echo "clear fifo done"
}

function create_fifo(){
  for str in ${nodes[@]};
  do
    fifo_path=/tmp/fifo_$str
    if [[ -e $fifo_path ]];then
      rm -f $fifo_path
    fi
    mkfifo $fifo_path
    nohup echo 'test' > $fifo_path &
  done
  echo "create fifo ok"
}

function switch_case(){
  if [[ $ARGC -lt 1 ]];then
    echo 'require clear or create'
    exit 0
  fi
  case $CMD in create)
    create_fifo
    ;;
    clear)
    clear_fifo
    ;;
    *)
      echo 'clear for delete,create for mkfifo'
    ;;
  esac
}
#./all_fifo_op.sh clear def
#echo ${CMD}
switch_case