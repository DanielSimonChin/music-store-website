/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.beans.SearchResult;
import com.gb4w21.musicalmoose.controller.AlbumJpaController;
import com.gb4w21.musicalmoose.controller.BanneradJpaController;
import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Individual search categories
 *
 * @author Alessandro Dare
 */
enum SearchCategory {
    Date,
    TrackName,
    AlbumTitle,
    Artist
}

/**
 * Controller responsible for handling the search bar feature on the client side
 * that finds and displays the list of search results based on the search field
 * and category
 *
 * @author Alessandro Dare
 * @version 1.0
 */
@Named
@SessionScoped
public class SearchController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(BanneradJpaController.class);
    private String searchText;
    private Date toDate;
    private Date fromDate;
    private List<SearchResult> searchResultsTrack = new ArrayList<SearchResult>();
    private List<SearchResult> searchResultsAlbum = new ArrayList<SearchResult>();
    @Inject
    private AlbumJpaController albumJpaController;
    @Inject
    private MusicTrackJpaController musicTrackJpaController;
    private String category = "TrackName";
    private boolean searchError;
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Default Constructor
     */

    public SearchController() {

    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public boolean getSearchError() {
        return searchError;
    }

    public void setSearchError(boolean searchError) {
        this.searchError = searchError;
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

    public List<SearchResult> getSearchResultsTrack() {

        return searchResultsTrack;
    }

    public void setSearchResultsTrack(List<SearchResult> searchResultsTrack) {
        this.searchResultsTrack = searchResultsTrack;

    }

    public List<SearchResult> getSearchResultsAlbum() {

        return searchResultsAlbum;
    }

    public void setSearchResultsAlbum(List<SearchResult> searchResultsAlbum) {
        this.searchResultsAlbum = searchResultsAlbum;

    }

    /**
     * clears all search results
     *
     * @author Alessandro Dare
     */
    private void clearSearchResult() {
        searchResultsTrack = new ArrayList<SearchResult>();
        searchResultsAlbum = new ArrayList<SearchResult>();
    }

    /**
     * Generates search results base on selected categories and takes a user to
     * a search page unless it gets a single results in which case it takes you
     * directly tot eh track/album page
     *
     * @author Alessandro Dare
     * @return String Search page or track/album page if there's a single result
     * @throws Exception
     */
    public String searchForPage() throws Exception {
        clearSearchResult();
        LOG.info("category" + category);
        LOG.info("search text:" + this.searchText);
        //different searches based ont eh category
        if (category.equals(SearchCategory.AlbumTitle.toString())) {
            searchResultsAlbums();
        } else if (category.equals(SearchCategory.Artist.toString())) {
            searchResultsArtist();
        } else if (category.equals(SearchCategory.Date.toString())) {

            searchResultsDate();

        } else if (category.equals(SearchCategory.TrackName.toString())) {
            searchResultsMusicTrack();
        }
        searchText = "";

        fromDate = null;
        toDate = null;
        //no resuls returns and an error
        if (searchResultsAlbum.size() + searchResultsTrack.size() == 0) {
            searchText = "";
            searchError = true;
            return null;
        }
        searchError = false;
        //if there's a single album take the user to the album page
        if (searchResultsTrack.size() == 0 && searchResultsAlbum.size() == 1) {
            setSingleAlbum();
            return "albumpage";
            //if there's a single track take the user to the track page 
        } else if (searchResultsTrack.size() == 1 && searchResultsAlbum.size() == 0) {
            setSingleTrack();

            return "trackpage";
        }
        return "searchPage";

    }

    /**
     * If the search list has just one result and it's a track take the user to
     * the selected track page
     *
     * @author Alessandro Dare
     */
    private void setSingleTrack() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        cq.select(musicTrack);
        LOG.info("Single track title:" + searchResultsAlbum.get(0).getTracktitle());
        // Use single search reuslt to find track
        cq.where(cb.equal(musicTrack.get("tracktitle"), searchResultsTrack.get(0).getTracktitle()),
                cb.equal(musicTrack.get("artist"), searchResultsTrack.get(0).getArtist()),
                cb.equal(musicTrack.get("musiccategory"), searchResultsTrack.get(0).getMusiccategory()),
                cb.equal(musicTrack.get("available"), 1));

        TypedQuery<MusicTrack> query = entityManager.createQuery(cq);

        musicTrackJpaController.setMusicTrack(query.getSingleResult());

    }

    /**
     * If the search list has just one result and it's n album take the user to
     * the selected album page
     *
     * @author Alessandro Dare
     */
    private void setSingleAlbum() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Album> cq = cb.createQuery(Album.class);
        Root<Album> album = cq.from(Album.class);
        cq.select(album);
        LOG.info("Single ablum title:" + searchResultsAlbum.get(0).getAlbumtitle());
        // Use single search reuslt to find album
        cq.where(cb.equal(album.get("albumtitle"), searchResultsAlbum.get(0).getAlbumtitle()),
                cb.equal(album.get("artist"), searchResultsAlbum.get(0).getArtist()),
                cb.equal(album.get("releasedate"), searchResultsAlbum.get(0).getReleasedate()),
                cb.equal(album.get("available"), 1));

        TypedQuery<Album> query = entityManager.createQuery(cq);
        albumJpaController.setSelectedAlbum(query.getSingleResult());

    }

    /**
     * Generates a list of tracks that have the same title or the title name is
     * similar to the search string
     *
     * @author Alessandro Dare
     */
    private void searchResultsMusicTrack() {
        searchText = "%" + searchText + "%";

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchResult> cq = cb.createQuery(SearchResult.class);
        Root<Album> album = cq.from(Album.class);
        Join musicTrack = album.join("musicTrackList");
        cq.where(cb.like(musicTrack.get("tracktitle"), searchText), cb.equal(musicTrack.get("available"), 1));
        cq.select(cb.construct(SearchResult.class, musicTrack.get("tracktitle"), musicTrack.get("musiccategory"), musicTrack.get("artist"), album.get("releasedate"), album.get("albumimagefilenamesmall"), musicTrack.get("inventoryid"))).distinct(true);
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchResultsTrack = query.getResultList();
        LOG.info("all tracks:" + searchResultsAlbum.size());
    }

    /**
     * Generates a list of albums that have the same title or the title name is
     * similar to the search string
     *
     * @author Alessandro Dare
     */
    private void searchResultsAlbums() {
        searchText = "%" + searchText + "%";
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchResult> cq = cb.createQuery(SearchResult.class);
        Root<Album> album = cq.from(Album.class);
        Join musicTrack = album.join("musicTrackList");
        cq.where(cb.like(album.get("albumtitle"), searchText), cb.equal(album.get("available"), 1));
        cq.select(cb.construct(SearchResult.class, album.get("albumtitle"), album.get("releasedate"), album.get("artist"), musicTrack.get("musiccategory"), album.get("albumimagefilenamesmall"), album.get("albumid"))).distinct(true);
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchResultsAlbum = query.getResultList();
        LOG.info("all albums:" + searchResultsAlbum.size());
    }

    /**
     * Generates a list of albums and tracks who's creation date is in between
     * the specified field
     *
     * @author Alessandro Dare
     * @throws ParseException
     */
    private void searchResultsDate() throws ParseException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchResult> cq = cb.createQuery(SearchResult.class);
        //searching for albums within date range
        Root<Album> album = cq.from(Album.class);
        Join musicTrack = album.join("musicTrackList");
        cq.where(cb.between(album.get("dateentered"), fromDate, toDate), cb.equal(album.get("available"), 1));
        cq.select(cb.construct(SearchResult.class, album.get("albumtitle"), album.get("releasedate"), album.get("artist"), musicTrack.get("musiccategory"), album.get("albumimagefilenamesmall"), album.get("albumid"))).distinct(true);
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchResultsAlbum = query.getResultList();
        LOG.info("datess all albums:" + searchResultsAlbum.size());
        //searching for tracks within date range
        album = cq.from(Album.class);
        musicTrack = album.join("musicTrackList");
        cq.where(cb.between(album.get("dateentered"), fromDate, toDate), cb.equal(musicTrack.get("available"), 1));
        cq.select(cb.construct(SearchResult.class, musicTrack.get("tracktitle"), musicTrack.get("musiccategory"), musicTrack.get("artist"), album.get("releasedate"), album.get("albumimagefilenamesmall"), musicTrack.get("inventoryid"))).distinct(true);
        query = entityManager.createQuery(cq);
        searchResultsTrack.addAll(query.getResultList());
        LOG.info("datess all tracks:" + query.getResultList().size());
    }

    /**
     * Generates a list of albums and tracks that have the same artist or the
     * artists name is similar to the search string
     *
     * @author Alessandro Dare
     */
    private void searchResultsArtist() {
        searchText = "%" + searchText + "%";
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchResult> cq = cb.createQuery(SearchResult.class);
        //searching for albums with artist
        Root<Album> album = cq.from(Album.class);
        Join musicTrack = album.join("musicTrackList");
        cq.where(cb.like(album.get("artist"), searchText), cb.equal(album.get("available"), 1));
        cq.select(cb.construct(SearchResult.class, album.get("albumtitle"), album.get("releasedate"), album.get("artist"), musicTrack.get("musiccategory"), album.get("albumimagefilenamesmall"), album.get("albumid"))).distinct(true);
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchResultsAlbum = query.getResultList();
        LOG.info("Arists all albums:" + searchResultsAlbum.size());
        //search for tracks with artist
        album = cq.from(Album.class);
        musicTrack = album.join("musicTrackList");
        cq.where(cb.like(musicTrack.get("artist"), searchText), cb.equal(musicTrack.get("available"), 1));
        cq.select(cb.construct(SearchResult.class, musicTrack.get("tracktitle"), musicTrack.get("musiccategory"), musicTrack.get("artist"), album.get("releasedate"), album.get("albumimagefilenamesmall"), musicTrack.get("inventoryid"))).distinct(true);
        query = entityManager.createQuery(cq);
        searchResultsTrack.addAll(query.getResultList());
        LOG.info("Arists all tracks:" + query.getResultList().size());
    }

}
