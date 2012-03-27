import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Mail 
{
	String from;
	String to;
	String subject;
	String smtpServer;
	String message;
	DataOutputStream mailSocketOut = null;
	BufferedReader mailSocketIn = null;
	Socket mailSocket = null;
	String output = "";
	
	public void parseMailData(String data)
	{
		from = data.substring(data.indexOf("FROM="), data.indexOf("&", data.indexOf("FROM=")));
		to = data.substring(data.indexOf("TO="), data.indexOf("&", data.indexOf("TO=")));
		subject = data.substring(data.indexOf("SUBJECT="), data.indexOf("&", data.indexOf("SUBJECT=")));
		smtpServer = data.substring(data.indexOf("SERVER="), data.indexOf("&", data.indexOf("SERVER=")));
		message = data.substring(data.indexOf("MESSAGE="));
	}

	public String sendMail()
	{
		try
		{
			mailSocket = new Socket("mail.ik2213.lab", 25); 
			mailSocketOut = new DataOutputStream(mailSocket.getOutputStream());
			mailSocketIn = new BufferedReader(new InputStreamReader(mailSocket.getInputStream()));
		}
		catch(IOException e)
		{
			System.out.println("Error connecting to SMTP-server.");
		}
		
		try {
			mailSocketOut.writeBytes("HELO someone.kth.se\r\n");
			if (mailSocketIn.ready())
			{	
				System.out.println(mailSocketIn.readLine());
			}
			
			mailSocketOut.writeBytes("MAIL FROM:<" + from + ">\r\n");
			if (mailSocketIn.ready())
			{	
				System.out.println(mailSocketIn.readLine());
			}
			
			mailSocketOut.writeBytes("RCPT TO:<" + to + ">\r\n");
			
			if (mailSocketIn.ready())
			{	
				System.out.println(mailSocketIn.readLine());
			}
			mailSocketOut.writeBytes("SUBJECT: " + subject +"\r\n");
			
			if (mailSocketIn.ready())
			{	
				System.out.println(mailSocketIn.readLine());
			}
			mailSocketOut.writeBytes("DATA" + "\r\n");
			
			if (mailSocketIn.ready())
			{	
				System.out.println(mailSocketIn.readLine());
			}
			mailSocketOut.writeBytes(message + "\r\n");
			
			if (mailSocketIn.ready())
			{	
				System.out.println(mailSocketIn.readLine());
			}
			mailSocketOut.writeBytes("\r\n.\r\n");
			
			if (mailSocketIn.ready())
			{	
				System.out.println(mailSocketIn.readLine());
			}
			mailSocketOut.writeBytes("QUIT\r\n");
			
			if (mailSocketIn.ready())
			{	
				System.out.println(mailSocketIn.readLine());
			}
			
			
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//KOLLA respons från servern
		

		return "fest";
		
	}
}
