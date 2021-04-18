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
@Table(name = "invoicedetail", catalog = "CSgb4w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "Invoicedetail.findAll", query = "SELECT i FROM Invoicedetail i"),
    @NamedQuery(name = "Invoicedetail.findByInvoiceid", query = "SELECT i FROM Invoicedetail i WHERE i.invoiceid = :invoiceid"),
    @NamedQuery(name = "Invoicedetail.findBySaledate", query = "SELECT i FROM Invoicedetail i WHERE i.saledate = :saledate"),
    @NamedQuery(name = "Invoicedetail.findByCurrentcost", query = "SELECT i FROM Invoicedetail i WHERE i.currentcost = :currentcost"),
    @NamedQuery(name = "Invoicedetail.findByProfit", query = "SELECT i FROM Invoicedetail i WHERE i.profit = :profit"),
    @NamedQuery(name = "Invoicedetail.findByInvoicedetailremoved", query = "SELECT i FROM Invoicedetail i WHERE i.invoicedetailremoved = :invoicedetailremoved"),
    @NamedQuery(name = "Invoicedetail.findByProductdownloaded", query = "SELECT i FROM Invoicedetail i WHERE i.productdownloaded = :productdownloaded")})
public class Invoicedetail implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "INVOICEID")
    private Integer invoiceid;
    @Column(name = "SALEDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date saledate;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation

    @Column(name = "CURRENTCOST")
    private Float currentcost;
    @Column(name = "PROFIT")
    private Float profit;

    @Column(name = "INVOICEDETAILREMOVED")
    private Boolean invoicedetailremoved;
    @JoinColumn(name = "ALBUMID", referencedColumnName = "ALBUMID")
    @ManyToOne
    private Album albumid;
    @JoinColumn(name = "INVENTORYID", referencedColumnName = "INVENTORYID")
    @ManyToOne
    private MusicTrack inventoryid;
    @JoinColumn(name = "SALEID", referencedColumnName = "SALEID")
    @ManyToOne
    private Sale saleid;
    @Column(name = "PRODUCTDOWNLOADED")
    private int productdownloaded;

    public Invoicedetail() {
    }
    public int getProductdownloaded(){
        return productdownloaded;
    }
    public void setProductdownloaded(int productdownloaded){
        this.productdownloaded=productdownloaded;
    }
    public Invoicedetail(Integer invoiceid) {
        this.invoiceid = invoiceid;
    }

    public Integer getInvoiceid() {
        return invoiceid;
    }

    public void setInvoiceid(Integer invoiceid) {
        this.invoiceid = invoiceid;
    }

    public Date getSaledate() {
        return saledate;
    }

    public void setSaledate(Date saledate) {
        this.saledate = saledate;
    }

    public Float getCurrentcost() {
        return currentcost;
    }

    public void setCurrentcost(Float currentcost) {
        this.currentcost = currentcost;
    }

    public Float getProfit() {
        return profit;
    }

    public void setProfit(Float profit) {
        this.profit = profit;
    }

    public Boolean getInvoicedetailremoved() {
        return invoicedetailremoved;
    }

    public void setInvoicedetailremoved(Boolean invoicedetailremoved) {
        this.invoicedetailremoved = invoicedetailremoved;
    }

    public Album getAlbumid() {
        return albumid;
    }

    public void setAlbumid(Album albumid) {
        this.albumid = albumid;
    }

    public MusicTrack getInventoryid() {
        return inventoryid;
    }

    public void setInventoryid(MusicTrack inventoryid) {
        this.inventoryid = inventoryid;
    }

    public Sale getSaleid() {
        return saleid;
    }

    public void setSaleid(Sale saleid) {
        this.saleid = saleid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (invoiceid != null ? invoiceid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Invoicedetail)) {
            return false;
        }
        Invoicedetail other = (Invoicedetail) object;
        if ((this.invoiceid == null && other.invoiceid != null) || (this.invoiceid != null && !this.invoiceid.equals(other.invoiceid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gb4w21.musicalmoose.entities.Invoicedetail[ invoiceid=" + invoiceid + " ]";
    }
    
}
