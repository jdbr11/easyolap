package org.easyolap.batch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.TextIO.Write;
import org.apache.beam.sdk.io.hbase.HBaseIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFn.ProcessElement;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.easyloap.batch.constant.HbaseConstant;
import org.easyolap.bigdata.batch.bean.RecordData;
import org.easyolap.bigdata.batch.fn.CollationResultsByNomodify;
import org.easyolap.bigdata.batch.options.StartEndTimeOptions;
import org.easyolap.bigdata.search.EsClient;
import org.easyolap.module.utils.UtilDateTime;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 实时数据索引（根据实时数据）
 * 
 * @author lizc
 *
 */
public final class ReadHbase2ElasticsearchPipeline {

    private static final Logger logger = LoggerFactory
            .getLogger(ReadHbase2ElasticsearchPipeline.class);

    private static Configuration conf = HBaseConfiguration.create();
    private static TransportClient client = null;

    public static void main(String[] args) {

        conf.setStrings(HbaseConstant.C_HBASE_ZOOKEEPER_QUORUM,
                HbaseConstant.getInstance().getZookeeperHost());
        conf.setStrings(HbaseConstant.C_HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT,
                HbaseConstant.getInstance().getZookeeperPort());
        conf.setStrings(HbaseConstant.C_ZOOKEEPER_ZNODE_PARENT,
                HbaseConstant.getInstance().getZookeeperZnodeParent());

        //client = EsClient.getInstance().getTransportClient();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

        String hbaseTable = "2:RealtimeMessage";

        StartEndTimeOptions options = PipelineOptionsFactory.fromArgs(args).withValidation()
                .as(StartEndTimeOptions.class);

        String startSendingTime = "2016-02-03_17:44:47";
        String endSendingTime = "2017-05-28_14:58:22";
        startSendingTime = options.getStartDatatime();
        endSendingTime = options.getEndDatatime();
        hbaseTable = options.getInputTablename();
        Date startDate = null;
        if (startSendingTime != null && startSendingTime.length() == 19) {
            try {
                startDate = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").parse(startSendingTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Date endDate = null;
        if (endSendingTime != null && endSendingTime.length() == 19) {
            try {
                endDate = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").parse(endSendingTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (startDate == null) {
            startDate = UtilDateTime.getDayStart();
        }
        if (endDate == null) {
            endDate = new Date();
        }

        String startRow = String.format("%s", startDate.getTime());
        String endRow = String.format("%s", endDate.getTime());
        startRow ="1489971972000";
        endRow="1500512779000";
        logger.info("date time filter where is {},({}) to {},({})", startRow, df.format(startDate),
                endRow, df.format(endDate));

        // 多条件
        List<Filter> filters = new ArrayList<Filter>();
        BinaryComparator startComparator = new BinaryComparator(startRow.getBytes());
        Filter startFilter = new SingleColumnValueFilter("data".getBytes(),
                "sendingTime".getBytes(), CompareOp.GREATER_OR_EQUAL, startComparator);

        BinaryComparator endComparator = new BinaryComparator(endRow.getBytes());
        Filter endFilter = new SingleColumnValueFilter("data".getBytes(), "sendingTime".getBytes(),
                CompareOp.LESS_OR_EQUAL, endComparator);
        filters.add(startFilter);
        filters.add(endFilter);
        FilterList filterLists = new FilterList(filters);

        Pipeline pipeline = Pipeline.create(options);
        long starTime = System.currentTimeMillis();
        pipeline.apply("ReadHbase",
                HBaseIO.read().withConfiguration(conf).withTableId(hbaseTable)
                        .withFilter(filterLists))
                .apply(new CollationResultsByNomodify())
                //.apply(ParDo.of(new ExtractWordsFn()))//PCollection<String>
                // 计算记录数
               // .apply(Count.<String>perElement())//PCollection<KV<String, Long>>
                
               // .apply(MapElements.via(new FormatAsTextFn()))
                //.apply("WriteCounts",TextIO.Write.to(options.getOutput()));
                //.apply(MapElements.via(new ElasticsearchIndexFn(options.getIndexName(),options.getIndexType())))
                ;

        pipeline.run().waitUntilFinish();
        long endTime = System.currentTimeMillis();
        logger.info("Use Time:" + (endTime - starTime));
    }

    private static void apply(
            PTransform<PCollection<String>, PCollection<KV<String, Long>>> perElement) {
        
        // TODO Auto-generated method stub
        
    }

    public static class ElasticsearchIndexFn
            extends SimpleFunction<KV<String, RecordData>, String> {
        private Logger logger = LoggerFactory.getLogger(ElasticsearchIndexFn.class);

        private static final long serialVersionUID = 1027809604194458163L;
        private String indexName;
        private String indexType;

        public ElasticsearchIndexFn(String indexName, String indexType) {
            this.indexName = indexName;
            this.indexType = indexType;
        }
        @ProcessElement
        public String apply(KV<String, RecordData> input) {
            String rowKey = input.getKey();
            RecordData record = input.getValue();
            try {

                XContentBuilder builder = jsonBuilder().startObject().field("rowKey", rowKey);

                Map<Integer, Integer> map = new HashMap<Integer, Integer>();

                for (Map.Entry<String, String> entry : record.getData().entrySet()) {
                    builder.field(entry.getKey(), entry.getValue());
                }
                builder.endObject();
                if (builder != null) {
                    IndexResponse response = client.prepareIndex(indexName, indexType)
                            .setSource(builder).get();
                    if (response != null) {
                        String _index = response.getIndex();
                        String _type = response.getType();
                        String _id = response.getId();
                        long _version = response.getVersion();
                        RestStatus status = response.status();
                        //logger.info("index:{},type:{}id:{}version:{}status:{}", _index, _type, _id,_version, status);
                    } else {
                        logger.warn("response is null");
                    }
                } else {
                    logger.warn("builder is null");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return rowKey;
        }
    }
    public static class FormatAsTextFn extends SimpleFunction<KV<String, Long>, String> {
        @Override
        public String apply(KV<String, Long> input) {
            logger.info(input.getKey() + "==:==" + input.getValue());
            return input.getKey() + ": " + input.getValue();
        }
    }
    static class ExtractWordsFn extends DoFn<KV<String, RecordData>, String> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            String data = c.element().getKey();
            c.output(data);
        }
    }
}
