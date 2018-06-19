package oop.ex6.main;

/**
 * A class representing a syntax error in a sjava file.
 */
public class SyntaxException extends ReadingCodeException {
	private static final long serialVersionUID = 1L;

	private static final String ERROR_MESSAGE = "SYNTAX EXCEPTION";

	/**
	 * Constructor with a specific message
	 * @param msg the message
	 */
	public SyntaxException(String msg) {super(msg);}

	/**
	 * Default constructor.
	 */
	public SyntaxException(){
		super(ERROR_MESSAGE);
	}
}
