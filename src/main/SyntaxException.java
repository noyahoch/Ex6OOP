package main;

public class SyntaxException extends ReadingCodeException {
	private static final long serialVersionUID = 1L;

	private static final String ERROR_MESSAGE = "Syntax Exception";//todo

	/**
	 * exception constructor.
	 * @param missing_return_statement
	 */
	SyntaxException(String missing_return_statement) {super(ERROR_MESSAGE);}
}
