/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entiteti;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Dusan
 */
@Entity
public class ZvonoAlarma implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    private String pesma;

    public ZvonoAlarma() {
    }

    public ZvonoAlarma(String pesma) {
        this.pesma = pesma;
    }

    public String getPesma() {
        return pesma;
    }

    public void setPesma(String pesma) {
        this.pesma = pesma;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
}
