/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planer;

import entiteti.Alarmi;
import entiteti.Kalendar;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
    private static Topic topic;

    @Resource(lookup = "AlarmT")
    private static Topic topicA;

    public static void main(String[] args) {
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();

        JMSConsumer consumer = context.createConsumer(topic, "id = " + 1);

        JMSConsumer consumerA = context.createConsumer(topicA, "id = " + 3);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlanerPU");
        EntityManager em = emf.createEntityManager();
        wh:
        while (true) {
            Message m = consumer.receive();
            if (m instanceof TextMessage) {
                try {
                    TextMessage tm = (TextMessage) m;
                    String vrstaPoruke = tm.getStringProperty("Vrsta");
                    String opis = "";
                    String datum = "";
                    String vreme = "";
                    String destinacija = "";
                    boolean podsetnik = false;
                    long idZaIzmenu = 0;
                    switch (vrstaPoruke) {
                        case "izlistaj":

                            Date danas = new Date();
                            java.sql.Date dns = new java.sql.Date(danas.getTime());
                            java.sql.Time sad = new java.sql.Time(danas.getTime());

                            String queryStr = "SELECT k FROM Kalendar k WHERE "
                                    + "(k.datum = :danas and k.vreme > :sad) or (k.datum>:danas) "
                                    + "ORDER BY k.datum, k.vreme ASC";
                            TypedQuery<Kalendar> query = em.createQuery(queryStr, Kalendar.class);
                            query.setParameter("danas", dns);
                            query.setParameter("sad", sad);

                            List<Kalendar> lista = query.getResultList();
                            LinkedList<Kalendar> lst = new LinkedList<>();

                            for (Kalendar k : lista) {
                                lst.add(k);
                            }

                            ObjectMessage om = context.createObjectMessage(lst);

                            om.setIntProperty("id", 2);
                            producer.send(topic, om);
                            System.out.println("Poslata lista obaveza");

                            break;
                        case "dodaj":
                            opis = tm.getText();
                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
                            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");

                            datum = tm.getStringProperty("Datum");
                            vreme = tm.getStringProperty("Vreme");
                            Date datumD = new Date();
                            Date vremeD = new Date();
                            try {
                                datumD = format1.parse(datum);
                                vremeD = format2.parse(vreme);
                            } catch (ParseException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            java.sql.Date datesql = new java.sql.Date(datumD.getTime());
                            java.sql.Time timesql = new java.sql.Time(vremeD.getTime());

                            destinacija = tm.getStringProperty("Destinacija");
                            if (destinacija.equals("")) {
                                destinacija = null;
                            }

                            podsetnik = tm.getBooleanProperty("Podsetnik");

                            Alarmi a = null;

                            if (podsetnik) {
                                TextMessage message = context.createTextMessage(vreme);
                                message.setStringProperty("datum", datum);
                                message.setStringProperty("Vrsta", "NavijAlarmPlaner");

                                int pot = 0;

                                if (destinacija != null) {
                                    //POZOVI ODREDJIVANJE RAZDALJINE
                                }

                                message.setIntProperty("potrebnovreme", pot);
                                message.setIntProperty("id", 1);
                                producer.send(topicA, message);

                                Message mes = consumerA.receive();
                                if (mes instanceof ObjectMessage) {
                                    try {
                                        ObjectMessage omes = (ObjectMessage) mes;
                                        Alarmi tmp = (Alarmi) omes.getObject();
                                        System.out.println("idA=" + tmp.getId());

                                        CriteriaBuilder cba = em.getCriteriaBuilder();
                                        CriteriaQuery<Alarmi> qa = cba.createQuery(Alarmi.class);
                                        Root<Alarmi> ca = qa.from(Alarmi.class);
                                        //q.where(cb.equal(c.get("periodican"), true));
                                        qa.where(cba.equal(ca.get("id"), tmp.getId()));
                                        qa.select(ca);

                                        TypedQuery<Alarmi> tqa = em.createQuery(qa);
                                        List<Alarmi> listaa = tqa.getResultList();

                                        a = listaa.get(0);

                                    } catch (JMSException ex) {
                                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }

                            em.getTransaction().begin();

                            Kalendar k = new Kalendar(opis, datesql, timesql, destinacija, podsetnik, a);

                            em.persist(k);

                            em.flush();

                            em.getTransaction().commit();

                            String s1 = "Dodata obaveza";
                            System.out.println(s1);

                            TextMessage tekstpor1 = context.createTextMessage(s1);
                            tekstpor1.setIntProperty("id", 2);
                            producer.send(topic, tekstpor1);
                            break;

                        case "izmeni":
                            idZaIzmenu = tm.getLongProperty("IdIzmeni");

                            Kalendar kal = em.find(Kalendar.class, idZaIzmenu);

                            String da = tm.getStringProperty("Datum");
                            String vr = tm.getStringProperty("Vreme");
                            String de = tm.getStringProperty("Destinacija");
                            boolean b = tm.getBooleanProperty("Podsetnik");

                            SimpleDateFormat for1 = new SimpleDateFormat("yyyy/MM/dd");
                            SimpleDateFormat for2 = new SimpleDateFormat("HH:mm");

                            Date dat = new Date();
                            Date vre = new Date();
                            try {
                                dat = for1.parse(da);
                                vre = for2.parse(vr);
                            } catch (ParseException ex) {
                                //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            java.sql.Date datsql = new java.sql.Date(dat.getTime());
                            java.sql.Time timsql = new java.sql.Time(vre.getTime());

                            em.getTransaction().begin();

                            opis = tm.getText();

                            boolean ispraviAlarm = false;

                            if (opis != null) {
                                kal.setOpis(opis);
                            }
                            if (!da.equals("")) {
                                kal.setDatum(datsql);
                                ispraviAlarm = true;
                            }
                            if (!vr.equals("")) {
                                kal.setVreme(timsql);
                                ispraviAlarm = true;
                            }
                            if (!de.equals("")) {
                                kal.setDestinacija(de);
                            }

                            em.flush();
                            em.getTransaction().commit();

                            Alarmi aa = null;

                            if (kal.getAlarm() == null) {
                                if (b) {

                                    Time t = kal.getVreme();
                                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                    Date dt = new Date(t.getTime());
                                    String formVreme = sdf.format(dt);

                                    java.sql.Date datu = kal.getDatum();
                                    SimpleDateFormat sdfdat = new SimpleDateFormat("yyyy/MM/dd");
                                    dt = new Date(datu.getTime());
                                    String formDatum = sdfdat.format(dt);

                                    TextMessage message = context.createTextMessage(formVreme);
                                    message.setStringProperty("datum", formDatum);
                                    message.setStringProperty("Vrsta", "NavijAlarmPlaner");

                                    int pot = 0;

                                    if (!kal.getDestinacija().equals("")) {
                                        //POZOVI ODREDJIVANJE RAZDALJINE
                                    }

                                    message.setIntProperty("potrebnovreme", pot);

                                    message.setIntProperty("id", 1);
                                    producer.send(topicA, message);

                                    Message mes = consumerA.receive();
                                    if (mes instanceof ObjectMessage) {
                                        try {
                                            ObjectMessage omes = (ObjectMessage) mes;
                                            Alarmi tmp = (Alarmi) omes.getObject();
                                            System.out.println("idA=" + tmp.getId());

                                            CriteriaBuilder cba = em.getCriteriaBuilder();
                                            CriteriaQuery<Alarmi> qa = cba.createQuery(Alarmi.class);
                                            Root<Alarmi> ca = qa.from(Alarmi.class);
                                            //q.where(cb.equal(c.get("periodican"), true));
                                            qa.where(cba.equal(ca.get("id"), tmp.getId()));
                                            qa.select(ca);

                                            TypedQuery<Alarmi> tqa = em.createQuery(qa);
                                            List<Alarmi> listaa = tqa.getResultList();

                                            aa = listaa.get(0);

                                            if (aa == null) {
                                                System.out.println("greska null je");
                                            } else {
                                                System.out.println("dobar je i id je = " + aa.getId());
                                            }

                                            em.getTransaction().begin();
                                            kal.setAlarm(aa);
                                            em.flush();
                                            em.getTransaction().commit();
                                            em.clear();

                                        } catch (JMSException ex) {
                                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }
                            } else {
                                if (ispraviAlarm) {
                                    TextMessage message = context.createTextMessage(vr);
                                    message.setStringProperty("datum", da);
                                    message.setStringProperty("Vrsta", "IspraviAlarm");
                                    message.setLongProperty("idAlZaIz", kal.getAlarm().getId());
                                    message.setStringProperty("destinacija", de);
                                    message.setIntProperty("id", 1);
                                    producer.send(topicA, message);

                                    Message mes = consumerA.receive();
                                    if (mes instanceof TextMessage) {
                                        System.out.println(((TextMessage) mes).getText());
                                    }
                                }
                            }

                            String s2 = "Izmenjena obaveza";
                            System.out.println(s2);

                            TextMessage tekstpor2 = context.createTextMessage(s2);
                            tekstpor2.setIntProperty("id", 2);
                            producer.send(topic, tekstpor2);
                            break;
                        case "obrisi":
                            long idZaBrisanje = tm.getLongProperty("IdIzmeni");
                            Kalendar kalen = em.find(Kalendar.class, idZaBrisanje);

                            if (kalen.getAlarm() != null) {
                                TextMessage message = context.createTextMessage("");
                                message.setLongProperty("iddeaktiviraj", kalen.getAlarm().getId());
                                message.setStringProperty("Vrsta", "deaktiviraj");
                                message.setIntProperty("id", 1);
                                producer.send(topicA, message);

                                Message mes = consumerA.receive();
                                if (mes instanceof TextMessage) {
                                    System.out.println(((TextMessage) mes).getText());
                                }
                            }
                            em.getTransaction().begin();
                            em.remove(kalen);
                            em.getTransaction().commit();

                            String s3 = "Obrisana obaveza";
                            System.out.println(s3);

                            TextMessage tekstpor3 = context.createTextMessage(s3);
                            tekstpor3.setIntProperty("id", 2);
                            producer.send(topic, tekstpor3);

                            break;
                        default:
                            System.out.println("Nepoznata komanda!");
                            break;
                    }
                } catch (JMSException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);

                }
            }
        }
    }

    public static long izracunajVreme(String source, String destination) {
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
                    String s1 = "";
                    if (m1.find()) {
                        s1 = m1.group(0);
                    }
                    Pattern p2 = Pattern.compile("\\d+ minute");
                    Matcher m2 = p2.matcher(vreme);
                    String s2 = "";
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

            long vr = minuta * 60 * 1000;
            return vr;

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Main.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(Main.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}
