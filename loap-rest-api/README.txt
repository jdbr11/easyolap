mvn -f pom.xml dependency:sources -DdownloadSources=true eclipse:eclipse
mvn -f pom.xml dependency:sources -DdownloadSources=true eclipse:eclipse

mvn -f pom.xml checkstyle:checkstyle


mvn -f pom.xml clean package -Dmaven.test.skip=true -Denforcer.skip=true

mvn -f pom.xml dependency:sources -DdownloadSources=true eclipse:eclipse -Dmaven.test.skip=true -Denforcer.skip=true -Pspark-runner



mvn -f pom.xml clean install -Pproduction_GEELY_TEST -Dmaven.test.skip=true



--打包编译
mvn -f pom-geely.xml clean install -Pproduction_GEELY_TEST assembly:assembly -Dmaven.test.skip=true -Pspark-runner
mvn -f pom-geely.xml clean package -Pproduction_GEELY_TEST  -Dmaven.test.skip=true -Pspark-runner

 

mvn compile exec:java -Dexec.mainClass=com.reachauto.automotive.batch.RealtimeStatisticsPipeline2 -Dexec.args="--runner=SparkRunner --startDatatime=2017-06-09_00:40:10 --endDatatime=2017-06-09_14:47:45" -Pspark-runner


mvn -f pom-geely.xml compile -Pproduction_GEELY_TEST exec:java -Dexec.mainClass=com.reachauto.automotive.batch.MileageStatisticsPipeline -Dexec.args="--runner=SparkRunner" -Pspark-runner

mvn -f pom-geely.xml compile -Pproduction_GEELY_TEST exec:java -Dexec.mainClass=com.reachauto.automotive.batch.MileageStatisticsPipeline -Pdirect-runner

mvn -f pom-geely.xml compile -Pproduction_GEELY_TEST exec:java -Dexec.mainClass=com.reachauto.automotive.batch.RealtimeStatisticsPipeline -Dexec.args="--runner=SparkRunner" -Pspark-runner


mvn -f pom-geely.xml clean compile -Dmaven.test.skip=true exec:java -Dexec.mainClass=com.reachauto.automotive.batch.WordCount1 -Dexec.args="--inputFile=pom.xml --output=counts" -Pdirect-runner -Pproduction_GEELY_TEST
mvn -f pom-geely.xml clean compile -Dmaven.test.skip=true exec:java -Dexec.mainClass=com.reachauto.automotive.batch.RealtimeStatisticsPipeline -Dexec.args="--startDatatime=2017-03-28_14:40:10 --endDatatime=2017-03-28_14:47:45" -Pproduction_GEELY_TEST -Pdirect-runner


spark-submit --class com.reachauto.automotive.batch.RealtimeStatisticsPipeline --master spark://master:18080 target/beam-examples-1.0.0-shaded.jar --runner=SparkRunner

spark-submit --class path.to.your.Class --master yarn --deploy-mode cluster [options] <app jar> [app options]

spark-submit --class com.reachauto.automotive.batch.RealtimeStatisticsPipeline --master yarn --deploy-mode cluster rtm-batch-geely-bundled-1.0.0.jar --runner=SparkRunner

spark-submit --name OnlineTimeStatisticsPipeline --conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=file:/home/spark/log4j-executor.properties" --class com.reachauto.automotive.batch.OnlineTimeStatisticsPipeline --master yarn-client --deploy-mode cluster rtm-batch-geely-1.0.0-jar-with-dependencies.jar --runner=SparkRunner


spark-submit --name OnlineTimeStatistics --conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=file:/home/spark/log4j-executor.properties" --class com.reachauto.automotive.batch.OnlineTimeStatisticsPipeline --master yarn --deploy-mode cluster rtm-batch-geely-1.0.0-jar-with-dependencies.jar --runner=org.apache.beam.runners.spark.SparkRunner

spark-submit --name OnlineTimeStatistics --class com.reachauto.automotive.batch.OnlineTimeStatisticsPipeline --master yarn --deploy-mode cluster rtm-batch-geely-shaded-1.0.0.jar --runner=SparkRunner
spark-submit --name OnlineTimeStatistics --class com.reachauto.automotive.batch.OnlineTimeStatisticsPipeline --master yarn --deploy-mode cluster rtm-batch-geely-shaded-1.0.0.jar --runner=org.apache.beam.runners.spark.SparkRunner

--充电时长统计
spark-submit --name ChargeStatistics --class com.reachauto.automotive.batch.ChargeStatisticsPipeline --master yarn --deploy-mode cluster rtm-batch-geely-shaded-1.0.0.jar --runner=SparkRunner

spark-submit --name ChargeStatistics --class com.reachauto.automotive.batch.ChargeStatisticsPipeline --master yarn --deploy-mode cluster rtm-batch-geely-shaded-1.0.0.jar --runner=org.apache.beam.runners.spark.SparkRunner
 
--spark kafka 2 es
spark-submit --name kafka2es --class com.reachauto.automotive.batch.Kafka2ElasticsearchPipeline --master yarn --deploy-mode cluster rtm-batch-test-shaded-1.0.0.jar --runner=org.apache.beam.runners.spark.SparkRunner

--spark standalone集群

spark-submit --name RealtimeStatisticsPipelin2 --class com.reachauto.automotive.batch.RealtimeStatisticsPipelin2 --master spark://el.reachauto.com:7077 rtm-batch-test-shaded-1.0.0.jar --runner=org.apache.beam.runners.spark.SparkRunner


查看日志
  yarn logs -applicationId  application id 
  example:
 yarn logs -applicationId  application_1491120895540_0033
 
 
 
   分     小时    日       月       星期     命令

 0-59   0-23   1-31   1-12     0-6     command     (取值范围,0表示周日一般一行对应一个任务)
#每10分钟执行一次任务
*/10 * * * * sh /home/spark/start-RealtimeStatistics.sh



#standalone


mvn dependency:tree -Dverbose -Dincludes=commons-logging:commons-loggging





 