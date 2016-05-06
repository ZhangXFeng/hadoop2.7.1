package org.apache.hadoop.examples.join;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class CSVRecordReader extends RecordReader<Text, CSVRecord> {
	private long start;
	private long pos;
	private long end;
	private FSDataInputStream fileIn;
	private Text key = null;
	private CSVRecord value = null;
	private BufferedReader br;
	private String left_path;
	private String right_path;
	private String left;
	private String right;
	private String tableName;

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context)
			throws IOException, InterruptedException {
		FileSplit fileSplit = (FileSplit) split;
		Path path = fileSplit.getPath();
		Configuration job = context.getConfiguration();
		start = fileSplit.getStart();
		end = start + fileSplit.getLength();

		FileSystem fs = path.getFileSystem(job);
		fileIn = fs.open(path);
		fileIn.seek(start);
		br = new BufferedReader(new InputStreamReader(fileIn));
		left_path = job.get("left.path");
		right_path = job.get("right.path");
		left = context.getConfiguration().get("left.tableName");
		right = context.getConfiguration().get("right.tableName");
		System.out.println(left_path
				+ " "
				+ right_path
				+ " "
				+ left
				+ " "
				+ right
				+ "  "
				+ new Path(left_path).toString()
				+ "  "
				+ path.toUri().getPath()
				+ "   "
				+ new Path(left_path).toString().equalsIgnoreCase(
						path.getParent().toUri().getPath()));
		if (new Path(left_path).toString().equalsIgnoreCase(path.getParent().toUri().getPath())) {
			tableName = left;
		} else {
			tableName = right;
		}

	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		String line = null;
		while ((line = br.readLine()) != null) {
			int index = line.indexOf(",");
			key = new Text(line.substring(0, index));
//			value = new Text(tableName + "," + line.substring(index + 1));
			System.out.println("KEY "+key+"  , VALUE "+value);
			
			String[] cvs=line.split(",");
			CSVRecord record=new CSVRecord(tableName);
			for (int i = 1; i < cvs.length; i++) {
				record.addField("c"+i, new Text(cvs[i]));
			}
			value=record;
			return true;
		}

		return false;
	}

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		return key;
	}

	@Override
	public CSVRecord getCurrentValue() throws IOException, InterruptedException {
		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		return 0;
	}

	@Override
	public void close() throws IOException {
		br.close();
	}

}
