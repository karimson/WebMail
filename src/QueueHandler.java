
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author apple
 */

public class QueueHandler extends Thread
{
    ArrayList<Mail> waitingMails = new ArrayList();
    Calendar clock;
    
    public void run()
    {
        while(true)
        {
            for(int i = 0; waitingMails.size()<i; i++)
            {
                clock = clock.getInstance();
                if(waitingMails.get(i).sendTime.before(clock))
                {
                    try {
                        waitingMails.get(i).sendMail();
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
    
    
    
}
