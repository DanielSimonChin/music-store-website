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
/**
 * the login controller that's responsible for login in both a client and a manager and signing them out
 * @author Alessandro Dare
 * @version 1.0
 */
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
   /**
    * if login bean is null it gives a blank one to avoid errors
    * @return LoginBean
    */
    public LoginBean getLoginBean(){
        if(loginBean==null){
            loginBean=new LoginBean();
        }
        return loginBean;
    }
    public void setLoginBean(LoginBean loginBean){
        this.loginBean=loginBean;
    }
    /**
     * takes a user to the login page
     * @return String web page address
     */
    public String toLoginPage(){
        loginBean = new LoginBean();
        FacesContext context=FacesContext.getCurrentInstance();
       
        loginLastPage=context.getViewRoot().getViewId();
        loginLastPage=loginLastPage.substring(1, loginLastPage.length() - 6);
        //stores the previously viested page if it can from the registration page
        if(loginLastPage.equals("register")||loginLastPage.equals("login")){

            loginLastPage=registrationController.getLastPageRegister();
            
        }
        
        //in case user cam from manager page
        if(cameFromManagerPage){
            cameFromManagerPage=false;
             
            return "index";
        }
       
        return "login";
    }
    public String getLoginLastPage(){
        return loginLastPage;
    }
    /**
     * sings the user out and returns them back to their previous page back to the index page if the user was a manager 
     * @return Previous page or index page
     */
    public String signOut(){
        loginBean = new LoginBean();
        loginBean.setLoggedIn(false);
         FacesContext context=FacesContext.getCurrentInstance();
        loginLastPage=context.getViewRoot().getViewId();
        loginLastPage=loginLastPage.substring(1, loginLastPage.length() - 6);
        LOG.info("log out form manager:"+cameFromManagerPage);
         if(cameFromManagerPage){
            cameFromManagerPage=false;
            
            return "index";
        }
        return loginLastPage;
    }
    /**
     * Validates if the user login information was correct and the information wasn't from a deactivated or false account
     * @param context
     * @param component
     * @param value 
     */
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
    /**
     * Logs the user in and sends the user to the their previously visited page 
     * if they log in as a client or the admin front page if they log in as a manager
     * or to the checkout page if they log in to finalized an order
     * @return String page address
     */
    public String loggIn(){
        LOG.info("username:"+loginBean.getUsername());
        LOG.info("password:"+loginBean.getPassword());
        Client registeredClient= clientJpaController.findUser(loginBean.getUsername(), loginBean.getPassword());
        loginBean.setId(registeredClient.getClientid());
        loginBean.setLoggedIn(true);
        loginBean.setEmailAddress(this.clientJpaController.findClient(loginBean.getId()).getEmail());
        LOG.info("Is manager:"+registeredClient.getIsmanager());
        //if the user logged in to finialize
        if (loginLastPage.equals("cart")){
            cameFromManagerPage=false;
            return checkoutController.toCheckout();
        }
        //if the user loged in as manager
        else if (registeredClient.getIsmanager()){
            cameFromManagerPage=true;
            return "adminfront";
        }
        //if the user just logged out as manager and logs in as a client
        else if(cameFromManagerPage){
            cameFromManagerPage=false;
            return "index";
        }
        // if the user logs in as a client
        else{
            cameFromManagerPage=false;
            return loginLastPage;
        }
    }
}
