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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class HistoryManager {
	
	private LinkedList history=new LinkedList();
	private HashMap selectedItemMap=new HashMap();
	
	private LinkedList listeners=new LinkedList();
	//position on current visiting path
	private int position=-1;
	
	
	public void addListener(HistoryListener listener) {
		this.listeners.add(listener);
	}
	public void removeListener(HistoryListener listener) {
		this.listeners.remove(listener);
	}
	public void notifyAddHistoryRecord(String path,boolean needRemove) {
		for (Iterator it=listeners.iterator(); it.hasNext(); ) {
			HistoryListener listener=(HistoryListener)it.next();
			listener.onAddHistoryRecord(path,needRemove);
		}
	}
	public void notifyChangePos() {
		for (Iterator it=listeners.iterator(); it.hasNext(); ) {
			HistoryListener listener=(HistoryListener)it.next();
			listener.onChangePos();
		}
	}
	
	
	
	public void setSelectedItem(String path,String item) {
		selectedItemMap.put(path, item);
	}
	
	public String getSelectedItem(String path) {
		return (String)	selectedItemMap.get(path);
	}
	
	public void addToHistory(String path) {
		
		boolean needRemove=false;
		//after some forward operate, remove the record at the rear
		if (position<history.size()-1) {
			int offset=history.size()-position-1;
			for (int i=0; i<offset; i++) {
				history.removeLast();
				needRemove=true;
			}
		}
		
		//if the list contains the path, then remove it and readd it.
		if (history.contains(path)) {
    		history.remove(path);
    		position--;
		}
		history.addLast(path);
		
		position++;
		notifyAddHistoryRecord(path,needRemove);
	}
	
	public String back() {
		if (position<0) return null;
		position--;
		notifyChangePos();
		return (String)history.get(position);
	}
	

	public String forward() {
		if (position>=history.size()-1) return null;
		position++;
		notifyChangePos();
		return (String)history.get(position);
	}
	
	public LinkedList getFullHistory() {
		return this.history;
	}
	
	public String forward(int count) {
		int i=0;
		if (position==history.size()-1) return (String)history.get(position);
		while (i++<count && position++<history.size()-1);
		notifyChangePos();
		return (String)history.get(position) ;
		
	}
	
	public String back(int count) {
		int i=0;
		if (position==0) return (String)history.get(0);
		while (i++<count && position-- > 1);
		notifyChangePos();
		return (String)history.get(position) ;
	}
	
	public void backTo(String path) {
		while (!back().equals(path) && position>0);
	}
	public void forwardTo(String path) {
		while (!forward().equals(path) && position<history.size()-1) ;
	}
	
	public int getPosition () {
		return this.position;
	}
	public void gotoPath(String path) {
		if (history==null) return;
		for (int i=0; i<history.size(); i++) {
			String his=(String)history.get(i);
			if (his.equals(path)) {
				position=i;
				notifyChangePos();
			}
		}
	}
	
	public String visitPathAt(int pos) {
		if (pos<0 || pos>history.size()-1) return null;
		position=pos;
		notifyChangePos();
		return (String)history.get(pos);
		
	}
	public List getBackList() {
		if (  history==null || history.size()<2 || position<1 ) return null;
		LinkedList result=new LinkedList();
		int i=0;
		for (Iterator it=history.iterator(); it.hasNext();) {
			if (i>=position) break;
			i++;
			result.addFirst(it.next());
			
		}
		return result;
	}
	public List getForwardList() {
		if (history==null || history.size()<2 || position >=history.size()-1 ) return null;
		LinkedList result=new LinkedList();
		int i=0;
		int count=history.size()-position-1;
		for (Iterator it=history.iterator(); it.hasNext();) {
			if (i>=count) break;
			
			i++;
			result.addFirst(it.next());
		}
		return result;
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HistoryManager historyManager=new HistoryManager();
		historyManager.addToHistory("www.javaeye.com");
		historyManager.addToHistory("www.header.com");
		historyManager.addToHistory("www.kernel.com");
		
		
		System.out.println(historyManager.back(1));
		System.out.println(historyManager.back(1));
		System.out.println(historyManager.back(1));
		System.out.println(historyManager.back(1));
		System.out.println(historyManager.back(1));
		System.out.println(historyManager.back(1));
		
		
		
		/*
		System.out.println(historyManager.back());
		System.out.println(historyManager.back());
		System.out.println(historyManager.back());
		*/
		System.out.println(historyManager.forward());
		
		historyManager.addToHistory("www.linuxeden.com");
		System.out.println(historyManager.back());
		System.out.println(historyManager.forward());
	}

}
