package main;

import block.*;
import variable.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommandFactory {
    ArrayList<Block> blocks;
    ArrayList<Variable> vars;


    private static final String VAR_TYPE = "(final )?(int|double|String|char|boolean)";
    private static final String BLOCK_TYPE = "while|if|void";
    private static final String END_BLOCK = "}";
    private static final String RETURN = "return;";
    private static final String INVALID_LINE = "INVALID";
    private static final String VAR_ASSIGN = Variable.VARIABLE_PATTERN_NAME+" *= * .+;";

    private static final String[] regexes = new String[]{VAR_TYPE,BLOCK_TYPE,END_BLOCK,RETURN};
    private static Block currentBlock = null;


    /**
     * Checks the if the lines in the file are valid.
     * @param lines
     * @return
     */
    static void check (ArrayList<String> lines) throws IOException {
        for (String line : lines) {
            String reg = identify_line(line);
            switch (reg) {
                case VAR_TYPE:
                    createVars(line);
                    break;
                case BLOCK_TYPE:
                    createBlock(line);
                    break;
                case END_BLOCK:
                    if (currentBlock != null) {
                        currentBlock = currentBlock.getParent();
                        if (currentBlock.isMethod() && !(lines.get(lines.indexOf(line)-1) == RETURN))
                                throw new IOException("MISSING RETURN STATEMENT");
                    }
                    else
                        throw new IOException("TOO MANY }");
                    break;
                case RETURN: continue; break;
                case VAR_ASSIGN:
                    checkAssignment(line);
                    break;
                default:
                    throw new IOException("UNRECOGNIZED COMMAND");
            }
        }

    }


    private static String identify_line(String line){
        Pattern p;
        Matcher m;
        for (String reg : regexes){
            p = Pattern.compile(reg);
            m = p.matcher(line);
            if (m.matches())
                return reg;
        }
        return INVALID_LINE;
    }

    /**
     * Reads a var declaration and creates Variable objects according to it.
     * Adds the variable to the variable arraylist.
     * @param line a line representing a variable declaration
     * @returns true iff succeeded creating variable objects
     */
    private static boolean createVars (String line){
        boolean succeeded = false;
        String[] words = line.split(" ");
        String type = words[0];

        return succeeded;
    }


    /**
     * Reads a block declaration and creates Block objects according to it.
     * Adds the blocks to the blocks arraylist.
     * @param line a line representing a blocks declaration
     * @returns true iff succeeded creating block objects
     */
    private static boolean createBlock (String line){
        boolean succeeded = false;


        return succeeded;

    }



}




