package variable;

import main.*;
import block.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates variables
 */
public class VariableFactory {
	private static final String VARIABLE_ASSIGNMENT_PATTERN = Variable.VARIABLE_PATTERN_NAME + "(\\s*(=)\\s*" +
			"(.*))?";

	public static Variable createVariable(boolean finality, String type, String assign, Block currentBlock)
			throws LogicalException, SyntaxException {

		Variable newVar;
		Pattern p = Pattern.compile(VARIABLE_ASSIGNMENT_PATTERN);
		Matcher m = p.matcher(assign);
		if (!m.matches()) {
			throw new SyntaxException("variable doesn't match");
		}
		if (m.group(2) != null) {// if the variable has no value
			String assignmentValue = currentBlock.valueOfVar(m.group(4));

			if (assignmentValue == null) {
				assignmentValue = m.group(4).trim();
			}
			boolean isAssigned = false;
			if (currentBlock.isMethod()) {
				if (((Method)currentBlock).findParam(assignmentValue) != null)
					isAssigned = true;
			}
			if (isAssigned) {
				newVar = new Variable(m.group(1), type, currentBlock, finality);
			} else {
				newVar = new Variable(m.group(1), type, currentBlock, finality, assignmentValue);
			}
		} else {
			newVar = new Variable(m.group(1), type, currentBlock, finality);
		}
		if (newVar.checkValidity()) {
				return newVar;
			} else {
				throw new LogicalException("ILLEGAL VARIABLE DECLARATION");
			}
		}
	}



