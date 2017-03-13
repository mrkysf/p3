import java.io.Serializable;

/**
 * Serializable Command Interface that must be implemented by all
 * Commands transmitted between the server and client.
 */
public interface ICommand extends Serializable {
	/**
	 * Implement this function with the necessary code to be executed on the
	 * destination machine.
	 * 
	 * @param serviceProvider - 
	 *            Contains all objects needed by this execute function to
	 *            perform its task.
	 */
    void execute(ServiceDataProvider serviceProvider);
}
