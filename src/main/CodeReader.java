package main;
import variable.*;
import block.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CodeReader {
	private static ArrayList<Method> methods;
	private static ArrayList<Variable> vars= new ArrayList<>();
	private static ArrayList<String> methodCalls;
	private static final String METHOD_DEC = "(void )\\s*" + Method.VALID_METHOD_NAME +
											"\\s*\\(([\\w ,]*)\\)\\s*\\{\\s*";
	private static final String CONDITIONAL = "(if|while) *\\(([\\w |&-.]*)\\) *\\{ *";
	private static final String VAR_TYPE = " *(final )?(int|double|String|char|boolean)+(.+); *";
	private static final String END_BLOCK = " *} *";
	private static final String RETURN = "return *;";
	private static final String VAR_ASSIGN = Variable.VARIABLE_PATTERN_NAME+" *= *([\\w\"]+) *; *";
	private static final String METHOD_CALL = Method.VALID_METHOD_NAME + "\\(([\\w ,\"]*)\\) *; *";
	private static final String RETURN_CALL = "return";//todo is it smart?


	private static final String[] regexes = new String[]
			{VAR_TYPE,METHOD_DEC, END_BLOCK, CONDITIONAL, RETURN, VAR_ASSIGN, METHOD_CALL};

	private static final String INVALID_LINE = "INVALID";
	private static Block currentBlock;
	private static Pattern p;
	private static Matcher m;
	private static boolean finality;
	private static final Global globalBlock = new Global(vars);


	/**
	 * Checks the if the lines in the file are valid.
	 * @param lines
	 * @return
	 */
	 static void checkCode(ArrayList<String> lines) throws ReadingCodeException {
		 methods = new ArrayList<>();
		 methodCalls = new ArrayList<>();
		 currentBlock =  globalBlock;
		 int index = -1;
	 	 for (String line : lines) {
	 		index ++;
			String reg = identify_line(line);
			switch (reg) {
				case VAR_TYPE:
					createVars(line, currentBlock.getVariables());
					break;
				case METHOD_DEC:
					currentBlock = createMethod();
					break;
				case CONDITIONAL:
					currentBlock = createConditional();
					break;
				case END_BLOCK:
					if (currentBlock.getParent() != null) {
						if (currentBlock.isMethod() && !(lines.get(index-1).contains(RETURN_CALL)))
							throw new SyntaxException("MISSING RETURN STATEMENT");
						currentBlock = currentBlock.getParent();
					} else
						throw new SyntaxException("TOO MANY }");
					break;
				case RETURN:
					break;
				case VAR_ASSIGN:
					checkAssignment();
					break;
				case METHOD_CALL:
					methodCalls.add(line);
					checkMethodCall();
					break;
				default:
					throw new SyntaxException("UNRECOGNIZED COMMAND IN LINE: " + lines.indexOf(line));
			}
		}
		if (currentBlock != globalBlock)
			throw new SyntaxException("SOME BLOCKS WERE NOT CLOSED");
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

	private static void createVars(String line, ArrayList<Variable> toAdd) throws ReadingCodeException {
		if (line.equals(";")){return;} // todo handle with empty line (args)
		p = Pattern.compile(VAR_TYPE);
		m = p.matcher(line);
		m.matches();

		finality = (m.group(1) != null);
		String type = m.group(2);
		String[] assignments = m.group(3).split(",");
		for (String assign : assignments) {
			Variable var = VariableFactory.createVariable(finality, type, assign.trim(), currentBlock);
			//for (Variable sibling: toAdd)
				//if(var.getName().equals(sibling.getName()) && var.getType().equals(sibling.getType()))
				//	throw new LogicalException("CANT NAME TWO VARS WITH SAME NAME");
			toAdd.add(var);
		}
	}

	/**
	 * Checks if an assignment line is valid.
	 * @throws IOException if the assignment is invalid.
	 */
	private static void checkAssignment() throws ReadingCodeException{
		m.matches();
		Variable firstVar = currentBlock.findVar(m.group(1));
		if (firstVar == null || !firstVar.getFinality()) // todo documentation
			throw new LogicalException("ILLEGAL ASSIGNMENT");
		String value;
		Variable secondVar = currentBlock.findVar(m.group(2));
		if (secondVar != null)
			value = secondVar.getValue();
		else
			value = m.group(2);
		if (!firstVar.setValue(value))
			throw new LogicalException("MISMATCH");
	}

	/**
	 * Creates a new conditional (if or while) according to the line and checks its validity
	 *
	 * @throws IOException if line is invalid
	 */
	private static Conditional createConditional() throws ReadingCodeException{
		if (currentBlock==globalBlock)
			throw new LogicalException("CANNOT DECLARE CONDITIONAL IN GLOBAL SCOPE");
		Conditional cond = new Conditional(m.group(2), currentBlock);
		boolean valid = cond.checkValidity();
		if (!valid)
			throw new SyntaxException("INVALID BOOLEAN CONDITION");
		return cond;
		//} catch (LogicalException e) {
		//	throw new LogicalException("INVALID IF WHILE DECLARATION");
		//}
	}

	/**
	 * Reads a method declaration and creates Block objects according to it.
	 * Adds the blocks to the blocks arraylist.
	 *
	 * @throws IOException if line is invalid
	 */
	private static Block createMethod() throws ReadingCodeException {
		if (currentBlock.isMethod() && currentBlock.getParent() != null)
			throw new LogicalException("DECLARED METHOD WITHIN METHOD");
		String methodName = m.group(2);
		for (Method method : methods)
			if (method.getName().equals(methodName))
				throw new LogicalException("METHOD OVERLOADING IS NOT SUPPORTED");

		ArrayList<Variable> params = new ArrayList<>();
		String[] varDec = m.group(3).split(",");

		for (String declaration : varDec)
			if (varDec.length!=1 && declaration.equals(""))
				throw new SyntaxException();
			else
				createVars(declaration + ";".trim(), params);

		Block createdMethod = new Method(methodName, currentBlock, params);
		if (!createdMethod.checkValidity())
			throw new LogicalException("INVALID METHOD DECLARATION");

		return createdMethod;
	}




	/**
     * Reads a line of method calls and checks if the method call is correct and logical.
     * @return true iff the call is correct and logical.
     */
    private static void checkMethodCall() throws ReadingCodeException {//todo rethinking
	    if (currentBlock.getParent()==null)
	    	throw new LogicalException("CANNOT CALL METHOD GLOBALLY");
		Method corresMethod;
	    boolean isValid = true;
	    p = Pattern.compile(METHOD_CALL);
	    for (String methodCall : methodCalls) {
		    m = p.matcher(methodCall);
		    m.matches();
	    	corresMethod = findMethod(m.group(1));
		    if (corresMethod != null){
		    	ArrayList<String> callArgs = new ArrayList<>(Arrays.asList(m.group(2).split(",")));
			    isValid = corresMethod.checkParamValidity(callArgs);
		    	if (!isValid)
			    	throw new LogicalException("INVALID METHOD ARGUMENTS");
		    } else {
				throw new LogicalException("INVALID METHOD CALL");
			}
	    }
    }

	/**
	 * Finds a method with the given name inputted.
	 * @param name the name of the method to search
	 * @return The method with the name if exists. null otherwise.
	 */
	private static Method findMethod (String name) { //todo: rethinking
		Block checkedBlock = currentBlock;
		while (checkedBlock!=null){
			for (Method method: methods)
				if (method.getName().equals(name))
					return method;
			checkedBlock = checkedBlock.getParent();
		}
		return null;
	}


}