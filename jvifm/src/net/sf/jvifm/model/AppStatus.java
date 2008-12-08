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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;

import net.sf.jvifm.Main;
import net.sf.jvifm.util.HomeLocator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class AppStatus {
	
	private static String appStatusPath = HomeLocator.getConfigHome()+ File.separator + "appStatus.xml";
	
	
	
	@SuppressWarnings("unchecked")
	public static  String[][] loadAppStatus() {
		File file=new File(appStatusPath);
		if (!file.exists()) return null;
		
		String[][] result=null;
		try {
				FileInputStream is = new FileInputStream(file);
        		SAXReader saxReader = new SAXReader();
				InputStreamReader reader = new InputStreamReader(is, "UTF-8");
				BufferedReader br = new BufferedReader(reader);
				Document document = saxReader.read(br);
				List elementList = document.selectNodes("//tab");
				result=new String[elementList.size()][2];

				for (int i=0; i<elementList.size(); i++) {

					Element tabElement = (Element) elementList.get(i);
					result[i][0]=tabElement.attributeValue("left");
					result[i][1]=tabElement.attributeValue("right");
				}

				reader.close();
				is.close();
		} catch (Exception e ) {
			e.printStackTrace();
			
		}
		return result;
		
	}
	
	public static boolean writeAppStatus() {
		
		try {
			
        	String[][] currentPath=Main.fileManager.getAllCurrentPath();
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("tabs");
			
			for (int i=0; i<currentPath.length; i++) {
				Element tabElement=root.addElement("tab");
				tabElement.addAttribute("left", currentPath[i][0]);
				tabElement.addAttribute("right", currentPath[i][1]);
			}
			
			FileOutputStream fos = new FileOutputStream(appStatusPath);
			OutputFormat outformat = OutputFormat.createPrettyPrint();
			outformat.setEncoding("UTF-8");
			BufferedOutputStream out = new BufferedOutputStream(fos);
			XMLWriter writer = new XMLWriter(out, outformat);
			writer.write(document);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}
	
}


