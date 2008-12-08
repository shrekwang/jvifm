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
package net.sf.jvifm.model.filter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.io.filefilter.AbstractFileFilter;
public class FileContentFilter extends AbstractFileFilter {
	private String pattern = null;
	public FileContentFilter(String pattern) {
		this.pattern = pattern;
	}
	public boolean accept(File file) {
		if (file==null || file.isDirectory()) return false;
		return isContainsPattern(file, pattern);
	}
	private boolean isContainsPattern(File file, String pattern) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String tmp = "";
			while (true) {
				tmp = br.readLine();
				if (tmp == null)
					break;
				if (tmp.indexOf(pattern) > -1)
					return true;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
