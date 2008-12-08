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

import net.sf.jvifm.Main;
import net.sf.jvifm.model.Preference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class PreferenceShell {
	private Shell shell;

	private TabFolder tabFolder;

	private Text txtTerminal;

	private Text txtEditor;

	private Button btnShowHide;

	private Preference preference = Preference.getInstance();

	public void open(Shell parent) {
		shell = new Shell(parent);
		init();
		shell.pack();
		shell.setText("setting");
		shell.open();
	}

	private void init() {

		GridLayout layoutMain = new GridLayout();
		layoutMain.marginWidth = 10;
		layoutMain.marginHeight = 10;
		shell.setLayout(layoutMain);
		shell.setLocation(200, 200);

		GridData gridData = new GridData();
		gridData.widthHint = 300;
		gridData.heightHint = 200;

		tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setLayoutData(gridData);

		TabItem appItem = new TabItem(tabFolder, SWT.NONE);

		Composite appSettingGroup = new Composite(tabFolder, SWT.NONE);
		GridLayout gridlayout = new GridLayout();
		gridlayout.numColumns = 3;
		appSettingGroup.setLayout(gridlayout);

		Label label = new Label(appSettingGroup, SWT.NONE);
		label.setText("Editor:");
		txtEditor = new Text(appSettingGroup, SWT.BORDER);
		txtEditor.setText(preference.getEditorApp());

		txtEditor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button btnOpenFileDialg1 = new Button(appSettingGroup, SWT.PUSH);
		btnOpenFileDialg1.setText("..");
		btnOpenFileDialg1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				String filename = fd.open();
				if (filename != null)
					txtEditor.setText(filename);
			}
		});

		Label label2 = new Label(appSettingGroup, SWT.NONE);
		label2.setText("Terminal:");
		txtTerminal = new Text(appSettingGroup, SWT.BORDER);
		txtTerminal.setText(preference.getTerminalApp());

		txtTerminal.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button btnOpenFileDialg2 = new Button(appSettingGroup, SWT.PUSH);
		btnOpenFileDialg2.setText("..");
		btnOpenFileDialg2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				String filename = fd.open();
				if (filename != null)
					txtTerminal.setText(filename);
			}
		});

		appItem.setText("app setting");
		appItem.setControl(appSettingGroup);

		TabItem miscItem = new TabItem(tabFolder, SWT.NONE);
		miscItem.setText("misc");

		Composite miscGroup = new Composite(tabFolder, SWT.NONE);
		miscGroup.setLayout(new GridLayout());

		btnShowHide = new Button(miscGroup, SWT.CHECK | SWT.BORDER);
		btnShowHide.setText("display the hidden files");

		if (preference.isShowHide()) {
			btnShowHide.setSelection(true);
		} else {
			btnShowHide.setSelection(false);
		}

		miscItem.setControl(miscGroup);

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		Composite footGroup = new Composite(shell, SWT.NONE);
		footGroup.setLayoutData(gridData);
		gridlayout = new GridLayout();
		gridlayout.numColumns = 2;
		footGroup.setLayout(gridlayout);

		Button btnOk = new Button(footGroup, SWT.PUSH);
		btnOk.setText("Ok");
		btnOk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				String editor = txtEditor.getText();
				String terminal = txtTerminal.getText();
				if (editor != null && !editor.trim().equals("")) {
					preference.setEditorApp(editor);
				}
				if (terminal != null && !terminal.trim().equals("")) {
					preference.setTerminalApp(terminal);
				}
				preference.setShowHide(btnShowHide.getSelection());
				preference.save();
				Main.fileManager.refresh();
				shell.close();
			}
		});
		final Button btnCancel = new Button(footGroup, SWT.PUSH);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				shell.close();
			}
		});
	}

}
