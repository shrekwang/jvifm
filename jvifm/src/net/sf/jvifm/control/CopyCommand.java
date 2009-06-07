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

import static net.sf.jvifm.ui.Messages.msgCpConfirmDlgTitle;
import static net.sf.jvifm.ui.Messages.msgFileReplace;
import static net.sf.jvifm.ui.Messages.msgFolderReplace;
import static net.sf.jvifm.ui.Messages.msgOptionCancel;
import static net.sf.jvifm.ui.Messages.msgOptionNo;
import static net.sf.jvifm.ui.Messages.msgOptionNoToAll;
import static net.sf.jvifm.ui.Messages.msgOptionYes;
import static net.sf.jvifm.ui.Messages.msgOptionYesToAll;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sf.jvifm.ui.Util;
import net.sf.jvifm.ui.shell.OptionShell;
import net.sf.jvifm.util.FileOperator;
import net.sf.jvifm.util.StringUtil;

import org.apache.commons.io.FilenameUtils;

public class CopyCommand extends InterruptableCommand {

	private String strValue = "";
	
	private String[] options = new String[] {
        msgOptionYes, 
        msgOptionNo, 
        msgOptionYesToAll, 
        msgOptionNoToAll, 
        msgOptionCancel } ;

	private boolean yesToAll = false;
	private boolean noToAll = false;

	public CopyCommand(String srcDir, String dstDir, String[] files) {
		this.srcDir = srcDir;
		this.dstDir = dstDir;
		this.files = files;
	}

	protected void doFileOperator(String src, String dst, String fileName)
			throws Exception {
		String baseName = FilenameUtils.getName(src);
		boolean isSame = false;

		updateStatusInfo("copying file " + baseName);

		// if is same file, make a copy
		if (FileOperator.isSameFile(src, dst)) {
			dst = FilenameUtils.concat(dst, FilenameUtils.getBaseName(src)
					+ "(1)." + FilenameUtils.getExtension(src));
			new File(dst).createNewFile();
			isSame = true;
		}
		cp(src, dst);
		addToPanel(dstDir, new String[] { fileName });
		if (isSame)
			addToPanel(dstDir, new String[] { dst });
	}

	public void execute() throws Exception {

		if (files == null || files.length <= 0)
			return;

		for (int i = 0; i < files.length; i++) {

			String src = FilenameUtils.concat(srcDir, files[i]);
			strValue = "";
			if (FileOperator.isDestFileExisted(src, dstDir)) {
				if (yesToAll) {
					doFileOperator(src, dstDir, files[i]);
				} else if (noToAll) {
					continue;
				} else {
					
					File srcFile=new File(src) ;
					File dstFile=new File(FilenameUtils.concat(dstDir, files[i]));
					String srcSize=StringUtil.formatSize(srcFile.length());
					String srcDate=StringUtil.formatDate(srcFile.lastModified());
					String dstSize=StringUtil.formatSize(dstFile.length());
					String dstDate=StringUtil.formatDate(dstFile.lastModified());
					
					
					String msg="";
					if (srcFile.isDirectory()) {
						msg=msgFolderReplace;
					} else {
						msg=msgFileReplace;
					}
					
					msg=msg.replaceAll("\\$name", srcFile.getName());
					msg=msg.replaceAll("\\$srcSize", srcSize);
					msg=msg.replaceAll("\\$srcDate", srcDate);
					msg=msg.replaceAll("\\$dstSize", dstSize);
					msg=msg.replaceAll("\\$dstDate", dstDate);
					strValue = new Util().openConfirmWindow(options, msgCpConfirmDlgTitle,  msg, OptionShell.WARN);
					
					
					if (strValue == null || strValue.equals(msgOptionCancel)) return;
					
					if (strValue.equals(msgOptionYesToAll)) {
						yesToAll = true;
						doFileOperator(src, dstDir, files[i]);
					}
					
					if (strValue.equals(msgOptionNoToAll))  noToAll = true;
					if (strValue.equals(msgOptionNo))  continue;
					if (strValue.equals(msgOptionYes))  doFileOperator(src, dstDir, files[i]);
				}
			} else {
				doFileOperator(src, dstDir, files[i]);
			}
		}

	}

	public void cp(String srcPath, String destPath) throws IOException {

		File srcFile = new File(srcPath);
		File dstFile = new File(destPath);
		if (srcFile == null)
			throw new NullPointerException("source file  is null");
		if (dstFile == null)
			throw new NullPointerException("dest file is null");

		if (srcFile.isFile() && dstFile.isFile())
			copyFile(srcFile, dstFile);

		if (srcFile.isFile() && dstFile.isDirectory())
			copyFile(srcFile, new File(dstFile, srcFile.getName()));

		if (srcFile.isDirectory() && dstFile.isDirectory()) {
			File newDstFile = new File(FilenameUtils.concat(dstFile.getPath(),
					srcFile.getName()));
			newDstFile.mkdirs();
			copyDirectory(srcFile, newDstFile);
		}

	}

	public void copyFile(File srcFile, File dstFile) throws IOException {
		File dstParent = dstFile.getParentFile();
		if (!dstParent.exists())
			dstParent.mkdirs();

		FileInputStream input = new FileInputStream(srcFile);
		FileOutputStream output = new FileOutputStream(dstFile);
		try {
			byte[] buffer = new byte[1024 * 16];
			int n = 0;
			while (-1 != (n = input.read(buffer)) && !aborted) {
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
			if (this.aborted)
				return;
			File copiedFile = new File(dstDir, files[i].getName());
			if (files[i].isDirectory()) {
				copiedFile.mkdirs();
				copyDirectory(files[i], copiedFile);
			} else {
				copyFile(files[i], copiedFile);
			}
		}
	}

}
