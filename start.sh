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
  sleep 
fi

#start
#java -jar app.jar
#nohup java -Xms2G -Xmx2G -server -verbose:gc -Xlog:gc*:.\gc.log -jar target\server-0.1.jar
echo 'no start,please change the shell'
exit 0;
java \
-Xms2G -Xmx2G -server -verbose:gc -Xlog:gc \
-verbose:class|module|gc|jni \
-jar target\server-0.1.jar \
2>&1 1>./server.log &