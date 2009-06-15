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

package net.sf.jvifm.ui;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jvifm.Main;
import net.sf.jvifm.ResourceManager;
import net.sf.jvifm.model.Bookmark;
import net.sf.jvifm.model.BookmarkManager;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class FileTree extends Canvas implements ViLister {

	private Tree tree;
	private TreeItem root;
	private TreeItem currentItem;
	private Image folderImage;
	private Image driveImage;
    private BookmarkManager bookmarkManager=BookmarkManager.getInstance();
    
    private List<TreeItem> selectedItems=new ArrayList<TreeItem>();
    
    private String searchString;
    
    private Color selectedColor = Main.display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
    private Color noSelectedColor = Main.display.getSystemColor(SWT.COLOR_WHITE);

    public void buildRootList(List<File> fileList) {
    	for (File file: fileList) {
    		buildRootNode(file);
    	}
    }
	public void buildRootNode(File file) {

		if (file == null) {
			File[] files = File.listRoots();
			for (int i = 0; files != null && i < files.length; i++)
				addFileToTree(tree, files[i], driveImage);
		} else {

			root = new TreeItem(tree, 0);
			root.setText(file.getPath());
			root.setImage(folderImage);
			root.setData(file);

			File[] files = file.listFiles((FileFilter)Util.getDefaultDirFilter());
			for (int i = 0; files != null && i < files.length; i++)
				addFileToTree(root, files[i], folderImage);
		}
		setSelection(tree.getItem(0));
	}
	
	public void listBookMarks() {
		String pwd=Main.fileManager.getActivePanel().getPwd();
		tree.removeAll();
		selectedItems.clear();
	  	for (Iterator<Bookmark> it=bookmarkManager.iterator(); it.hasNext(); ) {
    		Bookmark mark=(Bookmark)it.next();	
    		File file=new File(mark.getPath());
			addFileToTree(tree, file, folderImage);
	  	}
		setSelection(tree.getTopItem());
		syncView(pwd);
	}
	
		
	public FileTree(Composite parent, int style, String path) {
		super(parent, style);
		folderImage = ResourceManager.getImage("folder.png");
		driveImage = ResourceManager.getImage("drive.png");

		this.setLayout(new FillLayout());

		tree = new Tree(this, SWT.NONE);
		if (Main.operatingSystem == Main.WINDOWS	) {
			buildRootNode(null);
		} else {
			buildRootNode(new File("/"));
		}

		tree.addTreeListener(new TreeAdapter() {
			public void treeExpanded(TreeEvent e) {
				TreeItem item = (TreeItem) e.item;
				expandTree(item);
			}
		});

		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TreeItem item = (TreeItem) e.item;
				currentItem = item;
				File file = (File) item.getData();
				showInFileLister(file.getAbsolutePath());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				TreeItem item = (TreeItem) e.item;
				currentItem = item;
				File file = (File) item.getData();
				showInFileLister(file.getAbsolutePath());
			}
		});

		tree.addKeyListener(new FileTreeListener(this) );
		
		if (path != null) syncView(path);

	}
	
	public void syncView(String path) {
		
		TreeItem[] items=tree.getItems();
		int i=-1;
		while (true ) {
			i++;
			if (i>items.length-1) break;
			File file=(File)items[i].getData();
			if (path.equalsIgnoreCase(file.getPath())) {
				setSelection(items[i]);
				break;
			}
			if (path.startsWith(file.getPath()) ) {
				if (items[i].getExpanded()==false)  {
					expandTree(items[i]);
				}
				items=items[i].getItems();
				if (items==null || items.length==0) break;
				i=-1;
			}
		}
			
	}

	private void showInFileLister(String path) {
		Main.fileManager.getActivePanel().visit(path);
	}

	public void expandTree(TreeItem item) {
		TreeItem[] children = item.getItems();

		for (int i = 0; i < children.length; i++)
			if (children[i].getData() == null)
				children[i].dispose();
			else
				// Child files already added to the tree.
				return;

		File[] files = ((File) item.getData()).listFiles((FileFilter)Util.getDefaultDirFilter());
		for (int i = 0; files != null && i < files.length; i++)
			addFileToTree(item, files[i], folderImage);
	}

	private void addFileToTree(Object parent, File file, Image image) {
		TreeItem item = null;

		if (parent instanceof Tree)
			item = new TreeItem((Tree) parent, SWT.NULL);
		else if (parent instanceof TreeItem)
			item = new TreeItem((TreeItem) parent, SWT.NULL);
		else
			throw new IllegalArgumentException(
					"parent should be a tree or a tree item: " + parent);

		if (file.getName() == null || file.getName().equals("")) {
			item.setText(file.getPath());
		} else {
			item.setText(file.getName());
		}
		item.setImage(image);
		item.setData(file);

		new TreeItem(item, SWT.NULL);
	}

	
	public void filterView() {
		if (selectedItems.size()==0) {
			File file = (File) currentItem.getData();
			tree.removeAll();
			buildRootNode(file);
		} else {
			List<File> fileList=new ArrayList<File>();
			for (TreeItem item: selectedItems) {
				File file=(File)item.getData();
				fileList.add(file);
			}
			tree.removeAll();
			buildRootList(fileList);
		}
		selectedItems.clear();
		
	}
	
	
	public void collapseItem() {
		TreeItem parent=currentItem.getParentItem();
		if (parent!=null) parent.setExpanded(false);
	}
	
	public void selectParentDir() {
		TreeItem parent=currentItem.getParentItem();
		if (parent!=null) setSelection(parent);
	}

	public void enterPath() {
		File file = (File) currentItem.getData();
		expandTree(currentItem);
		currentItem.setExpanded(true);
		showInFileLister(file.getAbsolutePath());

		if (currentItem.getItemCount() > 0) {
			setSelection(currentItem.getItem(0));
		} 
	}
	

	public boolean isDisposed() {
		if (tree != null)
			return tree.isDisposed();
		return false;
	}

	public void redraw() {
		tree.redraw();
	}

	public void cursorDown(int count) {
		for (int i = 1; i <= count; i++) {
			cursorDown();
		}
	}

	public void cursorUp(int count) {
		for (int i = 1; i <= count; i++) {
			cursorUp();
		}
	}
	
	public void activeWidget() {
		tree.setFocus();
	}

	public boolean setFocus() {
		return tree.setFocus();
	}

	@Override public void switchPanel() {
		Main.fileManager.getActivePanel().active();
	}
	public void upOneDir() {
		TreeItem parent = currentItem.getParentItem();
		if (parent != null) {
			setSelection(parent);
		}
	}

	
	public void cursorHead() {
		setSelection(tree.getItem(0));
	}

	
	@Override public void cursorBottom() { 
		
		setSelection(getBottomItem());
	}


	public void cursorTop() {
		setSelection(tree.getItem(0));
	}
	
	public void cursorDown() {
		TreeItem tempItem=getNextItem(currentItem);
		if (tempItem !=null) setSelection(tempItem);
	}
	
	public void cursorUp() {
		TreeItem tempItem=getPrevItem(currentItem);
		if (tempItem !=null) setSelection(tempItem);
	}
	
	public TreeItem getBottomItem() {
		TreeItem item=tree.getItem(tree.getItemCount()-1);
		while (item.getExpanded()==true)  {
			item=item.getItem(item.getItemCount()-1);
		}
		return item;
	}
	
	public TreeItem getCurrentItem() {
		return currentItem;
	}
	
	public TreeItem getNextSiblingItem(TreeItem currentItem) {
		TreeItem parent = currentItem.getParentItem();
		if (parent!=null) {
			int index = parent.indexOf(currentItem);
			if (index < parent.getItemCount() - 1) {
				return (parent.getItem(index + 1));
			}
		}  else {
			int index = tree.indexOf(currentItem);
			if (index < tree.getItemCount() - 1) {
				return (tree.getItem(index + 1));
			}
		}
		return currentItem;
	}
	
	public TreeItem getPrevSiblingItem(TreeItem currentItem) {
		
		TreeItem parent = currentItem.getParentItem();
		if (parent!=null) {
			int index = parent.indexOf(currentItem);
			if (index > 0) {
				return (parent.getItem(index - 1));
			}
		}  else {
			int index=tree.indexOf(currentItem);
			if (index > 0) { 
				return (tree.getItem(index - 1));
			}
		}
		return currentItem;
		
	}
	
	public void cursorNextSibling() {
		TreeItem item=getNextSiblingItem(currentItem);
		setSelection(item);
	}
	public void cursorPrevSibling() {
		TreeItem item=getPrevSiblingItem(currentItem);
		setSelection(item);
	}
	
	public TreeItem getNextItem(TreeItem currentItem) {
		if (currentItem.getItemCount() > 0 && currentItem.getExpanded() == true) {
			return (currentItem.getItem(0));
		}
		TreeItem parent = currentItem.getParentItem();
		if (parent != null) {
			int index = parent.indexOf(currentItem);
			if (index < parent.getItemCount() - 1) {
				return (parent.getItem(index + 1));
			} else if (index >= parent.getItemCount() - 1) {
				return (getNextItemOfLastItem(currentItem));
			}
		} else {
			int index = tree.indexOf(currentItem);
			if (index < tree.getItemCount() - 1) {
				return (tree.getItem(index + 1));
			} 
		}
		return null;
	}

	private TreeItem getNextItemOfLastItem(TreeItem item) {

		if (item == null)
			return null;
		TreeItem parent = null;
		TreeItem sub = item;

		while (true) {
			parent = sub.getParentItem();
			if (parent != null) {
				if (parent.indexOf(sub) == parent.getItemCount() - 1) {
					sub = parent;
					parent = parent.getParentItem();
				} else {
					return parent.getItem(parent.indexOf(sub) + 1);
				}
			} else {
				if (tree.indexOf(sub) == tree.getItemCount() -1) {
					return null;
				} else {
					return tree.getItem(tree.indexOf(sub) + 1);
				}
			}

		}

	}
	
	public void openWithDefault() {
		File file = (File) currentItem.getData();
		String path=file.getPath();
		Util.openFileWithDefaultApp(path);
	}

	public TreeItem getPrevItem(TreeItem currentItem) {
		TreeItem parent = currentItem.getParentItem();
		if (parent != null) {
			int index = parent.indexOf(currentItem);
			if (index > 0) {
				return (getBottemItem(parent.getItem(index - 1)));
			} else if (index == 0) {
				return (parent);
			}
		} else {
			int index = tree.indexOf(currentItem);
			if (index > 0)
				return (getBottemItem(tree.getItem(index - 1)));
		}
		return null;
	}

	private TreeItem getBottemItem(TreeItem item) {
		do {
			if (item.getItemCount() <= 0)
				return item;
			if (item.getExpanded() == false)
				return item;
			item = item.getItem(item.getItemCount() - 1);
		} while (true);
	}

	

	private void setSelection(TreeItem item) {
		currentItem = item;
		tree.setSelection(currentItem);
		File file = (File) currentItem.getData();
		showInFileLister(file.getAbsolutePath());
	}

	
	@Override
	public void incSearch(String pattern, boolean isForward, boolean isIncrease) {
		this.searchString = pattern;
		TreeItem item=currentItem;
		
		if ( isIncrease ) {
			if (item.getText().toLowerCase().indexOf(pattern) > -1 )  return;
		}
		
		boolean wrapped=false;
		
		while (true) {
			
			if (isForward) {
				item=getNextItem(item);
			}else {
				item=getPrevItem(item);
			}
			
			if (item==null && !wrapped) {
				wrapped=true;
				if (isForward) {
					item=tree.getItem(0);
				}else {
					item=getBottomItem();
				}			}
			if (item==null) break;
			
			if (item.getText().toLowerCase().indexOf(pattern) > -1 ) {
				setSelection(item);
				break;
			}
			
		}
		
		
	}
	@Override public void searchNext(boolean isForward) { 	
		if (searchString != null) {
			if (isForward) {
				incSearch(searchString, true, false);
			} else {
				incSearch(searchString, false, false);
			}
		}
	}
	
	@Override public void cancelOperate() {
		if (selectedItems.size()==0) {
			switchPanel();
		} else {
			for (TreeItem item : selectedItems) {
				item.setData("selection","false");
				if (!item.isDisposed()) item.setBackground(noSelectedColor);
			}
			selectedItems.clear();
		}
	}

	@Override public void cursorLast() { 	}

	@Override public void cursorMiddle() { 	}

	@Override public void doChange() { 	}

	@Override public void doCut() { 	}

	@Override public void doDelete() { 	}

	@Override public void doPaste() { }

	@Override public void doYank() { }

	
	@Override public void refresh() { }


	@Override public void switchToVTagMode() { 	}

	@Override public void tagCurrentItem() {
		
		Object selection=currentItem.getData("selection");
		
		if (selection !=null && selection.equals("true")) {
			currentItem.setBackground(noSelectedColor);
			currentItem.setData("selection","false");
			selectedItems.remove(currentItem);
		} else {
			currentItem.setBackground(selectedColor);
			currentItem.setData("selection", "true");
			selectedItems.add(currentItem);
		}
	}
	
	
}
