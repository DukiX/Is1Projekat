/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korisnickiuredjaj;

import entiteti.Kalendar;
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

    @Resource(lookup = "PlanerT")
    private static Topic topicP;

    private static LinkedList<String> listaPrethVremena = new LinkedList<String>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        JMSContext contextRz = connectionFactory.createContext();
        JMSProducer producerRz = contextRz.createProducer();

        JMSConsumer consumerRz = contextRz.createConsumer(topicRz, "id = " + 2);

        JMSContext contextA = connectionFactory.createContext();
        JMSProducer producerA = contextA.createProducer();

        JMSConsumer consumerA = contextA.createConsumer(topicA, "id = " + 2);

        JMSContext contextP = connectionFactory.createContext();
        JMSProducer producerP = contextP.createProducer();

        JMSConsumer consumerP = contextP.createConsumer(topicP, "id = " + 2);

        try (Scanner sc = new Scanner(System.in)) {
            wh1:
            while (true) {
                System.out.println("Izaberite uredjaj:\n"
                        + "1: Uredjaj za reprodukciju zvuka\n"
                        + "2: Alarm\n"
                        + "3: Planer\n"
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
                                        + "1: Navij jednokratni alarm u zeljeno vreme\n"
                                        + "2: Navij periodican alarm u zeljeno vreme\n"
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
                                        System.out.println("Unesi zeljeno vreme: HH:mm");
                                        poruka = sc.nextLine();
                                        try {
                                            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                            format.parse(poruka);
                                        } catch (ParseException e) {
                                            System.out.println("Pogresan format vremena");
                                            break whA;
                                        }
                                        tipStr = "NavijAlarm";
                                        break;
                                    case 2:
                                        System.out.println("Unesi zeljeno vreme: HH:mm");
                                        poruka = sc.nextLine();
                                        try {
                                            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                            format.parse(poruka);
                                        } catch (ParseException e) {
                                            System.out.println("Pogresan format vremena");
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
                                                if (!lista.isEmpty()) {
                                                    System.out.println("Izaberi broj pored zeljenog alarma:");
                                                }else{
                                                    System.out.println("Nema neaktivnih alarma:");
                                                }
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
                                            int brAl = 0;
                                            try {
                                                brAl = Integer.parseInt(brAlStr);
                                            } catch (NumberFormatException e) {
                                                break whA;
                                            }
                                            if (brAl >= 0 && brAl < lista.size()) {
                                                System.out.println("Da li zelite da bude periodican? d za da/ostalo za ne");
                                                String per = sc.nextLine();
                                                if (per.equals("d")) {
                                                    tipStr = "NavijAlarmPeriodican";
                                                } else {
                                                    tipStr = "NavijAlarm";
                                                }
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
                    case 3:
                        whP:
                        while (true) {
                            try {
                                System.out.println("Izaberi opciju:\n"
                                        + "1: Izlistaj obaveze\n"
                                        + "2: Dodaj obavezu\n"
                                        + "3: Izmeni obavezu\n"
                                        + "4: Obrisi obavezu\n"
                                        + "5: Kalkulator razdaljine\n"
                                        + "0: Nazad");
                                String sctip = sc.nextLine();
                                int tip = Integer.parseInt(sctip);

                                String poruka = "";
                                String tipPoruke = "";
                                String datumProperty = "";
                                String vremeProperty = "";
                                String destinacijaProperty = "";
                                boolean podsetnikProperty = false;
                                int izmeniProperty = 0;

                                switch (tip) {
                                    case 0:
                                        break whP;
                                    case 1:
                                        tipPoruke = "izlistaj";
                                        poruka = "";
                                        break;
                                    case 2:
                                        tipPoruke = "dodaj";
                                        System.out.println("Unesite opis obaveze:");
                                        poruka = sc.nextLine();
                                        System.out.println("Unesite datum obaveze u formatu yyyy/MM/dd");
                                        String datum = sc.nextLine();
                                        try {
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                                            format.parse(datum);
                                        } catch (ParseException e) {
                                            System.out.println("Pogresan format datuma");
                                            break whP;
                                        }
                                        datumProperty = datum;

                                        System.out.println("Unesite vreme obaveze u formatu HH:mm");
                                        String vreme = sc.nextLine();

                                        try {
                                            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                            format.parse(vreme);
                                        } catch (ParseException e) {
                                            System.out.println("Pogresan format vremena");
                                            break whP;
                                        }
                                        vremeProperty = vreme;
                                        System.out.println("Da li zelite da unesete destinaciju obaveze? d za da/ostalo za ne");
                                        String d = sc.nextLine();
                                        if (d.equals("d")) {
                                            System.out.println("Unesite destinaciju:");
                                            destinacijaProperty = sc.nextLine();
                                        }
                                        System.out.println("Da li zelite da se postavi podsetnik? d za da/ostalo za ne");
                                        d = sc.nextLine();
                                        if (d.equals("d")) {
                                            podsetnikProperty = true;
                                        }
                                        break;
                                    case 3:
                                        tipPoruke = "izlistaj";
                                        poruka = "";
                                        TextMessage message = contextP.createTextMessage(poruka);
                                        message.setStringProperty("Vrsta", tipPoruke);

                                        message.setIntProperty("id", 1);

                                        producerP.send(topicP, message);
                                        System.out.println("Poslat je zahtev za izlistavanje obaveza");

                                        LinkedList<String> lista = null;

                                        Message m = consumerP.receive();
                                        if (m instanceof ObjectMessage) {
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
                                            int brAl = 0;
                                            try {
                                                brAl = Integer.parseInt(brAlStr);
                                            } catch (NumberFormatException e) {
                                                break whP;
                                            }
                                            if (brAl >= 0 && brAl < lista.size()) {
                                                izmeniProperty = 0;//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                            } else {
                                                System.out.println("Nepostojeci broj!");
                                                break whP;
                                            }
                                        } else {
                                            break whP;
                                        }

                                        tipPoruke = "izmeni";
                                        System.out.println("Da li zelite da izmenite opis obaveze? d za da/ostalo za ne");
                                        String dd = sc.nextLine();
                                        if (dd.equals("d")) {
                                            System.out.println("Unesite novi opis:");
                                            poruka = sc.nextLine();
                                        }
                                        System.out.println("Da li zelite da promenite datum obaveze? d za da/ostalo za ne");
                                        dd = sc.nextLine();
                                        String datumm = "";
                                        if (dd.equals("d")) {
                                            System.out.println("Unesite datum obaveze u formatu yyyy-MM-dd");
                                            datumm = sc.nextLine();
                                            try {
                                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                                format.parse(datumm);
                                            } catch (ParseException e) {
                                                System.out.println("Pogresan format datuma");
                                                break whP;
                                            }
                                            datumProperty = datumm;
                                        }

                                        System.out.println("Da li zelite da promenite vreme obaveze? d za da/ostalo za ne");
                                        dd = sc.nextLine();
                                        String vremee = "";
                                        if (dd.equals("d")) {
                                            System.out.println("Unesite datum obaveze u formatu HH:mm");
                                            vremee = sc.nextLine();
                                            try {
                                                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                                format.parse(vremee);
                                            } catch (ParseException e) {
                                                System.out.println("Pogresan format vremena");
                                                break whP;
                                            }
                                            vremeProperty = vremee;
                                        }

                                        System.out.println("Da li zelite da izmenite destinaciju obaveze? d za da/ostalo za ne");
                                        dd = sc.nextLine();
                                        if (dd.equals("d")) {
                                            System.out.println("Unesite destinaciju:");
                                            destinacijaProperty = sc.nextLine();
                                        }
                                        System.out.println("Da li zelite da se postavi podsetnik? d za da/ostalo za ne");
                                        d = sc.nextLine();
                                        if (d.equals("d")) {
                                            podsetnikProperty = true;
                                        }
                                        break;
                                    case 4:
                                        break;
                                    case 5:
                                        break;
                                }

                                TextMessage message = contextP.createTextMessage(poruka);
                                message.setStringProperty("Vrsta", tipPoruke);
                                message.setStringProperty("Datum", datumProperty);
                                message.setStringProperty("Vreme", vremeProperty);
                                message.setStringProperty("Destinacija", destinacijaProperty);
                                message.setBooleanProperty("Podsetnik", podsetnikProperty);
                                message.setIntProperty("IdIzmeni", izmeniProperty);

                                message.setIntProperty("id", 1);

                                producerP.send(topicP, message);

                                switch (tip) {
                                    case 1:
                                        System.out.println("Poslat je zahtev za izlistavanje obaveza");
                                        break;
                                    case 2:
                                        System.out.println("Poslat je zahtev za dodavanje obaveze: " + poruka);
                                        break;
                                    case 3:
                                        System.out.println("Poslat je zahtev za izmenu obaveze: " + poruka);
                                        break;
                                    case 4:
                                        System.out.println("Poslat je zahtev za brisanje obaveze: " + poruka);
                                        break;
                                }

                                Message m = consumerP.receive();
                                if (m instanceof ObjectMessage) {
                                    try {
                                        ObjectMessage om = (ObjectMessage) m;
                                        LinkedList<Kalendar> lista = (LinkedList<Kalendar>) om.getObject();
                                        System.out.println("Lista do sada pustenih pesama:");
                                        //System.out.println("Datum\tVreme\tOpis\tDestinacija");
                                        System.out.printf("%-15s %-15s %-40s %-15s", "Datum", "Vreme", "Opis", "Destinacija");
                                        System.out.println();
                                        lista.forEach((k) -> {
                                            //System.out.println(k.getDatum()+"\t"+k.getVreme()+"\t"+k.getOpis()+"\t"+k.getDestinacija());
                                            System.out.format("%-15s %-15s %-40s %-15s", k.getDatum(), k.getVreme(), k.getOpis(), k.getDestinacija());
                                            System.out.println();
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
