/**
 * Project Name:elasticsearch
 * File Name:EsClentTest.java
 * Package Name:org.easyolap.test.search
 * Date:2017年5月23日下午12:15:27
 * Copyright (c) 2017, Neusoft All Rights Reserved.
 *
*/

package org.easyolap.test.search;

import org.easyolap.bigdata.search.EsClient;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassName: EsAggregationsSearch <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年5月23日 下午12:15:27 <br/>
 * 
 * @author FrankLi
 * @version
 * @since JDK 1.8
 * @see
 */
public class EsAggregationsSearch {
    private static Logger log = LoggerFactory.getLogger(EsAggregationsSearch.class.getName());

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
        log.info("============================EsClentSearch @Before");

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

    @Test
    public void testTerms() {
        long startTime = System.currentTimeMillis();
        try {
            SearchResponse response = client.prepareSearch().get();
            response = client.prepareSearch(indexName).get();
            log.info("all data Search 耗时:" + response.getTookInMillis() + "\tHITS:"
                    + response.getHits().getTotalHits());

            System.out.println(
                    "==========================testTerms Dividing line===========================");
            SearchRequestBuilder responsebuilder = client.prepareSearch(indexName)
                    .setTypes(typeField).setFrom(0).setSize(50);
            AggregationBuilder aggregation = AggregationBuilders.terms("agg").field("SOC")
                    .subAggregation(AggregationBuilders.topHits("top").from(0).size(5)).size(15);

            AggregationBuilder aggBuilder2 = AggregationBuilders.sum("sumOfOut").field("mileage"); // 聚合统计

            String queryItem = "SOC:[20 TO 30]";
            QueryStringQueryBuilder queryStringBuilder = new QueryStringQueryBuilder(queryItem);
            queryStringBuilder.defaultOperator(org.elasticsearch.index.query.Operator.AND);
            queryStringBuilder.useDisMax(true);
            // QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery("VIN",
            // "LC0DF7"))

            SortBuilder<?> sortBuilder = SortBuilders.fieldSort("mileage").order(SortOrder.DESC);

            response = responsebuilder.setQuery(queryStringBuilder).addSort(sortBuilder)
                    .addAggregation(aggregation).addAggregation(aggBuilder2).setExplain(true)
                    .execute().actionGet();

            SearchHits hits = response.getHits();
            Aggregations aggregations = response.getAggregations();
            if (aggregations != null) {
                InternalSum sumOfOut = response.getAggregations().get("sumOfOut"); // 获取出流量总值
                System.out.println("mileage的总值是：" + sumOfOut.getValue());

                Terms agg = response.getAggregations().get("agg");
                System.out.println("agg.getBuckets().size:" + agg.getBuckets().size());
                for (Terms.Bucket entry : agg.getBuckets()) {
                    java.lang.Double key = (java.lang.Double) entry.getKey(); // bucket
                                                                              // key
                    long docCount = entry.getDocCount(); // Doc count
                    System.out.println("key " + key + " doc_count " + docCount);

                    // We ask for top_hits for each bucket
                    TopHits topHits = entry.getAggregations().get("top");
                    for (SearchHit hit : topHits.getHits().getHits()) {
                        System.out.println(" -> id " + hit.getId() + " _source [{}]:"
                                + "\tsendingTime:" + hit.getSource().get("sendingTime")
                                + "\tmileage:" + hit.getSource().get("mileage") + "\tSOC:"
                                + hit.getSource().get("SOC"));
                    }
                }
            } else {
                log.info("============aggregations is null ============");
            }
            System.out.println(hits.getTotalHits());
            int temp = 0;
            for (int i = 0; i < hits.getHits().length; i++) {
                // System.out.println(hits.getHits()[i].getSourceAsString());
                System.out.print("VIN:" + hits.getHits()[i].getId());
                // System.out.print("VIN:"+hits.getHits()[i].getSource().get("VIN"));
                System.out
                        .print("\tsendingTime:" + hits.getHits()[i].getSource().get("sendingTime"));
                System.out.print("\tlatitude:" + hits.getHits()[i].getSource().get("latitude"));
                // if(orderfield!=null&&(!orderfield.isEmpty()))
                // System.out.print("\t"+hits.getHits()[i].getSource().get(orderfield));
                System.out.print("\tlongitude:" + hits.getHits()[i].getSource().get("longitude"));
                System.out.println("\tSOC:" + hits.getHits()[i].getSource().get("SOC"));
                System.out.println("\tmileage:" + hits.getHits()[i].getSource().get("mileage"));
            }

            log.info("Aggregation 耗时:" + response.getTookInMillis());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("total use time(ms):" + (endTime - startTime));
    }

    @Test
    public void testRange() {
        long startTime = System.currentTimeMillis();
        try {
            SearchResponse response = client.prepareSearch().get();
            response = client.prepareSearch(indexName).get();
            log.info("all data Search 耗时:" + response.getTookInMillis() + "\tHITS:"
                    + response.getHits().getTotalHits());

            System.out.println(
                    "==========================testRange Dividing line===========================");
            SearchRequestBuilder responsebuilder = client.prepareSearch(indexName)
                    .setTypes(typeField).setFrom(0).setSize(50);
            AggregationBuilder aggregation = AggregationBuilders.range("agg").field("mileage")
                    .addUnboundedTo(50)
                    .addRange(51, 100)
                    .addRange(101, 1000)
                    .addUnboundedFrom(1001);
            aggregation.subAggregation(AggregationBuilders.topHits("top").from(0).size(5));

            String queryItem = "SOC:[20 TO 30]";
            QueryStringQueryBuilder queryStringBuilder = new QueryStringQueryBuilder(queryItem);
            queryStringBuilder.defaultOperator(org.elasticsearch.index.query.Operator.AND);
            queryStringBuilder.useDisMax(true);
            // QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery("VIN",
            // "LC0DF7"))

            SortBuilder<?> sortBuilder = SortBuilders.fieldSort("mileage").order(SortOrder.DESC);

            response = responsebuilder.setQuery(queryStringBuilder).addSort(sortBuilder)
                    .addAggregation(aggregation)
                    //.setPostFilter(QueryBuilders.rangeQuery("price").gt(1000).lt(5000))  
                    .setExplain(true).execute().actionGet();

            SearchHits hits = response.getHits();
            Aggregations aggregations = response.getAggregations();
            if (aggregations != null) {
                Range agg = response.getAggregations().get("agg");
                System.out.println("agg.getBuckets().size:" + agg.getBuckets().size());
                for (Range.Bucket entry : agg.getBuckets()) {
                    if (entry.getKey() instanceof String) {
                        String key = (String) entry.getKey(); // bucket key
                        long docCount = entry.getDocCount(); // Doc count
                        System.out.println("key " + key + " doc_count " + docCount);
                    } else if (entry.getKey() instanceof Double) {
                        Double key = (Double) entry.getKey(); // bucket key
                        long docCount = entry.getDocCount(); // Doc count
                        System.out.println("key " + key + " doc_count " + docCount);
                    }

                    // We ask for top_hits for each bucket
                    TopHits topHits = entry.getAggregations().get("top");
                    if (topHits != null) {
                        for (SearchHit hit : topHits.getHits().getHits()) {
                            System.out.println(" -> id " + hit.getId() + " _source [{}]:"
                                    + "\tsendingTime:" + hit.getSource().get("sendingTime")
                                    + "\tmileage:" + hit.getSource().get("mileage") + "\tSOC:"
                                    + hit.getSource().get("SOC"));
                        }
                    } else {
                        log.info("============topHits is null ============");
                    }
                }
            } else {
                log.info("============aggregations is null ============");
            }
            System.out.println(hits.getTotalHits());
            int temp = 0;
            for (int i = 0; i < hits.getHits().length; i++) {
                // System.out.println(hits.getHits()[i].getSourceAsString());
                System.out.print("VIN:" + hits.getHits()[i].getId());
                // System.out.print("VIN:"+hits.getHits()[i].getSource().get("VIN"));
                System.out
                        .print("\tsendingTime:" + hits.getHits()[i].getSource().get("sendingTime"));
                System.out.print("\tlatitude:" + hits.getHits()[i].getSource().get("latitude"));
                // if(orderfield!=null&&(!orderfield.isEmpty()))
                // System.out.print("\t"+hits.getHits()[i].getSource().get(orderfield));
                System.out.print("\tlongitude:" + hits.getHits()[i].getSource().get("longitude"));
                System.out.println("\tSOC:" + hits.getHits()[i].getSource().get("SOC"));
                System.out.println("\tmileage:" + hits.getHits()[i].getSource().get("mileage"));
            }

            log.info("Aggregation 耗时:" + response.getTookInMillis());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("total use time(ms):" + (endTime - startTime));
    }

    @Test
    public void testDateRange() {
        long startTime = System.currentTimeMillis();
        try {
            SearchResponse response = client.prepareSearch().get();
            response = client.prepareSearch(indexName).get();
            log.info("all data Search 耗时:" + response.getTookInMillis() + "\tHITS:"
                    + response.getHits().getTotalHits());

            System.out.println(
                    "==========================testDateRange Dividing line===========================");
            SearchRequestBuilder responsebuilder = client.prepareSearch(indexName)
                    .setTypes(typeField).setFrom(0).setSize(50);
            AggregationBuilder aggregation = AggregationBuilders.dateRange("agg")
                    .field("sendingTime")
                    .format("yyyy")
                    .addUnboundedTo("1970")
                    .addRange("1970", "2000")
                    .addRange("2000", "2010")
                    .addUnboundedFrom("2009");
            
            aggregation.subAggregation(AggregationBuilders.topHits("top").from(0).size(5));
            
            String queryItem = "SOC:[20 TO 30]";
            QueryStringQueryBuilder queryStringBuilder = new QueryStringQueryBuilder(queryItem);
            queryStringBuilder.defaultOperator(org.elasticsearch.index.query.Operator.AND);
            queryStringBuilder.useDisMax(true);
            // QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery("VIN",
            // "LC0DF7"))

            SortBuilder<?> sortBuilder = SortBuilders.fieldSort("mileage").order(SortOrder.DESC);

            response = responsebuilder.setQuery(queryStringBuilder).addSort(sortBuilder)
                    .addAggregation(aggregation).setExplain(true).execute().actionGet();

            SearchHits hits = response.getHits();
            Aggregations aggregations = response.getAggregations();
            if (aggregations != null) {
                Terms agg = response.getAggregations().get("agg");
                System.out.println("agg.getBuckets().size:" + agg.getBuckets().size());
                for (Terms.Bucket entry : agg.getBuckets()) {
                    java.lang.Double key = (java.lang.Double) entry.getKey(); // bucket
                                                                              // key
                    long docCount = entry.getDocCount(); // Doc count
                    System.out.println("key " + key + " doc_count " + docCount);

                    // We ask for top_hits for each bucket
                    TopHits topHits = entry.getAggregations().get("top");
                    for (SearchHit hit : topHits.getHits().getHits()) {
                        System.out.println(" -> id " + hit.getId() + " _source [{}]:"
                                + "\tsendingTime:" + hit.getSource().get("sendingTime")
                                + "\tmileage:" + hit.getSource().get("mileage") + "\tSOC:"
                                + hit.getSource().get("SOC"));
                    }
                }
            } else {
                log.info("============aggregations is null ============");
            }
            System.out.println(hits.getTotalHits());
            int temp = 0;
            for (int i = 0; i < hits.getHits().length; i++) {
                // System.out.println(hits.getHits()[i].getSourceAsString());
                System.out.print("VIN:" + hits.getHits()[i].getId());
                // System.out.print("VIN:"+hits.getHits()[i].getSource().get("VIN"));
                System.out
                        .print("\tsendingTime:" + hits.getHits()[i].getSource().get("sendingTime"));
                System.out.print("\tlatitude:" + hits.getHits()[i].getSource().get("latitude"));
                // if(orderfield!=null&&(!orderfield.isEmpty()))
                // System.out.print("\t"+hits.getHits()[i].getSource().get(orderfield));
                System.out.print("\tlongitude:" + hits.getHits()[i].getSource().get("longitude"));
                System.out.println("\tSOC:" + hits.getHits()[i].getSource().get("SOC"));
                System.out.println("\tmileage:" + hits.getHits()[i].getSource().get("mileage"));
            }

            log.info("Aggregation 耗时:" + response.getTookInMillis());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("total use time(ms):" + (endTime - startTime));
    }
}
