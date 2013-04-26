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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.jvifm.control.CommandRegister;
import net.sf.jvifm.model.Bookmark;
import net.sf.jvifm.model.BookmarkManager;
import net.sf.jvifm.model.Shortcut;
import net.sf.jvifm.model.ShortcutsManager;
import net.sf.jvifm.ui.Util;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;

public class AutoCompleteUtil {
	
	private static List<String> sysExeNameList = null;
	
	public  static List<String> getSysExeNameList() {
		if (sysExeNameList != null) return sysExeNameList;
		sysExeNameList = new ArrayList<String>();
		
		String pathStr = System.getenv("PATH");
		String[] paths = pathStr.split(";");
		for (String path : paths) {
			File file = new File(path);
			if (!file.exists()) continue;
			File[] subFiles = file.listFiles();
			if (subFiles == null) continue;
			for (File subFile : subFiles) {
				String ext = FilenameUtils.getExtension(subFile.getPath());
				if (isExecuteFile(ext)) {
					sysExeNameList.add(subFile.getAbsolutePath());
				}
			}
		}
		return sysExeNameList;
		
	}

	public static List<File> getFileCompleteList(String pwd, String path,
			boolean onlyDir) {
		List<File> list = new ArrayList<File>();
		String fileName = new File(path).getName();

		if (path.endsWith(":")) return null;

		File file = new File(FilenameUtils.concat(pwd, path));
		

		File pwdFile = null;
		pwdFile = file;
		if (path.endsWith(File.separator) || path.trim().equals("")) {
			pwdFile = file;
		} else {
			if (file.getParent() == null)
				return null;
			pwdFile = new File(file.getParent());
		}
		if (!pwdFile.exists())
			return null;
		File[] files = pwdFile.listFiles((FileFilter)Util.getDefaultFileFilter());

		if (files == null || files.length <= 0)
			return null;
		boolean lowerCase = false;
		if (fileName.toLowerCase().equals(fileName)) {
			lowerCase = true;
		}
		for (int i = 0; i < files.length; i++) {
			String tmpFileName = files[i].getName();
			if (lowerCase) tmpFileName = tmpFileName.toLowerCase();
			if (tmpFileName.startsWith(fileName)
					|| path.endsWith(File.separator)
					|| FilenameUtils.wildcardMatch(tmpFileName, fileName, IOCase.INSENSITIVE))
				if (onlyDir) {
					if (files[i].isDirectory()) list.add(files[i]);
				} else {
					list.add(files[i]);
				}
		}
		if (list.size() <= 0)
			return null;
		return list;
	}

	@SuppressWarnings("all")
	public static String[] getFileCompleteOptions(String pwd, String path,
			boolean onlyDir) {
		List list = getFileCompleteList(pwd, path, onlyDir);

		if (list == null)
			return null;
		String[] resultArray = new String[list.size()];
		for (int i = 0; i < resultArray.length; i++) {
			File file = (File) list.get(i);
			resultArray[i] = file.getAbsolutePath();
		}
		return resultArray;
	}

	public static String[] getExeFileCompleteOptions(String name) {
		List<String> nameList = new ArrayList<String>();

		List<String> sysExeNameList = getSysExeNameList();
		for (String exePath : sysExeNameList) {
			String exeName = FilenameUtils.getBaseName(exePath);
			String ext = FilenameUtils.getExtension(exePath);
			if (exeName.equals(name) && isExecuteFile(ext)) {
				nameList.add(exePath);
			}
		}
		
		if (nameList.size() == 0) return null;

		String[] result = new String[nameList.size()];
		for (int i = 0; i < nameList.size(); i++) {
			result[i] = nameList.get(i);
		}

		return result;
	}
	
