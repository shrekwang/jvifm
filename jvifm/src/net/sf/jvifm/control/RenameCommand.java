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
import java.util.ArrayList;

import net.sf.jvifm.Main;
import net.sf.jvifm.model.filter.WildcardFilter2;
import net.sf.jvifm.util.FileOperator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.eclipse.swt.widgets.Display;

public class RenameCommand extends Command{
	private String fromStr;
	private String toStr;
	
	private FileOperator fileOperator = FileOperator.getInstance();
	
    public static Options options=new Options();
		
	public void execute() throws Exception {
		ArrayList destFileList = filterWildCard(dstDir);
		for (int i = 0; i < destFileList.size(); i++) {
			fileOperator.rename(fromStr, toStr, (String) destFileList.get(i));
		}
		refreshActivePanel();
		
		return;

	}
	public RenameCommand(CommandLine cmdLine) {
		String[] args=cmdLine.getArgs();
		this.fromStr=args[0];
		this.toStr=args[1];
		this.dstDir=args[2];
	}
	
	private ArrayList filterWildCard(final String wildcardPath) {
		final ArrayList result = new ArrayList();
		if (wildcardPath == null || wildcardPath.trim().equals(""))
			return null;

		Display.getDefault().syncExec(new Runnable() {
			public void run() {

				File dir = new File(wildcardPath).getParentFile();
				if (dir == null)
					dir = new File(Main.fileManager.getActivePanel().getPwd());

				String wildcardName = new File(wildcardPath).getName();

				FilenameFilter filenameFilter = new AndFileFilter(
						new NotFileFilter(new PrefixFileFilter(".")),
						new WildcardFilter2(wildcardName));
				File[] subFiles = dir.listFiles(filenameFilter);
				for (int i = 0; i < subFiles.length; i++) {
					result.add(subFiles[i].getAbsolutePath());
				}
			}
		});
		return result;

	}

}


