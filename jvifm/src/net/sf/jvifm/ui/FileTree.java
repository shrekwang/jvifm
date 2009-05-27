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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class FileTree extends Canvas implements ViLister {

	private Tree tree;
	private TreeItem root;
	private TreeItem currentItem;
	private Image folderImage;
	private Image driveImage;

	private void initRootNode(File file) {
		tree.removeAll();

		if (file == null) {
			File[] files = File.listRoots();
			for (int i = 0; files != null && i < files.length; i++)
				addFileToTree(tree, files[i], driveImage);
		} else {

			root = new TreeItem(tree, 0);
			root.setText(file.getName());
			root.setImage(folderImage);
			root.setData(file);

			File[] files = file.listFiles(new TreeFolderFilter());
			for (int i = 0; files != null && i < files.length; i++)
				addFileToTree(root, files[i], folderImage);
		}
		setSelection(tree.getTopItem());
	}

	public FileTree(Composite parent, int style) {
		super(parent, style);
		folderImage = ResourceManager.getImage("folder.png");
		driveImage = ResourceManager.getImage("drive.png");

		this.setLayout(new FillLayout());

		tree = new Tree(this, SWT.NONE);
		initRootNode(null);

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

		tree.addKeyListener(new ViKeyListener(this) {
			public void keyPressed(KeyEvent event) {
				super.keyPressed(event);
				switch (event.character) {
				case 'h':
					collapseItem();
					break;
				case 'i':
					File file = (File) currentItem.getData();
					initRootNode(file);
					break;
				case 'u':
					initRootNode(null);
					break;
				}
			}
		});

	}

	private void showInFileLister(String path) {
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

		File[] files = ((File) item.getData()).listFiles(new TreeFolderFilter());
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

	
	public void collapseItem() {
		currentItem.getParentItem().setExpanded(false);
	}

	public void enterPath() {
		File file = (File) currentItem.getData();
		expandTree(currentItem);
		currentItem.setExpanded(true);
		showInFileLister(file.getAbsolutePath());

		if (currentItem.getItemCount() > 0) {
			setSelection(currentItem.getItem(0));
		} else {
			switchPanel();
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

	@Override
	public void switchPanel() {
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

	public void cursorLast() {
		setSelection(tree.getItem(tree.getItemCount() - 1));
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
				return (getNextItemOfLastItem(parent));
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
				return null;
			}

		}

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
				return (tree.getItem(index - 1));
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

	class TreeFolderFilter implements FileFilter {
		public boolean accept(File file) {
			if (file.isDirectory() 
					&& ! file.getName().startsWith(".")) 
				return true;
			return false;
		}
	}

	
	@Override
	public void incSearch(String pattern, boolean isForward, boolean isIncrease) {
		
		TreeItem item=currentItem;
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
					item=tree.getItem(tree.getItemCount() - 1);
				}			}
			if (item==null) break;
			
			if (item.getText().toLowerCase().indexOf(pattern) > -1 ) {
				setSelection(item);
				break;
			}
			
		}
		
		
	}

	
	@Override public void cancelOperate() { 	}

	@Override public void cursorBottom() { 	}

	@Override public void cursorMiddle() { 	}

	@Override public void doChange() { 	}

	@Override public void doCut() { 	}

	@Override public void doDelete() { 	}

	@Override public void doPaste() { }

	@Override public void doYank() { }

	
	@Override public void refresh() { }

	@Override public void searchNext(boolean isForward) { 	}


	@Override public void switchToVTagMode() { 	}

	@Override public void tagCurrentItem() { }
	
	
}
