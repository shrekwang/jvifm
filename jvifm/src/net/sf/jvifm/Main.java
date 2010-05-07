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

package net.sf.jvifm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import net.sf.jvifm.control.CommandRegister;
import net.sf.jvifm.control.FindCommand;
import net.sf.jvifm.control.InterruptableCommand;
import net.sf.jvifm.control.ListFileCommand;
import net.sf.jvifm.control.MetaCommand;
import net.sf.jvifm.control.MiscFileCommand;
import net.sf.jvifm.control.TouchCommand;
import net.sf.jvifm.model.AppStatus;
import net.sf.jvifm.model.BookmarkManager;
import net.sf.jvifm.model.ShortcutsManager;
import net.sf.jvifm.model.MimeManager;
import net.sf.jvifm.ui.FileLister;
import net.sf.jvifm.ui.FileManager;
import net.sf.jvifm.util.HomeLocator;
import net.sf.jvifm.util.FileListerServer;

import org.apache.commons.cli.Options;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Main {
	
	public static FileManager fileManager;
	public static Display display;
	public static Shell shell;
	public static InterruptableCommand currentJob;

	public static int operatingSystem;
	public static final int LINUX=1;
	public static final int WINDOWS=2;
	
	static {
        if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) {
            operatingSystem = WINDOWS;
        } else {
            operatingSystem = LINUX;
        }
	 }

	public static void main(String[] args) {
		initConfigDir();
		initCommandRegister();
        initServer();
		
		try {
			 File logFile=new File(HomeLocator.getConfigHome() + File.separator + "jvifm.log");
			PrintStream ps=new PrintStream(new FileOutputStream(logFile));
			System.setOut(ps);
			System.setErr(ps);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		display = new Display();
		fileManager = new FileManager();
		shell = fileManager.open(display);
		String[][] panelDirs=AppStatus.loadAppStatus();
		if (! ( panelDirs==null) && panelDirs.length>0) {
			for (int i=0; i<panelDirs.length; i++) {
                String leftPath = FileLister.FS_ROOT;
                String rightPath = FileLister.FS_ROOT;
                if (panelDirs[i][0] !=null && new File(panelDirs[i][0]).isDirectory()) {
                    leftPath = panelDirs[i][0];
                }
                if (panelDirs[i][1] !=null && new File(panelDirs[i][1]).isDirectory()) {
                    rightPath = panelDirs[i][1];
                }
                fileManager.tabnew(leftPath, rightPath);
			}
		} else {
    			fileManager.tabnew(FileLister.FS_ROOT,FileLister.FS_ROOT);
		}

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		
	
	}
	
	

	public static void initConfigDir() {
		File file = new File(HomeLocator.getConfigHome());
		if (!file.exists()) file.mkdir();
	}

    public static void initServer() {
        try {
            new FileListerServer().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public static void exit() {
		BookmarkManager.getInstance().store();
		ShortcutsManager.getInstance().store();
		MimeManager.getInstance().store();
		AppStatus.writeAppStatus();
		shell.dispose();
		display.dispose();
		System.exit(0);
	}
	
	
	public static void initCommandRegister() {
		CommandRegister commandRegister=CommandRegister.getInstance();
		
		commandRegister.register("find", FindCommand.options);
		commandRegister.register("ls", ListFileCommand.options);
		commandRegister.register("touch", TouchCommand.options);
		commandRegister.register("mkdir", new Options());
		commandRegister.register("compress", new Options());
		
		String[] cmdNames=MetaCommand.getCmdNames();
		for (int i=0; i<cmdNames.length; i++) {
			commandRegister.register(cmdNames[i], new Options());
		}
		cmdNames=MiscFileCommand.getCmdNames();
		for (int i=0; i<cmdNames.length; i++) {
			commandRegister.register(cmdNames[i], new Options());
		}
		
	}

}
