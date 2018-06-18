package main;

public class LogicalException extends ReadingCodeException {
	private static final long serialVersionUID = 1L;

	private static final String ERROR_MESSAGE = "LOGICAL EXCEPTION";

	/**
	 * exception constructor
	 * @param s
	 */
	public LogicalException(String s) {super(s);}

	LogicalException() {super (ERROR_MESSAGE);}
}
