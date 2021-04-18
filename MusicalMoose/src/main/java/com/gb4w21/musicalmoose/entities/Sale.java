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
@Table(name = "sale", catalog = "CSgb4w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "Sale.findAll", query = "SELECT s FROM Sale s"),
    @NamedQuery(name = "Sale.findBySaleid", query = "SELECT s FROM Sale s WHERE s.saleid = :saleid"),
    @NamedQuery(name = "Sale.findBySaleremoved", query = "SELECT s FROM Sale s WHERE s.saleremoved = :saleremoved"),
    @NamedQuery(name = "Sale.findBySaledate", query = "SELECT s FROM Sale s WHERE s.saledate = :saledate")})
public class Sale implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "SALEID")
    private Integer saleid;
    @Column(name = "SALEREMOVED")
    private Boolean saleremoved;
    @Column(name = "SALEDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date saledate;
    @JoinColumn(name = "CLIENTID", referencedColumnName = "CLIENTID")
    @ManyToOne
    private Client clientid;
    @OneToMany(mappedBy = "saleid")
    private List<Invoicedetail> invoicedetailList;
    public Sale() {
    }

    public Sale(Integer saleid) {
        this.saleid = saleid;
    }

    public Integer getSaleid() {
        return saleid;
    }

    public void setSaleid(Integer saleid) {
        this.saleid = saleid;
    }

    public Boolean getSaleremoved() {
        return saleremoved;
    }

    public void setSaleremoved(Boolean saleremoved) {
        this.saleremoved = saleremoved;
    }

    public Date getSaledate() {
        return saledate;
    }

    public void setSaledate(Date saledate) {
        this.saledate = saledate;
    }

    public Client getClientid() {
        return clientid;
    }

    public void setClientid(Client clientid) {
        this.clientid = clientid;
    }
    public List<Invoicedetail> getInvoicedetailList(){
        return invoicedetailList;
    }
    public void setInvoicedetailList(List<Invoicedetail> invoicedetailList){
        this.invoicedetailList =invoicedetailList;
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
        if (!(object instanceof Sale)) {
            return false;
        }
        Sale other = (Sale) object;
        if ((this.saleid == null && other.saleid != null) || (this.saleid != null && !this.saleid.equals(other.saleid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gb4w21.musicalmoose.entities.Sale[ saleid=" + saleid + " ]";
    }
    
}
