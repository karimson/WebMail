
import java.io.UnsupportedEncodingException;
import java.net.Socket;
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
                        System.out.println("Failed to send mail");
                    }
                }
            }
        }
    }
    
    public void addMail(Mail newMail)
    {
        waitingMails.add(newMail);
    }
    
    public String getStatusPage()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        
        result = "--- STATUS ---\n"
                + "SENDTIME\tFROM\t\tTO\t\tSTATUS\n";
        
        for(int i = 0; sentMails.size()>i; i++)
            result += dateFormat.format(sentMails.get(i).sendTime.getTime()) + "\t\t" + sentMails.get(i).from + "\t\t" + sentMails.get(i).to + "\t\t" + "SENT\n";
        for(int i = 0; waitingMails.size()>i; i++)
            result += dateFormat.format(waitingMails.get(i).sendTime.getTime()) + "\t\t" + waitingMails.get(i).from + "\t\t" + waitingMails.get(i).to + "\t\t" + "QUEUED\n";
        
        return result;
    }
}
