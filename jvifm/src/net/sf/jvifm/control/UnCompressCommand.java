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

public class UnCompressCommand extends InterruptableCommand {
	
	private String archiveFilePath;
	private String dstPath;

	public UnCompressCommand(String archiveFilePath, String dstPath) {
		this.archiveFilePath = archiveFilePath;
		this.dstPath = dstPath;
	}

	public void execute() {
		String ext = FilenameUtils.getExtension(archiveFilePath);

		try {
			if (ext.equals("zip") || ext.equals("jar") || ext.equals("war")
					|| ext.equals("ear")) {

				fileModelManager.unzip(archiveFilePath, dstPath);

			} else if (ext.equals("tar") || ext.equals("tgz")
					|| archiveFilePath.endsWith("tar.bz2")
					|| archiveFilePath.endsWith("tar.gz")) {
				fileModelManager.untar(archiveFilePath, dstPath);
			} else {
				Util.openMessageWindow("unknow archive format");
				return;
			}

		} catch (Exception e) {
			Util.openMessageWindow(e.getMessage());
			e.printStackTrace();
		}
	}

	
}
