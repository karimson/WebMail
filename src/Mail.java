import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.InetAddress;

public class Mail {
	String from;
	String to;
	String subject;
	String smtpServer;
	String message;
	BufferedWriter out = null;
	BufferedReader in = null;
	Socket mailSocket = null;
	String output = "";

	public void parseMailData(String data) 
	{
		from = data.substring(data.indexOf("FROM=")+5, data.indexOf("&", data.indexOf("FROM=")));
		to = data.substring(data.indexOf("TO=") + 3, data.indexOf("&", data.indexOf("TO=")));
		subject = data.substring(data.indexOf("SUBJECT=")+8, data.indexOf("&", data.indexOf("SUBJECT=")));
		message = data.substring(data.indexOf("MESSAGE=")+8, data.indexOf("&SENDBUTTON=Send"));
	}

	public String sendMail() {
		try {
                        NsLookup ns = new NsLookup();
                        String mxServer = ns.mxLookup(to.split("%40")[1]);
                        
                        if(mxServer == "")
                            return "Server Not Found";
                        
			mailSocket = new Socket(ns.mxLookup(to.split("%40")[1]), 25);
			out = new BufferedWriter(new OutputStreamWriter(
					mailSocket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(mailSocket.getInputStream(), "ISO-8859-1"));
		} catch (IOException e) {
			System.out.println("Error connecting to SMTP-server.");
		}

		send(in, out, "HELO someone.somebody.se", true);
		send(in, out, "MAIL FROM: <" + from + ">", true);
		send(in, out, "RCPT TO: <" + to + ">", true);
		send(in, out, "DATA", true);
		send(in, out, "Subject: " + subject, false);
		send(in, out, "From: <" + from + ">", false);
		send(in, out, "\n", false);
		send(in, out, message, false);
		send(in, out, "\n.\n", false);
		send(in, out, "QUIT", true);
		try {
			mailSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
                
                return "OK";
	}

	public void send(BufferedReader in, BufferedWriter out, String cmd, boolean blocking) 
	{
		try 
		{
			if (cmd.contains("%E5"))
			{
				String test = convertToSwedish(cmd);
				System.out.println(test);
				out.write(test + "\n");
			}
			else
			{
				out.write(URLDecoder.decode(cmd + "\n", "UTF-8"));				
			}
			out.flush();
			System.out.println(cmd);
			if (blocking)
			{
				String response = in.readLine();
				System.out.println(response);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public String convertToSwedish(String input)
	{
		/*if (input.contains("%E5"))
		{
			input = input.replace("%E5","å");			
		}
		if (input.contains("%E4"))
		{
			input.replace("%E4","ä");			
		}
		if (input.contains("%E5"))
		{
			input.replace("%F6","ö");			
		}
		
		input.replace("%C5","Å");
		input.replace("%C4","Ä");
		input.replace("%D6","Ö");*/
		
		return input = input.replace("%E5", "å");
	}

}
