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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class FileModelManager {
	
	private static FileModelManager instance=null;
	private ArrayList<FileModelListener> listeners = new ArrayList<FileModelListener>();
	
	private FileModelManager() { }

	public void addListener(FileModelListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(FileModelListener listener) {
		this.listeners.remove(listener);
	}

	public void notifyAddFile(File file) {
		for (FileModelListener listener : listeners) {
			listener.onAdd(file);
		}
	}

	public void notifyRemoveFile(String parent, String name) {
		for (FileModelListener listener : listeners) {
			listener.onRemove(parent, name);
		}
	}

	public static FileModelManager getInstance() {
		if (instance == null)
			instance = new FileModelManager();
		return instance;
	}
	
	
	
	public void calcDirInfo(File file, long[] infos) {
		
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

	public void rename(String srcName, String destName, String path) {
		File file = new File(path);
		String parent = file.getParent();

		String newName = file.getName().replaceAll(srcName, destName);
		if (parent != null) {
			file.renameTo(new File(FilenameUtils.concat(parent, newName)));
		} else {
			file.renameTo(new File(newName));
		}
	}

	
	public void rm(String path) throws IOException {
		
		File dstFile = new File(path);
		if (!dstFile.exists()) return;
		String parent=dstFile.getParent();
		String filename=dstFile.getName();
		
		if (dstFile.isFile()) {
			dstFile.delete();
		}

		if (dstFile.isDirectory()) {
			FileUtils.deleteDirectory(dstFile);
		}
		
		notifyRemoveFile(parent,filename);

	}
	
	public boolean touch(String filepath, long lastModified) {
		File file = new File(filepath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			file.setLastModified(lastModified);
		}
		return true;
	}


	public String getFileName(String fullPath) {
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
	public boolean isWildCardFileName(String name) {
		if (name.indexOf("*") > -1 || name.indexOf("?") > -1)
			return true;
		return false;
	}

	/**
	 * create a new empty file or set the last modified time
	 */
	

	public boolean mkdir(String path) {
		File file = new File(path);
		if (!file.exists()) {
			boolean success= file.mkdirs();
			if (success) notifyAddFile(file);
			return success;
		}
		return true;
	}
	
	public boolean isSameFile(String src,String dstDir) {

		File srcFile = new File(src);
		File dstFile = new File(dstDir);
		
		if (srcFile.isFile() && dstFile.isDirectory()) {
			File tmp = new File(FilenameUtils.concat(dstFile.getPath(), srcFile.getName()));
			if (srcFile.getAbsolutePath().equals(tmp.getAbsolutePath())) return true;
		}
		return false;
	}

	public boolean isDestFileExisted(String src, String dst) {
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
	
	public void cp(String srcPath, String destPath) throws IOException {

		File srcFile = new File(srcPath);
		File dstFile = new File(destPath);

		if (srcFile.isFile()) {
			if( ! dstFile.isFile()) dstFile = new File(dstFile, srcFile.getName());
			copyFile(srcFile, dstFile);
		} else {
			dstFile = new File(dstFile.getPath(), srcFile.getName());
			dstFile.mkdirs();
			copyDirectory(srcFile, dstFile);
		}
		notifyAddFile(dstFile);
	}

	/*****************************************************************************************/
	/*****   private section   **************************************************************/
	/*****************************************************************************************/

	private void copyFile(File srcFile, File dstFile) throws IOException {
		File dstParent = dstFile.getParentFile();
		if (!dstParent.exists())
			dstParent.mkdirs();

		FileInputStream input = new FileInputStream(srcFile);
		FileOutputStream output = new FileOutputStream(dstFile);
		try {
			byte[] buffer = new byte[1024 * 16];
			int n = 0;
			while (-1 != (n = input.read(buffer)) ) {
				output.write(buffer, 0, n);
			}
			dstFile.setLastModified(srcFile.lastModified());
		} finally {
			try {
				if (input != null) input.close();
			} catch (Exception e) {
			}
			try {
				if (output != null) output.close();
			} catch (Exception e) {
			}
		}
	}

	public void copyDirectory(File srcDir, File dstDir) throws IOException {

		File[] files = srcDir.listFiles();
		if (files == null) { // null if security restricted
			throw new IOException("Failed to list contents of " + srcDir);
		}
		for (int i = 0; i < files.length; i++) {
			File copiedFile = new File(dstDir, files[i].getName());
			if (files[i].isDirectory()) {
				copiedFile.mkdirs();
				copyDirectory(files[i], copiedFile);
			} else {
				copyFile(files[i], copiedFile);
			}
		}
	}
	
	public void mv(String srcPath, String destPath) throws IOException {

		File srcFile = new File(srcPath);
		File dstFile = new File(destPath);
		
		if (srcFile.isFile() && dstFile.isFile())
			moveFile(srcFile, dstFile);
		if (srcFile.isFile() && dstFile.isDirectory())
			moveFileToDirectory(srcFile, dstFile, true);
		if (srcFile.isDirectory() && dstFile.isDirectory()) {
			moveDirectoryToDirectory(srcFile, dstFile, true);
		}

	}

	public void moveDirectoryToDirectory(File src, File destDir,
			boolean createDestDir) throws IOException {

		if (!destDir.exists() && createDestDir) {
			destDir.mkdirs();
		}
		if (!destDir.exists()) {
			throw new FileNotFoundException("Destination directory '" + destDir
					+ "' does not exist [createDestDir=" + createDestDir + "]");
		}
		if (!destDir.isDirectory()) {
			throw new IOException("Destination '" + destDir
					+ "' is not a directory");
		}
		moveDirectory(src, new File(destDir, src.getName()));

	}

	public void moveDirectory(File srcDir, File destDir) throws IOException {
		if (srcDir == null) {
			throw new NullPointerException("Source must not be null");
		}
		if (destDir == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (!srcDir.exists()) {
			throw new FileNotFoundException("Source '" + srcDir
					+ "' does not exist");
		}
		if (!srcDir.isDirectory()) {
			throw new IOException("Source '" + srcDir + "' is not a directory");
		}

		boolean rename = srcDir.renameTo(destDir);
		if (!rename) {
			copyDirectory(srcDir, destDir);
			FileUtils.deleteDirectory(srcDir);
			if (srcDir.exists()) {
				throw new IOException("Failed to delete original directory '"
						+ srcDir + "' after copy to '" + destDir + "'");
			}
		}
	}

	public void moveFile(File srcFile, File destFile) throws IOException {

		boolean rename = srcFile.renameTo(destFile);
		if (!rename) {
			copyFile(srcFile, destFile);
			if (!srcFile.delete()) {
				FileUtils.deleteQuietly(destFile);
				throw new IOException("Failed to delete original file '"
						+ srcFile + "' after copy to '" + destFile + "'");
			}
		}
	}

	public void moveFileToDirectory(File srcFile, File destDir,
			boolean createDestDir) throws IOException {

		if (!destDir.exists() && createDestDir) {
			destDir.mkdirs();
		}
		moveFile(srcFile, new File(destDir, srcFile.getName()));
	}


}
