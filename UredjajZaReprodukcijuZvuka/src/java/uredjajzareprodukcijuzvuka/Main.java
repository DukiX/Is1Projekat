/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uredjajzareprodukcijuzvuka;

import entiteti.PustenePesme;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
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

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("UredjajZaReprodukcijuZvukaPU");
        EntityManager em = emf.createEntityManager();
        
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
                        
                        em.getTransaction().begin();
                        
                        PustenePesme p = new PustenePesme(imePesme);
                        
                        em.persist(p);
                        
                        em.getTransaction().commit();
                    }else if(vrstaPoruke.equals("PrikaziPrethodne")){
                        
                        CriteriaBuilder cb = em.getCriteriaBuilder();
                        
                        CriteriaQuery<PustenePesme> q = cb.createQuery(PustenePesme.class);
                        Root<PustenePesme> c = q.from(PustenePesme.class);
                        
                        q.select(c).distinct(true);
                        
                        TypedQuery<PustenePesme> tq = em.createQuery(q);
                        List<PustenePesme> lista = tq.getResultList();
                        
                        System.out.println("Lista dosada pustenih pesama:");
                        for(PustenePesme p:lista){
                            System.out.println(p.getNazivPesme());
                        }
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
