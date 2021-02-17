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
import com.gb4w21.musicalmoose.entities.Invoice;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import com.gb4w21.musicalmoose.entities.Salebrtrack;
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
public class SalebrtrackJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(SalebrtrackJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    public void create(Salebrtrack salebrtrack) throws RollbackFailureException {
     
     try{
           
            utx.begin();
            Invoice saleid = salebrtrack.getSaleid();
            if (saleid != null) {
                saleid = em.getReference(saleid.getClass(), saleid.getSaleid());
                salebrtrack.setSaleid(saleid);
            }
            MusicTrack inventoryid = salebrtrack.getInventoryid();
            if (inventoryid != null) {
                inventoryid = em.getReference(inventoryid.getClass(), inventoryid.getInventoryid());
                salebrtrack.setInventoryid(inventoryid);
            }
            em.persist(salebrtrack);
            if (saleid != null) {
                saleid.getSalebrtrackList().add(salebrtrack);
                saleid = em.merge(saleid);
            }
            if (inventoryid != null) {
                inventoryid.getSalebrtrackList().add(salebrtrack);
                inventoryid = em.merge(inventoryid);
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

    public void edit(Salebrtrack salebrtrack) throws NonexistentEntityException, Exception {
     
        try {
         
            utx.begin();
            Salebrtrack persistentSalebrtrack = em.find(Salebrtrack.class, salebrtrack.getSalebrtrackid());
            Invoice saleidOld = persistentSalebrtrack.getSaleid();
            Invoice saleidNew = salebrtrack.getSaleid();
            MusicTrack inventoryidOld = persistentSalebrtrack.getInventoryid();
            MusicTrack inventoryidNew = salebrtrack.getInventoryid();
            if (saleidNew != null) {
                saleidNew = em.getReference(saleidNew.getClass(), saleidNew.getSaleid());
                salebrtrack.setSaleid(saleidNew);
            }
            if (inventoryidNew != null) {
                inventoryidNew = em.getReference(inventoryidNew.getClass(), inventoryidNew.getInventoryid());
                salebrtrack.setInventoryid(inventoryidNew);
            }
            salebrtrack = em.merge(salebrtrack);
            if (saleidOld != null && !saleidOld.equals(saleidNew)) {
                saleidOld.getSalebrtrackList().remove(salebrtrack);
                saleidOld = em.merge(saleidOld);
            }
            if (saleidNew != null && !saleidNew.equals(saleidOld)) {
                saleidNew.getSalebrtrackList().add(salebrtrack);
                saleidNew = em.merge(saleidNew);
            }
            if (inventoryidOld != null && !inventoryidOld.equals(inventoryidNew)) {
                inventoryidOld.getSalebrtrackList().remove(salebrtrack);
                inventoryidOld = em.merge(inventoryidOld);
            }
            if (inventoryidNew != null && !inventoryidNew.equals(inventoryidOld)) {
                inventoryidNew.getSalebrtrackList().add(salebrtrack);
                inventoryidNew = em.merge(inventoryidNew);
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
                Integer id = salebrtrack.getSalebrtrackid();
                if (findSalebrtrack(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws  NonexistentEntityException, RollbackFailureException, Exception {
        try{
            utx.begin();
            Salebrtrack salebrtrack;
            try {
                salebrtrack = em.getReference(Salebrtrack.class, id);
                salebrtrack.getSalebrtrackid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The salebrtrack with id " + id + " no longer exists.", enfe);
            }
            Invoice saleid = salebrtrack.getSaleid();
            if (saleid != null) {
                saleid.getSalebrtrackList().remove(salebrtrack);
                saleid = em.merge(saleid);
            }
            MusicTrack inventoryid = salebrtrack.getInventoryid();
            if (inventoryid != null) {
                inventoryid.getSalebrtrackList().remove(salebrtrack);
                inventoryid = em.merge(inventoryid);
            }
            em.remove(salebrtrack);
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

    public List<Salebrtrack> findSalebrtrackEntities() {
        return findSalebrtrackEntities(true, -1, -1);
    }

    public List<Salebrtrack> findSalebrtrackEntities(int maxResults, int firstResult) {
        return findSalebrtrackEntities(false, maxResults, firstResult);
    }

    private List<Salebrtrack> findSalebrtrackEntities(boolean all, int maxResults, int firstResult) {
      
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Salebrtrack.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        
    }

    public Salebrtrack findSalebrtrack(Integer id) {
        
            return em.find(Salebrtrack.class, id);
        
    }

    public int getSalebrtrackCount() {
       
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Salebrtrack> rt = cq.from(Salebrtrack.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
       
    }
}