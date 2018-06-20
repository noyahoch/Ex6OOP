package oop.ex6.main;

import oop.ex6.variable.*;
import oop.ex6.block.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The main class that checks the validity of the code.
 */

public class CodeReader {
	private static final String METHOD_DEC = "(void\\s)\\s*" + Method.VALID_METHOD_NAME +
			"\\s*\\(([\\w\\s, -.'\"]*)\\)\\s*\\{\\s*";
	private static final String CONDITIONAL = "(if|while)\\s*\\(([\\w\\s|&-.]*)\\)\\s*\\{\\s*";
	private static final String VAR_TYPE = "\\s*(final\\s)?\\s*+(int|double|String|char|boolean)" +
			"\\s+(.+)\\s*;\\s*";
	private static final String END_BLOCK = " *} *";
	private static final String RETURN = "return *;";
	private static final String VAR_ASSIGN = Variable.VARIABLE_PATTERN_NAME + " *= *([\\w\"]+) *; *";
	private static final String METHOD_CALL = Method.VALID_METHOD_NAME
											+ "\\s*\\(([\\w\\s,\"]*)\\)\\s*;\\s*";
	private static final String INVALID_LINE = "INVALID";

	private static final String[] regexes = new String[]{VAR_TYPE, METHOD_DEC, END_BLOCK, CONDITIONAL,
			RETURN, VAR_ASSIGN, METHOD_CALL};

	private static final String NON_GLOBAL_METHOD_MESSAGE = "METHOD DEFINED IN NON-GLOBAL SCOPE";
	private static final String GLOBAL_RETURN_MESSAGE = "INAPPROPRIATE RETURN";
	private static final String UNRECOGNIZED_COMMAND = "UNRECOGNIZED COMMAND IN LINE: %d";
	private static final String BLOCKS_NOT_CLOSED_MESSAGE = "SOME BLOCKS WERE NOT CLOSED";
	private static final String CONDITIONAL_IN_GLOBAL_MESSAGE = "CANNOT DECLARE CONDITIONAL IN GLOBAL SCOPE";
	private static final String NON_MATCHING_SCOPES = "SCOPES DOESN'T MATCH";
	private static final String GLOBAL_METHOD_CALL_MESSAGE = "CANNOT CALL METHOD GLOBALLY";
	private static final String COMMAS_MISMATCH_MESSAGE = "ILLEGAL ASSIGNMENT, TOO MANY COMMAS";
	private static final String ILLEGAL_ASSIGNMENT_MESSAGE = "ILLEGAL ASSIGNMENT";
	private static final String SAME_NAME_ASSIGNMENT = "CANT NAME TWO VARS WITH SAME NAME";
	private static final String INVALID_CONDITIONAL = "CONDITIONAL IS NOT VALID";
	private static final String DUPLICATE_METHOD_MESSAGE = "METHOD OVERLOADING IS NOT SUPPORTED";
	private static final String INVALID_ARGUMENTS_MESSAGE = "INVALID METHOD ARGUMENTS";
	private static final String INVALID_METHOD_CALL = "INVALID METHOD CALL";
	private static final String METHOD_WITHOUT_RETURN_MESSAGE = "MISSING RETURN STATEMENT";
	private static final String INVALID_METHOD_DEC_MESSAGE = "INVALID METHOD DECLARATION";
	private static final String MISMATCHING_SCOPES_ERROR = "MISMATCH IN NUMBER OF SCOPES";
	private static final String TYPES_MISMATCH = "MISMATCH IN VARIABLES' ASSIGNMENT";

	private static Block currentBlock;
	private static Pattern p;
	private static Matcher m;
	private static Global globalBlock;

	/**
	 * Checks the lines representing an Sjava code.
	 * @param lines arraylist representing the lines in a sjava code
	 * @throws ReadingCodeException if the code is incorrect
	 */
	static void checkCode(ArrayList<String> lines) throws ReadingCodeException {
		globalBlock = new Global();
		currentBlock = globalBlock;
		createGlobals(lines);
		doCommand(lines);
		if (currentBlock != globalBlock)
			throw new SyntaxException(BLOCKS_NOT_CLOSED_MESSAGE);
	}

