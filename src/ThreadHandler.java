import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ThreadHandler extends Thread
{
	private Socket socket = null;
        private QueueHandler queue;
	
	public ThreadHandler(Socket inputSocket, QueueHandler inputQueue)
	{
		this.socket = inputSocket;
                this.queue = inputQueue;
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
				String processedLine;
				int inLineLength = inputLine.split("(\r\n|\r|\n)").length;
				for (int i = 0; i < inLineLength; i++)
				{
					processedLine = inputLine.split("(\r\n|\r|\n)")[i];
					if (processedLine.equals(""))
					{
						continue;
					}
					rrh.processRequest(processedLine, httpModel);
				}
				                   
				if (httpModel.type.equals("GET") || httpModel.type.equals("POST"))
				{
                                    if(httpModel.path.equals("/") || httpModel.path.equals("/status"))
                                    {
                                        if(httpModel.type.equals("POST") && !httpModel.path.equals("/status"))
                                        {
                                            mail.parseMailData(httpModel.mailData);
                                            queue.addMail(mail);
                                        }
                                        rrh.processOutput(httpModel, out, queue.getStatusPage());
                                    }
                                    else
                                    {
                                        out.write(rrh.get404());
                                    }
				}
				else
				{
					out.write("HTTP command not supported");
				}
				
				out.close();
				in.close();
				socket.close();
			}	
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
}
