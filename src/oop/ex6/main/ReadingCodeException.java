package oop.ex6.main;

/**
 * A class representing a code reading exception.
 */
public abstract class ReadingCodeException extends Exception {
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_MESSAGE = "ERROR READING THE CODE";

	/**
	 * Constructor with a specific message
	 * @param msg the message
	 */
	ReadingCodeException(String msg) {
		super(msg);
	}

	/**
	 * Default constructor.
	 */
	ReadingCodeException() {
		super(DEFAULT_MESSAGE);
	}
}

