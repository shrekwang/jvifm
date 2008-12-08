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

import org.apache.commons.io.FilenameUtils;

import net.sf.jvifm.ui.Util;
import net.sf.jvifm.util.FileOperator;

public class RemoveCommand extends Command {

	private static FileOperator fileOperator = FileOperator.getInstance();
	
	private String dir = null;

	public RemoveCommand(String[] files, String dir) {
		this.files = files;
		this.dir = dir;
	}

	public void execute() {

		try {
			for (int i = 0; i < files.length; i++) {
				String baseName=FilenameUtils.getName(files[i]);
				updateStatusInfo("deleting file "+baseName);
				fileOperator.rm(files[i]);
			}
		} catch (Exception e) {
			Util.openMessageWindow(e.getMessage());
			return;	
		}
		
		removeFromActivePanel(dir,files);

		return;

	}
}
