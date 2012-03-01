package net.sf.jvifm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import net.sf.jvifm.Main;
import net.sf.jvifm.ui.FileLister;
import net.sf.jvifm.ui.FileManager;

import org.eclipse.swt.widgets.Display;

public class FileListerServer extends Thread {

   ServerSocket ss;

   public FileListerServer() throws IOException {
      ss = new ServerSocket(9999);
   }

   public void run() {
      while (true) {
         try {
             Socket s = ss.accept();
             BufferedReader is = new BufferedReader(
                     new InputStreamReader(s.getInputStream()));
             final String path = is.readLine();

             Display.getDefault().syncExec(new Runnable() {
                 public void run() {
                     FileManager fileManager = Main.fileManager;
                     FileLister activeLister = fileManager.getActivePanel();
                     fileManager.activeGUI();
                     activeLister.visit(path);
                 }
             });

             System.out.println(path);
         }catch (Exception e) {
             e.printStackTrace();
         }
      }
   }
}

