export SPARK_JAR=/usr/hdp/2.4.3.0-227/spark/lib/spark-assembly-1.6.2.2.4.3.0-227-hadoop2.7.1.2.4.3.0-227.jar
 
spark-submit --name ReadHbase2ElasticsearchPipeline --class org.easyolap.batch.ReadHbase2ElasticsearchPipeline --master yarn-client olap-batch-1.0.jar --runner=SparkRunner
