package org.easyolap.bigdata.batch.fn;

import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.hadoop.hbase.client.Result;
import org.easyolap.bigdata.batch.bean.RecordData;

public class CollationResults extends PTransform<PCollection<Result>, PCollection<KV<String, RecordData>>> {
	private static final long serialVersionUID = -5552874389339777273L;

	@Override
	public PCollection<KV<String, RecordData>> expand(PCollection<Result> row) {
		// result -> map(rowkey,data)
		PCollection<KV<String, RecordData>> rowData = row.apply(ParDo.of(new Result2RecordDataFn()));
		return rowData;
	}

}
