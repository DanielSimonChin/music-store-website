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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author owner
 */
@Entity
@Table(name = "album", catalog = "MUSICSTORAGE", schema = "")
@NamedQueries({
    @NamedQuery(name = "Album.findAll", query = "SELECT a FROM Album a"),
    @NamedQuery(name = "Album.findByAlbumid", query = "SELECT a FROM Album a WHERE a.albumid = :albumid"),
    @NamedQuery(name = "Album.findByAlbumtitle", query = "SELECT a FROM Album a WHERE a.albumtitle = :albumtitle"),
    @NamedQuery(name = "Album.findByReleasedate", query = "SELECT a FROM Album a WHERE a.releasedate = :releasedate"),
    @NamedQuery(name = "Album.findByArtist", query = "SELECT a FROM Album a WHERE a.artist = :artist"),
    @NamedQuery(name = "Album.findByRecordlabel", query = "SELECT a FROM Album a WHERE a.recordlabel = :recordlabel"),
    @NamedQuery(name = "Album.findByNumberoftracks", query = "SELECT a FROM Album a WHERE a.numberoftracks = :numberoftracks"),
    @NamedQuery(name = "Album.findByDateentered", query = "SELECT a FROM Album a WHERE a.dateentered = :dateentered"),
    @NamedQuery(name = "Album.findByAlbumimagefilenamebig", query = "SELECT a FROM Album a WHERE a.albumimagefilenamebig = :albumimagefilenamebig"),
    @NamedQuery(name = "Album.findByAlbumimagefilenamesmall", query = "SELECT a FROM Album a WHERE a.albumimagefilenamesmall = :albumimagefilenamesmall"),
    @NamedQuery(name = "Album.findByCostprice", query = "SELECT a FROM Album a WHERE a.costprice = :costprice"),
    @NamedQuery(name = "Album.findByListprice", query = "SELECT a FROM Album a WHERE a.listprice = :listprice"),
    @NamedQuery(name = "Album.findBySaleprice", query = "SELECT a FROM Album a WHERE a.saleprice = :saleprice"),
    @NamedQuery(name = "Album.findByPst", query = "SELECT a FROM Album a WHERE a.pst = :pst"),
    @NamedQuery(name = "Album.findByGst", query = "SELECT a FROM Album a WHERE a.gst = :gst"),
    @NamedQuery(name = "Album.findByHst", query = "SELECT a FROM Album a WHERE a.hst = :hst"),
    @NamedQuery(name = "Album.findByRemovalstatus", query = "SELECT a FROM Album a WHERE a.removalstatus = :removalstatus"),
    @NamedQuery(name = "Album.findByRemovaldate", query = "SELECT a FROM Album a WHERE a.removaldate = :removaldate")})
public class Album implements Serializable {

    @Size(max = 255)
    @Column(name = "ALBUMTITLE")
    private String albumtitle;
    @Size(max = 255)
    @Column(name = "ARTIST")
    private String artist;
    @Size(max = 255)
    @Column(name = "RECORDLABEL")
    private String recordlabel;
    @Size(max = 255)
    @Column(name = "ALBUMIMAGEFILENAMEBIG")
    private String albumimagefilenamebig;
    @Size(max = 255)
    @Column(name = "ALBUMIMAGEFILENAMESMALL")
    private String albumimagefilenamesmall;
    @Lob
    @Column(name = "IMAGECONTENTBIG")
    private byte[] imagecontentbig;
    @Lob
    @Column(name = "IMAGECONTENTSMALL")
    private byte[] imagecontentsmall;
    @OneToMany(mappedBy = "albumid")
    private List<Invoicedetail> invoicedetailList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ALBUMID")
    private Integer albumid;
    
