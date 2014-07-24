package com.pommert.jedidiah.fractalviewerjava.fractals;

public class GenerationFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -929788809730340083L;

	public GenerationFailedException() {
	}

	public GenerationFailedException(String message) {
		super(message);
	}

	public GenerationFailedException(Throwable cause) {
		super(cause);
	}

	public GenerationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public GenerationFailedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
