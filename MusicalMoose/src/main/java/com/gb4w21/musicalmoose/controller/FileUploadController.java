/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.shaded.commons.io.FilenameUtils;
import org.primefaces.shaded.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles are file upload related methods for the track and album
 * management pages which allow users to import an image.
 *
 * @author Daniel
 */
@Named
@SessionScoped
public class FileUploadController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(FileUploadController.class);

    public FileUploadController() {
    }

    private UploadedFile file;

    /**
     * @return the file field
     */
    public UploadedFile getFile() {
        return file;
    }

    /**
     * Set the file object
     *
     * @param file
     */
    public void setFile(UploadedFile file) {
        this.file = file;
    }

    /**
     * Call the saveUploadedImage method to save the image to the project and
     * display a success message to the user
     *
     * @param event
     * @throws IOException
     */
    public void handleFileUpload(FileUploadEvent event) throws IOException {
        fileUpload(event, "album_covers");
    }
    
    /**
     * Call the saveUploadedImage method to save the image to the project and
     * display a success message to the user
     *
     * @param event
     * @throws IOException
     */
    public void handleFileAdUpload(FileUploadEvent event) throws IOException {
        fileUpload(event, "ads");
    }
    
    private void fileUpload(FileUploadEvent event, String fileName) {
        this.file = null;
        UploadedFile file = event.getFile();
        if (file != null && file.getContent() != null && file.getContent().length > 0 && file.getFileName() != null) {
            this.file = file;
            saveUploadedImage(fileName);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Success", this.file.getFileName() + " is uploaded."));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
        }
    }

    /**
     * Saves the input image file to the directory containing all album covers.
     */
    private void saveUploadedImage(String fileName) {
        InputStream inputStream = null;
        try {
            String currentClassPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            String destinationPath = currentClassPath.substring(0, currentClassPath.indexOf("target/")) + "src/main/webapp/" + fileName + "/";
            //String filename = FilenameUtils.getName(this.file.getFileName());
            inputStream = this.file.getInputStream();
            OutputStream outputStream = new FileOutputStream(new File(destinationPath, this.file.getFileName()));
            try {
                IOUtils.copy(inputStream, outputStream);
            } finally {
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(outputStream);
            }
        } catch (IOException ex) {
            LOG.error("Encountered error when saving image to project.");
        }
    }

    /**
     * Retrieves the filenames of all the images in the album_covers folder.
     *
     * @return the list of filenames
     */
    public List<String> getFileNames(String fileName) {
        List<String> fileNames = new ArrayList<>();

        String currentPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String path = currentPath.substring(0, currentPath.indexOf("target/")) + "src/main/webapp/" + fileName + "/";
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                fileNames.add(listOfFiles[i].getName());
            } 
        }
        return fileNames;
    }
}
