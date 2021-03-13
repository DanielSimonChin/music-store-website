/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.beans.SearchResult;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

enum SearchCategory {
    Date,
    TrackName,
    AlbumTitle,
    Artist
}

/**
 *
 * @author owner
 */
@Named
@RequestScoped
public class SearchController implements Serializable {

    private String searchText;
    private List<SearchResult> searchResults = new ArrayList<SearchResult>();
    private String category;
    private Album album;
    private MusicTrack musicTrack;
    @PersistenceContext
    private EntityManager entityManager;

    public SearchController() {

    }
    public Album getAlbum() {
        return album;
    }
    public MusicTrack getMusicTrack() {
        return musicTrack;
    }
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public List<SearchResult> getSearchResults() {
       /** List<SearchResult> searchResults = new ArrayList<SearchResult>();
        for (SearchResult searchResult : this.searchResults) {
            SearchResult newResult = new SearchResult();
            newResult.setAlbumimagefilenamebig(searchResult.getAlbumimagefilenamebig());
            newResult.setAlbumtitle(searchResult.getAlbumtitle());
            newResult.setArtist(searchResult.getArtist());
            newResult.setMusiccategory(searchResult.getMusiccategory());
            newResult.setReleasedate(searchResult.getReleasedate());
            newResult.setTracktitle(searchResult.getTracktitle());
            searchResults.add(newResult);
        }*/
        return searchResults;
    }

    public void setSearchResults(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
       /**  this.searchResults = new ArrayList<SearchResult>();
        for (SearchResult searchResult : searchResults) {
            SearchResult newResult = new SearchResult();
            newResult.setAlbumimagefilenamebig(searchResult.getAlbumimagefilenamebig());
            newResult.setAlbumtitle(searchResult.getAlbumtitle());
            newResult.setArtist(searchResult.getArtist());
            newResult.setMusiccategory(searchResult.getMusiccategory());
            newResult.setReleasedate(searchResult.getReleasedate());
            newResult.setTracktitle(searchResult.getTracktitle());
            this.searchResults.add(newResult);
        }*/

    }

    public String searchForPage() throws Exception {
        

        if (category.equals(SearchCategory.AlbumTitle.toString())) {
            searchResultsAlbums();
        } else if (category.equals(SearchCategory.Artist.toString())) {
            searchResultsArtist();
        } else if (category.equals(SearchCategory.Date.toString())) {
            searchResultsDate();
        } else if (category.equals(SearchCategory.TrackName.toString())) {
            searchResultsMusicTrack();
        }
        if(searchResults.size()==1){
            
            if(searchResults.get(0).getAlbumtitle()!=null&& searchResults.get(0).getAlbumtitle().equals("")){
                setSingleAlbum();
                return "albumpageSearch";
            }
            else{
                setSingleTrack();
                return "trackpageSearch";
            }
        }
        
        return "searchPage";

    }
    private void setSingleTrack(){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        cq.select(musicTrack);
            // Use String to refernce a field
        cq.where(cb.equal(musicTrack.get("tracktitle"), searchResults.get(0).getTracktitle()), 
                cb.equal(musicTrack.get("artist"), searchResults.get(0).getArtist()),
                cb.equal(musicTrack.get("musiccategory"), searchResults.get(0).getMusiccategory()));
       
        TypedQuery<MusicTrack> query = entityManager.createQuery(cq);
        this.musicTrack = query.getSingleResult();
    }
    private void setSingleAlbum(){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Album> cq = cb.createQuery(Album.class);
        Root<Album> album = cq.from(Album.class);
        cq.select(album);
            // Use String to refernce a field
        cq.where(cb.equal(album.get("albumtitle"), searchResults.get(0).getAlbumtitle()), 
                cb.equal(album.get("artist"), searchResults.get(0).getArtist()),
                cb.equal(album.get("releasedate"), searchResults.get(0).getReleasedate()));
       
        TypedQuery<Album> query = entityManager.createQuery(cq);
        this.album = query.getSingleResult();
    }

    private void searchResultsMusicTrack() {
        searchText = "%" + searchText + "%";
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchResult> cq = cb.createQuery(SearchResult.class);
        Root<Album> album = cq.from(Album.class);
        Join musicTrack = album.join("musicTrackList");
        cq.where(cb.like(musicTrack.get("tracktitle"), searchText));
        cq.select(cb.construct(SearchResult.class, musicTrack.get("tracktitle"), musicTrack.get("musiccategory"), musicTrack.get("artist"), album.get("releasedate"), album.get("albumimagefilenamesmall")));
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchResults = query.getResultList();

    }

    private void searchResultsAlbums() {
        searchText = "%" + searchText + "%";
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchResult> cq = cb.createQuery(SearchResult.class);
        Root<Album> album = cq.from(Album.class);
        Join musicTrack = album.join("musicTrackList");
        cq.where(cb.like(album.get("albumtitle"), searchText));
        cq.select(cb.construct(SearchResult.class, album.get("albumtitle"), album.get("releasedate"), album.get("artist"), musicTrack.get("musiccategory"), album.get("albumimagefilenamesmall")));
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchResults = query.getResultList();

    }

    private void searchResultsDate() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = formatter.parse(searchText);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchResult> cq = cb.createQuery(SearchResult.class);

        Root<Album> album = cq.from(Album.class);
        Join musicTrack = album.join("musicTrackList");
        cq.where(cb.greaterThan(album.get("dateentered"), date));
        cq.select(cb.construct(SearchResult.class, album.get("albumtitle"), album.get("releasedate"), album.get("artist"), musicTrack.get("musiccategory"), album.get("albumimagefilenamesmall")));
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchResults = query.getResultList();

        album = cq.from(Album.class);
        musicTrack = album.join("musicTrackList");
        cq.where(cb.greaterThan(musicTrack.get("dateentered"), date));
        cq.select(cb.construct(SearchResult.class, musicTrack.get("tracktitle"), musicTrack.get("musiccategory"), musicTrack.get("artist"), album.get("releasedate"), album.get("albumimagefilenamesmall")));
        query = entityManager.createQuery(cq);
        searchResults.addAll(query.getResultList());

    }

    private void searchResultsArtist() {
        searchText = "%" + searchText + "%";
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchResult> cq = cb.createQuery(SearchResult.class);

        Root<Album> album = cq.from(Album.class);
        Join musicTrack = album.join("musicTrackList");
        cq.where(cb.like(album.get("artist"), searchText));
        cq.select(cb.construct(SearchResult.class, album.get("albumtitle"), album.get("releasedate"), album.get("artist"), musicTrack.get("musiccategory"), album.get("albumimagefilenamesmall")));
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchResults = query.getResultList();

        album = cq.from(Album.class);
        musicTrack = album.join("musicTrackList");
        cq.where(cb.like(musicTrack.get("albumtitle"), searchText));
        cq.select(cb.construct(SearchResult.class, musicTrack.get("tracktitle"), musicTrack.get("musiccategory"), musicTrack.get("artist"), album.get("releasedate"), album.get("albumimagefilenamesmall")));
        query = entityManager.createQuery(cq);
        searchResults.addAll(query.getResultList());

    }
}
