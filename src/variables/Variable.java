package variables;

import blocks.Block;

import java.util.Set;
import java.util.regex.*;

public class Variable {
	Set types;
	String name;
    Type type;
    String value;
    Block parent;
	boolean finality;
	private static final String VARIABLE_PATTERN_NAME = "/S+.*|_/S+]";


	Variable (String name, String type, Block parent) throws ClassNotFoundException {
		this.name = name;
		this.type = Type.getType(type);
		this.parent = parent;
	}
	Variable (String name, String type, Block parent, String value, boolean finality) throws ClassNotFoundException {
		this.name = name;
		this.type = Type.getType(type);
		this.parent = parent;
		this.value = value;
		this.finality = finality;

	}


    boolean checkValidity() {
	    if (!types.contains(this.type)) {
		    return false;
	    }

	    Pattern namePattern = Pattern.compile(VARIABLE_PATTERN_NAME);
	    Matcher nameMatcher = namePattern.matcher(name);
	    if (!nameMatcher.lookingAt()){ //checks if the name begins with char
	    	return false;
	    }
	    if (value!=null){return type.}



    }
}