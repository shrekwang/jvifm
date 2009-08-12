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

package net.sf.jvifm.control;

import net.sf.jvifm.Main;
import net.sf.jvifm.model.Shortcut;
import net.sf.jvifm.model.ShortcutsManager;
import net.sf.jvifm.ui.FileLister;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;

public class CommandParser {

	public Command parseCommand(String cmd, String[] args) throws Exception {
		CommandLineParser parser = new BasicParser();
		CommandLine cmdLine;
		FileLister activeLister = Main.fileManager.getActivePanel();
		ShortcutsManager scm = ShortcutsManager.getInstance();
		String[] selectedFiles = activeLister.getSelectionFiles();
		Command command = null;

		if (cmd.equals("ls")) {
			cmdLine = parser.parse(ListFileCommand.options, args);
			command = new ListFileCommand(cmdLine);
		} else if (cmd.equals("compress")) {
			String dstFile = FilenameUtils.concat(activeLister.getPwd(),
					args[0]);
			command = new CompressCommand(dstFile, selectedFiles);
		} else if (cmd.equals("find")) {
			cmdLine = parser.parse(FindCommand.options, args);
			command = new FindCommand(cmdLine);
		} else if (cmd.equals("rename")) {
			cmdLine = parser.parse(RenameCommand.options, args);
			command = new RenameCommand(cmdLine);
		} else if (cmd.equals("touch")) {
			cmdLine = parser.parse(TouchCommand.options, args);
			command = new TouchCommand(cmdLine, selectedFiles);
		} else if (cmd.equals("mkdir")) {
			cmdLine = parser.parse(new Options(), args);
			command = new MkdirCommand(cmdLine);
		} else if (MetaCommand.isMetaCommand(cmd)) {
			command = new MetaCommand(cmd);
		} else if (scm.isShortCut(cmd)) {
			Shortcut cc = scm.findByName(cmd);
			command = new SystemCommand(cc.getText(), args, true);
		} else if (cmd.startsWith("!")) {
			command = new SystemCommand(cmd.substring(1), args, false);
		} else {
			cmdLine = parser.parse(new Options(), args);
			command = new MiscFileCommand(activeLister.getPwd(), cmd, cmdLine
					.getArgs(), selectedFiles);
		}

		command.setFileLister(activeLister);
		command.setInActiveFileLister(Main.fileManager.getInActivePanel());
		command.setPwd(activeLister.getPwd());

		return command;
	}

}
