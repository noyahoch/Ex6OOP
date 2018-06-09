package main;

import block.*;
import variable.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommandFactory {
    ArrayList<Block> blocks;
    ArrayList<Variable> vars;
    private final String validSuffixOnce = "$[;{}]";

    private final String varType = "(final )?(int|double|String|char|boolean)";
    private final String blockType = "while|if|void";
    private final String END_BLOCK = "}";
    private Block currentBlock = null;

    /**
     * Checks the if the lines in the file are valid.
     * @param lines
     * @return
     */
    boolean check (ArrayList<String> lines){
        for (String line : lines){
            Pattern p = Pattern.compile(validSuffixOnce);
            Matcher m = p.matcher(line);
            if (!m.matches())
                return false;
            p = Pattern.compile(varType);
            m = p.matcher(line);
            if (m.lookingAt()) {
                boolean valid = createVars(line);
                if (!valid)
                    return false;
                continue;
            }
            p = Pattern.compile(blockType);
            m = p.matcher(line);
            if (m.lookingAt()){
                boolean valid = createBlock(line);
                if (!valid)
                    return false;
                continue;
            }
            if (line == END_BLOCK){
                if (currentBlock == null)
                        return false;
                currentBlock = currentBlock.getParent();
            }



        }
        for (Block block : blocks){
            if (!block.checkValidity())
                return false;
        }
        for (Variable var : vars){
            if (!var.checkValidity())
                return false;
        }
        return true;
    }

    /**
     * Reads a var declaration and creates Variable objects according to it.
     * Adds the variables to the variable arraylist.
     * @param line a line representing a variable declaration
     * @returns true iff succeeded creating variable objects
     */
    private boolean createVars (String line){
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
    private boolean createBlock (String line){
        boolean succeeded = false;


        return succeeded;

    }
}
