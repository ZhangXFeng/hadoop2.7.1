package org.apache.hadoop.examples.join;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class CSVMapper extends Mapper<Text, CSVRecord, Text, CSVRecord> {
	@Override
	protected void map(Text key, CSVRecord value,
			Mapper<Text, CSVRecord, Text, CSVRecord>.Context context) throws IOException,
			InterruptedException {
		
		context.write(key, value);
		
	}

}
