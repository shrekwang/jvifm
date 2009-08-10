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

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ResourceManager {
	private static ResourceManager instance = new ResourceManager();
	private static MimeUtil mimeUtil=MimeUtil.getInstance();
	@SuppressWarnings("unchecked")
	private static HashMap resources = new HashMap();
	private static Image fileImage = getImage("file.png");     //$NON-NLS-1$
	private static Image folderImage = getImage("folder.png"); //$NON-NLS-1$

	@SuppressWarnings("unchecked")
	public static Image getImage(String url) {
		url = "icons/" + url;
		try {
			url = url.replace('\\', '/');
			if (url.startsWith("/"))
				url = url.substring(1);
			if (resources.containsKey(url))
				return (Image) resources.get(url);
			Image img = new Image(Display.getDefault(), instance.getClass()
					.getClassLoader().getResourceAsStream(url));
			if (img != null)
				resources.put(url, img);
			return img;
		} catch (Exception e) {
			System.err
					.println("SWTResourceManager.getImage: Error getting image "
							+ url + ", " + e);
			return null;
		}
	}
	
	public static Image getMimeImage(File file) {
		if (file.isDirectory()) return folderImage;
		String extName=FilenameUtils.getExtension(file.getName());
		String path=mimeUtil.getMimeIconPath(extName);
		if (path==null) return fileImage;
		Image mimeImage= getImage(path);
		if (mimeImage ==null) return fileImage;
		return mimeImage;
	}
	

	public static Font getFont(String name, int size, int style) {
		return getFont(name, size, style, false, false);
	}

	@SuppressWarnings("unchecked")
	public static Font getFont(String name, int size, int style,
			boolean strikeout, boolean underline) {
		String fontName = name + "|" + size + "|" + style + "|" + strikeout
				+ "|" + underline;
		if (resources.containsKey(fontName))
			return (Font) resources.get(fontName);
		FontData fd = new FontData(name, size, style);
		if (strikeout || underline) {
			try {
				Class lfCls = Class
						.forName("org.eclipse.swt.internal.win32.LOGFONT");
				Object lf = FontData.class.getField("data").get(fd);
				if (lf != null && lfCls != null) {
					if (strikeout)
						lfCls.getField("lfStrikeOut").set(lf,
								new Byte((byte) 1));
					if (underline)
						lfCls.getField("lfUnderline").set(lf,
								new Byte((byte) 1));
				}
			} catch (Throwable e) {
				System.err.println("Unable to set underline or strikeout"
						+ " (probably on a non-Windows platform). " + e);
			}
		}
		Font font = new Font(Display.getDefault(), fd);
		resources.put(fontName, font);
		return font;
	}

	@SuppressWarnings("unchecked")
	public static void disposeResource() {
		Iterator it = resources.keySet().iterator();
		while (it.hasNext()) {
			Object resource = resources.get(it.next());
			if (resource instanceof Font)
				((Font) resource).dispose();
			else if (resource instanceof Color)
				((Color) resource).dispose();
			else if (resource instanceof Image)
				((Image) resource).dispose();
			else if (resource instanceof Cursor)
				((Cursor) resource).dispose();
		}
		resources.clear();

	}

}