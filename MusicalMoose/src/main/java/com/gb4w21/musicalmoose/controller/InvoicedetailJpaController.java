/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.Invoicedetail;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import com.gb4w21.musicalmoose.entities.Sale;
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
public class InvoicedetailJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(InvoicedetailJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    public void create(Invoicedetail invoicedetail) throws RollbackFailureException {
        try {
            utx.begin();
            em.getTransaction().begin();
            Album albumid = invoicedetail.getAlbumid();
            if (albumid != null) {
                albumid = em.getReference(albumid.getClass(), albumid.getAlbumid());
                invoicedetail.setAlbumid(albumid);
            }
            MusicTrack inventoryid = invoicedetail.getInventoryid();
            if (inventoryid != null) {
                inventoryid = em.getReference(inventoryid.getClass(), inventoryid.getInventoryid());
                invoicedetail.setInventoryid(inventoryid);
            }
            Sale saleid = invoicedetail.getSaleid();
            if (saleid != null) {
                saleid = em.getReference(saleid.getClass(), saleid.getSaleid());
                invoicedetail.setSaleid(saleid);
            }
            em.persist(invoicedetail);
            if (albumid != null) {
                albumid.getInvoicedetailList().add(invoicedetail);
                albumid = em.merge(albumid);
            }
            if (inventoryid != null) {
                inventoryid.getInvoicedetailList().add(invoicedetail);
                inventoryid = em.merge(inventoryid);
            }
            if (saleid != null) {
                saleid.getInvoicedetailList().add(invoicedetail);
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

    public void edit(Invoicedetail invoicedetail) throws NonexistentEntityException, Exception {

        try {
            utx.begin();
            Invoicedetail persistentInvoicedetail = em.find(Invoicedetail.class, invoicedetail.getInvoiceid());
            Album albumidOld = persistentInvoicedetail.getAlbumid();
            Album albumidNew = invoicedetail.getAlbumid();
            MusicTrack inventoryidOld = persistentInvoicedetail.getInventoryid();
            MusicTrack inventoryidNew = invoicedetail.getInventoryid();
            Sale saleidOld = persistentInvoicedetail.getSaleid();
            Sale saleidNew = invoicedetail.getSaleid();
            if (albumidNew != null) {
                albumidNew = em.getReference(albumidNew.getClass(), albumidNew.getAlbumid());
                invoicedetail.setAlbumid(albumidNew);
            }
            if (inventoryidNew != null) {
                inventoryidNew = em.getReference(inventoryidNew.getClass(), inventoryidNew.getInventoryid());
                invoicedetail.setInventoryid(inventoryidNew);
            }
            if (saleidNew != null) {
                saleidNew = em.getReference(saleidNew.getClass(), saleidNew.getSaleid());
                invoicedetail.setSaleid(saleidNew);
            }
            invoicedetail = em.merge(invoicedetail);
            if (albumidOld != null && !albumidOld.equals(albumidNew)) {
                albumidOld.getInvoicedetailList().remove(invoicedetail);
                albumidOld = em.merge(albumidOld);
            }
            if (albumidNew != null && !albumidNew.equals(albumidOld)) {
                albumidNew.getInvoicedetailList().add(invoicedetail);
                albumidNew = em.merge(albumidNew);
            }
            if (inventoryidOld != null && !inventoryidOld.equals(inventoryidNew)) {
                inventoryidOld.getInvoicedetailList().remove(invoicedetail);
                inventoryidOld = em.merge(inventoryidOld);
            }
            if (inventoryidNew != null && !inventoryidNew.equals(inventoryidOld)) {
                inventoryidNew.getInvoicedetailList().add(invoicedetail);
                inventoryidNew = em.merge(inventoryidNew);
            }
            if (saleidOld != null && !saleidOld.equals(saleidNew)) {
                saleidOld.getInvoicedetailList().remove(invoicedetail);
                saleidOld = em.merge(saleidOld);
            }
            if (saleidNew != null && !saleidNew.equals(saleidOld)) {
                saleidNew.getInvoicedetailList().add(invoicedetail);
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
                Integer id = invoicedetail.getInvoiceid();
                if (findInvoicedetail(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Invoicedetail invoicedetail;
            try {
                invoicedetail = em.getReference(Invoicedetail.class, id);
                invoicedetail.getInvoiceid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The invoicedetail with id " + id + " no longer exists.", enfe);
            }
            Album albumid = invoicedetail.getAlbumid();
            if (albumid != null) {
                albumid.getInvoicedetailList().remove(invoicedetail);
                albumid = em.merge(albumid);
            }
            MusicTrack inventoryid = invoicedetail.getInventoryid();
            if (inventoryid != null) {
                inventoryid.getInvoicedetailList().remove(invoicedetail);
                inventoryid = em.merge(inventoryid);
            }
            Sale saleid = invoicedetail.getSaleid();
            if (saleid != null) {
                saleid.getInvoicedetailList().remove(invoicedetail);
                saleid = em.merge(saleid);
            }
            em.remove(invoicedetail);
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

    public List<Invoicedetail> findInvoicedetailEntities() {
        return findInvoicedetailEntities(true, -1, -1);
    }

    public List<Invoicedetail> findInvoicedetailEntities(int maxResults, int firstResult) {
        return findInvoicedetailEntities(false, maxResults, firstResult);
    }

    private List<Invoicedetail> findInvoicedetailEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Invoicedetail.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public Invoicedetail findInvoicedetail(Integer id) {

        return em.find(Invoicedetail.class, id);

    }

    public int getInvoicedetailCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Invoicedetail> rt = cq.from(Invoicedetail.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();

    }

}
