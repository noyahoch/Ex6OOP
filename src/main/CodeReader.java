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
	private static ArrayList<Method> methods;
	private static ArrayList<Variable> vars;
	private static ArrayList<String> methodCalls;
	private static final String METHOD_DEC = "(void )" + Method.VALID_METHOD_NAME +
											"\\(([\\w ,]*)\\) *\\{ *";
	private static final String CONDITIONAL = "(if|while) *\\(([\\w |&]*)\\) *\\{ *";
	private static final String VAR_TYPE = " *(final )?(int|double|String|char|boolean)+(.+); *";
	private static final String END_BLOCK = " *} *";
	private static final String RETURN = "return *;";
	private static final String VAR_ASSIGN = Variable.VARIABLE_PATTERN_NAME+" *= *([\\w\"]+) *; *";
	private static final String METHOD_CALL = Method.VALID_METHOD_NAME + "\\(([\\w ,\"]*)\\) *; *";

	private static final String[] regexes = new String[]
			{VAR_TYPE,METHOD_DEC, END_BLOCK, CONDITIONAL, RETURN, VAR_ASSIGN, METHOD_CALL};

	private static final String INVALID_LINE = "INVALID";
	private static Block currentBlock;
	private static Pattern p;
	private static Matcher m;
	private static boolean finality;

	/**
	 * Checks the if the lines in the file are valid.
	 * @param lines
	 * @return
	 */
	 static void checkCode(ArrayList<String> lines) throws Exception {
		 methods = new ArrayList<>();
		 vars = new ArrayList<>();
		 methodCalls = new ArrayList<>();
		 currentBlock =  new Method(null, null, vars);

	 	for (String line : lines) {
			try{
				String reg = identify_line(line);
				System.out.println(currentBlock);
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
							if (currentBlock.isMethod() && !(lines.get(lines.indexOf(line) - 1).equals(RETURN))) {
								System.out.println("rwkvjrev");
								throw new SyntaxException("MISSING RETURN STATEMENT");
							}
							currentBlock = currentBlock.getParent();
						} else
							throw new LogicalException("TOO MANY }");
						break;
					case RETURN:
						break;
					case VAR_ASSIGN:
						checkAssignment(line);
						break;
					case METHOD_CALL:
						methodCalls.add(line);
						checkMethodCall();
						break;
					default:
						throw new Exception("UNRECOGNIZED COMMAND IN LINE: " + lines.indexOf(line));
				}
			}catch(Exception e){
				throw new IOException(e.getMessage() + " " + lines.indexOf(line) + " " + line) ;
			}
		}
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

	private static void createVars(String line, ArrayList<Variable> toAdd) throws Exception {
		p = Pattern.compile(VAR_TYPE);
		m = p.matcher(line);
		m.matches();

		finality = (m.group(1) != null);
		String type = m.group(2);
		String[] assignments = m.group(3).split(",");
		try {
			for (String assign : assignments) {
				Variable var = VariableFactory.createVariable(finality, type, assign.trim(), currentBlock);
				toAdd.add(var);
			}
		}
		catch (LogicalException e) {
			throw new Exception("f");
		}
	}

	/**
	 * Checks if an assignment line is valid.
	 * @param line a String representing the assignment.
	 * @throws IOException if the assignment is invalid.
	 */
	private static void checkAssignment(String line) throws IOException{
		try{
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
				throw new IOException("MISMATCH");
		} catch (Exception e) {
			throw new IOException("ILLEGAL ASSIGNMENT");
		}
	}

	/**
	 * Creates a new conditional (if or while) according to the line and checks its validity
	 *
	 * @throws IOException if line is invalid
	 */
	private static Block createConditional() throws IOException {
		if (currentBlock == null)
			throw new IOException("CANNOT DECLARE CONDITIONAL IN GLOBAL SCOPE");
		try {
			Block cond = new Conditional(m.group(2), currentBlock);
			boolean valid = cond.checkValidity();
			if (!valid)
				throw new IOException("INVALID BOOLEAN CONDITION");
			return cond;
		} catch (Exception e) {
			throw new IOException("INVALID IF WHILE DECLARATION");
		}
	}

	/**
	 * Reads a method declaration and creates Block objects according to it.
	 * Adds the blocks to the blocks arraylist.
	 *
	 * @throws IOException if line is invalid
	 */
	private static Block createMethod() throws IOException {
		try {
			if (currentBlock.isMethod() && currentBlock.getParent() != null)
				throw new IOException("DECLARED METHOD WITHIN METHOD");
			String methodName = m.group(2);
			for (Method method : methods)
				if (method.getName().equals(methodName))
					throw new IOException("METHOD OVERLOADING IS NOT SUPPORTED");

			ArrayList<Variable> params = new ArrayList<>();
			String[] varDec = m.group(3).split(",");
			for (String declaration : varDec)
				createVars(declaration+";".trim(), params);

			Block createdMethod = new Method(methodName, currentBlock, params);

			if (!createdMethod.checkValidity())
				throw new IOException("INVALID METHOD DECLARATION");
			return createdMethod;
		} catch (Exception e) {
			throw new IOException("INVALID METHOD DECLARATION");
		}
	}



	/**
     * Reads a line of method calls and checks if the method call is correct and logical.
     * @return true iff the call is correct and logical.
     */
    private static void checkMethodCall() throws IOException {//todo rethinking
	    if (currentBlock.getParent() == null)
	    	throw new IOException("CANNOT CALL METHOD GLOBALLY");
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
		    }
		    else if (!isValid)
			    throw new IOException("INVALID METHOD CALL");
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