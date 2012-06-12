package edu.fudan.tbfetcher.compress;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip {
	static final int BUFFER = 2048;

	public static void main (String argv[]) {
	      try {
	         BufferedInputStream origin = null;
	         FileOutputStream dest = new 
	           FileOutputStream("/home/gschen/myfigs.zip");
	         CheckedOutputStream checksum = new 
	           CheckedOutputStream(dest, new Adler32());
	         ZipOutputStream out = new 
	           ZipOutputStream(new 
	             BufferedOutputStream(checksum));
	         //out.setMethod(ZipOutputStream.DEFLATED);
	         byte data[] = new byte[BUFFER];
	         // get a list of files from current directory
	         File f = new File(".");
	         String files[] = f.list();

	         for (int i=0; i<files.length; i++) {
	            System.out.println("Adding: "+files[i]);
	            FileInputStream fi = new 
	              FileInputStream(files[i]);
	            origin = new 
	              BufferedInputStream(fi, BUFFER);
	            ZipEntry entry = new ZipEntry(files[i]);
	            out.putNextEntry(entry);
	            int count;
	            while((count = origin.read(data, 0, 
	              BUFFER)) != -1) {
	               out.write(data, 0, count);
	            }
	            origin.close();
	         }
	         out.close();
	         System.out.println("checksum:"+checksum.getChecksum().getValue());
	      } catch(Exception e) {
	         e.printStackTrace();
	      }
	   }
}
