package org.easyloap.batch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Random;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.easyolap.bigdata.search.EsClient;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class Text2ElasticsearchPipeline {
    private static final Gson GSON = new Gson();
    static TransportClient client = EsClient.getInstance().getTransportClient();

    public interface Text2EsOptions extends PipelineOptions {

        /**
         * By default, this example reads from a public dataset containing the
         * text of King Lear. Set this option to choose a different input file
         * or glob.
         */
        @Description("Path of the file to read from")
        @Default.String("data/test.txt")
        String getInputFile();

        void setInputFile(String value);

        @Description("es indexName")
        @Default.String("vehicle2")
        String getIndexName();

        void setIndexName(String value);

        @Description("es indexType")
        @Default.String("realtime")
        String getIndexType();

        void setIndexType(String value);

    }

    public Text2EsOptions options = null;

    public static void main(String[] args) {
        Text2EsOptions options = PipelineOptionsFactory.fromArgs(args).withValidation()
                .as(Text2EsOptions.class);

        Pipeline p = Pipeline.create(options);

        p.apply("ReadLines", TextIO.read().from(options.getInputFile()))

                .apply(ParDo.of(
                        new Result2RecordDataFn(options.getIndexName(), options.getIndexType())));

        p.run().waitUntilFinish();
    }

    private static class Result2RecordDataFn extends DoFn<String, String> {
        private static final Logger logger = LoggerFactory.getLogger(Result2RecordDataFn.class);
        DecimalFormat df = new DecimalFormat("######0.00");
        private static final long serialVersionUID = 1027809604194458163L;
        private String indexName;
        private String indexType;

        public Result2RecordDataFn(String indexName, String indexType) {
            this.indexName = indexName;
            this.indexType = indexType;
        }

        @ProcessElement
        public void processElement(ProcessContext c) {
            String v = c.element();

            logger.info("===+++===value:" + v);
            Map<String, Object> map = GSON.fromJson(v, Map.class);
            if (map.isEmpty() || !map.containsKey("sendingTime")) {
                logger.warn("RtmData deserialize error, data is null or sendingTime is null!");
                return;
            }
            for (int i = 0; i < 100000; i++) {
                try {

                    XContentBuilder builder = jsonBuilder().startObject();
                    String vin = "";
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        String key = entry.getKey();
                        if ("message".equals(key)) {
                            Map<String, Object> message = (Map<String, Object>) entry.getValue();
                            for (Map.Entry<String, Object> mentry : message.entrySet()) {
                                String mkey = mentry.getKey();
                                if ("vin".equalsIgnoreCase(mkey)) {
                                    vin = (String) mentry.getValue();
                                } else {
                                    mkey = mkey.replaceAll("common:", "").replaceAll("realtime:",
                                            "");
                                    builder.field(mkey, mentry.getValue());
                                }
                            }
                        } else if ("vin".equalsIgnoreCase(key)) {
                            vin = (String) entry.getValue();
                        } else if ("SOC".equalsIgnoreCase(key)) {
                            double soc = new Random().nextInt(99) + new Random().nextDouble();
                            BigDecimal b = new BigDecimal(soc);
                            builder.field(key,
                                    b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                            logger.warn("socv is :" + soc);
                        } else if ("mileage".equalsIgnoreCase(key)) {
                            double mileage = new Random().nextInt(9999) + new Random().nextDouble();
                            BigDecimal b = new BigDecimal(mileage);
                            builder.field(key,
                                    b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                            logger.warn("mileage is :" + mileage);
                        } else {
                            builder.field(key, entry.getValue());
                        }

                    }
                    if (vin == null || vin.length() == 0) {
                        vin = (String) map.get("clientId");
                    }
                    builder.endObject();
                    // String rowKey = vin + "-" + map.get("sendingTime");
                    int sendingTime = new Random().nextInt(89999) + 10000;
                    int randomVin = new Random().nextInt(8999) + 1000;
                    // String rowKey = vin + "-15011500" + sendingTime;
                    String rowKey = "LC0DE6CB1G100" + randomVin + "-15011500" + sendingTime;
                    if (builder != null) {
                        IndexResponse response = client.prepareIndex(indexName, indexType, rowKey)
                                .setSource(builder).get();
                        if (response != null) {
                            String _index = response.getIndex();
                            String _type = response.getType();
                            String _id = response.getId();
                            long _version = response.getVersion();
                            RestStatus status = response.status();
                            logger.info("index:{},\ttype:{},\tid:{},\tversion:{},\tstatus:{}",
                                    _index, _type, _id, _version, status);
                        } else {
                            logger.warn("response is null");
                        }
                    } else {
                        logger.warn("builder is null");
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
            c.output(v);
        }
    }
}
