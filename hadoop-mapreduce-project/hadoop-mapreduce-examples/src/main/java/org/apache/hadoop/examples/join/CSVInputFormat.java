package org.apache.hadoop.examples.join;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.record.CsvRecordInput;

public class CSVInputFormat extends FileInputFormat<Text, CSVRecord> {


	@Override
	public org.apache.hadoop.mapreduce.RecordReader<Text, CSVRecord> createRecordReader(
			org.apache.hadoop.mapreduce.InputSplit split,
			TaskAttemptContext context) throws IOException,
			InterruptedException {
		return new CSVRecordReader();
	}

}
