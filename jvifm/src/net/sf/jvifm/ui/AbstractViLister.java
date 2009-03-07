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

import net.sf.jvifm.ResourceManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public abstract class AbstractViLister extends Canvas implements ViLister {
	public static final int NOMAL_MODE = 0;

	public static final int TAG_MODE = 1;

	public static final int VTAG_MODE = 2;

	protected Table table;

	private TableCursor cursor;

	protected int currentRow;

	protected String searchString;

	protected String filterString = null;

	protected String countString = null;

	protected int operateMode = 0;

	protected int origRow = 0;

	public AbstractViLister(Composite parent, int style) {
		super(parent, style);
		initViLister();
		addViKeyListener();

	}

	protected void initViLister() {
		this.setLayout(new FillLayout());
		table = new Table(this, SWT.MULTI);

		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				currentRow = table.getSelectionIndex();
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				enterPath();
			}
		});
	}

	protected void addViKeyListener() {
		table.addKeyListener(new ViKeyListener(this));
	}

	public void switchToVTagMode() {
		this.operateMode = VTAG_MODE;
		this.origRow = currentRow;
	}

	public void switchToTagMode() {
		this.operateMode = TAG_MODE;
		currentRow = table.getSelectionIndex();

		if (cursor == null || cursor.isDisposed()) {
			cursor = new TableCursor(table, SWT.NONE);
			cursor.setSelection(table.getSelectionIndex(), 0);
			cursor.setFocus();
			cursor.addKeyListener(new ViKeyListener(this));
		}

	}

	public void tagCurrentItem() {
		if (this.operateMode != TAG_MODE) {
			switchToTagMode();
		} else {
			toggleSelection(currentRow);
		}

	}

	protected void toggleSelection(int index) {
		if (table.isSelected(index)) {
			delSelection(index);
		} else {
			addSelection(index);
		}
	}

	protected void addSelection(int index) {
		int[] selections = table.getSelectionIndices();
		int[] tmp = new int[selections.length + 1];
		for (int i = 0; i < selections.length; i++) {
			tmp[i] = selections[i];
		}
		tmp[tmp.length - 1] = index;
		table.setSelection(tmp);
		table.showSelection();

	}

	protected void delSelection(int index) {
		int[] selections = table.getSelectionIndices();
		if (selections.length <= 1)
			return;
		int[] tmp = new int[selections.length];
		for (int i = 0; i < selections.length; i++) {
			if (index == selections[i])
				continue;
			tmp[i] = selections[i];
		}
		table.setSelection(tmp);
		table.showSelection();

	}

	public void searchNext(boolean isForward) {
		if (searchString != null) {
			if (isForward) {
				incSearch(searchString, true, false);
			} else {
				incSearch(searchString, false, false);
			}
		}
	}

	protected void select(int origRow, int currentRow) {
		if (origRow > currentRow) {
			table.setSelection(currentRow, origRow);
		} else {
			table.setSelection(origRow, currentRow);
		}
	}

	public void selectAll() {
		table.setSelection(0, table.getItemCount());
	}

	protected void doSelect() {
		switch (this.operateMode) {
		case NOMAL_MODE:
			table.setSelection(currentRow);
			break;
		case VTAG_MODE:
			select(origRow, currentRow);
			table.showItem(table.getItem(currentRow));
			break;
		case TAG_MODE:
			if (!cursor.isDisposed()) {
				cursor.setSelection(currentRow, 0);
			}
		}
	}

	public void cursorDown() {
		cursorDown(1);
	}

	public void cursorDown(int count) {
		int i = 0;
		while (i < count && currentRow < table.getItemCount() - 1) {
			i++;
			currentRow++;
		}
		doSelect();
	}

	public void cursorUp() {
		cursorUp(1);
	}

	public void cursorUp(int count) {
		int i = 0;
		while (i < count && currentRow > 0) {
			i++;
			currentRow--;
		}
		doSelect();
	}

	public void cursorHead() {
		currentRow = table.getTopIndex();
		doSelect();
	}

	public void cursorLast() {
		if (table.getItemCount() < 1)
			return;
		int count = table.getSize().y / table.getItem(0).getBounds().height - 1;
		currentRow = table.getTopIndex() + count - 2;
		if (currentRow >= table.getItemCount())
			currentRow = table.getItemCount() - 1;
		doSelect();
	}

	public void cursorMiddle() {
		if (table.getItemCount() < 1)
			return;
		int count = table.getSize().y / table.getItem(0).getBounds().height - 1;
		if (count >= table.getItemCount())
			count = table.getItemCount() - 1;
		currentRow = table.getTopIndex() + (count / 2);
		doSelect();
	}

	public void cursorBottom() {
		currentRow = table.getItemCount() - 1;
		doSelect();

	}

	public void cursorTop() {
		currentRow = 0;
		doSelect();
	}

	public void doPaste() {
	}

	public void doDelete() {
	}

	public void cancelOperate() {
		switchToNormalMode();
		table.setFocus();
		table.setSelection(currentRow);
	}

	public void switchToNormalMode() {
		if (this.operateMode == TAG_MODE) {
			if (!cursor.isDisposed())
				cursor.dispose();
			table.setFocus();
		}
		this.operateMode = NOMAL_MODE;
	}

	public abstract void enterPath();

	public void incSearch(String pattern, boolean isForward, boolean isIncrease) {

		this.searchString = pattern;
		int curSearchPos = 0;
		boolean isFind = false;
		if (!isIncrease)
			curSearchPos = 1;
		TableItem[] items = table.getItems();

		for (int i = 0; i < items.length - 1; i++) {
			int nextPos = 0;
			if (isForward) {
				nextPos = currentRow + curSearchPos;
				if (nextPos >= items.length) {
					curSearchPos = curSearchPos - items.length;
					nextPos = currentRow + curSearchPos;
				}
			} else {
				nextPos = currentRow - curSearchPos;
				if (nextPos < 0) {
					curSearchPos = curSearchPos - items.length;
					nextPos = currentRow - curSearchPos;
				}
			}

			if (items[nextPos].getText(0).toLowerCase().indexOf(pattern) > -1) {
				isFind = true;
				currentRow = nextPos;
				table.setSelection(currentRow);
				break;
			}
			curSearchPos++;
		}

		// if (!isIncrease && !isFind ) fileManager.setTipInfo("file not
		// found");
		redraw();
	}

	public void upOneDir() {
	}

	public void activeWidget() {
		table.setFocus();
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

	public void refresh() {
		// TODO Auto-generated method stub

	}

	public void switchPanel() {
		// TODO Auto-generated method stub

	}

}
