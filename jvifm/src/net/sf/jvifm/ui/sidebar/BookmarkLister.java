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
package net.sf.jvifm.ui.sidebar;

import java.io.File;
import java.util.Iterator;

import net.sf.jvifm.CommandBuffer;
import net.sf.jvifm.Main;
import net.sf.jvifm.ResourceManager;
import net.sf.jvifm.model.Bookmark;
import net.sf.jvifm.model.BookmarkListener;
import net.sf.jvifm.model.BookmarkManager;
import net.sf.jvifm.ui.BasicViLister;
import net.sf.jvifm.ui.FileLister;
import net.sf.jvifm.ui.Messages;
import net.sf.jvifm.ui.Util;
import net.sf.jvifm.ui.shell.BookmarkEditShell;
import net.sf.jvifm.ui.shell.OptionShell;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;


public class BookmarkLister extends BasicViLister implements BookmarkListener {

    //private BookmarkAccessor bookmarkAccess=BookmarkAccessor.getInstance();
    //private java.util.List bookmarkList=null;
	
    private Image folderImage = ResourceManager.getImage("folder.png");
    private BookmarkManager bookmarkManager=BookmarkManager.getInstance();

    public BookmarkLister(Composite parent, int style) {
    	
    	super(parent,style);
    	init();
    	bookmarkManager.addListener(this);
		

    }
    
    
	public void onChangeBookmark(String key, Bookmark bookmark) {
		
		int rowCount=table.getItemCount();
		for (int i=0; i<rowCount; i++) {
			String txt=table.getItem(i).getText();
			if (txt.indexOf("("+key+")")==0)  {
        		table.getItem(i).setText("("+bookmark.getKey()+")"+bookmark.getName());
        		table.getItem(i).setData("bookmark",bookmark);
        		currentRow=i;
        		table.setSelection(i);
				break;
			}
		}
		
	}


	public void onAddBookmark(Bookmark bookmark) {
		TableItem item=new TableItem(table,SWT.NONE);
		item.setImage(folderImage);
		item.setData("bookmark",bookmark);
		
		if (bookmark.getKey()!=null) 
    		item.setText("("+bookmark.getKey()+")"+bookmark.getName());
		else 
    		item.setText(bookmark.getName());
		
		currentRow=table.getItemCount()-1;
		table.setSelection(currentRow);
		
	}


	private void init() {
    	TableItem item;
    
    	
    	for (Iterator it=bookmarkManager.iterator(); it.hasNext(); ) {
    		Bookmark mark=(Bookmark)it.next();
    		item=new TableItem(table,SWT.NONE);
    		item.setImage(folderImage);
    		item.setData("bookmark", mark);
    		
    		if (mark.getKey()!=null) 
        		item.setText("("+mark.getKey()+")"+mark.getName());
    		else 
        		item.setText(mark.getName());
    	}
    	table.setSelection(0);
    }

    
    public void reloadBookmark() {
    	init();
    }
    

    public void enterPath() {
    	Bookmark bookmark=(Bookmark)table.getItem(currentRow).getData("bookmark");
    	String path=bookmark.getPath();
    	
    	File file=new File(path);
    	if (!file.exists() || !file.isDirectory()) {
    		Util.openMessageWindow("the location doesn't exists");
    	}
    	FileLister fileLister=Main.fileManager.getActivePanel();
    	fileLister.visit(path);
    	fileLister.refreshHistoryInfo();
    	fileLister.active();
    	
    }

    public void cancelOperate() {
    	if (this.operateMode== Mode.NORMAL) {
        	FileLister fileLister=Main.fileManager.getActivePanel();
        	fileLister.active();
    	} else {
        	super.cancelOperate();
    	}
    }
    
    public void doPaste() {
    	
		
		CommandBuffer commandBuffer=CommandBuffer.getInstance();
		String[] names=commandBuffer.getCommandSourceFiles();
		
		for (int i=0; i<names.length; i++){
			File file=new File(names[i]);
			if (!file.isDirectory()) continue;
    		Bookmark bookmark = new Bookmark();
    		bookmark.setName(file.getName());
    		bookmark.setPath(file.getPath());
    		BookmarkManager bm=BookmarkManager.getInstance();
    		bm.add(bookmark);
    	
		}
		currentRow=table.getItemCount()-1;
		
    }
    public void doDelete() {
    	int[] selectionIndices=table.getSelectionIndices();
    	if (selectionIndices==null || selectionIndices.length<0) return;
    	
    	String[] options = new String[] { 
    			Messages.getString("Messagebox.optionYes"),  //$NON-NLS-1$
    			Messages.getString("Messagebox.optionNo"),  //$NON-NLS-1$
    			Messages.getString("Messagebox.optionCancel")}; //$NON-NLS-1$ 
    	
    	String result=new Util().openConfirmWindow(options,
    			Messages.getString("BookmarkLister.warnDialogTitle"), //$NON-NLS-1$
    			Messages.getString("BookmarkLister.warnDialogMessage"),
    			OptionShell.WARN); //$NON-NLS-1$ 
    	
    	if (result==null || (!result.equals(Messages.getString("Messagebox.optionYes") ) ) ) return;
	    
		BookmarkManager bm=BookmarkManager.getInstance();
    	for (int i=0; i<selectionIndices.length; i++) {
    		Bookmark bookmark=(Bookmark)table.getItem(selectionIndices[i]).getData("bookmark");
    		bm.remove(bookmark);
    	}
    	table.remove(selectionIndices);
    	
     	if (currentRow>=0 && currentRow<=table.getItemCount()-1) {
     		table.setSelection(currentRow);
     	} else if (currentRow>table.getItemCount()-1) {
     		currentRow=table.getItemCount()-1;
     		table.setSelection(currentRow);
     	}
    	
    }


	public void dispose() {
		super.dispose();
		bookmarkManager.removeListener(this);
	}


	public void doChange() {
		TableItem item = table.getItem(currentRow);
		if (item == null) return;
		Bookmark bm=(Bookmark)item.getData("bookmark");
		BookmarkEditShell bmeShell=new BookmarkEditShell(bm);
		
		boolean isChanged=bmeShell.open(Main.shell);
		
		if (isChanged) {
    		if (bm.getKey()!=null) 
        		item.setText("("+bm.getKey()+")"+bm.getName());
    		else 
        		item.setText(bm.getName());
		}
	}
 
    

}
