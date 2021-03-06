
/**
 * Exception thrown by function when one of its required arguments is not
 * specified.
 */
public final class MissingArgumentException extends Exception {
	
	private static final long serialVersionUID = -558407018549401023L;

	public MissingArgumentException(String argName) {
		super(String.format("Argument '%s' is missing.", argName));
	}
}
