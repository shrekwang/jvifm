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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class OptionShell {
	private Shell shell;
	private Button[] buttons = null;
	private String value;

	public static final int ERROR = 1;
	public static final int INFO = 2;
	public static final int WARN = 3;

	public OptionShell(Shell parent, String title, String message,
			String[] options, int shellType) {

		shell = new Shell(parent, SWT.SHELL_TRIM);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 20;
		layout.marginHeight = 20;
		shell.setLayout(layout);
		shell.setText(title);

		GridData gridData = null;

		CLabel label = new CLabel(shell, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);

		label.setText(message);
		switch (shellType) {
		case ERROR:
			label.setImage(ResourceManager.getImage("error.png"));
			break;
		case INFO:
			label.setImage(ResourceManager.getImage("infomation.png"));
			break;
		case WARN:
			label.setImage(ResourceManager.getImage("warning.png"));
		}
		label.setLayoutData(gridData);

		Composite buttonGroup = new Composite(shell, SWT.None);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		RowLayout rowLayout = new RowLayout();
		buttonGroup.setLayout(rowLayout);
		buttonGroup.setLayoutData(gridData);

		buttons = new Button[options.length];
		for (int i = 0; i < options.length; i++) {
			buttons[i] = new Button(buttonGroup, SWT.PUSH);
			buttons[i].setText(options[i]);
			buttons[i].setData("index", new Integer(i));

			buttons[i].addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent event) {
					event.doit = false;
					Button button = (Button) event.widget;
					int index = ((Integer) button.getData("index")).intValue();
					if (event.character == 'h') {
						if (index > 0)
							buttons[index - 1].setFocus();
					}
					if (event.character == 'l') {
						if (index < buttons.length - 1)
							buttons[index + 1].setFocus();
					}

				}
			});
			buttons[i].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					Button button = (Button) event.widget;
					value = button.getText();
					shell.close();
					shell.dispose();
				}
			});
		}
		shell.pack();
	}

	public Point getSize() {
		return shell.getSize();
	}

	

	public String open(int x, int y) {

		shell.setLocation(x, y);
		shell.open();
		shell.setActive();
		buttons[0].setFocus();

		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		return value;

	}

}
