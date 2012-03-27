import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ThreadHandler extends Thread
{
	private Socket socket;
	
	public ThreadHandler(Socket inputSocket)
	{
		this.socket = inputSocket;
	}
	
	public void run()
	{
		RequestResponseHandler rrh = new RequestResponseHandler();
		PrintWriter out = null;
		BufferedReader in = null;
		try 
		{
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String inputLine;			
			while(in.ready())
			{
				inputLine = in.readLine();
				System.out.println(inputLine);
				rrh.processRequest(inputLine);
			}
			String outLine = rrh.processOutput();
			System.out.println(outLine);
			out.print(outLine);
			
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
