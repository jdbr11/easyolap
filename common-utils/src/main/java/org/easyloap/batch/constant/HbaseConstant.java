package org.easyloap.batch.constant;

import org.easyolap.module.utils.UtilProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseConstant {
    private static final Logger logger = LoggerFactory.getLogger(HbaseConstant.class);

    private static class LazyHolder {
        private static final HbaseConstant INSTANCE = new HbaseConstant();
    }

    private HbaseConstant() {

    }
    // --Config
    public static final String C_HBASE_ZOOKEEPER_QUORUM = "hbase.zookeeper.quorum";
    public static final String C_HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT = "hbase.zookeeper.property.clientPort";
    public static final String C_ZOOKEEPER_ZNODE_PARENT = "zookeeper.znode.parent";
    public static final String C_HBASE_CONF = "hbase.conf";
    
    public static final HbaseConstant getInstance() {
        return LazyHolder.INSTANCE;
    }

    private String ZOOKEEPER_HOST_STR = "";

    public String getZookeeperHost() {
        if (ZOOKEEPER_HOST_STR == null || ZOOKEEPER_HOST_STR.length() == 0) {
            ZOOKEEPER_HOST_STR = UtilProperties.getValueByKey("hbase.properties",
                    "ZOOKEEPER_HOSTS");
        }
        return ZOOKEEPER_HOST_STR;

    }

    private String ZOOKEEPER_PROPERTY_CLIENTPORT = null;

    public String getZookeeperPort() {
        if (ZOOKEEPER_PROPERTY_CLIENTPORT == null || ZOOKEEPER_PROPERTY_CLIENTPORT.length() == 0) {
            ZOOKEEPER_PROPERTY_CLIENTPORT = UtilProperties.getValueByKey("hbase.properties",
                    "ZOOKEEPER_PROPERTY_CLIENTPORT");
        }
        return ZOOKEEPER_PROPERTY_CLIENTPORT;

    }

    private String ZOOKEEPER_ZNODE_PARENT = null;

    public String getZookeeperZnodeParent() {
        if (ZOOKEEPER_ZNODE_PARENT == null || ZOOKEEPER_ZNODE_PARENT.length() == 0) {
            ZOOKEEPER_ZNODE_PARENT = UtilProperties.getValueByKey("hbase.properties",
                    "ZOOKEEPER_ZNODE_PARENT");
        }
        return ZOOKEEPER_ZNODE_PARENT;

    }

}
