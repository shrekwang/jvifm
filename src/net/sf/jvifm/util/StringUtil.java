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
package net.sf.jvifm.util;

import java.util.*;
import java.text.*;

public class StringUtil {

	private static SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static DecimalFormat df = new DecimalFormat("#.##");
	
	
	public static String formatDate(long dateValue) {
		return simpleFormat.format(new Date(dateValue));
	}

	public static String formatSize(long fileSize) {

		if (fileSize < 1024) {
			return String.valueOf(fileSize)+"bytes";
		} else if (fileSize >= 1024 && fileSize < 1048576) {
			float temp = (float) fileSize / 1024;
			return df.format(temp) + "Kb";
		} else if (fileSize >= 1048576 && fileSize < 1073741824) {
			float temp = (float) fileSize / 1048576;
			return df.format(temp) + "Mb";
		} else {
			float temp = (float) fileSize / (1048576 * 1024);
			return df.format(temp) + "Gb";
		}
	}

}
