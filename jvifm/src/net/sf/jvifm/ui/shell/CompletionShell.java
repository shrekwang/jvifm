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

package net.sf.jvifm.ui.shell;

import net.sf.jvifm.ResourceManager;
import net.sf.jvifm.model.TipOption;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class CompletionShell {

	private Table tblTipInfo;
	private Shell shell;
	private Display display;
	private int x;
	private int y;
	private Image fileImage = null;
	private Image folderImage = null;

	public CompletionShell(TipOption[] options, int x, int y) {
		this.x = x;
		this.y = y;
		initGUI();
		setOptions(options);
	}

	public void open() {
		shell.open();
		shell.setVisible(true);
	}

	public void dispose() {
		shell.dispose();
	}

	public void setOptions(TipOption[] options) {
		tblTipInfo.removeAll();
		if (options != null) {
			for (int i = 0; i < options.length; i++) {
				TableItem tableItem = new TableItem(tblTipInfo, SWT.NONE);
				if (options[i].getTipType().equals("dir")) {
					tableItem.setImage(folderImage);
				} else {
					tableItem.setImage(fileImage);
				}
				tableItem.setText(0, options[i].getName());
				tableItem.setText(1, options[i].getExtraInfo());
				if (i > 25)
					break;
			}
			tblTipInfo.setSelection(0);
		}
		shell.pack();
	}

	public void setOptionIndex(int index) {
		if (index < 0)
			index = 0;
		if (index > tblTipInfo.getItemCount() - 1)
			index = tblTipInfo.getItemCount() - 1;
		tblTipInfo.setSelection(index);
	}

	public void initGUI() {

		fileImage = ResourceManager.getImage("file.png"); //$NON-NLS-1$
		folderImage = ResourceManager.getImage("folder.png"); //$NON-NLS-1$

		display = Display.getDefault();
		shell = new Shell(display, SWT.ON_TOP);

		shell.setLayout(new FillLayout());
		tblTipInfo = new Table(shell, SWT.NONE);
		shell.setLocation(x, y);

		TableColumn clmName = new TableColumn(tblTipInfo, SWT.BORDER);
		clmName.setText("name");
		clmName.setWidth(110);

		TableColumn clmExtra = new TableColumn(tblTipInfo, SWT.BORDER);
		clmExtra.setText("extra");
		clmExtra.setWidth(210);

		tblTipInfo.setHeaderVisible(true);

	}

}
