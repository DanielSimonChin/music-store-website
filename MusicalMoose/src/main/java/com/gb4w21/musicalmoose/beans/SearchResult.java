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
    public SearchResult() {
    }
    
    public SearchResult(String tracktitle, String musiccategory, String artist, Date releasedate,String albumimagefilenamesmall ) {
        this.tracktitle=tracktitle;
        this.musiccategory=musiccategory;
        this.artist=artist;
        this.releasedate=releasedate;
        this.albumimagefilenamesmall=albumimagefilenamesmall;
    }
    public SearchResult(String albumtitle, Date releasedate, String artist, String musiccategory,String albumimagefilenamesmall ) {
        this.albumtitle=albumtitle;
        this.musiccategory=musiccategory;
        this.artist=artist;
        this.releasedate=releasedate;
        this.albumimagefilenamesmall=albumimagefilenamesmall;
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
   
}
