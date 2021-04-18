/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller.exceptions;

/**
 * Error thrown if the report controller is given a nothing to conduct a specified search
 * @author Alesandro Dare
 * @version 1.0
 */
public class NullSearchValueException extends Exception {

    public NullSearchValueException(String message) {
        super(message);
    }

}
