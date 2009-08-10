/** 
 
 * @author wangsn
 * @since 1.0
 * @version $Id: MimeUtil.java, v 1.0 2009-8-10 ÏÂÎç06:49:47 wangsn Exp $
 */
package net.sf.jvifm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class MimeUtil {
	private static MimeUtil instance = null;
	private static HashMap<String,String> mimeMap=new HashMap<String,String>();

	private MimeUtil() {
	}

	public static MimeUtil getInstance() {
		if (instance == null) {

			try {
				InputStream is = MimeUtil.class.getClassLoader().getResourceAsStream("mimes.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				while (true) {
					String tmp = br.readLine();
					if (tmp == null) break;
					String [] info=tmp.split(" ");
					String path="mimes/"+info[1].replace("/", "-").trim()+".png";
					mimeMap.put(info[0].trim(), path);
				}
				br.close();
				is.close();
			} catch (Exception e) {

			}
			instance=new MimeUtil();
		}
		return instance;
	}
	
	public String getMimeIconPath(String postfix) {
		return mimeMap.get(postfix);
	}

}
