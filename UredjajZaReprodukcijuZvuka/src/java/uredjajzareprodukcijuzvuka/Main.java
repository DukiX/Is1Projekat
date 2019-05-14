/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uredjajzareprodukcijuzvuka;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author Dusan
 */
public class Main {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory cf;
    
    @Resource(lookup = "RepZvuka")
    private static Queue q;
    /**
     * @param args the command line arguments
     */ 
    public static void main(String[] args) {
        JMSContext context = cf.createContext();
        JMSConsumer consumer = context.createConsumer(q);   

        while(true){
            Message m = consumer.receive();
            if(m instanceof TextMessage){
                try {
                    TextMessage tm = (TextMessage) m;
                    String vrstaPoruke = tm.getStringProperty("Vrsta");
                    
                    if(vrstaPoruke.equals("PustiPesmu")){
                        String imePesme = tm.getText();
                        System.out.println("Pustam pesmu: "+imePesme);
                        pustiPesmu(imePesme);
                    }else if(vrstaPoruke.equals("PrikaziPrethodne")){
                        //do baze
                        System.out.println("Prikazujem");
                    }else{
                        System.out.println("Nepoznata komanda!");
                    }
                } catch (JMSException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }
    
    public static void pustiPesmu(String imePesme){
        String encoding = "UTF-8";
        try {
            String searchText = "youtube "+imePesme;
            Document google = Jsoup.connect("http://google.com/search?q=" + URLEncoder.encode(searchText, encoding)).userAgent("Mozilla/5.0").get();

            Elements webSitesLinks = google.getElementsByTag("cite");

            //Check if any results found
            if (webSitesLinks.isEmpty()) {
                System.out.println("No results found");
                return;
            }

            //webSitesLinks.forEach( link -> System.out.println(link.text()));

            java.awt.Desktop.getDesktop().browse(java.net.URI.create(webSitesLinks.get(0).text()));

            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
}
