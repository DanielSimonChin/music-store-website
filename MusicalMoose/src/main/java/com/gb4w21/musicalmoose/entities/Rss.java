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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Generated entity from database
 *
 * @author MusicalMoose
 */
@Entity
@Table(name = "rss", catalog = "CSgb4w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "Rss.findAll", query = "SELECT r FROM Rss r"),
    @NamedQuery(name = "Rss.findById", query = "SELECT r FROM Rss r WHERE r.id = :id"),
    @NamedQuery(name = "Rss.findByUrl", query = "SELECT r FROM Rss r WHERE r.url = :url"),
    @NamedQuery(name = "Rss.findByRssremoved", query = "SELECT r FROM Rss r WHERE r.rssremoved = :rssremoved")})
public class Rss implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Size(max = 255)
    @Column(name = "URL")
    private String url;
    @Column(name = "RSSREMOVED")
    private Boolean rssremoved;

    public Rss() {
    }

    public Rss(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getRssremoved() {
        return rssremoved;
    }

    public void setRssremoved(Boolean rssremoved) {
        this.rssremoved = rssremoved;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Rss)) {
            return false;
        }
        Rss other = (Rss) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gb4w21.musicalmoose.entities.Rss[ id=" + id + " ]";
    }
    
}
