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
import net.sf.jvifm.model.Shortcut;
import net.sf.jvifm.model.ShortcutsListener;
import net.sf.jvifm.model.ShortcutsManager;
import net.sf.jvifm.ui.BasicViLister;
import net.sf.jvifm.ui.FileLister;
import net.sf.jvifm.ui.Messages;
import net.sf.jvifm.ui.Util;
import net.sf.jvifm.ui.shell.OptionShell;
import net.sf.jvifm.ui.shell.ShortcutsEditShell;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class ShortcutsLister extends BasicViLister implements
		ShortcutsListener {


	private Image runImage = ResourceManager.getImage("system-run.png");
	private TableEditor editor;
	private ShortcutsManager shortcutsManager = ShortcutsManager.getInstance();

	public ShortcutsLister(Composite parent, int style) {
		super(parent, style);
		init();
		shortcutsManager.addListener(this);

	}

	public void onChangeshortcut(Shortcut shortcuts) {

		int rowCount = table.getItemCount();
		for (int i = 0; i < rowCount; i++) {
			String txt = table.getItem(i).getText();
			table.getItem(i).setText(shortcuts.getName());
			table.getItem(i).setData("shortcuts", shortcuts);
			currentRow = i;
			table.setSelection(i);
			break;
		}

	}

	public void onAddshortcut(Shortcut shortcuts) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setImage(runImage);
		item.setData("shortcuts", shortcuts);
		item.setText(shortcuts.getName());

		currentRow = table.getItemCount() - 1;
		table.setSelection(currentRow);

	}

	private void init() {
		TableItem item;

		for (Iterator it = shortcutsManager.iterator(); it.hasNext();) {
			Shortcut command = (Shortcut) it.next();
			item = new TableItem(table, SWT.NONE);
			item.setImage(runImage);
			item.setData("shortcuts", command);
			item.setText(command.getName());
		}
		table.setSelection(0);
	}

	public void reloadshortcuts() {
		init();
	}

	public void editshortcutsName() {
		editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 20;
		Control oldEditor = editor.getEditor();
		if (oldEditor != null)
			oldEditor.dispose();

		TableItem item = table.getItem(currentRow);
		if (item == null)
			return;

		final Text newEditor = new Text(table, SWT.NONE);
		Shortcut shortcuts = (Shortcut) item.getData("shortcuts");
		newEditor.setText(shortcuts.getName());
		newEditor.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent arg0) {
				newEditor.dispose();
			}
		});
		newEditor.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.keyCode == SWT.CR
						&& !newEditor.getText().trim().equals("")) {

					TableItem item = table.getItem(currentRow);
					Shortcut shortcuts = (Shortcut) item.getData("shortcuts");
					item.setText(newEditor.getText().trim());
					shortcuts.setName(newEditor.getText().trim());

					newEditor.dispose();
					table.setFocus();
				}
				if (event.character == SWT.ESC) {
					newEditor.dispose();
					table.setFocus();
				}
			}

		});
		newEditor.selectAll();
		newEditor.setFocus();
		editor.setEditor(newEditor, item, 0);
	}

	public void enterPath() {
		TableItem item = table.getItem(currentRow);
		if (item == null)
			return;
		Shortcut cmd = (Shortcut) item.getData("shortcuts");
		File file = new File(cmd.getText());
		String ext = FilenameUtils.getExtension(file.getName());
		Program program = Program.findProgram(ext);

		if (ext.equals("bat") || ext.equals("sh") || program == null) { //$NON-NLS-1$ //$NON-NLS-2$
			try {
				Runtime.getRuntime().exec(new String[] { file.getPath() },
						null, file.getParentFile());
			} catch (Exception e) {
			}
		} else {
			Program.launch(file.getPath());
		}

	}

	public void cancelOperate() {
		if (this.operateMode == Mode.NORMAL) {
			FileLister fileLister = Main.fileManager.getActivePanel();
			fileLister.active();
		} else {
			super.cancelOperate();
		}
	}

	public void doPaste() {

		CommandBuffer commandBuffer = CommandBuffer.getInstance();
		String[] names = commandBuffer.getCommandSourceFiles();

		for (int i = 0; i < names.length; i++) {
			File file = new File(names[i]);
			if (file.isDirectory())
				continue;
			Shortcut shortcuts = new Shortcut();
			shortcuts.setName(file.getName());
			shortcuts.setText(file.getPath());
			ShortcutsManager scm = ShortcutsManager.getInstance();
			scm.add(shortcuts);

		}
		currentRow = table.getItemCount() - 1;

	}

	public void doDelete() {
		int[] selectionIndices = table.getSelectionIndices();
		if (selectionIndices == null || selectionIndices.length < 0)
			return;

		String[] options = new String[] {
				Messages.getString("Messagebox.optionYes"), //$NON-NLS-1$
				Messages.getString("Messagebox.optionNo"), //$NON-NLS-1$
				Messages.getString("Messagebox.optionCancel") }; //$NON-NLS-1$ 

		String result = new Util().openConfirmWindow(options,
				Messages.getString("shortcutsLister.warnDialogTitle"), //$NON-NLS-1$
				Messages.getString("shortcutsLister.warnDialogMessage"),
				OptionShell.WARN); //$NON-NLS-1$ 

		if (result == null
				|| (!result.equals(Messages.getString("Messagebox.optionYes"))))
			return;

		ShortcutsManager scm = ShortcutsManager.getInstance();
		for (int i = 0; i < selectionIndices.length; i++) {
			Shortcut shortcuts = (Shortcut) table.getItem(selectionIndices[i])
					.getData("shortcuts");
			scm.remove(shortcuts);
		}
		table.remove(selectionIndices);

		if (currentRow >= 0 && currentRow <= table.getItemCount() - 1) {
			table.setSelection(currentRow);
		} else if (currentRow > table.getItemCount() - 1) {
			currentRow = table.getItemCount() - 1;
			table.setSelection(currentRow);
		}

	}

	public void dispose() {
		super.dispose();
		shortcutsManager.removeListener(this);
	}

	public void doChange() {
		TableItem item = table.getItem(currentRow);
		if (item == null)
			return;
		Shortcut shortcuts = (Shortcut) item.getData("shortcuts");
		ShortcutsEditShell cceShell = new ShortcutsEditShell(shortcuts);
		boolean isChanged = cceShell.open(Main.shell);

		if (isChanged) {
			item.setText(shortcuts.getName());
		}
	}

}
