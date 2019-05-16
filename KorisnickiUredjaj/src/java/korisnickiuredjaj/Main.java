/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korisnickiuredjaj;

import entiteti.PustenePesme;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;

/**
 *
 * @author Dusan
 */
public class Main {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(lookup = "RepZvukaT")
    private static Topic topic;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();

        JMSConsumer consumer = context.createConsumer(topic);

        consumer.setMessageListener((message) -> {
            if (message instanceof ObjectMessage) {
                try {
                    ObjectMessage om = (ObjectMessage) message;
                    LinkedList<PustenePesme> lista = (LinkedList<PustenePesme>) om.getObject();
                    System.out.println("Lista do sada pustenih pesama:");
                    lista.forEach((p) -> {
                        System.out.println(p.getNazivPesme());
                    });
                } catch (JMSException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        try (Scanner sc = new Scanner(System.in)) {
            wh1:
            while (true) {
                System.out.println("Izaberite uredjaj:\n"
                        + "1: Uredjaj za reprodukciju zvuka\n"
                        + "0: Kraj rada");
                String izbor = sc.nextLine();
                int izborInt = Integer.parseInt(izbor);
                switch (izborInt) {
                    case 1:
                        wh2:
                        while (true) {
                            try {
                                System.out.println("Tip poruke:\n"
                                        + "1: Pusti pesmu\n"
                                        + "2: Izlistaj prethodne pesme\n"
                                        + "0: Nazad");
                                String sctip = sc.nextLine();
                                int tip = Integer.parseInt(sctip);
                                String tipStr = "";
                                String pesma = "";
                                switch (tip) {
                                    case 0:
                                        break wh2;
                                    case 1:
                                        System.out.println("Unesi ime pesme:");
                                        pesma = sc.nextLine();
                                        tipStr = "PustiPesmu";
                                        break;
                                    case 2:
                                        pesma = "";
                                        tipStr = "PrikaziPrethodne";
                                        break;
                                    default:
                                        System.out.println("Nepoznat tip");
                                        continue;
                                }
                                TextMessage message = context.createTextMessage(pesma);
                                message.setStringProperty("Vrsta", tipStr);
                                if (tip == 1) {
                                    producer.send(topic, message);
                                    System.out.println("Poslat je zahtev za pustanje pesme: " + message.getText());
                                } else if (tip == 2) {
                                    producer.send(topic, message);
                                    System.out.println("Poslat je zahtev za prikazivanje prethodnih pesama");
                                }
                            } catch (JMSException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (NumberFormatException e) {
                                System.out.println("Morate uneti broj!");
                            }
                        }
                        break;
                    case 0:
                        break wh1;
                    default:
                        System.out.println("Nepoznat izbor");
                }
            }
        }
    }

}
