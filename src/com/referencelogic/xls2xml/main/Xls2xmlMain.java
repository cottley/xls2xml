/*
 * Copyright (c) 2012, Reference Logic
 * All rights reserved.
 */
package com.referencelogic.xls2xml.main;

import java.util.Arrays;
import java.io.File;
import org.apache.commons.io.FileUtils;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;

import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.configuration.HierarchicalConfiguration;

public class Xls2xmlMain {
    
    private static final Logger log = Logger.getLogger( Xls2xmlMain.class );
    private static boolean isDebugging;
    private static final String configFileName = "xsl2xml.config.xml";
    private ExecutorService exec;
    public LandmarkList landmarks;
    
    public static void main(String args[]) {
        PropertyConfigurator.configure("log4j.properties");
         isDebugging = log.isDebugEnabled();
        new Xls2xmlMain().run();
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
          String ignoreformatting = landsub.getString("[@ignoreformatting]");
          
          landmarks.addLandmark(value, landmarkid, direction, distance, type, collectionid, identifies, section, maxblanklines, row, col, sheetNo, parentlandmarkid, substringsearch, matchnumber, ignoreformatting);
        }
      }
    }

    public void run() {
      try {
        XMLConfiguration config = new XMLConfiguration(configFileName);
        landmarks = new LandmarkList(config);
        String sourceDir = config.getString("source.path");
        List allowedExtensions = config.getList("source.extensions");
        String destDir = config.getString("destination.path");
        int poolSize = config.getInt("threadpool.size");
        populateLandmarkList(config);
        if (isDebugging) { log.debug("Loaded configuration successfully. Reading file list from: " + sourceDir + " with allowed extensions " + allowedExtensions); }
        Iterator iter =  FileUtils.iterateFiles(new File(sourceDir), (String[])allowedExtensions.toArray(new String[allowedExtensions.size()]), true);
        if (poolSize < 1) { poolSize = 5; }

        exec = Executors.newFixedThreadPool(poolSize);
        
        while(iter.hasNext()) {
          File file = (File) iter.next();
          exec.execute(new Xls2xmlConverter(file, config, landmarks));
        }
        
        exec.shutdown();

      } catch(ConfigurationException cex) {
        log.fatal("Unable to load config file " + configFileName + " to determine configuration.", cex);
      }   
    }
    
}
