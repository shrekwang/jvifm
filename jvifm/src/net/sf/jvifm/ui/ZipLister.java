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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import net.sf.jvifm.Main;
import net.sf.jvifm.ResourceManager;
import net.sf.jvifm.model.HistoryManager;
import net.sf.jvifm.model.Preference;
import net.sf.jvifm.ui.factory.GuiDataFactory;
import net.sf.jvifm.util.StringUtil;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ZipLister extends BasicViLister implements Panel {

	private String filename;

	private Image folderImage = ResourceManager.getImage("folder.png");

	private Image fileImage = ResourceManager.getImage("file.png");

	private StyledText textLocation;

	private Composite mainArea;

	private Button btnUpDir;

	private Button btnTopDir;

	private Preference preference = Preference.getInstance();

	private String EDITOR = preference.getEditorApp();

	private HashMap map = new HashMap();

	private FileObject currentFileObject = null;

	private HistoryManager historyManager = new HistoryManager();

	public ZipLister(Composite parent, int style, String zipfilename) {
		super(parent, style);
		this.filename = zipfilename;
		initData();
	}

	private void initData() {
		try {
			FileSystemManager fsManager = VFS.getManager();
			currentFileObject = fsManager.resolveFile("jar://" + filename);
			textLocation.setText(currentFileObject.getName().getPath());

		} catch (Exception e) {
			e.printStackTrace();
		}
		changeCurrentNode();

	}

	public Control getControl() {
		return this.mainArea;
	}

	public void active() {
		table.setFocus();
	}

	protected void initViLister() {

		this.setLayout(new FillLayout());
		fileImage = ResourceManager.getImage("file.png");
		folderImage = ResourceManager.getImage("folder.png");

		mainArea = new Composite(this, SWT.NONE);
		GridLayout layout = GuiDataFactory.createkGridLayout(1, 0, 0, 0, 0, true);
		mainArea.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		mainArea.setLayoutData(gridData);

		Composite headGroup = new Composite(mainArea, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		headGroup.setLayoutData(gridData);

		layout = GuiDataFactory.createkGridLayout(3, 0, 0, 0, 0, false);
		headGroup.setLayout(layout);

		btnUpDir = new Button(headGroup, SWT.PUSH);
		btnUpDir.setText(".."); //$NON-NLS-1$
		gridData = new GridData();
		btnUpDir.setLayoutData(gridData);

		btnTopDir = new Button(headGroup, SWT.PUSH);
		btnTopDir.setText("/"); //$NON-NLS-1$
		gridData = new GridData();
		btnTopDir.setLayoutData(gridData);

		textLocation = new StyledText(headGroup, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		textLocation.setLayoutData(gridData);

		table = new Table(mainArea, SWT.MULTI);
		gridData = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(gridData);

		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				currentRow = table.getSelectionIndex();
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				enterPath();
			}
		});

		TableColumn columName = new TableColumn(table, SWT.BORDER);
		columName.setText("name");
		columName.setWidth(270);

		TableColumn columnSize = new TableColumn(table, SWT.BORDER);
		columnSize.setText("size");
		columnSize.setWidth(80);

		TableColumn columnDate = new TableColumn(table, SWT.BORDER);
		columnDate.setText("date");
		columnDate.setWidth(120);

		table.setHeaderVisible(true);
		table.setLinesVisible(false);
	}

	public void addViKeyListener() {
		table.addKeyListener(new ZipListerKeyListener(this));
	}

	public void openWithDefault() {
		FileObject fileObject = (FileObject) table.getItem(currentRow).getData(
				"fileObject");
		if (fileObject == null)
			return;
		File file = extractToTemp(fileObject);
		if (file == null)
			return;
		if (file.isFile()) {
			Program.launch(file.getPath());
		}
	}

	public void enterPath() {
		FileObject fileObject = (FileObject) table.getItem(currentRow).getData(
				"fileObject");

		try {
			if (fileObject.getType().equals(FileType.FOLDER)) {
				historyManager.setSelectedItem(currentFileObject.getName()
						.getPath(), fileObject.getName().getBaseName());
				currentFileObject = currentFileObject.getChild(fileObject
						.getName().getBaseName());
				changeCurrentNode();
				textLocation.setText(currentFileObject.getName().getPath());
			} else {
				editFile(fileObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private File extractToTemp(FileObject fileObject) {
		String ext = fileObject.getName().getExtension();
		String basename = fileObject.getName().getBaseName();
		File tempFile = null;
		try {
			String tmpPath = System.getProperty("java.io.tmpdir");
			tempFile = new File(FilenameUtils.concat(tmpPath, basename));

			byte[] buf = new byte[4096];
			BufferedInputStream bin = new BufferedInputStream(fileObject
					.getContent().getInputStream());
			BufferedOutputStream bout = new BufferedOutputStream(
					new FileOutputStream(tempFile));
			while (bin.read(buf, 0, 1) != -1) {
				bout.write(buf, 0, 1);
			}
			bout.close();
			bin.close(); // by barney
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return tempFile;
	}

	public void editFile(FileObject fileObject) {
		File tempFile = extractToTemp(fileObject);
		try {
			String cmd[] = { EDITOR, tempFile.getPath() };
			Runtime.getRuntime().exec(cmd);
		} catch (Exception e) {
		}

	}

	public void upOneDir() {
		try {
			// if the currentFileObject is the root of the archive file, do
			// nothing.
			if (currentFileObject.getName().getPath().equals("/"))
				return;
			FileObject parentFO = currentFileObject.getParent();
			if (parentFO != null) {
				if (table.getItemCount() > 0) {
					TableItem item = table.getItem(currentRow);
					FileObject selectFO = (FileObject) item
							.getData("fileObject");
					historyManager.setSelectedItem(currentFileObject.getName()
							.getPath(), selectFO.getName().getBaseName());
				}

				currentFileObject = parentFO;
				changeCurrentNode();
				textLocation.setText(currentFileObject.getName().getPath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sortFiles(FileObject[] children) {
		Arrays.sort(children, new Comparator() {
			public int compare(Object o1, Object o2) {

				try {
					FileType fileType1 = ((FileObject) o1).getType();
					FileType fileType2 = ((FileObject) o2).getType();
					String filename1 = ((FileObject) o1).getName()
							.getBaseName();
					String filename2 = ((FileObject) o2).getName()
							.getBaseName();
					if (fileType1.equals(FileType.FILE)
							&& fileType2.equals(FileType.FOLDER)) {
						return 1;
					}
					if (fileType2.equals(FileType.FILE)
							&& fileType1.equals(FileType.FOLDER)) {
						return -1;
					}
					return filename1.compareTo(filename2);
				} catch (Exception e) {
					return 1;
				}
			}

		});

	}

	private void changeCurrentNode() {

		boolean hasMatchSelectedName = false;
		FileObject[] children = null;
		try {
			children = currentFileObject.getChildren();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (children == null)
			return;

		sortFiles(children);

		String selectedName = historyManager.getSelectedItem(currentFileObject
				.getName().getPath());
		table.removeAll();
		TableItem item;

		for (int i = 0; i < children.length; i++) {
			FileName fileName = children[i].getName();

			if (fileName.getBaseName().equals(selectedName)) {
				currentRow = i;
				hasMatchSelectedName = true;
			}

			item = new TableItem(table, SWT.NONE);
			item.setData("fileObject", children[i]);
			item.setText(fileName.getBaseName());

			try {
				FileType fileType = children[i].getType();
				FileContent fileContent = children[i].getContent();

				if (fileType.equals(FileType.FOLDER)) {
					item.setImage(folderImage);
					item.setText(1, "--");
					item.setText(2, StringUtil.formatDate(fileContent
							.getLastModifiedTime()));
				} else {
					item.setImage(fileImage);
					item.setText(1, StringUtil
							.formatSize(fileContent.getSize()));
					item.setText(2, StringUtil.formatDate(fileContent
							.getLastModifiedTime()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		if (!hasMatchSelectedName)
			currentRow = 0;
		table.setSelection(currentRow);
		table.setFocus();

	}

	public void cancelOperate() {
		switchToNormalMode();
		table.setFocus();
		table.setSelection(currentRow);
	}

	public void doChange() {
		// TODO Auto-generated method stub

	}

	public void doCut() {
		// TODO Auto-generated method stub

	}

	public void doYank() {
		// TODO Auto-generated method stub

	}

	public void switchPanel() {
		// TODO Auto-generated method stub

	}

	public void switchToVTagMode() {
		this.operateMode = Mode.VTAG;
		this.origRow = currentRow;
	}

	public void tagCurrentItem() {
		if (this.operateMode != Mode.TAG ) {
			switchToTagMode();
		} else {
			toggleSelection(currentRow);
		}

	}

	public void activeWidget() {
		table.setFocus();

	}

	public void activeSearchMode() {
		Main.fileManager.activeSearchMode(this);
	}

	public void refresh() {
	}

	

	public boolean setFocus() {
		return table.setFocus();
	}

}
