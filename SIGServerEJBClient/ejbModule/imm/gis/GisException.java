package imm.gis;

public class GisException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GisException() {
		super();
	}

	public GisException(String message) {
		super(message);
	}

	public GisException(Throwable cause) {
		super(cause);
	}

	public GisException(String message, Throwable cause) {
		super(message, cause);
	}

}