import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Calendar;

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
    int delay;
    Calendar sendTime;
    Calendar submitTime;
    String status;

	public void parseMailData(String data) 
	{
            from = data.substring(data.indexOf("FROM=")+5, data.indexOf("&", data.indexOf("FROM=")));
            to = data.substring(data.indexOf("TO=") + 3, data.indexOf("&", data.indexOf("TO=")));
            String tempDelay = data.substring(data.indexOf("DELAY=")+6, data.indexOf("&", data.indexOf("DELAY=")));
            subject = data.substring(data.indexOf("SUBJECT=")+8, data.indexOf("&", data.indexOf("SUBJECT=")));
            message = data.substring(data.indexOf("MESSAGE=")+8, data.indexOf("&SENDBUTTON=Submit"));
            sendTime = sendTime.getInstance();
            submitTime = submitTime.getInstance();
            status = "QUEUED";
	            
            if(tempDelay != null && !tempDelay.equals(null) && !tempDelay.equals("") && !tempDelay.equals(" "))
            {
                delay = Integer.parseInt(tempDelay);
                sendTime.add(Calendar.SECOND,delay);
            }         
	}

	public void sendMail() throws UnsupportedEncodingException {
		try 
                {
                        NsLookup ns = new NsLookup();
                        String mxServer = ns.mxLookup(to.split("%40")[1]);
                        
                        if(mxServer == "")
                        {
                            status = "SERVER NOT FOUND";
                            out.write("SMTP server could not be found."); 
                       }
			mailSocket = new Socket(ns.mxLookup(to.split("%40")[1]), 25);
			out = new BufferedWriter(new OutputStreamWriter(
					mailSocket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(mailSocket.getInputStream()));
                } 
                catch (IOException e) 
                {
			System.out.println("Error connecting to SMTP-server.");
                        status = "COULD NOT CONNECT TO SERVER";
		}
		send(in, out, "HELO someone.somebody.se", true);
		send(in, out, "MAIL FROM: <" + URLDecoder.decode(from, "UTF-8") + ">", true);
		send(in, out, "RCPT TO: <" + URLDecoder.decode(to, "UTF-8") + ">", true);
		send(in, out, "DATA", true);
		send(in, out, "Subject: " + encodeSubject(subject), false);
		send(in, out, "From: <" + URLDecoder.decode(from, "UTF-8") + ">", false);
		send(in, out, "MIME-Version: 1.0", false);
                send(in, out, "Content-Type: multipart/mixed; boundary=\"=_\"", false);
                send(in, out, "--=_", false);
                send(in, out, "Content-Type: text/plain; charset=iso-8859-1", false);
                send(in, out, "Content-Transfer-Encoding: quoted-printable", false);
                send(in, out, "\n", false);
                send(in, out, encodeMessage(URLDecoder.decode(message, "ISO-8859-1")), false);
                send(in, out, "\n--=_--", false);
                send(in, out, "\n.\n", false);
		send(in, out, "QUIT", true);
		
		try 
                {
			mailSocket.close();
			out.close();
			in.close();
		} 
                catch (IOException e) 
                {
			e.printStackTrace();
		}
                
                status = "SENT";
	}

	public void send(BufferedReader in, BufferedWriter out, String cmd, boolean blocking) 
	{
		try 
		{
			out.write(cmd + "\n");				
			out.flush();
			if (blocking)
			{
				String response = in.readLine();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	public String encodeMessage(String message)
	{
		String result = "";
		String[] splitMessages;
		if (!message.equals(""))
		{		
			message = message.replace("%28", "=28"); //(
			message = message.replace("%29", "=29"); //)
			message = message.replace("%3F", "=3F"); //?	
			message = message.replace("=", "=3D"); //=
			
			splitMessages = message.split("\n");
			if (splitMessages.length > 1)
			{
				for (int i=0; i<splitMessages.length; i++)
				{
					result += splitMessages[i].substring(0,splitMessages[i].length()).concat("=0A");
				}
				
				return result;
			}
			else
			{	
				return message;
			}	
		}
		
		return "Auto-fill: No Message";
	}
	public String encodeSubject(String subject)
	{
		if (!subject.equals(""))
		{
			subject = subject.replace("+", " ");
			if (subject.contains("%"))
			{
				subject = subject.replace("%E5", "=?ISO-8859-1?Q?=E5?="); //�
				subject = subject.replace("%E4", "=?ISO-8859-1?Q?=E4?="); //�
				subject = subject.replace("%F6", "=?ISO-8859-1?Q?=F6?="); //�
				subject = subject.replace("%C5", "=?ISO-8859-1?Q?=C5?="); //�
				subject = subject.replace("%C4", "=?ISO-8859-1?Q?=C4?="); //�
				subject = subject.replace("%D6", "=?ISO-8859-1?Q?=D6?="); //�
				subject = subject.replace("%28", "=?ISO-8859-1?Q?=28?="); //(
				subject = subject.replace("%29", "=?ISO-8859-1?Q?=29?="); //)
				subject = subject.replace("%3F", "=?ISO-8859-1?Q?=3F?="); //?	
			}
			return (subject);
		}
		
		return "Auto-fill: No Subject";

		
		
	}

}
