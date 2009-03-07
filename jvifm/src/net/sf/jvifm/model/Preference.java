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
package net.sf.jvifm.model;

import java.io.*;
import java.util.*;

import net.sf.jvifm.util.*;

public class Preference {

	public static final String EDITOR = "editor";

	public static final String TERMINAL = "terminal";

	public static final String ISSHOWHIDE = "isShowHide";

	private static Preference preference = null;

	private static Properties properties = new Properties();

	private static String RC_FILE = HomeLocator.getConfigHome()
			+ File.separator + "jvifmrc";

	private Preference() {
	}

	public static Preference getInstance() {

		if (preference == null) {
			File file = new File(RC_FILE);
			if (file.exists()) {
				try {
					BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
					properties.load(bis);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			preference = new Preference();
		}
		return preference;

	}

	public void setEditorApp(String name) {
		properties.setProperty(EDITOR, name);
	}

	public void setTerminalApp(String name) {
		properties.setProperty(TERMINAL, name);
	}

	public void setShowHide(boolean isShowHide) {
		if (isShowHide) {
			properties.setProperty(ISSHOWHIDE, "true");
		} else {
			properties.setProperty(ISSHOWHIDE, "false");
		}
	}

	public String getEditorApp() {
		String app = properties.getProperty(EDITOR);
		if (app == null || app.equals(""))
			return "gvim";
		return app;
	}

	public String getTerminalApp() {
		String app = properties.getProperty(TERMINAL);
		if (app == null || app.trim().equals(""))
			return "gnome-terminial";
		return app;
	}

	public boolean isShowHide() {
		String isShowHide = properties.getProperty(ISSHOWHIDE);
		if (isShowHide == null || isShowHide.equals("") || isShowHide.equals("false"))
			return false;
		return true;
	}

	public void save() {
		try {
			BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream(RC_FILE));
			properties.store(bos, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
