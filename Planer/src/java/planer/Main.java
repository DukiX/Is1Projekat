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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.persistence.criteria.Order;
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

                            CriteriaBuilder cb = em.getCriteriaBuilder();
                            CriteriaQuery<Kalendar> q = cb.createQuery(Kalendar.class);
                            Root<Kalendar> c = q.from(Kalendar.class);
                            q.select(c);
                            List<Order> orderList = new ArrayList();
                            orderList.add(cb.asc(c.get("datum")));
                            orderList.add(cb.asc(c.get("vreme")));

                            q.orderBy(orderList);

                            TypedQuery<Kalendar> tq = em.createQuery(q);
                            List<Kalendar> lista = tq.getResultList();
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

                            Alarmi a =null;

                            if (podsetnik) {
                                //napraviti alarm i tako to
                            }

                            em.getTransaction().begin();

                            Kalendar k = new Kalendar(opis, datesql, timesql, destinacija, podsetnik, a);

                            em.persist(k);

                            em.getTransaction().commit();

                            String s1 = "Dodata obaveza";
                            System.out.println(s1);

                            TextMessage tekstpor1 = context.createTextMessage(s1);
                            tekstpor1.setIntProperty("id", 2);
                            producer.send(topic, tekstpor1);
                            break;
                        case "izmeni":
                            String s2 = "Izmenjena obaveza";
                            System.out.println(s2);

                            TextMessage tekstpor2 = context.createTextMessage(s2);
                            tekstpor2.setIntProperty("id", 2);
                            producer.send(topic, tekstpor2);
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
