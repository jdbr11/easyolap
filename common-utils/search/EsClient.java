package org.easyolap.bigdata.search;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

import org.easyloap.batch.constant.EsConstant;
import org.easyolap.module.utils.UtilProperties;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsClient {
    private static Logger log = LoggerFactory.getLogger(EsClient.class.getName());

    private static class LaziHolder {
        private static final EsClient INSTANCE = new EsClient();
    }

    private EsClient() {

    }

    public static final EsClient getInstance() {
        return LaziHolder.INSTANCE;
    }

    // 取得实例
    public TransportClient getTransportClient() {
            try {
            Settings settings = Settings.builder()
                    .put("cluster.name", EsConstant.getInstance().getClasterName()) // 设置ES实例的名称
                    // .put("client.transport.sniff", true) //
                    // 自动嗅探整个集群的状态，把集群中其他ES节点的ip添加到本地的客户端列表中
                    //.put("xpack.security.user", "elastic:changeme")// x-pach配置 
                    .put("transport.type","netty3")
                    .put("http.type", "netty3")
                    .build();
            InetAddress addr = InetAddress.getByName(EsConstant.getInstance().getClasterHosts());
            InetSocketAddress ip = new InetSocketAddress(addr, EsConstant.getInstance().getClusterPort());
            TransportAddress transportAddress = new InetSocketTransportAddress(ip);
            TransportClient client = new PreBuiltTransportClient(settings);

            client.addTransportAddress(transportAddress);
            return client;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public String addIndex(TransportClient client, String index, String type,
            HashMap<String, Object> hashMap) {
        try {
            IndexResponse response = client.prepareIndex(index, type, hashMap.get("id").toString())
                    .setSource(hashMap).execute().actionGet();
            System.out.println(response.getId());
            return response.getId(); // 返回主键
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public void addAllIndex(TransportClient client, String index, String type,
            HashMap<String, Object> hashMap) {

        try {
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            for (int i = 0; i < 10000; i++) {
                bulkRequest.add(client.prepareIndex(index, type).setSource(hashMap));
                // 每1000条提交一次
                if (i % 10000 == 0) {
                    bulkRequest.execute().actionGet();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    // 【设置自动提交文档】
    public static BulkProcessor getBulkProcessor(TransportClient client) {
        // 自动批量提交方式
        try {
            BulkProcessor staticBulkProcessor = BulkProcessor
                    .builder(client, new BulkProcessor.Listener() {
                        @Override
                        public void beforeBulk(long executionId, BulkRequest request) {
                            // 提交前调用
                            // System.out.println(new Date().toString() + "
                            // before");
                        }

                        @Override
                        public void afterBulk(long executionId, BulkRequest request,
                                BulkResponse response) {
                            // 提交结束后调用（无论成功或失败）
                            // System.out.println(new Date().toString() + "
                            // response.hasFailures=" + response.hasFailures());
                            log.info("提交" + response.getItems().length + "个文档，用时"
                                    + response.getTookInMillis() + "MS"
                                    + (response.hasFailures() ? " 有文档提交失败！" : ""));
                            // response.hasFailures();//是否有提交失败
                        }

                        @Override
                        public void afterBulk(long executionId, BulkRequest request,
                                Throwable failure) {
                            // 提交结束且失败时调用
                            log.error(" 有文档提交失败！after failure=" + failure);
                        }
                    })

                    .setBulkActions(1000)// 文档数量达到1000时提交
                    .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))// 总文档体积达到5MB时提交
                                                                       // //
                    .setFlushInterval(TimeValue.timeValueSeconds(5))// 每5S提交一次（无论文档数量、体积是否达到阈值）
                    .setConcurrentRequests(1)// 加1后为可并行的提交请求数，即设为0代表只可1个请求并行，设为1为2个并行
                    .build();
            // staticBulkProcessor.awaitClose(10,
            // TimeUnit.MINUTES);//关闭，如有未提交完成的文档则等待完成，最多等待10分钟
            return staticBulkProcessor;
        } catch (Exception e) {// 关闭时抛出异常
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
