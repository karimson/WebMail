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
                Mail mail;
	
		try 
		{
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			httpModel = new HTTPModel();
			mail = new Mail();
                        String inputLine = "";
                        	
                        while((inputLine = in.readLine()) != null)
			{
                                System.out.println(inputLine);
				rrh.processRequest(inputLine, httpModel);
                                System.out.println(inputLine);
			}
                        
			System.out.println(httpModel.type);
                                
			if (httpModel.type.equals("GET") || httpModel.type.equals("POST"))
			{
                            if(httpModel.path.equals("/"))
                            {
				rrh.processOutput(httpModel, out, mail);
                            }
                            else
                            {
                                out.write(rrh.get404());
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
