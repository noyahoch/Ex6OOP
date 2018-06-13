package main;

import block.*;
import variable.Variable;
import variable.VariableFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommandFactory {
    ArrayList<Block> blocks;
    ArrayList<Variable> vars;


    private static final String VAR_TYPE = "(final )?(int|double|String|char|boolean)";
    private static final String BLOCK_TYPE = "while |if |void ";
    private static final String END_BLOCK = "}";
    private static final String RETURN = "return *;";
    private static final String INVALID_LINE = "INVALID";
    private static final String VAR_ASSIGN = "(final) ?(int|double|String|char|boolean) +(.+);";
    private static final String METHOD_CALL = Method.VALID_METHOD_NAME+"(.*) *;";
    private static final String FINALITY= "final"; // todo is this the best way?
	private static final String START_BLOCK = BLOCK_TYPE + "(.*) +\(.*\)\\s*{"//todo can the parentheses bewithout
	// space?
    private static final String[] regexes = new String[]{VAR_TYPE,BLOCK_TYPE,END_BLOCK,RETURN
                                                        ,VAR_ASSIGN,METHOD_CALL};

    private static Block currentBlock = null;
	Pattern p;
	Matcher m;
	boolean finality;


    /**
     * Checks the if the lines in the file are valid.
     * @param lines
     * @return
     */
    static void check (ArrayList<String> lines) throws IOException {
        for (String line : lines) {
            String reg = identify_line(line);
            switch (reg) {
                case VAR_TYPE: createVars(line); break;
                case BLOCK_TYPE: createBlock(line); break;
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
                case VAR_ASSIGN: checkAssignment(line); break;
                case METHOD_CALL: checkMethodCall(line); break;
                // TODO if methods can be called before assignment,
                // TODO they should just be added to a new arraylist here and checked later
                default: throw new IOException("UNRECOGNIZED COMMAND");
            }
        }
        //TODO go through all methods and variables and check validity
        // TODO or check immediately when creating them??

    }


    private String identify_line(String line){
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
    private boolean createVars (String line) throws Exception {
	    finality = m.group(1) != null;// todo check if finality is for all values in the row
	    String type = m.group(2);
	    String[] assignments = m.group(3).split(",");
	    for (String assign : assignments) {
		    currentBlock.addVariable(VariableFactory.variableFactory(finality, type, assign, currentBlock))
		    ; //todo try&catch here?

	    }
    }



        //TODO create multiple var objects according to declaration
        //TODO recognize if declaration is based on another var
        //TODO add them to the variables of the current block


    /**
     * Reads a block declaration and creates Block objects according to it.
     * Adds the blocks to the blocks arraylist.
     * @param line a line representing a blocks declaration
     * @returns true iff succeeded creating block objects
     */
    private static boolean createBlock (String line){
    	Pattern p = Pattern.compile(START_BLOCK);
    	Matcher m  = p.matcher(line);
    		Block newBlock = BlockFactory(m.group(1), m.group(2), m.group(3).split(","));
    		if (newBlock.checkValidity()){currentBlock.addBlock(newBlock); return true;}
    		return false;
	    }



    //TODO break the line to method and params or conditional and condition
        // TODO advance currentBlock by one.
        //TODO make sure method is not declared in another method.
    }

    /**
     * Reads a line of method calls and checks if the method call is correct and logical.
     * @return true iff the call is correct and logical.
     */
    private static boolean checkMethodCall(){




        //TODO look through the Arraylist of blocks, if the first word before ( is a name of a method
        //TODO send it the object with parameters and call relevant method inside.
        //TODO make sure a method is called inside a method.
    }


}




