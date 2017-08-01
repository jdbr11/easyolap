#!/bin/sh
BATCH_HOME="$( cd -P "$( dirname "$0" )" && pwd )"/

MAIN_JAR=olap-batch-1.0.jar
START_PIPELINE_CLASS="org.easyloap.batch.Kafka2ElasticsearchPipeline"
PIPELINE_NAME="Kafka2ElasticsearchPipeline"
indexName="vehicle"
indexType="realdata"

OUT_LOG=`date  +%Y-%m-%d_%H:%M:%S`
if [  -n "$1" ] ;then
	indexName=$1
else
		echo "Warn no indexName paramter exec default $indexName filter"
fi

if [  -n "$2" ] ;then
        indexType=$2
else
		echo "Warn no indexType paramter exec default $indexType filter"
fi

(cd "$BATCH_HOME" && spark-submit --name $PIPELINE_NAME --class $START_PIPELINE_CLASS --master yarn --deploy-mode cluster $MAIN_JAR --startDatatime=$START_TIME --endDatatime=$END_TIME --inputTablename=$inputTablename --runner=SparkRunner 1>realtime-error.log 2>$PIPELINE_NAME$OUT_LOG.log &)
