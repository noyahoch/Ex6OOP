package variable;

import block.Block;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableFactory {
	private static final String VARIABLE_ASSIGNMENT_PATTERN = Variable.VARIABLE_PATTERN_NAME
			+"( *(=) *(\S|\".*\"))?";
	//todo double with command-factory & is it okay to make all possible
	private Matcher m;

	public Variable variableFactory(boolean finality, String type, String assign, Block currentBlock) throws Exception {//todo is it ok with the name (vary similer to the class's name
		Variable newVar;
		Pattern p = Pattern.compile(VARIABLE_ASSIGNMENT_PATTERN);
		Matcher m = p.matcher(assign);

		if (m.group(2) != null) {
			String assignmentValue = Block.valueOfVar(m.group(1), currentBlock);// todo what happens if the
			// value of the another value is null
			if (assignmentValue == null)
				assignmentValue = m.group(3);
			newVar = new Variable(m.group(1), type, currentBlock, finality,  m.group(3));
		} else {
			newVar = new Variable(m.group(1), type, currentBlock, finality);
			}
		if (newVar.checkValidity()) {
			return newVar;
		} else {
			throw new Exception("fuck");
		}
	}
	// TODO: think how to make sure there is no duplicates
}
