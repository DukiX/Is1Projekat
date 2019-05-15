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
public class PustenePesme implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public PustenePesme() {
    }

    public PustenePesme(String NazivPesme) {
        this.NazivPesme = NazivPesme;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String NazivPesme;

    public String getNazivPesme() {
        return NazivPesme;
    }

    public void setNazivPesme(String NazivPesme) {
        this.NazivPesme = NazivPesme;
    }

    

}
