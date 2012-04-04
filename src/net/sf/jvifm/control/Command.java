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
import net.sf.jvifm.model.FileModelManager;
import net.sf.jvifm.ui.FileLister;

import org.eclipse.swt.widgets.Display;

public abstract class Command {

	protected String pwd;
	protected String srcDir;
	protected String dstDir;
	protected String[] files;
	protected FileLister fileLister;
	protected FileLister inActiveFileLister;
	
	protected FileModelManager fileModelManager=FileModelManager.getInstance();

	private String action;

	public abstract void execute() throws Exception;

	public void refreshActivePanel() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				fileLister.refresh();
			}
		});
	}

	public void switchToNormal() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				fileLister.switchToNormalMode();
			}
		});
	}

	public void listSubFileInPanel(final File[] files) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				fileLister.removeAllItem();
				fileLister.generateItems(files, false);
				fileLister.switchToNormalMode();
			}
		});
	}

	public void addSubFileInPanel(final File file) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				fileLister.generateItems(new File[] {file}, true);
			}
		});
	}

	public void removeAllItemInPanel() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				fileLister.removeAllItem();
			}
		});
	}


	public void showStatusAnimation() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Main.fileManager.showStatusAnimation();
			}
		});
	}

	public void hideStatusAnimation() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Main.fileManager.hideStatusAnimation();
			}
		});
	}

	public void updateStatusInfo(final String status) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Main.fileManager.setStatusInfo(status);
			}
		});

	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDstDir() {
		return dstDir;
	}

	public void setDstDir(String dstDir) {
		this.dstDir = dstDir;
	}

	public String[] getFiles() {
		return files;
	}

	public void setFiles(String[] files) {
		this.files = files;
	}

	public String getSrcDir() {
		return srcDir;
	}

	public void setSrcDir(String srcDir) {
		this.srcDir = srcDir;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public FileLister getFileLister() {
		return fileLister;
	}

	public FileLister getInActiveFileLister() {
		return inActiveFileLister;
	}

	public void setFileLister(FileLister fileLister) {
		this.fileLister = fileLister;
	}

	public void setInActiveFileLister(FileLister inActiveFileLister) {
		this.inActiveFileLister = inActiveFileLister;
	}

}
