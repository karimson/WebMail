import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class RequestResponseHandler
{
	public static Mail mail = new Mail();
	
	public void processRequest(String request, HTTPModel httpModel)
	{
		if(request.startsWith("GET"))
		{
			httpModel.type = "GET";
			httpModel.path = request.split(" ")[1].trim();
		}
		else if(request.startsWith("POST"))
		{
			httpModel.type = "POST";
			httpModel.path = request.split(" ")[1].trim();
		}
		else if (request.startsWith("Connection:")) 
		{
			httpModel.connection = request.substring(12).trim();
		}
		else if (request.startsWith("User-Agent:")) 
		{
			httpModel.userAgent = request.substring(12).trim();
		}
		else if(request.startsWith("Host:"))
		{
			httpModel.host = request.split(" ")[1];
		}
		else if (request.startsWith("Accept:")) 
		{
			httpModel.accept = request.substring(7).trim();
		}
		else if (request.startsWith("Accept-Encoding:")) 
		{
			httpModel.acceptEncoding = request.substring(16).trim();
		}
		else if (request.startsWith("Accept-Language:")) 
		{
			httpModel.acceptLanguage = request.substring(16).trim();
		}
		else if (request.startsWith("Accept-Charset:")) 
		{
			httpModel.acceptCharset = request.substring(15).trim();
		} 	
		else if(httpModel.type.equals("POST") && request.startsWith("FROM"))
		{
			httpModel.mailData = request;
		}
	}

	public String processOutput(HTTPModel httpModel) 
	{
		String data = "";
		String inputLine = "";
		String response = "";
		BufferedReader in = null;
		
		if(httpModel.type.equals("POST") && httpModel.path.equals("/mail")) 
		{
			
			mail.parseMailData(httpModel.mailData);
			data = mail.sendMail();
			
		} 
		else{
			try 
			{
				in = new BufferedReader(new FileReader("webmail.html"));
			} 
			catch (FileNotFoundException e)
			{
				System.out.println("No such file");
			}
			try 
			{
				while ((inputLine = in.readLine()) != null) 
				{
					data += inputLine + "\r\n";
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		
		}
		response = "HTTP/1.0 200 OK\r\n";
		response += "Content-Type: text/html\r\n";
		response += "Content-Length: " + data.length() + "\r\n";
		response += "Connection: close\r\n";
		response += "\r\n";
		response += data;

		return response;
		
	}
	
}
