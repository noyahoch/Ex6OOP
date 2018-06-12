package variable;



import block.Block;

import java.util.regex.*;

public class Variable {
	private String name;
	private String type;
	private String value;
	private Block parent;
	private boolean finality;
	public static final String VARIABLE_PATTERN_NAME = "[A-Za-z]+.*|_\\S+";
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
		String typePattern;
		String typeName;

		Types(String typeName, String typePattern) {
			this.typeName = typeName;
			this.typePattern = typePattern;
		}

	}

	boolean checkValidity() {
		Pattern p = Pattern.compile(VARIABLE_PATTERN_NAME);
		Matcher m = p.matcher(name);
		if (!m.lookingAt()) return false; //checks if the name begins with char
		if (value != null) {
			for (Types type : Types.values()) {
				if (type.typeName.equals(this.type)) {
					p = Pattern.compile(type.typePattern);
					m = p.matcher(value);
					return m.find();
				}
			}
			return true;
		}
		return true;
	}


	public void setValue (String value){
		this.value = value;
	}

}