	private static boolean isExecuteFile(String name) {
		return name.endsWith("exe") || name.endsWith("bat") || name.endsWith("com");
	}
	
	
	@SuppressWarnings("all")
	public static String[] getBookmarkFileOptions(String name) {

		ArrayList result = new ArrayList();

		BookmarkManager bookmarkManager = BookmarkManager.getInstance();

		boolean lowerCase = false;
		if (name.toLowerCase().equals(name)) {
			lowerCase = true;
		}
		List bookmarkList = bookmarkManager.getAll();
		if (bookmarkList != null) {
			for (Iterator it = bookmarkList.iterator(); it.hasNext();) {
				Bookmark bookmark = (Bookmark) it.next();
				String bookmarkName = bookmark.getName();
				if (lowerCase) {
					bookmarkName = bookmarkName.toLowerCase();
				}
				if ( FilenameUtils.wildcardMatch(bookmarkName, name, IOCase.INSENSITIVE)
						|| bookmarkName.startsWith(name)) {
					result.add(bookmark.getPath());
				}
			}
		}

		if (result.size() <= 0)
			return null;

		String[] resultArray = new String[result.size()];
		for (int i = 0; i < resultArray.length; i++) {
			resultArray[i] = (String) result.get(i);
		}
		return resultArray;
	}

	@SuppressWarnings("all")
	public static String getCommandOptionTip(String cmd) {
		Options options = CommandRegister.getInstance().getCommandOptions(cmd);
		if (options == null)
			return null;
		Collection ops = options.getOptions();
		StringBuffer sb = new StringBuffer();
		for (Iterator it = ops.iterator(); it.hasNext();) {
			String opt = ((Option) it.next()).getOpt();
			sb.append("-").append(opt).append("  ");
		}
		return sb.toString();
	}

	@SuppressWarnings("all")
	public static String[] getCommandCompleteList(String cmd) {
		ArrayList result = new ArrayList();
		String[] cmds = CommandRegister.getInstance().getCmdNames();

		for (int i = 0; i < cmds.length; i++) {
			if (cmds[i].startsWith(cmd))
				result.add(cmds[i]);
		}
		ShortcutsManager scm = ShortcutsManager.getInstance();

		ArrayList customCmdNameList = scm.getCmdNameList();

		if (customCmdNameList != null) {
			for (Iterator it = customCmdNameList.iterator(); it.hasNext();) {
				String cmdName = (String) it.next();
				if (cmdName.startsWith(cmd))
					result.add(cmdName);
			}
		}

		if (result.size() <= 0)
			return null;

		String[] resultArray = new String[result.size()];
		for (int i = 0; i < resultArray.length; i++) {
			resultArray[i] = (String) result.get(i);
		}
		return resultArray;
	}

	@SuppressWarnings("all")
	public static String[] getShortcutsCompleteList(String cmd) {
		ArrayList result = new ArrayList();

		ShortcutsManager scm = ShortcutsManager.getInstance();

		ArrayList customCmdNameList = scm.getCmdNameList();
		if (customCmdNameList != null) {
			for (Iterator it = customCmdNameList.iterator(); it.hasNext();) {
				String cmdName = (String) it.next();
				if (cmdName.toLowerCase().startsWith(cmd))
					result.add(cmdName);
			}
		}

		if (result.size() <= 0)
			return null;

		String[] resultArray = new String[result.size()];
		for (int i = 0; i < resultArray.length; i++) {
			resultArray[i] = (String) result.get(i);
		}
		return resultArray;
	}

	@SuppressWarnings("all")
	public static Shortcut[] getShortcutsCompleteList2(String cmd) {
		ArrayList result = new ArrayList();

		ShortcutsManager scm = ShortcutsManager.getInstance();

		boolean lowerCase = false;
		if (cmd.toLowerCase().equals(cmd)) {
			lowerCase = true;
		}
		ArrayList customCmdNameList = scm.getAll();
		if (customCmdNameList != null) {
			for (Iterator it = customCmdNameList.iterator(); it.hasNext();) {
				Shortcut shortcut = (Shortcut) it.next();
				String cmdName = shortcut.getName();
				if (lowerCase) {
					cmdName = cmdName.toLowerCase();
				}
				if ( FilenameUtils.wildcardMatch(cmdName, cmd, IOCase.INSENSITIVE)
						||  cmdName.startsWith(cmd) ) {
					result.add(shortcut);
				}
			}
		}

		if (result.size() <= 0)
			return null;

		Shortcut[] resultArray = new Shortcut[result.size()];
		for (int i = 0; i < resultArray.length; i++) {
			resultArray[i] = (Shortcut) result.get(i);
		}
		return resultArray;
	}
}
