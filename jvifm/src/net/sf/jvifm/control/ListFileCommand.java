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
import java.io.FilenameFilter;
import java.util.Arrays;

import net.sf.jvifm.util.FileComprator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.comparator.ExtensionFileComparator;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.comparator.SizeFileComparator;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class ListFileCommand extends Command {
	
	private CommandLine cmdLine = null;
	private File[] subFiles=null;
	
	public static Options options;

	static {
		options = new Options();
		options.addOption("a", "all", false,
				"do not ignore entries starting with .");
		options.addOption("r", false, "reverse order while sorting");
		options.addOption("R", "recursive", false,
				" list subdirectories recursively");
		options.addOption("S", false, "sort by file size");
		options.addOption("t", false, "sort by modification time");
		options.addOption("U", false,
				"do not sort; list entries in directory order");
		options.addOption("X", false, "sort alphabetically by entry extension");
	}

	public ListFileCommand(CommandLine cmdLine) {
		this.cmdLine = cmdLine;
	}
	

	@SuppressWarnings("unchecked")
	public void execute() throws Exception {

	
		File currentDir=new File(pwd);
		
		String[] filters=cmdLine.getArgs();
		OrFileFilter orFileFilter=new OrFileFilter();
		
		if (filters==null || filters.length<1) {
			orFileFilter.addFileFilter(new WildcardFileFilter("*"));
		} else {
    		for (int i=0; i<filters.length; i++) {
    			orFileFilter.addFileFilter( new WildcardFileFilter(filters[i]));
    		}
		}
		
		
		if (cmdLine.hasOption("R")) {
			//FileFinder finder = new FileFinder();
			//subFiles = finder.find(currentDir, orFileFilter);
		} else {
			subFiles=currentDir.listFiles((FilenameFilter)orFileFilter);
		}
		
		if (cmdLine.hasOption("r")) {
			if (cmdLine.hasOption("t") ) {
        		Arrays.sort(subFiles,LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			} else if (cmdLine.hasOption("S")) {
        		Arrays.sort(subFiles,SizeFileComparator.SIZE_REVERSE);
			} else if (cmdLine.hasOption("X") ) {
        		Arrays.sort(subFiles,ExtensionFileComparator.EXTENSION_REVERSE);
			} else {
				Arrays.sort(subFiles,FileComprator.getFileComprator("name", true));
			}
		} else {
			if (cmdLine.hasOption("t") ) {
        		Arrays.sort(subFiles,LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
			} else if (cmdLine.hasOption("S")) {
        		Arrays.sort(subFiles,SizeFileComparator.SIZE_COMPARATOR);
			} else if (cmdLine.hasOption("X") ) {
        		Arrays.sort(subFiles,ExtensionFileComparator.EXTENSION_COMPARATOR);
			} else {
				Arrays.sort(subFiles,FileComprator.getFileComprator("name", false));
			}
		}
		
		listSubFileInPanel(subFiles);
		
	}

	

}
