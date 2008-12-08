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

import net.sf.jvifm.Main;
import net.sf.jvifm.ui.FileLister;
import net.sf.jvifm.ui.FileManager;
import net.sf.jvifm.ui.Messages;
import net.sf.jvifm.ui.Util;
import net.sf.jvifm.ui.shell.OptionShell;
import net.sf.jvifm.util.Digest;
import net.sf.jvifm.util.FileOperator;
import net.sf.jvifm.util.StringUtil;

import org.apache.commons.io.FilenameUtils;

public class MiscFileCommand extends Command {
	private static String[] cmdNames=new String[]{"du","md5sum","sha1sum","tabnew",
			"tabclose","cd","sort"};
			
	private String cmd;

	private String[] args;

	private FileManager fileManager = Main.fileManager;

	private FileOperator fileOperator = FileOperator.getInstance();

	public static String[] getCmdNames() {
		return cmdNames;
	}
	public MiscFileCommand(String pwd,String cmdText, String[] args,String[] selectedFiles) {
		this.pwd=pwd;
		this.cmd = cmdText;
		if (args!=null && args.length>0) {
			this.args=new String[args.length];
    		for (int i=0; i<args.length; i++) {
    			this.args[i]=replacePath(args[i]);
    		}
		} else {
			this.args=selectedFiles;
		}
	}
	
	private String replacePath(String path) {
		if (path.startsWith(".") || path.startsWith(File.separator)) return path;
		return FilenameUtils.concat(pwd, path);
	}

	public void execute() {

		if (cmd.equals("du") && args!=null && args.length > 0) {
			//infos[]=[size,dirCount,fileCount];
			long[] infos =new long[]{0,0,0};
			for (int i = 0; i < args.length; i++) {
				File file = new File(args[i]);
				fileOperator.calcDirInfo(file, infos);
			}
			fileManager.setTipInfo(infos[1]+ " dirs,"+infos[2]+" files, total size is " + StringUtil.formatSize(infos[0]));
			return;
		}
		

		if (cmd.equals("md5sum") && args!=null && args.length > 0) {
			try {
				String md5sum = Digest.digest(args[0], "MD5");
				fileManager.setTipInfo("md5sum is: " + md5sum);
			} catch (Exception e) {
				fileManager.setTipInfo("md5sum can't calculate");
			}
			return;
		}
		if (cmd.equals("sha1sum") && args!=null && args.length > 0) {
			try {
				String md5sum = Digest.digest(args[0], "SHA");
				fileManager.setTipInfo("sha1sum is: " + md5sum);
			} catch (Exception e) {
				fileManager.setTipInfo("sha1sum can't calculate");
			}
			return;
		}
		
		if (cmd.equals("tabnew")) {
			fileManager.tabnew(pwd,FileLister.FS_ROOT);
			/*
			if (args!=null && args.length > 0) {
				fileManager.tabnew((String) args[0],FileLister.FS_ROOT);
			} else {
				fileManager.tabnew(pwd,FileLister.FS_ROOT);
			}
			*/
			return;
		}
		
		if (cmd.equals("tabclose")) {
			fileManager.tabclose();
		}
	

		if (cmd.equals("cd") && args!=null && args.length >0) {
			String newPath = FilenameUtils.concat(pwd, args[0]);
			if (newPath != null)
    			if (newPath.endsWith(File.separator) && !newPath.equals(File.separator)) 
    				newPath=newPath.substring(0,newPath.length()-1);
			fileLister.visit(newPath);
			return;
		}

		if (cmd.equals("sort") ) {
			String[] options = new String[] { 
					Messages.getString("MiscFileCommand.name"), //$NON-NLS-1$
					Messages.getString("MiscFileCommand.lastModified"), //$NON-NLS-1$
					Messages.getString("MiscFileCommand.size") };//$NON-NLS-1$
	    	
	    	String result= new Util().openConfirmWindow(
	    			options,
	    			Messages.getString("MiscFileCommand.title"),  //$NON-NLS-1$ 
	    			Messages.getString("MiscFileCommand.tipInfo"),  //$NON-NLS-1$ 
	    			OptionShell.WARN); 
	    	if (result ==null ) return;
	    	
			
			if (result.equals(Messages.getString("MiscFileCommand.size"))){
				fileLister.sort("size");
			} else if (result.equals(Messages.getString("MiscFileCommand.lastModified"))) {
				fileLister.sort("date");
			} else if (result.equals(Messages.getString("MiscFileCommand.name"))) {
				fileLister.sort("name");
			}
			
			return;
		}
	}

}
