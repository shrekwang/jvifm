/*
 * Jvifm - Java vifm (File Manager with vi like key binding)
 *
 * Copyright (C) 2006 wsn <shrek.wang@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package net.sf.jvifm.control;

import java.io.File;

import net.sf.jvifm.model.filter.AgeFileFilter2;
import net.sf.jvifm.model.filter.FileContentFilter;
import net.sf.jvifm.model.filter.SizeFileFilter2;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class FindCommand extends InterruptableCommand {

	private CommandLine cmdLine = null;
	public static final Options options;

	static {
		options = new Options();
		options.addOption("name", true, "file name");
		options.addOption("size", true, "file size");
		options.addOption("text", true, "file contains the text");
		options.addOption("mtime", true, "last modified time of file");
	}

	public FindCommand(CommandLine cmdLine) {
		this.cmdLine = cmdLine;
	}

	public void execute() {
		removeAllItemInPanel();
		find(new File(pwd), cmdLine);
		// listSubFileInPanel(files);

	}

	@SuppressWarnings("unchecked")
	public void find(File directory, CommandLine cmdLine) {
		IOFileFilter filter = findingFilter(cmdLine);
		find(directory, filter);
		// if (filter.accept(directory)) {
		// foundFiles.add(directory);
		// }
	}

	@SuppressWarnings("unchecked")
	private void find(File directory, IOFileFilter filter) {
		if (this.aborted)
			return;
		File[] list = directory.listFiles();
		if (list == null) {
			return;
		}
		int sz = list.length;
		for (int i = 0; i < sz; i++) {
			if (this.aborted)
				return;
			File tmp = list[i];
			if (filter.accept(tmp)) {
				// retlist.add(tmp);
				addSubFileInPanel(tmp);
			}
			if (tmp.isDirectory()) {
				find(tmp, filter);
			}
		}
	}

	private IOFileFilter findingFilter(CommandLine cmdLine) {

		AndFileFilter filters = new AndFileFilter();
		Option[] options = cmdLine.getOptions();
		for (int i = 0; i < options.length; i++) {
			IOFileFilter filter = createFilter(options[i].getOpt(), options[i]
					.getValue());
			filters.addFileFilter(filter);
		}
		return filters;
	}

	private IOFileFilter createFilter(String option, String argument) {

		boolean invert = false;

		if (option.equals("mtime")) {
			String[] arguments = getArgumentValueArray(argument);
			if (arguments.length == 2) {
				return new AndFileFilter(createAgeFilter(arguments[0]),
						createAgeFilter(arguments[1]));
			} else {
				return createAgeFilter(arguments[0]);
			}
		}
		if (option.equals("size")) {
			String[] arguments = getArgumentValueArray(argument);
			if (arguments.length == 2) {
				return new AndFileFilter(createSizeFilter(arguments[0]),
						createSizeFilter(arguments[1]));
			} else {
				return createSizeFilter(arguments[0]);
			}
		}
		if (option.equals("name")) {
			IOFileFilter filter = new WildcardFileFilter(argument.toString());
			return (invert ? new NotFileFilter(filter) : filter);
		}
		if (option.equals("text")) {
			IOFileFilter filter = new FileContentFilter(argument.toString());
			return (invert ? new NotFileFilter(filter) : filter);
		}

		return null;
	}

	private String[] getArgumentValueArray(String argument) {

		String[] result = null;
		if (!argument.startsWith("-") && argument.indexOf("-") > 0) {
			result = argument.split("-");
			result[0] = "+" + result[0];
			result[1] = "-" + result[1];
		} else {
			result = new String[] { argument };
		}
		return result;
	}

	private IOFileFilter createAgeFilter(String argument) {
		long value;
		int flag;
		if (argument.startsWith("-")) {
			flag = AgeFileFilter2.LT;
			value = parseTime(argument.substring(1));
		} else if (argument.startsWith("+")) {
			flag = AgeFileFilter2.GT;
			value = parseTime(argument.substring(1));
		} else {
			flag = AgeFileFilter2.EQ;
			value = parseTime(argument);
		}
		IOFileFilter filter = new AgeFileFilter2(value, flag);
		return filter;
	}

	private IOFileFilter createSizeFilter(String argument) {
		long size;
		int flag;
		if (argument.startsWith("-")) {
			flag = SizeFileFilter2.LT;
			size = parseSize(argument.substring(1));
		} else if (argument.startsWith("+")) {
			flag = SizeFileFilter2.GT;
			size = parseSize(argument.substring(1));
		} else {
			flag = SizeFileFilter2.EQ;
			size = parseSize(argument);
		}
		IOFileFilter filter = new SizeFileFilter2(size, flag);
		return filter;
	}

	public long parseTime(String timeString) {

		if (timeString.endsWith("m")) {
			return Long.parseLong(timeString.substring(0,
					timeString.length() - 1)) * 1000 * 60;
		} else if (timeString.endsWith("h")) {
			return Long.parseLong(timeString.substring(0,
					timeString.length() - 1)) * 1000 * 60 * 60;
		} else if (timeString.endsWith("d")) {
			return Long.parseLong(timeString.substring(0,
					timeString.length() - 1))
					* 1000 * 60 * 60 * 24;
		} else {
			return Long.parseLong(timeString.substring(0,
					timeString.length() - 1))
					* 1000 * 60 * 60 * 24;
		}
	}

	public long parseSize(String sizeString) {
		if (sizeString.endsWith("k")) {
			return Long.parseLong(sizeString.substring(0,
					sizeString.length() - 1)) * 1024;
		} else if (sizeString.endsWith("m")) {
			return Long.parseLong(sizeString.substring(0,
					sizeString.length() - 1)) * 1024 * 1024;
		} else if (sizeString.endsWith("g")) {
			return Long.parseLong(sizeString.substring(0,
					sizeString.length() - 1)) * 1024 * 1024 * 1024;
		} else {
			return Long.parseLong(sizeString);
		}
	}

}
