
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alen
 */
public class NsLookup 
{
    Properties env = new Properties();
    InitialDirContext idc;
    
    public NsLookup()
    {
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
        try 
        {
            idc = new InitialDirContext(env);
        } 
        catch (NamingException ex) 
        {
            Logger.getLogger(NsLookup.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error in nameserver lookup");
        }
    }
    
    public String mxLookup(String domain)
    {
        String server = "";
        Attributes attrs = null;
        String[] mxAttributes = {"MX"};
        String mxAttr = "";
        
        try 
        {
            attrs = idc.getAttributes(domain, mxAttributes);
        } 
        catch (NamingException ex) 
        {
            Logger.getLogger(NsLookup.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error in nameserver lookup");
        }
        
        Attribute attr = attrs.get("MX");
        
        if (attr != null) 
        {   
                try 
                {
                    mxAttr = (String) attr.get(0);
                } 
                catch (NamingException ex) 
                {
                    Logger.getLogger(NsLookup.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Error in nameserver lookup");
                }
                String[] parts = mxAttr.split(" ");
                // Split off the priority, and take the last field
                server = parts[parts.length - 1];
            
        }
     
    return server;
    }
    
    
}
