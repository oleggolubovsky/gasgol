package il.wapp.app.exception;

public class FileFormatException extends RuntimeException {

	public FileFormatException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}

	public FileFormatException(String errorMessage) {
		super(errorMessage);
	}
}
