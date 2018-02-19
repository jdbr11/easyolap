/**
 * Project Name:olap-core
 * File Name:HiveByJdbc.java
 * Package Name:org.easyolap.test
 * Date:2017年7月28日下午5:54:03
 * Copyright (c) 2017, Neusoft All Rights Reserved.
 *
*/

package org.easyolap.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * ClassName:HiveByJdbc <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年7月28日 下午5:54:03 <br/>
 * 
 * @author FrankLi
 * @version
 * @since JDK 1.8
 * @see
 */
public class JDBCToHiveUtils {
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    private static String Url = "jdbc:hive2://10.3.14.43:10000/default"; // 填写hive的IP，之前在配置文件中配置的IP
    private static Connection conn;

    public static Connection getConnnection() {
        Url ="jdbc:hive2://bigdata2.easyolap.org:2181,bigdata1.easyolap.org:2181,bigdata3.easyolap.org:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2";
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(Url, "hive", ""); // 此处的用户名一定是有权限操作HDFS的用户，否则程序会提示"permission
                                                               // deny"异常
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static PreparedStatement prepare(Connection conn, String sql) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ps;
    }
}
