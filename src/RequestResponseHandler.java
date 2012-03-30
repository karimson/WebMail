import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RequestResponseHandler
{	
	public void processRequest(String request, HTTPModel httpModel, Mail mail)
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
		if(httpModel.type.equals("POST") && request.startsWith("FROM"))
		{
			httpModel.mailData = request;
		}
	}

	public void processOutput(HTTPModel httpModel, PrintWriter out, Mail mail) 
	{	
            if(httpModel.type.equals("POST")) 
            {
                mail.parseMailData(httpModel.mailData);
                try 
                {
                    if(mail.sendMail().equals("OK"))
                        out.write(getPage("mailSent.html"));
                    else
                        out.write(getPage("mailNotSent.html"));
                } 
                catch (UnsupportedEncodingException ex) 
                {
                    Logger.getLogger(RequestResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            } 
            else
            {
               out.write(getPage("webmail.html"));
            }
	}
        
        public String getPage(String fileName)
        {
            BufferedReader in = null;
            String data = "";
            String inputLine = "";
            String response = "";
            
            try 
            {
		in = new BufferedReader(new FileReader(fileName));
            } 
            catch (FileNotFoundException e)
            {
		System.out.println("File not found");
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
            response = "HTTP/1.0 200 OK\r\n";
            response += "Content-Type: text/html\r\n";
            response += "Content-Length: " + data.length() + "\r\n";
            response += "Connection: close\r\n";
            response += "\r\n";
            response += data;
            
            return response;
        }
	
}
