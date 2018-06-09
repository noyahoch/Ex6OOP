package main;

import block.*;
import variable.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandFactory {
    ArrayList<Block> blocks;
    ArrayList<Variable> vars;
    private final String validSuffixOnce = "[$;{}]{1}";
    enum VarTypes {INT, DOUBLE, CHAR, BOOLEAN, String}
    enum BlockTypes {WHILE, IF, VOID}


    /**
     * Checks the if the lines in the file are valid.
     * @param lines
     * @return
     */
    boolean check (ArrayList<String> lines){
        for (String line : lines){
            Pattern p = Pattern.compile(validSuffixOnce);
            Matcher m = p.matcher(line);
            String firstWord = line.split(" ")[0];
            if (!m.matches())
                return false;
            } else if(firstWord.toUpperCase()== BlockTypes.values()){

            } else if (firstWord.){

            } else {
            
        }
    }
}
