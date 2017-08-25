/**
 * Project Name:elasticsearch-hadoop
 * File Name:EsClentTest.java
 * Package Name:org.easyolap.test.search
 * Date:2017年5月23日下午12:15:27
 * Copyright (c) 2017, Neusoft All Rights Reserved.
 *
*/

package org.easyolap.test.search;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.Map;

import org.easyolap.bigdata.search.EsClient;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.GeoBoundingBoxQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.UnmappedTerms;
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
public class EsClentSearch {
    private static Logger log = LoggerFactory.getLogger(EsClentSearch.class.getName());
 
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

    @Test
    public void testSearch1() {
        long startTime = System.currentTimeMillis();
        try {
            SearchResponse response = client.prepareSearch().get();
            logSearchResponse(response);

            response = client.prepareSearch(indexName).get();
            // assertEquals(165589, response.getHits().getTotalHits());
            log.info("testSearch1 耗时:" + response.getTookInMillis() + "\tHITS:"
                    + response.getHits().getTotalHits());

            SearchResponse detailedResponse = client.prepareSearch(indexName).setTypes(typeField)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setPostFilter(QueryBuilders.rangeQuery("soc1").from(20).to(30)).setFrom(0)
                    .setSize(60).get();
            logSearchResponse(detailedResponse);
            log.info("testSearch1 detailedResponse 耗时:" + detailedResponse.getTookInMillis());

            assertEquals(0, detailedResponse.getHits().getTotalHits());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("testSearch1 use time(ms):" + (endTime - startTime));
    }

    // @Test
    public void testSearch2() {
        long startTime = System.currentTimeMillis();
        try {
            SearchResponse response = client.prepareSearch(indexName).setTypes(typeField, "type2")
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.termQuery("multi", "test")) // Query
                    .setPostFilter(QueryBuilders.rangeQuery("soc1").from(20).to(30)) // Filter
                    .setFrom(0).setSize(60).setExplain(true).get();
            log.info("testSearch2 response 耗时:" + response.getTookInMillis());

            logSearchResponse(response);
            assertEquals(0, response.getHits().getTotalHits());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("testSearch2 use time(ms):" + (endTime - startTime));

    }

    @Test
    public void testSearch3() {
        long startTime = System.currentTimeMillis();
        try {
            String queryItem = "SOC:[20 TO 30]";
            // queryItem="*SOC:*";
            QueryStringQueryBuilder queryStringBuilder = new QueryStringQueryBuilder(queryItem);
            queryStringBuilder.defaultOperator(org.elasticsearch.index.query.Operator.AND);
            queryStringBuilder.useDisMax(true);

            SortBuilder sortBuilder = SortBuilders.fieldSort("mileage").order(SortOrder.DESC);

            SearchRequestBuilder builder = client.prepareSearch(indexName).setTypes(typeField)
                    .setSearchType(SearchType.DEFAULT).setFrom(0)// 起始记录
                    .setSize(10);// 每页条数

            builder.setQuery(queryStringBuilder);
            builder.addSort(sortBuilder);

            SearchResponse response = builder.get();
            log.info("testSearch3 response 耗时:(" + queryItem + ")" + response.getTookInMillis()
                    + "\tHITS:" + response.getHits().totalHits());
            logSearchResponse(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("testSearch3 use time(ms):" + (endTime - startTime));
    }

    @Test
    public void testAggregation() {
        long startTime = System.currentTimeMillis();
        try {
            SearchRequestBuilder srb = client.prepareSearch(indexName).setTypes(typeField);
            srb.setSearchType(SearchType.DEFAULT);
            TermsAggregationBuilder gradeTermsBuilder = AggregationBuilders.terms("socAgg")
                    .field("soc");
            TermsAggregationBuilder classTermsBuilder = AggregationBuilders.terms("mileageAgg")
                    .field("mileage");

            gradeTermsBuilder.subAggregation(classTermsBuilder);

            srb.addAggregation(gradeTermsBuilder);

            SearchResponse sr = srb.execute().actionGet();

            Map<String, Aggregation> aggMap = sr.getAggregations().asMap();

            UnmappedTerms gradeTerms = (UnmappedTerms) aggMap.get("socAgg");

            Iterator<Bucket> gradeBucketIt = gradeTerms.getBuckets().iterator();

            while (gradeBucketIt.hasNext()) {
                Bucket gradeBucket = (Bucket) gradeBucketIt.next();
                System.out.println(
                        gradeBucket.getKey() + "soc有" + gradeBucket.getDocCount() + "个学生。");
                StringTerms classTerms = (StringTerms) gradeBucket.getAggregations().asMap()
                        .get("classAgg");
                Iterator<Bucket> classBucketIt = classTerms.getBuckets().iterator();
                while (classBucketIt.hasNext()) {
                    Bucket classBucket = classBucketIt.next();
                    log.info(gradeBucket.getKey() + "电量" + classBucket.getKey() + "里程有"
                            + classBucket.getDocCount() + "个。");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("testAggregation use time(ms):" + (endTime - startTime));
    }

    @Test
    public void testGeo() {
        long startTime = System.currentTimeMillis();
        try {
            GeoBoundingBoxQueryBuilder queryBuilder = QueryBuilders.geoBoundingBoxQuery("location");
            GeoPoint topLeft = new GeoPoint(40.0, 117);
            GeoPoint bottomRight = new GeoPoint(39.9, 116);
            queryBuilder.setCorners(topLeft, bottomRight);
            SearchResponse searchResponse = client.prepareSearch(indexName).setQuery(queryBuilder)
                    .get();
            System.out.println("testGeo::"+searchResponse);
            System.err.println("testGeo hits::"+searchResponse.getHits().totalHits());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("testGeo use time(ms):" + (endTime - startTime));
    }

    private static void logSearchResponse(SearchResponse response) {
        SearchHits hits = response.getHits();
        log.info("Result size: " + hits.totalHits());
        log.info(response.toString());
    }

}
