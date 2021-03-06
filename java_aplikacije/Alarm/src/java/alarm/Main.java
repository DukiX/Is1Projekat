/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarm;

import entiteti.Alarmi;
import entiteti.ZvonoAlarma;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
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

    @Resource(lookup = "RepZvukaT")
    private static Topic topicRz;

    public static void main(String[] args) {
        JMSContext context = cf.createContext();
        JMSConsumer consumer = context.createConsumer(topic, "id = " + 1);
        JMSProducer producer = context.createProducer();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlarmPU");
        EntityManager em = emf.createEntityManager();

        AlarmThread at = new AlarmThread(em, cf, topicRz);
        at.start();
        try {
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

                                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                    Date vremeD = format.parse(vreme);

                                    Calendar calSad = Calendar.getInstance();

                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(vremeD);

                                    cal.set(Calendar.DAY_OF_MONTH, calSad.get(Calendar.DAY_OF_MONTH));
                                    cal.set(Calendar.MONTH, calSad.get(Calendar.MONTH));
                                    cal.set(Calendar.YEAR, calSad.get(Calendar.YEAR));

                                    if (calSad.getTimeInMillis() > cal.getTimeInMillis()) {
                                        cal.add(Calendar.DATE, 1);
                                    }

                                    Time t = new Time(vremeD.getTime());

                                    java.sql.Date d = new java.sql.Date(cal.getTimeInMillis());
                                    Alarmi a = new Alarmi(t, d, periodican, true);

                                    em.persist(a);

                                    em.flush();

                                    em.getTransaction().commit();

                                    synchronized (at) {
                                        at.notifyAll();
                                    }

                                    String s = "Alarm zabelezen: " + vreme;
                                    System.out.println(s);

                                    TextMessage tekstpor = context.createTextMessage(s);
                                    tekstpor.setIntProperty("id", 2);
                                    producer.send(topic, tekstpor);

                                    //deaktivirajStare(em);
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

                                    Calendar calSad = Calendar.getInstance();

                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(vremeD);

                                    cal.set(Calendar.DAY_OF_MONTH, calSad.get(Calendar.DAY_OF_MONTH));
                                    cal.set(Calendar.MONTH, calSad.get(Calendar.MONTH));
                                    cal.set(Calendar.YEAR, calSad.get(Calendar.YEAR));

                                    if (calSad.getTimeInMillis() > cal.getTimeInMillis()) {
                                        cal.add(Calendar.DATE, 1);
                                    }
                                    Time t = new Time(vremeD.getTime());

                                    java.sql.Date d = new java.sql.Date(cal.getTimeInMillis());
                                    Alarmi a = new Alarmi(t, d, periodican, true);

                                    em.persist(a);

                                    em.flush();

                                    em.getTransaction().commit();

                                    synchronized (at) {
                                        at.notifyAll();
                                    }

                                    String s = "Periodicni alarm zabelezen: " + vreme;
                                    System.out.println(s);

                                    TextMessage tekstpor = context.createTextMessage(s);
                                    tekstpor.setIntProperty("id", 2);
                                    producer.send(topic, tekstpor);

                                    //deaktivirajStare(em);
                                } catch (ParseException ex) {
                                    String s = "Pogresan format vremena: " + vreme;
                                    System.out.println(s);

                                    TextMessage tekstpor = context.createTextMessage(s);
                                    tekstpor.setIntProperty("id", 2);
                                    producer.send(topic, tekstpor);
                                }
                                break;
                            case "NavijAlarmPlaner":
                                try {
                                    vreme = tm.getText();
                                    boolean periodican = false;
                                    String dat = tm.getStringProperty("datum");
                                    int potrebnoVreme = tm.getIntProperty("potrebnovreme");
                                    em.getTransaction().begin();

                                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                    Date vremeD = format.parse(vreme);

                                    SimpleDateFormat format2 = new SimpleDateFormat("yyyy/MM/dd");
                                    Date datumD = format2.parse(dat);
                                    
                                    Calendar c1 = Calendar.getInstance();
                                    Calendar c2 = Calendar.getInstance();
                                    c1.setTime(vremeD);
                                    c2.setTime(datumD);
                                    
                                    c1.set(Calendar.DAY_OF_MONTH, c2.get(Calendar.DAY_OF_MONTH));
                                    c1.set(Calendar.MONTH, c2.get(Calendar.MONTH));
                                    c1.set(Calendar.YEAR, c2.get(Calendar.YEAR));
                                    
                                    c1.add(Calendar.MINUTE, -potrebnoVreme);
                                    
                                    vremeD = c1.getTime();
                                    
                                    datumD = c1.getTime();

                                    Time t = new Time(vremeD.getTime());

                                    java.sql.Date d = new java.sql.Date(datumD.getTime());
                                    Alarmi a = new Alarmi(t, d, periodican, true);

                                    em.persist(a);

                                    em.flush();

                                    em.getTransaction().commit();

                                    synchronized (at) {
                                        at.notifyAll();
                                    }

                                    String s = "Alarm zabelezen: " + vreme;
                                    System.out.println(s);

                                    ObjectMessage objm = context.createObjectMessage(a);
                                    objm.setIntProperty("id", 3);
                                    producer.send(topic, objm);

                                    //deaktivirajStare(em);
                                } catch (ParseException ex) {
                                    String s = "Pogresan format vremena: " + vreme;
                                    System.out.println(s);

                                    TextMessage tekstpor = context.createTextMessage(s);
                                    tekstpor.setIntProperty("id", 2);
                                    producer.send(topic, tekstpor);
                                }
                                break;
                            case "IspraviAlarm":
                                vreme = tm.getText();
                                String dat = tm.getStringProperty("datum");
                                long idAl = tm.getLongProperty("idAlZaIz");
                                int potrebnoVreme = tm.getIntProperty("potrebnovreme");
                                
                                Alarmi alIzm = em.find(Alarmi.class, idAl);

                                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                SimpleDateFormat format2 = new SimpleDateFormat("yyyy/MM/dd");
                                Date vremeD = new Date();
                                Date datumD = new Date();
                                try {
                                    vremeD = format.parse(vreme);

                                    datumD = format2.parse(dat);
                                    
                                    Calendar c1 = Calendar.getInstance();
                                    Calendar c2 = Calendar.getInstance();
                                    c1.setTime(vremeD);
                                    c2.setTime(datumD);
                                    
                                    c1.set(Calendar.DAY_OF_MONTH, c2.get(Calendar.DAY_OF_MONTH));
                                    c1.set(Calendar.MONTH, c2.get(Calendar.MONTH));
                                    c1.set(Calendar.YEAR, c2.get(Calendar.YEAR));
                                    
                                    c1.add(Calendar.MINUTE, -potrebnoVreme);
                                    
                                    vremeD = c1.getTime();
                                    
                                    datumD = c1.getTime();

                                } catch (ParseException ex) {
                                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                Time tt = new Time(vremeD.getTime());

                                java.sql.Date d = new java.sql.Date(datumD.getTime());

                                em.getTransaction().begin();

                                alIzm.setDatumAlarma(d);
                                alIzm.setVremeAlarma(tt);
                                alIzm.setAktivan(true);

                                em.flush();
                                em.getTransaction().commit();
                                em.clear();

                                synchronized (at) {
                                    at.notifyAll();
                                }

                                String st = "Alarm ispravljen";
                                System.out.println(st);

                                TextMessage tem = context.createTextMessage(st);
                                tem.setIntProperty("id", 3);
                                producer.send(topic, tem);

                                break;
                            case "DohvatiVremena":
                                CriteriaBuilder cb = em.getCriteriaBuilder();
                                CriteriaQuery<Alarmi> q = cb.createQuery(Alarmi.class);
                                Root<Alarmi> c = q.from(Alarmi.class);
                                //q.where(cb.equal(c.get("periodican"), true));
                                q.where(cb.equal(c.get("aktivan"), false));
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

                                String pesma = tm.getText();

                                CriteriaBuilder cb1 = em.getCriteriaBuilder();
                                CriteriaUpdate<ZvonoAlarma> update = cb1.createCriteriaUpdate(ZvonoAlarma.class);
                                Root e = update.from(ZvonoAlarma.class);
                                update.set("pesma", pesma);
                                update.where(cb1.equal(e.get("id"), 1));

                                em.getTransaction().begin();

                                em.createQuery(update).executeUpdate();

                                em.flush();

                                em.getTransaction().commit();

                                String s = "Postavljena pesma zvona: " + pesma;
                                System.out.println(s);

                                TextMessage tekstpor = context.createTextMessage(s);
                                tekstpor.setIntProperty("id", 2);
                                producer.send(topic, tekstpor);

                                break;
                            case "deaktiviraj":
                                long idDeak = tm.getLongProperty("iddeaktiviraj");
                                Alarmi al = em.find(Alarmi.class, idDeak);
                                em.getTransaction().begin();
                                al.setAktivan(false);
                                em.getTransaction().commit();
                                
                                synchronized (at) {
                                    at.notifyAll();
                                }
                                
                                String str = "Alarm ispravljen";
                                System.out.println(str);

                                TextMessage teme = context.createTextMessage(str);
                                teme.setIntProperty("id", 3);
                                producer.send(topic, teme);
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
        } finally {
            at.interrupt();
        }
    }

    public static void deaktivirajStare(EntityManager em) {

        CriteriaBuilder cb1 = em.getCriteriaBuilder();
        CriteriaUpdate<Alarmi> update = cb1.createCriteriaUpdate(Alarmi.class);
        Root e = update.from(Alarmi.class);
        update.set("aktivan", false);

        Date danas = new Date();

        update.where(cb1.or(cb1.and(cb1.lessThan(e.get("vremeAlarma"), new Time(danas.getTime())),
                cb1.equal(e.get("datumAlarma"), new java.sql.Date(danas.getTime()))), cb1.lessThan(e.get("datumAlarma"), new java.sql.Date(danas.getTime()))));

        em.getTransaction().begin();

        em.createQuery(update).executeUpdate();

        em.flush();

        em.getTransaction().commit();
    }
}
