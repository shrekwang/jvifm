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

import net.sf.jvifm.ui.Util;

import org.apache.commons.io.FilenameUtils;

public class CompressCommand extends Command {
	private String dstFile;
	private String[] paths;

	public CompressCommand(String dstFile, String[] paths) {
		this.dstFile = dstFile;
		this.paths = paths;
	}

	public void execute() {

		String ext = FilenameUtils.getExtension(dstFile);

		try {
			if (ext.equals("zip") || ext.equals("jar") || ext.equals("war")
					|| ext.equals("ear")) {
				fileModelManager.zip(dstFile, paths);

			} else if (ext.equals("tar")) {
				fileModelManager.tar(dstFile, paths, null);
			} else if (ext.equals("tgz") || dstFile.endsWith("tar.gz")) {
				fileModelManager.tar(dstFile, paths, "gz");
			} else if (dstFile.endsWith("tar.bz2")) {
				fileModelManager.tar(dstFile, paths, "bz2");
			} else {
				Util.openMessageWindow("unknow archive format");
				return;
			}
		} catch (Exception e) {
			Util.openMessageWindow(e.getMessage());
		}
		switchToNormal();
	}
}
