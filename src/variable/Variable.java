package variable;
import block.Block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class representing a variable.
 */
public class Variable {
	private String name;
	private String type;
	private String value;
	private boolean finality;

	public static final String VARIABLE_PATTERN_NAME = "([A-Za-z]+[\\w]*|_\\w+)";

	private static final String STRING_PATTERN = "\".*\"";

	private static final String INT_PATTERN = "-?\\d+";

	private static final String DOUBLE_PATTERN = "-?\\d+(\\.\\d+)?";

	private static final String CHAR_PATTERN = "\'(.?)\'";

	private static final String BOOLEAN_PATTERN = "true|false|\\d+(\\.\\d+)?|"+DOUBLE_PATTERN;


	Variable(String name, String type, Block parent, boolean finality){
		this.name = name;
		this.type = type;
		this.finality = finality;
		}

	Variable(String name, String type, Block parent, boolean finality, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.finality = finality;
		setDouble();
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
	private void setDouble (){
		if (type.equals(Types.DOUBLE.typeName)){
			if (!this.value.contains(".")){
				this.value +=".0";
			}
		}
	}
	public boolean checkValidity() {
		Pattern p = Pattern.compile(VARIABLE_PATTERN_NAME);
		Matcher m = p.matcher(name);
		if (!m.lookingAt()) {
			return false; //checks if the name begins with char
		}
		if (value != null) {
			for (Types type : Types.values()) {
				if (type.typeName.equals(this.type)) {
					p = Pattern.compile(type.typePattern);
					m = p.matcher(value);
					return m.matches();
				}
			}
			return true;

		} else {
			return !finality;
		}
	}

	public boolean checkValidity (String newVal) {
		for (Types type : Types.values()) {
			if (type.typeName.equals(this.type)) {
				Pattern p = Pattern.compile(type.typePattern);
				Matcher m = p.matcher(newVal);
				return m.find();
			}
		}
		return true;
	}

	public boolean getFinality(){
		return this.finality;
	}

	public String getName(){
		return this.name;
	}

	public String getValue() {
		return this.value;
	}

	public boolean setValue(String value){
		value = value.trim();
		if (checkValidity(value)) {
			this.value = value;
			return true;
		}
		return false;
	}
}