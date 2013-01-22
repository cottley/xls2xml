/*
 * Copyright (c) 2012, Reference Logic
 * All rights reserved.
 */
package com.referencelogic.xls2xml.main;

import java.util.concurrent.atomic.AtomicInteger;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class Xls2xmlStats {

  private static int noOfFiles = 0;
  private static AtomicInteger noOfProcessedFiles = new AtomicInteger(0);
  private static Hashtable threadDetails = new Hashtable();
  private static String separator = System.getProperty( "line.separator" );  
  
  public static void setNoOfFiles(int newNoOfFiles) {
    if (newNoOfFiles >= 0) {
      noOfFiles = newNoOfFiles;
    }
  }
  
  public static void recordFileProcessed() { noOfProcessedFiles.getAndIncrement(); }
  
  public static int getNoOfProcessedFiles() { return noOfProcessedFiles.get(); }
  
  public static void setThreadFileProcess(String key, String value) {
    threadDetails.put(key, value);
  }
  
  public static String status() {
    String result = "No stats available yet.";
    
    if (noOfFiles > 0) {
      int processedCount = getNoOfProcessedFiles();
      if (processedCount <= noOfFiles) {
        result = "" + (new DecimalFormat("###.##")).format(((float)processedCount / (float)noOfFiles) * 100) + "% complete. " + processedCount + " out of " + noOfFiles;
        
        result += separator;
        
        synchronized(threadDetails) {
        
        Set set = threadDetails.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
          Map.Entry entry = (Map.Entry) it.next();
          result += "STATISTICS: Thread " + entry.getKey() + " : " + entry.getValue() + separator;
        }
        
        }
      }
    }
    
    return result;
  }

}