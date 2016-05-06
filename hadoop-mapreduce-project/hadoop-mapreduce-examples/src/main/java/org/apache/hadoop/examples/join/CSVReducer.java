package org.apache.hadoop.examples.join;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class CSVReducer extends Reducer<Text, CSVRecord, Text, Text> {

	private String join_type = "inner";
	private String left;
	private String right;

	@Override
	protected void setup(Reducer<Text, CSVRecord, Text, Text>.Context context)
			throws IOException, InterruptedException {
		join_type = context.getConfiguration().get("join.type", "inner");
		left = context.getConfiguration().get("left.tableName");
		right = context.getConfiguration().get("right.tableName");
		System.out.println(join_type + " " + left + " " + right);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void reduce(Text key, Iterable<CSVRecord> values,
			Reducer<Text, CSVRecord, Text, Text>.Context context)
			throws IOException, InterruptedException {
		System.out.println("KEY " + key);
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		ArrayList<CSVRecord> texts = new ArrayList<CSVRecord>();
		for (CSVRecord text : values) {
			System.out.println("ORI VALUE = " + text);
			texts.add(text);

			String value = text.toString();
			int index = value.indexOf(",");
			String tableName = value.substring(0, index);
			System.out.println("VALUE " + value);
			if (map.containsKey(tableName)) {
				map.get(tableName).add(value.substring(index + 1));
			} else {
				List<String> list = new ArrayList<String>();
				list.add(value.substring(index + 1));
				map.put(tableName, list);
			}

		}
		List<String> leftVals = map.get(left);
		System.out.println("LEFTVALS " + leftVals);
		List<String> rigthVals = map.get(right);
		System.out.println("RIGHTVALS " + rigthVals);
		if (join_type.equalsIgnoreCase("inner")) {
			if (map.size() < 2 || leftVals == null || rigthVals == null) {
				return;
			}

			for (Iterator iterator = leftVals.iterator(); iterator.hasNext();) {

				String leftVal = (String) iterator.next();

				for (Iterator iterator2 = rigthVals.iterator(); iterator2
						.hasNext();) {
					Text value = new Text();
					value.append(leftVal.getBytes(), 0,
							leftVal.getBytes().length);
					String rightVal = "," + (String) iterator2.next();
					value.append(rightVal.getBytes(), 0,
							rightVal.getBytes().length);
					context.write(key, value);
				}

			}
		} else if (join_type.equalsIgnoreCase("left")) {
			if (leftVals == null) {
				return;
			}

			for (Iterator iterator = leftVals.iterator(); iterator.hasNext();) {

				String leftVal = (String) iterator.next();

				if (null == rigthVals) {
					context.write(key, new Text(leftVal + ",NULL"));
				} else {
					for (Iterator iterator2 = rigthVals.iterator(); iterator2
							.hasNext();) {
						Text value = new Text();
						value.append(leftVal.getBytes(), 0,
								leftVal.getBytes().length);
						String rightVal = "," + (String) iterator2.next();
						value.append(rightVal.getBytes(), 0,
								rightVal.getBytes().length);
						context.write(key, value);
					}
				}

			}

		} else if (join_type.equalsIgnoreCase("right")) {
			if (rigthVals == null) {
				return;
			}
			if (null == leftVals) {
				for (Iterator iterator = rigthVals.iterator(); iterator
						.hasNext();) {
					String string = (String) iterator.next();
					context.write(key, new Text("NULL," + string));
				}

			} else {
				for (Iterator iterator = leftVals.iterator(); iterator
						.hasNext();) {

					String leftVal = (String) iterator.next();

					for (Iterator iterator2 = rigthVals.iterator(); iterator2
							.hasNext();) {
						Text value = new Text();
						value.append(leftVal.getBytes(), 0,
								leftVal.getBytes().length);
						String rightVal = "," + (String) iterator2.next();
						value.append(rightVal.getBytes(), 0,
								rightVal.getBytes().length);
						context.write(key, value);
					}
				}
			}
		}
	}
}
