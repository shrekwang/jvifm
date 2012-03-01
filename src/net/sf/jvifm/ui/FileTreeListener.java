package net.sf.jvifm.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class FileTreeListener extends ViKeyListener {
	private FileTree fileTree;

	public FileTreeListener(FileTree fileTree) {
		super(fileTree);
		this.fileTree=fileTree;
	}
	
	public void keyPressed(KeyEvent event) {
		super.keyPressed(event);
		if (event.keyCode==SWT.CR) {
			fileTree.showCurrentNodeInFileLister();
    		return;
    	}
		
		switch (event.character) {
		case 'h':
			fileTree.selectParentDir();
			break;
		case 'i':
			fileTree.filterView();
			break;
		case 'u':
			fileTree.backToRoot();
			break;
		case 'B':
			fileTree.listBookMarks();
			break;
		case 'o':
			fileTree.toggleExpanded();
		}
	}

	@Override
	protected void doAction() {
		super.doAction();
		String cmd=getCmd();
		if (cmd.equals("gj")) {
			fileTree.cursorNextSibling();
		} else if  (cmd.equals("gk")) {
			fileTree.cursorPrevSibling();
		}
	}
	

}
