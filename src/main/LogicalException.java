package main;

public class LogicalException extends Exception {
	private static final long serialVersionUID = 1L;

	private static final String ERROR_MESSAGE = "ERROR: Bad format of Commands File";//TODO

	/**
	 * exception constructor.
	 */
	LogicalException() {super(ERROR_MESSAGE);}
}

