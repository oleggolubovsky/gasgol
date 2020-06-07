package il.wapp.app.exception;

public class AuthException extends RuntimeException {

	public AuthException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}

	public AuthException(String errorMessage) {
		super(errorMessage);
	}
}
