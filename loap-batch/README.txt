mvn dependency:sources -DdownloadSources=true eclipse:eclipse
mvn dependency:sources -DdownloadSources=true eclipse:eclipse

mvn checkstyle:checkstyle


mvn clean package -Dmaven.test.skip=true -Denforcer.skip=true

mvn dependency:sources -DdownloadSources=true eclipse:eclipse -Dmaven.test.skip=true -Denforcer.skip=true -Pspark-runner



mvn clean install -Dmaven.test.skip=true



--打包编译
mvn clean install assembly:assembly -Dmaven.test.skip=true -Pspark-runner
 

spark-submit --class path.to.your.Class --master yarn --deploy-mode cluster [options] <app jar> [app options]


spark-submit --class org.easyloap.batch.Kafka2ElasticsearchPipeline --master spark://master:18080 target/olap-batch-1.0.jar --runner=SparkRunner
spark-submit --class org.easyloap.batch.ReadHbase2ElasticsearchPipeline --master yarn --deploy-mode cluster olap-batch-1.0.jar --runner=SparkRunner

spark-submit --name OnlineTimeStatisticsPipeline --conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=file:/home/spark/log4j-executor.properties" --class org.easyolap.bigdata.batch.OnlineTimeStatisticsPipeline --master yarn-client --deploy-mode cluster olap-batch-1.0.jar --runner=SparkRunner

##从广义上讲，yarn-cluster适用于生产环境；而yarn-client适用于交互和调试，也就是希望快速地看到application的输出。

spark-submit --name Text2ElasticsearchPipeline  --class org.easyolap.bigdata.batch.Text2ElasticsearchPipeline --master yarn --deploy-mode cluster olap-batch-1.0.jar --runner=SparkRunner


spark-submit --name Text2ElasticsearchPipeline  --class org.easyolap.bigdata.batch.Text2ElasticsearchPipeline --master yarn-client --deploy-mode cluster olap-batch-1.0.jar --runner=SparkRunner



  
--spark kafka 2 es
spark-submit --name kafka2es --class org.easyloap.batch.Kafka2ElasticsearchPipeline --master yarn --deploy-mode cluster olap-batch-1.0.jar --runner=org.apache.beam.runners.spark.SparkRunner

--spark standalone集群

spark-submit --name kafka2es --class org.easyloap.batch.Kafka2ElasticsearchPipeline --master spark://spark.easyolap.com:7077 olap-batch-1.0.jar --runner=org.apache.beam.runners.spark.SparkRunner
spark-submit --name Text2ElasticsearchPipeline  --class org.easyloap.batch.Text2ElasticsearchPipeline --master spark://127.0.1.1:7077 olap-batch-1.0.jar --runner=SparkRunner 


spark-submit --name Text2ElasticsearchPipeline  --class org.easyloap.batch.Text2ElasticsearchPipeline --master spark://127.0.1.1:7077  olap-batch-1.0-jar-with-dependencies.jar --runner=SparkRunner --inputFile=test.txt

查看日志
  yarn logs -applicationId  application id 
  example:
 yarn logs -applicationId  application_1491120895540_0033
 
 
 
   分     小时    日       月       星期     命令

 0-59   0-23   1-31   1-12     0-6     command     (取值范围,0表示周日一般一行对应一个任务)
#每10分钟执行一次任务
*/10 * * * * sh /home/spark/start-Statistics.sh



#standalone


mvn dependency:tree -Dverbose -Dincludes=commons-logging:commons-loggging





 