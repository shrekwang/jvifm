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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jvifm.Main;
import net.sf.jvifm.model.Bookmark;
import net.sf.jvifm.model.BookmarkManager;
import net.sf.jvifm.ui.shell.QuickRunShell;
import net.sf.jvifm.util.HomeLocator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

public class FileListerKeyListener extends KeyAdapter {
	private static String INIT = "INIT";
	private static String IMPEND = "IMPEND";
	private static String DONE = "DONE";

	private FileLister fileLister;
	private String mode = INIT;
	private StringBuffer commandBuffer = new StringBuffer();
	private StringBuffer countBuffer = new StringBuffer();
	private Pattern oneCharCmds = null;
	private Pattern twoCharCmds = null;

	private Matcher oneCharCmdMatcher = null;
	private Matcher twoCharCmdMatcher = null;
	private FileManager fileManager = Main.fileManager;

	public FileListerKeyListener(FileLister fileLister) {
		super();
		this.fileLister = fileLister;
		oneCharCmds = Pattern.compile("[bftvhjklorxXiHMLS^@$GpP:/nNM ]");
		twoCharCmds = Pattern.compile("[cgdDyYzms']");

	}

	public void keyPressed(KeyEvent event) {

		event.doit = false;

		if (event.keyCode == SWT.SHIFT) {
			return;
		}
		if (event.character == '\b') {
			fileLister.upOneDir();
		}
		if (event.character == 'd' && event.stateMask == SWT.ALT) {
			fileLister.editAddress();
			return;
		}
		if (event.keyCode == SWT.ESC) {
			fileLister.cancelOperate();
			return;
		}
		if (event.keyCode == SWT.F5) {
			fileLister.refresh();
			return;
		}
		if (event.keyCode == SWT.F4) {
			String file = fileLister.getSelectionFiles()[0];
			fileManager.preview(file);
		}
		if (fileLister.isOrigMode() ) {
			event.doit = true;
			return;
		}

		String keyStr = String.valueOf(event.character);

		oneCharCmdMatcher = oneCharCmds.matcher(keyStr);
		twoCharCmdMatcher = twoCharCmds.matcher(keyStr);

		if (mode.equals(INIT)) {

			if (event.character >= '0' && event.character <= '9') {
				countBuffer.append(event.character);
				fileManager.setTipInfo(countBuffer.toString()
						+ commandBuffer.toString());
			} else if (oneCharCmdMatcher.matches()) {
				commandBuffer.append(event.character);
				mode = DONE;
			} else if (twoCharCmdMatcher.matches()) {
				commandBuffer.append(event.character);
				mode = IMPEND;
				fileManager.setTipInfo(countBuffer.toString()
						+ commandBuffer.toString());
			}
		} else if (mode.equals(IMPEND)) {

			commandBuffer.append(event.character);
			fileManager.setTipInfo(countBuffer.toString()
					+ commandBuffer.toString());
			mode = DONE;
		}
		if (mode.equals(DONE)) {
			fileManager.setTipInfo("");
			doAction();
			commandBuffer.delete(0, commandBuffer.length());
			countBuffer.delete(0, countBuffer.length());
			mode = INIT;
		}

	}

	private void doAction() {
		String cmd = commandBuffer.toString();
		int count = 1;
		if (countBuffer.length() > 0) {
			count = Integer.parseInt(countBuffer.toString());
		}
		String pwd = fileLister.getPwd();
		int currentRow = fileLister.getCurrentRow();
		if (cmd.length() == 1) {
			char character = cmd.charAt(0);
			switch (character) {
			case 't':
				fileLister.tagCurrentItem();
				break;
			case 'v':
				fileLister.switchToVTagMode();
				break;
			case 'i':
				fileLister.switchToOrigMode();
				break;

			case 'k':
				fileLister.cursorUp(count);
				break;
			case 'j':
				fileLister.cursorDown(count);
				break;
			case 'l':
				fileLister.enterPath(count);
				break;
			case 'h':
				fileLister.upOneDir(count);
				break;
			case 'o':
				fileLister.openWithDefault();
				break;

			case '@':
				Main.fileManager.activeSideView();
				break;

			case 'H':
				fileLister.cursorHead();
				break;
			case 'M':
				fileLister.cursorMiddle();
				break;
			case 'L':
				fileLister.cursorLast();
				break;
			case '^':
				fileLister.cursorTop();
				break;
			case '$':
				fileLister.cursorBottom();
				break;
			case 'G':
				fileLister.cursorBottom();
				break;
			case 'p':
				fileLister.doPaste();
				break;
			case 'P':
				fileLister.doPasteFromClipboard();
				break;

			case ' ':
				fileLister.switchPanel();
				break;
			case ':':
				fileManager.activeMiniShell(null, ":");
				break;
			case '/':
				fileManager.activeMiniShell(null, "/");
				break;

			case 'f':
				fileLister.forward(count);
				break;
			case 'b':
				fileLister.back(count);
				break;

			case 'n':
				fileLister.searchNext(true);
				break;
			case 'N':
				fileLister.searchNext(false);
				break;
			case 'S':
				fileLister.addshortcuts();
				break;
			case 'r':
				fileLister.refresh();
				break;

			case 'x':
				fileLister.doUnCompress(false);
				break;
			case 'X':
				fileLister.doUnCompress(true);
				break;

			}
		} else {
			if (cmd.startsWith("m")) {
				File file = new File(fileLister.getPwd());
				Bookmark bookmark = new Bookmark();
				bookmark.setName(file.getName());
				bookmark.setPath(file.getPath());
				String key = cmd.substring(1, 2);
				bookmark.setKey(key);
				BookmarkManager bm = BookmarkManager.getInstance();
				bm.add(bookmark);
			}
			if (cmd.startsWith("'")) {
				String key = cmd.substring(1, 2);
				BookmarkManager bm = BookmarkManager.getInstance();
				Bookmark bookmark = bm.getBookmark(key);
				if (bookmark != null) {
					String path = bookmark.getPath();
					fileLister.visit(path);
				}

			}
			if (cmd.equals("cc")) {
				fileLister.doChange();
			}
			if (cmd.equals("dd")) {
				fileLister.doCut();
			}
			if (cmd.equals("gg")) {
				fileLister.cursorTop();
			}
			if (cmd.equals("yy")) {
				fileLister.doYank();
			}
			if (cmd.equals("YY")) {
				fileLister.doCopyToClipboard();
			}
			if (cmd.equals("gh")) {
				fileLister.visit(HomeLocator.getUserHome());
				fileLister.refreshHistoryInfo();
			}
			if (cmd.equals("g/")) {
				fileLister.visit(FileLister.FS_ROOT);
				fileLister.refreshHistoryInfo();
			}
			if (cmd.equals("ss")) {
				QuickRunShell qr = new QuickRunShell();
				qr.open();
			}
			if (cmd.equals("gt")) {
				fileManager.switchToNextTab();
			}
			if (cmd.equals("gT")) {
				fileManager.switchToPrevTab();
			}
			if (cmd.equals("DD")) {
				fileLister.doDelete();
			}
			if (cmd.equals("zz")) {
				fileLister.pack();
			}

		}
		if (!pwd.equals(fileLister.getPwd())
				|| currentRow != fileLister.getCurrentRow()) {
			fileLister.notifyChangeSelection();
		}

	}

}
