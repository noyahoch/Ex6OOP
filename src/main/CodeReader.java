package main;
import variable.*;
import block.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The main class that checks the validity of the code.
 */

public class CodeReader {
	private static ArrayList<Method> methods;
	private static ArrayList<Conditional> conditionals;
	private static ArrayList<Variable> vars;
	private static ArrayList<MethodCall> methodCalls;

	private static final String TRUE = "true";
	private static final String FALSE = "false";
	private static final String METHOD_DEC = "(void\\s)\\s*" + Method.VALID_METHOD_NAME +
			"\\s*\\(([\\w\\s,]*)\\)\\s*\\{\\s*";
	private static final String CONDITIONAL = "(if|while)\\s*\\(([\\w\\s|&-.]*)\\)\\s*\\{\\s*";
	private static final String VAR_TYPE = "\\s*(final\\s)?\\s*+(int|double|String|char|boolean)\\s*(.+)" +
			"\\s*;" +
			"\\s*";
	private static final String END_BLOCK = " *} *";
	private static final String RETURN = "return *;";
	private static final String VAR_ASSIGN = Variable.VARIABLE_PATTERN_NAME + " *= *([\\w\"]+) *; *";
	private static final String METHOD_CALL = Method.VALID_METHOD_NAME + "\\s*\\(([\\w\\s,\"]*)\\)\\s*;\\s*";
	private static final String RETURN_CALL = "return";//todo is it smart?


	private static final String[] regexes = new String[]
			{VAR_TYPE, METHOD_DEC, END_BLOCK, CONDITIONAL, RETURN, VAR_ASSIGN, METHOD_CALL};

	private static final String INVALID_LINE = "INVALID";
	private static Block currentBlock;
	private static Pattern p;
	private static Matcher m;
	private static boolean finality;
	private static Global globalBlock;


	/**
	 * Checks the if the lines in the file are valid.
	 *
	 * @param lines
	 * @return
	 */
	static void checkCode(ArrayList<String> lines) throws ReadingCodeException {
		methods = new ArrayList<>();
		methodCalls = new ArrayList<>();
		conditionals = new ArrayList<>();
		vars = new ArrayList<>();
		globalBlock = new Global(vars);
		currentBlock = globalBlock;
		createGlobals(lines);
		int index = -1;
		for (String line : lines) {
			index++;
			String reg = identify_line(line);
			switch (reg) {
				case VAR_TYPE:
					if (currentBlock != globalBlock) {
						createVars(line, currentBlock.getVariables());
					}
					break;
				case METHOD_DEC:
					Method method = findMethod(m.group(2));

					if (method == null) {
						throw new LogicalException("method inside method");
					}
					currentBlock = method;
					break;
				case CONDITIONAL:
					Conditional conditional = createConditional();
					currentBlock = conditional;
					break;
				case END_BLOCK:

					if (currentBlock.getParent() != null) {
						if (currentBlock.isMethod() && !(lines.get(index - 1).contains(RETURN_CALL)))
							throw new SyntaxException("MISSING RETURN STATEMENT");
						currentBlock = currentBlock.getParent();
					} else
						throw new SyntaxException("TOO MANY }");
					break;
				case RETURN:
					if (currentBlock == globalBlock)
						throw new SyntaxException("INAPPROPRIATE RETURN");
					break;
				case VAR_ASSIGN:
					checkAssignment();
					break;
				case METHOD_CALL:
					MethodCall methodCall = createMethodCall();
					methodCalls.add(methodCall);
					break;
				default:
					throw new SyntaxException("UNRECOGNIZED COMMAND IN LINE: " + lines.indexOf(line));
			}

		}
		if (currentBlock != globalBlock)
			throw new SyntaxException("SOME BLOCKS WERE NOT CLOSED");
		checkMethodCall();

	}

	static void createGlobals(ArrayList<String> lines) throws ReadingCodeException {
		int scopesCounter = 0;
		Pattern varPattern = Pattern.compile(VAR_TYPE);
		Pattern methodPattern = Pattern.compile(METHOD_DEC);
		Pattern varAssignPattern = Pattern.compile(VAR_ASSIGN);
		for (String line : lines) {
			if (scopesCounter == 0) {
				m = varPattern.matcher(line);
				if (m.matches()) {
					createVars(line, vars);
				} else {
					m = varAssignPattern.matcher(line);
					if (m.matches()) {
						checkAssignment();
					}
					m = methodPattern.matcher(line);
					if (m.matches()) {
						createMethod();
					}
				}
			}
			if (line.charAt(line.length() - 1) == '{') {
				scopesCounter++;
			}
			if (line.charAt(line.length() - 1) == '}') {
				scopesCounter--;
			}
		}
		if (scopesCounter != 0) {
			throw new SyntaxException("scopes doesn't match");
		}
	}


