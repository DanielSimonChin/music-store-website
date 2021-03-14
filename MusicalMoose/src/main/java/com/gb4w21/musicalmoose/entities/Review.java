/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.entities;

import com.gb4w21.musicalmoose.entities.Client;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author owner
 */
@Entity
@Table(name = "review", catalog = "MUSICSTORAGE", schema = "")
@NamedQueries({
    @NamedQuery(name = "Review.findAll", query = "SELECT r FROM Review r"),
    @NamedQuery(name = "Review.findByReviewid", query = "SELECT r FROM Review r WHERE r.reviewid = :reviewid"),
    @NamedQuery(name = "Review.findByReviewdate", query = "SELECT r FROM Review r WHERE r.reviewdate = :reviewdate"),
    @NamedQuery(name = "Review.findByClientname", query = "SELECT r FROM Review r WHERE r.clientname = :clientname"),
    @NamedQuery(name = "Review.findByRating", query = "SELECT r FROM Review r WHERE r.rating = :rating"),
    @NamedQuery(name = "Review.findByAprovalstatus", query = "SELECT r FROM Review r WHERE r.aprovalstatus = :aprovalstatus")})
public class Review implements Serializable {

    @Size(max = 255)
    @Column(name = "CLIENTNAME")
    private String clientname;
    @Lob
    @Size(max = 16777215)
    @Column(name = "REVIEWTEXT")
    private String reviewtext;
    @JoinColumn(name = "INVENTORYID", referencedColumnName = "INVENTORYID")
    @ManyToOne
    private MusicTrack inventoryid;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "REVIEWID")
    private Integer reviewid;
    @Column(name = "REVIEWDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reviewdate;
    @Column(name = "RATING")
    private Integer rating;
    @Column(name = "APROVALSTATUS")
    private Boolean aprovalstatus;
    @JoinColumn(name = "CLIENTID", referencedColumnName = "CLIENTID")
    @ManyToOne
    private Client clientid;

    public Review() {
    }

    public Review(Integer reviewid) {
        this.reviewid = reviewid;
    }

    public Integer getReviewid() {
        return reviewid;
    }

    public void setReviewid(Integer reviewid) {
        this.reviewid = reviewid;
    }

    public Date getReviewdate() {
        return reviewdate;
    }

    public void setReviewdate(Date reviewdate) {
        this.reviewdate = reviewdate;
    }


    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }


    public Boolean getAprovalstatus() {
        return aprovalstatus;
    }

    public void setAprovalstatus(Boolean aprovalstatus) {
        this.aprovalstatus = aprovalstatus;
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
        hash += (reviewid != null ? reviewid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Review)) {
            return false;
        }
        Review other = (Review) object;
        if ((this.reviewid == null && other.reviewid != null) || (this.reviewid != null && !this.reviewid.equals(other.reviewid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gb4w21.musicalmoose.Review[ reviewid=" + reviewid + " ]";
    }


    public MusicTrack getInventoryid() {
        return inventoryid;
    }

    public void setInventoryid(MusicTrack inventoryid) {
        this.inventoryid = inventoryid;
    }

    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }

    public String getReviewtext() {
        return reviewtext;
    }

    public void setReviewtext(String reviewtext) {
        this.reviewtext = reviewtext;
    }
    
}
