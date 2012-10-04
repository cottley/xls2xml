package com.referencelogic.xls2xml.main;

import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Hashtable;
import java.util.Enumeration;

import org.apache.poi.ss.usermodel.Sheet;

public class LandmarkMatchList {

  private static final Logger log = Logger.getLogger( LandmarkMatchList.class );
  
  protected ArrayList matches[];
  
  public LandmarkMatchList(int size) {
    matches = new ArrayList[size];
    for (int i=0; i<size; i++) {
      matches[i] = new ArrayList();
    }
  }
  
  public void addMatches(Set landmarkNos, int row, int col) {
    Iterator it = landmarkNos.iterator();
		while (it.hasNext()) {
			matches[(int)it.next()].add(new LandmarkMatch(row, col));
		} 
  }
  
  public String getTemplateName(LandmarkList landmarks) {
    String result = "";
    
    // Get the list of landmarks - ids match template names
    Hashtable lmids = landmarks.getLandmarkIdsForIdentifier("worksheet");
    Enumeration workbookKeys = lmids.keys();
    
    while (workbookKeys.hasMoreElements()) {
      String key = (String) workbookKeys.nextElement();
      if (isMatchForId(key, landmarks)) {
        result = key;
        break;
      }
    }
    
    log.debug("Got template name: " + result);
    
    return result;
  }
  
  public boolean isMatchForId(String id, LandmarkList landmarks) {
    boolean result = true;
    
    // Get all the landmark numbers for the id
    ArrayList<Integer> numbersForId = landmarks.getIndexesForId(id);
    
    if (numbersForId.size() == 0) { result = false; }
    
    // See if there are matches with non-empty arraylists for all numbers in the matches array    
    for (int landmarkIndex : numbersForId) {
      result = (result && (matches[landmarkIndex].size()>0));
    }
    
    return result;
  }

  public Hashtable getTemplateValues(String templateName, Sheet sheet, LandmarkList landmarks) {
    Hashtable result = new Hashtable();
    
    return result;
  }
}