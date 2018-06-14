package main;

public class SyntaxException extends Exception {
	private static final long serialVersionUID = 1L;

	private static final String ERROR_MESSAGE = "ERROR: Bad format of Commands File";//todo

	/**
	 * exception constructor.
	 */
	SyntaxException() {super(ERROR_MESSAGE);}
}
