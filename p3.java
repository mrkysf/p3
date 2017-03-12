import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCCS
{
	private static ServerSocket server;

	private static ArrayList<ThreadedSocket> threads = new ArrayList<ThreadedSocket>();

	public static void main(String[] args) throws Exception 
	{
		try
		{
			if(args.length < 1)
			{
				System.out.println("No port number entered. Now exiting.");
				System.exit(-1);
			}

			int portNumber = Integer.parseInt(args[0]);
			
			//int portNumber = 7777;

			server = new ServerSocket(portNumber);

			System.out.println("Server is running.");
			while (true) 
			{
				int emptyThreadIndex = 0;
				boolean foundEmptyThread = false;
				Socket socket = server.accept();
				for(int i=0; i < threads.size(); i++)
				{
					if (threads.get(i)==null && !foundEmptyThread)
					{
						emptyThreadIndex = i;
						foundEmptyThread = true;
					}
				}
				if(foundEmptyThread)
					threads.set(emptyThreadIndex, new ThreadedSocket(socket)).start();
			}

		} 
		catch(IOException e) 
		{ 
			e.printStackTrace();
		}	
		finally
		{
			server.close();
		}
	} 

	private static class ThreadedSocket extends Thread 
	{
		private String name;
		private Socket socket;
		ObjectInputStream input; 
		ObjectOutputStream output;

		ThreadedSocket(Socket socket)
		{
			this.socket = socket;
		}

		public void run() 
		{
			boolean runThread = true;
			try {
				input = new ObjectInputStream(socket.getInputStream());

				output = new ObjectOutputStream(socket.getOutputStream());
				

				output.writeObject("Enter Your Nickname:");
				name = (String) input.readObject();
				//name cannot contain spaces
				Pattern p = Pattern.compile("\\s");
				Matcher m = p.matcher(name);
				boolean hasSpaces = m.find();
				
				if (name == null) 
				{
					name = "anonymous";
				}			
				else if(hasSpaces)
				{
					name = null;
					System.out.println("Nickname cannot contain spaces");
					return;
				}

				output.writeObject("Your nickname is: " + name);

				while (runThread) 
				{
					String message = (String) input.readObject();
					if (message == null) 
					{
					}
					else if(message.startsWith("/quit")) 
					{
						runThread = false;
					}
					else if(message.startsWith("/nick")) 
					{
						String[] words = message.split("\\s");
						assert( words.length > 1);
						String newName = words[1];
						//assert has no spaces
						Pattern pattern = Pattern.compile("\\s");
						Matcher matcher = pattern.matcher(newName);
						boolean containsSpaces = matcher.find();
						if(containsSpaces)
							System.out.println("Nickname cannot contain spaces");
						else
							name = newName;
					}
					else if (message.startsWith("/dm")) 
					{
						String[] words = message.split("\\s");
						if(words.length < 3)
						{
							System.out.println("Incorrect syntax for /dm");
						}
						else
						{
							String to = words[1];
							//assert to does not contain spaces
							Pattern pattern = Pattern.compile("\\s");
							Matcher matcher = pattern.matcher(to);
							boolean containsSpaces = matcher.find();

							if(containsSpaces)
								System.out.println("Incorrect name syntax in /dm");
							else
							{
								String msg = null;
								for(int i = 2; i < words.length; i++)
								{  
									msg += words[i];
								}
								//assert msg is not just spaces
								boolean onlyWhiteSpaces = msg.matches("^\\s*$");
								if(onlyWhiteSpaces)
									System.out.println("Message cannot contain only spaces.");
								else
								{
									synchronized (this) 
									{
										for (ThreadedSocket s : threads) 
											if (s != this && s.name.equals(to))
											{
												s.output.writeObject(name + ": " + msg);
												this.output.writeObject(name + ": " + msg);
											}
									}
								}
							}
						}
					}
					else //send message to Everyone
					{
						synchronized(this)
						{
							for (ThreadedSocket s : threads) 
								if(s.name != null)
									s.output.writeObject(name + ": " + message);

						}
					}
				} 

			}
			catch (Exception e) 
			{
				e.printStackTrace();
			} 
			finally 
			{
				try 
				{
					name = null;
					for (ThreadedSocket s : threads) 
						if(s == this)
							s = null;
					input.close();
					output.close();
					socket.close();
					System.out.println("Chat Ended"); 
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
}
