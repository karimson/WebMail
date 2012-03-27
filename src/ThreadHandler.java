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
			if ((inputLine.contains("GET") || inputLine.contains("POST")) && inputLine != null )
			{
				rrh.processRequest(inputLine, httpModel);		
				while(in.ready())
				{
					inputLine = in.readLine();
					rrh.processRequest(inputLine, httpModel);
				}
				System.out.println(httpModel.type);
				String outLine = rrh.processOutput(httpModel);
				//System.out.println(outLine);
				out.print(outLine);
				
			}
			else
			{
				System.out.println("FAKEEEEEEEEEEEEE");
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
