/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author owner
 */
@Entity
@Table(name = "invoice", catalog = "MUSICSTORAGE", schema = "")
@NamedQueries({
    @NamedQuery(name = "Invoice.findAll", query = "SELECT i FROM Invoice i"),
    @NamedQuery(name = "Invoice.findBySaleid", query = "SELECT i FROM Invoice i WHERE i.saleid = :saleid"),
    @NamedQuery(name = "Invoice.findBySaledate", query = "SELECT i FROM Invoice i WHERE i.saledate = :saledate"),
    @NamedQuery(name = "Invoice.findByTotalnetvalue", query = "SELECT i FROM Invoice i WHERE i.totalnetvalue = :totalnetvalue"),
    @NamedQuery(name = "Invoice.findByPst", query = "SELECT i FROM Invoice i WHERE i.pst = :pst"),
    @NamedQuery(name = "Invoice.findByGst", query = "SELECT i FROM Invoice i WHERE i.gst = :gst"),
    @NamedQuery(name = "Invoice.findByHst", query = "SELECT i FROM Invoice i WHERE i.hst = :hst"),
    @NamedQuery(name = "Invoice.findByTotalgrossvalue", query = "SELECT i FROM Invoice i WHERE i.totalgrossvalue = :totalgrossvalue")})
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "SALEID")
    private Integer saleid;
    @Column(name = "SALEDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date saledate;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "TOTALNETVALUE")
    private Float totalnetvalue;
    @Column(name = "PST")
    private Float pst;
    @Column(name = "GST")
    private Float gst;
    @Column(name = "HST")
    private Float hst;
    @Column(name = "TOTALGROSSVALUE")
    private Float totalgrossvalue;
    @OneToMany(mappedBy = "saleid")
    private List<Salebralbum> salebralbumList;
    @JoinColumn(name = "CLIENTID", referencedColumnName = "CLIENTID")
    @ManyToOne
    private Client clientid;
    @OneToMany(mappedBy = "saleid")
    private List<Salebrtrack> salebrtrackList;

    public Invoice() {
    }

    public Invoice(Integer saleid) {
        this.saleid = saleid;
    }

    public Integer getSaleid() {
        return saleid;
    }

    public void setSaleid(Integer saleid) {
        this.saleid = saleid;
    }

    public Date getSaledate() {
        return saledate;
    }

    public void setSaledate(Date saledate) {
        this.saledate = saledate;
    }

    public Float getTotalnetvalue() {
        return totalnetvalue;
    }

    public void setTotalnetvalue(Float totalnetvalue) {
        this.totalnetvalue = totalnetvalue;
    }

    public Float getPst() {
        return pst;
    }

    public void setPst(Float pst) {
        this.pst = pst;
    }

    public Float getGst() {
        return gst;
    }

    public void setGst(Float gst) {
        this.gst = gst;
    }

    public Float getHst() {
        return hst;
    }

    public void setHst(Float hst) {
        this.hst = hst;
    }

    public Float getTotalgrossvalue() {
        return totalgrossvalue;
    }

    public void setTotalgrossvalue(Float totalgrossvalue) {
        this.totalgrossvalue = totalgrossvalue;
    }

    public List<Salebralbum> getSalebralbumList() {
        return salebralbumList;
    }

    public void setSalebralbumList(List<Salebralbum> salebralbumList) {
        this.salebralbumList = salebralbumList;
    }

    public Client getClientid() {
        return clientid;
    }

    public void setClientid(Client clientid) {
        this.clientid = clientid;
    }

    public List<Salebrtrack> getSalebrtrackList() {
        return salebrtrackList;
    }

    public void setSalebrtrackList(List<Salebrtrack> salebrtrackList) {
        this.salebrtrackList = salebrtrackList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (saleid != null ? saleid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Invoice)) {
            return false;
        }
        Invoice other = (Invoice) object;
        if ((this.saleid == null && other.saleid != null) || (this.saleid != null && !this.saleid.equals(other.saleid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gb4w21.musicalmoose.entities.Invoice[ saleid=" + saleid + " ]";
    }
    
}
