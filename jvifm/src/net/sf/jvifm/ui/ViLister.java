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

public interface ViLister {



    public void cursorDown();
    public void cursorDown(int count);
    public void cursorUp();
    public void cursorUp(int count);

    public void cursorHead();
    public void cursorLast();
    public void cursorMiddle();

    public void cursorTop();
    public void cursorBottom();

    public void enterPath();
   
    public void upOneDir();

    //public void changePanel();
    public void cancelOperate();

    public void searchNext(boolean isForward) ;
    
    public void doPaste() ;
    public void doDelete() ;
    public void doChange();
    public void doCut();
    public void doYank();
    

    public void  tagCurrentItem();
    public void switchToVTagMode();
    public void switchPanel();
    
    public void incSearch(String pattern,boolean isForward, boolean isIncrease);

    public void redraw();
    public void refresh();
    public boolean isDisposed();
    public boolean setFocus();
    public void dispose();
	 public void activeWidget() ;
	
	
}
