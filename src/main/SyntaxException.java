package main;

public class SyntaxException extends ReadingCodeException {
	private static final long serialVersionUID = 1L;

	private static final String ERROR_MESSAGE = "SYNTAX EXCEPTION";

	/**
	 * exception constructor.
	 * @param s the exception message.
	 */
	public SyntaxException(String s) {super(s);}

	public SyntaxException(){
		super(ERROR_MESSAGE);
	}
}
