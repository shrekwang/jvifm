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

import java.io.*;
import java.util.*;
import org.apache.commons.io.*;

public class FileComprator {
	
	public static Comparator<File> getFileComprator(String field,boolean isReverse) {
		if (field.equals("size")) {
			return new CompratorBySize(isReverse);
		} else if (field.equals("name")) {
			return new CompratorByName(isReverse);
		} else if (field.equals("date")) {
			return new CompratorByLastModified(isReverse);
		} else if (field.equals("ext")) {
			return new CompratorByExt(isReverse);
		} 
		return null;
	}
}

    class CompratorByLastModified implements Comparator<File> {
    	private boolean isReverse=false;
    	
    	public CompratorByLastModified(boolean isReverse) {
    		this.isReverse=isReverse;
    	}

       public int compare(File file1, File file2) {

           long diff = file1.lastModified() - file2.lastModified();
           int result;

           if (diff >= 0)
              result= 1;
           else
              result= -1;
           if (isReverse) return 0-result; 
           return result;

       }
       public boolean equals(Object obj){
           return true;  
       }

    }


   class CompratorBySize implements Comparator<File> {
	   private boolean isReverse=false;
   	
	   	public CompratorBySize(boolean isReverse) {
	   		this.isReverse=isReverse;
	   	}
       public int compare(File file1, File file2) {
           long diff = file1.length() - file2.length();
           int result;

           if (diff >= 0)
              result= 1;
           else
              result= -1;
           if (isReverse) return 0-result; 
           return result;

       }
      public boolean equals(Object obj){
           return true;  
       }

    }
    class CompratorByName implements Comparator<File> {
    	 private boolean isReverse=false;
    	   	
 	   	public CompratorByName(boolean isReverse) {
 	   		this.isReverse=isReverse;
 	   	}

       public int compare(File file1, File file2) {
           boolean file1IsFile=file1.isDirectory() ? false : true;
           boolean file2IsFile=file2.isDirectory() ? false : true;
           
           if (file1IsFile && ! file2IsFile)  {
        	   if (isReverse) return -1;
        	   return 1;
           }
           if (!file1IsFile && file2IsFile)  {
        	   if (isReverse) return 1;
        	   return -1;
           }
           if (isReverse)  return file2.getName().compareTo(file1.getName());
    	   return file1.getName().compareTo(file2.getName());
           

       }
       public boolean equals(Object obj){
           return true;  
       }

    }
    class CompratorByExt implements Comparator<File> {
    	 private boolean isReverse=false;
 	   	
  	   	public  CompratorByExt(boolean isReverse) {
  	   		this.isReverse=isReverse;
  	   	}
    	 public int compare(File file1, File file2) {
             String file1Ext=FilenameUtils.getExtension(file1.getName());
             String file2Ext=FilenameUtils.getExtension(file2.getName());
             if (file1.isFile() && file2.isDirectory()) {
            	 if (isReverse) return -1;
            	 return 1;
             }
             if (file1.isDirectory() && file2.isFile()) {
            	 if (isReverse) return 1;
            	 return -1;
             }
             int result = 0;
             if (isReverse) {
            	 result = file2Ext.compareTo(file1Ext);
             } else {
          	   result= file1Ext.compareTo(file2Ext);
             }
             if (result == 0) result =1 ;
             return result;
         }
         public boolean equals(Object obj){
             return true;  
         }
    }
