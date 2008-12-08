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

public class ZipListerKeyListener extends KeyAdapter{
	private static String INIT="INIT";
	private static String IMPEND="IMPEND";
	private static String DONE="DONE";
	
	private ZipLister zipLister;
	private String mode=INIT;
	private StringBuffer commandBuffer=new StringBuffer();
	private StringBuffer countBuffer=new StringBuffer();
	private Pattern oneCharCmds=null;
	private Pattern twoCharCmds=null;
	
    private Matcher oneCharCmdMatcher =null;
    private Matcher twoCharCmdMatcher =null;
    private FileManager fileManager=Main.fileManager;
 
	
	public ZipListerKeyListener(ZipLister zipLister) {
		super();
		this.zipLister=zipLister;
	    oneCharCmds =  Pattern.compile("[bftvhjklosrxiHML^@$GpP:/nNM ]");
        twoCharCmds =  Pattern.compile("[cgdDyYzm']");
        //impendCharCmds =  Pattern.compile("[cgdDyYhtT/]");
		
	}
	public void keyPressed(KeyEvent event) {

	    	event.doit=false;
	    	
	    	if (event.keyCode==SWT.SHIFT) {
	    		return;
	    	}
	    	if (event.character=='\b') {
	    		zipLister.upOneDir();
	    	}
	    	if (event.character=='d' && event.stateMask==SWT.ALT) {
	    		//zipLister.editAddress();
	    		return;
	    	}
	    	if (event.keyCode==SWT.ESC){
	    		zipLister.cancelOperate();
	    		return;
	    	}
	    	if (event.keyCode == SWT.F5 ) {
	    		zipLister.refresh();
	    		return;
	    	}
	    	//if (zipLister.getOperateMode()==ZipLister.ORIG_MODE) {
	    		//event.doit=true;
	    		//return;
	    	//}
	    	
	    	
	    	String keyStr=String.valueOf(event.character);

            oneCharCmdMatcher =  oneCharCmds.matcher(keyStr);
            twoCharCmdMatcher = twoCharCmds.matcher(keyStr);
          
            
            if (mode.equals(INIT)) {
            	
            	 if (event.character>='0' && event.character<='9'){
            		 countBuffer.append(event.character);
	            	 fileManager.setTipInfo(countBuffer.toString()+commandBuffer.toString());
            	 } else  if (oneCharCmdMatcher.matches()) {
            		 commandBuffer.append(event.character);
                	mode=DONE;
                 }  else if (twoCharCmdMatcher.matches()) {
                	 commandBuffer.append(event.character);
                	 mode=IMPEND;
	            	 fileManager.setTipInfo(countBuffer.toString()+commandBuffer.toString());
                 }
        	 }  else  if (mode.equals(IMPEND)) {
            
        		commandBuffer.append(event.character);
            	fileManager.setTipInfo(countBuffer.toString()+commandBuffer.toString());
            	mode=DONE;
            }
            if (mode.equals(DONE)) {
            	fileManager.setTipInfo("");
            	doAction();
            	commandBuffer.delete(0, commandBuffer.length());
            	countBuffer.delete(0, countBuffer.length());
            	mode=INIT;
            }
            
	}
	
	private void doAction() {
		String cmd=commandBuffer.toString();
		int count=1;
		if (countBuffer.length()>0) {
			count=Integer.parseInt(countBuffer.toString());
		}
		if (cmd.length()==1) {
			char character=cmd.charAt(0);
			switch (character){
	    	case 't': zipLister.tagCurrentItem(); break;
	    	case 'v': zipLister.switchToVTagMode(); break;
    		//case 'i' : zipLister.switchToOrigMode(); break;
	    	
	    	case 'k': zipLister.cursorUp(count); break;
    		case 'j': zipLister.cursorDown(count); break;
    		case 'l': zipLister.enterPath(); break;
    		case 'h': zipLister.upOneDir(); break;
    		case 'o': zipLister.openWithDefault(); break;
    		
    		case '@': Main.fileManager.activeSideView(); break;
    		
    		case 'H': zipLister.cursorHead(); break;
    		case 'M': zipLister.cursorMiddle(); break;
    		case 'L': zipLister.cursorLast(); break;
    		case '^': zipLister.cursorTop(); break;
    		case '$': zipLister.cursorBottom(); break;
    		case 'G': zipLister.cursorBottom(); break;
    		case 'p': zipLister.doPaste(); break;
    		//case 'P': zipLister.doPasteFromClipboard(); break;
    		
    		case ' ': zipLister.switchPanel(); break;
    		case ':': fileManager.activeCommandMode(zipLister); break;
    		case '/': fileManager.activeSearchMode(zipLister); break;
			
    		//case 'f': zipLister.forward(count); break;
    		//case 'b':zipLister.back(count); break;
    		
    		case 'n': zipLister.searchNext(true); break;
    		case 'N': zipLister.searchNext(false); break;
    		//case 's': zipLister.addshortcuts(); break;
    		
			}
		} else {
			
			if (cmd.equals("cc")) {
				zipLister.doChange();
			}
			if (cmd.equals("dd")) {
				zipLister.doCut();
			}
			if (cmd.equals("gg") ) {
				zipLister.cursorTop();
			}
			if (cmd.equals("yy")) {
				zipLister.doYank();
			}
			if (cmd.equals("YY")) {
				//zipLister.doCopyToClipboard();
			}
			if (cmd.equals("gh")) {
				//zipLister.visit(HomeLocator.getUserHome());
				//zipLister.refreshHistoryInfo();
			}
			if (cmd.equals("g/") ) {
				//zipLister.visit(ZipLister.FS_ROOT);
				//zipLister.refreshHistoryInfo();
			}
			if (cmd.equals("gt")) {
				fileManager.switchToNextTab();
			}
			if (cmd.equals("gT")) {
				fileManager.switchToPrevTab();
			}
			if (cmd.equals("DD")) {
				zipLister.doDelete();
			}
			if (cmd.equals("zz")) {
				zipLister.pack();
			}
			if (cmd.equals("zo")) {
				//zipLister.doUnCompress();
			}
			if (cmd.equals("zc")) {
				//zipLister.doCompress();
			}
			
		}
		
	}

}
