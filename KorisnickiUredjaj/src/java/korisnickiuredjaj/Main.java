/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korisnickiuredjaj;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;

/**
 *
 * @author Dusan
 */
public class Main {
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;
    
    @Resource(lookup = "RepZvuka")
    private static Queue q;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        Scanner sc = new Scanner(System.in); 
        while(true){
            try {
                System.out.println("Tip poruke ili kraj");
                String tip = sc.nextLine();
                String pesma="";
                if(tip.equals("kraj")){
                    break;
                }
                if(tip.equals("PustiPesmu")){
                    pesma = sc.nextLine();
                }else if(tip.equals("PrikaziPrethodne")){
                    pesma = "";
                }else {
                    System.out.println("Nepoznat tip");
                    continue;
                }
                TextMessage message = context.createTextMessage(pesma);
                message.setStringProperty("Vrsta", tip);
                
                producer.send(q, message);
                
                System.out.println("Poslat je zahtev: " + message.getText() + ", tip vesti: " + tip);
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        sc.close();
    }
    
}
