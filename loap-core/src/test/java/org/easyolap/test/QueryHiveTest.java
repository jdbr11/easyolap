/**
 * Project Name:olap-core
 * File Name:QueryHiveTest.java
 * Package Name:org.easyolap.test
 * Date:2017年7月28日下午5:54:51
 * Copyright (c) 2017, Neusoft All Rights Reserved.
 *
*/

package org.easyolap.test;

/**
 * ClassName:QueryHiveTest <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年7月28日 下午5:54:51 <br/>
 * 
 * @author FrankLi
 * @version
 * @since JDK 1.8
 * @see
 */
public class QueryHiveTest {
    public static void main(String[] args) {
        String tablename = "vehicle2";
      //  QueryHiveUtils.countByTable(tablename);
        QueryHiveUtils.getAll(tablename);
    }
}