	/**
	 * Creates global variables and the methods of the code.
	 * @param lines
	 * @throws ReadingCodeException
	 */
	private static void createGlobals(ArrayList<String> lines) throws ReadingCodeException {
		int scopesCounter = 0;
		Pattern varPattern = Pattern.compile(VAR_TYPE);
		Pattern methodPattern = Pattern.compile(METHOD_DEC);
		Pattern varAssignPattern = Pattern.compile(VAR_ASSIGN);
		for (String line : lines) {
			if (scopesCounter == 0) {
				m = varPattern.matcher(line);
				if (m.matches())
					createVars(line, globalBlock.getVariables());
				else {
					m = varAssignPattern.matcher(line);
					if (m.matches())
						checkAssignment();
					else {
						m = methodPattern.matcher(line);
						if (m.matches())
							createMethod();
					}
				}
			}
			if (line.charAt(line.length() - 1) == '{')
				scopesCounter++;
			if (line.charAt(line.length() - 1) == '}')
				scopesCounter--;
		}
		if (scopesCounter != 0)
			throw new SyntaxException(NON_MATCHING_SCOPES);
	}

	/**
	 * Identifies which kind of regex the line matches.
	 * @param line the line to check
	 * @return a regex that matches the line, INVALID LINE if there's no such regex.
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
	 * Follows the command of the lines in a Sjava files
	 * The main loop that check the sjava code
	 * @param lines representing the lines of sjava code
	 * @throws ReadingCodeException in case the code is invalid
	 */
	private static void doCommand(ArrayList<String> lines) throws ReadingCodeException {
		int index = -1;
		for (String line : lines) {
			index++;
			String reg = identify_line(line);
			switch (reg) {
				case VAR_TYPE:
					if (currentBlock != globalBlock) // Globals were created
						createVars(line, currentBlock.getVariables());
					break;
				case METHOD_DEC:
					currentBlock= findMethod(m.group(2)); // Method already created
					if (currentBlock == null)
						throw new LogicalException(NON_GLOBAL_METHOD_MESSAGE);
					break;
				case CONDITIONAL:
					currentBlock = createConditional();
					break;
				case END_BLOCK:
					checkEndBlock(lines, index);
					break;
				case RETURN:
					if (currentBlock == globalBlock)
						throw new SyntaxException(GLOBAL_RETURN_MESSAGE);
					break;
				case VAR_ASSIGN:
					checkAssignment();
					break;
				case METHOD_CALL:
					checkMethodCall();
					break;
				default:
					throw new SyntaxException(String.format(UNRECOGNIZED_COMMAND, lines.indexOf(line)));
			}
		}
	}

	/**
	 * Checks if a method call is logical - calls a declared function with relevant params.
	 * @throws LogicalException
	 */
	private static void checkMethodCall() throws LogicalException {
		if (currentBlock == globalBlock || !m.matches())
			throw new LogicalException(GLOBAL_METHOD_CALL_MESSAGE);
		ArrayList<String> callArgs = new ArrayList<>(Arrays.asList(m.group(2).split(",")));
		Method corresMethod = findMethod(m.group(1));

		if (corresMethod != null) {
			if (!corresMethod.checkParamValidity(callArgs, currentBlock))
				//checks if the params contain valid values
				//or valid values of variables in the current scope.
				throw new LogicalException(INVALID_ARGUMENTS_MESSAGE);
		} else
			throw new LogicalException(INVALID_METHOD_CALL);
	}


