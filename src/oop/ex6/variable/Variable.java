package oop.ex6.variable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class representing a variable in an Sjava file.
 */
public class Variable {
	private String name;
	private String type;
	private String value;
	private boolean finality;

	public static final String VARIABLE_PATTERN_NAME = "([A-Za-z]+[\\w]*|_\\w+)";
	public static final String STRING_PATTERN = "\".*\"";
	public static final String INT_PATTERN = "-?\\d+";
	public static final String DOUBLE_PATTERN = "-?\\d+(\\.\\d+)?";
	public static final String CHAR_PATTERN = "\'(.?)\'";
	public static final String BOOLEAN_PATTERN = "true|false|"+DOUBLE_PATTERN;

	/**
	 * Constructs a new variable.
	 * @param name the variable's name
	 * @param type the variable type
	 * @param finality true iff the variable is final
	 */
	Variable(String name, String type, boolean finality){
		this.name = name;
		this.type = type;
		this.finality = finality;
		}

	/**
	 * Constructs a new variable
	 * @param name the variable's name
	 * @param type the variable type
	 * @param finality true iff the variable is final
	 * @param value the value of the variable.
	 */
	Variable(String name, String type, boolean finality, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.finality = finality;
		setDouble(); // Changes the value of the variable if its double
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

	/**
	 * In case the variable is a double assigned with int, add ".0"
	 * so it won't be considered as an int
	 */
	private void setDouble (){
		if (type.equals(Types.DOUBLE.typeName))
			if (!this.value.contains("."))
				this.value +=".0";
	}

	/**
	 * Checks the validity of the variable, according to its type.
	 * @return true iff the variable is valid.
	 */
	public boolean checkValidity() {
		//Pattern p = Pattern.compile(VARIABLE_PATTERN_NAME); TODO make sure its OK to erase
		//Matcher m = p.matcher(name);
		//if (!m.lookingAt()) {
		//	return false; //checks if the name begins with char
		//}
		if (value != null)
			return checkValidity(value);
		else
			return !finality; // Final var must be declared with a value.
	}


	/**
	 * Checks the validity of a given value assigned a variable.
	 * @param newVal the value to be assigned.
	 * @return true iff the new value is valid
	 */
	public boolean checkValidity (String newVal) {
		for (Types type : Types.values()) {
			if (type.typeName.equals(this.type)) {
				Pattern p = Pattern.compile(type.typePattern);
				Matcher m = p.matcher(newVal);
				return m.matches();
			}
		}
		return false; // Declaration didn't match anything TODO make sure its fine with Noya
	}

	/**
	 * @return true iff the variable is final.
	 */
	public boolean getFinality(){
		return this.finality;
	}

	/**
	 * @return the variable's name
	 */
	public String getName(){
		return this.name;
	}

	/**
	 * @return A String representation of the variable's value.
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the value of the variable iff its valud
	 * @param value the value to be assigned
	 * @return true if the assignment was successful
	 */
	public boolean setValue(String value){ //TODO should throw exception?
		value = value.trim();
		if (checkValidity(value)) {
			this.value = value;
			return true;
		}
		return false;
	}
}