/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.util;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Daniel
 */
@Named
@SessionScoped
public class FileUploadController implements Serializable {

    public List<String> getFileNames() {
        List<String> fileNames = new ArrayList<>();

        String currentPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String path = currentPath.substring(0, currentPath.indexOf("target/")) + "src/main/webapp/album_covers/";
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
                fileNames.add(listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
        return fileNames;

    }
}
