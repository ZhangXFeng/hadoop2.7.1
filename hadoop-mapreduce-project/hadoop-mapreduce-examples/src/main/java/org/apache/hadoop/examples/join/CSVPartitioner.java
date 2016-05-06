package org.apache.hadoop.examples.join;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class CSVPartitioner extends Partitioner<Text, CSVRecord> {

	@Override
	public int getPartition(Text key, CSVRecord value, int numPartitions) {

		Integer.parseInt(key.toString());
		return Integer.parseInt(key.toString()) % numPartitions;
	}

}
