/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author owner
 */
@Entity
@Table(name = "creditcardinfo", catalog = "MUSICSTORAGE", schema = "")
@NamedQueries({
    @NamedQuery(name = "Creditcardinfo.findAll", query = "SELECT c FROM Creditcardinfo c"),
    @NamedQuery(name = "Creditcardinfo.findByCreditcardid", query = "SELECT c FROM Creditcardinfo c WHERE c.creditcardid = :creditcardid"),
    @NamedQuery(name = "Creditcardinfo.findByCreditcardbrand", query = "SELECT c FROM Creditcardinfo c WHERE c.creditcardbrand = :creditcardbrand"),
    @NamedQuery(name = "Creditcardinfo.findByCreditcardnumber", query = "SELECT c FROM Creditcardinfo c WHERE c.creditcardnumber = :creditcardnumber"),
    @NamedQuery(name = "Creditcardinfo.findByExpirationmonth", query = "SELECT c FROM Creditcardinfo c WHERE c.expirationmonth = :expirationmonth"),
    @NamedQuery(name = "Creditcardinfo.findByExpirationyear", query = "SELECT c FROM Creditcardinfo c WHERE c.expirationyear = :expirationyear")})
public class Creditcardinfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "CREDITCARDID")
    private Integer creditcardid;
    @Size(max = 255)
    @Column(name = "CREDITCARDBRAND")
    private String creditcardbrand;
    @Column(name = "CREDITCARDNUMBER")
    private Integer creditcardnumber;
    @Column(name = "EXPIRATIONMONTH")
    private Short expirationmonth;
    @Column(name = "EXPIRATIONYEAR")
    private Short expirationyear;
    @JoinColumn(name = "CLIENTID", referencedColumnName = "CLIENTID")
    @ManyToOne
    private Client clientid;

    public Creditcardinfo() {
    }

    public Creditcardinfo(Integer creditcardid) {
        this.creditcardid = creditcardid;
    }

    public Integer getCreditcardid() {
        return creditcardid;
    }

    public void setCreditcardid(Integer creditcardid) {
        this.creditcardid = creditcardid;
    }

    public String getCreditcardbrand() {
        return creditcardbrand;
    }

    public void setCreditcardbrand(String creditcardbrand) {
        this.creditcardbrand = creditcardbrand;
    }

    public Integer getCreditcardnumber() {
        return creditcardnumber;
    }

    public void setCreditcardnumber(Integer creditcardnumber) {
        this.creditcardnumber = creditcardnumber;
    }

    public Short getExpirationmonth() {
        return expirationmonth;
    }

    public void setExpirationmonth(Short expirationmonth) {
        this.expirationmonth = expirationmonth;
    }

    public Short getExpirationyear() {
        return expirationyear;
    }

    public void setExpirationyear(Short expirationyear) {
        this.expirationyear = expirationyear;
    }

    public Client getClientid() {
        return clientid;
    }

    public void setClientid(Client clientid) {
        this.clientid = clientid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (creditcardid != null ? creditcardid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Creditcardinfo)) {
            return false;
        }
        Creditcardinfo other = (Creditcardinfo) object;
        if ((this.creditcardid == null && other.creditcardid != null) || (this.creditcardid != null && !this.creditcardid.equals(other.creditcardid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gb4w21.musicalmoose.entities.Creditcardinfo[ creditcardid=" + creditcardid + " ]";
    }
    
}
