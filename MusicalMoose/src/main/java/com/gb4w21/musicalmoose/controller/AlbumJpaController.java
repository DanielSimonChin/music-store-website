/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.entities.Album;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.gb4w21.musicalmoose.entities.Salebralbum;
import java.util.ArrayList;
import java.util.List;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
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

    public void create(Album album) throws RollbackFailureException {
        if (album.getSalebralbumList() == null) {
            album.setSalebralbumList(new ArrayList<Salebralbum>());
        }
        if (album.getMusicTrackList() == null) {
            album.setMusicTrackList(new ArrayList<MusicTrack>());
        }

        try {
            utx.begin();
            List<Salebralbum> attachedSalebralbumList = new ArrayList<Salebralbum>();
            for (Salebralbum salebralbumListSalebralbumToAttach : album.getSalebralbumList()) {
                salebralbumListSalebralbumToAttach = em.getReference(salebralbumListSalebralbumToAttach.getClass(), salebralbumListSalebralbumToAttach.getSalebralbumid());
                attachedSalebralbumList.add(salebralbumListSalebralbumToAttach);
            }
            album.setSalebralbumList(attachedSalebralbumList);
            List<MusicTrack> attachedMusicTrackList = new ArrayList<MusicTrack>();
            for (MusicTrack musicTrackListMusicTrackToAttach : album.getMusicTrackList()) {
                musicTrackListMusicTrackToAttach = em.getReference(musicTrackListMusicTrackToAttach.getClass(), musicTrackListMusicTrackToAttach.getInventoryid());
                attachedMusicTrackList.add(musicTrackListMusicTrackToAttach);
            }
            album.setMusicTrackList(attachedMusicTrackList);
            em.persist(album);
            for (Salebralbum salebralbumListSalebralbum : album.getSalebralbumList()) {
                Album oldAlbumidOfSalebralbumListSalebralbum = salebralbumListSalebralbum.getAlbumid();
                salebralbumListSalebralbum.setAlbumid(album);
                salebralbumListSalebralbum = em.merge(salebralbumListSalebralbum);
                if (oldAlbumidOfSalebralbumListSalebralbum != null) {
                    oldAlbumidOfSalebralbumListSalebralbum.getSalebralbumList().remove(salebralbumListSalebralbum);
                    oldAlbumidOfSalebralbumListSalebralbum = em.merge(oldAlbumidOfSalebralbumListSalebralbum);
                }
            }
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
            List<Salebralbum> salebralbumListOld = persistentAlbum.getSalebralbumList();
            List<Salebralbum> salebralbumListNew = album.getSalebralbumList();
            List<MusicTrack> musicTrackListOld = persistentAlbum.getMusicTrackList();
            List<MusicTrack> musicTrackListNew = album.getMusicTrackList();
            List<Salebralbum> attachedSalebralbumListNew = new ArrayList<Salebralbum>();
            for (Salebralbum salebralbumListNewSalebralbumToAttach : salebralbumListNew) {
                salebralbumListNewSalebralbumToAttach = em.getReference(salebralbumListNewSalebralbumToAttach.getClass(), salebralbumListNewSalebralbumToAttach.getSalebralbumid());
                attachedSalebralbumListNew.add(salebralbumListNewSalebralbumToAttach);
            }
            salebralbumListNew = attachedSalebralbumListNew;
            album.setSalebralbumList(salebralbumListNew);
            List<MusicTrack> attachedMusicTrackListNew = new ArrayList<MusicTrack>();
            for (MusicTrack musicTrackListNewMusicTrackToAttach : musicTrackListNew) {
                musicTrackListNewMusicTrackToAttach = em.getReference(musicTrackListNewMusicTrackToAttach.getClass(), musicTrackListNewMusicTrackToAttach.getInventoryid());
                attachedMusicTrackListNew.add(musicTrackListNewMusicTrackToAttach);
            }
            musicTrackListNew = attachedMusicTrackListNew;
            album.setMusicTrackList(musicTrackListNew);
            album = em.merge(album);
            for (Salebralbum salebralbumListOldSalebralbum : salebralbumListOld) {
                if (!salebralbumListNew.contains(salebralbumListOldSalebralbum)) {
                    salebralbumListOldSalebralbum.setAlbumid(null);
                    salebralbumListOldSalebralbum = em.merge(salebralbumListOldSalebralbum);
                }
            }
            for (Salebralbum salebralbumListNewSalebralbum : salebralbumListNew) {
                if (!salebralbumListOld.contains(salebralbumListNewSalebralbum)) {
                    Album oldAlbumidOfSalebralbumListNewSalebralbum = salebralbumListNewSalebralbum.getAlbumid();
                    salebralbumListNewSalebralbum.setAlbumid(album);
                    salebralbumListNewSalebralbum = em.merge(salebralbumListNewSalebralbum);
                    if (oldAlbumidOfSalebralbumListNewSalebralbum != null && !oldAlbumidOfSalebralbumListNewSalebralbum.equals(album)) {
                        oldAlbumidOfSalebralbumListNewSalebralbum.getSalebralbumList().remove(salebralbumListNewSalebralbum);
                        oldAlbumidOfSalebralbumListNewSalebralbum = em.merge(oldAlbumidOfSalebralbumListNewSalebralbum);
                    }
                }
            }
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

    public void destroy(Integer id) throws  NonexistentEntityException, RollbackFailureException, Exception{
        try {
            utx.begin();
            Album album;
            try {
                album = em.getReference(Album.class, id);
                album.getAlbumid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The album with id " + id + " no longer exists.", enfe);
            }
            List<Salebralbum> salebralbumList = album.getSalebralbumList();
            for (Salebralbum salebralbumListSalebralbum : salebralbumList) {
                salebralbumListSalebralbum.setAlbumid(null);
                salebralbumListSalebralbum = em.merge(salebralbumListSalebralbum);
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

}

