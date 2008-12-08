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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class HelpContentShell {
	
	public void showGUI() {
		Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		
		shell.setLayout(new FillLayout());
		StyledText 	text = new StyledText(shell, SWT.READ_ONLY |   SWT.V_SCROLL | SWT.BORDER);
		text.setText(readHelpFile());
	
		shell.setMaximized(true);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	private String readHelpFile() {
		BufferedReader br=new BufferedReader(new 
				InputStreamReader(HelpContentShell.class
				.getClassLoader().getResourceAsStream("net/sf/jvifm/ui/help.txt")));
		StringBuffer sb = new StringBuffer();
		try {
			String tmp = "";
			while (true) {
				tmp = br.readLine();
				if (tmp == null)
					break;
				sb.append(tmp + "\n");
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}
