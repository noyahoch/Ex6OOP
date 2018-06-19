package variable;

import main.*;
import block.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class responsible for creating a valid vairable according to the values.
 */
public class VariableFactory {

	private static final String VARIABLE_ASSIGNMENT_PATTERN = Variable.VARIABLE_PATTERN_NAME +
			"(\\s*(=)\\s*(.*))?";
	private static final String MISMATCH_VAR_MESSAGE = "VARIABLE ASSIGNMENT DOESN'T MATCH";
	private static final String ILLEGAL_VARIABLE_DEC_MESSAGE = "ILLEGAL VARIABLE DECLARATION";

	/**
	 * Creates a variable according to assignment command and checks its validity.
	 * @param finality the finality of the variable
	 * @param type
	 * @param assign
	 * @param currentBlock
	 * @return
	 * @throws LogicalException
	 * @throws SyntaxException
	 */

	public static Variable createVariable(boolean finality, String type, String assign, Block currentBlock)
			throws LogicalException, SyntaxException {

		Variable newVar;
		Pattern p = Pattern.compile(VARIABLE_ASSIGNMENT_PATTERN);
		Matcher m = p.matcher(assign);
		if (!m.matches())
			throw new SyntaxException(MISMATCH_VAR_MESSAGE);
		if (m.group(2) != null) { // if the variable has no value
			String assignmentValue = currentBlock.valueOfVar(m.group(4));
			if (assignmentValue == null)
				assignmentValue = m.group(4).trim();

			newVar = new Variable(m.group(1), type, finality, assignmentValue);
		} else
			newVar = new Variable(m.group(1), type, finality);

		if (newVar.checkValidity())
			return newVar;
		 else
			throw new LogicalException(ILLEGAL_VARIABLE_DEC_MESSAGE);
	}
}



