/*
 * Copyright (c) 2012, Reference Logic
 * All rights reserved.
 */
package com.referencelogic.xls2xml.main;

import java.util.Arrays;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;

import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.HierarchicalConfiguration;

import java.net.ServerSocket;

public class Xls2xmlMain {
    
    private static final Logger log = Logger.getLogger( Xls2xmlMain.class );
    private static boolean isDebugging;
    private static final String configFileName = "xls2xml.config.xml";
    private static boolean runModifiedOnly = false;
    private static boolean ignoreExisting = false;
    private static boolean matchRegex = false;
    private static boolean convertSingleFile = false;
    private static String convertSingleFilePath = "";
    private static String matchRegexStr = "";
    private ExecutorService exec;
    public LandmarkList landmarks;
    private ServerSocket pid;
    
    public static void main(String args[]) {
        PropertyConfigurator.configure("log4j.properties");
        isDebugging = log.isDebugEnabled();
        for (String s : args)
        {
          if (s.equalsIgnoreCase("--modified")) {
            runModifiedOnly = true;
          }
          
          if (s.equalsIgnoreCase("--ignore-existing")) {
            ignoreExisting = true;
          }
                    
          if (matchRegex && matchRegexStr.equals("")) {
            matchRegexStr = s;
            log.debug("Set regex string to: " + matchRegexStr);
          }
          
          if (s.equalsIgnoreCase("--restrict")) {
            matchRegex = true;
            log.debug("Got restrict flag, so matching regex");
          }

          if (convertSingleFile && convertSingleFilePath.equalsIgnoreCase("")) {
            convertSingleFilePath = s;
            log.debug("Setting single file path conversion to: " + s);
          }

          if (s.equalsIgnoreCase("--csf")) {
            convertSingleFile = true;
            log.debug("Got convert single file flag, so only trying to convert one file");
          }      
        }

        if (convertSingleFile) {

          new Xls2xmlMain().runSingleFile(convertSingleFilePath);

        } else {

          new Xls2xmlMain().run();

        }
    }
    
    public void populateLandmarkList(XMLConfiguration config) {
      List<HierarchicalConfiguration> collections;
      collections = config.configurationsAt("conversion.landmarks.collection");

      for(HierarchicalConfiguration sub : collections)
      {
        String type = sub.getString("[@type]");
        String collectionid = sub.getString("[@id]");
        String identifies = sub.getString("[@identifies]");
        String section = sub.getString("[@section]");
        String maxblanklines = sub.getString("[@maxblanklines]");

        List<HierarchicalConfiguration> landmarksInCollection = sub.configurationsAt("landmark");

        for(HierarchicalConfiguration landsub : landmarksInCollection)
        {
          String value = landsub.getString(".");
          String landmarkid = landsub.getString("[@id]");
          String direction = landsub.getString("[@direction]");
          String distance = landsub.getString("[@distance]");
          String row = landsub.getString("[@row]");
          String col = landsub.getString("[@col]");
          String sheetNo = landsub.getString("[@sheetno]");
          String parentlandmarkid = landsub.getString("[@parentlandmarkid]");
          String substringsearch = landsub.getString("[@substringsearch]");
          String matchnumber = landsub.getString("[@matchnumber]");
          String ignorecase = landsub.getString("[@ignorecase]");
	        String ignorewhitespaces = landsub.getString("[@ignorewhitespaces]");
	        String ignorechars =  landsub.getString("[@ignorechars]");
          String ignoreformatting = landsub.getString("[@ignoreformatting]");
          
          landmarks.addLandmark(value, landmarkid, direction, distance, type, collectionid, identifies, section, maxblanklines, row, col, sheetNo, parentlandmarkid, substringsearch, matchnumber, ignorecase, ignorewhitespaces, ignorechars, ignoreformatting);
        }
      }
    }


    public void runSingleFile(String filepath) {
      try {

        XMLConfiguration config = new XMLConfiguration(configFileName);
        landmarks = new LandmarkList(config);
        populateLandmarkList(config);

        File file = new File(filepath);
        File specificDestFile = new File(filepath + ".xml");

        new Xls2xmlConverter(file, specificDestFile, config, landmarks, ignoreExisting).runSingleFile();

      } catch(ConfigurationException cex) {
        log.fatal("Unable to load config file " + configFileName + " to determine configuration.", cex);
      }
    }


