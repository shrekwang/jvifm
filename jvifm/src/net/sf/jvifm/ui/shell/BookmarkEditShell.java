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

import net.sf.jvifm.model.Bookmark;
import net.sf.jvifm.model.BookmarkManager;
import net.sf.jvifm.ui.Messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class BookmarkEditShell {
	private Shell shell;
	private Bookmark bookmark;
	private boolean isChanged=false;
	
	public BookmarkEditShell(Bookmark bookmark) {
		this.bookmark=bookmark;
	}
	public boolean open(Shell parent) {
		
		shell = new Shell(parent);
		init();
		shell.pack();
		Point size=shell.getSize();
		
		Point location=parent.getLocation();
		
		int offsetX=location.x+(parent.getSize().x/2)-(size.x/2);
		int offsetY=location.y+(parent.getSize().y/2)-(size.y/2);
		shell.setLocation(offsetX, offsetY);
		
		shell.setText("Bookmark Edit");
		shell.open();
		
	    Display display=shell.getDisplay();	
		while (!shell.isDisposed()) {
		    if ( ! display.readAndDispatch() ) display.sleep();
		}

		return isChanged;
	}
	
	private void init() {
		GridLayout layoutMain = new GridLayout();
		layoutMain.marginWidth = 5;
		layoutMain.marginHeight = 5;
		shell.setLayout(layoutMain);
		shell.setLocation(200, 200);

		GridData gridData = new GridData();
		gridData.widthHint = 300;
		gridData.heightHint = 200;
		
		Composite mainContainer = new Composite(shell, SWT.NONE);
		GridLayout gridlayout = new GridLayout();
		gridlayout.numColumns = 3;
		mainContainer.setLayout(gridlayout);

		Label label = new Label(mainContainer, SWT.NONE);
		label.setText(Messages.getString("BookmarkLister.name"));
		
		final Text txtName = new Text(mainContainer, SWT.BORDER);
		txtName.setText(bookmark.getName());
	

		gridData=new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint=200;
		txtName.setLayoutData(gridData);
		new Label(mainContainer,SWT.NONE).setText("");
		

		Label label2 = new Label(mainContainer, SWT.NONE);
		label2.setText(Messages.getString("BookmarkLister.path"));
		
		final Text txtLocation = new Text(mainContainer, SWT.BORDER);
		txtLocation.setText(bookmark.getPath());
		txtLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button btnOpenFileDialg2 = new Button(mainContainer, SWT.PUSH);
		btnOpenFileDialg2.setText("..");
		btnOpenFileDialg2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dd=new DirectoryDialog(shell,SWT.OPEN);
				String path = dd.open();
				if (path != null)
					txtLocation.setText(path);
			}
		});
		
		Label label3 = new Label(mainContainer, SWT.NONE);
		label3.setText("key");
		
		final Text txtKey = new Text(mainContainer, SWT.BORDER);
		if (bookmark.getKey()!=null) txtKey.setText(bookmark.getKey());
		txtKey.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtKey.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.character=='\b' || e.character==SWT.DEL)  return;
				e.doit=false;
				if (e.character >'a' && e.character<'z' ) {
					txtKey.setText(String.valueOf(e.character));
				}
			}
		});
		
			
					
			
		
		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		Composite footGroup = new Composite(shell, SWT.NONE);
		footGroup.setLayoutData(gridData);
		gridlayout = new GridLayout();
		gridlayout.numColumns = 2;
		footGroup.setLayout(gridlayout);

		Button btnOk = new Button(footGroup, SWT.PUSH);
		btnOk.setText(Messages.getString("Messagebox.optionOk"));
		btnOk.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent arg0) {
				bookmark.setName(txtName.getText());
				bookmark.setPath(txtLocation.getText());
				if (txtKey.getText().trim().equals("")){
					bookmark.setKey(null);
				} else {
					
					String oldKey=bookmark.getKey();
					BookmarkManager bm=BookmarkManager.getInstance();
					bm.changeBookmarkKey(oldKey, txtKey.getText().trim(), bookmark);
					
					bookmark.setKey(txtKey.getText().trim());
				}
				isChanged=true;
				shell.close();
			}
		});
		
		final Button btnCancel = new Button(footGroup, SWT.PUSH);
		btnCancel.setText(Messages.getString("Messagebox.optionCancel"));
		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				shell.close();
			}
		});
		
	}

}


