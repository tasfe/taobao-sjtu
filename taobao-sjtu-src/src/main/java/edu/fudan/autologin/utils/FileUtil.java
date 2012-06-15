package edu.fudan.autologin.utils;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileUtil {

	public static void writeStringToFile(String content, String path) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			out.write(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/** Read the contents of the given file. */
	public static String read(String fileName) {
		StringBuilder text = new StringBuilder();
		String NL = System.getProperty("line.separator");
		Scanner scanner;
		try {
			scanner = new Scanner(new FileInputStream(fileName), "utf-8");
			try {
				while (scanner.hasNextLine()) {
					text.append(scanner.nextLine() + NL);
				}
			} finally {
				scanner.close();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String target = text.toString();
		scanner = null;
		text = null;
		
		return target;
	}

}
