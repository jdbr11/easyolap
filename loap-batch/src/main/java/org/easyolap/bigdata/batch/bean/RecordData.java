package org.easyolap.bigdata.batch.bean;

import java.io.Serializable;
import java.util.Map;

import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@DefaultCoder(SerializableCoder.class)
public class RecordData  implements Serializable {

	private static final long serialVersionUID = -9091718474376230285L;
	private String rowKey;
	private String vin;// vin ,  vin-sendingtime  is rowkey
	private String sendingtime;// sending time utc format by string
	private Map<String, String> data;
	public String getRowKey() {
		return rowKey;
	}
	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	
	public String getVin() {
        return vin;
    }
    public void setVin(String vin) {
        this.vin = vin;
    }
    public String getSendingtime() {
        return sendingtime;
    }
    public void setSendingtime(String sendingtime) {
        this.sendingtime = sendingtime;
    }
    public String toString(){
		return ReflectionToStringBuilder.toString(this); 
	}
}
