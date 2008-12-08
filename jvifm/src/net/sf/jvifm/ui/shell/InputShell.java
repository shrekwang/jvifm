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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class InputShell {
    private Shell shell;
    private Text text;
    private String value;
    private boolean isOk=false;

    public String open(Shell parent,int x, int y, String defaultValue) {

	//shell=new Shell(parent , SWT.NO_TRIM | SWT.PRIMARY_MODAL);
	shell=new Shell(parent ,  SWT.PRIMARY_MODAL);
	shell.setLayout(new FillLayout());
	text=new Text(shell,SWT.NONE);
	//text.setBackground(ResourceManager.getInstance().getColor(ResourceManager.INFO_COLOR));
    int width;	
	if (defaultValue!=null){
		GC gc=new GC(text);
		width=gc.stringExtent(defaultValue).y+80;
		text.setText(defaultValue);
		text.setSelection(0,defaultValue.length());
	} else {
		width=100;
	}
	text.addKeyListener(new KeyAdapter() {
	    public void keyPressed(KeyEvent event) {
			if (event.character==SWT.CR) {
				isOk=true;
				shell.close();
		    }
		   
	    }
	});
	text.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent arg0) {
			value=text.getText();
		}
	});

	shell.setSize(width,24);
	shell.open();
	shell.setLocation(x,y);
	Display display=shell.getDisplay();
	while (!shell.isDisposed()) {
	    if (!display.readAndDispatch()) display.sleep();
	}
	if (isOk)	return value;
	return null;
    }


}
