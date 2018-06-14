package main;

import block.*;
import variable.Variable;
import variable.VariableFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CodeReader {
	static ArrayList<Method> methods; //TODO - do we really need it? cant we use a list of methods only,
	//TODO and variables check will be based on cuurentblock and its parents??
	static ArrayList<Variable> vars;
	static ArrayList<String> methodCalls;
	private static final String VAR_TYPE = "(final )?(int|double|String|char|boolean)+(.+);";
	private static final String BLOCK_DEC = "(void |while *\\(?|if *\\(?)";
	private static final String END_BLOCK = "}";
	private static final String RETURN = "return *;";
	private static final String VAR_ASSIGN = Variable.VARIABLE_PATTERN_NAME+" *= *([\\w\"]+) *; *";
	private static final String METHOD_CALL = Method.VALID_METHOD_NAME + "\\(([\\w ,]*)\\) *; *";

	private static final String[] regexes = new String[]{VAR_TYPE,
			END_BLOCK, RETURN,VAR_ASSIGN, METHOD_CALL};

	private static final String INVALID_LINE = "INVALID";
	private static Block currentBlock = null;
	static Pattern p;
	static Matcher m;
	static boolean finality;


	/**
	 * Checks the if the lines in the file are valid.
	 * @param lines
	 * @return
	 */

	 static void check(ArrayList<String> lines) throws IOException {
		for (String line : lines) {
			String reg = identify_line(line);
			switch (reg) {
				case VAR_TYPE:
					createVars(line);
					break;
				case BLOCK_DEC:
					currentBlock = BlockFactory.createBlock(m.group(1), line), currentBlock;
					break;
				case END_BLOCK:
					if (currentBlock != null) {
						currentBlock = currentBlock.getParent();
						if (currentBlock.isMethod() && !(lines.get(lines.indexOf(line) - 1) == RETURN))
							throw new IOException("MISSING RETURN STATEMENT");
					} else
						throw new IOException("TOO MANY }");
					break;
				case RETURN:
					continue;
					break;
				case VAR_ASSIGN:
					checkAssignment(line);
					break;
				case METHOD_CALL:
					methodCalls.add(line);
					break;
				default:
					throw new IOException("UNRECOGNIZED COMMAND");
			}

		}
		 checkMethodCall();
	}

	/**
	 * Goes through the relevant lines type, and returns the type of the line
	 * @param line a Sjava line
	 * @return the type of the line.
	 */
	private static String identify_line(String line) {
		for (String reg : regexes) {
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
	 *
	 * @param line a line representing a variable declaration
	 * @returns true iff succeeded creating variable objects
	 */
	public static boolean createVars(String line) throws Exception {
		return createVars(line, vars);
	}

	public static boolean createVars(String line, ArrayList<Variable> toAdd) throws Exception {
		finality = m.group(1) != null;// todo check if finality is for all values in the row
		String type = m.group(2);
		String[] assignments = m.group(3).split(",");
		for (String assign : assignments) {
			toAdd.add(VariableFactory.createVariable(finality, type, assign, currentBlock)); //todo try&catch
			// here?

		}
	}



	/**
	 * Checks if an assignment line is valid.
	 * @param line a String representing the assignment.
	 * @throws IOException if the assignment is invalid.
	 */
	private static void checkAssignment(String line) throws IOException{
		try{
			Variable firstVar = currentBlock.findVar(m.group(1));
			if (firstVar == null || (firstVar !=null && !firstVar.getFinalty()))
				throw new IOException("ILLEGAL ASSIGNMENT");
			String value;
			Variable secondVar = currentBlock.findVar(m.group(2));
			if (secondVar != null)
				value = secondVar.getValue();
			else
				value = m.group(2);
			firstVar.setValue(value);
			if (!firstVar.checkValidity())
				throw new IOException("MISMATCH");
		} catch (Exception e) {
			throw new IOException("ILLEGAL ASSIGNMENT");
		}
	}


    /**
     * Reads a line of method calls and checks if the method call is correct and logical.
     * @return true iff the call is correct and logical.
     */
    private static boolean checkMethodCall() throws IOException { // todo check type & parameters type
	    if (currentBlock == null)
	    	throw new IOException("CANNOT CALL METHOD GLOBALLY");
		Block corresMethod;
	    boolean isValid = true;
	    p = Pattern.compile(METHOD_CALL);
	    for (String methodCall : methodCalls) {
		    m = p.matcher(methodCall);
	    	corresMethod = findMethod(m.group(1));
		    if (corresMethod != null)
		    	ArrayList<String> callArgs =
						new ArrayList<String>(Arrays.asList(m.group(2).split(","));
			    isValid = corresMethod.checkParamValidity(callArgs);
		    else if (corresMethod == null || !isValid)
			    throw new IOException("INVALID METHOD CALL");
	    }
    }

	/**
	 * Finds a method with the given name inputted.
	 * @param name the name of the method to search
	 * @return The method with the name if exists. null otherwise.
	 */
	private static Block findMethod (String name) {
		Block checkedBlock = currentBlock;
		while (checkedBlock!=null){
			for (Block block : blocks)
				if (block.isMethod() && block.getName().equals(name))
					return block;
			checkedBlock = checkedBlock.getParent();
		}
		return null;
	}



/*

	private static boolean checkParameters(String[] methodParameters, Block originalMethod) throws Exception {
		if (methodParameters.length != originalMethod.methods.length)//todo need methods Arraylist?
			return false;
		for (int i = 0; i < methodParameters.length; i++) {
			if (!originalMethod.methods.get(i).checkValidity(methodParameters[i])) {
				return false;
			}
		}
		return true;
	}
*/

}