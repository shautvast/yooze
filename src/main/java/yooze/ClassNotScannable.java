package yooze;

@SuppressWarnings("serial")
public class ClassNotScannable extends RuntimeException {

	public ClassNotScannable(String message) {
		super(message);
	}

	public ClassNotScannable(Throwable cause) {
		super(cause);
	}

}