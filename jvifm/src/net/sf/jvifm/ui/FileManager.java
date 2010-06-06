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

import java.io.File;
import java.util.List;

import net.sf.jvifm.Main;
import net.sf.jvifm.ResourceManager;
import net.sf.jvifm.model.FileListerListener;
import net.sf.jvifm.model.HistoryManager;
import net.sf.jvifm.model.Preference;
import net.sf.jvifm.ui.factory.GuiDataFactory;
import net.sf.jvifm.ui.hotkeys.Hotkeys;
import net.sf.jvifm.ui.shell.AboutShell;
import net.sf.jvifm.ui.shell.QuickRunShell;
import net.sf.jvifm.ui.sidebar.BookmarkLister;
import net.sf.jvifm.ui.sidebar.HistoryLister;
import net.sf.jvifm.ui.sidebar.ShortcutsLister;
import net.sf.jvifm.ui.viewer.PicViewer;
import net.sf.jvifm.ui.viewer.TextViewer;
import net.sf.jvifm.util.HomeLocator;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class FileManager implements FileListerListener {
	private Shell shell;

	private Panel leftPanel;
	private Panel rightPanel;

	private MiniShell miniShell;
	private JobStatus statusBar;
	private SashForm sashForm;
	private SashForm mainSashForm;
	private TabFolder tabFolder;
	private Tray systemTray;
	private Composite sideViewContentContainer;
	private ViLister sideViLister;

	private Menu menuBar;
	private MenuItem renameMenuItem;
	private MenuItem pasteMenuItem;
	private MenuItem copyMenuItem;
	private MenuItem cutMenuItem;
	private MenuItem preferenceMenuItem;
	private MenuItem createFolderMenuItem;
	private Menu editMenu;
	private Menu fileMenu;
	private MenuItem navigateMenuItem;
	private Menu navigateMenu;
	private MenuItem backMenuItem;
	private MenuItem forwardMenuItem;
	private MenuItem uponeleveMenuItem;
	private MenuItem goHomeMenuItem;
	private MenuItem goRootMenuItem;
	private MenuItem fileMenuItem;
	private MenuItem editMenuItem;
	private MenuItem showFileTreeMenuItem;
	private MenuItem showShortcutsMenuItem;
	private MenuItem showHistoryMenuItem;
	private MenuItem findMenuItem;
	private MenuItem showBookMarkMenuItem;
	private MenuItem searchCurrentFolderMenuItem;
	private Menu searchMenu;
	private MenuItem searchMenuItem;
	private MenuItem openInNewTabMenuItem;
	private MenuItem openInTerminalMenuItem;
	private MenuItem quitMenuItem;
	private MenuItem selectAllMenuItem;
	private MenuItem delMenuItem;
	private MenuItem createFileMenuItem;
	private MenuItem aboutMenuItem;
	private MenuItem contentsMenuItem;
	private Menu helpMenu;
	private MenuItem helpMenuItem;

	private ToolItem goRootToolItem;
	private ToolItem goHomeToolItem;

	private ToolItem goUpToolItem;
	private ToolItem goForwardToolItem;
	private ToolItem goBackToolItem;
	private ToolItem editCutToolItem;
	private ToolItem editCopyToolItem;
	private ToolItem editPasteToolItem;
	private ToolItem findFileToolItem;
	private ToolItem sideviewTypeToolItem;
	private ToolItem refreshToolItem;

	// private String activePanel = "left";

	public static final int ACTION_CUT = 101;
	public static final int ACTION_COPY = 102;
	public static final int ACTION_PASTE = 103;
	public static final int ACTION_RENAME = 104;
	public static final int ACTION_DEL = 105;
	public static final int ACTION_SELECTALL = 106;
	public static final int ACTION_NEWFILE = 107;
	public static final int ACTION_NEWFOLDER = 108;
	public static final int ACTION_OPENINTAB = 109;
	public static final int ACTION_OPENINTERMINAL = 110;

	public static final int NV_BACK = 201;
	public static final int NV_FORWARD = 202;
	public static final int NV_UP = 203;
	public static final int NV_ROOT = 204;
	public static final int NV_HOME = 205;
	public static final int NV_REFRESH = 206;

	public static final int SIDE_BOOKMARK = 301;
	public static final int SIDE_HISTORY = 302;
	public static final int SIDE_PROGRAM = 303;
	public static final int SIDE_FOLDER = 304;

	public static final int MISC_PREFERENCE = 401;
	public static final int MISC_ABOUT = 402;
	public static final int MISC_CONTENT = 403;
	public static final int MISC_QUIT = 404;
	public static final int MISC_SEARCHINFOLDER = 405;
	public static final int MISC_FIND = 406;
	

	public Shell open(Display display) {
		shell = new Shell(display);
		GridLayout thisLayout = GuiDataFactory.createkGridLayout(1, 1, 0, 0, 0, true);
		shell.setLayout(thisLayout);

		initMenu();
		initToolBar();
		initContent();
		initSysTray();

		shell.setSize(700, 500);
		shell.setText("jvifm"); //$NON-NLS-1$
		shell.setImage(ResourceManager.getImage("filemanager.png")); //$NON-NLS-1$

		shell.addShellListener(new ShellAdapter() {
			public void shellIconified(ShellEvent arg0) {
				// shell.setVisible(false);
			}

			public void shellClosed(ShellEvent arg0) {
				arg0.doit = false;
				shell.setVisible(false);
			}

		});
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				systemTray.dispose();
				ResourceManager.disposeResource();
			}
		});

		shell.open();

		Hotkeys.bindKeys();

		return shell;
	}

	private Menu createBackwardMenu() {
		Menu menu = new Menu(shell, SWT.POP_UP);
		final FileLister fileLister = getActivePanel();
		final HistoryManager historyManager = fileLister.getHistoryInfo();
		List<String> backwardPaths = historyManager.getBackList();
		if (backwardPaths == null || backwardPaths.size() <= 0) return null;
		for (String path : backwardPaths ) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(path);
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					MenuItem item = (MenuItem) e.widget;
					fileLister.changePwd(item.getText());
					historyManager.backTo(item.getText());
				}
			});
		}
		return menu;
	}

	private Menu createForwardMenu() {
		Menu menu = new Menu(shell, SWT.POP_UP);
		final FileLister fileLister = getActivePanel();
		final HistoryManager historyManager = fileLister.getHistoryInfo();
		List<String> forwardPaths = historyManager.getForwardList();
		if (forwardPaths == null || forwardPaths.size() <= 0)
			return null;
		for (String path : forwardPaths ) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(path);
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					MenuItem item = (MenuItem) e.widget;
					fileLister.changePwd(item.getText());
					historyManager.forwardTo(item.getText());
				}
			});
		}
		return menu;
	}

	private Menu createSideviewTypeMenu() {
		Menu menu = new Menu(shell, SWT.POP_UP);
		MenuItem bookmarkItem = new MenuItem(menu, SWT.PUSH);
		bookmarkItem
				.setText(Messages.getString("FileManager.menuitemBookmark")); //$NON-NLS-1$
		bookmarkItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showBookmarkSidevew();
			}

		});

		MenuItem historyItem = new MenuItem(menu, SWT.PUSH);
		historyItem.setText(Messages.getString("FileManager.menuitemHistory")); //$NON-NLS-1$
		historyItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showHistorySideview();
			}

		});

		MenuItem foldertreeItem = new MenuItem(menu, SWT.PUSH);
		foldertreeItem.setText(Messages
				.getString("FileManager.menuitemFolderTree")); //$NON-NLS-1$
		foldertreeItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showFileTree();
			}
		});
		MenuItem shortcutsItem = new MenuItem(menu, SWT.PUSH);
		shortcutsItem.setText(Messages
				.getString("FileManager.menuitemShortcuts")); //$NON-NLS-1$
		shortcutsItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showShortcutsSideview();
			}
		});
		return menu;

	}

	private void initSysTray() {
		systemTray = shell.getDisplay().getSystemTray();

		final Menu systemTrayItemMenu = new Menu(shell, SWT.POP_UP);
		MenuItem showItem = new MenuItem(systemTrayItemMenu, SWT.NONE);
		showItem.setText(Messages.getString("FileManager.menuitemShowMainWin")); //$NON-NLS-1$

		showItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(true);
				// shell.setMaximized(true);
				shell.setActive();
				shell.setFocus();
			}
		});

		new MenuItem(systemTrayItemMenu, SWT.SEPARATOR);

		MenuItem quitItem = new MenuItem(systemTrayItemMenu, SWT.NONE);

		quitItem.setText(Messages.getString("FileManager.menuitemQuit")); //$NON-NLS-1$
		quitItem.setImage(ResourceManager.getImage("quit.png"));
		quitItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Main.exit();
			}
		});

		final TrayItem systemTrayItem = new TrayItem(systemTray, SWT.NONE);

		systemTrayItem.setImage(ResourceManager.getImage("filemanager.png")); //$NON-NLS-1$
		systemTrayItem.setToolTipText("jvifm"); //$NON-NLS-1$

		systemTrayItem.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				systemTrayItemMenu.setVisible(true);
			}
		});

		systemTrayItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (shell.isVisible()) {
					shell.setVisible(false);
				} else {
					shell.setVisible(true);
					// shell.setMaximized(true);
					shell.setActive();
					shell.setFocus();
					activePanel();
				}
			}
		});

	}

	private void initMenu() {
		menuBar = new Menu(getShell(), SWT.BAR);
		getShell().setMenuBar(menuBar);
		{
			fileMenuItem = new MenuItem(menuBar, SWT.CASCADE);
			fileMenuItem.setText(Messages.getString("FileManager.fileMenu")); //$NON-NLS-1$
			{
				fileMenu = new Menu(fileMenuItem);
				fileMenuItem.setMenu(fileMenu);
				{
					createFolderMenuItem = new MenuItem(fileMenu, SWT.PUSH);
					createFolderMenuItem.setText(Messages
							.getString("FileManager.menuitemCreateFolder")); //$NON-NLS-1$
					createFolderMenuItem
							.addSelectionListener(new FileOperateListener(
									ACTION_NEWFOLDER));
				}
				{
					createFileMenuItem = new MenuItem(fileMenu, SWT.PUSH);
					createFileMenuItem.setText(Messages
							.getString("FileManager.menuitemCreateFile")); //$NON-NLS-1$
					createFileMenuItem
							.addSelectionListener(new FileOperateListener(
									ACTION_NEWFILE));
				}
				{
					new MenuItem(fileMenu, SWT.SEPARATOR);
				}
				{
					openInNewTabMenuItem = new MenuItem(fileMenu, SWT.PUSH);
					openInNewTabMenuItem.setText(Messages
							.getString("FileManager.menuitemOpenInNewTab")); //$NON-NLS-1$
					openInNewTabMenuItem.setImage(ResourceManager
							.getImage("tab-new.png")); //$NON-NLS-1$
					openInNewTabMenuItem
							.addSelectionListener(new FileOperateListener(
									ACTION_OPENINTAB));

					openInTerminalMenuItem = new MenuItem(fileMenu, SWT.PUSH);
					openInTerminalMenuItem.setText(Messages
							.getString("FileManager.menuitemOpenInTerminal")); //$NON-NLS-1$
					openInTerminalMenuItem.setImage(ResourceManager
							.getImage("terminal.png")); //$NON-NLS-1$
					openInTerminalMenuItem
							.addSelectionListener(new FileOperateListener(
									ACTION_OPENINTERMINAL));
				}
				{
					new MenuItem(fileMenu, SWT.SEPARATOR);
				}
				{
					quitMenuItem = new MenuItem(fileMenu, SWT.PUSH);
					quitMenuItem.setText(Messages
							.getString("FileManager.menuitemQuit")); //$NON-NLS-1$
					quitMenuItem.setImage(ResourceManager.getImage("quit.png"));
					quitMenuItem.addSelectionListener(new MiscListener(
							MISC_QUIT));

				}
			}
		}
		{
			editMenuItem = new MenuItem(menuBar, SWT.CASCADE);
			editMenuItem.setText(Messages.getString("FileManager.editMenu")); //$NON-NLS-1$
			{
				editMenu = new Menu(editMenuItem);
				editMenuItem.setMenu(editMenu);
				{
					cutMenuItem = new MenuItem(editMenu, SWT.PUSH);
					cutMenuItem.setText(Messages
							.getString("FileManager.menuitemCut")); //$NON-NLS-1$
					cutMenuItem.setImage(ResourceManager
							.getImage("edit-cut.png")); //$NON-NLS-1$
					cutMenuItem.addSelectionListener(new FileOperateListener(
							ACTION_CUT));
				}
				{
					copyMenuItem = new MenuItem(editMenu, SWT.PUSH);
					copyMenuItem.setText(Messages
							.getString("FileManager.menuitemCopy")); //$NON-NLS-1$
					copyMenuItem.setImage(ResourceManager
							.getImage("edit-copy.png")); //$NON-NLS-1$
					copyMenuItem.addSelectionListener(new FileOperateListener(
							ACTION_COPY));
				}
				{
					pasteMenuItem = new MenuItem(editMenu, SWT.PUSH);
					pasteMenuItem.setText(Messages
							.getString("FileManager.menuitemPaste")); //$NON-NLS-1$
					pasteMenuItem.setImage(ResourceManager
							.getImage("edit-paste.png")); //$NON-NLS-1$
					pasteMenuItem.addSelectionListener(new FileOperateListener(
							ACTION_PASTE));
				}
				{
					new MenuItem(editMenu, SWT.SEPARATOR);
				}
				{
					renameMenuItem = new MenuItem(editMenu, SWT.PUSH);
					renameMenuItem.setText(Messages
							.getString("FileManager.menuitemRename")); //$NON-NLS-1$
					renameMenuItem
							.addSelectionListener(new FileOperateListener(
									ACTION_RENAME));
				}
				{
					delMenuItem = new MenuItem(editMenu, SWT.PUSH);
					delMenuItem.setText(Messages
							.getString("FileManager.menuitemDelete")); //$NON-NLS-1$
					delMenuItem.setImage(ResourceManager
							.getImage("edit-delete.png")); //$NON-NLS-1$
					delMenuItem.addSelectionListener(new FileOperateListener(
							ACTION_DEL));
				}
				{
					new MenuItem(editMenu, SWT.SEPARATOR);
				}
				{
					selectAllMenuItem = new MenuItem(editMenu, SWT.PUSH);
					selectAllMenuItem.setText(Messages
							.getString("FileManager.menuitemSelectAll")); //$NON-NLS-1$
					selectAllMenuItem
							.addSelectionListener(new FileOperateListener(
									ACTION_SELECTALL));
				}
				{
					new MenuItem(editMenu, SWT.SEPARATOR);
				}
				{
					preferenceMenuItem = new MenuItem(editMenu, SWT.PUSH);
					preferenceMenuItem.setText(Messages
							.getString("FileManager.menuitemPreference")); //$NON-NLS-1$
					preferenceMenuItem.setImage(ResourceManager
							.getImage("preferences.png"));
					preferenceMenuItem.addSelectionListener(new MiscListener(
							MISC_PREFERENCE));

				}
			}
		}
		{
			navigateMenuItem = new MenuItem(menuBar, SWT.CASCADE);
			navigateMenuItem.setText(Messages.getString("FileManager.menuitemNavigate"));
			{
				navigateMenu = new Menu(navigateMenuItem);
				navigateMenuItem.setMenu(navigateMenu);
				{
					backMenuItem = new MenuItem(navigateMenu, SWT.PUSH);
					backMenuItem.setText(Messages.getString("FileManager.menuitemBack"));
					backMenuItem.setImage(ResourceManager.getImage("go-previous.png"));//$NON-NLS-1$
					backMenuItem.addSelectionListener(new NavigateListener(NV_BACK));
				}
				{
					forwardMenuItem = new MenuItem(navigateMenu, SWT.PUSH);
					forwardMenuItem.setText(Messages.getString("FileManager.menuitemForward"));
					forwardMenuItem.setImage(ResourceManager.getImage("go-next.png"));//$NON-NLS-1$
					forwardMenuItem.addSelectionListener(new NavigateListener(NV_FORWARD));
				}
				{
					uponeleveMenuItem = new MenuItem(navigateMenu, SWT.PUSH);
					uponeleveMenuItem.setText(Messages.getString("FileManager.menuitemUponeLevel"));
					uponeleveMenuItem.setImage(ResourceManager.getImage("go-up.png")); //$NON-NLS-1$
					uponeleveMenuItem.addSelectionListener(new NavigateListener(NV_UP));
				}
				{
					goHomeMenuItem = new MenuItem(navigateMenu, SWT.PUSH);
					goHomeMenuItem.setText(Messages.getString("FileManager.menuitemGoHome"));
					goHomeMenuItem.setImage(ResourceManager.getImage("go-home.png")); //$NON-NLS-1$
					goHomeMenuItem.addSelectionListener(new NavigateListener(NV_HOME));
				}
				{
					goRootMenuItem = new MenuItem(navigateMenu, SWT.PUSH);
					goRootMenuItem.setText(Messages.getString("FileManager.menuitemGoRoot"));
					goRootMenuItem.setImage(ResourceManager.getImage("computer.png")); //$NON-NLS-1$
					goRootMenuItem.addSelectionListener(new NavigateListener( NV_ROOT));
				}
				{
					new MenuItem(navigateMenu, SWT.SEPARATOR);
				}
				{
					showBookMarkMenuItem = new MenuItem(navigateMenu, SWT.PUSH);
					showBookMarkMenuItem.setText(Messages.getString("FileManager.menuitemBookmark")); //$NON-NLS-1$
					showBookMarkMenuItem.addSelectionListener(new ShowSideViewListener(SIDE_BOOKMARK));
				}
				{
					showHistoryMenuItem = new MenuItem(navigateMenu, SWT.PUSH);
					showHistoryMenuItem.setText(Messages.getString("FileManager.menuitemHistory")); //$NON-NLS-1$
					showHistoryMenuItem.addSelectionListener(new ShowSideViewListener(SIDE_HISTORY));
				}
				{
					showFileTreeMenuItem = new MenuItem(navigateMenu, SWT.PUSH);
					showFileTreeMenuItem.setText(Messages
							.getString("FileManager.menuitemFolderTree")); //$NON-NLS-1$
					showFileTreeMenuItem.addSelectionListener(new ShowSideViewListener(SIDE_FOLDER));
				}
				{
					showShortcutsMenuItem = new MenuItem(navigateMenu, SWT.PUSH);
					showShortcutsMenuItem.setText(Messages
							.getString("FileManager.menuitemShortcuts")); //$NON-NLS-1$
					showShortcutsMenuItem.addSelectionListener(new ShowSideViewListener(SIDE_PROGRAM));
				}
			}
		}

		{
			searchMenuItem = new MenuItem(menuBar, SWT.CASCADE);
			searchMenuItem.setText(Messages
					.getString("FileManager.menuitemSearch")); //$NON-NLS-1$
			{
				searchMenu = new Menu(searchMenuItem);
				searchMenuItem.setMenu(searchMenu);
				{
					searchCurrentFolderMenuItem = new MenuItem(searchMenu, SWT.PUSH);
					searchCurrentFolderMenuItem
						.setText(Messages.getString("FileManager.menuitemSearchInCurrentFolder")); //$NON-NLS-1$
					searchCurrentFolderMenuItem.addSelectionListener(new MiscListener(MISC_SEARCHINFOLDER));
				}
				{
					findMenuItem = new MenuItem(searchMenu, SWT.PUSH);
					findMenuItem.setText(Messages.getString("FileManager.menuitemFind")); //$NON-NLS-1$
					findMenuItem.setImage(ResourceManager.getImage("edit-find.png")); //$NON-NLS-1$
					findMenuItem.addSelectionListener(new MiscListener(MISC_FIND));
				}
			}
		}

		{
			helpMenuItem = new MenuItem(menuBar, SWT.CASCADE);
			helpMenuItem
					.setText(Messages.getString("FileManager.menuitemHelp")); //$NON-NLS-1$
			{
				helpMenu = new Menu(helpMenuItem);
				{
					contentsMenuItem = new MenuItem(helpMenu, SWT.CASCADE);
					contentsMenuItem.setText(Messages.getString("FileManager.menuitemContents")); //$NON-NLS-1$
					contentsMenuItem.setImage(ResourceManager.getImage("help.png"));
					contentsMenuItem.addSelectionListener(new MiscListener( MISC_CONTENT));

				}
				{
					aboutMenuItem = new MenuItem(helpMenu, SWT.CASCADE);
					aboutMenuItem.setText(Messages.getString("FileManager.menuitemAbout")); //$NON-NLS-1$
					aboutMenuItem.addSelectionListener(new MiscListener(MISC_ABOUT));

				}
				helpMenuItem.setMenu(helpMenu);
			}
		}

	}

	private void initToolBar() {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		ToolBar toolBar1 = new ToolBar(shell, SWT.FLAT);
		toolBar1.setLayoutData(gridData);
		{
			goBackToolItem = new ToolItem(toolBar1, SWT.DROP_DOWN);
			goBackToolItem
					.setImage(ResourceManager.getImage("go-previous.png")); //$NON-NLS-1$
			goBackToolItem.addSelectionListener(new NavigateListener(NV_BACK));

			goForwardToolItem = new ToolItem(toolBar1, SWT.DROP_DOWN);
			goForwardToolItem.setImage(ResourceManager.getImage("go-next.png")); //$NON-NLS-1$
			goForwardToolItem.addSelectionListener(new NavigateListener(
					NV_FORWARD));

			goUpToolItem = new ToolItem(toolBar1, SWT.NONE);
			goUpToolItem.setImage(ResourceManager.getImage("go-up.png")); //$NON-NLS-1$
			goUpToolItem.addSelectionListener(new NavigateListener(NV_UP));
			
			refreshToolItem = new ToolItem(toolBar1, SWT.NONE);
			refreshToolItem.setImage(ResourceManager.getImage("view-refresh.png")); //$NON-NLS-1$
			refreshToolItem.addSelectionListener(new NavigateListener(NV_REFRESH));

		}
		{
			new ToolItem(toolBar1, SWT.SEPARATOR);
		}
		{
			goHomeToolItem = new ToolItem(toolBar1, SWT.NONE);
			goHomeToolItem.setImage(ResourceManager.getImage("go-home.png")); //$NON-NLS-1$
			goHomeToolItem.addSelectionListener(new NavigateListener(NV_HOME));

			goRootToolItem = new ToolItem(toolBar1, SWT.NONE);
			goRootToolItem.setImage(ResourceManager.getImage("computer.png")); //$NON-NLS-1$
			goRootToolItem.addSelectionListener(new NavigateListener(NV_ROOT));
		}
		{
			new ToolItem(toolBar1, SWT.SEPARATOR);
		}
		{
			editCutToolItem = new ToolItem(toolBar1, SWT.NONE);
			editCutToolItem.setImage(ResourceManager.getImage("edit-cut.png")); //$NON-NLS-1$
			editCutToolItem.addSelectionListener(new FileOperateListener(
					ACTION_CUT));

			editCopyToolItem = new ToolItem(toolBar1, SWT.NONE);
			editCopyToolItem
					.setImage(ResourceManager.getImage("edit-copy.png")); //$NON-NLS-1$
			editCopyToolItem.addSelectionListener(new FileOperateListener(
					ACTION_COPY));

			editPasteToolItem = new ToolItem(toolBar1, SWT.NONE);
			editPasteToolItem.setImage(ResourceManager
					.getImage("edit-paste.png")); //$NON-NLS-1$
			editPasteToolItem.addSelectionListener(new FileOperateListener(
					ACTION_PASTE));

		}
		{
			new ToolItem(toolBar1, SWT.SEPARATOR);
		}
		{
			findFileToolItem = new ToolItem(toolBar1, SWT.NONE);
			findFileToolItem.setImage(ResourceManager.getImage("edit-find.png")); //$NON-NLS-1$
			findFileToolItem.addSelectionListener(new MiscListener(MISC_FIND));
		}

	}

	private void initContent() {

		GridData gridData = null;

		mainSashForm = new SashForm(shell, SWT.HORIZONTAL | SWT.SMOOTH);

		Composite sideViewContainer = new Composite(mainSashForm, SWT.NONE);

		sideViewContainer.setLayout(GuiDataFactory.createkGridLayout(1, 0, 0,
				0, 0, true));
		{
			Composite sideViewHeadContainer = new Composite(sideViewContainer,
					SWT.BORDER);
			sideViewHeadContainer.setLayout(GuiDataFactory.createkGridLayout(2,
					0, 0, 0, 0, false));
			sideViewHeadContainer.setLayoutData(GuiDataFactory.createGridData(
					GridData.BEGINNING, GridData.FILL, true, false));
			{
				ToolBar toolBar1 = new ToolBar(sideViewHeadContainer, SWT.FLAT);
				sideviewTypeToolItem = new ToolItem(toolBar1, SWT.DROP_DOWN);
				GridData label1LData = new GridData();
				label1LData.grabExcessHorizontalSpace = true;
				toolBar1.setLayoutData(label1LData);
				sideviewTypeToolItem.setText(Messages
						.getString("FileManager.labelBookmark")); //$NON-NLS-1$
				sideviewTypeToolItem
						.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent event) {
								if (event.detail == SWT.ARROW) {
									Rectangle rect = sideviewTypeToolItem
											.getBounds();
									Point pt = new Point(rect.x, rect.y
											+ rect.height);
									pt = sideviewTypeToolItem.getParent()
											.toDisplay(pt);
									Menu menu = createSideviewTypeMenu();
									menu.setLocation(pt);
									menu.setVisible(true);
								}
							}

						});

			}
			{
				gridData = new GridData();
				gridData.horizontalAlignment = GridData.END;
				ToolBar toolBar2 = new ToolBar(sideViewHeadContainer, SWT.FLAT);
				toolBar2.setLayoutData(gridData);
				{
					ToolItem toolItem1 = new ToolItem(toolBar2, SWT.NONE);
					toolItem1.setImage(ResourceManager
							.getImage("window-close.png")); //$NON-NLS-1$
					toolItem1.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							hideSideBar();
						}
					});
				}
			}
		}
		{
			sideViewContentContainer = new Composite(sideViewContainer,
					SWT.NONE);
			sideViewContentContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
			sideViewContentContainer.setLayoutData(GuiDataFactory
					.createGridData(GridData.FILL, GridData.FILL, true, true));

			sideViLister = new BookmarkLister(sideViewContentContainer,
					SWT.NONE);
			sideViewContainer.layout();
		}

		mainSashForm.setLayoutData(GuiDataFactory.createGridData(GridData.FILL,
				GridData.FILL, true, true));

		tabFolder = new TabFolder(mainSashForm, SWT.NONE);

		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				activeTab();
			}
		});

		statusBar = new JobStatus(shell, SWT.NONE);
		gridData = new GridData();
		// gridData.heightHint=20;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		statusBar.setLayoutData(gridData);

		miniShell = new MiniShell(shell, SWT.NONE | SWT.SINGLE);

		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		miniShell.setLayoutData(gridData);

		mainSashForm.setWeights(new int[] { 2, 8 });

	}

	public void activeTab() {

		TabItem currentItem = (tabFolder.getSelection())[0];
		if (currentItem == null)
			currentItem = tabFolder.getItem(0);
		String tabtype = (String) currentItem.getData("tabtype"); //$NON-NLS-1$
		if (tabtype == null)
			return;

		if (tabtype.equals("fileLister")) { //$NON-NLS-1$
			sashForm = (SashForm) currentItem.getControl();
			leftPanel = (Panel) sashForm.getData("leftPanel"); //$NON-NLS-1$
			rightPanel = (Panel) sashForm.getData("rightPanel"); //$NON-NLS-1$
			activePanel();
		} else if (tabtype.equals("zipLister")) { //$NON-NLS-1$
			ZipLister lister = (ZipLister) currentItem.getControl();
			lister.setFocus();
		}

	}

	public String[][] getAllCurrentPath() {

		int itemCount = tabFolder.getItemCount();
		String[][] result = new String[itemCount][2];
		for (int i = 0; i < itemCount; i++) {
			TabItem item = tabFolder.getItem(i);
			if (item.getData("tabtype").equals("fileLister")) { //$NON-NLS-1$ //$NON-NLS-2$
				SashForm sashForm = (SashForm) item.getControl();
				FileLister left = (FileLister) sashForm.getData("leftPanel"); //$NON-NLS-1$
				FileLister right = (FileLister) sashForm.getData("rightPanel"); //$NON-NLS-1$
				result[i][0] = left.getPwd();
				result[i][1] = right.getPwd();
			}
		}
		return result;
	}

	public void renameTab(String title) {
		TabItem currentItem = (tabFolder.getSelection())[0];
		currentItem.setText(title);
	}

	public void onChangePwd(String oldPath, String newPath) {

	}

	public void onChangeSelection(String fullpath) {
		preview(fullpath);
	}

	public void showBookmarkSidevew() {
		Control[] childrens = sideViewContentContainer.getChildren();
		for (int i = 0; i < childrens.length; i++) {
			if (!childrens[i].isDisposed())
				childrens[i].dispose();
		}
		sideViLister = new BookmarkLister(sideViewContentContainer, SWT.NONE);
		sideViewContentContainer.layout();
		sideviewTypeToolItem.setText(Messages
				.getString("FileManager.labelBookmark")); //$NON-NLS-1$
		showSideBar();
	}

	public void showShortcutsSideview() {
		Control[] childrens = sideViewContentContainer.getChildren();
		for (int i = 0; i < childrens.length; i++) {
			if (!childrens[i].isDisposed())
				childrens[i].dispose();
		}
		sideViLister = new ShortcutsLister(sideViewContentContainer, SWT.NONE);
		sideViewContentContainer.layout();
		sideviewTypeToolItem.setText(Messages
				.getString("FileManager.labelShortcuts")); //$NON-NLS-1$
		showSideBar();
	}

	public void showHistorySideview() {
		Control[] childrens = sideViewContentContainer.getChildren();
		for (int i = 0; i < childrens.length; i++) {
			if (!childrens[i].isDisposed())
				childrens[i].dispose();
		}
		sideViLister = new HistoryLister(sideViewContentContainer, SWT.NONE);
		sideViewContentContainer.layout();

		sideviewTypeToolItem.setText(Messages
				.getString("FileManager.labelHistory")); //$NON-NLS-1$
		showSideBar();

	}

	public void showFileTree() {
		Control[] childrens = sideViewContentContainer.getChildren();
		for (int i = 0; i < childrens.length; i++) {
			if (!childrens[i].isDisposed())
				childrens[i].dispose();
		}
		String pwd=getActivePanel().getPwd();
		sideViLister = new FileTree(sideViewContentContainer, SWT.NONE, pwd);
		sideViewContentContainer.layout();

		sideviewTypeToolItem.setText(Messages
				.getString("FileManager.labelFolderTree")); //$NON-NLS-1$
		only();
		mainSashForm.setWeights(new int[]{4,6});
		showSideBar();
	}

	public void switchTab(int index) {
		int count = tabFolder.getItemCount();
		if (index > 0 && index <= count)
			tabFolder.setSelection(index - 1);
		activeTab();

	}

	public void swapPanel() {

		if (leftPanel instanceof FileLister && rightPanel instanceof FileLister) {
			String leftPwd = ((FileLister) leftPanel).getPwd();
			String rightPwd = ((FileLister) rightPanel).getPwd();
			((FileLister) leftPanel).visit(rightPwd);
			((FileLister) rightPanel).visit(leftPwd);
		}
	}

	public void switchToNextTab() {
		int index = tabFolder.getSelectionIndex();
		if (index == tabFolder.getItemCount() - 1) {
			index = 0;
		} else {
			index = index + 1;
		}
		tabFolder.setSelection(index);
		activeTab();
	}

	public void switchToPrevTab() {
		int index = tabFolder.getSelectionIndex();
		if (index == 0) {
			index = tabFolder.getItemCount() - 1;
		} else {
			index = index - 1;
		}
		tabFolder.setSelection(index);
		activeTab();
	}

	public void quit() {

		if (tabFolder.getItemCount() > 1) {
			TabItem currentItem = (tabFolder.getSelection())[0];
			currentItem.dispose();
		} else {
			Main.exit();
		}

	}

	public void nopreview() {
		rightPanel.dispose();
		rightPanel = new FileLister(sashForm, SWT.BORDER, "", "right"); //$NON-NLS-1$
		((FileLister) rightPanel).visit(FileLister.FS_ROOT);
		sashForm.layout();
	}

	public void preview(String file) {

		String ext = FilenameUtils.getExtension(file);

		if (ext.endsWith("jpg") || ext.endsWith("png") || ext.endsWith("bmp")
				|| ext.endsWith("gif")) {
			if (rightPanel instanceof PicViewer) {
				((PicViewer) rightPanel).loadFile(file);
			} else {
				rightPanel.dispose();
				rightPanel = new PicViewer(sashForm, file);
			}

		} else {
			if (rightPanel instanceof TextViewer) {
				((TextViewer) rightPanel).loadFile(file);
			} else {
				rightPanel.dispose();
				rightPanel = new TextViewer(sashForm, file);
			}
		}

		sashForm.setData("rightPanel", rightPanel);//$NON-NLS-1$
		sashForm.layout(true);

	}

	public void pack() {
		Point size = getActivePanel().getSize();
		int width1 = (size.x + 10) * 100 / sashForm.getSize().x;
		if (width1 > 90)
			width1 = 80;
		int width2 = 100 - width1;

		String activePanel = (String) sashForm.getData("activePanel");
		if (activePanel.equals("left")) {
			sashForm.setWeights(new int[] { width1, width2 });
		} else {
			sashForm.setWeights(new int[] { width2, width1 });
		}

	}

	public void tabnew(String leftPath, String rightPath) {

		TabItem one = new TabItem(tabFolder, SWT.NONE);
		one.setData("tabtype", "fileLister"); //$NON-NLS-1$ //$NON-NLS-2$

		sashForm = new SashForm(tabFolder, SWT.HORIZONTAL);
		sashForm.SASH_WIDTH = 6;

		leftPanel = new FileLister(sashForm, SWT.BORDER, leftPath, "left"); //$NON-NLS-1$

		rightPanel = new FileLister(sashForm, SWT.BORDER, rightPath, "right"); //$NON-NLS-1$

		sashForm.setData("leftPanel", leftPanel); //$NON-NLS-1$
		sashForm.setData("rightPanel", rightPanel); //$NON-NLS-1$
		sashForm.setData("activePanel", "left"); //$NON-NLS-1$ //$NON-NLS-2$

		one.setControl(sashForm);
		tabFolder.setSelection(new TabItem[] { one });
		activeTab();

	}

	public void zipTabNew(String zipfilePath) {
		TabItem one = new TabItem(tabFolder, SWT.NONE);
		one.setText(new File(zipfilePath).getName());
		ZipLister list = new ZipLister(tabFolder, SWT.NONE, zipfilePath);
		one.setControl(list);
		one.setData("tabtype", "zipLister"); //$NON-NLS-1$ //$NON-NLS-2$
		tabFolder.setSelection(new TabItem[] { one });

		list.setFocus();
	}

	public void only() {
		String activePanel = (String) sashForm.getData("activePanel"); //$NON-NLS-1$
		if (activePanel.equals("left")) { //$NON-NLS-1$
			sashForm.setMaximizedControl(leftPanel.getControl());
		} else {
			sashForm.setMaximizedControl(rightPanel.getControl());
		}

	}

	public void split() {
		sashForm.setMaximizedControl(null);
	}

	public void hideSideBar() {
		mainSashForm.setMaximizedControl(tabFolder);
	}

	public void showSideBar() {
		mainSashForm.setMaximizedControl(null);
	}

	public void changePanel() {
		if (sashForm.getMaximizedControl() != null)
			return;

		String activePanel = (String) sashForm.getData("activePanel"); //$NON-NLS-1$
		if (activePanel.equals("left")) { //$NON-NLS-1$
			rightPanel.active();
			sashForm.setData("activePanel", "right"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			leftPanel.active();
			sashForm.setData("activePanel", "left"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (sideViLister instanceof HistoryLister) {
			((HistoryLister) sideViLister).loadHistoryRecord();
		}
		// leftPanel.redraw();
		// rightPanel.redraw();
	}

	public FileLister getActivePanel() {
		String activePanel = (String) sashForm.getData("activePanel"); //$NON-NLS-1$
		if (activePanel.equals("left")) { //$NON-NLS-1$
			return leftPanel instanceof FileLister ? (FileLister) leftPanel
					: null;
		}
		return rightPanel instanceof FileLister ? (FileLister) rightPanel
				: null;
	}

	public FileLister getInActivePanel() {
		String activePanel = (String) sashForm.getData("activePanel"); //$NON-NLS-1$
		if (activePanel.equals("left")) { //$NON-NLS-1$
			return rightPanel instanceof FileLister ? (FileLister) rightPanel
					: null;
		}
		return leftPanel instanceof FileLister ? (FileLister) leftPanel : null;
	}

	public void refresh() {
		// leftPanel.refresh();
		// rightPanel.refresh();
	}

	public void activePanel() {
		String activePanel = (String) sashForm.getData("activePanel"); //$NON-NLS-1$
		if (activePanel.equals("left")) { //$NON-NLS-1$
			leftPanel.active();
		} else {
			rightPanel.active();
		}
	}

	public void activePanel(String pos) {
		if (pos.equals("left")) { //$NON-NLS-1$
			sashForm.setData("activePanel", "left"); //$NON-NLS-1$ //$NON-NLS-2$
			leftPanel.active();
		} else {
			sashForm.setData("activePanel", "right"); //$NON-NLS-1$ //$NON-NLS-2$
			rightPanel.active();
		}
	}

	public void hide() {
		shell.setVisible(false);
	}


	public void activeSideView() {
		sideViLister.activeWidget();
	}


	public void activeMiniShell(ViLister lister,String leadStr) {
		if (lister !=null)
			miniShell.setViLister(lister);
		miniShell.activeWith(leadStr); //$NON-NLS-1$
		miniShell.setFocus();
	}
	
	public void setModeIndicate(String mode) {
		miniShell.setText(mode);
	}

	public Shell getShell() {
		return shell;
	}

	public void showStatusAnimation() {
		statusBar.show();
	}

	public void setStatusInfo(String status) {
		statusBar.updateStatus(status);
	}

	public void hideStatusAnimation() {
		statusBar.hide();
	}

	public void onHotKey(final int id) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (id == Hotkeys.ACTION_SHOWMAIN) {
					activeGUI();
				} else {
					QuickRunShell qrs = new QuickRunShell();
					qrs.open();
				}
			}
		});
	}

	public void activeGUI() {
		shell.setVisible(true);
		shell.setActive();
		shell.setFocus();
		activePanel();
	}

	class ShowSideViewListener extends SelectionAdapter {
		int sideType;

		public ShowSideViewListener(int sideType) {
			this.sideType = sideType;
		}

		public void widgetSelected(SelectionEvent e) {
			if (sideType == FileManager.SIDE_BOOKMARK) {
				Main.fileManager.showBookmarkSidevew();
			} else if (sideType == FileManager.SIDE_HISTORY) {
				Main.fileManager.showHistorySideview();
			} else if (sideType == FileManager.SIDE_FOLDER) {
				Main.fileManager.showFileTree();
			} else if (sideType == FileManager.SIDE_PROGRAM) {
				Main.fileManager.showShortcutsSideview();
			}
		}
	}

	class NavigateListener extends SelectionAdapter {
		int actionName;

		public NavigateListener(int actionName) {
			this.actionName = actionName;
		}

		public void widgetSelected(SelectionEvent e) {
			FileLister fileLister = Main.fileManager.getActivePanel();

			if (actionName == FileManager.NV_FORWARD) {
				if (e.detail == SWT.ARROW) {
					Menu menu = createForwardMenu();
					if (menu == null)
						return;

					Rectangle rect = goBackToolItem.getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					pt = goForwardToolItem.getParent().toDisplay(pt);
					menu.setLocation(pt);
					menu.setVisible(true);
				} else {
					fileLister.forward();
				}

			} else if (actionName == FileManager.NV_BACK) {
				if (e.detail == SWT.ARROW) {
					Menu menu = createBackwardMenu();
					if (menu == null)
						return;

					Rectangle rect = goBackToolItem.getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					pt = goBackToolItem.getParent().toDisplay(pt);
					menu.setLocation(pt);
					menu.setVisible(true);
				} else {
					fileLister.back();
				}

			} else if (actionName == FileManager.NV_UP) {
				fileLister.upOneDir();
			} else if (actionName == FileManager.NV_HOME) {
				fileLister.visit(HomeLocator.getUserHome());
				fileLister.refreshHistoryInfo();
			} else if (actionName == FileManager.NV_ROOT) {
				fileLister.visit(FileLister.FS_ROOT);
				fileLister.refreshHistoryInfo();
			} else if (actionName == FileManager.NV_REFRESH) {
				fileLister.refresh();
			}
		}
	}

	class FileOperateListener extends SelectionAdapter {
		int actionName;

		public FileOperateListener(int actionName) {
			this.actionName = actionName;
		}

		public void widgetSelected(SelectionEvent e) {
			FileLister fileLister = Main.fileManager.getActivePanel();
			if (actionName == FileManager.ACTION_COPY) {
				fileLister.doYank();
			} else if (actionName == FileManager.ACTION_CUT) {
				fileLister.doCut();
			} else if (actionName == FileManager.ACTION_PASTE) {
				fileLister.doPaste();
			} else if (actionName == FileManager.ACTION_DEL) {
				fileLister.doDelete();
			} else if (actionName == FileManager.ACTION_RENAME) {
				fileLister.doChange();
			} else if (actionName == FileManager.ACTION_SELECTALL) {
				fileLister.selectAll();
			} else if (actionName == FileManager.ACTION_NEWFILE) {
				fileLister.doAddItem("file");
			} else if (actionName == FileManager.ACTION_NEWFOLDER) {
				fileLister.doAddItem("folder");
			} else if (actionName == FileManager.ACTION_OPENINTAB) {
				tabnew(fileLister.getPwd(), FileLister.FS_ROOT);
			} else if (actionName == FileManager.ACTION_OPENINTERMINAL) {
				Util.openTerminal(fileLister.getPwd());
			}
		}
	}

	class FileSelectListener extends SelectionAdapter {
		String actionName;

		public FileSelectListener(String actionName) {
			this.actionName = actionName;
		}

	}

	class MiscListener extends SelectionAdapter {
		int actionName;

		public MiscListener(int actionName) {
			this.actionName = actionName;
		}

		public void widgetSelected(SelectionEvent e) {
			if (actionName == FileManager.MISC_PREFERENCE) {
				Util.openPreferenceShell(shell);
			} else if (actionName == FileManager.MISC_ABOUT) {
				AboutShell aboutShell = new AboutShell();
				aboutShell.showGUI();
			} else if (actionName == FileManager.MISC_CONTENT) {
				String EDITOR = Preference.getInstance().getEditorApp();
				String cmd[] = { EDITOR, "../doc/help.txt" };
				try {
					Runtime.getRuntime().exec(cmd);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (actionName == FileManager.MISC_SEARCHINFOLDER) {
				activeMiniShell(null, "/");
			} else if (actionName == FileManager.MISC_FIND) {
				activeMiniShell(null, ":find");
			} else if (actionName == FileManager.MISC_QUIT) {
				Main.exit();
			}
		}
	}

}
