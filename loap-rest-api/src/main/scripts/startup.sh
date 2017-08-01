#!/bin/sh
 
START_SERVER_CLASS="com.reach.automotive.start.Start"
 
SERVER_HOME="$( cd -P "$( dirname "$0" )" && pwd )"

MEMIF="-Xmx2g -Xms2g -Xmn1g"
MEMIF="$MEMIF -XX:+UseParallelGC  -XX:MaxGCPauseMillis=100 -XX:+UseAdaptiveSizePolicy"

MISC="-Duser.language=zh -Duser.timezone=Asia/Shanghai -Dserver.base.path=$SERVER_HOME "

PROC_NAME=" -DPROCESS_NAME=Gateway "

VMARGS="$MEMIF $MISC $JMX $DEBUG $RMIIF $PROC_NAME"

if [ -f "$JAVA_HOME/bin/java" ]; then
  JAVA="$JAVA_HOME/bin/java"
else
  JAVA=java
fi


#引入依赖类库到类路径   
PCLASSPATH=`find -name "*.jar"|xargs|sed "s/ /:/g"`
PCLASSPATH=".:$PCLASSPATH"

PCLASSPATH=$PCLASSPATH:$SERVER_HOME/config:$SERVER_HOME/
(cd "$SERVER_HOME" && exec "$JAVA" -cp $PCLASSPATH $VMARGS $START_SERVER_CLASS  1>/dev/null 2>error.log & )
