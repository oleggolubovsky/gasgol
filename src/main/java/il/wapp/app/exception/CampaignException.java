package il.wapp.app.exception;

public class CampaignException extends RuntimeException {

	public CampaignException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}

	public CampaignException(String errorMessage) {
		super(errorMessage);
	}
}
