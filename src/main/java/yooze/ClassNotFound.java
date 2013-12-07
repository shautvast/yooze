package yooze;

@SuppressWarnings("serial")
public class ClassNotFound extends RuntimeException {

	public ClassNotFound(String message) {
		super(message);
	}

	public ClassNotFound(Throwable cause) {
		super(cause);
	}

}