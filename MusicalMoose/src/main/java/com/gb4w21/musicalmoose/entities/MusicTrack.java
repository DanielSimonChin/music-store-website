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
 * @author Daniel
 */
@Entity
@Table(name = "music_track", catalog = "", schema = "")
@NamedQueries({
    @NamedQuery(name = "MusicTrack.findAll", query = "SELECT m FROM MusicTrack m"),
    @NamedQuery(name = "MusicTrack.findByInventoryid", query = "SELECT m FROM MusicTrack m WHERE m.inventoryid = :inventoryid"),
    @NamedQuery(name = "MusicTrack.findByTracktitle", query = "SELECT m FROM MusicTrack m WHERE m.tracktitle = :tracktitle"),
    @NamedQuery(name = "MusicTrack.findByArtist", query = "SELECT m FROM MusicTrack m WHERE m.artist = :artist"),
    @NamedQuery(name = "MusicTrack.findBySongwriter", query = "SELECT m FROM MusicTrack m WHERE m.songwriter = :songwriter"),
    @NamedQuery(name = "MusicTrack.findByPlaylength", query = "SELECT m FROM MusicTrack m WHERE m.playlength = :playlength"),
    @NamedQuery(name = "MusicTrack.findBySelectionnumber", query = "SELECT m FROM MusicTrack m WHERE m.selectionnumber = :selectionnumber"),
    @NamedQuery(name = "MusicTrack.findByMusiccategory", query = "SELECT m FROM MusicTrack m WHERE m.musiccategory = :musiccategory"),
    @NamedQuery(name = "MusicTrack.findByAlbumimagefilenamebig", query = "SELECT m FROM MusicTrack m WHERE m.albumimagefilenamebig = :albumimagefilenamebig"),
    @NamedQuery(name = "MusicTrack.findByAlbumimagefilenamesmall", query = "SELECT m FROM MusicTrack m WHERE m.albumimagefilenamesmall = :albumimagefilenamesmall"),
    @NamedQuery(name = "MusicTrack.findByCostprice", query = "SELECT m FROM MusicTrack m WHERE m.costprice = :costprice"),
    @NamedQuery(name = "MusicTrack.findByListprice", query = "SELECT m FROM MusicTrack m WHERE m.listprice = :listprice"),
    @NamedQuery(name = "MusicTrack.findBySaleprice", query = "SELECT m FROM MusicTrack m WHERE m.saleprice = :saleprice"),
    @NamedQuery(name = "MusicTrack.findByPst", query = "SELECT m FROM MusicTrack m WHERE m.pst = :pst"),
    @NamedQuery(name = "MusicTrack.findByGst", query = "SELECT m FROM MusicTrack m WHERE m.gst = :gst"),
    @NamedQuery(name = "MusicTrack.findByHst", query = "SELECT m FROM MusicTrack m WHERE m.hst = :hst"),
    @NamedQuery(name = "MusicTrack.findByDateentered", query = "SELECT m FROM MusicTrack m WHERE m.dateentered = :dateentered"),
    @NamedQuery(name = "MusicTrack.findByPartofalbum", query = "SELECT m FROM MusicTrack m WHERE m.partofalbum = :partofalbum"),
    @NamedQuery(name = "MusicTrack.findByRemovalstatus", query = "SELECT m FROM MusicTrack m WHERE m.removalstatus = :removalstatus"),
    @NamedQuery(name = "MusicTrack.findByRemovaldate", query = "SELECT m FROM MusicTrack m WHERE m.removaldate = :removaldate")})
public class MusicTrack implements Serializable {

    @Size(max = 255)
    @Column(name = "TRACKTITLE")
    private String tracktitle;
    @Size(max = 255)
    @Column(name = "ARTIST")
    private String artist;
    @Size(max = 255)
    @Column(name = "SONGWRITER")
    private String songwriter;
    @Size(max = 255)
    @Column(name = "MUSICCATEGORY")
    private String musiccategory;
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
    @OneToMany(mappedBy = "inventoryid")
    private List<Invoicedetail> invoicedetailList;
    @OneToMany(mappedBy = "inventoryid")
    private List<Review> reviewList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "INVENTORYID")
    private Integer inventoryid;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PLAYLENGTH")
    private Float playlength;
    @Column(name = "SELECTIONNUMBER")
    private Integer selectionnumber;
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

    public String getPartOfAlbumStringFormat() {
        if (partofalbum) {
            return "Part of album";
        }
        return "Single";
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

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
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

    public String getMusiccategory() {
        return musiccategory;
    }

    public void setMusiccategory(String musiccategory) {
        this.musiccategory = musiccategory;
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
