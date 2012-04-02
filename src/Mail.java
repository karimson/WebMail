import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
        String tempDelay;
        int delay;
        Calendar sendTime;

	public void parseMailData(String data) 
	{
		from = data.substring(data.indexOf("FROM=")+5, data.indexOf("&", data.indexOf("FROM=")));
		to = data.substring(data.indexOf("TO=") + 3, data.indexOf("&", data.indexOf("TO=")));
		tempDelay = data.substring(data.indexOf("DELAY=")+6, data.indexOf("&", data.indexOf("DELAY=")));
                subject = data.substring(data.indexOf("SUBJECT=")+8, data.indexOf("&", data.indexOf("SUBJECT=")));
		message = data.substring(data.indexOf("MESSAGE=")+8, data.indexOf("&SENDBUTTON=Send"));
                sendTime = sendTime.getInstance();
                
                if(tempDelay != null && !tempDelay.equals(null) && !tempDelay.equals("") && !tempDelay.equals(" "))
                {
                    delay = Integer.parseInt(tempDelay);
                    sendTime.add(Calendar.SECOND,delay);
                }
                
                 
	}

	public String sendMail() throws UnsupportedEncodingException {
		try {
                        NsLookup ns = new NsLookup();
                        String mxServer = ns.mxLookup(to.split("%40")[1]);
                        
                        if(mxServer == "")
                            return "Server Not Found";
                        
			mailSocket = new Socket(ns.mxLookup(to.split("%40")[1]), 25);
			out = new BufferedWriter(new OutputStreamWriter(
					mailSocket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(mailSocket.getInputStream(), "ISO-8859-15"));
		} catch (IOException e) {
			System.out.println("Error connecting to SMTP-server.");
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
        message = message.replace("%", "=");
        message = message.replace("+", " ");
    	send(in, out, message, false);
        send(in, out, "\n--=_--", false);
		send(in, out, "\n.\n", false);
		send(in, out, "QUIT", true);
		try {
			mailSocket.close();
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
                
                return "OK";
	}

	public void send(BufferedReader in, BufferedWriter out, String cmd, boolean blocking) 
	{
		try 
		{
			out.write(cmd + "\n");				
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
	
	public String encodeSubject(String subject)
	{
		if (!subject.equals(""))
		{
			subject = subject.replace("+", " ");
			if (subject.contains("%"))
			{
				subject = subject.replace("%", "=?ISO-8859-1?Q?=");
				return (subject + "?=");
			}
		}
		
		return "Auto-fill: No Subject";

		
		
	}

}
