import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ChatCommands {
	CHANGE_NICKNAME  ("^/nick\\s+(\\w+)$")      ,
	DIRECT_MESSAGE   ("^/dm\\s+(\\w+)\\s+(.*)$"),
	QUIT             ("^/quit$")                ,
	NONE             (null)                     ;
	
	private String commandRegex;
	
	ChatCommands(String cmdRegex) {
		this.commandRegex = cmdRegex;
	} 
	
	private Pattern get_regex() {
		return Pattern.compile(this.commandRegex,
				               Pattern.CASE_INSENSITIVE | Pattern.COMMENTS);
	}
	
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
