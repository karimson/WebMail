import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer
{
	private static boolean listen;
	
	
	public static void main(String [] args) throws UnsupportedEncodingException
	{
		listen = true;
		int port = 8000;
		ServerSocket serverSocket = null;
		
		try
		{
			serverSocket = new ServerSocket(port); 
		}
		catch(IOException e)
		{
			System.out.println("Error listening to port.");
			System.exit(0);
		}
                
		System.out.println("Server started, waiting for incoming connections.");
		
                QueueHandler queue = new QueueHandler();
                queue.start();
                
                System.out.println("Queue thread started..");
                               
		while(listen)
		{
			try 
                        {
				Socket clientSocket = serverSocket.accept();
				ThreadHandler handler = new ThreadHandler(clientSocket, queue);
				handler.start();
				System.out.println("Connection accepted");
			} 
                        catch (IOException e) 
                        {
				e.printStackTrace();
			} 
		}
		System.out.println("Server is now dead");
	}
	
	public static void kill()
	{
		listen = false;
	}

}
