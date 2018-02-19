export SPARK_JAR=/usr/hdp/2.4.3.0-227/spark/lib/spark-assembly-1.6.2.2.4.3.0-227-hadoop2.7.1.2.4.3.0-227.jar
####spark-submit --name Text2ElasticsearchPipeline  --class org.easyloap.batch.Text2ElasticsearchPipeline --master yarn-cluster  olap-batch-1.0-jar-with-dependencies.jar --runner=SparkRunner --inputFile=hdfs://bigdata1.easyolap.org:8020/user/spark/test.txt
spark-submit --name Text2ElasticsearchPipeline1.4 --class org.easyolap.batch.Text2ElasticsearchPipeline --master yarn-client olap-batch-1.0.jar --runner=SparkRunner --inputFile=hdfs://bigdata1.easyolap.org:8020/user/spark/test2.txt
