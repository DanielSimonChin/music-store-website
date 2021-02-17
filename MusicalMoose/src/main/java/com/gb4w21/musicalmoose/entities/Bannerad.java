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
import javax.persistence.Lob;
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
@Table(name = "bannerad", catalog = "MUSICSTORAGE", schema = "")
@NamedQueries({
    @NamedQuery(name = "Bannerad.findAll", query = "SELECT b FROM Bannerad b"),
    @NamedQuery(name = "Bannerad.findBySponcer", query = "SELECT b FROM Bannerad b WHERE b.sponcer = :sponcer"),
    @NamedQuery(name = "Bannerad.findByAddrevenue", query = "SELECT b FROM Bannerad b WHERE b.addrevenue = :addrevenue"),
    @NamedQuery(name = "Bannerad.findByAdimagename", query = "SELECT b FROM Bannerad b WHERE b.adimagename = :adimagename"),
    @NamedQuery(name = "Bannerad.findByBanneraddid", query = "SELECT b FROM Bannerad b WHERE b.banneraddid = :banneraddid"),
    @NamedQuery(name = "Bannerad.findByBannertitle", query = "SELECT b FROM Bannerad b WHERE b.bannertitle = :bannertitle")})
public class Bannerad implements Serializable {

    private static final long serialVersionUID = 1L;
    @Size(max = 255)
    @Column(name = "SPONCER")
    private String sponcer;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "ADDREVENUE")
    private Float addrevenue;
    @Column(name = "ADIMAGENAME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date adimagename;
    @Lob
    @Column(name = "IMAGE")
    private byte[] image;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BANNERADDID")
    private Integer banneraddid;
    @Size(max = 255)
    @Column(name = "BANNERTITLE")
    private String bannertitle;

    public Bannerad() {
    }

    public Bannerad(Integer banneraddid) {
        this.banneraddid = banneraddid;
    }

    public String getSponcer() {
        return sponcer;
    }

    public void setSponcer(String sponcer) {
        this.sponcer = sponcer;
    }

    public Float getAddrevenue() {
        return addrevenue;
    }

    public void setAddrevenue(Float addrevenue) {
        this.addrevenue = addrevenue;
    }

    public Date getAdimagename() {
        return adimagename;
    }

    public void setAdimagename(Date adimagename) {
        this.adimagename = adimagename;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Integer getBanneraddid() {
        return banneraddid;
    }

    public void setBanneraddid(Integer banneraddid) {
        this.banneraddid = banneraddid;
    }

    public String getBannertitle() {
        return bannertitle;
    }

    public void setBannertitle(String bannertitle) {
        this.bannertitle = bannertitle;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (banneraddid != null ? banneraddid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Bannerad)) {
            return false;
        }
        Bannerad other = (Bannerad) object;
        if ((this.banneraddid == null && other.banneraddid != null) || (this.banneraddid != null && !this.banneraddid.equals(other.banneraddid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gb4w21.musicalmoose.entities.Bannerad[ banneraddid=" + banneraddid + " ]";
    }
    
}