	/**
	 * Reads a var declaration and creates Variable objects according to it.
	 * Adds the variable to the variable array-list.
	 * @param line a line representing a variable declaration
	 * @returns true iff succeeded creating variable objects
	 * @throws ReadingCodeException if var is invalid
	 */
	private static void createVars(String line, ArrayList<Variable> toAdd) throws ReadingCodeException {
		if (line.trim().equals(";"))
			return;
		p = Pattern.compile(VAR_TYPE); // Need to compile again, new string to match.
		m = p.matcher(line);
		if (!m.matches())
			throw new SyntaxException();

		boolean finality = (m.group(1) != null);
		String type = m.group(2);
		String assignLine = m.group(3);
		if (assignLine.charAt(assignLine.length() - 1) == ',')
			throw new SyntaxException(COMMAS_MISMATCH_MESSAGE);

		String[] assignments = assignLine.split(",");
		for (String assign : assignments) {
			Variable var = VariableFactory.createVariable(finality, type, assign.trim(), currentBlock);
			for (Variable sibling : toAdd) // Checks for two vars with same name in same scope
				if (var.getName().equals(sibling.getName()))
					throw new LogicalException(SAME_NAME_ASSIGNMENT);
			toAdd.add(var); // adds the new variables to the relevant arraylist.
		}
	}


	/**
	 * Checks if an assignment line is valid.
	 * @throws ReadingCodeException if the assignment is invalid.
	 */
	private static void checkAssignment() throws ReadingCodeException {
		String value = m.group(1);
		Variable firstVar = currentBlock.findVar(value);
		if (firstVar == null || firstVar.getFinality())
			throw new LogicalException(ILLEGAL_ASSIGNMENT_MESSAGE);

		Variable secondVar = currentBlock.findVar(m.group(2));
		if (secondVar != null) // Decides if the assignment is a new value or a var name.
			value = secondVar.getValue();
		 else
			value = m.group(2);

		boolean valid;
		if (currentBlock == globalBlock)
			valid = firstVar.setValue(value); // Actually changes the value if assignment is global.
		 else
			valid = firstVar.checkValidity(value);

		if (!valid)
			throw new LogicalException(TYPES_MISMATCH);
	}

	/**
	 * Creates a new conditional (if\while block) according to the line and checks its validity
	 * @throws ReadingCodeException if conditional is invalid
	 */
	private static Conditional createConditional() throws ReadingCodeException {
		if (currentBlock == globalBlock)
			throw new LogicalException(CONDITIONAL_IN_GLOBAL_MESSAGE);
		Conditional conditional = new Conditional(m.group(2), currentBlock);
		if (conditional.checkValidity())
			return conditional;
		else
			throw new LogicalException(INVALID_CONDITIONAL);
	}

	/**
	 * Reads a method declaration and creates a method objects according to it.
	 * Adds the blocks to the blocks arraylist.
	 * @throws ReadingCodeException if method is invalid
	 */
	private static void createMethod() throws ReadingCodeException {
		String methodName = m.group(2);
		for (Method method : globalBlock.getMethods())
			if (method.getName().equals(methodName))
				throw new LogicalException(DUPLICATE_METHOD_MESSAGE);

		ArrayList<Variable> params = new ArrayList<>();
		String[] varDec = m.group(3).split(",");
		for (String declaration : varDec)
			if (varDec.length != 1 && declaration.equals(""))
				throw new SyntaxException();
			else
				createVars(declaration.trim()+";", params);
		Method createdMethod = new Method(methodName, currentBlock, params);
		if (!createdMethod.checkValidity())
			throw new LogicalException(INVALID_METHOD_DEC_MESSAGE);
		globalBlock.getMethods().add(createdMethod);
	}


	/**
	 * Finds a method with the given name inputted.
	 * @param name the name of the method to search
	 * @return The method with the name if exists. null otherwise.
	 */
	private static Method findMethod(String name) {
		for (Method method : globalBlock.getMethods())
			if (method.getName().equals(name))
				return method;
		return null;
	}

	/**
	 * Checks if the end of the block is end of a method, and checks if there was a return call.
	 * @param lines the lines representing the code
	 * @param index the index of the block ending within lines
	 * @throws SyntaxException
	 */
	private static void checkEndBlock(ArrayList<String> lines, int index) throws SyntaxException {
		if (currentBlock.getParent() != null) {
			if (currentBlock.isMethod() && !(lines.get(index - 1).matches(RETURN)))
				throw new SyntaxException(METHOD_WITHOUT_RETURN_MESSAGE);
			currentBlock = currentBlock.getParent();
		} else
			throw new SyntaxException(MISMATCHING_SCOPES_ERROR);
	}
}
