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
public class ShoppingCartItem {
//    private List<MusicTrack> cartMusicTracks;
//    private List<Album> cartAlbums;
//    private int totalPrice;
    
    private int id;
    private String title;
    private String artist;
    private float price;
    private boolean isAlbum;
    private String imgNameSmall;
    
    public ShoppingCartItem() {
        this.id = -1;
        this.title = "";
        this.artist = "";
        this.price = -1;
        this.isAlbum = false;
        this.imgNameSmall = "";
    }
    
    public ShoppingCartItem(int id, String title, String artist, float price, boolean isAlbum, String imgNameSmall) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.price = price;
        this.isAlbum = isAlbum;
        this.imgNameSmall = imgNameSmall;
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
    
    public String getImgNameSmall() {
        return this.imgNameSmall;
    }
    
    public void setImgNameSmall(String imgNameSmall) {
        this.imgNameSmall = imgNameSmall;
    }
    
//    public ShoppingCart(List<MusicTrack> cartMusicTracks, List<Album> cartAlbums, int totalPrice) {
//        this.cartMusicTracks = cartMusicTracks;
//        this.cartAlbums = cartAlbums;
//        this.totalPrice = totalPrice;
//    }
    
//    public List<MusicTrack> getCartMusicTracks() {
//        return this.cartMusicTracks;
//    }
//    
//    public void setCartMusicTracks(List<MusicTrack> cartMusicTracks) {
//        this.cartMusicTracks = cartMusicTracks;
//    }
//    
//    public List<Album> getCartAlbums() {
//        return this.cartAlbums;
//    }
//    
//    public void setCartAlbums(List<Album> cartAlbums) {
//        this.cartAlbums = cartAlbums;
//    }
//    
//    public int getTotalPrice(){
//        return this.totalPrice;
//    }
//    
//    public void setTotalPrice(int totalPrice){
//        this.totalPrice = totalPrice;
//    }
}
