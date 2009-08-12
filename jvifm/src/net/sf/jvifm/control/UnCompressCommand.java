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
package net.sf.jvifm.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;

import net.sf.jvifm.ui.Util;

import org.apache.commons.io.FilenameUtils;
import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class UnCompressCommand extends InterruptableCommand {
	private byte[] buffer = new byte[1024 * 8];
	private String archiveFilePath;
	private String dstPath;

	public UnCompressCommand(String archiveFilePath, String dstPath) {
		this.archiveFilePath = archiveFilePath;
		this.dstPath = dstPath;
	}

	public void execute() {
		String ext = FilenameUtils.getExtension(archiveFilePath);

		try {
			if (ext.equals("zip") || ext.equals("jar") || ext.equals("war")
					|| ext.equals("ear")) {

				unzip(archiveFilePath, dstPath);

			} else if (ext.equals("tar") || ext.equals("tgz")
					|| archiveFilePath.endsWith("tar.bz2")
					|| archiveFilePath.endsWith("tar.gz")) {
				untar(archiveFilePath, dstPath);

			} else {
				Util.openMessageWindow("unknow archive format");
				return;
			}

		} catch (Exception e) {
			Util.openMessageWindow(e.getMessage());
			e.printStackTrace();
		}

		File dstDir = new File(dstPath);
		String parent = dstDir.getParent();
		addToPanel(parent, new String[] { dstPath });

	}

	@SuppressWarnings("unchecked")
	public void unzip(String zipFilePath, String dstPath) throws Exception {
		ZipFile zipFile = new ZipFile(new File(zipFilePath));
		ZipEntry entry = null;
		Enumeration e = zipFile.getEntries();
		while (e.hasMoreElements() && !aborted) {
			entry = (org.apache.tools.zip.ZipEntry) e.nextElement();
			String dstEntryPath = dstPath + File.separator + entry.getName();

			updateStatusInfo("uncompressing " + entry.getName());

			File dstEntryFile = new File(dstEntryPath);

			if (!entry.isDirectory()) {
				File dstParentDir = dstEntryFile.getParentFile();
				if (!dstParentDir.exists())
					dstParentDir.mkdirs();
				extractEntry(zipFile.getInputStream(entry), dstEntryFile);
			} else {
				dstEntryFile.mkdirs();
			}
		}
		zipFile.close();
	}

	private void extractEntry(InputStream zi, File file) throws Exception {
		BufferedOutputStream bf = new BufferedOutputStream(
				new FileOutputStream(file));
		while (!aborted) {
			int n = zi.read(buffer);
			if (n < 0)
				break;
			bf.write(buffer, 0, n);
		}
		bf.close();
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
			file.mkdirs();

		while (tarEntry != null && !aborted) {
			File entryPath = new File(dstPath + File.separatorChar
					+ tarEntry.getName());
			updateStatusInfo("uncompressing " + tarEntry.getName());
			File parent = entryPath.getParentFile();
			if (!parent.exists())
				parent.mkdirs();
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
}
