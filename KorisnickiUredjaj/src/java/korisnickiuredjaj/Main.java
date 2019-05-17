/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korisnickiuredjaj;

import entiteti.PustenePesme;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javax.jms.Message;
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
    private static Topic topicRz;

    @Resource(lookup = "AlarmT")
    private static Topic topicA;

    private static LinkedList<String> listaPrethVremena = new LinkedList<String>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        JMSContext contextRz = connectionFactory.createContext();
        JMSProducer producerRz = contextRz.createProducer();

        JMSConsumer consumerRz = contextRz.createConsumer(topicRz, "id = " + 2);

        /*consumerRz.setMessageListener((message) -> {
            if (message instanceof ObjectMessage) {
                try {
                    ObjectMessage om = (ObjectMessage) message;
                    LinkedList<PustenePesme> lista = (LinkedList<PustenePesme>) om.getObject();
                    System.out.println("Lista do sada pustenih pesama:");
                    lista.forEach((p) -> {
                        System.out.println(p.getNazivPesme());
                    });
                    System.out.println("Izaberi opciju:\n"
                            + "1: Pusti pesmu\n"
                            + "2: Izlistaj prethodne pesme\n"
                            + "0: Nazad");
                } catch (JMSException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });*/
        JMSContext contextA = connectionFactory.createContext();
        JMSProducer producerA = contextA.createProducer();

        JMSConsumer consumerA = contextA.createConsumer(topicA, "id = " + 2);

        /*consumerA.setMessageListener((message) -> {
            
        });*/
        try (Scanner sc = new Scanner(System.in)) {
            wh1:
            while (true) {
                System.out.println("Izaberite uredjaj:\n"
                        + "1: Uredjaj za reprodukciju zvuka\n"
                        + "2: Alarm\n"
                        + "0: Kraj rada");
                String izbor = sc.nextLine();
                int izborInt = Integer.parseInt(izbor);
                switch (izborInt) {
                    case 1:
                        whRz:
                        while (true) {
                            try {
                                System.out.println("Izaberi opciju:\n"
                                        + "1: Pusti pesmu\n"
                                        + "2: Izlistaj prethodne pesme\n"
                                        + "0: Nazad");
                                String sctip = sc.nextLine();
                                int tip = Integer.parseInt(sctip);
                                String tipStr = "";
                                String pesma = "";
                                switch (tip) {
                                    case 0:
                                        break whRz;
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
                                TextMessage message = contextRz.createTextMessage(pesma);
                                message.setStringProperty("Vrsta", tipStr);
                                message.setIntProperty("id", 1);
                                if (tip == 1) {
                                    producerRz.send(topicRz, message);
                                    System.out.println("Poslat je zahtev za pustanje pesme: " + message.getText());
                                } else if (tip == 2) {
                                    producerRz.send(topicRz, message);
                                    System.out.println("Poslat je zahtev za prikazivanje prethodnih pesama.");
                                }
                                Message m = consumerRz.receive();
                                if (m instanceof ObjectMessage) {
                                    try {
                                        ObjectMessage om = (ObjectMessage) m;
                                        LinkedList<PustenePesme> lista = (LinkedList<PustenePesme>) om.getObject();
                                        System.out.println("Lista do sada pustenih pesama:");
                                        lista.forEach((p) -> {
                                            System.out.println(p.getNazivPesme());
                                        });
                                    } catch (JMSException ex) {
                                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                } else if (m instanceof TextMessage) {
                                    try {
                                        TextMessage tmes = (TextMessage) m;
                                        String primljeno = tmes.getText();
                                        System.out.println(primljeno);
                                    } catch (JMSException ex) {
                                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            } catch (JMSException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (NumberFormatException e) {
                                System.out.println("Morate uneti broj!");
                            }
                        }
                        break;
                    case 2:
                        whA:
                        while (true) {
                            try {
                                System.out.println("Izaberi opciju:\n"
                                        + "1: Navij alarm u zeljeno vreme i datum\n"
                                        + "2: Navij periodican alarm u zeljeno vreme"
                                        + "3: Navij alarm u ponudjeno vreme\n"
                                        + "4: Postavi zeljeno zvono alarma\n"
                                        + "0: Nazad");
                                String sctip = sc.nextLine();
                                int tip = Integer.parseInt(sctip);
                                String tipStr = "";
                                String poruka = "";
                                switch (tip) {
                                    case 0:
                                        break whA;
                                    case 1:
                                        System.out.println("Unesi zeljeno vreme: dd/MM/yyyy HH:mm");
                                        poruka = sc.nextLine();
                                        try {
                                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                            format.parse(poruka);
                                        } catch (ParseException e) {
                                            System.out.println("Pogresan format datuma");
                                            break whA;
                                        }
                                        tipStr = "NavijAlarm";
                                        break;
                                    case 2:
                                        System.out.println("Unesi zeljeno vreme: HH:mm");
                                        poruka = sc.nextLine();
                                        poruka = "10/10/2010" + poruka;
                                        try {
                                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                            format.parse(poruka);
                                        } catch (ParseException e) {
                                            System.out.println("Pogresan format datuma");
                                            break whA;
                                        }
                                        tipStr = "NavijAlarmPeriodican";
                                        break;
                                    case 3:
                                        poruka = "";
                                        tipStr = "DohvatiVremena";
                                        TextMessage tm = contextA.createTextMessage(poruka);
                                        tm.setStringProperty("Vrsta", tipStr);
                                        tm.setIntProperty("id", 1);
                                        producerA.send(topicA, tm);
                                        System.out.println("Poslat je zahtev za dohvatanje vremena.");

                                        LinkedList<String> lista = null;

                                        Message message = consumerA.receive();
                                        if (message instanceof ObjectMessage) {
                                            try {
                                                ObjectMessage om = (ObjectMessage) message;
                                                lista = (LinkedList<String>) om.getObject();
                                                System.out.println("Izaberi broj pored zeljenog alarma:");
                                                int i = 0;
                                                for (String s : lista) {
                                                    System.out.println((i++) + ". " + s);
                                                }

                                            } catch (JMSException ex) {
                                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        } else if (message instanceof TextMessage) {
                                            try {
                                                TextMessage tmes = (TextMessage) message;
                                                String primljeno = tmes.getText();
                                                System.out.println(primljeno);
                                            } catch (JMSException ex) {
                                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }

                                        if (lista != null) {
                                            String brAlStr = sc.nextLine();
                                            int brAl = Integer.parseInt(brAlStr);
                                            if (brAl >= 0 && brAl < lista.size()) {
                                                tipStr = "NavijAlarm";
                                                poruka = lista.get(brAl);
                                            } else {
                                                System.out.println("Nepostojeci broj!");
                                                break whA;
                                            }
                                        } else {
                                            break whA;
                                        }

                                        break;
                                    case 4:
                                        System.out.println("Unesi ime pesme:");
                                        poruka = sc.nextLine();
                                        tipStr = "PostaviZvono";
                                        break;
                                    default:
                                        System.out.println("Nepoznat tip");
                                        continue;
                                }
                                TextMessage message = contextA.createTextMessage(poruka);
                                message.setStringProperty("Vrsta", tipStr);
                                message.setIntProperty("id", 1);
                                if (tip == 1 || tip == 2 || tip == 3) {
                                    if (tip == 2) {
                                        message.setBooleanProperty("periodican", true);
                                    } else {
                                        message.setBooleanProperty("periodican", false);
                                    }
                                    producerA.send(topicA, message);
                                    System.out.println("Poslat je zahtev za navijanje alarma: " + message.getText());
                                } else if (tip == 4) {
                                    producerA.send(topicA, message);
                                    System.out.println("Poslat je zahtev za postavljanje novog zvona alarma");
                                }
                                Message m = consumerA.receive();
                                if (m instanceof TextMessage) {
                                    try {
                                        TextMessage tmes = (TextMessage) m;
                                        String primljeno = tmes.getText();
                                        System.out.println(primljeno);
                                    } catch (JMSException ex) {
                                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            } catch (JMSException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
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
