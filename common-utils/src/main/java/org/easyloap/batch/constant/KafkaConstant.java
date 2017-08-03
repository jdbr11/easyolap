package org.easyloap.batch.constant;

import java.util.Arrays;
import java.util.List;

import org.easyolap.module.utils.UtilProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConstant {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConstant.class);

    private static class LazyHolder {
        private static final KafkaConstant INSTANCE = new KafkaConstant();
    }

    private KafkaConstant() {

    }

    public static final KafkaConstant getInstance() {
        return LazyHolder.INSTANCE;
    }

    private String KAFKA_BootstrapServers = "";

    public String getKafkaBootstrapServers() {
        if (KAFKA_BootstrapServers == null || KAFKA_BootstrapServers.length() == 0) {
            KAFKA_BootstrapServers = UtilProperties.getValueByKey("kafka.properties",
                    "KAFKA_BootstrapServers");
        }
        return KAFKA_BootstrapServers;
    }

    private List<String> KAFKA_TOPICS = null;

    public List<String> getKafkaTopicS() {
        if (KAFKA_TOPICS == null || KAFKA_TOPICS.size() == 0) {
            String kafkaTopics = UtilProperties.getValueByKey("kafka.properties", "KAFKA_TOPICS");
            if (kafkaTopics != null) {
                KAFKA_TOPICS = Arrays.asList(kafkaTopics.split(","));
            }
        }
        return KAFKA_TOPICS;
    }

    private String ZOOKEEPER_OFFSETHOSTS = "";

    public String getZookeeperOffsethosts() {
        if (ZOOKEEPER_OFFSETHOSTS == null || ZOOKEEPER_OFFSETHOSTS.length() == 0) {
            ZOOKEEPER_OFFSETHOSTS = UtilProperties.getValueByKey("kafka.properties",
                    "ZOOKEEPER_OFFSETHOSTS");
        }
        return ZOOKEEPER_OFFSETHOSTS;
    }
}
