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

package net.sf.jvifm.ui.viewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import net.sf.jvifm.Main;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class TextViewer implements Previewer {
	
	
	private StyledText text;
	
	public TextViewer(Composite composite, String filepath) {
		text=new StyledText(composite,SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.READ_ONLY);
		text.addVerifyKeyListener(new VerifyKeyListener() {
			public void verifyKey(VerifyEvent event) {
				if (event.character==' ') {
					Main.fileManager.changePanel();
				}
				if (event.character=='j') {
					//text.scroll(destX, destY, x, y, width, height, all)
				}
				
			}
		});
		loadFile(filepath);
	}
	
	public void loadFile(String filepath) {
		
		StringBuffer sb=new StringBuffer("");
		try {
			File file=new File(filepath);
			BufferedReader br=new BufferedReader(new FileReader(file));
			String tmp;
			while ((tmp=br.readLine())!=null) {
				sb.append(tmp).append("\n");
			}
			br.close();
		}catch (Exception e) {
			
		}
		
		text.setText(sb.toString());
	}
	
	public Control getControl() {
		return this.text;
	}
	public void dispose () {
		text.dispose();
	}
	public void active() {
		text.setFocus();
		text.setCaretOffset(0);
	}
	public void refresh() {
		text.redraw();
	}
	public void switchPanel() {
		Main.fileManager.changePanel();
	}
	
	
	

}
