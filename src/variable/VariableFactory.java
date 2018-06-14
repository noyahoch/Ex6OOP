package variable;

import block.Block;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableFactory {
	private static final String VARIABLE_ASSIGNMENT_PATTERN = Variable.VARIABLE_PATTERN_NAME +"( *(=) " +
			"*(\\S|\".*\"))?";

	public static Variable createVariable(boolean finality, String type, String assign, Block currentBlock) throws
			Exception {

		Variable newVar;
		Pattern p = Pattern.compile(VARIABLE_ASSIGNMENT_PATTERN);
		Matcher m = p.matcher(assign);
		m.matches();
		if (m.group(2) != null) {// if the variable has no value
			String assignmentValue = currentBlock.valueOfVar(m.group(4));
			if (assignmentValue == null)
				assignmentValue = m.group(4);
			newVar = new Variable(m.group(1), type, currentBlock, finality,  assignmentValue);
		} else
			newVar = new Variable(m.group(1), type, currentBlock, finality);


		if (newVar.checkValidity())
			return newVar;
		else
			throw new Exception("fuck");
	}
}
