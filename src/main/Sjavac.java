package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sjavac {
    private static final int DIRECTORY_PLACE = 0;
    private static final int ILLEGAL_CODE = 1;
    private static final int LEGAL_CODE = 0;
    private static final int IOPROMBLEM = 2;
    private static final String IGNORE_LINE = "\\s | [^\\\\]";
    private static final String validSuffixOnce = "$[;{}]";

    /**
     * Creates an Arraylist of String based on the lines in the file.
     * Empty lines and comments are ignored.
     * @param file The file to check
     * @return Array list of Strings representing the lines.
     * @throws IOException in case there was a problem with reading the file.
     */
    private static ArrayList<String> parseData(File file) throws IOException{
        ArrayList<String> lines = new ArrayList<>();
        FileReader reader = new FileReader(file);
        BufferedReader buff = new BufferedReader(reader);
        String curLine = buff.readLine();
        while(curLine != null){
            Pattern p = Pattern.compile(IGNORE_LINE);
            Matcher m = p.matcher(curLine);
            if(m.matches())
                continue;
            else{
                p = Pattern.compile(validSuffixOnce);
                m = p.matcher(curLine);
                if(m.matches())
                    lines.add(curLine.trim()); //Ignores spaces at the beginning and the end of the line
                else
                    throw new IOException("INVALID LINE SUFFIX");
            }
            curLine = buff.readLine();
        }
        reader.close();
        buff.close();
        return lines;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println(IOPROMBLEM);
            System.err.println("INVALID SYSTEM ARGUMENTS.");
        }else {
            File pathName = new File(args[DIRECTORY_PLACE]);
            try{
                ArrayList<String> lines = parseData(pathName);
                CommandFactory.check(lines);
                System.out.println(LEGAL_CODE);
            } catch (IOException e) {
                System.out.println(IOPROMBLEM);
                System.err.println("INVALID DIRECTORY.");
            }
        }
    }
}
