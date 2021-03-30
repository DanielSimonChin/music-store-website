/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.beans;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Named(value = "loginBean")
@Dependent
public class LoginBean {
    private String password;
    private String username;
    private int id;
    private boolean loggedIn;
    public LoginBean(){
        
    }
    public String getPassword(){
        return password;
    }
     public int getId(){
        return id;
    }
      public String getUsername(){
        return username;
    }
       public boolean isLoggedIn(){
        return loggedIn;
    }
       public void setPassword(String password){
         this.password=password;
    }
     public void setId(int id){
         this.id=id;
    }
      public void setUsername(String username){
         this.username=username;
    }
       public void setLoggedIn(boolean loggedIn){
         this.loggedIn=loggedIn;
    }
}
