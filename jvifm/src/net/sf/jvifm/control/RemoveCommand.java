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

import static net.sf.jvifm.ui.Messages.msgFileDelete;
import static net.sf.jvifm.ui.Messages.msgOptionCancel;
import static net.sf.jvifm.ui.Messages.msgOptionNo;
import static net.sf.jvifm.ui.Messages.msgOptionYes;
import static net.sf.jvifm.ui.Messages.msgRmConfirmDlgTitle;
import net.sf.jvifm.ui.Util;
import net.sf.jvifm.ui.shell.OptionShell;

import org.apache.commons.io.FilenameUtils;

public class RemoveCommand extends Command {


	private String dir = null;

	public RemoveCommand(String[] files, String dir) {
		this.files = files;
		this.dir = dir;
	}

	public void execute() {
		
		String[] options = new String[] { msgOptionYes, msgOptionNo, msgOptionCancel };
		
		String result = new Util().openConfirmWindow(options, 
				msgRmConfirmDlgTitle, msgFileDelete, OptionShell.WARN); 
		
		if (result == null) return;
		if (! result.equals(msgOptionYes)) return;

		try {
			for (int i = 0; i < files.length; i++) {
				String baseName = FilenameUtils.getName(files[i]);
				updateStatusInfo("deleting file " + baseName);
				fileModelManager.rm(files[i]);
			}
		} catch (Exception e) {
			Util.openMessageWindow(e.getMessage());
			return;
		}
		return;

	}
}
