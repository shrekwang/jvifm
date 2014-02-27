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

import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

public class SystemCommand extends Command {
	private String[] cmdArray = null;
	private String cmd = null;
	private boolean isFileShortcut = false;
	private boolean runInshell = false;
	private String[] args = null;

	public SystemCommand(String cmd, String[] args, boolean isFileShortcut) {

		this.cmd = cmd;
		this.args = args;
		this.isFileShortcut = isFileShortcut;

		if (args != null && args.length > 0) {
			cmdArray = new String[args.length + 1];
		} else {
			cmdArray = new String[1];
		}
		cmdArray[0] = cmd;

		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				cmdArray[i + 1] = args[i];
			}
		}

	}
	
	public SystemCommand(String cmd, String[] args, boolean isFileShortcut, boolean runInShell) {
		this(cmd,args,isFileShortcut);
		this.runInshell = runInShell;
	}

	// ugly .. ugly ... ugly ..
	public void execute() throws Exception {
		String ext = FilenameUtils.getExtension(cmd);
		
		if (runInshell) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
				    if (args != null & args.length > 0 ) {
				        try {
                            Runtime.getRuntime().exec(cmdArray, null, new File(pwd));
				        } catch (Exception e) {
				            
				        }
				    } else {
                        Program.launch(cmd,pwd);
				    }
				}
			});
			return;
		}
		Program program = Program.findProgram(ext);
		if (!isFileShortcut || cmdArray.length > 1) {
			if (ext.equals("bat") || ext.equals("sh")) {
				Runtime.getRuntime().exec(cmdArray, null, new File(cmd).getParentFile());
			} else {
				Runtime.getRuntime().exec(cmdArray, null, new File(pwd));
			}
		} else {
			if (ext.equals("bat") || ext.equals("sh") || program == null ) {
				Runtime.getRuntime().exec(new String[] { cmd }, null, new File(cmd).getParentFile());
			} else {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						Program.launch(cmd,pwd);
					}
				});
			}
		}

	}
}
