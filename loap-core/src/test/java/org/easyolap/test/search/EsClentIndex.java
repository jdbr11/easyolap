/**
 * Project Name:elasticsearch-hadoop
 * File Name:EsClentTest.java
 * Package Name:com.reachauto.test
 * Date:2017年5月23日下午12:15:27
 * Copyright (c) 2017, Neusoft All Rights Reserved.
 *
*/

package org.easyolap.test.search;

import java.util.HashMap;
import java.util.Map;

import org.easyolap.bigdata.search.EsClient;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassName:EsClentTest <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年5月23日 下午12:15:27 <br/>
 * 
 * @author FrankLi
 * @version
 * @since JDK 1.8
 * @see
 */
public class EsClentIndex {
    private static Logger log = LoggerFactory.getLogger(EsClentIndex.class.getName());

    private static TransportClient client = null;
    private static String indexName;
    String typeField;
    private String id = "";

    @BeforeClass
    public static void setUpClass() {
        log.info("EsClentSearch @BeforeClass");
        client = EsClient.getInstance().getTransportClient();
    }

    @Before
    public void setUp() throws Exception {
        log.info("EsClentSearch @Before");

        indexName = "vehicle2";
        typeField = "realtime";

    }

    @After
    public void tearDown() {
        log.info("EsClentSearch @After");
    }

    @AfterClass
    public static void after() {
        if (client != null)
            client.close();
    }

   // @Test
    public void testCreateIndex() {
        long startTime = System.currentTimeMillis();
        try {
            // 创建index
            Map<String, Object> settings = new HashMap<String, Object>();
            settings.put("number_of_shards", 4); // 分片数量
            settings.put("number_of_replicas", 0); // 复制数量, 导入时最好为0, 之后2-3即可
            settings.put("refresh_interval", "10s");// 刷新时间

            CreateIndexRequestBuilder prepareCreate = client.admin().indices()
                    .prepareCreate(indexName);
            prepareCreate.setSettings(settings);

            // 创建mapping
            XContentBuilder mapping = XContentFactory.jsonBuilder().startObject()
                    .startObject(typeField)

                    .startObject("location").field("type", "geo_point")
                    .field("geohash_prefix", true).field("geohash_precision", "1km").endObject()
                    /* .field("lat_lon", true) */.endObject()
                    .endObject().endObject();
            System.out.println(mapping.string());

            prepareCreate.addMapping(typeField, mapping);
            CreateIndexResponse response = prepareCreate.execute().actionGet();
            System.out.println(response);

          
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("testSearch1 use time(ms):" + (endTime - startTime));
    }
    
    
    @Test
    public void testUpdateIndex() {
        long startTime = System.currentTimeMillis();
        try {
            //IndicesExistsRequestBuilder existsRequestBuilder = 
            GetMappingsRequestBuilder getMappingsRequestBuilder = client.admin().indices().prepareGetMappings(indexName);
            GetMappingsResponse  response =  getMappingsRequestBuilder.get();
            
            ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> mapping = response.getMappings();
           
            ///UpdateSettingsRequestBuilder builder = client.admin().indices().prepareUpdateSettings(indexName);
           
            
           // UpdateSettingsResponse response = builder.execute().actionGet();
            System.out.println(response.toString());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("testUpdateIndex use time(ms):" + (endTime - startTime));
    }


}
