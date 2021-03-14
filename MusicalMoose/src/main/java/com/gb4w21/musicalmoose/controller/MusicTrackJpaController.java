/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
public class MusicTrackJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(MusicTrackJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    private MusicTrack searchedTrack;

    public void create(MusicTrack musicTrack) throws RollbackFailureException {
        try {
            utx.begin();

            Album albumid = musicTrack.getAlbumid();
            if (albumid != null) {
                albumid = em.getReference(albumid.getClass(), albumid.getAlbumid());
                musicTrack.setAlbumid(albumid);
            }
            em.persist(musicTrack);
            if (albumid != null) {
                albumid.getMusicTrackList().add(musicTrack);
                albumid = em.merge(albumid);
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

    public void edit(MusicTrack musicTrack) throws NonexistentEntityException, Exception {
        try {
            utx.begin();

            MusicTrack persistentMusicTrack = em.find(MusicTrack.class, musicTrack.getInventoryid());
            Album albumidOld = persistentMusicTrack.getAlbumid();
            Album albumidNew = musicTrack.getAlbumid();
            if (albumidNew != null) {
                albumidNew = em.getReference(albumidNew.getClass(), albumidNew.getAlbumid());
                musicTrack.setAlbumid(albumidNew);
            }
            musicTrack = em.merge(musicTrack);
            if (albumidOld != null && !albumidOld.equals(albumidNew)) {
                albumidOld.getMusicTrackList().remove(musicTrack);
                albumidOld = em.merge(albumidOld);
            }
            if (albumidNew != null && !albumidNew.equals(albumidOld)) {
                albumidNew.getMusicTrackList().add(musicTrack);
                albumidNew = em.merge(albumidNew);
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
                Integer id = musicTrack.getInventoryid();
                if (findMusicTrack(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            MusicTrack musicTrack;
            try {
                musicTrack = em.getReference(MusicTrack.class, id);
                musicTrack.getInventoryid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The musicTrack with id " + id + " no longer exists.", enfe);
            }
            Album albumid = musicTrack.getAlbumid();
            if (albumid != null) {
                albumid.getMusicTrackList().remove(musicTrack);
                albumid = em.merge(albumid);
            }
            em.remove(musicTrack);
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

    public List<MusicTrack> findMusicTrackEntities() {
        return findMusicTrackEntities(true, -1, -1);
    }

    public List<MusicTrack> findMusicTrackEntities(int maxResults, int firstResult) {
        return findMusicTrackEntities(false, maxResults, firstResult);
    }

    private List<MusicTrack> findMusicTrackEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(MusicTrack.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public MusicTrack findMusicTrack(Integer id) {
        MusicTrack mt = em.find(MusicTrack.class, id);
        LOG.info(mt.getArtist());
        return em.find(MusicTrack.class, id);

    }

    /**
     * Returns a list of the three most recently added MusicTrack objects
     *
     * @return List of MusicTrack objects
     */
    public List<MusicTrack> findMostRecentTracks() {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery cq = cb.createQuery();
        Root e = cq.from(MusicTrack.class);
        cq.orderBy(cb.asc(e.get("dateentered")));
        Query q = em.createQuery(cq);

        List<MusicTrack> originalList = q.getResultList();

        List<MusicTrack> threeRecentTracks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            threeRecentTracks.add(originalList.get(i));
        }

        return threeRecentTracks;
    }

    public int getMusicTrackCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<MusicTrack> rt = cq.from(MusicTrack.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();

    }

    /**
     * Finds all tracks that belong to a track's album
     *
     * @param track
     * @return tracks from same album
     */
    public List<MusicTrack> findAllRelatedTracks(MusicTrack track) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);

        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);

        //Joining the MusicTrack table to the Album table
        Join albumsTracks = musicTrack.join("albumid");

        //We want all the tracks from this album that isn't the selected track
        cq.where(cb.equal(albumsTracks.get("albumid"), track.getAlbumid().getAlbumid()), cb.notEqual(musicTrack.get("tracktitle"), track.getTracktitle()));

        Query q = em.createQuery(cq);

        return q.getResultList();

    }

    /**
     * Set the selected track and display the trackpage.xhtml
     *
     * @param track
     * @return display the trackpage.xhtml
     */
    public String searchTrack(MusicTrack track) {
        this.searchedTrack = track;
        return "detailTrack";

    }

    /**
     * Simple getter so the track page can access the selected track
     *
     * @return a track
     */
    public MusicTrack getMusicTrack() {
        return this.searchedTrack;
    }

    /**
     * When a user clicks on a related track, set the selected track and show
     * the track page once again.
     *
     * @param track
     * @return display the trackpage.xhtml
     */
    public String searchRelatedTrack(MusicTrack track) {
        this.searchedTrack = track;
        return "relatedTrack";
    }

}
