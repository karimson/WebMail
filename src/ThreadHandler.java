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
			
			char[] inBuff = new char[10000];
			int charsRead = 0;
			String inputLine = "";  

			while (in.ready())
			{
				charsRead = in.read(inBuff, 0, 10000);
				inputLine += String.valueOf(inBuff,0, charsRead); 
			}
			
			if (charsRead != 0)
			{
				String proccessedLine;
				int inLineLength = inputLine.split("(\r\n|\r|\n)").length;
				for (int i = 0; i < inLineLength; i++)
				{
					proccessedLine = inputLine.split("(\r\n|\r|\n)")[i];
					if (proccessedLine.equals(""))
					{
						continue;
					}
					rrh.processRequest(proccessedLine, httpModel);
				}
				
				
	           /* while((inputLine = in.readLine()) != null)
				{
	                                System.out.println(inputLine);
					rrh.processRequest(inputLine, httpModel);
	                                System.out.println(inputLine);
				}
	            */            
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
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
}
