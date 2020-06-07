package il.wapp.app.exception;

import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.*;
import org.springframework.web.servlet.mvc.method.annotation.*;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({AuthException.class, AttrValidationErrorException.class})
	public final ResponseEntity<Object> handleCustomExceptions(Exception ex, WebRequest request) {
		log.error(ex.getMessage(), ex);
		HttpStatus httpStatus = ex instanceof AuthException ? HttpStatus.UNAUTHORIZED : HttpStatus.BAD_REQUEST;
		return new ResponseEntity(ErrorResponse.builder().errorMessage(ex.getMessage())
				.statusCode(httpStatus.value())
				.build(), httpStatus);
	}

	@ExceptionHandler({Exception.class})
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		log.error(ex.getMessage(), ex);
		return new ResponseEntity(ErrorResponse.builder().errorMessage(ex.getMessage())
				.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.build(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
