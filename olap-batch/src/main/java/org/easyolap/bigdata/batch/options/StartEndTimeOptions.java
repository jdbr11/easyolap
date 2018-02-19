package org.easyolap.bigdata.batch.options;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

public interface StartEndTimeOptions extends PipelineOptions{
    @Description("Start Datatime yyyy-MM-dd_HH:mm:ss")
    String getStartDatatime();

    void setStartDatatime(String value);

    @Description("End Datatime yyyy-MM-dd_HH:mm:ss")
    String getEndDatatime();

    void setEndDatatime(String value);
    @Description("Input hbase table name ")
    @Default.String("2:RealtimeMessage")
    String getInputTablename();
    void setInputTablename(String value);
    
    
    @Description("es indexName")
    @Default.String("vehicle1")
    String getIndexName();

    void setIndexName(String value);

    @Description("es indexType")
    @Default.String("realtime")
    String getIndexType();

    void setIndexType(String value);

    @Description("Path of the file to write to")
    String getOutput();
    void setOutput(String value);
}
