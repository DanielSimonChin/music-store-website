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
@Table(name = "salebralbum", catalog = "MUSICSTORAGE", schema = "")
@NamedQueries({
    @NamedQuery(name = "Salebralbum.findAll", query = "SELECT s FROM Salebralbum s"),
    @NamedQuery(name = "Salebralbum.findBySalebralbumid", query = "SELECT s FROM Salebralbum s WHERE s.salebralbumid = :salebralbumid"),
    @NamedQuery(name = "Salebralbum.findByDateofsale", query = "SELECT s FROM Salebralbum s WHERE s.dateofsale = :dateofsale"),
    @NamedQuery(name = "Salebralbum.findByPrice", query = "SELECT s FROM Salebralbum s WHERE s.price = :price")})
public class Salebralbum implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "SALEBRALBUMID")
    private Integer salebralbumid;
    @Column(name = "DATEOFSALE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateofsale;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PRICE")
    private Float price;
    @JoinColumn(name = "ALBUMID", referencedColumnName = "ALBUMID")
    @ManyToOne
    private Album albumid;
    @JoinColumn(name = "SALEID", referencedColumnName = "SALEID")
    @ManyToOne
    private Invoice saleid;

    public Salebralbum() {
    }

    public Salebralbum(Integer salebralbumid) {
        this.salebralbumid = salebralbumid;
    }

    public Integer getSalebralbumid() {
        return salebralbumid;
    }

    public void setSalebralbumid(Integer salebralbumid) {
        this.salebralbumid = salebralbumid;
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

    public Album getAlbumid() {
        return albumid;
    }

    public void setAlbumid(Album albumid) {
        this.albumid = albumid;
    }

    public Invoice getSaleid() {
        return saleid;
    }

    public void setSaleid(Invoice saleid) {
        this.saleid = saleid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (salebralbumid != null ? salebralbumid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Salebralbum)) {
            return false;
        }
        Salebralbum other = (Salebralbum) object;
        if ((this.salebralbumid == null && other.salebralbumid != null) || (this.salebralbumid != null && !this.salebralbumid.equals(other.salebralbumid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gb4w21.musicalmoose.entities.Salebralbum[ salebralbumid=" + salebralbumid + " ]";
    }
    
}
