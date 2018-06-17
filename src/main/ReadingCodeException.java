package main;

public abstract class ReadingCodeException extends Exception {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "ERROR READING THE CODE";

	/**
	 * exception constructor.
	 * @param s message to be shown
	 */
	ReadingCodeException(String s) {
		super(s);
	}

	ReadingCodeException() {
		super(DEFAULT_MESSAGE);
	}
}

