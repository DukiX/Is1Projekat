/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entiteti;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Dusan
 */
@Entity
public class Kalendar implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String opis;
    private java.sql.Date datum;
    private java.sql.Time vreme;
    private String destinacija;
    private boolean podsetnik;
    private long idAlarm;

    public Kalendar() {
    }

    public Kalendar(String opis, Date datum, Time vreme, String destinacija, boolean podsetnik, long idAlarm) {
        this.opis = opis;
        this.datum = datum;
        this.vreme = vreme;
        this.destinacija = destinacija;
        this.podsetnik = podsetnik;
        this.idAlarm = idAlarm;
    }

    public long getIdAlarm() {
        return idAlarm;
    }

    public void setIdAlarm(long idAlarm) {
        this.idAlarm = idAlarm;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Time getVreme() {
        return vreme;
    }

    public void setVreme(Time vreme) {
        this.vreme = vreme;
    }

    public String getDestinacija() {
        return destinacija;
    }

    public void setDestinacija(String destinacija) {
        this.destinacija = destinacija;
    }

    public boolean isPodsetnik() {
        return podsetnik;
    }

    public void setPodsetnik(boolean podsetnik) {
        this.podsetnik = podsetnik;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    
    
}
