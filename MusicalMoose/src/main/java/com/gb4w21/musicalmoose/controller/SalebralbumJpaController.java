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
import com.gb4w21.musicalmoose.entities.Invoice;
import com.gb4w21.musicalmoose.entities.Salebralbum;
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
public class SalebralbumJpaController implements Serializable {

       private final static Logger LOG = LoggerFactory.getLogger(SalebralbumJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    public void create(Salebralbum salebralbum) throws RollbackFailureException {
        try{
            utx.begin();
            Album albumid = salebralbum.getAlbumid();
            if (albumid != null) {
                albumid = em.getReference(albumid.getClass(), albumid.getAlbumid());
                salebralbum.setAlbumid(albumid);
            }
            Invoice saleid = salebralbum.getSaleid();
            if (saleid != null) {
                saleid = em.getReference(saleid.getClass(), saleid.getSaleid());
                salebralbum.setSaleid(saleid);
            }
            em.persist(salebralbum);
            if (albumid != null) {
                albumid.getSalebralbumList().add(salebralbum);
                albumid = em.merge(albumid);
            }
            if (saleid != null) {
                saleid.getSalebralbumList().add(salebralbum);
                saleid = em.merge(saleid);
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

    public void edit(Salebralbum salebralbum) throws NonexistentEntityException, Exception {
        
        try {
         
            utx.begin();
            Salebralbum persistentSalebralbum = em.find(Salebralbum.class, salebralbum.getSalebralbumid());
            Album albumidOld = persistentSalebralbum.getAlbumid();
            Album albumidNew = salebralbum.getAlbumid();
            Invoice saleidOld = persistentSalebralbum.getSaleid();
            Invoice saleidNew = salebralbum.getSaleid();
            if (albumidNew != null) {
                albumidNew = em.getReference(albumidNew.getClass(), albumidNew.getAlbumid());
                salebralbum.setAlbumid(albumidNew);
            }
            if (saleidNew != null) {
                saleidNew = em.getReference(saleidNew.getClass(), saleidNew.getSaleid());
                salebralbum.setSaleid(saleidNew);
            }
            salebralbum = em.merge(salebralbum);
            if (albumidOld != null && !albumidOld.equals(albumidNew)) {
                albumidOld.getSalebralbumList().remove(salebralbum);
                albumidOld = em.merge(albumidOld);
            }
            if (albumidNew != null && !albumidNew.equals(albumidOld)) {
                albumidNew.getSalebralbumList().add(salebralbum);
                albumidNew = em.merge(albumidNew);
            }
            if (saleidOld != null && !saleidOld.equals(saleidNew)) {
                saleidOld.getSalebralbumList().remove(salebralbum);
                saleidOld = em.merge(saleidOld);
            }
            if (saleidNew != null && !saleidNew.equals(saleidOld)) {
                saleidNew.getSalebralbumList().add(salebralbum);
                saleidNew = em.merge(saleidNew);
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
                Integer id = salebralbum.getSalebralbumid();
                if (findSalebralbum(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws  NonexistentEntityException, RollbackFailureException, Exception {
        try{
            utx.begin();
            Salebralbum salebralbum;
            try {
                salebralbum = em.getReference(Salebralbum.class, id);
                salebralbum.getSalebralbumid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The salebralbum with id " + id + " no longer exists.", enfe);
            }
            Album albumid = salebralbum.getAlbumid();
            if (albumid != null) {
                albumid.getSalebralbumList().remove(salebralbum);
                albumid = em.merge(albumid);
            }
            Invoice saleid = salebralbum.getSaleid();
            if (saleid != null) {
                saleid.getSalebralbumList().remove(salebralbum);
                saleid = em.merge(saleid);
            }
            em.remove(salebralbum);
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

    public List<Salebralbum> findSalebralbumEntities() {
        return findSalebralbumEntities(true, -1, -1);
    }

    public List<Salebralbum> findSalebralbumEntities(int maxResults, int firstResult) {
        return findSalebralbumEntities(false, maxResults, firstResult);
    }

    private List<Salebralbum> findSalebralbumEntities(boolean all, int maxResults, int firstResult) {
        
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Salebralbum.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
       
    }

    public Salebralbum findSalebralbum(Integer id) {
        
            return em.find(Salebralbum.class, id);
       
    }

    public int getSalebralbumCount() {
       
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Salebralbum> rt = cq.from(Salebralbum.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
       
    }
    
}
