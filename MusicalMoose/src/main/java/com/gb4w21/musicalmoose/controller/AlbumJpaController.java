/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.business.PreRenderViewBean;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.entities.Album;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class AlbumJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(AlbumJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    private Album selectedAlbum;

    private PreRenderViewBean preRenderViewBean = new PreRenderViewBean();

    public void create(Album album) throws RollbackFailureException {
        if (album.getMusicTrackList() == null) {
            album.setMusicTrackList(new ArrayList<MusicTrack>());
        }
        try {
            utx.begin();
            List<MusicTrack> attachedMusicTrackList = new ArrayList<MusicTrack>();
            for (MusicTrack musicTrackListMusicTrackToAttach : album.getMusicTrackList()) {
                musicTrackListMusicTrackToAttach = em.getReference(musicTrackListMusicTrackToAttach.getClass(), musicTrackListMusicTrackToAttach.getInventoryid());
                attachedMusicTrackList.add(musicTrackListMusicTrackToAttach);
            }
            album.setMusicTrackList(attachedMusicTrackList);
            em.persist(album);
            for (MusicTrack musicTrackListMusicTrack : album.getMusicTrackList()) {
                Album oldAlbumidOfMusicTrackListMusicTrack = musicTrackListMusicTrack.getAlbumid();
                musicTrackListMusicTrack.setAlbumid(album);
                musicTrackListMusicTrack = em.merge(musicTrackListMusicTrack);
                if (oldAlbumidOfMusicTrackListMusicTrack != null) {
                    oldAlbumidOfMusicTrackListMusicTrack.getMusicTrackList().remove(musicTrackListMusicTrack);
                    oldAlbumidOfMusicTrackListMusicTrack = em.merge(oldAlbumidOfMusicTrackListMusicTrack);
                }
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
                LOG.error("Rollback");
            } catch (IllegalStateException | SecurityException | SystemException re) {
                LOG.error("Rollback2");

                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
        }
    }

    public void edit(Album album) throws NonexistentEntityException, Exception {
        try {

            utx.begin();
            Album persistentAlbum = em.find(Album.class, album.getAlbumid());
            List<MusicTrack> musicTrackListOld = persistentAlbum.getMusicTrackList();
            List<MusicTrack> musicTrackListNew = album.getMusicTrackList();
            List<MusicTrack> attachedMusicTrackListNew = new ArrayList<MusicTrack>();
            for (MusicTrack musicTrackListNewMusicTrackToAttach : musicTrackListNew) {
                musicTrackListNewMusicTrackToAttach = em.getReference(musicTrackListNewMusicTrackToAttach.getClass(), musicTrackListNewMusicTrackToAttach.getInventoryid());
                attachedMusicTrackListNew.add(musicTrackListNewMusicTrackToAttach);
            }
            musicTrackListNew = attachedMusicTrackListNew;
            album.setMusicTrackList(musicTrackListNew);
            album = em.merge(album);
            for (MusicTrack musicTrackListOldMusicTrack : musicTrackListOld) {
                if (!musicTrackListNew.contains(musicTrackListOldMusicTrack)) {
                    musicTrackListOldMusicTrack.setAlbumid(null);
                    musicTrackListOldMusicTrack = em.merge(musicTrackListOldMusicTrack);
                }
            }
            for (MusicTrack musicTrackListNewMusicTrack : musicTrackListNew) {
                if (!musicTrackListOld.contains(musicTrackListNewMusicTrack)) {
                    Album oldAlbumidOfMusicTrackListNewMusicTrack = musicTrackListNewMusicTrack.getAlbumid();
                    musicTrackListNewMusicTrack.setAlbumid(album);
                    musicTrackListNewMusicTrack = em.merge(musicTrackListNewMusicTrack);
                    if (oldAlbumidOfMusicTrackListNewMusicTrack != null && !oldAlbumidOfMusicTrackListNewMusicTrack.equals(album)) {
                        oldAlbumidOfMusicTrackListNewMusicTrack.getMusicTrackList().remove(musicTrackListNewMusicTrack);
                        oldAlbumidOfMusicTrackListNewMusicTrack = em.merge(oldAlbumidOfMusicTrackListNewMusicTrack);
                    }
                }
            }
            utx.commit();
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = album.getAlbumid();
                if (findAlbum(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Album album;
            try {
                album = em.getReference(Album.class, id);
                album.getAlbumid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The album with id " + id + " no longer exists.", enfe);
            }
            List<MusicTrack> musicTrackList = album.getMusicTrackList();
            for (MusicTrack musicTrackListMusicTrack : musicTrackList) {
                musicTrackListMusicTrack.setAlbumid(null);
                musicTrackListMusicTrack = em.merge(musicTrackListMusicTrack);
            }
            em.remove(album);
            utx.commit();
        } catch (NonexistentEntityException | IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        }
    }

    public List<Album> findAlbumEntities() {
        return findAlbumEntities(true, -1, -1);
    }

    public List<Album> findAlbumEntities(int maxResults, int firstResult) {
        return findAlbumEntities(false, maxResults, firstResult);
    }

    private List<Album> findAlbumEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Album.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public String findGenreAlbumId(int albumId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);

        Root<MusicTrack> track = cq.from(MusicTrack.class);

        //Joining the Album table to the MusicTrack table
        Join albumTracks = track.join("albumid");

        //We want all the tracks from this album that isn't the selected track
        cq.where(cb.equal(albumTracks.get("albumid"), albumId));

        Query q = em.createQuery(cq);

        if (q.getResultList().size() < 1) {
            return null;
        }

        MusicTrack musicTrack = (MusicTrack) q.getResultList().get(0);

        return musicTrack.getMusiccategory();
    }

    public Album findAlbum(Integer id) {
        return em.find(Album.class, id);
    }

    public int getAlbumCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Album> rt = cq.from(Album.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();

    }

    public String searchSingleAlbum(int id) {
        this.selectedAlbum = findAlbum(id);

        validateGenreCookie();
        LOG.info("album id:" + id);
        return "searchAlbum";
    }

    /**
     * Store given album by id to selectedAlbum
     * 
     * @author Victor
     * 
     * @param id
     * @return string to navigate to album page
     */
    public String selectSingleAlbum(int id) {
        this.selectedAlbum = findAlbum(id);
        validateGenreCookie();
        return "detailAlbum";
    }

    /**
     * Finds an album given the id
     * 
     * @author Victor
     * 
     * @param id
     * @return found album
     * @throws NonexistentEntityException 
     */
    public Album findAlbumById(int id) throws NonexistentEntityException {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Album> cq = cb.createQuery(Album.class);

        Root<Album> album = cq.from(Album.class);

        cq.select(album);

        cq.where(cb.equal(album.get("albumid"), id));

        Query q = em.createQuery(cq);
        if (q.getResultList().size() != 1) {
            throw new NonexistentEntityException("Cannot find Album with id");
        }

        return (Album) q.getResultList().get(0);
    }

    public String backToAlbum(Album album) {
        this.selectedAlbum = album;
        return "reviewAlbum";
    }

    /**
     * The track page will show 3 albums from other artists that are part of the
     * same category
     *
     * @author Daniel
     *
     * @param track
     * @return A list of Album objects
     */
    public List<Album> findRelatedAlbums(MusicTrack track) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Album> cq = cb.createQuery(Album.class);

        Root<Album> album = cq.from(Album.class);

        Join albumsTracks = album.join("musicTrackList");

        //If the track is a single, then we cant get the first three related albums
        if (track.getAlbumid() == null) {
            cq.where(cb.equal(albumsTracks.get("musiccategory"), track.getMusiccategory()), cb.equal(album.get("available"), 1)).distinct(true);

            //If the track is part of an album, then we do not want albums is the same as the current track's album
        } else {
            cq.where(cb.equal(albumsTracks.get("musiccategory"), track.getMusiccategory()), cb.notEqual(album.get("albumid"), track.getAlbumid().getAlbumid()), cb.equal(album.get("available"), 1)).distinct(true);

        }

        Query q = em.createQuery(cq);

        List<Album> results = q.getResultList();

        if (results.size() >= 3) {
            return results.subList(0, 3);
        }

        return results;
    }

    /**
     * Overloaded method that finds all related albums given an input album
     *
     * @Daniel
     *
     * @param album
     * @return all related albums in that music category
     */
    public List<Album> findRelatedAlbums(Album album) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Album> cq = cb.createQuery(Album.class);

        Root<Album> albums = cq.from(Album.class);

        Join albumsTracks = albums.join("musicTrackList");

        cq.where(cb.equal(albumsTracks.get("musiccategory"), album.getMusicTrackList().get(0).getMusiccategory()), cb.notEqual(albums.get("albumid"), album.getAlbumid()),
                cb.notEqual(albums.get("artist"), album.getArtist()), cb.equal(albums.get("available"), 1)).distinct(true);

        Query q = em.createQuery(cq);

        List<Album> results = q.getResultList();

        if (results.size() >= 3) {
            return results.subList(0, 3);
        }

        return results;
    }

    /**
     * Gets the list of albums of the most recently searched genre
     * 
     * @author Victor
     * 
     * @return list of albums of specific genre
     */
    public List<Album> getRecentGenreAlbums() {
        String recentGenre = this.preRenderViewBean.findRecentGenreCookie();

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Album> cq = cb.createQuery(Album.class);

        Root<Album> albums = cq.from(Album.class);

        Join albumsTracks = albums.join("musicTrackList");

        cq.where(cb.equal(albumsTracks.get("musiccategory"), recentGenre), cb.equal(albums.get("available"), 1)).distinct(true);

        Query q = em.createQuery(cq);

        return q.getResultList();
    }

    /**
     * Set the selected album into the private variable and return the
     * navigation rule to display the album page.
     *
     * @param album
     * @return the view for the album page.
     */
    public String selectAlbum(Album album) {
        this.selectedAlbum = album;
        LOG.info("" + album.getAlbumtitle());
        validateGenreCookie();

        return "detailAlbum";
    }

    /**
     * Sends page to album page given an Album object
     * 
     * @author Victor
     * 
     * @param album
     * @return 
     */
    public String toAlbum(Album album) {
        this.selectedAlbum = album;
        LOG.info("" + album.getAlbumtitle());

        validateGenreCookie();
        return "albumpage";
    }

    /**
     * Checks if genre cookie is a valid genre string
     * 
     * @author Victor
     */
    private void validateGenreCookie() {
        if (selectedAlbum != null) {
            List<MusicTrack> musicTracks = selectedAlbum.getMusicTrackList();
            if (musicTracks.size() > 0 && musicTracks.get(0).getMusiccategory() != null) {
                preRenderViewBean.writeGenreTrackingCookie(musicTracks.get(0).getMusiccategory());
            }
        }
    }

    /**
     * @return The selected album to be displayed in the album page.
     */
    public Album getSelectedAlbum() {
        return this.selectedAlbum;
    }

    /**
     * @param album The selected album to be displayed in the album page.
     */
    public void setSelectedAlbum(Album album) {
        this.selectedAlbum = album;
    }

    public String showRelatedAlbum(Album album) {
        this.selectedAlbum = album;
        return "relatedAlbumFromAlbum";
    }

    public List<Album> getSpecialAlbums() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Album> cq = cb.createQuery(Album.class);
        Root<Album> album = cq.from(Album.class);
        cq.select(album);

        cq.where(cb.notEqual(album.get("saleprice"), 0), cb.lessThan(album.get("saleprice"), album.get("listprice")), cb.equal(album.get("available"), 1));
        cq.orderBy(cb.desc(album.get("saleprice")));
        TypedQuery<Album> query = em.createQuery(cq);
        List<Album> albums = query.getResultList();
        final int specialsLimt = 3;
        List<Album> specialList = new ArrayList<>();
        if (albums.size() > specialsLimt) {
            for (int i = 0; i < specialsLimt; i++) {
                specialList.add(albums.get(i));
            }
            return specialList;
        } else {
            return albums;
        }
    }

    /**
     * Retrieves an album's tracks in a list of MusicTrack objects
     *
     * @param albumId
     * @return all the tracks in the album
     */
    public List<MusicTrack> getAlbumTracks(int albumId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);

        Root<MusicTrack> track = cq.from(MusicTrack.class);

        //Joining the Album table to the MusicTrack table
        Join albumTracks = track.join("albumid");

        //We want all the tracks from this album that isn't the selected track
        cq.where(cb.equal(albumTracks.get("albumid"), albumId), cb.equal(albumTracks.get("available"), 1));

        Query q = em.createQuery(cq);

        return q.getResultList();
    }
}
