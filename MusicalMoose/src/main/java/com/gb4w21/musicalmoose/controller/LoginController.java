/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.controller.CheckoutController;
import com.gb4w21.musicalmoose.beans.LoginBean;
import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.entities.Client;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
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
  //  private final static String[] adminPages;
    private final static Logger LOG = LoggerFactory.getLogger(LoginController.class);
    private LoginBean loginBean;
    private String loginLastPage;
    @PersistenceContext
    private EntityManager entityManager;
    @Inject
    private ClientJpaController clientJpaController;
    @Inject
    private CheckoutController checkoutController;
    @Inject
    RegistrationController registrationController;
    private boolean cameFromManagerPage=false;
    public LoginController(){
        
    }
   
    public LoginBean getLoginBean(){
        if(loginBean==null){
            loginBean=new LoginBean();
        }
        return loginBean;
    }
    public void setLoginBean(LoginBean loginBean){
        this.loginBean=loginBean;
    }
    public String toLoginPage(){
        loginBean = new LoginBean();
        FacesContext context=FacesContext.getCurrentInstance();
       
        loginLastPage=context.getViewRoot().getViewId();
        loginLastPage=loginLastPage.substring(1, loginLastPage.length() - 6);
        if(loginLastPage.equals("register")||loginLastPage.equals("login")){
            LOG.debug("LOGIN 1");
            loginLastPage=registrationController.getLastPageRegister();
             LOG.debug("LOGIN 2"+loginLastPage);
        }
        if(cameFromManagerPage){
            cameFromManagerPage=false;
             LOG.debug("LOGIN 3");
            return "index";
        }
         LOG.debug("LOGIN 4");
        return "login";
    }
    public String getLoginLastPage(){
        return loginLastPage;
    }
    public String signOut(){
        loginBean = new LoginBean();
        loginBean.setLoggedIn(false);
         FacesContext context=FacesContext.getCurrentInstance();
        loginLastPage=context.getViewRoot().getViewId();
        loginLastPage=loginLastPage.substring(1, loginLastPage.length() - 6);
         if(cameFromManagerPage){
            cameFromManagerPage=false;
             LOG.debug("LOGIN 3");
            return "index";
        }
        return loginLastPage;
    }
    
    public void validateUser(FacesContext context, UIComponent component,
            Object value) {
        // These values have not yet been added to the bean
        // so they must be read from the user interface
        UIInput nameInput = (UIInput) component.findComponent("cname");
        String userName = ((String) nameInput.getLocalValue());
        String password = value.toString();
        Client registeredClient= clientJpaController.findUser(userName, password);
        if (registeredClient==null||!registeredClient.getClientactive()) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "loginError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            loginBean.setLoggedIn(false);
            throw new ValidatorException(message);
        }
    }
    
    public String loggIn(){
        LOG.info("username:"+loginBean.getUsername());
        LOG.info("password:"+loginBean.getPassword());
        Client registeredClient= clientJpaController.findUser(loginBean.getUsername(), loginBean.getPassword());
        loginBean.setId(registeredClient.getClientid());
        loginBean.setLoggedIn(true);
        loginBean.setEmailAddress(this.clientJpaController.findClient(loginBean.getId()).getEmail());
        LOG.info("Is manager:"+registeredClient.getIsmanager());
        
        if (loginLastPage.equals("cart")){
            cameFromManagerPage=false;
            return checkoutController.toCheckout();
        }
        else if (registeredClient.getIsmanager()){
            cameFromManagerPage=true;
            return "adminfront";
        }
        else if(cameFromManagerPage){
            cameFromManagerPage=false;
            return "index";
        }
        else{
            cameFromManagerPage=false;
            return loginLastPage;
        }
    }
}
