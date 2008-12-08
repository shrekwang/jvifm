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
import java.util.ArrayList;
import java.util.List;

import net.sf.jvifm.Main;
import net.sf.jvifm.control.Command;
import net.sf.jvifm.control.CommandParser;
import net.sf.jvifm.control.CommandRunner;
import net.sf.jvifm.util.AutoCompleteUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;

public class MiniShell {
	private FileManager fileManager;
	private ViLister lister;
	private StyledText styledText;
	private String tip = null;
	private CommandRunner commandRunner=CommandRunner.getInstance();
	private String[] completeOptions=null;
	private int completeIndex=0;
	private ArrayList commandhistory=new ArrayList();
	private int currentCommand=-99;

	private List completionFileList=null;
	private int currentSelectionCompletion=0;

	public MiniShell(Composite parent, int style) {
		
		styledText=new StyledText(parent, style);
		fileManager = Main.fileManager;
	

		styledText.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				e.doit=false;
			}
			
		});
		
		styledText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
    			if (styledText.getText().startsWith("/")) {
    				doSearch();
    			}
			}
		});
		
		styledText.addVerifyKeyListener(new VerifyKeyListener() {
			public void verifyKey(VerifyEvent e) {
				if (e.character!=SWT.TAB) {
					completionFileList=null;
					currentSelectionCompletion=0;
					
				}
				if (e.keyCode == SWT.ESC) {
					cancelOperate();
					return;
				}
				if (e.keyCode==SWT.ARROW_UP) {
					if (commandhistory.size()<1) return;
					if (currentCommand==-99) {
						currentCommand=commandhistory.size()-1;
						styledText.setText((String)commandhistory.get(commandhistory.size()-1));
					} else {
						if (currentCommand>0) currentCommand--;
						styledText.setText((String)commandhistory.get(currentCommand));
					}
					styledText.setCaretOffset(styledText.getText().length());
					styledText.setFocus();
					return;
				} else if (e.keyCode==SWT.ARROW_DOWN) {
					if (commandhistory.size()<1) return;
					if (currentCommand==-99) {
						return ;
					} else {
						if (currentCommand<commandhistory.size()-1) currentCommand++;
						styledText.setText((String)commandhistory.get(currentCommand));
					}
					styledText.setCaretOffset(styledText.getText().length());
					styledText.setFocus();
				}
				switch (e.character) {
				case SWT.CR:
					doCommand();
					break;
				case SWT.TAB:
					doAutoComplete();
					e.doit=false;
					styledText.setFocus();
					break;
				}
			}
			
		});
	
	

	}

	private void doSearch() {
		if (lister == null || lister.isDisposed()) {
			fileManager.getActivePanel().incSearch(styledText.getText().substring(1), true, true);
		} else {
			lister.incSearch(styledText.getText().substring(1), true, true);
		}
	}

	

	private void cancelOperate() {
		styledText.setText("");
		 currentCommand=-99;
		if (lister == null || lister.isDisposed()) {
			fileManager.activePanel();
		} else {
			lister.setFocus();
			lister = null;
		}
	}

	private void doCommand() {
		 currentCommand=-99;
		if (lister == null || lister.isDisposed()) {
			fileManager.activePanel();
		} else {
			lister.setFocus();
			lister = null;
		}
		if (styledText.getText().startsWith(":")) {
			
			commandhistory.add(styledText.getText()); //add to command history
			
			String cmdText=styledText.getText().substring(1).trim();
			String cmd;
			String[] args;
			if (cmdText.indexOf(" ") >0 ) {
				cmd=cmdText.substring(0,cmdText.indexOf(" "));
				args=replaceVariableValue(cmdText.substring(cmdText.indexOf(" ")+1)).split("(?<=[^\\\\\\\\])\\s+");
			} else {
				cmd=cmdText;
				args=null;
			}
			if (args!=null ) {
				for (int i=0; i<args.length; i++) {
					args[i]=args[i].replaceAll("\\\\\\\\ ", " ");
				}
			}
			
			try {
    			Command command=new CommandParser().parseCommand(cmd,args);
    			commandRunner.run(command);
			} catch (Exception e ) {
	    		Util.openMessageWindow(e.getMessage());
				e.printStackTrace();
			}
		}
		styledText.setText("");
	}

	private void doAutoComplete() {
		
		String cmdText=styledText.getText();
		
		String pwd=Main.fileManager.getActivePanel().getPwd();
		final String[] tokens = cmdText.split("(?<=[^\\\\\\\\])\\s+");
		String cmd=tokens[0].substring(1);
		String lastToken=tokens[tokens.length-1];
		
		if (completionFileList!=null) {
			doCompletionFileList(lastToken,pwd,cmd,cmdText);
			styledText.setCaretOffset(styledText.getText().length());
			return;
		}
		
	
		if (tokens.length == 1) {
			completeOptions = AutoCompleteUtil.getCommandCompleteList(tokens[0] .substring(1));
			if (completeOptions==null) return;
			
			styledText.setText(":"+completeOptions[0]);
			StringBuffer sb=new StringBuffer();
			for (int i=0;i<completeOptions.length; i++) {
				sb.append("["+completeOptions[i]+"]  ");
			}
			Main.fileManager.setTipInfo(sb.toString());
			styledText.setCaretOffset(styledText.getText().length());
		} else {
			
			lastToken=lastToken.replaceAll("\\\\\\\\ "," ");
			
			completionFileList= AutoCompleteUtil.getFileCompleteList(pwd,lastToken,true);
			
			if (completionFileList != null && completionFileList.size() > 0) {
				doCompletionFileList(lastToken,pwd,cmd,cmdText);
			}
			
			styledText.setCaretOffset(styledText.getText().length());
		}
	}
	
	private void doCompletionFileList(String lastToken,String pwd,String cmd,String cmdText) {
		
		if (currentSelectionCompletion==completionFileList.size()) currentSelectionCompletion=0;
		File file=(File)completionFileList.get(currentSelectionCompletion++);
		if (completionFileList.size()==1) completionFileList=null;
		String filePath=file.getPath().replaceAll(" ", "\\\\\\\\ ");
		String newCmdText="";
		//absolute path
		if (lastToken.startsWith(File.separator) || ( lastToken.length()>1 &&
				lastToken.charAt(1)==':' ) ) {
			newCmdText=cmdText.substring(0,cmd.length()+2)+filePath+File.separator;
		} else { //relative path
    		pwd=pwd.replaceAll(" ", "\\\\\\\\ ");
    		if (pwd.endsWith(File.separator)) pwd=pwd.substring(0,pwd.length()-1);
			if (pwd.equals("/")) {
				newCmdText=cmdText.substring(0,cmd.length()+2)+filePath.substring(1)+File.separator;
			} else {
				newCmdText=cmdText.substring(0,cmd.length()+2)+filePath.substring(pwd.length()+1)+File.separator;
			}
		}
		styledText.setText(newCmdText);
		StringBuffer sb=new StringBuffer();
		if (completionFileList!=null) {
            for (int i = 0; i<completionFileList.size(); i++) {
			   file=(File)completionFileList.get(i);
               sb.append("[").append(file.getName()).append("] ");
            }
		}

		Main.fileManager.setTipInfo(sb.toString());

	}

	public void activeWith(String text) {
		setText(text);
		styledText.setCaretOffset(text.length());
	}

	private void setText(String text) {
		this.styledText.setText(text);
	}

	public String getText() {
		return styledText.getText();
	}

	public void setViLister(ViLister lister) {
		this.lister = lister;
	}
	public void setFocus() {
		styledText.setFocus();
		
	}
	public void setLayoutData(Object data) {
		styledText.setLayoutData(data);
	}
	
	
	private String replaceVariableValue(String variable) {
		FileLister activePanel = Main.fileManager.getActivePanel();
		FileLister inActivePanel = Main.fileManager.getInActivePanel();

		if (activePanel!=null) {
			if (variable.indexOf("%f") >= 0 ) {
				variable= variable.replaceAll("%f", normPath(concatStringArray(activePanel.getSelectionFiles())));
			} 
			if (variable.indexOf("%d") >= 0) {
				variable=variable.replaceAll("%d", normPath(activePanel.getPwd()));
			} 
		}
		
		if (inActivePanel !=null) {
			if (variable.indexOf("%F") >= 0) {
				variable= variable.replaceAll("%F",normPath(concatStringArray(inActivePanel.getSelectionFiles())));
			} 
			if (variable.indexOf("%D") >= 0 ) {
				variable=variable.replaceAll("%D", normPath(inActivePanel.getPwd()));
			}
		}
		return variable;
	}
	
	private String normPath(String path) {
		return path.replaceAll("\\\\","\\\\\\\\");
	}
	

	private String concatStringArray(String[] values) {
		if (values == null) return "";
		if (values.length==1) return values[0];
		
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < values.length; i++) {
			sb.append(values[i]).append(" ");
		}
		return sb.toString();

	}

}
