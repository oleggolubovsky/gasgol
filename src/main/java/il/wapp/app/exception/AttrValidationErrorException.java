package il.wapp.app.exception;

import il.wapp.app.validation.*;
import lombok.*;

import java.util.*;

@Getter
public class AttrValidationErrorException extends RuntimeException {

	private List<AttrValidationError> errors;

	public AttrValidationErrorException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}

	public AttrValidationErrorException(String message) {

		super(message == null ? "Ошибка при проверке данных с формы": message);
	}
}
