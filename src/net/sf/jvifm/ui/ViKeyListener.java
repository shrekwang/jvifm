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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jvifm.Main;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

public class ViKeyListener extends KeyAdapter {
	private static String INIT="INIT";
	private static String IMPEND="IMPEND";
	private static String DONE="DONE";
	
	private ViLister viLister;
	private String mode=INIT;
	private StringBuffer commandBuffer=new StringBuffer();
	private StringBuffer countBuffer=new StringBuffer();
	private Pattern oneCharCmds=null;
	private Pattern twoCharCmds=null;
	
    private Matcher oneCharCmdMatcher =null;
    private Matcher twoCharCmdMatcher =null;
    private FileManager fileManager=Main.fileManager;
 
	
	public ViKeyListener(ViLister viLister) {
		this.viLister=viLister;
	    oneCharCmds =  Pattern.compile("[bftvhjklomsrxHML^@$GpP:/nN ]");
        twoCharCmds =  Pattern.compile("[cgdDyYz]");
		
	}
	public void keyPressed(KeyEvent event) {

	    	event.doit=false;
	    	
	    	if (event.keyCode==SWT.SHIFT) {
	    		return;
	    	}
	    
	    	if (event.keyCode == SWT.ESC || (event.keyCode=='[' && event.stateMask == SWT.CTRL)) {
	    		viLister.cancelOperate();
	    		return;
	    	}
	    	if (event.keyCode == SWT.F5 ) {
	    		viLister.refresh();
	    		return;
	    	}
	    	
	    	String keyStr=String.valueOf(event.character);

            oneCharCmdMatcher =  oneCharCmds.matcher(keyStr);
            twoCharCmdMatcher = twoCharCmds.matcher(keyStr);
          
            
            if (mode.equals(INIT)) {
            	
            	 if (event.character>='0' && event.character<='9'){
            		 countBuffer.append(event.character);
	            	 fileManager.setStatusInfo(countBuffer.toString()+commandBuffer.toString());
            	 } else  if (oneCharCmdMatcher.matches()) {
            		 commandBuffer.append(event.character);
                	mode=DONE;
                 }  else if (twoCharCmdMatcher.matches()) {
                	 commandBuffer.append(event.character);
                	 mode=IMPEND;
	            	 fileManager.setStatusInfo(countBuffer.toString()+commandBuffer.toString());
                 }
        	 }  else  if (mode.equals(IMPEND)) {
            
        		commandBuffer.append(event.character);
            	fileManager.setStatusInfo(countBuffer.toString()+commandBuffer.toString());
            	mode=DONE;
            }
            if (mode.equals(DONE)) {
            	fileManager.setStatusInfo("");
            	doAction();
            	commandBuffer.delete(0, commandBuffer.length());
            	countBuffer.delete(0, countBuffer.length());
            	mode=INIT;
            }
            
	}
	
	public String getCmd() {
		return commandBuffer.toString();
	}
	
	protected void doAction() {
		String cmd=getCmd();
		int count=1;
		if (countBuffer.length()>0) {
			count=Integer.parseInt(countBuffer.toString());
		}
		if (cmd.length()==1) {
			char character=cmd.charAt(0);
			switch (character){
	    	case 't': viLister.tagCurrentItem(); break;
	    	case 'v': viLister.switchToVTagMode(); break;
	    	
	    	case 'k': viLister.cursorUp(count); break;
    		case 'j': viLister.cursorDown(count); break;
    		case 'l': viLister.enterPath(); break;
    	
    		case 'H': viLister.cursorHead(); break;
    		case 'M': viLister.cursorMiddle(); break;
    		case 'L': viLister.cursorLast(); break;
    		case '^': viLister.cursorTop(); break;
    		case '$': viLister.cursorBottom(); break;
    		case 'G': viLister.cursorBottom(); break;
    		case 'p': viLister.doPaste(); break;
    		case ' ': viLister.switchPanel(); break;
    		case '@': viLister.switchPanel(); break;
    		case ':': fileManager.activeMiniShell(viLister, ":"); break;
    		case '/': fileManager.activeMiniShell(viLister, "/"); break;
			
    		case 'n': viLister.searchNext(true); break;
    		case 'N': viLister.searchNext(false); break;
    		case 'r': viLister.refresh(); break;
    		
			}
		} else {
			if (cmd.equals("cc")) {
				viLister.doChange();
			}
			if (cmd.equals("dd")) {
				viLister.doCut();
			}
			if (cmd.equals("gg") ) {
				viLister.cursorTop();
			}
			if (cmd.equals("yy")) {
				viLister.doYank();
			}
			if (cmd.equals("DD")) {
				viLister.doDelete();
			}
		
			
		}
		
	}
}
