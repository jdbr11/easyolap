package org.easyolap.bigdata.batch.fn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.easyolap.bigdata.batch.bean.RecordData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Result2RecordDataFn extends DoFn<Result, KV<String, RecordData>> {
	private static final Logger logger = LoggerFactory.getLogger(Result2RecordDataFn.class);

	
	private static final long serialVersionUID = 1027809604194458163L;

	@ProcessElement
	public void processElement(ProcessContext c) {
		byte[] data = c.element().getRow();// row key
		String rowKey = new String(data);
		List<Cell> cells = c.element().listCells();
		Map<String, String> cellsData = new HashMap<String, String>();
		for (Cell cell : cells) {
			String qvualifier = toStr(CellUtil.cloneQualifier(cell));
			String value = toStr(CellUtil.cloneValue(cell));
			//logger.info("qvualifier:" + qvualifier + "   value:" + value);
			cellsData.put(qvualifier, value);
			if("sendingTime".equals(qvualifier)){
				rowKey=rowKey.substring(0,rowKey.indexOf("-"))+"-"+value;
			}
		}
		if (cellsData != null && cellsData.size() > 0) {
			RecordData record = new RecordData();
			record.setData(cellsData);
			record.setRowKey(rowKey);
			KV<String,RecordData> rowData = KV.of(rowKey, record);
			logger.info("rowKey:" + rowKey + "\tdata:" + record);
			if (rowData != null) {
				c.output(rowData);
			}
		}

	}
	
	public static String toStr(byte[] bt) {
		return Bytes.toString(bt);
	}
}

