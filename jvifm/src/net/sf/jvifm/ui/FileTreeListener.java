package net.sf.jvifm.ui;

import java.io.File;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.TreeItem;

public class FileTreeListener extends ViKeyListener {
	private FileTree fileTree;

	public FileTreeListener(FileTree fileTree) {
		super(fileTree);
		this.fileTree=fileTree;
	}
	
	public void keyPressed(KeyEvent event) {
		super.keyPressed(event);
		switch (event.character) {
		case 'h':
			fileTree.selectParentDir();
			break;
		case 'i':
			fileTree.filterView();
			break;
		case 'u':
			fileTree.buildRootNode(null);
			break;
		case 'B':
			fileTree.listBookMarks();
			break;
		case 'o':
			fileTree.openWithDefault();
		}
	}

	@Override
	protected void doAction() {
		super.doAction();
		String cmd=getCmd();
		
		if (cmd.equals("zc")) {
			fileTree.collapseItem();
		} else if (cmd.equals("gj")) {
			fileTree.cursorNextSibling();
		} else if  (cmd.equals("gk")) {
			fileTree.cursorPrevSibling();
		}
	}
	

}