    @Column(name = "RELEASEDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date releasedate;
    
    @Column(name = "NUMBEROFTRACKS")
    private Integer numberoftracks;
    @Column(name = "DATEENTERED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateentered;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "COSTPRICE")
    private Float costprice;
    @Column(name = "LISTPRICE")
    private Float listprice;
    @Column(name = "SALEPRICE")
    private Float saleprice;
    @Column(name = "PST")
    private Float pst;
    @Column(name = "GST")
    private Float gst;
    @Column(name = "HST")
    private Float hst;
    @Column(name = "REMOVALSTATUS")
    private Boolean removalstatus;
    @Column(name = "REMOVALDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date removaldate;
    @OneToMany(mappedBy = "albumid")
    private List<MusicTrack> musicTrackList;

    public Album() {
    }

    public Album(Integer albumid) {
        this.albumid = albumid;
    }

    public Integer getAlbumid() {
        return albumid;
    }

    public void setAlbumid(Integer albumid) {
        this.albumid = albumid;
    }


    public Date getReleasedate() {
        return releasedate;
    }

    public void setReleasedate(Date releasedate) {
        this.releasedate = releasedate;
    }


    public Integer getNumberoftracks() {
        return numberoftracks;
    }

    public void setNumberoftracks(Integer numberoftracks) {
        this.numberoftracks = numberoftracks;
    }

    public Date getDateentered() {
        return dateentered;
    }

    public void setDateentered(Date dateentered) {
        this.dateentered = dateentered;
    }


    public Float getCostprice() {
        return costprice;
    }

    public void setCostprice(Float costprice) {
        this.costprice = costprice;
    }

    public Float getListprice() {
        return listprice;
    }

    public void setListprice(Float listprice) {
        this.listprice = listprice;
    }

    public Float getSaleprice() {
        return saleprice;
    }

    public void setSaleprice(Float saleprice) {
        this.saleprice = saleprice;
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

    public Boolean getRemovalstatus() {
        return removalstatus;
    }

    public void setRemovalstatus(Boolean removalstatus) {
        this.removalstatus = removalstatus;
    }

    public Date getRemovaldate() {
        return removaldate;
    }

    public void setRemovaldate(Date removaldate) {
        this.removaldate = removaldate;
    }

    public List<MusicTrack> getMusicTrackList() {
        return musicTrackList;
    }

    public void setMusicTrackList(List<MusicTrack> musicTrackList) {
        this.musicTrackList = musicTrackList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (albumid != null ? albumid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Album)) {
            return false;
        }
        Album other = (Album) object;
        if ((this.albumid == null && other.albumid != null) || (this.albumid != null && !this.albumid.equals(other.albumid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gb4w21.musicalmoose.entities.Album[ albumid=" + albumid + " ]";
    }

    public String getAlbumtitle() {
        return albumtitle;
    }

    public void setAlbumtitle(String albumtitle) {
        this.albumtitle = albumtitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getRecordlabel() {
        return recordlabel;
    }

    public void setRecordlabel(String recordlabel) {
        this.recordlabel = recordlabel;
    }

    public String getAlbumimagefilenamebig() {
        return albumimagefilenamebig;
    }

    public void setAlbumimagefilenamebig(String albumimagefilenamebig) {
        this.albumimagefilenamebig = albumimagefilenamebig;
    }

    public String getAlbumimagefilenamesmall() {
        return albumimagefilenamesmall;
    }

    public void setAlbumimagefilenamesmall(String albumimagefilenamesmall) {
        this.albumimagefilenamesmall = albumimagefilenamesmall;
    }

    public byte[] getImagecontentbig() {
        return imagecontentbig;
    }

    public void setImagecontentbig(byte[] imagecontentbig) {
        this.imagecontentbig = imagecontentbig;
    }

    public byte[] getImagecontentsmall() {
        return imagecontentsmall;
    }

    public void setImagecontentsmall(byte[] imagecontentsmall) {
        this.imagecontentsmall = imagecontentsmall;
    }

    public List<Invoicedetail> getInvoicedetailList() {
        return invoicedetailList;
    }

    public void setInvoicedetailList(List<Invoicedetail> invoicedetailList) {
        this.invoicedetailList = invoicedetailList;
    }
    
}
