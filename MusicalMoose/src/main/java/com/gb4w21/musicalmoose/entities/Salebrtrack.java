/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author owner
 */
@Entity
@Table(name = "salebrtrack", catalog = "MUSICSTORAGE", schema = "")
@NamedQueries({
    @NamedQuery(name = "Salebrtrack.findAll", query = "SELECT s FROM Salebrtrack s"),
    @NamedQuery(name = "Salebrtrack.findBySalebrtrackid", query = "SELECT s FROM Salebrtrack s WHERE s.salebrtrackid = :salebrtrackid"),
    @NamedQuery(name = "Salebrtrack.findByDateofsale", query = "SELECT s FROM Salebrtrack s WHERE s.dateofsale = :dateofsale"),
    @NamedQuery(name = "Salebrtrack.findByPrice", query = "SELECT s FROM Salebrtrack s WHERE s.price = :price")})
public class Salebrtrack implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "SALEBRTRACKID")
    private Integer salebrtrackid;
    @Column(name = "DATEOFSALE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateofsale;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PRICE")
    private Float price;
    @JoinColumn(name = "SALEID", referencedColumnName = "SALEID")
    @ManyToOne
    private Invoice saleid;
    @JoinColumn(name = "INVENTORYID", referencedColumnName = "INVENTORYID")
    @ManyToOne
    private MusicTrack inventoryid;

    public Salebrtrack() {
    }

    public Salebrtrack(Integer salebrtrackid) {
        this.salebrtrackid = salebrtrackid;
    }

    public Integer getSalebrtrackid() {
        return salebrtrackid;
    }

    public void setSalebrtrackid(Integer salebrtrackid) {
        this.salebrtrackid = salebrtrackid;
    }

    public Date getDateofsale() {
        return dateofsale;
    }

    public void setDateofsale(Date dateofsale) {
        this.dateofsale = dateofsale;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Invoice getSaleid() {
        return saleid;
    }

    public void setSaleid(Invoice saleid) {
        this.saleid = saleid;
    }

    public MusicTrack getInventoryid() {
        return inventoryid;
    }

    public void setInventoryid(MusicTrack inventoryid) {
        this.inventoryid = inventoryid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (salebrtrackid != null ? salebrtrackid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Salebrtrack)) {
            return false;
        }
        Salebrtrack other = (Salebrtrack) object;
        if ((this.salebrtrackid == null && other.salebrtrackid != null) || (this.salebrtrackid != null && !this.salebrtrackid.equals(other.salebrtrackid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gb4w21.musicalmoose.entities.Salebrtrack[ salebrtrackid=" + salebrtrackid + " ]";
    }
    
}
