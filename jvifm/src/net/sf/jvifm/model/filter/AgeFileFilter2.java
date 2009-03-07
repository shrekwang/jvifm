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

package net.sf.jvifm.model.filter;


import java.io.File;
import java.io.Serializable;

import org.apache.commons.io.filefilter.AbstractFileFilter;

public class AgeFileFilter2 extends AbstractFileFilter implements Serializable {

	private static final long serialVersionUID = -3929906512173891655L;
	
	private final long cutoff;
	private final int flag;
    
    public static final int EQ=1;
    public static final int GT=2;
    public static final int LT=3;

  
    public AgeFileFilter2(long cutoff, int flag) {
        this.cutoff = cutoff;
        this.flag=flag;
    }

   
    public boolean accept(File file) {
    	long threshold=System.currentTimeMillis()-cutoff;
    	long modifiedTime=file.lastModified();
    	
    	if (flag==EQ) {
    		return (modifiedTime==threshold);
    		
    	} else if (flag==GT) {
    		return (modifiedTime<threshold);
    		
    	} else if (flag==LT) {
    		return (modifiedTime>threshold);
    	} else {
    		return false;
    	}
    	
    }

}
