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

  public void addLandmark(String value, String landmarkid, String direction, String distance, String type, String collectionid, String identifies, String section, String maxblanklines, String row, String col, String sheetNo, String parentlandmarkid, String substringsearch, String matchnumber, String ignorecase, String ignorewhitespaces, String ignorechars, String ignoreformatting) {
    Landmark landmark = new Landmark();

    landmark.setValue(value);
    landmark.setId(landmarkid);
    landmark.setDirection(direction);
    landmark.setDistance(distance);
    landmark.setCollectionType(type);
    landmark.setCollectionId(collectionid);
    landmark.setCollectionIdentifier(identifies);
    landmark.setCollectionSection(section);
    landmark.setCollectionMaxBlankLines(maxblanklines);
    landmark.setRow(row);
    landmark.setCol(col);
    landmark.setSheetNo(sheetNo);
    landmark.setParentLandmarkId(parentlandmarkid);
    landmark.setSubstringSearch(substringsearch);
    landmark.setMatchNumber(matchnumber);
    landmark.setIgnoreCase(ignorecase);
    landmark.setIgnoreWhitespaces(ignorewhitespaces);
    landmark.setIgnoreChars(ignorechars);
    landmark.setIgnoreFormatting(ignoreformatting);
    
    log.debug(landmark);

    landmarks.add(landmark);
  }
  
  public Set getLandmarksFor(String cellValue) {
    Set result = new HashSet();
    
    String ignorecharsset = ignorechars;
    
    for (int i = 0; i < landmarks.size(); i++) {
      Landmark currentLandmark = (Landmark)landmarks.get(i); 
      String landmarkValue = currentLandmark.getValue();
      
      if (landmarkValue != null) {

        boolean ignorecaselandmark = ignorecase;
        if (currentLandmark.getIgnoreCase() != null) {
          if (currentLandmark.getIgnoreCase().equalsIgnoreCase("true")) {
            ignorecaselandmark = true;
          } else if (currentLandmark.getIgnoreCase().equalsIgnoreCase("false")) {
            ignorecaselandmark = false;
          }
        }

        if (ignorecaselandmark) {
           landmarkValue = landmarkValue.toUpperCase();
           cellValue = cellValue.toUpperCase();
           ignorecharsset = ignorecharsset.toUpperCase();
        }

        String ignorecharssetlandmark = ignorecharsset;
        if (currentLandmark.getIgnoreChars() != null) {
          ignorecharssetlandmark = currentLandmark.getIgnoreChars();
        }
      
        if (ignorecharssetlandmark.length() > 0) {
          for (int charindex = 0; charindex < ignorecharssetlandmark.length(); charindex++) {
            landmarkValue = landmarkValue.replace("" + ignorecharssetlandmark.charAt(charindex), "");
            cellValue = cellValue.replace("" + ignorecharssetlandmark.charAt(charindex), "");
          }
        }

        boolean ignorewhitespaceslandmark = ignorewhitespaces;
        if (currentLandmark.getIgnoreWhitespaces() != null) {
          if (currentLandmark.getIgnoreWhitespaces().equalsIgnoreCase("true")) {
            ignorewhitespaceslandmark = true;
          } else if (currentLandmark.getIgnoreWhitespaces().equalsIgnoreCase("false")) {
            ignorewhitespaceslandmark = false;
          }
        }
      
        if (ignorewhitespaceslandmark) {
          landmarkValue = landmarkValue.replaceAll("\\s", "");
          cellValue = cellValue.replaceAll("\\s", "");
        }
      
        log.trace("Landmark value before compare:'" + landmarkValue + "'");
        log.trace("Cell value before compare:'" + cellValue + "'"); 
 
        boolean substringsearchforcurrentlandmark = substringsearch;
        if (currentLandmark.getSubstringSearch() != null) {
          if (currentLandmark.getSubstringSearch().equalsIgnoreCase("true")) {
            substringsearchforcurrentlandmark = true;
          } else if (currentLandmark.getSubstringSearch().equalsIgnoreCase("false")) {
            substringsearchforcurrentlandmark = false;
          }
        }
      
        if (substringsearchforcurrentlandmark) {
          if (cellValue.indexOf(landmarkValue) != -1) {
            result.add(i);
          }
        } else { // If matches
          if (cellValue.equals(landmarkValue)) {
            result.add(i);
          }
        }
      
      }
    }
    
    return result;
  }

  public int size() {
    return landmarks.size();
  }
  
  public Hashtable getLandmarkCollectionIdsForIdentifier(String identifier) {
    Hashtable result = new Hashtable();
    
    for (int i = 0; i < landmarks.size(); i++) {
      Landmark thisLandmark = (Landmark)landmarks.get(i);
      if (thisLandmark.getCollectionIdentifier().equalsIgnoreCase(identifier)) {
        result.put(thisLandmark.getCollectionId(), thisLandmark.getCollectionId()); 
      }
    }
    
    return result;
  }

  public Hashtable getLandmarkSectionsForIdentifier(String identifier) {
    Hashtable result = new Hashtable();
    
    for (int i = 0; i < landmarks.size(); i++) {
      Landmark thisLandmark = (Landmark)landmarks.get(i);
      if (thisLandmark.getCollectionIdentifier().equalsIgnoreCase(identifier)) {
        if (thisLandmark.getCollectionSection() != null) {
          result.put(thisLandmark.getCollectionSection(), thisLandmark.getCollectionSection()); 
        }
      }
    }
    
    return result;
  }
  
  public Hashtable getLandmarkIdsForIdentifier(String identifier, String type) {
    Hashtable result = new Hashtable();
    
    for (int i = 0; i < landmarks.size(); i++) {
      Landmark thisLandmark = (Landmark)landmarks.get(i);
      if (thisLandmark.getCollectionIdentifier().equalsIgnoreCase(identifier) && 
          thisLandmark.getCollectionType().equalsIgnoreCase(type)) {
        result.put(thisLandmark.getId(), thisLandmark.getId()); 
      }
    }
    
    return result;
  }

  public Landmark getLandmark(int landmarkNumber) {
    Landmark result = null;
    if ((landmarkNumber > -1) && (landmarkNumber < landmarks.size())) {
      result = (Landmark)landmarks.get(landmarkNumber);
    }
    return result;
  }

  public int getLandmarkNumberFromId(String landmarkId) {
    int result = -1;
    for (int i = 0; i < landmarks.size(); i++) {
      Landmark thisLandmark = (Landmark)landmarks.get(i);
      if (thisLandmark.getId() != null) {
        if (thisLandmark.getId().equals(landmarkId)) {
          result = i;
          break;
        }
      }
    }

    return result;
  }
  
  public Landmark getLandmark(String landmarkId) {
    return getLandmark(getLandmarkNumberFromId(landmarkId));
  }

  public ArrayList getIndexesForId(String id) {
    ArrayList result = new ArrayList();
  
    for (int i = 0; i < landmarks.size(); i++) {
      Landmark thisLandmark = (Landmark)landmarks.get(i);
      if (thisLandmark != null) {
        if (thisLandmark.getCollectionId() != null) {
          if (thisLandmark.getCollectionId().equals(id)) {
            result.add(i); 
          }
        }
      }
    }
    
    return result;
  }
  
  public Hashtable getLandmarkIdsForIdentifierForSectionsForType(String identifier, String section, String type) {
    Hashtable result = new Hashtable();
    
    for (int i = 0; i < landmarks.size(); i++) {
      Landmark thisLandmark = (Landmark)landmarks.get(i);
      if (thisLandmark.getCollectionIdentifier().equalsIgnoreCase(identifier)) {
        if (thisLandmark.getCollectionSection() != null) {
          if (thisLandmark.getCollectionSection().equalsIgnoreCase(section)) {
            if (thisLandmark.getCollectionType() != null) {
              if (thisLandmark.getCollectionType().equalsIgnoreCase(type)) {
                result.put(thisLandmark.getId(), thisLandmark.getId());
              }
            }
          }
        }
      }
    }
    
    return result;
  }
}
