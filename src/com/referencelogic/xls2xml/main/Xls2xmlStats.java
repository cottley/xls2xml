/*
 * Copyright (c) 2012, Reference Logic
 * All rights reserved.
 */
package com.referencelogic.xls2xml.main;

import java.util.concurrent.atomic.AtomicInteger;

public class Xls2xmlStats {

  private static int noOfFiles = 0;
  private static AtomicInteger noOfProcessedFiles = new AtomicInteger(0);
  
  public static void setNoOfFiles(int newNoOfFiles) {
    if (newNoOfFiles >= 0) {
      noOfFiles = newNoOfFiles;
    }
  }
  
  public static void recordFileProcessed() { noOfProcessedFiles.getAndIncrement(); }
  
  public static int getNoOfProcessedFiles() { return noOfProcessedFiles.get(); }
  
  public static String status() {
    String result = "No stats available yet.";
    
    if (noOfFiles > 0) {
      int processedCount = getNoOfProcessedFiles();
      if (processedCount <= noOfFiles) {
        result = "" + (processedCount / noOfFiles) + "% complete. " + processedCount + " out of " + noOfFiles;
      }
    }
    
    return result;
  }

}