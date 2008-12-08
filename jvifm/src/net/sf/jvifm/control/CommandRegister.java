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

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.cli.Options;

public class CommandRegister {
	
	@SuppressWarnings("unchecked")
	private HashMap commands=new HashMap();
	
	private static CommandRegister instance=new CommandRegister();
	
	public static CommandRegister getInstance() {
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public void register(String name,Options options) {
		commands.put(name, options);
	}
	public void unRegister(String name) {
		commands.remove(name);
	}
	public Options getCommandOptions(String name) {
		Object result=commands.get(name);
		if (result==null) return null;
		return (Options) result;
	}
	
	@SuppressWarnings("unchecked")
	public String[] getCmdNames() {
		if (commands==null) return null;
		String[] result=new String[commands.keySet().size()];
		int i=0;
		for (Iterator it=commands.keySet().iterator(); it.hasNext();) {
			result[i++]=(String)it.next();
		}
		return result;
	}

}
