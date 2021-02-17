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
    @NamedQuery(name = "Album.findByAlbumimagefilename", query = "SELECT a FROM Album a WHERE a.albumimagefilename = :albumimagefilename"),
    @NamedQuery(name = "Album.findByCostprice", query = "SELECT a FROM Album a WHERE a.costprice = :costprice"),
    @NamedQuery(name = "Album.findByListprice", query = "SELECT a FROM Album a WHERE a.listprice = :listprice"),
    @NamedQuery(name = "Album.findBySaleprice", query = "SELECT a FROM Album a WHERE a.saleprice = :saleprice"),
    @NamedQuery(name = "Album.findByRemovalstatus", query = "SELECT a FROM Album a WHERE a.removalstatus = :removalstatus"),
    @NamedQuery(name = "Album.findByRemovaldate", query = "SELECT a FROM Album a WHERE a.removaldate = :removaldate")})
public class Album implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ALBUMID")
    private Integer albumid;
    @Size(max = 255)
    @Column(name = "ALBUMTITLE")
    private String albumtitle;
    @Column(name = "RELEASEDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date releasedate;
    @Size(max = 255)
    @Column(name = "ARTIST")
    private String artist;
    @Size(max = 255)
    @Column(name = "RECORDLABEL")
    private String recordlabel;
    @Column(name = "NUMBEROFTRACKS")
    private Integer numberoftracks;
    @Column(name = "DATEENTERED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateentered;
    @Size(max = 255)
    @Column(name = "ALBUMIMAGEFILENAME")
    private String albumimagefilename;
    @Lob
    @Column(name = "IMAGECONTENT")
    private byte[] imagecontent;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "COSTPRICE")
    private Float costprice;
    @Column(name = "LISTPRICE")
    private Float listprice;
    @Column(name = "SALEPRICE")
    private Float saleprice;
    @Column(name = "REMOVALSTATUS")
    private Boolean removalstatus;
    @Column(name = "REMOVALDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date removaldate;
    @OneToMany(mappedBy = "albumid")
    private List<Salebralbum> salebralbumList;
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

    public String getAlbumtitle() {
        return albumtitle;
    }

    public void setAlbumtitle(String albumtitle) {
        this.albumtitle = albumtitle;
    }

    public Date getReleasedate() {
        return releasedate;
    }

    public void setReleasedate(Date releasedate) {
        this.releasedate = releasedate;
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

    public String getAlbumimagefilename() {
        return albumimagefilename;
    }

    public void setAlbumimagefilename(String albumimagefilename) {
        this.albumimagefilename = albumimagefilename;
    }

    public byte[] getImagecontent() {
        return imagecontent;
    }

    public void setImagecontent(byte[] imagecontent) {
        this.imagecontent = imagecontent;
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

    public List<Salebralbum> getSalebralbumList() {
        return salebralbumList;
    }

    public void setSalebralbumList(List<Salebralbum> salebralbumList) {
        this.salebralbumList = salebralbumList;
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
    
}
