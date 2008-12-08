package net.sf.jvifm.control;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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


import java.util.Date;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;

public class TouchCommand extends Command {
	public static Options options;
	private CommandLine cmdLine = null;

	static {
		options = new Options();
		options.addOption("d", "date", true, " parse STRING and use it instead of current time");
		options.addOption("r", "reference", true, "use this file's times instead of current time");
	}

	public TouchCommand(CommandLine cmdLine,String[] selectedFiles) {
		this.cmdLine=cmdLine;
		this.files=selectedFiles;
		
	}
	public void execute() throws Exception {
			
		String[] filenames=cmdLine.getArgs();
		
		if (filenames.length<=0) filenames=this.files;
		
		long lastModified=0;
		if (cmdLine.hasOption("d")) {
			
			String dateValue=cmdLine.getOptionValue("d");
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			Date date=format.parse(dateValue);
			lastModified=date.getTime();
				
		}
		if (cmdLine.hasOption("r")) {
			String referFile=FilenameUtils.concat(pwd,cmdLine.getOptionValue("r"));
			File file=new File(referFile);
			if (file.exists()) {
				lastModified=file.lastModified();
			}
		}
		
		for (int i=0; i<filenames.length; i++) {
			String fileName=FilenameUtils.concat(pwd,filenames[i]);
			if (lastModified !=0 ) {
    			touch(fileName, lastModified);
    			addToPanel(pwd,new String[] {fileName});
			} else {
    			touch(fileName);
    			addToPanel(pwd,new String[] {fileName});
			}
		}
			
		

	}
	
	public boolean touch(String filepath) {
		return touch(filepath,System.currentTimeMillis());
	}
	
	public boolean touch(String filepath,long lastModified) {
		File file = new File(filepath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
    		file.setLastModified(lastModified);
		}
		return true;
	}

}
