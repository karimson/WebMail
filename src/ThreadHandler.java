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
                Mail mail = new Mail();
	
		try 
		{
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			httpModel = new HTTPModel();
			String inputLine = in.readLine();
			if (inputLine != null && (inputLine.contains("GET") || inputLine.contains("POST")))
			{
                            if(inputLine.split(" ")[1].equals("/"))
                            {
                                rrh.processRequest(inputLine, httpModel, mail);		
				while(in.ready())
				{
					inputLine = in.readLine();
					rrh.processRequest(inputLine, httpModel, mail);
				}
				System.out.println(httpModel.type);
				rrh.processOutput(httpModel, out, mail);
                            }
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