    public void run() {
      try {
        int noOfFilesToProcess = 0;
      
        XMLConfiguration config = new XMLConfiguration(configFileName);
        landmarks = new LandmarkList(config);
        String sourceDir = config.getString("source.path");
        List allowedExtensions = config.getList("source.extensions");
        String destDir = config.getString("destination.path");
        int poolSize = config.getInt("threadpool.size");
        populateLandmarkList(config);
        
        boolean waitToRun = true;
        while (waitToRun) {
          try {
             // Use socket number as PID
             pid = new ServerSocket(1901);
             waitToRun = false;
          } catch (Exception e) {
             log.error("Could not lock port", e);
             try {
               // Sleep between 1 and 60 seconds before trying to bind again
               Thread.sleep((1 + (int)(Math.random() * ((60 - 1) + 1))) * 1000);
             } catch (InterruptedException ie) {
             
             }
          }
        }
        log.info("Starting to run");
        
        
        if (isDebugging) { log.debug("Loaded configuration successfully. Reading file list from: " + sourceDir + " with allowed extensions " + allowedExtensions); }
        Iterator iter =  FileUtils.iterateFiles(new File(sourceDir), (String[])allowedExtensions.toArray(new String[allowedExtensions.size()]), true);
        if (poolSize < 1) { poolSize = 5; }

        exec = Executors.newFixedThreadPool(poolSize);
        
        long lastmodifieddatetime = 0L;

        File modifiedfile  = new File("xls2xml.modified");
        if (isDebugging) { log.debug("Created new modified file."); }
        
        if (runModifiedOnly) {
          // If file exists
          if (modifiedfile.exists()) {
            // Get last modified date for file
            lastmodifieddatetime = modifiedfile.lastModified();
          }
        }
        
        try {
          modifiedfile.delete();
          if (isDebugging) { log.debug("Tried to delete modified file tracker"); }
          modifiedfile.createNewFile();
          if (isDebugging) { log.debug("Tried to create modified file tracker"); }
        } catch (IOException ioe) {
          // Do nothing
          if (isDebugging) { log.debug("Unable to create/update modified file tracker", ioe); }
        }

        while(iter.hasNext()) {
          File file = (File) iter.next();
          String filePath = "";
          try {
            filePath = file.getCanonicalPath();
            log.debug("Canonical path being processed is: " + filePath);
          } catch (IOException ioe) {
            log.warn("Unable to get canonical path from file", ioe);
          }
          if (((runModifiedOnly) && (file.lastModified() >= lastmodifieddatetime)) || (!runModifiedOnly)){
            
            if ((!matchRegex) || (matchRegex && filePath.matches(matchRegexStr))) {
             
              if (!ignoreExisting) {
                exec.execute(new Xls2xmlConverter(file, config, landmarks, ignoreExisting));
                noOfFilesToProcess++;
              } else {
                String sourceFilePath = file.toString();
                String destFilePath = sourceFilePath.substring(sourceDir.length());
                if (destFilePath.startsWith(File.separator)) { destFilePath = destFilePath.substring(1); } 
                // Add extensions with .xml
                destFilePath = destFilePath + ".xml";  
                File destFile = new File(destDir, destFilePath);
                
                if (ignoreExisting && destFile.exists() && (FileUtils.sizeOf(destFile) > 0)) {
                  log.info("Ignoring the recreation of file: " + destFilePath);
                  log.debug("Ignored filesize is: " + FileUtils.sizeOf(destFile));
                } else {
                  log.info("Queueing the creation of file: " + destFilePath);
                  exec.execute(new Xls2xmlConverter(file, config, landmarks, ignoreExisting));
                  noOfFilesToProcess++;
                }
        
              }
            }
            
          }
        }
        
        Xls2xmlStats.setNoOfFiles(noOfFilesToProcess);
        
        exec.shutdown();
        try {
          while(!exec.isTerminated()) {
            exec.awaitTermination(30, TimeUnit.SECONDS); 
          }
        } catch (InterruptedException ie) {
          // Do nothing, going to exit anyway
        }

      } catch(ConfigurationException cex) {
        log.fatal("Unable to load config file " + configFileName + " to determine configuration.", cex);
      }   
      
      try {
         pid.close();
      } catch (Exception e) {
         log.error("Unable to release port!");
      }
    }
    
}
