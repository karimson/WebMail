import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ThreadHandler extends Thread
{
	private Socket socket = null;
	
	public ThreadHandler(Socket inputSocket)
	{
		this.socket = inputSocket;
	}
	
	public void run()
	{
		RequestResponseHandler rrh = new RequestResponseHandler();
		PrintWriter out = null;
		BufferedReader in = null;
		HTTPModel httpModel;
	
		try 
		{
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			httpModel = new HTTPModel();
			String inputLine = in.readLine();
			if (inputLine != null && (inputLine.contains("GET") || inputLine.contains("POST")))
			{
				rrh.processRequest(inputLine, httpModel);		
				while(in.ready())
				{
					inputLine = in.readLine();
					rrh.processRequest(inputLine, httpModel);
				}
				System.out.println(httpModel.type);
				rrh.processOutput(httpModel, out);
			}
			else
			{
				System.out.println("Command not supported");
			}
			
			out.close();
			in.close();
			socket.close();
			System.out.println("Thread closed");
						
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}

}