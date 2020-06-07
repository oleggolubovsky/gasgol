package il.wapp.app.exception;

public class ParseJsonException extends RuntimeException {

	public ParseJsonException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}

	public ParseJsonException(String errorMessage) {
		super(errorMessage);
	}
}
