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

import java.util.LinkedList;

import net.sf.jvifm.Main;
import net.sf.jvifm.ResourceManager;
import net.sf.jvifm.model.HistoryListener;
import net.sf.jvifm.model.HistoryManager;
import net.sf.jvifm.ui.AbstractViLister;
import net.sf.jvifm.ui.FileLister;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

public class HistoryLister extends AbstractViLister implements HistoryListener {

	private Image folderImage = ResourceManager.getImage("folder.png");
	private FileLister fileLister;
	private HistoryManager historyManager;

	public void onAddHistoryRecord(String path, boolean needRemove) {
		if (needRemove) {
			refresh();
		} else {
			addRecord(path);
		}
	}

	public void onChangePos() {
		currentRow = historyManager.getPosition();
		table.setSelection(currentRow);
	}

	public void loadHistoryRecord() {
		this.fileLister = Main.fileManager.getActivePanel();
		historyManager = fileLister.getHistoryInfo();
		historyManager.addListener(this);
		refresh();
	}

	public HistoryLister(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new FillLayout());
		loadHistoryRecord();
	}

	private void addRecord(String path) {
		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getText().equals(path))
				table.remove(i);
		}

		TableItem item = new TableItem(table, SWT.NONE);
		item.setImage(folderImage);
		item.setText(path);
		currentRow = table.getItemCount() - 1;
		table.setSelection(currentRow);
	}

	public void refresh() {
		table.removeAll();
		TableItem item;

		LinkedList list = historyManager.getFullHistory();
		currentRow = historyManager.getPosition();

		for (int i = 0; i < list.size(); i++) {
			String dir = (String) list.get(i);
			item = new TableItem(table, SWT.NONE);
			item.setImage(folderImage);
			item.setText(dir);
		}
		table.setSelection(currentRow);
	}

	public void enterPath() {

		fileLister.changePwd(table.getItem(currentRow).getText());
		fileLister.active();
		historyManager.gotoPath(table.getItem(currentRow).getText());
	}

	public void cancelOperate() {
		if (this.operateMode == NOMAL_MODE) {
			FileLister fileLister = Main.fileManager.getActivePanel();
			fileLister.active();
		} else {
			super.cancelOperate();
		}
	}

	public void dispose() {
		super.dispose();
		historyManager.removeListener(this);
	}

}
