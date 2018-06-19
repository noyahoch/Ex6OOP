package main;

/**
 * A class representing a logical exception in a Sjava file.
 */

public class LogicalException extends ReadingCodeException {
	private static final long serialVersionUID = 1L;
	private static final String ERROR_MESSAGE = "LOGICAL EXCEPTION";

	/**
	 * Constructor with a specific message
	 * @param msg the message
	 */
	public LogicalException(String msg) {super(msg);}

	/**
	 * Default constructor.
	 */
	LogicalException() {super (ERROR_MESSAGE);}
}
