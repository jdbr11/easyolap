/**
 * Project Name:olap-core
 * File Name:QueryHiveUtils.java
 * Package Name:org.easyolap.test
 * Date:2017年7月28日下午5:54:28
 * Copyright (c) 2017, Neusoft All Rights Reserved.
 *
*/

package org.easyolap.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.Partition;
import org.apache.spark.sql.internal.SessionState;
/**
 * ClassName:QueryHiveUtils <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年7月28日 下午5:54:28 <br/>
 * 
 * @author FrankLi
 * @version
 * @since JDK 1.8
 * @see
 */
public class QueryHiveUtils {
    private static Connection conn = JDBCToHiveUtils.getConnnection();
    private static PreparedStatement ps;
    private static ResultSet rs;

    public static void getAll(String tablename) {
        String sql = "select * from " + tablename +" limit 100";
        sql ="select * from "+tablename+" where SOC1 > 20 and soc1<30 limit 10";
        System.out.println(sql);
        long startTime = System.currentTimeMillis();
        try {
            ps = JDBCToHiveUtils.prepare(conn, sql);
            rs = ps.executeQuery();
            int columns = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columns; i++) {
                    System.out.print(rs.getString(i));
                    System.out.print("\t\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println(sql+"\tuse time(ms):"+(endTime-startTime));
    }
    
    public static void countByTable(String tablename) {
        String sql ="count " +tablename;
        long startTime = System.currentTimeMillis();
        try {
            HiveConf conf = new HiveConf(SessionState.class);
            Hive hive = Hive.get(conf);
            System.out.println(hive.getTable(tablename).getTTable()
                    .getParameters());
            List<Partition> list = hive.getPartitions(hive.getTable(tablename));
            for (Partition p : list) {
                System.out.println(p.getParameters());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println(sql+"\tuse time(ms):"+(endTime-startTime));
    }
}
