/** 
 
 * @author wangsn
 * @since 1.0
 * @version $Id: TipOption.java,v 1.1 2008/11/10 10:35:23 bignemo Exp $
 */
package net.sf.jvifm.model;

public class TipOption {

	private String tipType;
	private String name;
	private String extraInfo;

	public String getTipType() {
		return tipType;
	}

	public String getName() {
		return name;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setTipType(String tipType) {
		this.tipType = tipType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

}
