/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.beans.LoginBean;
import com.gb4w21.musicalmoose.entities.Client;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class LoginController  implements Serializable{
    private final static Logger LOG = LoggerFactory.getLogger(LoginController.class);
    private LoginBean loginBean;
   
    @PersistenceContext
    private EntityManager entityManager;
    public LoginController(){
        
    }
    
    public LoginBean getLoginBean(){
        return loginBean;
    }
    public void setLoginBean(LoginBean loginBean){
        this.loginBean=loginBean;
    }
    public String toLoginPage(){
        loginBean=new  LoginBean();
        return "login";
    }
    public String signOut(){
        loginBean=new  LoginBean();
        loginBean.setLoggedIn(false);
        return null;
    }
    public void validateUser(FacesContext context, UIComponent component,
            Object value) {

        // These values have not yet been added to the bean
        // so they must be read from the user interface
        UIInput nameInput = (UIInput) component.findComponent("cname");
        UIInput passwordInput = (UIInput) component.findComponent("password");

        String userName = ((String) nameInput.getLocalValue());
        String password = ((String) passwordInput.getLocalValue());
       
        Client registeredClient= findUser(userName, password);
        if (registeredClient==null) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "loginError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            loginBean.setLoggedIn(false);
            throw new ValidatorException(message);
            
        }
        loginBean.setId(registeredClient.getClientid());
        loginBean.setLoggedIn(true);
        
    }
    
    private Client findUser(String userName, String password){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Client> client = cq.from(Client.class);
        cq.select(client);
        cq.where(cb.equal(client.get("userName"), userName), cb.equal(client.get("password"), password));
        TypedQuery<Client> query = entityManager.createQuery(cq);
        try{
            return query.getSingleResult();
        }
        catch(javax.persistence.NoResultException NoResultException){
            return null;
        }
    }
}
