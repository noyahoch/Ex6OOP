package main;

public class SyntaxException extends ReadingCodeException {
	private static final long serialVersionUID = 1L;

	private static final String ERROR_MESSAGE = "Syntax Exception";//todo

	/**
	 * exception constructor.
	 * @param s the exception message.
	 */
	SyntaxException(String s) {super(s);}

	SyntaxException(){
		super(ERROR_MESSAGE);
	}
}
