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

  public void addLandmark(String value, String landmarkid, String direction, String distance, String type, String collectionid, String scope, String section) {
    Landmark landmark = new Landmark();

    landmark.value = value;
    landmark.id = landmarkid;
    landmark.direction = direction;
    landmark.distance = distance;
    landmark.collectionType = type;
    landmark.collectionId = collectionid;
    landmark.collectionScope = scope;
    landmark.collectionSection = section;
    
    log.debug(landmark);

    landmarks.add(landmark);
  }

}
