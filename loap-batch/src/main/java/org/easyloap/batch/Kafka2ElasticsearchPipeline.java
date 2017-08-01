package org.easyloap.batch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.util.Map;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.easyloap.batch.constant.KafkaConstant;
import org.easyolap.bigdata.search.EsClient;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class Kafka2ElasticsearchPipeline {
    private static final Logger logger = LoggerFactory.getLogger(Kafka2ElasticsearchPipeline.class);
    private static final Gson GSON = new Gson();
    static TransportClient client = EsClient.getInstance().getTransportClient();

    private static String indexName = "vehicle";
    private static String indexType = "realtime";

    public static void main(String[] args) {
        try {
            Kafka2ElasticsearchOptions options = PipelineOptionsFactory.fromArgs(args)
                    .withValidation().as(Kafka2ElasticsearchOptions.class);
            indexName = options.getIndexName();
            indexType = options.getIndexType();
            
            Pipeline pipeline = Pipeline.create(options);
            long starTime = System.currentTimeMillis();
            pipeline.apply(KafkaIO.<String, String> read()
                    .withBootstrapServers(KafkaConstant.getInstance().getKafkaBootstrapServers())
                    .withTopics(KafkaConstant.getInstance().getKafkaTopicS())
                    .withKeyDeserializer(
                            org.apache.kafka.common.serialization.StringDeserializer.class)
                    .withValueDeserializer(
                            org.apache.kafka.common.serialization.StringDeserializer.class)
                    .withoutMetadata()

            ).apply(ParDo.of(new Result2RecordDataFn()));

            pipeline.run().waitUntilFinish();
            long endTime = System.currentTimeMillis();
            logger.info("Static Mileage Hours Use Time:" + (endTime - starTime));

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static class Result2RecordDataFn extends DoFn<KV<String, String>, String> {
        private static final Logger logger = LoggerFactory.getLogger(Result2RecordDataFn.class);

        private static final long serialVersionUID = 1027809604194458163L;

        @ProcessElement
        public void processElement(ProcessContext c) {
            KV<String, String> kv = c.element();
            String k = kv.getKey();
            String v = kv.getValue();
            logger.info(k + "===+++===value:" + v);
            Map<String, Object> map = GSON.fromJson(v, Map.class);
            if (map.isEmpty() || !map.containsKey("sendingTime")) {
                logger.warn("RtmData deserialize error, data is null or sendingTime is null!");
                return;
            }
            try {

                XContentBuilder builder = jsonBuilder().startObject();
                String vin = "";
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    if ("message".equals(key)) {

                        Map<String, Object> message = (Map<String, Object>) entry.getValue();
                        for (Map.Entry<String, Object> mentry : message.entrySet()) {
                            String mkey = mentry.getKey();
                            if ("vin".equals(mkey)) {
                                vin = (String) mentry.getValue();
                            } else {
                                mkey = mkey.replaceAll("common:", "").replaceAll("realtime:", "");
                                builder.field(mkey, mentry.getValue());
                            }
                        }
                    } else {
                        builder.field(key, entry.getValue());
                    }
                }
                if(vin==null || vin.length()==0){
                    vin = (String) map.get("clientId");
                }
                builder.endObject();
                String rowKey = vin + "-" + map.get("sendingTime");
                if (builder != null) {
                    IndexResponse response = client.prepareIndex(indexName, indexType, rowKey)
                            .setSource(builder).get();
                    if (response != null) {
                        String _index = response.getIndex();
                        String _type = response.getType();
                        String _id = response.getId();
                        long _version = response.getVersion();
                        RestStatus status = response.status();
                        logger.info("index:{},\ttype:{},\tid:{},\tversion:{},\tstatus:{}", _index,
                                _type, _id, _version, status);
                    } else {
                        logger.warn("response is null");
                    }
                } else {
                    logger.warn("builder is null");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            c.output(k);
        }
    }

    public interface Kafka2ElasticsearchOptions extends PipelineOptions {
        
        String getIndexName();

        String getIndexType();

    }
}
