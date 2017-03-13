import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enumeration storing all user commands supported by the Chat Client.
 */
public enum ChatCommands {
	CHANGE_NICKNAME  ("^/nick\\s+(\\w+)$")      ,
	DIRECT_MESSAGE   ("^/dm\\s+(\\w+)\\s+(.*)$"),
	QUIT             ("^/quit$")                ,
	NONE             (null)                     ;
	
	private final String commandRegex;
	
	ChatCommands(String cmdRegex) {
		this.commandRegex = cmdRegex;
	} 
	
	/**
	 * @return Pattern object for {@link #commandRegex} with case-insensitive
	 *         flag and white-space ignored.
	 */
	private Pattern get_regex() {
		return Pattern.compile(this.commandRegex,
				               Pattern.CASE_INSENSITIVE | Pattern.COMMENTS);
	}
	
	/**
	 * Finds the {@link ChatCommands} whose regex matches the
	 * {@code commandText} provided by the user.
	 * 
	 * @param commandText - User Input
	 * 
	 * @return {@link ChatCommands} value corresponding to the provided
	 *         {@code commandText} or {@link #NONE}.
	 */
	public static ChatCommands convert(String commandText) {
		
		for (ChatCommands command : ChatCommands.values()) {
			
			// Skip NONE
			if (StringHelper.isNullOrEmpty(command.commandRegex)) {
				continue;
			}
			
			// Create matcher for the regex and find all matches
			Matcher matcher = command.get_regex().matcher(commandText);
			
			if (matcher.find()){
				return command;
			} 
		}
		
		return ChatCommands.NONE;
	}
	
	/**
	 * Parses the provided {@code commandText} and returns the retrieved
	 * arguments if there is any.
	 * 
	 * @param commandText - User Input
	 * 
	 * @return List of all arguments specified in the {@code commandText}.
	 */
	public static List<String> get_args(String commandText) {
		for (ChatCommands command : ChatCommands.values()) {
			
			// Skip NONE
			if (StringHelper.isNullOrEmpty(command.commandRegex)) {
				continue;
			}
			
			// Create matcher for the regex and find all matches
			Matcher matcher = command.get_regex().matcher(commandText);
			
			if (matcher.find()){
				List<String> args = new ArrayList<String>();
				
				for (int i = 1; i <= matcher.groupCount(); i++) {
					args.add(matcher.group(i));
				}
				
				return args;
			} 
		}
		
		return null;
	}
}
