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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
@Table(name = "music_track", catalog = "MUSICSTORAGE", schema = "")
@NamedQueries({
    @NamedQuery(name = "MusicTrack.findAll", query = "SELECT m FROM MusicTrack m"),
    @NamedQuery(name = "MusicTrack.findByInventoryid", query = "SELECT m FROM MusicTrack m WHERE m.inventoryid = :inventoryid"),
    @NamedQuery(name = "MusicTrack.findByTracktitle", query = "SELECT m FROM MusicTrack m WHERE m.tracktitle = :tracktitle"),
    @NamedQuery(name = "MusicTrack.findByArtist", query = "SELECT m FROM MusicTrack m WHERE m.artist = :artist"),
    @NamedQuery(name = "MusicTrack.findBySongwriter", query = "SELECT m FROM MusicTrack m WHERE m.songwriter = :songwriter"),
    @NamedQuery(name = "MusicTrack.findByPlaylength", query = "SELECT m FROM MusicTrack m WHERE m.playlength = :playlength"),
    @NamedQuery(name = "MusicTrack.findBySelectionnumber", query = "SELECT m FROM MusicTrack m WHERE m.selectionnumber = :selectionnumber"),
    @NamedQuery(name = "MusicTrack.findByMusiccategory", query = "SELECT m FROM MusicTrack m WHERE m.musiccategory = :musiccategory"),
    @NamedQuery(name = "MusicTrack.findByAlbumimagefilename", query = "SELECT m FROM MusicTrack m WHERE m.albumimagefilename = :albumimagefilename"),
    @NamedQuery(name = "MusicTrack.findByCostprice", query = "SELECT m FROM MusicTrack m WHERE m.costprice = :costprice"),
    @NamedQuery(name = "MusicTrack.findByListprice", query = "SELECT m FROM MusicTrack m WHERE m.listprice = :listprice"),
    @NamedQuery(name = "MusicTrack.findBySaleprice", query = "SELECT m FROM MusicTrack m WHERE m.saleprice = :saleprice"),
    @NamedQuery(name = "MusicTrack.findByDateentered", query = "SELECT m FROM MusicTrack m WHERE m.dateentered = :dateentered"),
    @NamedQuery(name = "MusicTrack.findByPartofalbum", query = "SELECT m FROM MusicTrack m WHERE m.partofalbum = :partofalbum"),
    @NamedQuery(name = "MusicTrack.findByRemovalstatus", query = "SELECT m FROM MusicTrack m WHERE m.removalstatus = :removalstatus"),
    @NamedQuery(name = "MusicTrack.findByRemovaldate", query = "SELECT m FROM MusicTrack m WHERE m.removaldate = :removaldate")})
public class MusicTrack implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "INVENTORYID")
    private Integer inventoryid;
    @Size(max = 255)
    @Column(name = "TRACKTITLE")
    private String tracktitle;
    @Size(max = 255)
    @Column(name = "ARTIST")
    private String artist;
    @Size(max = 255)
    @Column(name = "SONGWRITER")
    private String songwriter;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PLAYLENGTH")
    private Float playlength;
    @Column(name = "SELECTIONNUMBER")
    private Integer selectionnumber;
    @Size(max = 255)
    @Column(name = "MUSICCATEGORY")
    private String musiccategory;
    @Size(max = 255)
    @Column(name = "ALBUMIMAGEFILENAME")
    private String albumimagefilename;
    @Lob
    @Column(name = "IMAGECONTENT")
    private byte[] imagecontent;
    @Column(name = "COSTPRICE")
    private Float costprice;
    @Column(name = "LISTPRICE")
    private Float listprice;
    @Column(name = "SALEPRICE")
    private Float saleprice;
    @Column(name = "DATEENTERED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateentered;
    @Column(name = "PARTOFALBUM")
    private Boolean partofalbum;
    @Column(name = "REMOVALSTATUS")
    private Boolean removalstatus;
    @Column(name = "REMOVALDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date removaldate;
    @JoinColumn(name = "ALBUMID", referencedColumnName = "ALBUMID")
    @ManyToOne
    private Album albumid;
    @OneToMany(mappedBy = "inventoryid")
    private List<Salebrtrack> salebrtrackList;

    public MusicTrack() {
    }

    public MusicTrack(Integer inventoryid) {
        this.inventoryid = inventoryid;
    }

    public Integer getInventoryid() {
        return inventoryid;
    }

    public void setInventoryid(Integer inventoryid) {
        this.inventoryid = inventoryid;
    }

    public String getTracktitle() {
        return tracktitle;
    }

    public void setTracktitle(String tracktitle) {
        this.tracktitle = tracktitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSongwriter() {
        return songwriter;
    }

    public void setSongwriter(String songwriter) {
        this.songwriter = songwriter;
    }

    public Float getPlaylength() {
        return playlength;
    }

    public void setPlaylength(Float playlength) {
        this.playlength = playlength;
    }

    public Integer getSelectionnumber() {
        return selectionnumber;
    }

    public void setSelectionnumber(Integer selectionnumber) {
        this.selectionnumber = selectionnumber;
    }

    public String getMusiccategory() {
        return musiccategory;
    }

    public void setMusiccategory(String musiccategory) {
        this.musiccategory = musiccategory;
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

    public Date getDateentered() {
        return dateentered;
    }

    public void setDateentered(Date dateentered) {
        this.dateentered = dateentered;
    }

    public Boolean getPartofalbum() {
        return partofalbum;
    }

    public void setPartofalbum(Boolean partofalbum) {
        this.partofalbum = partofalbum;
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

    public Album getAlbumid() {
        return albumid;
    }

    public void setAlbumid(Album albumid) {
        this.albumid = albumid;
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
        hash += (inventoryid != null ? inventoryid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MusicTrack)) {
            return false;
        }
        MusicTrack other = (MusicTrack) object;
        if ((this.inventoryid == null && other.inventoryid != null) || (this.inventoryid != null && !this.inventoryid.equals(other.inventoryid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gb4w21.musicalmoose.entities.MusicTrack[ inventoryid=" + inventoryid + " ]";
    }
    
}
