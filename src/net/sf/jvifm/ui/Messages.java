package net.sf.jvifm.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "net.sf.jvifm.ui.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public static final String msgOptionYes=Messages.getString("Messagebox.optionYes");
	public static final String msgOptionNo=Messages.getString("Messagebox.optionNo");
	public static final String msgOptionYesToAll=Messages.getString("Messagebox.optionYesToAll");
	public static final String msgOptionNoToAll=Messages.getString("Messagebox.optionNoToAll");
	public static final String msgOptionCancel=Messages.getString("Messagebox.optionCancel") ;
	
	public static final String msgFileReplace =Messages.getString("CopyCommand.confirmFileReplaceDlgMsg");
	public static final String msgFolderReplace =Messages.getString("CopyCommand.confirmFolderReplaceDlgMsg");
	public static final String msgCpConfirmDlgTitle=Messages.getString("CopyCommand.confirmDlgTitle");
	
	public static final String msgRmConfirmDlgTitle=Messages.getString("RemoveCommand.confirmDlgTitle");
	public static final String msgFileDelete=Messages.getString("RemoveCommand.confirmFileDelete");
	
}
