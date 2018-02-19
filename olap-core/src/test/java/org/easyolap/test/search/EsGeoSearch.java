/**
 * Project Name:elasticsearch
 * File Name:EsGeoSearch.java
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
 * ClassName:EsGeoSearch <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年5月23日 下午12:15:27 <br/>
 * 
 * @author FrankLi
 * @version
 * @since JDK 1.8
 * @see
 */
public class EsGeoSearch {
    private static Logger log = LoggerFactory.getLogger(EsGeoSearch.class.getName());

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

    
}
