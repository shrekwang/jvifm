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

import net.sf.jvifm.Main;
import net.sf.jvifm.model.Preference;
import net.sf.jvifm.ui.shell.OptionShell;
import net.sf.jvifm.ui.shell.PreferenceShell;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class Util {

	private String result;

	public static void openMessageWindow(final String message) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				MessageBox box = new MessageBox(Main.fileManager.getShell(),
						SWT.NONE);
				if (message == null) {
					box.setMessage("unknow error");
				} else {
					box.setMessage(message);
				}
				box.open();
			}
		});
	}

	public static void openPreferenceShell(final Shell shell) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				new PreferenceShell().open(shell);
			}
		});
	}

	public String openConfirmWindow(final String[] option, final String title,
			final String message, final int shellType) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Shell shell = Main.fileManager.getShell();
				OptionShell optionShell = new OptionShell(shell, title,
						message, option, shellType);
				Point optionSize = optionShell.getSize();
				Point location = shell.getLocation();
				
				int offsetX = location.x + (shell.getSize().x / 2) - (optionSize.x / 2);
				int offsetY = location.y + (shell.getSize().y / 2) - (optionSize.y / 2);
				
				result = optionShell.open(offsetX, offsetY);
			}
		});
		return result;

	}

	public static void openTerminal(String path) {
		String ENV_OS = System.getProperty("os.name");
		String terminal_exe = "";
		if (ENV_OS.substring(0, 3).equalsIgnoreCase("win")) {
			terminal_exe = "cmd /c start";
		} else {
			terminal_exe = "gnome-terminal";
		}

		try {
			Runtime.getRuntime().exec(terminal_exe, null, new File(path));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void openFileWithDefaultApp(String path) {
		if (path == null)
			return;
		String ext = FilenameUtils.getExtension(path);
		File file = new File(path);
		if (ext.equals("bat") || ext.equals("sh")) {
			try {
				Runtime.getRuntime().exec(new String[] { path }, null,
						file.getParentFile());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Program.launch(path);
		}

	}
	
	public static void editFile(String pwd, String[] paths) {
		Preference preference = Preference.getInstance();
		String EDITOR = preference.getEditorApp();
		String[] cmd = new String[paths.length + 5];
		cmd[0] = EDITOR;
		cmd[1] = "--servername";
		cmd[2] = "JVIFM";
		cmd[3] = "-p";
		cmd[4] = "--remote-tab-silent";
		System.arraycopy(paths, 0, cmd, 5, paths.length);

		try {
			Runtime.getRuntime().exec(cmd, null, new File(pwd));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void editFile(String pwd, String path) {
		Preference preference = Preference.getInstance();
		String EDITOR = preference.getEditorApp();
		File file = new File(path);
		if (file.isFile() && file.canRead()) {
			String ext = FilenameUtils.getExtension(path);
			if (ext.equals("zip") || ext.equals("jar") || ext.equals("war")) { //$NON-NLS-1$
				
				Util.openFileWithDefaultApp(path);
				//Main.fileManager.zipTabNew(path);
			} else {
				try {
					String param1 = "-p";
					String param2 = "--remote-tab-silent";
					String param3 = "--servername";
					String param4 = "JVIFM";

					String cmd[] = { EDITOR, param3, param4, param1, param2,
							path };
					// String cmd[]={EDITOR , path};
					Runtime.getRuntime().exec(cmd, null, new File(pwd));
				} catch (Exception e) {
					Util.openFileWithDefaultApp(path);
				}
			}
		}

	}
	
	public static IOFileFilter getDefaultDirFilter() {
		IOFileFilter filter = new AndFileFilter(
				getDefaultFileFilter(), DirectoryFileFilter.DIRECTORY);
		return filter;
	}
	
	public static IOFileFilter getDefaultFileFilter() {
		
		IOFileFilter filter;

		Preference preference = Preference.getInstance();
		if (preference.isShowHide()) {
			filter = TrueFileFilter.INSTANCE;
		} else {
			filter = HiddenFileFilter.VISIBLE;
		}
		return filter;
	}

}
