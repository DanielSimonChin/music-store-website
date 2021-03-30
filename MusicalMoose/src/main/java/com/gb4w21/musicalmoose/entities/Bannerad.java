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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author owner
 */
@Entity
@Table(name = "bannerad", catalog = "MUSICSTORAGE", schema = "")
@NamedQueries({
    @NamedQuery(name = "Bannerad.findAll", query = "SELECT b FROM Bannerad b"),
    @NamedQuery(name = "Bannerad.findByUrl", query = "SELECT b FROM Bannerad b WHERE b.url = :url"),
    @NamedQuery(name = "Bannerad.findByBanneraddid", query = "SELECT b FROM Bannerad b WHERE b.banneraddid = :banneraddid"),
    @NamedQuery(name = "Bannerad.findByFilename", query = "SELECT b FROM Bannerad b WHERE b.filename = :filename")})
public class Bannerad implements Serializable {

    @Lob
    @Size(max = 16777215)
    @Column(name = "URL")
    private String url;
    @Size(max = 255)
    @Column(name = "FILENAME")
    private String filename;
    @Column(name = "DISPLAYED")
    private Boolean displayed;
    @Column(name = "PAGEPOSITION")
    private Integer pageposition;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BANNERADDID")
    private Integer banneraddid;

    public Bannerad() {
    }

    public Bannerad(Integer banneraddid) {
        this.banneraddid = banneraddid;
    }


    public Integer getBanneraddid() {
        return banneraddid;
    }

    public void setBanneraddid(Integer banneraddid) {
        this.banneraddid = banneraddid;
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
        return "com.gb4w21.musicalmoose.Bannerad[ banneraddid=" + banneraddid + " ]";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Boolean getDisplayed() {
        return displayed;
    }

    public void setDisplayed(Boolean displayed) {
        this.displayed = displayed;
    }

    public Integer getPageposition() {
        return pageposition;
    }

    public void setPageposition(Integer pageposition) {
        this.pageposition = pageposition;
    }
    
}
