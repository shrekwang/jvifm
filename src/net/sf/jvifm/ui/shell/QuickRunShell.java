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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.jvifm.Main;
import net.sf.jvifm.control.Command;
import net.sf.jvifm.control.CommandRunner;
import net.sf.jvifm.control.SystemCommand;
import net.sf.jvifm.model.Shortcut;
import net.sf.jvifm.model.ShortcutsManager;
import net.sf.jvifm.model.TipOption;
import net.sf.jvifm.ui.FileLister;
import net.sf.jvifm.ui.FileManager;
import net.sf.jvifm.util.AutoCompleteUtil;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class QuickRunShell {

	private StyledText txtCommand;
	private CommandRunner commandRunner = CommandRunner.getInstance();
	private Shell shell;
	private Display display;
	private CompletionShell completionShell;
	private TipOption[] completeOptions = null;
	private int currentOptionIndex = -1;
	private String pwd = null;
	private Color color = null;
	private boolean needRefreshOptions = true;

	public QuickRunShell() {
		this.pwd = Main.fileManager.getActivePanel().getPwd();
	}

	public void open() {

		initGUI();
		initListener();
		shell.open();
		shell.setFocus();

	}

	public void initListener() {

		txtCommand.addVerifyKeyListener(new VerifyKeyListener() {
			public void verifyKey(VerifyEvent e) {
				if (e.character == SWT.CR) {
					e.doit = false;
					if (currentOptionIndex == -1)
						currentOptionIndex = 0;
					boolean success = doCommand();
					if (success) {
						if (completionShell != null) {
							completionShell.dispose();
							completionShell = null;
						}
						shell.close();
						return;
					}
				}
				if (e.character == SWT.TAB) {
					e.doit = false;
					doComplete();

					return;
				}
				if (e.character == '/') {
					if (Main.operatingSystem == Main.WINDOWS) {
						e.doit = false;
						String tmpText = txtCommand.getText() + "\\";
						needRefreshOptions = true;
						txtCommand.setText(tmpText);
						txtCommand.setCaretOffset(tmpText.length());
						return;
					}
				}
				if (e.keyCode == SWT.ESC || (e.keyCode=='[' && e.stateMask == SWT.CTRL)) {
					e.doit = false;
					cancelOperate();
				}
				needRefreshOptions = true;
			}
		});
		txtCommand.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				if (!needRefreshOptions) return;
				changeCompletion(txtCommand.getText());
			}
		});
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				if (color != null)
					color.dispose();
			}
		});
	}
	
	private void cancelOperate() {
		if (txtCommand.getSelectionText().equals(
				txtCommand.getText())
				|| txtCommand.getText().equals("")) {
			if (completionShell != null) {
				completionShell.dispose();
				completionShell = null;
			}
			shell.close();
		} else {
			txtCommand.setSelection(0, txtCommand.getText()
					.length());
		}
		return;
	}
	
	private void changeCompletion(String txt) {
		completeOptions = getCompletionOptions(txt);
		currentOptionIndex = -1;
		// if (completeOptions==null) return;
		int x = shell.getLocation().x;
		int y = shell.getLocation().y + shell.getSize().y;

		if (completionShell == null) {
			completionShell = new CompletionShell(completeOptions, x, y);
			completionShell.open();
			txtCommand.setFocus();
		} else {
			completionShell.setOptions(completeOptions);
			txtCommand.setFocus();
		}
	}

	@SuppressWarnings("unchecked")
	private List buildPathOptions(String[] options) {
		ArrayList list = new ArrayList();

		if (options != null) {
			for (int i = 0; i < options.length; i++) {
				File file = new File(options[i]);
				TipOption tipOption = new TipOption();
				tipOption.setExtraInfo(file.getParent());
				if (file.isDirectory()) {
					tipOption.setTipType("dir");
				} else {
					tipOption.setTipType("file");
				}
				tipOption.setName(file.getName());
				list.add(tipOption);

			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private void addOptions(HashMap optionMap, List options) {
		for (Iterator it = options.iterator(); it.hasNext();) {
			TipOption option = (TipOption) it.next();
			String key = option.getName() + "-" + option.getExtraInfo();
			optionMap.put(key, option);
		}
	}

	@SuppressWarnings("unchecked")
	public TipOption[] getCompletionOptions(String text) {

		HashMap optionMap = new HashMap();
		String[] options = AutoCompleteUtil.getFileCompleteOptions(pwd, text,
				false);
		addOptions(optionMap, buildPathOptions(options));

		options = AutoCompleteUtil.getBookmarkFileOptions(text);
		addOptions(optionMap, buildPathOptions(options));

		ArrayList list = new ArrayList();
		Shortcut[] links = AutoCompleteUtil.getShortcutsCompleteList2(text);
		if (links != null) {
			for (int i = 0; i < links.length; i++) {
				TipOption tipOption = new TipOption();
				tipOption.setName(links[i].getName());
				tipOption.setExtraInfo(links[i].getText());
				tipOption.setTipType("shortcut");
				list.add(tipOption);
			}
		}
		addOptions(optionMap, list);

		TipOption[] result = new TipOption[optionMap.size()];
		int i = 0;
		for (Iterator it = optionMap.values().iterator(); it.hasNext();) {
			result[i++] = (TipOption) it.next();

		}
		return result;
	}

	public void doComplete() {
		needRefreshOptions = false;
		currentOptionIndex++;
		if (currentOptionIndex > completeOptions.length - 1)
			currentOptionIndex = 0;
		
		if (completeOptions.length == 1 && completeOptions[0].getTipType().equals("dir")) {
			String tmpText = getCompletionText()+File.separator;
			txtCommand.setText(tmpText);
			txtCommand.setCaretOffset(tmpText.length());
			changeCompletion(tmpText);
			
		} else {
			String tmpText = getCompletionText();
			if (tmpText != null) {
				txtCommand.setText(tmpText);
				txtCommand.setCaretOffset(tmpText.length());
			}

			if (completionShell != null) {
				completionShell.setOptionIndex(currentOptionIndex);
			}
			
		}

	}

	private String getCompletionText() {
		String tmpText = null;

		if (completeOptions == null || completeOptions.length < 1)
			return null;

		TipOption currentOption = completeOptions[currentOptionIndex];

		String path = currentOption.getExtraInfo();
		if (currentOption.getTipType().equals("shortcut")) {
			return currentOption.getExtraInfo();
		}

		if (path.indexOf(pwd) == 0 && ! pwd.equals(FileLister.FS_ROOT)) {
			if (path.equalsIgnoreCase(pwd) || path.endsWith(":\\")) {
				tmpText = currentOption.getName();
			} else if (pwd.endsWith(File.separator)) {
				tmpText = path.substring(pwd.length()) + File.separator
						+ currentOption.getName();
			} else {
				tmpText = path.substring(pwd.length() + 1) + File.separator
						+ currentOption.getName();
			}
		} else {
			if (path.endsWith(File.separator)) {
				tmpText = path + currentOption.getName();
			} else {
				tmpText = path + File.separator + currentOption.getName();
			}

		}
		return tmpText;
	}

	public boolean doCommand() {
		String cmd = null;
		String[] args = new String[] {};
		/*
		if (completeOptions != null && completeOptions.length > 0) {
			cmd = getCompletionText();
		} else {
		*/
		
		cmd = txtCommand.getText();
		if (cmd.indexOf("\"") > 0) { // arg
			String arg = cmd.substring(cmd.indexOf("\"") + 1, cmd
					.lastIndexOf("\""));
			cmd = cmd.substring(0, cmd.indexOf("\"")).trim();
			args = new String[] { arg };
		}

		ShortcutsManager scm = ShortcutsManager.getInstance();
		if (scm.isShortCut(cmd)) {
			Shortcut cc = scm.findByName(cmd);
			Command command = new SystemCommand(cc.getText(), args, true);
			command.setPwd(Main.fileManager.getActivePanel().getPwd());
			commandRunner.run(command);
			return true;
		}

		String newPath = FilenameUtils.concat(pwd, cmd);
		newPath=FilenameUtils.normalizeNoEndSeparator(newPath);
		
        File file = null;
        try {
            file = new File(newPath);
        } catch (Exception e ) {
            return false;
        }
		if (file !=null && file.exists()) {
			if (file.isFile()) {
				// Util.openFileWithDefaultApp(newPath);
				Command command = new SystemCommand(newPath, args, true);
				command.setPwd(Main.fileManager.getActivePanel().getPwd());
				commandRunner.run(command);
				return true;
			}

			FileManager fileManager = Main.fileManager;
			FileLister activeLister = fileManager.getActivePanel();
			fileManager.activeGUI();
			activeLister.visit(newPath);
			return true;
		}
		return false;
	}

	public void initGUI() {

		display = Display.getDefault();
		shell = new Shell(display, SWT.ON_TOP);

		GridLayout thisLayout = new GridLayout();
		thisLayout.makeColumnsEqualWidth = false;
		thisLayout.marginHeight = 2;
		thisLayout.marginWidth = 3;
		thisLayout.horizontalSpacing = 2;
		thisLayout.numColumns = 1;

		color = new Color(shell.getDisplay(), 248, 248, 180);
		shell.setLayout(thisLayout);
		shell.setBackground(color);
		GridData gridData = null;
		
		txtCommand = new StyledText(shell, SWT.NONE);
		txtCommand.setBackground(color);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = 300;
		txtCommand.setLayoutData(gridData);


		shell.pack();

		Point size = shell.getSize();

		int width = display.getBounds().width;
		int height = display.getBounds().height;

		int offsetX = (width - size.x) / 2;
		int offsetY = (height - size.y) / 2;
		shell.setLocation(offsetX, offsetY);
		shell.layout();
	}

}
