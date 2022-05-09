#!/bin/bash
cd /app
/bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
java -server -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+CMSClassUnloadingEnabled -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=80 -XX:+PrintClassHistogram -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -XX:+HeapDumpOnOutOfMemoryError -Xloggc:/app/logs/gc.log -jar -Dspring.profiles.active=$DEFOR /app/dnt-warehouse-server-1.0.jar $* &
while true; do sleep 1; done
