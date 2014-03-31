#!/bin/sh
if [ -f server.pid ]; then
echo "Server already started!"
else
/opt/faceshow/jdk1.7.0_45/bin/java -Dlogback.configurationFile=./logback.xml -server -cp "./classes:./lib/*" -Xms512m -Xmx512m -Xmn125m -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n -Dio.netty.leakDetectionLevel=advanced -XX:+HeapDumpOnOutOfMemoryError -XX:+UseConcMarkSweepGC -XX:+UseParNewGC com.v5.test.worker &
fi