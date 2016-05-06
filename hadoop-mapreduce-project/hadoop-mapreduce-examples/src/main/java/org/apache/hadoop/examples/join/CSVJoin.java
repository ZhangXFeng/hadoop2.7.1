package org.apache.hadoop.examples.join;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class CSVJoin {
	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException, URISyntaxException,
			ParseException {

		Options options = new Options();
		Option option = new Option("t", "type", true, "join type");
		option.setRequired(true);
		options.addOption(option);

		option = new Option(null, "left-path", true, "left table data path");
		option.setRequired(true);
		options.addOption(option);

		option = new Option(null, "right-path", true, "right table data path");
		option.setRequired(true);
		options.addOption(option);

		option = new Option(null, "output-path", true, "output path");
		option.setRequired(true);
		options.addOption(option);
		option = new Option("h", "help", false, "display help text");
		option.setRequired(false);
		options.addOption(option);

		CommandLineParser parser = new GnuParser();
		CommandLine commandLine = parser.parse(options, args);

		if (commandLine.hasOption('h')) {
			new HelpFormatter().printHelp("CSVJoin", options, true);
			return;
		}
		Configuration conf = new Configuration();
		conf.set("join.type", commandLine.getOptionValue('t'));

		conf.set("left.path", commandLine.getOptionValue("left-path"));
		conf.set("right.path", commandLine.getOptionValue("right-path"));


		conf.set("left.tableName", "left");
		conf.set("right.tableName", "right");
		Job job = Job.getInstance(conf, "CSV Join");
		job.setJarByClass(CSVJoin.class);
		job.setInputFormatClass(CSVInputFormat.class);
		job.setReducerClass(CSVReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setPartitionerClass(CSVPartitioner.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(CSVRecord.class);
		job.setNumReduceTasks(2);
		FileInputFormat.addInputPath(job,
				new Path(commandLine.getOptionValue("left-path")));
		FileInputFormat.addInputPath(job,
				new Path(commandLine.getOptionValue("right-path")));
		FileOutputFormat.setOutputPath(job, new Path(
				commandLine.getOptionValue("output-path")));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
