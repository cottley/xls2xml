package com.referencelogic.xls2xml.main;

import org.apache.log4j.Logger;

import java.util.ArrayList;

import org.apache.commons.configuration.XMLConfiguration;

public class LandmarkList {

  private static final Logger log = Logger.getLogger( LandmarkList.class );

  protected ArrayList landmarks = new ArrayList();
  protected boolean isDebugging;
  protected String ignorechars;
  protected boolean ignorecase;
  protected boolean substringsearch;

  public LandmarkList(XMLConfiguration config) {
    isDebugging = log.isDebugEnabled();
    ignorechars = config.getString("conversion.searchparams.ignorechars");
    ignorecase = config.getBoolean("conversion.searchparams.ignorecase");
    substringsearch = config.getBoolean("conversion.searchparams.substringsearch");

    if (isDebugging) {
      log.debug("Search parameters: Substring search - " + substringsearch + ", Ignore case - " + ignorecase + ", Characters to ignore - '" + ignorechars + "'"); 
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

}
