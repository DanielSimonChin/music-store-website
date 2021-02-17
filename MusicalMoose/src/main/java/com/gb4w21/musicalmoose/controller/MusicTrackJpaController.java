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
import com.gb4w21.musicalmoose.entities.Salebrtrack;
import java.util.ArrayList;
import java.util.List;
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
public class MusicTrackJpaController implements Serializable {

       private final static Logger LOG = LoggerFactory.getLogger(MusicTrackJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    public void create(MusicTrack musicTrack) throws RollbackFailureException {
        if (musicTrack.getSalebrtrackList() == null) {
            musicTrack.setSalebrtrackList(new ArrayList<Salebrtrack>());
        }
         try{
            utx.begin();
            Album albumid = musicTrack.getAlbumid();
            if (albumid != null) {
                albumid = em.getReference(albumid.getClass(), albumid.getAlbumid());
                musicTrack.setAlbumid(albumid);
            }
            List<Salebrtrack> attachedSalebrtrackList = new ArrayList<Salebrtrack>();
            for (Salebrtrack salebrtrackListSalebrtrackToAttach : musicTrack.getSalebrtrackList()) {
                salebrtrackListSalebrtrackToAttach = em.getReference(salebrtrackListSalebrtrackToAttach.getClass(), salebrtrackListSalebrtrackToAttach.getSalebrtrackid());
                attachedSalebrtrackList.add(salebrtrackListSalebrtrackToAttach);
            }
            musicTrack.setSalebrtrackList(attachedSalebrtrackList);
            em.persist(musicTrack);
            if (albumid != null) {
                albumid.getMusicTrackList().add(musicTrack);
                albumid = em.merge(albumid);
            }
            for (Salebrtrack salebrtrackListSalebrtrack : musicTrack.getSalebrtrackList()) {
                MusicTrack oldInventoryidOfSalebrtrackListSalebrtrack = salebrtrackListSalebrtrack.getInventoryid();
                salebrtrackListSalebrtrack.setInventoryid(musicTrack);
                salebrtrackListSalebrtrack = em.merge(salebrtrackListSalebrtrack);
                if (oldInventoryidOfSalebrtrackListSalebrtrack != null) {
                    oldInventoryidOfSalebrtrackListSalebrtrack.getSalebrtrackList().remove(salebrtrackListSalebrtrack);
                    oldInventoryidOfSalebrtrackListSalebrtrack = em.merge(oldInventoryidOfSalebrtrackListSalebrtrack);
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

    public void edit(MusicTrack musicTrack) throws NonexistentEntityException, Exception {
        
        try {

            utx.begin();
            MusicTrack persistentMusicTrack = em.find(MusicTrack.class, musicTrack.getInventoryid());
            Album albumidOld = persistentMusicTrack.getAlbumid();
            Album albumidNew = musicTrack.getAlbumid();
            List<Salebrtrack> salebrtrackListOld = persistentMusicTrack.getSalebrtrackList();
            List<Salebrtrack> salebrtrackListNew = musicTrack.getSalebrtrackList();
            if (albumidNew != null) {
                albumidNew = em.getReference(albumidNew.getClass(), albumidNew.getAlbumid());
                musicTrack.setAlbumid(albumidNew);
            }
            List<Salebrtrack> attachedSalebrtrackListNew = new ArrayList<Salebrtrack>();
            for (Salebrtrack salebrtrackListNewSalebrtrackToAttach : salebrtrackListNew) {
                salebrtrackListNewSalebrtrackToAttach = em.getReference(salebrtrackListNewSalebrtrackToAttach.getClass(), salebrtrackListNewSalebrtrackToAttach.getSalebrtrackid());
                attachedSalebrtrackListNew.add(salebrtrackListNewSalebrtrackToAttach);
            }
            salebrtrackListNew = attachedSalebrtrackListNew;
            musicTrack.setSalebrtrackList(salebrtrackListNew);
            musicTrack = em.merge(musicTrack);
            if (albumidOld != null && !albumidOld.equals(albumidNew)) {
                albumidOld.getMusicTrackList().remove(musicTrack);
                albumidOld = em.merge(albumidOld);
            }
            if (albumidNew != null && !albumidNew.equals(albumidOld)) {
                albumidNew.getMusicTrackList().add(musicTrack);
                albumidNew = em.merge(albumidNew);
            }
            for (Salebrtrack salebrtrackListOldSalebrtrack : salebrtrackListOld) {
                if (!salebrtrackListNew.contains(salebrtrackListOldSalebrtrack)) {
                    salebrtrackListOldSalebrtrack.setInventoryid(null);
                    salebrtrackListOldSalebrtrack = em.merge(salebrtrackListOldSalebrtrack);
                }
            }
            for (Salebrtrack salebrtrackListNewSalebrtrack : salebrtrackListNew) {
                if (!salebrtrackListOld.contains(salebrtrackListNewSalebrtrack)) {
                    MusicTrack oldInventoryidOfSalebrtrackListNewSalebrtrack = salebrtrackListNewSalebrtrack.getInventoryid();
                    salebrtrackListNewSalebrtrack.setInventoryid(musicTrack);
                    salebrtrackListNewSalebrtrack = em.merge(salebrtrackListNewSalebrtrack);
                    if (oldInventoryidOfSalebrtrackListNewSalebrtrack != null && !oldInventoryidOfSalebrtrackListNewSalebrtrack.equals(musicTrack)) {
                        oldInventoryidOfSalebrtrackListNewSalebrtrack.getSalebrtrackList().remove(salebrtrackListNewSalebrtrack);
                        oldInventoryidOfSalebrtrackListNewSalebrtrack = em.merge(oldInventoryidOfSalebrtrackListNewSalebrtrack);
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
                Integer id = musicTrack.getInventoryid();
                if (findMusicTrack(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws  NonexistentEntityException, RollbackFailureException, Exception {
        try{
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
            List<Salebrtrack> salebrtrackList = musicTrack.getSalebrtrackList();
            for (Salebrtrack salebrtrackListSalebrtrack : salebrtrackList) {
                salebrtrackListSalebrtrack.setInventoryid(null);
                salebrtrackListSalebrtrack = em.merge(salebrtrackListSalebrtrack);
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
        
            return em.find(MusicTrack.class, id);
       
    }

    public int getMusicTrackCount() {
        
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MusicTrack> rt = cq.from(MusicTrack.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
       
    }
    
}

