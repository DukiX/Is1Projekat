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
public class Alarmi implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private Time vremeAlarma;
    private Date datumAlarma;
    
    private boolean periodican;
    
    private boolean aktivan;

    public Alarmi() {
    }

    public Alarmi(Time vremeAlarma, Date datumAlarma, boolean periodican, boolean aktivan) {
        this.vremeAlarma = vremeAlarma;
        this.datumAlarma = datumAlarma;
        this.periodican = periodican;
        this.aktivan = aktivan;
    }

    public boolean isAktivan() {
        return aktivan;
    }

    public void setAktivan(boolean aktivan) {
        this.aktivan = aktivan;
    }

    public Time getVremeAlarma() {
        return vremeAlarma;
    }

    public void setVremeAlarma(Time vremeAlarma) {
        this.vremeAlarma = vremeAlarma;
    }

    public Date getDatumAlarma() {
        return datumAlarma;
    }

    public void setDatumAlarma(Date datumAlarma) {
        this.datumAlarma = datumAlarma;
    }
    
    public boolean isPeriodican() {
        return periodican;
    }

    public void setPeriodican(boolean periodican) {
        this.periodican = periodican;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
   
}
