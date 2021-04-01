/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.beans.LoginBean;
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
    private final static Logger LOG = LoggerFactory.getLogger(LoginController.class);
    private LoginBean loginBean;
    private String loginLastPage;
    @PersistenceContext
    private EntityManager entityManager;
    @Inject
    private ClientJpaController clientJpaController;
    @Inject
    RegistrationController registrationController;
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
        loginBean=new  LoginBean();
        FacesContext context=FacesContext.getCurrentInstance();
        loginLastPage=context.getViewRoot().getViewId();
        loginLastPage=loginLastPage.substring(1, loginLastPage.length() - 6);
        if(loginLastPage.equals("register")||loginLastPage.equals("login")){
            loginLastPage=registrationController.getLastPageRegister();
        }
        return "login";
    }
    public String getLoginLastPage(){
        return loginLastPage;
    }
    public String signOut(){
        loginBean = new LoginBean();
        loginBean.setLoggedIn(false);
        return "index";
    }
    public void validateUser(FacesContext context, UIComponent component,
            Object value) {
        // These values have not yet been added to the bean
        // so they must be read from the user interface
        UIInput nameInput = (UIInput) component.findComponent("cname");
        String userName = ((String) nameInput.getLocalValue());
        String password = value.toString();
        Client registeredClient= clientJpaController.findUser(userName, password);
        if (registeredClient==null) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "loginError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            loginBean.setLoggedIn(false);
            throw new ValidatorException(message);
            
        }
        
        
    }
    public String loggIn(){
        Client registeredClient= clientJpaController.findUser(loginBean.getUsername(), loginBean.getPassword());
        loginBean.setId(registeredClient.getClientid());
        loginBean.setLoggedIn(true);
        
        if (loginLastPage.equals("cart")) {
            return "checkout";
        }
        return loginLastPage;
    }
   
}
