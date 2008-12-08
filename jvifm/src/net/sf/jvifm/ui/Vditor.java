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

import java.io.BufferedReader;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Vditor extends Composite {
	private static String MODE_NORMAL="normal";
	private static String MODE_INSERT="insert";
	private static String MODE_VISUAL="visual";
	
	private StyledText text;
	
	public static void main(String[] args) {
		Display display=new Display();
		Shell shell=new Shell(display);
		shell.setLayout(new FillLayout());
		Vditor vd=new Vditor(shell);
		
		shell.setSize(400,300);
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}
	
	public Vditor(Composite parent) {
		super(parent, SWT.NONE);
		super.setLayout(new FillLayout());
		text = new StyledText(this,  SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		
		text.setData("mode",MODE_NORMAL);

		text.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.keyCode == SWT.ESC) {
					doCancel();
				}
				switch (event.character) {
				case 'k': break;
				}
			}
		});
		
		text.addVerifyKeyListener(new VerifyKeyListener() {
			public void verifyKey(VerifyEvent event) {
				if (event.keyCode == SWT.ESC) {
					text.setData("mode",MODE_NORMAL);
				}
				String mode=(String)text.getData("mode");
				if (mode.equals(MODE_INSERT)) {
					return ;
				}
				if (mode.equals(MODE_NORMAL)) {
					if (event.character=='i') {
						text.setData("mode",MODE_INSERT);
						
					}
					event.doit=false;
				}
				
				
			}
		});
		
	}
	public void doCancel() {
	}
	public void open(BufferedReader reader) {
		StringBuffer sb = new StringBuffer();
		try {
			String tmp = "";
			while (true) {
				tmp = reader.readLine();
				if (tmp == null)
					break;
				sb.append(tmp + "\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		text.setText(sb.toString());
	}
	public int getCurrentLine() {
		return  text.getLineAtOffset(text.getSelectionRange().x);
	}
	public void cursorLeft() {
		text.setSelection(text.getSelection().x-1);
		
	}
	public void cursorRight() {
		text.setSelection(text.getSelection().x+1);
	}
	public void cusorDown() {
	}
	public void cursorUp() {
	}
	public void cursorTop() {
	}
	public void cursorBottom() {
	}
	public void cursorMiddle() {
	}

}


