package il.wapp.app.exception;

import lombok.*;

@Data
@Builder
public class ErrorResponse {
	private String errorMessage;
	private int statusCode;

	public ErrorResponse(String errorMessage, int statusCode) {
		super();
		this.errorMessage = errorMessage;
		this.statusCode = statusCode;
	}
}
