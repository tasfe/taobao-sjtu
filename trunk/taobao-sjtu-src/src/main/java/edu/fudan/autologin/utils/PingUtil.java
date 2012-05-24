package edu.fudan.autologin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PingUtil {

	/*
	**
	 * 能否ping通IP地址
	 * @param server IP地址
	 * @param timeout 超时时长
	 * @return true能ping通
	 */
	public static boolean pingServer(String server, int timeout) {
       BufferedReader in = null;
       Runtime r = Runtime.getRuntime();

       String pingCommand = "ping " + server + " -n 1 -w " + timeout;
       try {
           Process p = r.exec(pingCommand);
           if (p == null) {
               return false;
           }
           in = new BufferedReader(new InputStreamReader(p.getInputStream()));
           String line = null;
           while ((line = in.readLine()) != null) {
               if (line.startsWith("Reply from")) {
                   return true;
               }
           }

       } catch (Exception ex) {
           ex.printStackTrace();
           return false;
       } finally {
           try {
               in.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
       return false;
   }
}
