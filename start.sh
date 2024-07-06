#!/bin/bash

#PORT=80
#T_PID=$(netstat -tlnp|grep ":${PORT} "|grep -v "::${PORT} "|awk '{print $7}'|cut -d/ -f1)
T_PID=`cat application.pid`
if [[ !(0 -eq $?) ]]; then
  echo 'pid file not exit'
  exit 0
fi
if [[ $T_PID ]]; then
  kill -9 $T_PID
fi
