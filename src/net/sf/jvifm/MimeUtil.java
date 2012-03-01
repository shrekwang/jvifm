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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class MimeUtil {
	private static MimeUtil instance = null;
	private static HashMap<String,String> mimeMap=new HashMap<String,String>();

	private MimeUtil() {
	}

	public static MimeUtil getInstance() {
		if (instance == null) {

			try {
				InputStream is = MimeUtil.class.getClassLoader().getResourceAsStream("mimes.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				while (true) {
					String tmp = br.readLine();
					if (tmp == null) break;
					String [] info=tmp.split(" ");
					String path="mimes/"+info[1].replace("/", "-").trim()+".png";
					mimeMap.put(info[0].trim(), path);
				}
				br.close();
				is.close();
			} catch (Exception e) {

			}
			instance=new MimeUtil();
		}
		return instance;
	}
	
	public String getMimeIconPath(String postfix) {
		return mimeMap.get(postfix);
	}

}
