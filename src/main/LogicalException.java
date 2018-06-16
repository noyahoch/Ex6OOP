package main;

public class LogicalException extends ReadingCodeException {
	private static final long serialVersionUID = 1L;

	private static final String ERROR_MESSAGE = "Logical Exception";//TODO

	/**
	 * exception constructor.
	 * @param s
	 */
	LogicalException(String s) {super(ERROR_MESSAGE);}
}

