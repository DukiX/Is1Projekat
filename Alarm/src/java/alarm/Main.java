/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarm;

import entiteti.Alarmi;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Dusan
 */
public class Main {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory cf;

    @Resource(lookup = "AlarmT")
    private static Topic topic;

    public static void main(String[] args) {
        JMSContext context = cf.createContext();
        JMSConsumer consumer = context.createConsumer(topic, "id = " + 1);
        JMSProducer producer = context.createProducer();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlarmPU");
        EntityManager em = emf.createEntityManager();

        wh:
        while (true) {
            Message m = consumer.receive();
            if (m instanceof TextMessage) {
                try {
                    TextMessage tm = (TextMessage) m;
                    String vrstaPoruke = tm.getStringProperty("Vrsta");
                    String vreme = "";
                    switch (vrstaPoruke) {
                        case "NavijAlarm":
                            try {
                                vreme = tm.getText();
                                boolean periodican = tm.getBooleanProperty("periodican");

                                em.getTransaction().begin();

                                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                Date vremeD = format.parse(vreme);
                                Time t = new Time(vremeD.getTime());
                                java.sql.Date d = new java.sql.Date(vremeD.getTime());
                                Alarmi a = new Alarmi(t, d, periodican,true);

                                em.persist(a);

                                em.getTransaction().commit();

                                String s = "Alarm zabelezen: " + vreme;
                                System.out.println(s);

                                TextMessage tekstpor = context.createTextMessage(s);
                                tekstpor.setIntProperty("id", 2);
                                producer.send(topic, tekstpor);

                                obrisiStare(em);
                            } catch (ParseException ex) {
                                String s = "Pogresan format vremena: " + vreme;
                                System.out.println(s);

                                TextMessage tekstpor = context.createTextMessage(s);
                                tekstpor.setIntProperty("id", 2);
                                producer.send(topic, tekstpor);
                            }
                            break;
                        case "NavijAlarmPeriodican":
                            try {
                                vreme = tm.getText();
                                boolean periodican = tm.getBooleanProperty("periodican");

                                em.getTransaction().begin();

                                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                Date vremeD = format.parse(vreme);
                                Time t = new Time(vremeD.getTime());
                                Alarmi a = new Alarmi(t, null, periodican,true);

                                em.persist(a);

                                em.getTransaction().commit();

                                String s = "Periodicni alarm zabelezen: " + vreme;
                                System.out.println(s);

                                TextMessage tekstpor = context.createTextMessage(s);
                                tekstpor.setIntProperty("id", 2);
                                producer.send(topic, tekstpor);

                                obrisiStare(em);

                            } catch (ParseException ex) {
                                String s = "Pogresan format vremena: " + vreme;
                                System.out.println(s);

                                TextMessage tekstpor = context.createTextMessage(s);
                                tekstpor.setIntProperty("id", 2);
                                producer.send(topic, tekstpor);
                            }
                            break;
                        case "DohvatiVremena":
                            CriteriaBuilder cb = em.getCriteriaBuilder();
                            CriteriaQuery<Alarmi> q = cb.createQuery(Alarmi.class);
                            Root<Alarmi> c = q.from(Alarmi.class);
                            q.where(cb.equal(c.get("periodican"), true));
                            q.select(c);

                            TypedQuery<Alarmi> tq = em.createQuery(q);
                            List<Alarmi> lista = tq.getResultList();
                            LinkedList<String> lst = new LinkedList<>();
                            lista.forEach((l) -> {
                                Time t = l.getVremeAlarma();
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                Date dt = new Date(t.getTime());
                                String formatiran = sdf.format(dt);
                                lst.add(formatiran);
                            });
                            ObjectMessage om = context.createObjectMessage(lst);
                            
                            om.setIntProperty("id", 2);
                            producer.send(topic, om);
                            System.out.println("Poslata lista starih alarma");
                            
                            break;
                        case "PostaviZvono":
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

    public static void obrisiStare(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Alarmi> delete = cb.createCriteriaDelete(Alarmi.class);
        Root e = delete.from(Alarmi.class);
        Date danas = new Date();
        delete.where(cb.lessThan(e.get("vremeAlarma"), new Time(danas.getTime())),
                cb.lessThanOrEqualTo(e.get("datumAlarma"), new java.sql.Date(danas.getTime())),
                cb.equal(e.get("periodican"), false));

        em.getTransaction().begin();

        em.createQuery(delete).executeUpdate();

        em.getTransaction().commit();
    }
}
