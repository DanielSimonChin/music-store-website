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
import com.gb4w21.musicalmoose.entities.Client;
import com.gb4w21.musicalmoose.entities.Invoicedetail;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import com.gb4w21.musicalmoose.entities.Sale;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
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
public class SaleJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(SaleJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    public void create(Sale sale) throws RollbackFailureException {
        if (sale.getInvoicedetailList() == null) {
            sale.setInvoicedetailList(new ArrayList<Invoicedetail>());
        }
        try {
            utx.begin();

            Client clientid = sale.getClientid();
            if (clientid != null) {
                clientid = em.getReference(clientid.getClass(), clientid.getClientid());
                sale.setClientid(clientid);
            }
            List<Invoicedetail> attachedInvoicedetailList = new ArrayList<Invoicedetail>();
            for (Invoicedetail invoicedetailListInvoicedetailToAttach : sale.getInvoicedetailList()) {
                invoicedetailListInvoicedetailToAttach = em.getReference(invoicedetailListInvoicedetailToAttach.getClass(), invoicedetailListInvoicedetailToAttach.getInvoiceid());
                attachedInvoicedetailList.add(invoicedetailListInvoicedetailToAttach);
            }
            sale.setInvoicedetailList(attachedInvoicedetailList);
            em.persist(sale);
            if (clientid != null) {
                clientid.getSaleList().add(sale);
                clientid = em.merge(clientid);
            }
            for (Invoicedetail invoicedetailListInvoicedetail : sale.getInvoicedetailList()) {
                Sale oldSaleidOfInvoicedetailListInvoicedetail = invoicedetailListInvoicedetail.getSaleid();
                invoicedetailListInvoicedetail.setSaleid(sale);
                invoicedetailListInvoicedetail = em.merge(invoicedetailListInvoicedetail);
                if (oldSaleidOfInvoicedetailListInvoicedetail != null) {
                    oldSaleidOfInvoicedetailListInvoicedetail.getInvoicedetailList().remove(invoicedetailListInvoicedetail);
                    oldSaleidOfInvoicedetailListInvoicedetail = em.merge(oldSaleidOfInvoicedetailListInvoicedetail);
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

    public void edit(Sale sale) throws NonexistentEntityException, Exception {

        try {
            utx.begin();
            Sale persistentSale = em.find(Sale.class, sale.getSaleid());
            Client clientidOld = persistentSale.getClientid();
            Client clientidNew = sale.getClientid();
            List<Invoicedetail> invoicedetailListOld = persistentSale.getInvoicedetailList();
            List<Invoicedetail> invoicedetailListNew = sale.getInvoicedetailList();
            if (clientidNew != null) {
                clientidNew = em.getReference(clientidNew.getClass(), clientidNew.getClientid());
                sale.setClientid(clientidNew);
            }
            List<Invoicedetail> attachedInvoicedetailListNew = new ArrayList<Invoicedetail>();
            for (Invoicedetail invoicedetailListNewInvoicedetailToAttach : invoicedetailListNew) {
                invoicedetailListNewInvoicedetailToAttach = em.getReference(invoicedetailListNewInvoicedetailToAttach.getClass(), invoicedetailListNewInvoicedetailToAttach.getInvoiceid());
                attachedInvoicedetailListNew.add(invoicedetailListNewInvoicedetailToAttach);
            }
            invoicedetailListNew = attachedInvoicedetailListNew;
            sale.setInvoicedetailList(invoicedetailListNew);
            sale = em.merge(sale);
            if (clientidOld != null && !clientidOld.equals(clientidNew)) {
                clientidOld.getSaleList().remove(sale);
                clientidOld = em.merge(clientidOld);
            }
            if (clientidNew != null && !clientidNew.equals(clientidOld)) {
                clientidNew.getSaleList().add(sale);
                clientidNew = em.merge(clientidNew);
            }
            for (Invoicedetail invoicedetailListOldInvoicedetail : invoicedetailListOld) {
                if (!invoicedetailListNew.contains(invoicedetailListOldInvoicedetail)) {
                    invoicedetailListOldInvoicedetail.setSaleid(null);
                    invoicedetailListOldInvoicedetail = em.merge(invoicedetailListOldInvoicedetail);
                }
            }
            for (Invoicedetail invoicedetailListNewInvoicedetail : invoicedetailListNew) {
                if (!invoicedetailListOld.contains(invoicedetailListNewInvoicedetail)) {
                    Sale oldSaleidOfInvoicedetailListNewInvoicedetail = invoicedetailListNewInvoicedetail.getSaleid();
                    invoicedetailListNewInvoicedetail.setSaleid(sale);
                    invoicedetailListNewInvoicedetail = em.merge(invoicedetailListNewInvoicedetail);
                    if (oldSaleidOfInvoicedetailListNewInvoicedetail != null && !oldSaleidOfInvoicedetailListNewInvoicedetail.equals(sale)) {
                        oldSaleidOfInvoicedetailListNewInvoicedetail.getInvoicedetailList().remove(invoicedetailListNewInvoicedetail);
                        oldSaleidOfInvoicedetailListNewInvoicedetail = em.merge(oldSaleidOfInvoicedetailListNewInvoicedetail);
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
                Integer id = sale.getSaleid();
                if (findSale(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Sale sale;
            try {
                sale = em.getReference(Sale.class, id);
                sale.getSaleid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sale with id " + id + " no longer exists.", enfe);
            }
            Client clientid = sale.getClientid();
            if (clientid != null) {
                clientid.getSaleList().remove(sale);
                clientid = em.merge(clientid);
            }
            List<Invoicedetail> invoicedetailList = sale.getInvoicedetailList();
            for (Invoicedetail invoicedetailListInvoicedetail : invoicedetailList) {
                invoicedetailListInvoicedetail.setSaleid(null);
                invoicedetailListInvoicedetail = em.merge(invoicedetailListInvoicedetail);
            }
            em.remove(sale);
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

    public List<Sale> findSaleEntities() {
        return findSaleEntities(true, -1, -1);
    }

    public List<Sale> findSaleEntities(int maxResults, int firstResult) {
        return findSaleEntities(false, maxResults, firstResult);
    }

    private List<Sale> findSaleEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Sale.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public Sale findSale(Integer id) {
        return em.find(Sale.class, id);
    }

    public int getSaleCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Sale> rt = cq.from(Sale.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * Retrieve the total gross value of a track's sales
     *
     * @param inventoryid
     * @return the total gross sales of a track
     */
    public Double getTotalTrackSales(int inventoryid) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Invoicedetail> cq = cb.createQuery(Invoicedetail.class);

        Root<Invoicedetail> invoice = cq.from(Invoicedetail.class);

        //We want all the invoicedetail objects that have the parameter inventoryid
        cq.where(cb.equal(invoice.get("inventoryid").get("inventoryid"), inventoryid));

        Query q = em.createQuery(cq);

        List<Invoicedetail> invoices = q.getResultList();

        Float totalSales = 0.0f;

        for (Invoicedetail i : invoices) {
            totalSales += i.getCurrentcost();
        }
        return totalSales.doubleValue();
    }

    /**
     * Retrieve the total gross value of an album's sales
     *
     * @param albumid
     * @return the total gross sales of an album
     */
    public Double getTotalAlbumSales(int albumid) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Invoicedetail> cq = cb.createQuery(Invoicedetail.class);

        Root<Invoicedetail> invoice = cq.from(Invoicedetail.class);

        //We want all the invoicedetail objects that have the parameter albumid
        cq.where(cb.equal(invoice.get("albumid").get("albumid"), albumid));

        Query q = em.createQuery(cq);

        List<Invoicedetail> invoices = q.getResultList();

        Float totalSales = 0.0f;

        for (Invoicedetail i : invoices) {
            totalSales += i.getCurrentcost();
        }
        return totalSales.doubleValue();
    }
    
    public List<Sale> findSaleByClientId(int clientId) throws NonexistentEntityException {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Sale> cq = cb.createQuery(Sale.class);

        Root<Sale> sale = cq.from(Sale.class);

        cq.where(cb.equal(sale.get("clientid").get("clientid"), clientId)); 

        Query q = em.createQuery(cq);
        
        List<Sale> results = q.getResultList();
        
        if (results.size() == 0) {
            throw new NonexistentEntityException("Cannot find Album with id");
        }

        return results;
    }
}
