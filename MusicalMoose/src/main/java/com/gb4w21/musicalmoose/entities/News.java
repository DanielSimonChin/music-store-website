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
@Table(name = "news", catalog = "CSgb4w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "News.findAll", query = "SELECT n FROM News n"),
    @NamedQuery(name = "News.findByNewsid", query = "SELECT n FROM News n WHERE n.newsid = :newsid"),
    @NamedQuery(name = "News.findByNewtitle", query = "SELECT n FROM News n WHERE n.newtitle = :newtitle"),
    @NamedQuery(name = "News.findByCreateddate", query = "SELECT n FROM News n WHERE n.createddate = :createddate"),
    @NamedQuery(name = "News.findByLastdisplayeddtae", query = "SELECT n FROM News n WHERE n.lastdisplayeddtae = :lastdisplayeddtae")})
public class News implements Serializable {

    @Size(max = 255)
    @Column(name = "NEWTITLE")
    private String newtitle;
    @Lob
    @Size(max = 16777215)
    @Column(name = "NEWSTEXT")
    private String newstext;
    @Lob
    @Size(max = 16777215)
    @Column(name = "URL")
    private String url;
    @Column(name = "DISPLAYED")
    private Boolean displayed;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "NEWSID")
    private Integer newsid;
    @Column(name = "CREATEDDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createddate;
    @Column(name = "LASTDISPLAYEDDTAE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastdisplayeddtae;

    public News() {
    }

    public News(Integer newsid) {
        this.newsid = newsid;
    }

    public Integer getNewsid() {
        return newsid;
    }

    public void setNewsid(Integer newsid) {
        this.newsid = newsid;
    }


    public Date getCreateddate() {
        return createddate;
    }

    public void setCreateddate(Date createddate) {
        this.createddate = createddate;
    }

    public Date getLastdisplayeddtae() {
        return lastdisplayeddtae;
    }

    public void setLastdisplayeddtae(Date lastdisplayeddtae) {
        this.lastdisplayeddtae = lastdisplayeddtae;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (newsid != null ? newsid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof News)) {
            return false;
        }
        News other = (News) object;
        if ((this.newsid == null && other.newsid != null) || (this.newsid != null && !this.newsid.equals(other.newsid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gb4w21.musicalmoose.News[ newsid=" + newsid + " ]";
    }

    
    /**
     * @return the shortened version of the news text for the front page.
     */
    public String getTruncatedNewsText(){
        return newstext.substring(0,Math.min(newstext.length(), 180)) + "...";
    }

    public Boolean getDisplayed() {
        return displayed;
    }

    public void setDisplayed(Boolean displayed) {
        this.displayed = displayed;
    }

    public String getNewtitle() {
        return newtitle;
    }

    public void setNewtitle(String newtitle) {
        this.newtitle = newtitle;
    }

    public String getNewstext() {
        return newstext;
    }

    public void setNewstext(String newstext) {
        this.newstext = newstext;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
