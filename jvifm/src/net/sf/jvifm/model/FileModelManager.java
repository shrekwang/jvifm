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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.bzip2.CBZip2OutputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.apache.tools.tar.TarOutputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class FileModelManager {
	
	private byte[] buffer = new byte[1024 * 8];
	private String prefix = "";
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
		notifyAddFile(file);
		return true;
	}

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
			if( ! dstFile.isFile()) dstFile = new File(destPath, srcFile.getName());
			copyFile(srcFile, dstFile);
		} else {
			dstFile = new File(dstFile.getPath(), srcFile.getName());
			dstFile.mkdirs();
			copyDirectory(srcFile, dstFile);
		}
		notifyAddFile(dstFile);
	}
	
	public void mv(String srcPath, String destPath) throws IOException {

		File srcFile = new File(srcPath);
		File dstFile = new File(destPath);
		
		String srcParent=srcFile.getParent();
		String srcFileName=srcFile.getName();
		
		if (srcFile.isFile()) {
			if (!dstFile.isFile()) dstFile=new File(destPath,srcFileName);
			moveFile(srcFile, dstFile);
		} else {
			dstFile = new File(dstFile.getPath(), srcFileName);
			moveDirectory(srcFile, dstFile);
		}
		notifyRemoveFile(srcParent, srcFileName);
		notifyAddFile(dstFile);

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void unzip(String zipFilePath, String dstPath) throws Exception {
		File file=new File(zipFilePath);
		ZipFile zipFile = new ZipFile(file);
		ZipEntry entry = null;
		Enumeration e = zipFile.getEntries();
		while (e.hasMoreElements() ) {
			entry = (org.apache.tools.zip.ZipEntry) e.nextElement();
			String dstEntryPath = dstPath + File.separator + entry.getName();

			File dstEntryFile = new File(dstEntryPath);

			if (!entry.isDirectory()) {
				File dstParentDir = dstEntryFile.getParentFile();
				if (!dstParentDir.exists())
					mkdir(dstParentDir.getPath());
				extractEntry(zipFile.getInputStream(entry), dstEntryFile);
			} else {
				mkdir(dstEntryFile.getPath());
			}
		}
		zipFile.close();
	}
	
	public void untar(String tarFilePath, String dstPath) throws IOException {

		InputStream in = null;
		String ext = FilenameUtils.getExtension(tarFilePath);
		if (ext.equals("gz") || ext.equals("tgz")) {
			in = new GZIPInputStream(new FileInputStream(new File(tarFilePath)));
		} else if (ext.equals("bz2")) {

			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(new File(tarFilePath)));
			int b = bis.read();
			if (b != 'B') {
				throw new IOException("Invalid bz2 file.");
			}
			b = bis.read();
			if (b != 'Z') {
				throw new IOException("Invalid bz2 file.");
			}
			in = new CBZip2InputStream(bis);

		} else {
			in = new FileInputStream(new File(tarFilePath));
		}

		TarInputStream tin = new TarInputStream(in);
		TarEntry tarEntry = tin.getNextEntry();
		File file = new File(dstPath);
		if (!file.exists())
			mkdir(file.getPath());

		while (tarEntry != null ) {
			File entryPath = new File(dstPath + File.separatorChar
					+ tarEntry.getName());
			File parent = entryPath.getParentFile();
			if (!parent.exists())
				mkdir(parent.getPath());
			if (!tarEntry.isDirectory()) {

				FileOutputStream fout = new FileOutputStream(entryPath);
				tin.copyEntryContents(fout);
				fout.close();
			} else {
				entryPath.mkdir();
			}
			tarEntry = tin.getNextEntry();
		}
		tin.close();
	}

	public void tar(String filename, String[] paths, String compressMethod)
			throws Exception {
		File tarFile=new File(filename);
		FileOutputStream fo = new FileOutputStream(tarFile);
		TarOutputStream to = null;
		if (compressMethod == null) {
			to = new TarOutputStream(fo);
		} else if (compressMethod.equals("gz")) {
			to = new TarOutputStream(new GZIPOutputStream(fo));
		} else if (compressMethod.equals("bz2")) {
			fo.write('B');
			fo.write('Z');
			to = new TarOutputStream(new CBZip2OutputStream(fo));

		}

		for (int i = 0; i < paths.length; i++) {
			File file = new File(paths[i]).getAbsoluteFile();
			prefix = file.getParent();
			if (!prefix.endsWith(File.separator))
				prefix = prefix + File.separator;
			doTar(to, file);
		}
		to.close();
		notifyAddFile(tarFile);
	}
	
	public void zip(String filename, String[] paths) throws Exception {
		File zipFile=new File(filename);
		ZipOutputStream zo = new ZipOutputStream(new FileOutputStream(zipFile));
		for (int i = 0; i < paths.length; i++) {
			File file = new File(paths[i]).getAbsoluteFile();
			prefix = file.getParent();
			if (!prefix.endsWith(File.separator))
				prefix = prefix + File.separator;
			doZip(zo, file);
		}
		zo.close();
		notifyAddFile(zipFile);
	}


	/*****************************************************************************************/
	/***************************   private section   *****************************************/
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
			try { if (input != null) input.close(); } catch (Exception e) { }
			try { if (output != null) output.close(); } catch (Exception e) { }
		}
	}

	private void copyDirectory(File srcDir, File dstDir) throws IOException {

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
	
	private void moveDirectory(File srcDir, File destDir) throws IOException {
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

	private void moveFile(File srcFile, File destFile) throws IOException {

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

	private void extractEntry(InputStream zi, File file) throws Exception {
		BufferedOutputStream bf = new BufferedOutputStream(
				new FileOutputStream(file));
		while (true) {
			int n = zi.read(buffer);
			if (n < 0)
				break;
			bf.write(buffer, 0, n);
		}
		bf.close();
	}


	private void doTar(TarOutputStream to, File file) throws Exception {

		if (file.isDirectory()) {
			File[] subFiles = file.listFiles();
			for (int i = 0; i < subFiles.length; i++) {
				if (subFiles[i].isDirectory()) {
					doTar(to, subFiles[i]);
				} else {
					putEntry(to, subFiles[i]);
				}
			}
		} else {
			putEntry(to, file);
		}

	}

	private void putEntry(TarOutputStream to, File file) throws Exception {

		String name = file.getPath().substring(prefix.length());

		TarEntry entry = new TarEntry(file);
		entry.setName(name);

		to.putNextEntry(entry);
		BufferedInputStream bi = new BufferedInputStream(new FileInputStream(
				file));
		while (true) {
			int n = bi.read(buffer);
			if (n < 0)
				break;
			to.write(buffer, 0, n);
		}
		to.closeEntry();
		bi.close();

	}

	

	private void doZip(ZipOutputStream zo, File file) throws Exception {

		if (file.isDirectory()) {
			File[] subFiles = file.listFiles();
			for (int i = 0; i < subFiles.length; i++) {
				if (subFiles[i].isDirectory()) {
					doZip(zo, subFiles[i]);
				} else {
					putEntry(zo, subFiles[i]);
				}
			}
		} else {
			putEntry(zo, file);
		}

	}

	private void putEntry(ZipOutputStream zo, File file) throws Exception {

		String name = file.getPath().substring(prefix.length());

		ZipEntry entry = new ZipEntry(name);
		zo.putNextEntry(entry);
		BufferedInputStream bi = new BufferedInputStream(new FileInputStream(
				file));
		while (true) {
			int n = bi.read(buffer);
			if (n < 0)
				break;
			zo.write(buffer, 0, n);
		}
		zo.closeEntry();
		bi.close();

	}

}