	static MethodCall createMethodCall() throws LogicalException, SyntaxException {
		if (currentBlock == globalBlock || !m.matches())
			throw new LogicalException("CANNOT CALL METHOD GLOBALLY");
		ArrayList<String> callArgs = new ArrayList<>(Arrays.asList(m.group(2).split(",")));

		return new MethodCall(m.group(1), currentBlock, callArgs);
	}

	/**
	 * Goes through the relevant lines type, and returns the type of the line
	 *
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
		if (line.trim().equals(";")) {
			return;
		} // todo handle with empty line (args)
		p = Pattern.compile(VAR_TYPE);
		m = p.matcher(line);
		if (!m.matches()) {
			throw new SyntaxException();
		}//todo changed
		finality = (m.group(1) != null);
		String type = m.group(2);
		String assignLine = m.group(3);
		if (assignLine.charAt(assignLine.length() - 1) == ',')
			throw new SyntaxException("ILLEGAL ASSIGMENT, TOO MANY COMMAS");

		String[] assignments = assignLine.split(",");
		for (String assign : assignments) {
			Variable var = VariableFactory.createVariable(finality, type, assign.trim(), currentBlock);
			for (Variable sibling : toAdd) {
				if (var.getName().equals(sibling.getName())) {
					throw new LogicalException("CANT NAME TWO VARS WITH SAME NAME");
				}
			}
			if (currentBlock.isMethod()) {
				if (((Method) currentBlock).findParam(var.getName()) != null) {
					throw new LogicalException("CANT NAME TWO VARS WITH SAME NAME");
				}
			}
			toAdd.add(var);
		}
	}

	/**
	 * Checks if an assignment line is valid.
	 *
	 * @throws IOException if the assignment is invalid.
	 */
	private static void checkAssignment() throws ReadingCodeException {
		m.matches();
		String value = m.group(1);
		Variable firstVar = currentBlock.findVar(value);
		if (firstVar == null && currentBlock.isMethod())
			firstVar = ((Method) currentBlock).findParam(value);
		if (firstVar == null || firstVar.getFinality())
			throw new LogicalException("ILLEGAL ASSIGNMENT");
		Variable secondVar = currentBlock.findVar(m.group(2));
		if (secondVar != null) {
			value = secondVar.getValue();

		} else {
			value = m.group(2);
		}
		boolean valid;
		if (currentBlock==globalBlock){
			valid = firstVar.setValue(value);
		} else {
			valid = firstVar.checkValidity(value);
		}
		if (!valid) {
			throw new LogicalException("MISMATCH");
		}

	}


	/**
	 * Creates a new conditional (if or while) according to the line and checks its validity
	 *
	 * @throws IOException if line is invalid
	 */
	private static Conditional createConditional() throws ReadingCodeException {
		if (currentBlock == globalBlock) {
			throw new LogicalException("CANNOT DECLARE CONDITIONAL IN GLOBAL SCOPE");
		}
		Conditional conditional = new Conditional(m.group(2), currentBlock);
		if (conditional.checkValidity()) {
			return conditional;
		} else {
			throw new LogicalException("conditional is not valid");
		}
	}

	/**
	 * Reads a method declaration and creates Block objects according to it.
	 * Adds the blocks to the blocks arraylist.
	 *
	 * @throws IOException if line is invalid
	 */
	private static Method createMethod() throws ReadingCodeException {
		String methodName = m.group(2);
		for (Method method : methods) {
			if (method.getName().equals(methodName))
				throw new LogicalException("METHOD OVERLOADING IS NOT SUPPORTED");
		}
		ArrayList<Variable> params = new ArrayList<>();
		String[] varDec = m.group(3).split(",");

		for (String declaration : varDec)
			if (varDec.length != 1 && declaration.equals(""))
				throw new SyntaxException();
			else
				createVars(declaration + ";".trim(), params);

		Method createdMethod = new Method(methodName, currentBlock, params);
		if (!createdMethod.checkValidity()) {
			throw new LogicalException("INVALID METHOD DECLARATION");
		}
		methods.add(createdMethod);
		return createdMethod;
	}


	/**
	 * Reads a line of method calls and checks if the method call is correct and logical.
	 *
	 * @return true iff the call is correct and logical.
	 */
	private static void checkMethodCall() throws ReadingCodeException {//todo rethinking
		Method corresMethod;
		boolean isValid;
		for (MethodCall methodCall : methodCalls) {
			corresMethod = findMethod(methodCall.getName());
			if (corresMethod != null) {
				ArrayList<String> callArgs = methodCall.getCallArgs();
				isValid = corresMethod.checkParamValidity(callArgs, methodCall.getParent());
				if (!isValid)
					throw new LogicalException("INVALID METHOD ARGUMENTS");
			} else {
				throw new LogicalException("INVALID METHOD CALL");
			}
		}
	}

	/**
	 * Finds a method with the given name inputted.
	 *
	 * @param name the name of the method to search
	 * @return The method with the name if exists. null otherwise.
	 */
	private static Method findMethod(String name) { //todo: rethinking
		for (Method method : methods) {
			if (method.getName().equals(name))
				return method;
		}

		return null;
	}
}