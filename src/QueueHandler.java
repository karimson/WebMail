
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueueHandler extends Thread
{
    ArrayList<Mail> waitingMails = new ArrayList();
    ArrayList<Mail> sentMails = new ArrayList();
    Calendar clock;
    String result;
    
    public void run()
    {
       
        while(true)
        {
            for(int i = 0; waitingMails.size()>i; i++)
            {
                clock = clock.getInstance();
                if(waitingMails.get(i).sendTime.before(clock))
                {
                    try {
                        waitingMails.get(i).sendMail();
                        sentMails.add(waitingMails.get(i));
                        waitingMails.remove(i);
                        
                    } catch (UnsupportedEncodingException ex) {
                        sentMails.add(waitingMails.get(i));
                        waitingMails.remove(i);
                    	sentMails.get(i).status = "ERROR SENDING MAIL";
                        System.out.println("Error sending mail");
                    }
                }
            }
        }
    }
    
    public void addMail(Mail newMail)
    {
        waitingMails.add(newMail);
    }
    
    public String getStatusPage() throws UnsupportedEncodingException
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        
        result = "--- STATUS PAGE ---\nSENDTIME\t\tFROM\t\t\tTO\t\t\tSTATUS\n";
        
        for(int i = 0; sentMails.size()>i; i++)
            result += dateFormat.format(sentMails.get(i).sendTime.getTime()) + "\t" + URLDecoder.decode(sentMails.get(i).from, "UTF-8") + "\t" + URLDecoder.decode(sentMails.get(i).to, "UTF-8") + "\t\t" + sentMails.get(i).status + "\n";
        for(int i = 0; waitingMails.size()>i; i++)
            result += dateFormat.format(waitingMails.get(i).sendTime.getTime()) + "\t" + URLDecoder.decode(waitingMails.get(i).from, "UTF-8") + "\t" + URLDecoder.decode(waitingMails.get(i).to, "UTF-8") + "\t\t" + waitingMails.get(i).status +"\n";
        
        return result;
    }
}
