package searcher.exception;

public class NotAFolderException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4589232867793965501L;
	private final String message;

	public String getMessage() {
		return message;
	}

	protected NotAFolderException(String message) {
		this.message = message;
	}

}
