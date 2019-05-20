/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Dusan
 */
public class Main {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;
    
    @Resource(lookup = "PlanerT")
    private static Topic topicP;
    
    @Resource(lookup = "AlarmT")
    private static Topic topicA;
    
    public static void main(String[] args) {
        JMSContext contextP = connectionFactory.createContext();
        JMSProducer producerP = contextP.createProducer();

        JMSConsumer consumerP = contextP.createConsumer(topicP, "id = " + 1);
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlanerPU");
        EntityManager em = emf.createEntityManager();
    }
    
    
    
    
    
    
    
    public static long izracunajVreme(String source,String destination){
        try {
            Document time = Jsoup.connect("http://www.travelmath.com/driving-time/from/" + source + "/to/" + destination).userAgent("Mozilla/5.0").get();
            
            Elements elems2 = time.getAllElements();

            if (elems2.isEmpty()) {
                System.out.println("No results found");
                return 0;
            }
            
            String vreme = "";
            int minuta = 0;

            Pattern pattern = Pattern.compile("\\d+ hours?, \\d+ minutes?");
            Pattern pattern2 = Pattern.compile("\\d+ minutes?");
            for (Element e : elems2) {

                Matcher matcher = pattern.matcher(e.text());
                if (matcher.find()) {
                    vreme = matcher.group(0);
                    Pattern p1 = Pattern.compile("\\d+ hour");
                    Matcher m1 = p1.matcher(vreme);
                    String s1="";
                    if (m1.find()) {
                        s1 = m1.group(0);
                    }
                    Pattern p2 = Pattern.compile("\\d+ minute");
                    Matcher m2 = p2.matcher(vreme);
                    String s2="";
                    if (m2.find()) {
                        s2 = m2.group(0);
                    }
                    String s1sub = s1.substring(0, s1.length() - 5);
                    String s2sub = s2.substring(0, s2.length() - 7);
                    minuta = Integer.parseInt(s1sub) * 60 + Integer.parseInt(s2sub);
                    break;
                }
                Matcher matcher2 = pattern2.matcher(e.text());
                if (matcher2.find()) {
                    vreme = matcher2.group(0);
                    String sub;
                    sub = vreme.substring(0, vreme.length() - 8);
                    minuta = Integer.parseInt(sub);
                    break;
                }
            }
            System.out.println(vreme);
            System.out.println(minuta);
            
            long vr = minuta*60*1000;
            return vr;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}
