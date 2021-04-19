/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.entities.Client;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import com.gb4w21.musicalmoose.entities.Sale;
import com.gb4w21.musicalmoose.entities.Review;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
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
public class ClientJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(BanneradJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    public void create(Client client) throws RollbackFailureException {
        if (client.getSaleList() == null) {
            client.setSaleList(new ArrayList<Sale>());
        }
        if (client.getReviewList() == null) {
            client.setReviewList(new ArrayList<Review>());
        }

        try {
            utx.begin();

            List<Sale> attachedSaleList = new ArrayList<Sale>();
            for (Sale saleListSaleToAttach : client.getSaleList()) {
                saleListSaleToAttach = em.getReference(saleListSaleToAttach.getClass(), saleListSaleToAttach.getSaleid());
                attachedSaleList.add(saleListSaleToAttach);
            }
            client.setSaleList(attachedSaleList);
            List<Review> attachedReviewList = new ArrayList<Review>();
            for (Review reviewListReviewToAttach : client.getReviewList()) {
                reviewListReviewToAttach = em.getReference(reviewListReviewToAttach.getClass(), reviewListReviewToAttach.getReviewid());
                attachedReviewList.add(reviewListReviewToAttach);
            }
            client.setReviewList(attachedReviewList);
            em.persist(client);
            for (Sale saleListSale : client.getSaleList()) {
                Client oldClientidOfSaleListSale = saleListSale.getClientid();
                saleListSale.setClientid(client);
                saleListSale = em.merge(saleListSale);
                if (oldClientidOfSaleListSale != null) {
                    oldClientidOfSaleListSale.getSaleList().remove(saleListSale);
                    oldClientidOfSaleListSale = em.merge(oldClientidOfSaleListSale);
                }
            }
            for (Review reviewListReview : client.getReviewList()) {
                Client oldClientidOfReviewListReview = reviewListReview.getClientid();
                reviewListReview.setClientid(client);
                reviewListReview = em.merge(reviewListReview);
                if (oldClientidOfReviewListReview != null) {
                    oldClientidOfReviewListReview.getReviewList().remove(reviewListReview);
                    oldClientidOfReviewListReview = em.merge(oldClientidOfReviewListReview);
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

    public void edit(Client client) throws NonexistentEntityException, Exception {

        try {
            utx.begin();
            Client persistentClient = em.find(Client.class, client.getClientid());
            List<Sale> saleListOld = persistentClient.getSaleList();
            List<Sale> saleListNew = client.getSaleList();
            List<Review> reviewListOld = persistentClient.getReviewList();
            List<Review> reviewListNew = client.getReviewList();
            List<Sale> attachedSaleListNew = new ArrayList<Sale>();
            for (Sale saleListNewSaleToAttach : saleListNew) {
                saleListNewSaleToAttach = em.getReference(saleListNewSaleToAttach.getClass(), saleListNewSaleToAttach.getSaleid());
                attachedSaleListNew.add(saleListNewSaleToAttach);
            }
            saleListNew = attachedSaleListNew;
            client.setSaleList(saleListNew);
            List<Review> attachedReviewListNew = new ArrayList<Review>();
            for (Review reviewListNewReviewToAttach : reviewListNew) {
                reviewListNewReviewToAttach = em.getReference(reviewListNewReviewToAttach.getClass(), reviewListNewReviewToAttach.getReviewid());
                attachedReviewListNew.add(reviewListNewReviewToAttach);
            }
            reviewListNew = attachedReviewListNew;
            client.setReviewList(reviewListNew);
            client = em.merge(client);
            for (Sale saleListOldSale : saleListOld) {
                if (!saleListNew.contains(saleListOldSale)) {
                    saleListOldSale.setClientid(null);
                    saleListOldSale = em.merge(saleListOldSale);
                }
            }
            for (Sale saleListNewSale : saleListNew) {
                if (!saleListOld.contains(saleListNewSale)) {
                    Client oldClientidOfSaleListNewSale = saleListNewSale.getClientid();
                    saleListNewSale.setClientid(client);
                    saleListNewSale = em.merge(saleListNewSale);
                    if (oldClientidOfSaleListNewSale != null && !oldClientidOfSaleListNewSale.equals(client)) {
                        oldClientidOfSaleListNewSale.getSaleList().remove(saleListNewSale);
                        oldClientidOfSaleListNewSale = em.merge(oldClientidOfSaleListNewSale);
                    }
                }
            }
            for (Review reviewListOldReview : reviewListOld) {
                if (!reviewListNew.contains(reviewListOldReview)) {
                    reviewListOldReview.setClientid(null);
                    reviewListOldReview = em.merge(reviewListOldReview);
                }
            }
            for (Review reviewListNewReview : reviewListNew) {
                if (!reviewListOld.contains(reviewListNewReview)) {
                    Client oldClientidOfReviewListNewReview = reviewListNewReview.getClientid();
                    reviewListNewReview.setClientid(client);
                    reviewListNewReview = em.merge(reviewListNewReview);
                    if (oldClientidOfReviewListNewReview != null && !oldClientidOfReviewListNewReview.equals(client)) {
                        oldClientidOfReviewListNewReview.getReviewList().remove(reviewListNewReview);
                        oldClientidOfReviewListNewReview = em.merge(oldClientidOfReviewListNewReview);
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
                Integer id = client.getClientid();
                if (findClient(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }

    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {

        try {
            utx.begin();
            Client client;
            try {
                client = em.getReference(Client.class, id);
                client.getClientid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The client with id " + id + " no longer exists.", enfe);
            }
            List<Sale> saleList = client.getSaleList();
            for (Sale saleListSale : saleList) {
                saleListSale.setClientid(null);
                saleListSale = em.merge(saleListSale);
            }
            List<Review> reviewList = client.getReviewList();
            for (Review reviewListReview : reviewList) {
                reviewListReview.setClientid(null);
                reviewListReview = em.merge(reviewListReview);
            }
            em.remove(client);
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

    public List<Client> findClientEntities() {
        return findClientEntities(true, -1, -1);
    }

    public List<Client> findClientEntities(int maxResults, int firstResult) {
        return findClientEntities(false, maxResults, firstResult);
    }

    private List<Client> findClientEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Client.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public Client findClient(Integer id) {
        return em.find(Client.class, id);
    }

    public int getClientCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Client> rt = cq.from(Client.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * find user with matching username and password
     *
     * @author Alessandro Dare
     * @param userName String
     * @param password String
     * @return
     */
    public Client findUser(String userName, String password) {
        LOG.info("User Name:" + userName);
        LOG.info("Password:" + password);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Client> client = cq.from(Client.class);
        cq.select(client);
        cq.where(cb.equal(client.get("username"), userName), cb.equal(client.get("password"), password));
        TypedQuery<Client> query = em.createQuery(cq);
        try {
            return query.getSingleResult();
        } catch (javax.persistence.NoResultException NoResultException) {
            return null;
        }
    }

}
