import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
	DataInputStream mailSocketIn = null;
	Socket mailSocket = null;
	
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
			mailSocketIn = new DataInputStream(mailSocket.getInputStream());
		}
		catch(IOException e)
		{
			System.out.println("Error connecting to SMTP-server.");
		}
		
		try {
			mailSocketOut.writeBytes("HELO someone.kth.se\r\n");
			mailSocketOut.writeBytes("MAIL FROM:<" + from + ">\r\n");
			mailSocketOut.writeBytes("RCPT TO:<" + to + ">\r\n");
			mailSocketOut.writeBytes("SUBJECT: " + subject +"\r\n");
			mailSocketOut.writeBytes("DATA" + "\r\n");
			mailSocketOut.writeBytes(message + "\r\n");
			mailSocketOut.writeBytes("\r\n.\r\n");
			mailSocketOut.writeBytes("QUIT\r\n");
			
			String responseline;
		    while((responseline = mailSocketIn.readLine())!=null)
		    {  // System.out.println(responseline);
		        if(responseline.indexOf("Ok") != -1)
		            break;
		    }
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//KOLLA respons från servern

		return "ALLES GUT";
		
	}
}
