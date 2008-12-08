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
import java.io.File;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
public class WildcardFilter2 extends AbstractFileFilter {
	private String[] wildcards = null;
	public WildcardFilter2(String wildcard) {
		if (wildcard == null) {
			throw new java.lang.IllegalArgumentException();
		}
		wildcards = new String[] { wildcard };
	}
	public WildcardFilter2(String[] wildcards) {
		if (wildcards == null) {
			throw new java.lang.IllegalArgumentException();
		}
		this.wildcards = wildcards;
	}
	@SuppressWarnings("unchecked")
	public WildcardFilter2(List wildcards) {
		if (wildcards == null) {
			throw new java.lang.IllegalArgumentException();
		}
		this.wildcards = (String[]) wildcards.toArray(new String[wildcards .size()]);
	}
	public boolean accept(File dir, String name) {
		for (int i = 0; i < wildcards.length; i++) {
			if (FilenameUtils.wildcardMatch(name, wildcards[i])) {
				return true;
			}
		}
		return false;
	}
	public boolean accept(File file) {
		for (int i = 0; i < wildcards.length; i++) {
			if (FilenameUtils.wildcardMatch(file.getName(), wildcards[i])) {
				return true;
			}
		}
		return false;
	}
}
