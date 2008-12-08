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

package net.sf.jvifm;

public class CommandBuffer {
	
	private static CommandBuffer instance=new CommandBuffer();
	
	private String impendingCommand=null;
	private String commandCount=null;
	private String commandBuffer=null;
	private String[] commandSourceFiles=null;
	private String commandSourcePath=null;
	
	private CommandBuffer() {
	}
	
	public static CommandBuffer getInstance() {
		return instance;
	}
	
	public void setImpendingCommand(String command) {
	    this.impendingCommand=command;
	}
	public String getImpendingCommand() {
	    return this.impendingCommand;
	}
	public void setCommandCount(String countString) {
	    this.commandCount=countString;
	}
	public String getCommandCount() {
	    return this.commandCount;
	}
	public void setCommandBuffer(String commandBuffer) {
	    this.commandBuffer=commandBuffer;
	}
	public String getCommandBuffer() {
	    return this.commandBuffer;
	}
	public void setCommandSourceFiles(String[] commandSource) {
	    this.commandSourceFiles=commandSource;
	}
	public String[] getCommandSourceFiles() {
	    return this.commandSourceFiles;
	}
	
	public String getCommandSourcePath() {
		return commandSourcePath;
	}

	public void setCommandSourcePath(String commandSourcePath) {
		this.commandSourcePath = commandSourcePath;
	}

}
