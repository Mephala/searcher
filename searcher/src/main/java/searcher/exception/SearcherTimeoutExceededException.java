package searcher.exception;

public class SearcherTimeoutExceededException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2180141426360192446L;

	private final String msg;

	protected SearcherTimeoutExceededException(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

}
