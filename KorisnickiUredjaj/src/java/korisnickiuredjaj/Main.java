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
                System.out.println("Tip poruke:\n"
                        + "1: Pusti pesmu\n"
                        + "2: Izlistaj prethodne pesme\n"
                        + "0: Kraj rada");
                String sctip = sc.nextLine();
                int tip = Integer.parseInt(sctip);
                String tipStr="";
                String pesma="";
                if(tip==0){
                    break;
                }
                if(tip==1){
                    System.out.println("Unesi ime pesme:");
                    pesma = sc.nextLine();
                    tipStr = "PustiPesmu";
                }else if(tip==2){
                    pesma = "";
                    tipStr = "PrikaziPrethodne";
                }else {
                    System.out.println("Nepoznat tip");
                    continue;
                }
                TextMessage message = context.createTextMessage(pesma);
                message.setStringProperty("Vrsta", tipStr);
                
                producer.send(q, message);
                
                if(tip==1){
                    System.out.println("Poslat je zahtev za pustanje pesme: "+pesma);
                }else if(tip==2){
                    System.out.println("Poslat je zahtev za prikazivanje prethodnih pesama");
                }
                
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NumberFormatException e){
                System.out.println("Morate uneti broj!");
            }
        }
        sc.close();
    }
    
}
