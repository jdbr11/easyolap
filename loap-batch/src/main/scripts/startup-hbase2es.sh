#!/bin/sh
BATCH_HOME="$( cd -P "$( dirname "$0" )" && pwd )"/

MAIN_JAR=olap-batch-1.0.jar
START_PIPELINE_CLASS="org.easyloap.batch.ReadHbase2ElasticsearchPipeline"
PIPELINE_NAME="ReadHbase2ElasticsearchPipeline"
START_TIME=`date  +%Y-%m-%d_00:00:00`
END_TIME=`date  +%Y-%m-%d_59:59:59`
inputTablename=""

OUT_LOG=`date  +%Y-%m-%d_%H:%M:%S`
if [  -n "$1" ] ;then
	START_TIME=$1
else
		echo "Warn no Start paramter exec default $START_TIME filter"
fi

if [  -n "$2" ] ;then
        END_TIME=$2
else
		echo "Warn no End paramter exec default $END_TIME filter"
fi

if [  -n "$3" ] ;then
        inputTablename=$3
fi

(cd "$BATCH_HOME" && spark-submit --name $PIPELINE_NAME --class $START_PIPELINE_CLASS --master yarn --deploy-mode cluster $MAIN_JAR --startDatatime=$START_TIME --endDatatime=$END_TIME --inputTablename=$inputTablename --runner=SparkRunner 1>realtime-error.log 2>$PIPELINE_NAME$OUT_LOG.log &)
