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

package net.sf.jvifm.ui;

import java.io.File;

import net.sf.jvifm.Main;

import net.sf.jvifm.ui.shell.OptionShell;
import net.sf.jvifm.ui.shell.PreferenceShell;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class Util {
	
	private String result;
	
	public static void openMessageWindow(final String message) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				MessageBox box=new MessageBox(Main.fileManager.getShell(),SWT.NONE);
				if (message==null) {
					box.setMessage("unknow error");
				} else {
					box.setMessage(message);
				}
				box.open();
			}
		});
	}
	public static void openPreferenceShell(final Shell shell) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
			    new PreferenceShell().open(shell);
			}
		});
	}
	
	
	
	public  String openConfirmWindow(final String[] option, final String title,final String message,final int shellType) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Shell shell=Main.fileManager.getShell();
				OptionShell optionShell=new OptionShell(shell,title,message,option,shellType);
				Point optionSize=optionShell.getSize();
				Point location=shell.getLocation();
				Point size=shell.getSize();
				int offsetX=location.x+(shell.getSize().x/2)-(optionSize.x/2);
				int offsetY=location.y+(shell.getSize().y/2)-(optionSize.y/2);
				result=optionShell.open(offsetX,offsetY);
			}
		});
		return result;
				
	}
	
	public static void openTerminal(String path) {
		String ENV_OS=System.getProperty("os.name");
		String terminal_exe="";
		if (ENV_OS.substring(0,3).equalsIgnoreCase("win")) {
			terminal_exe="cmd /c start";
		}else {
			terminal_exe="gnome-terminal";
		}
		
		try {
			Runtime.getRuntime().exec(terminal_exe,null,new File(path));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void openFileWithDefaultApp(String path) {
		if (path == null ) return;
		String ext=FilenameUtils.getExtension(path);
		File file=new File(path);
		if (ext.equals("bat") || ext.equals("sh")) { 
			try {
				Runtime.getRuntime().exec(new String[]{path},null,file.getParentFile());
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		} else {
			Program.launch(path);
		}
		
	}

}
