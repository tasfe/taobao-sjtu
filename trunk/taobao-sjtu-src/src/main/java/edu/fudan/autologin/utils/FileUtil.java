package edu.fudan.autologin.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class FileUtil {

	public static String readFile(String filePath) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filePath);
            BufferedReader reader = new BufferedReader(fileReader);
            return readAll(reader);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                fileReader.close();
            } catch (Exception ex) {
            }
        }
    }

    public static String read1(String filePath) {
        FileInputStream inputstream = null;

        InputStreamReader reader = null;
        BufferedReader br = null;

        try {
            inputstream = new FileInputStream(filePath);

            reader = new InputStreamReader(inputstream, "utf-8");

            br = new BufferedReader(reader);

            return readAll(br);

        } catch (IOException ex2) {
            ex2.printStackTrace();
            return null;
        } finally {
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (IOException ex) {
                }
            }

        }

    }

    private static String readAll(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();

        String line = null;
        while ((line = br.readLine()) != null) {

            sb.append(line).append("\r\n");
        }

        return sb.toString();
    }
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
		return readFile(fileName);
	}

}
