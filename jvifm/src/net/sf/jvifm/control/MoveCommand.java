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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;


public class MoveCommand extends CopyCommand{

	public MoveCommand(String srcDir, String dstDir, String[] files) {
		super(srcDir, dstDir, files);
	}
	protected void doFileOperator(String src, String dst,String fileName) throws Exception {
		String baseName=FilenameUtils.getName(src);
		updateStatusInfo("moving file "+baseName);
		mv(src, dst);
		addToPanel(dstDir,new String[]{fileName});
		removeFromInActivePanel(srcDir,new String[]{fileName});
	}

	
	public void mv(String srcPath, String destPath)  throws IOException  {
		
		File srcFile = new File(srcPath);
		File dstFile = new File(destPath);
		if (srcFile==null)  throw new NullPointerException("source file  is null");
		if (dstFile==null)  throw new NullPointerException("dest file is null");
		
		if (srcFile.isFile() && dstFile.isFile()) moveFile(srcFile, dstFile);
		if (srcFile.isFile() && dstFile.isDirectory()) moveFileToDirectory(srcFile, dstFile,true);
		if (srcFile.isDirectory() && dstFile.isDirectory())  {
			moveDirectoryToDirectory(srcFile, dstFile,true);
		};
		

	}
	
	  public void moveDirectoryToDirectory(File src, File destDir, boolean createDestDir) throws IOException {
	       
	        if (!destDir.exists() && createDestDir) {
	            destDir.mkdirs();
	        }
	        if (!destDir.exists()) {
	            throw new FileNotFoundException("Destination directory '" + destDir +
	                    "' does not exist [createDestDir=" + createDestDir +"]");
	        }
	        if (!destDir.isDirectory()) {
	            throw new IOException("Destination '" + destDir + "' is not a directory");
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
	            throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
	        }
	        if (!srcDir.isDirectory()) {
	            throw new IOException("Source '" + srcDir + "' is not a directory");
	        }
	       
	        boolean rename = srcDir.renameTo(destDir);
	        if (!rename) {
	            copyDirectory( srcDir, destDir );
	            FileUtils.deleteDirectory( srcDir );
	            if (srcDir.exists()) {
	                throw new IOException("Failed to delete original directory '" + srcDir +
	                        "' after copy to '" + destDir + "'");
	            }
	        }
	    }


	 
	
	   public void moveFile(File srcFile, File destFile) throws IOException {
		   
	        boolean rename = srcFile.renameTo(destFile);
	        if (!rename) {
	            copyFile( srcFile, destFile );
	            if (!srcFile.delete()) {
	                FileUtils.deleteQuietly(destFile);
	                throw new IOException("Failed to delete original file '" + srcFile +
	                        "' after copy to '" + destFile + "'");
	            }
	        }
	    }
	   
	   public void moveFileToDirectory(File srcFile, File destDir, boolean createDestDir) throws IOException {
		   
	        if (!destDir.exists() && createDestDir) {
	            destDir.mkdirs();
	        }
	        moveFile(srcFile, new File(destDir, srcFile.getName()));
	    }

}
