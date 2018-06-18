package block;

import main.LogicalException;
import variable.*;

import java.util.ArrayList;

/**
 * a class representing a while loop or an if statement in a Sjavac file.
 */
public class Conditional extends Block{

    private static final String BOOLEAN_OP = " *&& *| *\\|\\| *";

    private static final String TRUE = "true";

    private static final String FALSE = "false";

	private String condition;

    public Conditional(String condition, Block parent){
    	this.condition = condition.trim();
    	this.isMethod = false;
    	this.parent = parent;
    	this.variables = new ArrayList<>();
    }

	public String[] breakCondToParts() {
		return condition.split(BOOLEAN_OP);
	}

	/**
     * Checks validity of the condition of the Conditional.
     * @return true iff it is a valid boolean condition.
     */

    public boolean checkValidity() {
	    if (condition.equals("")) {
		    return false;
	    }
	    char lastChar = condition.charAt(condition.length() - 1);
	    if (lastChar == '&' || lastChar == '|') {
		    return false;
	    }
	    String parts[] = condition.split(BOOLEAN_OP);
	    for (String part : parts) {
		    if (part.contains("|") || part.contains("&")) {
			    return false;
		    }

		    Variable var = findVar(part);

		    if (var != null) {
			    part = var.getValue();
		    }

		    if (part == null || part.trim().equals("")) {
			    return false;
		    }

		    if (!(part.trim().equals(TRUE) || part.equals(FALSE))) {
			    try {
				    Double.parseDouble(part);
			    } catch (Exception e) {
				    return false;
			    }
		    }
	    }
	    return true;
    }

}
