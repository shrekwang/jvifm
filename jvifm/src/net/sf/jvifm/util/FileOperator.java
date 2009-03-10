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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class FileOperator {
	
	
	
	public static void calcDirInfo(File file, long[] infos) {
		
		if (file.isDirectory()) {
			infos[1]++;
			File[] files=file.listFiles();
			for (int i=0; i<files.length; i++) {
				calcDirInfo(files[i],infos);
			}
				
		} else if (file.isFile()) {
			infos[0]=infos[0]+file.length();
			infos[2]++;
		}
		
		
	}

	public static void rename(String srcName, String destName, String path) {
		File file = new File(path);
		String parent = file.getParent();

		String newName = file.getName().replaceAll(srcName, destName);
		if (parent != null) {
			file.renameTo(new File(FilenameUtils.concat(parent, newName)));
		} else {
			file.renameTo(new File(newName));
		}
	}

	
	public static void rm(String path) throws IOException {
		
		File dstFile = new File(path);
		if (dstFile.isFile()) {
			dstFile.delete();
		}

		if (dstFile.isDirectory()) {
			FileUtils.deleteDirectory(dstFile);
			
		}

	}


	public static String getFileName(String fullPath) {
		int index = fullPath.lastIndexOf(File.separator);
		if (index >= 0 && index < fullPath.length() - 1)
			return fullPath.substring(index + 1);
		return fullPath;
	}

	/**
	 * whether the file name is wildcard or not
	 * 
	 * @param name
	 *            filename
	 * 
	 */
	public static boolean isWildCardFileName(String name) {
		if (name.indexOf("*") > -1 || name.indexOf("?") > -1)
			return true;
		return false;
	}

	/**
	 * create a new empty file or set the last modified time
	 */
	

	public static boolean mkdir(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return file.mkdirs();
		}
		return true;
	}
	
	public static boolean isSameFile(String src,String dstDir) {

		File srcFile = new File(src);
		File dstFile = new File(dstDir);
		
		if (srcFile.isFile() && dstFile.isDirectory()) {
			File tmp = new File(FilenameUtils.concat(dstFile.getPath(), srcFile.getName()));
			if (srcFile.getAbsolutePath().equals(tmp.getAbsolutePath())) return true;
		}
		return false;
	}

	public static boolean isDestFileExisted(String src, String dst) {
		File srcFile = new File(src);
		File dstFile = new File(dst);

		if (srcFile.isFile()) {
			if (dstFile.isFile() && dstFile.exists()
					&& srcFile.getName().equals(dstFile.getName())
					&& !srcFile.getParent().equals(dstFile.getParent()))
				return true;

			if (dstFile.isDirectory()) {
				File tmp = new File(FilenameUtils.concat(dstFile.getPath(),
						srcFile.getName()));
				if (tmp.exists()
						&& !srcFile.getParent().equals(dstFile.getPath()))
					return true;
			}
			return false;
		}
		if (srcFile.isDirectory()) {
			if (dstFile.isDirectory()) {
				File tmp = new File(FilenameUtils.concat(dstFile.getPath(),
						srcFile.getName()));
				if (tmp.exists() && !src.equals(dst))
					return true;
			}
		}

		return false;
	}

}
