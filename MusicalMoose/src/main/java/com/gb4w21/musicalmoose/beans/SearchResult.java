/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.beans;

import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.persistence.TypedQuery;

/**
 *
 * @author owner
 */

@Named(value = "searchResult")
@Dependent
public class SearchResult {

   private String albumimagefilenamesmall;
   private String tracktitle;
   private String albumtitle;
   private Date releasedate;
   private String artist;
   private String musiccategory;
   private int inventoryid;
   private int albumid;
   
    public SearchResult() {
    }
  
    public SearchResult(String tracktitle, String musiccategory, String artist, Date releasedate,String albumimagefilenamesmall,  int inventoryid) {
        this.tracktitle=tracktitle;
        this.musiccategory=musiccategory;
        this.artist=artist;
        this.releasedate=releasedate;
        this.albumimagefilenamesmall=albumimagefilenamesmall;
        this.inventoryid = inventoryid;
    }
    public SearchResult(String albumtitle, Date releasedate, String artist, String musiccategory,String albumimagefilenamesmall, int albumid) {
        this.albumtitle=albumtitle;
        this.musiccategory=musiccategory;
        this.artist=artist;
        this.releasedate=releasedate;
        this.albumimagefilenamesmall=albumimagefilenamesmall;
        this.albumid = albumid;
    }

    public int getInventoryid(){
        return inventoryid;
    }
    public int getAlbumid(){
        return albumid;
    }
    public void setInventoryid(int inventoryid){
        this.inventoryid=inventoryid;
    }
    public void setAlbumid(int albumid){
        this.albumid=albumid;
    }
   public String getAlbumimagefilenamesmall(){
       return albumimagefilenamesmall;
   }
   public void setAlbumimagefilenamesmall(String albumimagefilenamesmall){
       this.albumimagefilenamesmall=albumimagefilenamesmall;
   }
   public String getTracktitle(){
       return tracktitle;
   }
   public void setTracktitle(String tracktitle){
       this.tracktitle=tracktitle;
   }
   public String getAlbumtitle(){
       return albumtitle;
   }
   public void setAlbumtitle(String albumtitle){
       this.albumtitle=albumtitle;
   }
   public Date getReleasedate(){
       return releasedate;
   }
   public void setReleasedate(Date releasedate){
       this.releasedate=releasedate;
   }
   public String getArtist(){
       return artist;
   }
   public void setArtist(String artist){
       this.artist=artist;
   }
   public String getMusiccategory(){
       return musiccategory;
   }
   public void setMusiccategory(String musiccategory){
       this.musiccategory=musiccategory;
   }
   public String getTitle(){
       if (tracktitle!=null&& !tracktitle.equals("")){
           return tracktitle;
       }
       if (albumtitle!=null&& !albumtitle.equals("")){
           return albumtitle;
       }
       return "";
   }
  
}
