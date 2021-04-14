/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.beans;

import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

/**
 *
 * @author victo
 */
@Named(value = "shoppingCart")
@Dependent
public class MusicItem {
//    private List<MusicTrack> cartMusicTracks;
//    private List<Album> cartAlbums;
//    private int totalPrice;
    
    private int id;
    private String title;
    private String artist;
    private float price;
    private boolean isAlbum;
    private String imgNameBig;
    private String genre;
    private Float songLength;
    private int numberOfTracks;
    private int invoiceId;
    public MusicItem() {
        this.id = -1;
        this.title = "";
        this.artist = "";
        this.price = -1;
        this.isAlbum = false;
        this.imgNameBig = "";
        this.genre = "";
        this.songLength = 0f;
        this.numberOfTracks = 0;
        this.invoiceId=-1;
    }
    
    public MusicItem(int id, String title, String artist, float price, boolean isAlbum, String imgNameSmall, String genre, Float songLength, int numberOfTracks) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.price = price;
        this.isAlbum = isAlbum;
        this.imgNameBig = imgNameSmall;
        this.genre = genre;
        this.songLength = songLength;
        this.numberOfTracks = numberOfTracks;
        this.invoiceId=-1;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getArtiste() {
        return artist;
    }
    
    public void setArtist(String artist) {
        this.artist = artist;
    }
    
    public float getPrice() {
        return price;
    }
    
    public void setPrice(float price) {
        this.price = price;
    }
    
    public boolean getIsAlbum() {
        return isAlbum;
    }
    
    public void setIsAlbum(boolean isAlbum) {
        this.isAlbum = isAlbum;
    }
    
    public String getImgNameBig() {
        return this.imgNameBig;
    }
    
    public void setImgNameBig(String imgNameBig) {
        this.imgNameBig = imgNameBig;
    }
    
    public String getGenre() {
        return this.genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public Float getSongLength() {
        return this.songLength;
    }
    
    public void setSongLength(Float songLength) {
        this.songLength = songLength;
    }
    
    public int getNumberOfTracks() {
        return this.numberOfTracks;
    }
    
    public void setNumberOfTracks(int numberOfTracks) {
        this.numberOfTracks = numberOfTracks;
    }
    public int getInvoiceId(){
        return this.invoiceId;
    }
    public void setInvoiceId(int invoiceId){
        this.invoiceId=invoiceId;
    }
}
