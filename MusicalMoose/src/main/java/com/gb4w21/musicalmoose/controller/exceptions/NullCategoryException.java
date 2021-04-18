/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller.exceptions;

/**
 * Error thrown if the report controller is given a null category for a search
 * @author Alesandro Dare
 * @version 1.0
 */
public class NullCategoryException extends Exception{
     public NullCategoryException(String message) {
        super(message);
    }
}
