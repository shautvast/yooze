package yooze.domain;

@SuppressWarnings("serial")
public class MethodNotFound extends RuntimeException {

	public MethodNotFound(String message) {
		super(message);
	}

	public MethodNotFound(Throwable cause) {
		super(cause);
	}

}