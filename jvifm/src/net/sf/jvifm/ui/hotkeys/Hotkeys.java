/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
 *                  Kovacs Zsolt (kovacs.zsolt.85@gmail.com)
 *
 *  This file is part of Coopnet.
 *
 *  Coopnet is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Coopnet is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Coopnet.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.jvifm.ui.hotkeys;

import java.awt.event.KeyEvent;
import java.io.File;

import net.sf.jvifm.Main;

public class Hotkeys {

    private static int ACTION_SHOWMAIN =88; 
    private static int ACTION_QUICKRUN = 89;
    private static HotkeyHandler handler;
    
    static{
        switch (Main.operatingSystem) {
            case 2:
                System.loadLibrary("JIntellitype");
                handler = new JIntellitypeHandler();
                break;
            case 1:
                System.loadLibrary("JXGrabKey");
                handler = new JXGrabKeyHandler();
                break;
        }
    }

    private Hotkeys() {}

    public static void bindKeys() {
        handler.registerHotkey(ACTION_SHOWMAIN, KeyEvent.CTRL_MASK | KeyEvent.ALT_MASK, KeyEvent.VK_J);
        handler.registerHotkey(ACTION_QUICKRUN, KeyEvent.CTRL_MASK | KeyEvent.ALT_MASK, KeyEvent.VK_H);
    }

    public static void unbindKeys() {
        handler.unregisterHotkey(ACTION_SHOWMAIN);
        handler.unregisterHotkey(ACTION_QUICKRUN);
    }

    public static void reBind() {
        unbindKeys();
        bindKeys();
    }

    public static void cleanUp() {
        handler.cleanUp();
    }

    protected static void onHotkey(int id) {
    	Main.fileManager.onHotKey(id);
    }
}
