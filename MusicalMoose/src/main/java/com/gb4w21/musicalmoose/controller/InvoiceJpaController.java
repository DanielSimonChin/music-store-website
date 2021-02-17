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
import com.gb4w21.musicalmoose.entities.Client;
import com.gb4w21.musicalmoose.entities.Invoice;
import com.gb4w21.musicalmoose.entities.Salebralbum;
import java.util.ArrayList;
import java.util.List;
import com.gb4w21.musicalmoose.entities.Salebrtrack;
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
public class InvoiceJpaController implements Serializable {

      private final static Logger LOG = LoggerFactory.getLogger(InvoiceJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    public void create(Invoice invoice) throws RollbackFailureException {
        if (invoice.getSalebralbumList() == null) {
            invoice.setSalebralbumList(new ArrayList<Salebralbum>());
        }
        if (invoice.getSalebrtrackList() == null) {
            invoice.setSalebrtrackList(new ArrayList<Salebrtrack>());
        }
      try{
            utx.begin();
            Client clientid = invoice.getClientid();
            if (clientid != null) {
                clientid = em.getReference(clientid.getClass(), clientid.getClientid());
                invoice.setClientid(clientid);
            }
            List<Salebralbum> attachedSalebralbumList = new ArrayList<Salebralbum>();
            for (Salebralbum salebralbumListSalebralbumToAttach : invoice.getSalebralbumList()) {
                salebralbumListSalebralbumToAttach = em.getReference(salebralbumListSalebralbumToAttach.getClass(), salebralbumListSalebralbumToAttach.getSalebralbumid());
                attachedSalebralbumList.add(salebralbumListSalebralbumToAttach);
            }
            invoice.setSalebralbumList(attachedSalebralbumList);
            List<Salebrtrack> attachedSalebrtrackList = new ArrayList<Salebrtrack>();
            for (Salebrtrack salebrtrackListSalebrtrackToAttach : invoice.getSalebrtrackList()) {
                salebrtrackListSalebrtrackToAttach = em.getReference(salebrtrackListSalebrtrackToAttach.getClass(), salebrtrackListSalebrtrackToAttach.getSalebrtrackid());
                attachedSalebrtrackList.add(salebrtrackListSalebrtrackToAttach);
            }
            invoice.setSalebrtrackList(attachedSalebrtrackList);
            em.persist(invoice);
            if (clientid != null) {
                clientid.getInvoiceList().add(invoice);
                clientid = em.merge(clientid);
            }
            for (Salebralbum salebralbumListSalebralbum : invoice.getSalebralbumList()) {
                Invoice oldSaleidOfSalebralbumListSalebralbum = salebralbumListSalebralbum.getSaleid();
                salebralbumListSalebralbum.setSaleid(invoice);
                salebralbumListSalebralbum = em.merge(salebralbumListSalebralbum);
                if (oldSaleidOfSalebralbumListSalebralbum != null) {
                    oldSaleidOfSalebralbumListSalebralbum.getSalebralbumList().remove(salebralbumListSalebralbum);
                    oldSaleidOfSalebralbumListSalebralbum = em.merge(oldSaleidOfSalebralbumListSalebralbum);
                }
            }
            for (Salebrtrack salebrtrackListSalebrtrack : invoice.getSalebrtrackList()) {
                Invoice oldSaleidOfSalebrtrackListSalebrtrack = salebrtrackListSalebrtrack.getSaleid();
                salebrtrackListSalebrtrack.setSaleid(invoice);
                salebrtrackListSalebrtrack = em.merge(salebrtrackListSalebrtrack);
                if (oldSaleidOfSalebrtrackListSalebrtrack != null) {
                    oldSaleidOfSalebrtrackListSalebrtrack.getSalebrtrackList().remove(salebrtrackListSalebrtrack);
                    oldSaleidOfSalebrtrackListSalebrtrack = em.merge(oldSaleidOfSalebrtrackListSalebrtrack);
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

    public void edit(Invoice invoice) throws NonexistentEntityException, Exception {
      
        try {
          
            utx.begin();
            Invoice persistentInvoice = em.find(Invoice.class, invoice.getSaleid());
            Client clientidOld = persistentInvoice.getClientid();
            Client clientidNew = invoice.getClientid();
            List<Salebralbum> salebralbumListOld = persistentInvoice.getSalebralbumList();
            List<Salebralbum> salebralbumListNew = invoice.getSalebralbumList();
            List<Salebrtrack> salebrtrackListOld = persistentInvoice.getSalebrtrackList();
            List<Salebrtrack> salebrtrackListNew = invoice.getSalebrtrackList();
            if (clientidNew != null) {
                clientidNew = em.getReference(clientidNew.getClass(), clientidNew.getClientid());
                invoice.setClientid(clientidNew);
            }
            List<Salebralbum> attachedSalebralbumListNew = new ArrayList<Salebralbum>();
            for (Salebralbum salebralbumListNewSalebralbumToAttach : salebralbumListNew) {
                salebralbumListNewSalebralbumToAttach = em.getReference(salebralbumListNewSalebralbumToAttach.getClass(), salebralbumListNewSalebralbumToAttach.getSalebralbumid());
                attachedSalebralbumListNew.add(salebralbumListNewSalebralbumToAttach);
            }
            salebralbumListNew = attachedSalebralbumListNew;
            invoice.setSalebralbumList(salebralbumListNew);
            List<Salebrtrack> attachedSalebrtrackListNew = new ArrayList<Salebrtrack>();
            for (Salebrtrack salebrtrackListNewSalebrtrackToAttach : salebrtrackListNew) {
                salebrtrackListNewSalebrtrackToAttach = em.getReference(salebrtrackListNewSalebrtrackToAttach.getClass(), salebrtrackListNewSalebrtrackToAttach.getSalebrtrackid());
                attachedSalebrtrackListNew.add(salebrtrackListNewSalebrtrackToAttach);
            }
            salebrtrackListNew = attachedSalebrtrackListNew;
            invoice.setSalebrtrackList(salebrtrackListNew);
            invoice = em.merge(invoice);
            if (clientidOld != null && !clientidOld.equals(clientidNew)) {
                clientidOld.getInvoiceList().remove(invoice);
                clientidOld = em.merge(clientidOld);
            }
            if (clientidNew != null && !clientidNew.equals(clientidOld)) {
                clientidNew.getInvoiceList().add(invoice);
                clientidNew = em.merge(clientidNew);
            }
            for (Salebralbum salebralbumListOldSalebralbum : salebralbumListOld) {
                if (!salebralbumListNew.contains(salebralbumListOldSalebralbum)) {
                    salebralbumListOldSalebralbum.setSaleid(null);
                    salebralbumListOldSalebralbum = em.merge(salebralbumListOldSalebralbum);
                }
            }
            for (Salebralbum salebralbumListNewSalebralbum : salebralbumListNew) {
                if (!salebralbumListOld.contains(salebralbumListNewSalebralbum)) {
                    Invoice oldSaleidOfSalebralbumListNewSalebralbum = salebralbumListNewSalebralbum.getSaleid();
                    salebralbumListNewSalebralbum.setSaleid(invoice);
                    salebralbumListNewSalebralbum = em.merge(salebralbumListNewSalebralbum);
                    if (oldSaleidOfSalebralbumListNewSalebralbum != null && !oldSaleidOfSalebralbumListNewSalebralbum.equals(invoice)) {
                        oldSaleidOfSalebralbumListNewSalebralbum.getSalebralbumList().remove(salebralbumListNewSalebralbum);
                        oldSaleidOfSalebralbumListNewSalebralbum = em.merge(oldSaleidOfSalebralbumListNewSalebralbum);
                    }
                }
            }
            for (Salebrtrack salebrtrackListOldSalebrtrack : salebrtrackListOld) {
                if (!salebrtrackListNew.contains(salebrtrackListOldSalebrtrack)) {
                    salebrtrackListOldSalebrtrack.setSaleid(null);
                    salebrtrackListOldSalebrtrack = em.merge(salebrtrackListOldSalebrtrack);
                }
            }
            for (Salebrtrack salebrtrackListNewSalebrtrack : salebrtrackListNew) {
                if (!salebrtrackListOld.contains(salebrtrackListNewSalebrtrack)) {
                    Invoice oldSaleidOfSalebrtrackListNewSalebrtrack = salebrtrackListNewSalebrtrack.getSaleid();
                    salebrtrackListNewSalebrtrack.setSaleid(invoice);
                    salebrtrackListNewSalebrtrack = em.merge(salebrtrackListNewSalebrtrack);
                    if (oldSaleidOfSalebrtrackListNewSalebrtrack != null && !oldSaleidOfSalebrtrackListNewSalebrtrack.equals(invoice)) {
                        oldSaleidOfSalebrtrackListNewSalebrtrack.getSalebrtrackList().remove(salebrtrackListNewSalebrtrack);
                        oldSaleidOfSalebrtrackListNewSalebrtrack = em.merge(oldSaleidOfSalebrtrackListNewSalebrtrack);
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
                Integer id = invoice.getSaleid();
                if (findInvoice(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } 
    }

    public void destroy(Integer id) throws  NonexistentEntityException, RollbackFailureException, Exception {
        try{
            utx.begin();
            Invoice invoice;
            try {
                invoice = em.getReference(Invoice.class, id);
                invoice.getSaleid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The invoice with id " + id + " no longer exists.", enfe);
            }
            Client clientid = invoice.getClientid();
            if (clientid != null) {
                clientid.getInvoiceList().remove(invoice);
                clientid = em.merge(clientid);
            }
            List<Salebralbum> salebralbumList = invoice.getSalebralbumList();
            for (Salebralbum salebralbumListSalebralbum : salebralbumList) {
                salebralbumListSalebralbum.setSaleid(null);
                salebralbumListSalebralbum = em.merge(salebralbumListSalebralbum);
            }
            List<Salebrtrack> salebrtrackList = invoice.getSalebrtrackList();
            for (Salebrtrack salebrtrackListSalebrtrack : salebrtrackList) {
                salebrtrackListSalebrtrack.setSaleid(null);
                salebrtrackListSalebrtrack = em.merge(salebrtrackListSalebrtrack);
            }
            em.remove(invoice);
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

    public List<Invoice> findInvoiceEntities() {
        return findInvoiceEntities(true, -1, -1);
    }

    public List<Invoice> findInvoiceEntities(int maxResults, int firstResult) {
        return findInvoiceEntities(false, maxResults, firstResult);
    }

    private List<Invoice> findInvoiceEntities(boolean all, int maxResults, int firstResult) {
       
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Invoice.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
      
    }

    public Invoice findInvoice(Integer id) {
        
            return em.find(Invoice.class, id);
       
    }

    public int getInvoiceCount() {
       
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Invoice> rt = cq.from(Invoice.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
       
    }
    
}

