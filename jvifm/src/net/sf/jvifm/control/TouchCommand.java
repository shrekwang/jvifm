package net.sf.jvifm.control;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;

public class TouchCommand extends Command {
	public static final Options options;
	private CommandLine cmdLine = null;

	static {
		options = new Options();
		options.addOption("d", "date", true,
				" parse STRING and use it instead of current time");
		options.addOption("r", "reference", true,
				"use this file's times instead of current time");
	}

	public TouchCommand(CommandLine cmdLine, String[] selectedFiles) {
		this.cmdLine = cmdLine;
		this.files = selectedFiles;

	}

	public void execute() throws Exception {

		String[] filenames = cmdLine.getArgs();

		if (filenames.length <= 0)
			filenames = this.files;

		long lastModified = 0;
		if (cmdLine.hasOption("d")) {

			String dateValue = cmdLine.getOptionValue("d");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = format.parse(dateValue);
			lastModified = date.getTime();

		}
		if (cmdLine.hasOption("r")) {
			String referFile = FilenameUtils.concat(pwd, cmdLine
					.getOptionValue("r"));
			File file = new File(referFile);
			if (file.exists()) {
				lastModified = file.lastModified();
			}
		}

		for (int i = 0; i < filenames.length; i++) {
			String fileName = FilenameUtils.concat(pwd, filenames[i]);
			if (lastModified == 0)  lastModified=System.currentTimeMillis();
			fileModelManager.touch(fileName, lastModified);
		}

	}

}
