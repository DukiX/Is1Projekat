/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarm;

import entiteti.Alarmi;
import entiteti.ZvonoAlarma;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

/**
 *
 * @author Dusan
 */
public class AlarmThread extends Thread {

    private EntityManager em;

    private ConnectionFactory cf;

    private Topic topicRz;

    @Override
    public void run() {
        while (!interrupted()) {
            deaktivirajStare();
            obnoviDatumPeriodicnim();

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Alarmi> query = criteriaBuilder.createQuery(Alarmi.class);
            Root<Alarmi> r = query.from(Alarmi.class);

            query.select(r);
            query.where(criteriaBuilder.equal(r.get("aktivan"), 1));

            List<Order> orderList = new ArrayList();
            orderList.add(criteriaBuilder.asc(r.get("datumAlarma")));
            orderList.add(criteriaBuilder.asc(r.get("vremeAlarma")));

            query.orderBy(orderList);

            TypedQuery<Alarmi> tq = em.createQuery(query);
            List<Alarmi> lista = tq.getResultList();
            synchronized (this) {
                if (lista != null) {
                    if (!lista.isEmpty()) {
                        Alarmi a = lista.get(0);
                        Calendar cal1 = Calendar.getInstance();
                        cal1.setTime(a.getDatumAlarma());
                        Calendar cal2 = Calendar.getInstance();
                        cal2.setTime(a.getVremeAlarma());

                        cal1.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
                        cal1.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
                        cal1.set(Calendar.SECOND, cal2.get(Calendar.SECOND));

                        System.out.println("budim se u: " + cal1.get(Calendar.HOUR_OF_DAY) + ":" + cal1.get(Calendar.MINUTE)+"dat "+cal1.get(Calendar.DATE));

                        Calendar sad = Calendar.getInstance();

                        long milis = cal1.getTimeInMillis() - sad.getTimeInMillis();

                        try {
                            System.out.println("Spavam: " + milis);
                            wait(milis);
                            System.out.println("budan");
                            sad = Calendar.getInstance();
                            if ((Math.abs(cal1.getTimeInMillis() - sad.getTimeInMillis())) < 1000) {
                                zvoni();
                                wait(1001);
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AlarmThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            wait();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AlarmThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AlarmThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    public AlarmThread(EntityManager em, ConnectionFactory cf, Topic topicRz) {
        this.em = em;
        this.cf = cf;
        this.topicRz = topicRz;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    private void zvoni() {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<ZvonoAlarma> q = cb.createQuery(ZvonoAlarma.class);
            Root<ZvonoAlarma> c = q.from(ZvonoAlarma.class);
            q.where(cb.equal(c.get("id"), 1));
            q.select(c);
            TypedQuery<ZvonoAlarma> tq = em.createQuery(q);
            List<ZvonoAlarma> lista = tq.getResultList();
            String pesma = lista.get(0).getPesma();

            JMSContext contextRz = cf.createContext();
            JMSProducer producerRz = contextRz.createProducer();

            TextMessage message = contextRz.createTextMessage(pesma);
            message.setStringProperty("Vrsta", "PustiPesmuA");
            message.setIntProperty("id", 1);

            producerRz.send(topicRz, message);
            System.out.println("Poslat je zahtev za pustanje pesme: " + message.getText());
            JMSConsumer consumerRz = contextRz.createConsumer(topicRz, "id = " + 3);

            Message m = consumerRz.receive();
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
            Logger.getLogger(AlarmThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deaktivirajStare() {
        CriteriaBuilder cb1 = em.getCriteriaBuilder();
        CriteriaUpdate<Alarmi> update = cb1.createCriteriaUpdate(Alarmi.class);
        Root e = update.from(Alarmi.class);
        update.set("aktivan", false);

        Date danas = new Date();

        update.where(cb1.and(cb1.equal(e.get("periodican"), false),cb1.or(cb1.and(cb1.lessThan(e.get("vremeAlarma"), new Time(danas.getTime())),
                cb1.equal(e.get("datumAlarma"), new java.sql.Date(danas.getTime()))), 
                cb1.lessThan(e.get("datumAlarma"), new java.sql.Date(danas.getTime())))));

        em.getTransaction().begin();

        em.createQuery(update).executeUpdate();

        em.getTransaction().commit();
    }

    private void obnoviDatumPeriodicnim() {
        Date danas = new Date();
        java.sql.Date dns = new java.sql.Date(danas.getTime());
        java.sql.Time sad = new java.sql.Time(danas.getTime());

        String queryStr = "SELECT a FROM Alarmi a WHERE (a.periodican = 1)"
                + "and ((a.datumAlarma = :danas and a.vremeAlarma < :sad) or (a.datumAlarma<:danas))";
        TypedQuery<Alarmi> query = em.createQuery(queryStr, Alarmi.class);
        query.setParameter("danas", dns);
        query.setParameter("sad", sad);
        List<Alarmi> results = query.getResultList();
        for (Alarmi a : results) {
            java.sql.Date dat = a.getDatumAlarma();
            Calendar cal = Calendar.getInstance();
            cal.setTime(dat);
            cal.add(Calendar.DATE, 1);
            dat = new java.sql.Date(cal.getTimeInMillis());

            CriteriaBuilder cb1 = em.getCriteriaBuilder();
            CriteriaUpdate<Alarmi> update = cb1.createCriteriaUpdate(Alarmi.class);
            Root e = update.from(Alarmi.class);
            update.set("datumAlarma", dat);

            update.where(cb1.equal(e.get("id"), a.getId()));

            em.getTransaction().begin();

            em.createQuery(update).executeUpdate();

            em.flush();
            
            em.getTransaction().commit();
            
        }
    }
}
