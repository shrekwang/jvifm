
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



import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

public class JobStatus   {
	private Composite container;
	private ProgressBar progressBar;
	private Label lblStatus;
	

	public JobStatus(Composite parent,int style) {
		
		GridLayout gridLayout=new GridLayout();
		gridLayout.numColumns=4;
		gridLayout.marginRight=40;
		gridLayout.marginLeft=20;
		gridLayout.marginHeight=0;
		gridLayout.verticalSpacing=0;
		container=new Composite(parent,SWT.NONE);
		
		container.setLayout(gridLayout);
		
		
		progressBar=new ProgressBar(container , SWT.INDETERMINATE|SWT.NONE);
		
		lblStatus=new Label(container,SWT.NONE);
		lblStatus.setText("faint");
		lblStatus.setAlignment(SWT.RIGHT);
		GridData gridData=new GridData();
		gridData.horizontalAlignment=GridData.FILL;
		gridData.grabExcessHorizontalSpace=true;
		lblStatus.setLayoutData(gridData);
		
		progressBar.setVisible(false);
		
	}
	public void show() {
		progressBar.setVisible(true);
	}
	public void hide() {
		progressBar.setVisible(false);
		lblStatus.setText("");
	}
	public void updateStatus(String status) {
		lblStatus.setText(status);
	}
	
	public void setLayoutData(Object data) {
		container.setLayoutData(data);
	}
}


