package il.wapp.app.exception;

public class PermissionException extends RuntimeException {

	public PermissionException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}

	public PermissionException(String errorMessage) {
		super(errorMessage);
	}
}
