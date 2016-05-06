package org.apache.hadoop.examples.join;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

public class CSVRecord implements WritableComparable<CSVRecord> {

	private String tableName;
	private Map<String, Text> values = new HashMap<String, Text>();

	public CSVRecord() {

	}

	public CSVRecord(String tableName) {
		this.tableName = tableName;
	}

	public void addField(String columnName, Text value) {
		values.put(columnName, value);
	}

	public Text getFieldValue(String columnName) {
		return values.get(columnName);
	}

	public String getTableName() {
		return tableName;
	}

	@Override
	public String toString() {
		Set<Map.Entry<String, Text>> entries = values.entrySet();
		StringBuffer sb = new StringBuffer();
		sb.append(tableName + ",");
		for (Iterator<Entry<String, Text>> iterator = entries.iterator(); iterator
				.hasNext();) {
			Entry<String, Text> entry = (Entry<String, Text>) iterator.next();
			Text text = entry.getValue();
			sb.append(text.toString() + ",");
		}
		return sb.toString().substring(0, sb.length() - 1);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		byte[] value=toString().getBytes();
		
		out.writeInt(value.length);
		out.write(value);

	}

	@Override
	public void readFields(DataInput in) throws IOException {
		int length=in.readInt();
		byte[] value=new byte[length];
		in.readFully(value, 0, length);
		String line = new String(value);
		System.out.println("LINE = "+line);
		String[] strings = line.split(",");
		tableName = strings[0];
		for (int i = 1; i < strings.length; i++) {
			values.put("c" + (i - 1), new Text(strings[i]));
		}
	}

	@Override
	public int compareTo(CSVRecord o) {
		return 0;
	}

}
