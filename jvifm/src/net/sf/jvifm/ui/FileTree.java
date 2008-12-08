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

import net.sf.jvifm.Main;
import net.sf.jvifm.ResourceManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
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
	
	private FileLister fileLister;

	private File rootDir;

	public FileTree(Composite parent,int style) {
		super(parent, style);
		folderImage = ResourceManager.getImage("folder16.png");

		this.setLayout(new FillLayout());
		
		
		tree = new Tree(this, SWT.NONE);
		root = new TreeItem(tree, 0);
		root.setText("file system");
		root.setImage(folderImage);
		root.setData(new File(""));
		
		File[] files = File.listRoots();
		for (int i = 0; files != null && i < files.length; i++)
			addFileToTree(root, files[i]);
		


		tree.addTreeListener(new TreeAdapter() {
			public void treeExpanded(TreeEvent e) {
				TreeItem item = (TreeItem) e.item;
				expandTree(item);
			}
		});

		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TreeItem item = (TreeItem) e.item;
				currentItem=item;
				File file = (File) item.getData();
				enterPath(file.getAbsolutePath());
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				TreeItem item = (TreeItem) e.item;
				currentItem=item;
				File file = (File) item.getData();
				enterPath(file.getAbsolutePath());
			}
		});
		
		tree.addKeyListener(new ViKeyListener(this) {
			public void keyPressed(KeyEvent event) {
				super.keyPressed(event);
				switch (event.character) {
				case ' ':
					Main.fileManager.getActivePanel().active();
					break;
				case '<':
					collapseItem();
					break;
				case '>':
					expandItem();
					break;
				}
			}
		});

	}
	
	private void enterPath(String path) {
		Main.fileManager.getActivePanel().visit(path);
	}
	
	private void expandTree(TreeItem item) {
		TreeItem[] children = item.getItems();

		for (int i = 0; i < children.length; i++)
			if (children[i].getData() == null)
				children[i].dispose();
			else
				// Child files already added to the tree.
				return;

		File[] files = ((File) item.getData())
				.listFiles(new DirFilter());
		for (int i = 0; files != null && i < files.length; i++)
			addFileToTree(item, files[i]);
	}
	

	private void addFileToTree(Object parent, File file) {
		TreeItem item = null;

		if (parent instanceof Tree)
			item = new TreeItem((Tree) parent, SWT.NULL);
		else if (parent instanceof TreeItem)
			item = new TreeItem((TreeItem) parent, SWT.NULL);
		else
			throw new IllegalArgumentException(
					"parent should be a tree or a tree item: " + parent);
		
		if (file.getName()==null || file.getName().equals(""))  {
			item.setText(file.getPath());
		} else {
			item.setText(file.getName());
		}
		item.setImage(folderImage);
		item.setData(file);
		

		new TreeItem(item, SWT.NULL);
	}

	public void activeCommandMode() {
		// TODO Auto-generated method stub

	}

	public void activeSearchMode() {
		// TODO Auto-generated method stub

	}

	public void cancelOperate() {
		// TODO Auto-generated method stub

	}

	

	public void cursorBottom() {
		// TODO Auto-generated method stub

	}
	public void changePwd() {
		
	}

	public void cursorDown() {
		if (currentItem.getItemCount()>0 &&currentItem.getExpanded()==true) {
			setSelection(currentItem.getItem(0));
			return;
		}
		TreeItem parent=currentItem.getParentItem();
		if (parent!=null) {
			int index=parent.indexOf(currentItem);
			if (index<parent.getItemCount()-1) {
				setSelection(parent.getItem(index+1));
			}  else if (index>=parent.getItemCount()-1)  {
				setSelection(getNextItem(parent));
			}
		} else {
			int index=tree.indexOf(currentItem);
			if (index<tree.getItemCount()-1) setSelection(tree.getItem(index+1));
		}

	}
	private TreeItem getNextItem(TreeItem item) {
		
		if (item==null ) return null;
		TreeItem parent=null;
		TreeItem sub=item;
		
		while (true) {
			parent=sub.getParentItem();
			if (parent!=null) {
				if ( parent.indexOf(sub)==parent.getItemCount()-1) {
    				sub=parent;
    				parent=parent.getParentItem();
    			}  else {
    				return parent.getItem(parent.indexOf(sub)+1);
    			}
			}
			
		}
		
	}
	public void cursorUp() {
		TreeItem parent=currentItem.getParentItem();
		if (parent!=null) {
			int index=parent.indexOf(currentItem);
			if (index>0) {
				setSelection(getBottemItem(parent.getItem(index-1)));
			} else if (index==0) {
				setSelection(parent);
			}
		} else {
			int index=tree.indexOf(currentItem);
			if (index>0) setSelection(tree.getItem(index-1));
		}

	}
	
	private TreeItem getBottemItem(TreeItem item) {
		do {
			if (item.getItemCount()<=0) return item;
    		if (item.getExpanded()==false)   return item;
			item=item.getItem(item.getItemCount()-1);
		} while (true);
	}

	public void cursorHead() {
		setSelection(tree.getItem(0)) ;

	}

	public void cursorLast() {
		setSelection(tree.getItem(tree.getItemCount()-1));
	}

	public void cursorMiddle() {
		// TODO Auto-generated method stub

	}

	public void cursorTop() {
		setSelection(tree.getItem(0)) ;
	}

	
	
	private void setSelection(TreeItem item) {
		currentItem=item;
		tree.setSelection(currentItem);
		File file = (File) currentItem.getData();
		enterPath(file.getAbsolutePath());
	}

	public void doDelete() {
		// TODO Auto-generated method stub

	}

	public void doPaste() {
		// TODO Auto-generated method stub

	}
	
	public void expandItem() {
		File file = (File) currentItem.getData();
		expandTree(currentItem);
		currentItem.setExpanded(true);
		enterPath(file.getAbsolutePath());
		
		if (currentItem.getItemCount()>0 ) {
			setSelection(currentItem.getItem(0));
		}
	}
	public void collapseItem() {
		currentItem.setExpanded(false);
	}

	public void enterPath() {
		TreeItem parent=currentItem.getParentItem();
		TreeItem item=getNextItem(parent);
		if (item!=null) setSelection(item);
	}

	public void incSearch(String pattern, boolean isForward, boolean isIncrease) {
		// TODO Auto-generated method stub

	}

	public boolean isDisposed() {
		if (tree !=null) return tree.isDisposed();
		return false;
	}

	public void redraw() {
		tree.redraw();
	}
	public void refresh() {}

	public void searchNext(boolean isForward) {
		// TODO Auto-generated method stub

	}

	public void cursorDown(int count) {
		// TODO Auto-generated method stub
		
	}

	public void cursorUp(int count) {
		// TODO Auto-generated method stub
		
	}

	public void doChange() {
		// TODO Auto-generated method stub
		
	}

	public void doCut() {
		// TODO Auto-generated method stub
		
	}

	public void doYank() {
		// TODO Auto-generated method stub
		
	}

	

	public void switchPanel() {
		// TODO Auto-generated method stub
		
	}

	public void switchToVTagMode() {
		// TODO Auto-generated method stub
		
	}

	public void tagCurrentItem() {
		// TODO Auto-generated method stub
		
	}

	public void activeWidget() {
		tree.setFocus();
	}

	public boolean setFocus() {
		return tree.setFocus();
	}

	public void upOneDir() {
		TreeItem parent=currentItem.getParentItem();
		if (parent!=null) {
			setSelection(parent);
		}
	}

	class DirFilter implements FileFilter {
		public boolean accept(File pathname) {
			if (pathname.isDirectory())
				return true;
			return false;
		}
	}
}
