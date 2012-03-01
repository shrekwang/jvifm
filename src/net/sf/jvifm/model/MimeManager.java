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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.jvifm.util.HomeLocator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class MimeManager {

	private static MimeManager instance = null;
	private String storePath = null;
	private HashMap<String, List<String>> mimeInfo = new HashMap<String,List<String>>();

	private MimeManager() {
		init();
	}

	public static MimeManager getInstance() {
		if (instance == null)
			instance = new MimeManager();
		return instance;
	}

    public List<String> get(String postfix) {
        List<String> appPaths = mimeInfo.get(postfix);
        return appPaths;
    }

	public void add(String postfix, String appPath) {
        List<String> appPaths = mimeInfo.get(postfix);
        if (appPaths == null) {
            appPaths = new ArrayList<String>();
            appPaths.add(appPath);
            mimeInfo.put(postfix,appPaths);
        } else {
            if (appPaths.contains(appPath)) return;
            appPaths.add(appPath);
        }

	}

	@SuppressWarnings("unchecked")
	private void init() {

		storePath = HomeLocator.getConfigHome() + File.separator + "mime.xml";
		SAXReader saxReader = new SAXReader();
		File file = new File(storePath);
		Document document = null;

		try {

			if (file.exists()) {
				FileInputStream is = new FileInputStream(file);
				InputStreamReader reader = new InputStreamReader(is, "UTF-8");
				BufferedReader br = new BufferedReader(reader);
				document = saxReader.read(br);

				List elementList = document.selectNodes("//filetype");

				for (Iterator it = elementList.iterator(); it.hasNext();) {

					Element filetypeEle = (Element) it.next();
					String postfix = filetypeEle.attributeValue("postfix");

                    List appPathEleList = filetypeEle.selectNodes("appPath");

                    List<String> appPaths = new ArrayList<String>();
                    for (Iterator it2 = appPathEleList.iterator(); it2.hasNext();) {
                        Element appPathEle = (Element)it2.next();
                        String appPath = appPathEle.getText();
                        appPaths.add(appPath);
                    }
                    mimeInfo.put(postfix, appPaths);
				}

				reader.close();
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public void store() {

		try {
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("mimes");
			for (Iterator it = mimeInfo.keySet().iterator(); it.hasNext();) {
				String postfix = (String) it.next();
                Element filetypeEle = root.addElement("filetype");
                filetypeEle.addAttribute("postfix",postfix);
                List<String> appPathList = mimeInfo.get(postfix);
                for (String appPath : appPathList ) {
                    filetypeEle.addElement("appPath").addText(appPath);
                }
			}

			FileOutputStream fos = new FileOutputStream(storePath);
			OutputFormat outformat = OutputFormat.createPrettyPrint();

			outformat.setEncoding("UTF-8");
			BufferedOutputStream out = new BufferedOutputStream(fos);
			XMLWriter writer = new XMLWriter(out, outformat);

			writer.write(document);
			writer.flush();
			writer.close();
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
