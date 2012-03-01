package net.sf.jvifm.model;

public interface FileListerListener {
	
	public void onChangeSelection(String fullpath);
	
	public void onChangePwd(String oldPath, String newPath);

}
