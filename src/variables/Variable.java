package variables;

import blocks.Block;
import com.sun.deploy.security.ValidationState;

import java.util.Set;
import java.util.regex.*;

public class Variable {
	Set types;
	String name;
	String type;
	String value;
	Block parent;
	boolean finality;
	private static final String VARIABLE_PATTERN_NAME = "[A-Za-z]+.*|_\\S+";
	private static final String STRING_PATTERN = "\".*\"";
	private static final String INT_PATTERN = "\\d+";
	private static final String DOUBLE_PATTERN = "\\d+(\\.\\d+)?";
	private static final String CHAR_PATTERN = "\'(.*)?";
	private static final String BOOLEAN_PATTERN = "true|false|\\d+(\\.\\d+)?";


	Variable(String name, String type, Block parent) throws ClassNotFoundException {
		this.name = name;
		this.type = type;
		this.parent = parent;
	}

	Variable(String name, String type, Block parent, String value, boolean finality) throws ClassNotFoundException {
		this.name = name;
		this.type = type;
		this.parent = parent;
		this.value = value;
		this.finality = finality;

	}

	private enum Types {
		INT("int", INT_PATTERN),
		STRING("String", STRING_PATTERN),
		BOOLEAN("boolean", BOOLEAN_PATTERN),
		DOUBLE("double", DOUBLE_PATTERN),
		CHAR("char", CHAR_PATTERN);
		String typesPattern;
		String typesName;

		Types(String typesName, String typesPattern) {
			this.typesName = typesName;
			this.typesPattern = typesPattern;
		}

	}

	boolean checkValidity() {
		if (!types.contains(this.type)) {
			return false;
		}

		Pattern namePattern = Pattern.compile(VARIABLE_PATTERN_NAME);
		Matcher nameMatcher = namePattern.matcher(name);
		if (!nameMatcher.lookingAt()) return false; //checks if the name begins with char

		if (value != null) {
			for (Types type : Types.values()) {
				if (type.typesName.equals(type)) {
					namePattern = Pattern.compile(type.typesPattern);
					nameMatcher = namePattern.matcher(value);
					if (!nameMatcher.find()) ;
					return false;
				}

			}

		}
		return true;
	}
}



    }
}