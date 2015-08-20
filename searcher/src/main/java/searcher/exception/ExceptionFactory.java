package searcher.exception;

public class ExceptionFactory {

	public static NotAFolderException createNotAFolderException(String msg) {
		return new NotAFolderException(msg);
	}

	public static SearcherTimeoutExceededException createSearcherTimeoutExceededException(String msg) {
		return new SearcherTimeoutExceededException(msg);
	}

}
