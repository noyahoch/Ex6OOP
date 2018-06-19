package oop.ex6.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that parses an Sjavac file and checks its validity.
 */

public class Sjavac {
	private static final int DIRECTORY_PLACE = 0;
	private static final int ILLEGAL_CODE = 1;
	private static final int LEGAL_CODE = 0;
	private static final int IO_PROBLEM = 2;

	private static final String LINE_SUFFIX_MESSAGE = "INVALID LINE SUFFIX";
	private static final String NUM_OF_ARGUMENTS_MESSAGE = "INVALID SYSTEM ARGUMENTS";
	private static final String IGNORE_LINE = "\\s*|//.*";
	private static final String validSuffixOnce = ".*[;{}]\\s*$";

	/**
	 * Creates an Arraylist of String based on the lines in the file,
	 * Empty lines and comments are ignored, shallow checks are made.
	 * @param file The file to check
	 * @return Array list of Strings representing the lines.
	 * @throws IOException in case there was a problem with reading the file.
	 * @throws ReadingCodeException if the code is incorrect
	 */
	private static ArrayList<String> parseData(File file) throws ReadingCodeException, IOException {
		ArrayList<String> lines = new ArrayList<>();
		FileReader reader = new FileReader(file);
		BufferedReader buff = new BufferedReader(reader);
		String curLine = buff.readLine();
		while (curLine != null) {
			Pattern p = Pattern.compile(IGNORE_LINE);
			Matcher m = p.matcher(curLine);
			if (!m.matches()) {
				p = Pattern.compile(validSuffixOnce);
				m = p.matcher(curLine);
				if (m.matches())
					lines.add(curLine.trim());
				else
					throw new SyntaxException(LINE_SUFFIX_MESSAGE);
			}
			curLine = buff.readLine();
		}
		reader.close();
		buff.close();
		return lines;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println(IO_PROBLEM);
			System.err.println(NUM_OF_ARGUMENTS_MESSAGE);
		} else {
			File pathName = new File(args[DIRECTORY_PLACE]);
			try {
				ArrayList<String> lines = parseData(pathName);
				CodeReader.checkCode(lines);
				System.out.println(LEGAL_CODE);
			} catch (IOException e) {
				System.out.println(IO_PROBLEM);
				System.err.println(e.getMessage());
			} catch (ReadingCodeException e) {
				System.out.println(ILLEGAL_CODE);
				System.err.println(e.getMessage());
			}
		}
	}
}
