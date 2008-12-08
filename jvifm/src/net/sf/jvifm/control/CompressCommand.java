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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;

import net.sf.jvifm.ui.Util;

import org.apache.commons.io.FilenameUtils;
import org.apache.tools.bzip2.CBZip2OutputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

public class CompressCommand extends Command {
	private String dstFile;
	private String[] paths;
	
	private byte[] buffer =new byte[1024*8];
	private String prefix="";
	
	public CompressCommand(String dstFile,String[] paths) {
		this.dstFile=dstFile;
		this.paths=paths;
	}
	
	public void execute() {
		
		String ext = FilenameUtils.getExtension(dstFile);

		try {
			if (ext.equals("zip") || ext.equals("jar") || ext.equals("war")
					|| ext.equals("ear")) {
				zip(dstFile,paths);

			} else if (ext.equals("tar") ) {
				tar(dstFile,paths,null);
			} else if ( ext.equals("tgz")  || dstFile.endsWith("tar.gz")) {
				tar(dstFile,paths,"gz");
				
			} else if ( dstFile.endsWith("tar.bz2") ) {
				tar(dstFile,paths,"bz2");

			} else {
				Util.openMessageWindow("unknow archive format");
				return;
			}
		}catch (Exception e ) {
			Util.openMessageWindow(e.getMessage());
		}
		
		String parentPath=new File(dstFile).getParent();
		addToPanel(parentPath,new String[]{dstFile});
		switchToNormal();
		
	}
	
	public void tar(String filename, String[] paths,String compressMethod) throws Exception {
		FileOutputStream fo=new FileOutputStream(new File(filename));
		TarOutputStream to=null;
		if (compressMethod==null) {
    		to=new TarOutputStream(fo);
		} else if (compressMethod.equals("gz")) {
    		to=new TarOutputStream(new GZIPOutputStream(fo));
		} else if (compressMethod.equals("bz2")) {
            fo.write('B');
            fo.write('Z');
			to=new TarOutputStream(new CBZip2OutputStream(fo));
			
		}
		
		for (int i=0; i<paths.length; i++) {
			File file=new File(paths[i]).getAbsoluteFile();
			prefix=file.getParent();
			if (!prefix.endsWith(File.separator)) prefix=prefix+File.separator;
			doTar(to,file);
		}
		to.close();

	}

	
	private void doTar(TarOutputStream to, File file) throws Exception {

		if (file.isDirectory()) {
			File[] subFiles=file.listFiles();
			for (int i=0; i<subFiles.length; i++) {
				if (subFiles[i].isDirectory()) {
					doTar(to,subFiles[i]);
				} else {
					putEntry(to,subFiles[i]);
				}
			}
		} else {
			putEntry(to,file);
		}


	}
	private void putEntry(TarOutputStream to, File file) throws Exception { 
		
		String name=file.getPath().substring(prefix.length());

		TarEntry entry=new TarEntry(file);
		entry.setName(name);
		
		to.putNextEntry(entry);
		BufferedInputStream bi=new BufferedInputStream(new FileInputStream(file));
		while(true) {
			int n=bi.read(buffer);
			if (n<0) break;
			to.write(buffer,0,n);
		}
		to.closeEntry();
		bi.close();

	}

	
	@SuppressWarnings("unchecked")
	
	public  void zip(String filename, String[] paths) throws Exception {
		ZipOutputStream zo=new ZipOutputStream(new FileOutputStream(new File(filename)));
		for (int i=0; i<paths.length; i++) {
			File file=new File(paths[i]).getAbsoluteFile();
			prefix=file.getParent();
			if (!prefix.endsWith(File.separator)) prefix=prefix+File.separator;
			doZip(zo,file);
		}
		zo.close();

	}

	
	

	
	private void doZip(ZipOutputStream zo, File file) throws Exception {

		if (file.isDirectory()) {
			File[] subFiles=file.listFiles();
			for (int i=0; i<subFiles.length; i++) {
				if (subFiles[i].isDirectory()) {
					doZip(zo,subFiles[i]);
				} else {
					putEntry(zo,subFiles[i]);
				}
			}
		} else {
			putEntry(zo,file);
		}


	}
	private void putEntry(ZipOutputStream zo, File file) throws Exception { 
		
		String name=file.getPath().substring(prefix.length());

		ZipEntry entry=new ZipEntry(name);
		zo.putNextEntry(entry);
		BufferedInputStream bi=new BufferedInputStream(new FileInputStream(file));
		while(true) {
			int n=bi.read(buffer);
			if (n<0) break;
			zo.write(buffer,0,n);
		}
		zo.closeEntry();
		bi.close();

	}


}


