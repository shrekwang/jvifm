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
import net.sf.jvifm.ui.Messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;



public class AboutShell  {

	
	
	private Composite container;
	private Label lblCopyRight;
	private Label lblName;
	private Label lblPic;
	private Label lblDescription;
	private Button btnClose;
	private Link linkWebsite;

	public void showGUI() {
		Display display = Display.getDefault();
		final Shell shell = new Shell(display,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	
	
		shell.setLayout(new FillLayout());
		{
			container = new Composite(shell, SWT.NONE);
			GridLayout composite1Layout = new GridLayout();
			container.setLayout(composite1Layout);
	
			{
				lblPic = new Label(container, SWT.WRAP);
				GridData label3LData = new GridData();
				label3LData.verticalAlignment = GridData.BEGINNING;
				label3LData.horizontalAlignment = GridData.CENTER;
				label3LData.widthHint = 50;
				label3LData.heightHint = 50;
				lblPic.setLayoutData(label3LData);
				lblPic.setImage(ResourceManager.getImage("drawer48x48.png"));
			}
			{
				lblName = new Label(container, SWT.WRAP);
				lblName.setText("Jvifm 0.9b");
				GridData label4LData = new GridData();
				label4LData.verticalAlignment = GridData.BEGINNING;
				label4LData.horizontalAlignment = GridData.CENTER;
				lblName.setLayoutData(label4LData);
				lblName.setFont(ResourceManager.getFont("Sans", 12, 1, false, false));
			}
			{
				lblDescription = new Label(container, SWT.WRAP);
				lblDescription.setText("Jvifm is a file manager with vi key bindings.");
				GridData label2LData = new GridData();
				lblDescription.setLayoutData(label2LData);
			}
			{
				lblCopyRight = new Label(container, SWT.WRAP);
				lblCopyRight.setText("Copyright 2006-2008 by shrek wang");
				GridData label1LData = new GridData();
				label1LData.verticalAlignment = GridData.BEGINNING;
				label1LData.horizontalAlignment = GridData.CENTER;
				lblCopyRight.setLayoutData(label1LData);
			}
			{
				linkWebsite = new Link(container, SWT.NONE);
				linkWebsite.setText("<a href=\"http://jvifm.sf.net\">http://jvifm.sf.net</a>");
				GridData link1LData = new GridData();
				link1LData.verticalAlignment = GridData.BEGINNING;
				link1LData.horizontalAlignment = GridData.CENTER;
				linkWebsite.setLayoutData(link1LData);
			}
			{
				btnClose = new Button(container, SWT.PUSH | SWT.CENTER);
				btnClose.setText(Messages.getString("Messagebox.close"));
			
				GridData button1LData = new GridData();
				button1LData.horizontalAlignment = GridData.END;
				btnClose.setLayoutData(button1LData);
				btnClose.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						shell.dispose();
					}
				});
				
			}
		}
		shell.layout();
		shell.pack();
		Point location = new Point(display.getBounds().width / 2
				- shell.getSize().x / 2, display.getBounds().height / 2
				- shell.getSize().y / 2);
		shell.setLocation(location);
		shell.open();
		btnClose.setFocus();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	

	

}
