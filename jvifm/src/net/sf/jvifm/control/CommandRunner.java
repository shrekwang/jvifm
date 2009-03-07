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
import net.sf.jvifm.ui.Util;

import org.eclipse.swt.widgets.Display;

public class CommandRunner {

	private static CommandRunner instance = new CommandRunner();

	private CommandRunner() {
	}

	public static CommandRunner getInstance() {
		return instance;
	}

	public void run(final Command command) {

		Thread thread = new Thread() {

			public void run() {
				if (command instanceof MiscFileCommand
						|| command instanceof MetaCommand) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							try {
								command.execute();
							} catch (Exception e) {
								Util.openMessageWindow(e.getMessage());
								e.printStackTrace();
							}
						}
					});

				} else {

					command.showStatusAnimation();
					try {
						command.execute();
					} catch (Exception e) {
						Util.openMessageWindow(e.getMessage());
					}
					command.hideStatusAnimation();
				}
			}

		};
		thread.start();
		if (command instanceof InterruptableCommand)
			Main.currentJob = (InterruptableCommand) command;
	}

}
