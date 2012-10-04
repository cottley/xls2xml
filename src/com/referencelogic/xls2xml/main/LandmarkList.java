package com.referencelogic.xls2xml.main;

import org.apache.log4j.Logger;

import java.util.ArrayList;

import org.apache.commons.configuration.XMLConfiguration;

import java.util.HashSet;
import java.util.Set;
import java.util.Hashtable;

public class LandmarkList {

  private static final Logger log = Logger.getLogger( LandmarkList.class );

  protected ArrayList landmarks = new ArrayList();
  protected boolean isDebugging;
  protected String ignorechars;
  protected boolean ignorecase;
  protected boolean substringsearch;
  protected boolean ignorewhitespaces;

  public LandmarkList(XMLConfiguration config) {
    isDebugging = log.isDebugEnabled();
    ignorechars = config.getString("conversion.searchparams.ignorechars");
    ignorecase = config.getBoolean("conversion.searchparams.ignorecase");
    substringsearch = config.getBoolean("conversion.searchparams.substringsearch");
    ignorewhitespaces = config.getBoolean("conversion.searchparams.ignorewhitespaces");
    
    if (isDebugging) {
      log.debug("Search parameters: Substring search - " + substringsearch + ", Ignore case - " + ignorecase + ", Characters to ignore - '" + ignorechars + "', Ignore white spaces - '" + ignorewhitespaces + "'"); 
    }
  }

  public void addLandmark(String value, String landmarkid, String direction, String distance, String type, String collectionid, String scope, String section, String row, String col, String sheetNo) {
    Landmark landmark = new Landmark();

    landmark.setValue(value);
    landmark.setId(landmarkid);
    landmark.setDirection(direction);
    landmark.setDistance(distance);
    landmark.setCollectionType(type);
    landmark.setCollectionId(collectionid);
    landmark.setCollectionScope(scope);
    landmark.setCollectionSection(section);
    landmark.setRow(row);
    landmark.setCol(col);
    landmark.setSheetNo(sheetNo);
    
    log.debug(landmark);

    landmarks.add(landmark);
  }
  
  public Set getLandmarksFor(String cellValue) {
    Set result = new HashSet();
    
    String ignorecharsset = ignorechars;
    
    for (int i = 0; i < landmarks.size(); i++) {
      String landmarkValue = ((Landmark)landmarks.get(i)).getValue();
      if (ignorecase) {
         landmarkValue = landmarkValue.toUpperCase();
         cellValue = cellValue.toUpperCase();
         ignorecharsset = ignorecharsset.toUpperCase();
      }
      
      if (ignorecharsset.length() > 0) {
        for (int charindex = 0; charindex < ignorecharsset.length(); charindex++) {
          landmarkValue = landmarkValue.replace("" + ignorecharsset.charAt(charindex), "");
          cellValue = cellValue.replace("" + ignorecharsset.charAt(charindex), "");
        }
      }
      
      if (ignorewhitespaces) {
        landmarkValue = landmarkValue.replaceAll("\\s", "");
        cellValue = cellValue.replaceAll("\\s", "");
      }
      
      log.trace("Landmark value before compare:'" + landmarkValue + "'");
      log.trace("Cell value before compare:'" + cellValue + "'"); 
      
      if (substringsearch) {
        if (cellValue.indexOf(landmarkValue) != -1) {
          result.add(i);
        }
      } else { // If matches
        if (cellValue.equals(landmarkValue)) {
          result.add(i);
        }
      }
      
    }
    
    return result;
  }

  public int size() {
    return landmarks.size();
  }
  
  public Hashtable getLandmarkIdsForScope(String scope) {
    Hashtable result = new Hashtable();
    
    for (int i = 0; i < landmarks.size(); i++) {
      Landmark thisLandmark = (Landmark)landmarks.get(i);
      if (thisLandmark.getCollectionScope().equalsIgnoreCase(scope)) {
        result.put(thisLandmark.getCollectionId(), thisLandmark.getCollectionId()); 
      }
    }
    
    return result;
  }
  
  public ArrayList getIndexesForId(String id) {
    ArrayList result = new ArrayList();
  
    for (int i = 0; i < landmarks.size(); i++) {
      Landmark thisLandmark = (Landmark)landmarks.get(i);
      if (thisLandmark.getCollectionId().equals(id)) {
        result.add(i); 
      }
    }
    
    return result;
  }
}
