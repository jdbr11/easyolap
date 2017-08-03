package org.easyloap.batch.constant;

import org.easyolap.module.utils.UtilProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsConstant {
    private static final Logger logger = LoggerFactory.getLogger(EsConstant.class);
    private static final String CONFIG_NAME = "es.properties";

    private static class LazyHolder {
        private static final EsConstant INSTANCE = new EsConstant();
    }

    private EsConstant() {

    }

    public static final EsConstant getInstance() {
        return LazyHolder.INSTANCE;
    }

    private String clusterName = "";

    public String getClasterName() {
        if (clusterName == null || clusterName.length() == 0) {
            clusterName = UtilProperties.getValueByKey(CONFIG_NAME, "cluster.name");
        }
        return clusterName;
    }

    private Integer clusterPort = null;

    public int getClusterPort() {
        if (clusterPort == null || clusterPort == 0) {
            String clusterPortStr = UtilProperties.getValueByKey(CONFIG_NAME, "cluster.port");
            clusterPort = Integer.parseInt(clusterPortStr);
        }
        return clusterPort.intValue();
    }

    private String clusterHosts = "";

    public String getClasterHosts() {
        if (clusterHosts == null || clusterHosts.length() == 0) {
            clusterHosts = UtilProperties.getValueByKey(CONFIG_NAME, "cluster.hosts");
        }
        return clusterHosts;
    }
}
