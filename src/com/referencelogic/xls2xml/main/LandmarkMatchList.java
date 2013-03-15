package com.referencelogic.xls2xml.main;

import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Hashtable;
import java.util.Enumeration;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.text.SimpleDateFormat;

import java.util.Formatter;
import java.math.BigDecimal;

public class LandmarkMatchList {

  private static final Logger log = Logger.getLogger( LandmarkMatchList.class );
  
  protected ArrayList matches[];
  
  protected DataFormatter dataFormatter = new DataFormatter();

  public LandmarkMatchList(int size) {
    matches = new ArrayList[size];
    for (int i=0; i<size; i++) {
      matches[i] = new ArrayList();
    }
  }
/*  
  public void addMatches(Set landmarkNos, int row, int col) {
    Iterator it = landmarkNos.iterator();
    while (it.hasNext()) {
      matches[(int)it.next()].add(new LandmarkMatch(row, col));
    } 
  }
*/
  public void addMatches(Set landmarkNos, Cell cell) {
    Iterator it = landmarkNos.iterator();
    while (it.hasNext()) {
      matches[(int)it.next()].add(cell);
    } 
  }

  
  public String getTemplateName(LandmarkList landmarks) {
    String result = "";
    
    // Get the list of landmarks - ids match template names
    Hashtable lmids = landmarks.getLandmarkCollectionIdsForIdentifier("worksheet");
    Enumeration workbookKeys = lmids.keys();
    
    while (workbookKeys.hasMoreElements()) {
      String key = (String) workbookKeys.nextElement();
      if (isMatchForId(key, landmarks)) {
        result = key;
        break;
      }
    }
    
    if (result.equals("")) {
      log.debug("Unable to find matching template name for worksheet");
    } else {
      log.debug("Got template name: " + result);
    }
    
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

  protected boolean isDouble(String s) {
    boolean result = true;
    try {
      Double.valueOf(s);
    } catch (NumberFormatException e) {
      result = false;
    }
    return result;
  }

  public String getCellValue(Cell cell, FormulaEvaluator evaluator) {
     return getCellValue(cell, evaluator, false);
  }

  public String getCellValue(Cell cell, FormulaEvaluator evaluator, boolean ignoreformatting) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");

    String cellvalue = "";

            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    cellvalue = cell.getRichStringCellValue().getString();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if ((!ignoreformatting) && (DateUtil.isCellDateFormatted(cell))) {
                        cellvalue = "" + format.format(cell.getDateCellValue());
                    } else {
                        cellvalue = "" + cell.getNumericCellValue();
                        if (cellvalue.indexOf("E") > -1) { // Only when scientific notation, expand
                          BigDecimal bg = new BigDecimal(cell.getNumericCellValue());
                          Formatter fmt = new Formatter();
                          fmt.format("%." + bg.scale() + "f", bg);
                          cellvalue = "" + fmt.toString();
                        }
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    cellvalue = "" + cell.getBooleanCellValue();
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    try { cellvalue = cell.getRichStringCellValue().getString(); } catch (Exception e) { } 
                    try { 
                      cellvalue = "" + cell.getNumericCellValue();
                      if (cellvalue.indexOf("E") > -1) { // Only when scientific notation, expand
                        BigDecimal bg = new BigDecimal(cell.getNumericCellValue());
                        Formatter fmt = new Formatter();
                        fmt.format("%." + bg.scale() + "f", bg);
                        cellvalue = "" + fmt.toString();
                      }  
                    } catch (Exception e) { } 
                    try { cellvalue = "" + cell.getBooleanCellValue(); } catch (Exception e) { } 
                    
                    try {
                
                      CellValue cellValue = evaluator.evaluate(cell);

                      switch (cellValue.getCellType()) {
                        case Cell.CELL_TYPE_BOOLEAN:
                          cellvalue = "" + cellValue.getBooleanValue();
                          break;
                        case Cell.CELL_TYPE_NUMERIC:
                          if ((!ignoreformatting) && (!isDouble(dataFormatter.formatCellValue(cell, evaluator)))) {
                            cellvalue = "" + format.format(DateUtil.getJavaDate(cellValue.getNumberValue()));
                          } else {
                            cellvalue = "" + cellValue.getNumberValue();
                            if (cellvalue.indexOf("E") > -1) { // Only when scientific notation, expand
                              BigDecimal bg = new BigDecimal(cellValue.getNumberValue());
                              Formatter fmt = new Formatter();
                              fmt.format("%." + bg.scale() + "f", bg);
                              cellvalue = "" + fmt.toString();
                            }
                          }
                          break;
                        case Cell.CELL_TYPE_STRING:
                          cellvalue = "" + cellValue.getStringValue();
                          break;
                      }
                    
                    } catch (Exception e) {
                      log.error("Could not get formula value for cell at row " + cell.getRowIndex() + " and col " + cell.getColumnIndex(), e);
                      log.error("Formula cell value used as " + cellvalue);
                    }
                  
                    break;
                default:                    
            }

     return cellvalue;
  }  
  
  public LandmarkMatch getDataLocationUsingLandmark(Landmark landmark, Cell cell) {
    LandmarkMatch result = new LandmarkMatch(-1, -1);

    int row = cell.getRowIndex();
    int col = cell.getColumnIndex();

    int distance = Integer.parseInt(landmark.getDistance());
    String direction = landmark.getDirection();
    
    if (direction.equalsIgnoreCase("N") || direction.equalsIgnoreCase("W")) { distance *= -1; }

    if (direction.equalsIgnoreCase("N") || direction.equalsIgnoreCase("S")) { row += distance; }
    if (direction.equalsIgnoreCase("W") || direction.equalsIgnoreCase("E")) { col += distance; }
    
    // If calculated row is beyond limit, don't go beyond limit
    if (row > 65535) {
      row = 65535;
    }
    
    result = new LandmarkMatch(row, col);

    return result;
  }

  public LandmarkMatch getDataLocationUsingLandmark(Landmark landmark, Cell cell, String defaultDirection, int defaultDistance) {
    LandmarkMatch result = new LandmarkMatch(-1, -1);

    int row = cell.getRowIndex();
    int col = cell.getColumnIndex();

    int distance = defaultDistance;
    try { 
      distance = Integer.parseInt(landmark.getDistance());
    } catch (Exception e) {
      log.trace("Could not determine landmark distance to use, using default of " + defaultDistance, e);
    }
    
    String direction = defaultDirection;
    
    if (landmark.getDirection() != null) {
      direction = landmark.getDirection();
    }
    
    if (direction.equalsIgnoreCase("N") || direction.equalsIgnoreCase("W")) { distance *= -1; }

    if (direction.equalsIgnoreCase("N") || direction.equalsIgnoreCase("S")) { row += distance; }
    if (direction.equalsIgnoreCase("W") || direction.equalsIgnoreCase("E")) { col += distance; }

    // If calculated row is beyond limit, don't go beyond limit
    if (row > 65535) {
      row = 65535;
    }    
    result = new LandmarkMatch(row, col);

    return result;
  }
  

  public Hashtable getCellValueForLandmark(String landmarkId, Sheet sheet, LandmarkList landmarks, FormulaEvaluator evaluator) {
    Hashtable result = new Hashtable();    
    result.put("result", "");
    result.put("lmm", new LandmarkMatch(-1,-1));
    
    int count = 0;
    int matchnumber = 1; // By default return the first match

    try {
      int landmarkNumber = landmarks.getLandmarkNumberFromId(landmarkId);

      Landmark currentLandmark = landmarks.getLandmark(landmarkNumber);
      if (currentLandmark != null) {
        if (currentLandmark.getMatchNumber() != null) {
          try {
            matchnumber = Integer.parseInt(currentLandmark.getMatchNumber());
          } catch (Exception inte) {
            log.warn("Unable to parse match number as integer", inte);
          }
        }
      }
      
      boolean ignoreformatting = false;
      if (currentLandmark != null) {
        if (currentLandmark.getIgnoreFormatting() != null) {
          if (currentLandmark.getIgnoreFormatting().equalsIgnoreCase("true")) {
            ignoreformatting = true;
          }
        }
      }

      // Get match index for landmark
      ArrayList<Cell> landmarkCellMatches = matches[landmarkNumber];

      // Get cell of landmark
      for (Cell cell : landmarkCellMatches) {

        // If the cell is for the sheet
        if (cell.getSheet().equals(sheet)) {
          count++;

          // Get updated X,y location
          LandmarkMatch lmm = getDataLocationUsingLandmark(currentLandmark, cell);
          result.put("lmm", lmm);

          // Get value from sheet - row, col
          result.put("result", "" + getCellValue(CellUtil.getCell(CellUtil.getRow(lmm.getRow(), sheet), lmm.getCol()), evaluator, ignoreformatting));
          
          if (count >= matchnumber) {
            break;
          }
        }

      }
    } catch (Exception e) {
      log.warn("Unable to get cell value based on landmark " + landmarkId, e);
    }

    return result;
  }

  public Hashtable getCellValueForLandmark(String landmarkId, Sheet sheet, LandmarkList landmarks, FormulaEvaluator evaluator, String defaultDirection, int rowoffset) {
    Hashtable result = new Hashtable();    
    result.put("result", "");
    result.put("lmm", new LandmarkMatch(-1,-1));

    int count = 0;
    int matchnumber = 1; // By default return the first match

    try {
      int landmarkNumber = landmarks.getLandmarkNumberFromId(landmarkId);

      Landmark currentLandmark = landmarks.getLandmark(landmarkNumber);
      if (currentLandmark != null) {
        if (currentLandmark.getMatchNumber() != null) {
          try {
            matchnumber = Integer.parseInt(currentLandmark.getMatchNumber());
          } catch (Exception inte) {
            log.warn("Unable to parse match number as integer", inte);
          }
        }
      }
      
      boolean ignoreformatting = false;
      if (currentLandmark != null) {
        if (currentLandmark.getIgnoreFormatting() != null) {
          if (currentLandmark.getIgnoreFormatting().equalsIgnoreCase("true")) {
            ignoreformatting = true;
          }
        }
      }
      
      // Get match index for landmark
      ArrayList<Cell> landmarkCellMatches = matches[landmarkNumber];

      // Get cell of landmark
      for (Cell cell : landmarkCellMatches) {

        // If the cell is for the sheet
        if (cell.getSheet().equals(sheet)) {
          count++;
          
          // Get updated X,y location
          LandmarkMatch lmm = getDataLocationUsingLandmark(currentLandmark, cell, defaultDirection, rowoffset);
          result.put("lmm", lmm);
          
          // Get value from sheet - row, col
          result.put("result", getCellValue(CellUtil.getCell(CellUtil.getRow(lmm.getRow(), sheet), lmm.getCol()), evaluator, ignoreformatting));
          
          if (count >= matchnumber) { 
            break;
          }
        }

      }
    } catch (Exception e) {
      log.warn("Unable to get cell value based on landmark " + landmarkId, e);
    }

    return result;
  }

  
  public void resolveChildLandmarks(Hashtable cellLandmarks, LandmarkList landmarks, Sheet sheet) {
    Hashtable resolved = new Hashtable();
    Hashtable unresolved = new Hashtable();
  
    Enumeration cellLandmarksKeys = cellLandmarks.keys();
  
    while (cellLandmarksKeys.hasMoreElements()) {
      String key = (String) cellLandmarksKeys.nextElement();
      // If landmark has parent, put it in unresolved
      Landmark currentLandmark = landmarks.getLandmark(key);
      if (currentLandmark != null) {
        if (currentLandmark.getParentLandmarkId() != null) {
          unresolved.put(key, key);
        } else {
          resolved.put(key, key);
        }
      }
    }
    
    boolean resolvedAtLeastOne = true;
    
    // While unresolved keys exist
    while (resolvedAtLeastOne) {
      resolvedAtLeastOne = false;
      
      Enumeration unresolvedKeys = unresolved.keys();
      while (unresolvedKeys.hasMoreElements()) {
        String unresolvedKey = (String) unresolvedKeys.nextElement();
        //   if Parent id is in resolved
        
        String unresolvedKeyParentId = null;
        
        int currentLandmarkNumber = landmarks.getLandmarkNumberFromId(unresolvedKey);
        Landmark currentLandmark = landmarks.getLandmark(unresolvedKey);
        if (currentLandmark != null) {
          unresolvedKeyParentId = currentLandmark.getParentLandmarkId();
        } 
        
        if (resolved.containsKey(unresolvedKeyParentId)) {
          log.debug("Landmark " + unresolvedKey + " has parent " + unresolvedKeyParentId);
    //     Get matches for parent
          int parentLandmarkNumber = landmarks.getLandmarkNumberFromId(unresolvedKeyParentId);
          ArrayList<Cell> parentLandmarkCellMatches = matches[parentLandmarkNumber];
    
    //     Duplicate matches for child
    //     Modify location to follow directions
          for (Cell cell : parentLandmarkCellMatches) {
             int childCellRow = -1;
             int childCellCol = -1;

             // Get the location of the cell after the parent direction and child direction applied
             LandmarkMatch lmm = getDataLocationUsingLandmark(landmarks.getLandmark(parentLandmarkNumber), cell);

             childCellRow = lmm.getRow();
             childCellCol = lmm.getCol();

             if (childCellRow < 0) { childCellRow = 0; }
             if (childCellCol < 0) { childCellCol = 0; }

             log.debug("Adding cell match for landmark " + unresolvedKey + " with row " + childCellRow + " and col " + childCellCol);

             Cell childCell = CellUtil.getCell(CellUtil.getRow(childCellRow, sheet), childCellCol);
             matches[currentLandmarkNumber].add(childCell);
          }
    
    //     Put id in resolved
          resolved.put(unresolvedKey, unresolvedKey);
          unresolved.remove(unresolvedKey);
          resolvedAtLeastOne = true;
        }
    //   end if
      }

    // End While
    }
  }
  
  public Hashtable getCellTemplateValues(String templateName, Sheet sheet, LandmarkList landmarks, FormulaEvaluator evaluator, String sourcefilename, int sheetno) {
    Hashtable result = new Hashtable();

    log.debug("Getting landmarks for template with type cell. Template: " + templateName); 
    // Get landmarks for template with type cell 
    Hashtable cellLandmarks = landmarks.getLandmarkIdsForIdentifier(templateName, "cells");

    Enumeration cellLandmarksKeys = cellLandmarks.keys();
    
    resolveChildLandmarks(cellLandmarks, landmarks, sheet);
    
    result.put("xls2xml_sourcefilename", sourcefilename);
    result.put("xls2xml_sheetno", "" + sheetno);      
    
    while (cellLandmarksKeys.hasMoreElements()) {
      String key = (String) cellLandmarksKeys.nextElement();
      Hashtable value = getCellValueForLandmark(key, sheet, landmarks, evaluator);
      log.debug("Getting cell template key and value - [" + key + "," + value + "]");
      result.put(key, (String)value.get("result"));
      log.debug("Landmark match for key is: " + value.get("lmm"));
      result.put(key + "_xls2xml_row", "" + ((LandmarkMatch)value.get("lmm")).getRow());      
      result.put(key + "_xls2xml_col", "" + ((LandmarkMatch)value.get("lmm")).getCol());      
    }

    return result;
  }
  
  public ArrayList getSectionNamesForTemplate(String templateName, LandmarkList landmarks) {
    ArrayList result = new ArrayList();
        
    Hashtable lmsect = landmarks.getLandmarkSectionsForIdentifier(templateName);
    Enumeration lmsectkeys = lmsect.keys();
    
    while (lmsectkeys.hasMoreElements()) {
      String key = (String) lmsectkeys.nextElement();
      log.debug("Found section name for template " + templateName + ": " + key);
      result.add(key);
    }
        
    return result;
  }
  
  public boolean isRowBlank(Hashtable sectionLandmarks, int rowoffset, Sheet sheet, LandmarkList landmarks, FormulaEvaluator evaluator, String defaultDirection) {
    boolean result = false;

    String rowcontent = "";
    
    Enumeration sectionLandmarksKeys = sectionLandmarks.keys();
    //   For each landmark in the section
    while (sectionLandmarksKeys.hasMoreElements()) {
      String key = (String) sectionLandmarksKeys.nextElement();
      
      //     Get Cell Value
      Hashtable value = getCellValueForLandmark(key, sheet, landmarks, evaluator, defaultDirection, rowoffset);
        
      //     Add to rowcontent
      rowcontent += ((String)value.get("result")).trim();
    }
      
    result = rowcontent.equals("");
        
    return result;
  }
  
  public boolean checkEndOfSection(Hashtable sectionLandmarks, int rowoffset, Hashtable sectionEndLandmarks, LandmarkList landmarks, Sheet sheet, FormulaEvaluator evaluator, String defaultDirection) {
    boolean result = false;

    // Cannot have rows past end of spreadsheet
    if (rowoffset > 65535) { result = true; }
    
    if (!result) {
    
      // Get max blank lines
      int maxblanklines = 0; // Min valid value is 1
      
      try {
        // Get a section landmark
        Enumeration sectionEndLandmarksKeys = sectionEndLandmarks.keys();
        if (sectionEndLandmarksKeys.hasMoreElements()) {
          String key = (String) sectionEndLandmarksKeys.nextElement();
          Landmark maxblanklineslm = landmarks.getLandmark(landmarks.getLandmarkNumberFromId(key));
          maxblanklines = Integer.parseInt(maxblanklineslm.getCollectionMaxBlankLines());
        }
      } catch (Exception e) {
        log.trace("Unable to get a defined value for max blank lines for section end landmarks", e);
      }
      
      // If limit on max blank lines
      if (maxblanklines > 0) {
        boolean allareblank = true;
        //  repeat max blank lines times
        for (int count=0; count<maxblanklines; count++) {
          //    check to see if specified line is blank for section
          allareblank &= isRowBlank(sectionLandmarks, rowoffset + count, sheet, landmarks, evaluator, defaultDirection);
        }
        //  if all lines are blank result is true
        result = allareblank;
      } else {
      //   for each landmark in section end
        log.debug("For each landmark in section end");
        Enumeration sectionEndLandmarksKeys = sectionEndLandmarks.keys();
        while (sectionEndLandmarksKeys.hasMoreElements()) {
          String key = (String) sectionEndLandmarksKeys.nextElement();
          log.debug("sectionEndLandmarkKeys: key: " + key);
          //     Get section end landmark match row and column
          ArrayList<Cell> sectionEndLandmarksMatches = matches[landmarks.getLandmarkNumberFromId(key)];
          
          //     for each of the section landmarks
          for (Cell sectionEndLandmarkMatch : sectionEndLandmarksMatches) {

            Enumeration sectionLandmarksKeys = sectionLandmarks.keys();
            //   For each landmark in the section
            log.debug("For each landmark in the section");
            while (sectionLandmarksKeys.hasMoreElements()) {
              String lmkey = (String) sectionLandmarksKeys.nextElement();
              log.debug("sectionLandmarkKeys: lmkey: " + lmkey);
              
              ArrayList<Cell> sectionLandmarksMatches = matches[landmarks.getLandmarkNumberFromId(lmkey)];
              
              for (Cell sectionLandmarkMatch : sectionLandmarksMatches) {
      //       if section landmark row + rowoffset equals section end landmark row
      //          and section landmark col equals section end landmark col
      //            result is true
      //       end if
                 if ((sectionEndLandmarkMatch.getRowIndex()    == sectionLandmarkMatch.getRowIndex() + rowoffset) ||
                     (sectionEndLandmarkMatch.getRowIndex()    == sectionLandmarkMatch.getRowIndex() ) 
                     ) { // && (sectionEndLandmarkMatch.getColumnIndex() == sectionLandmarkMatch.getColumnIndex())
                   result = true;
                   break;
                 }
              }
              
              if (result) { break; }
            }
          
            if (result) { break; }
          } //     end for
          
          if (result) { break; }
        } //   end for
      
      // end if
      }
    
    }
    
    return result;
  }
  
  public ArrayList<Hashtable> getSectionRows(String templateName, Sheet sheet, LandmarkList landmarks, FormulaEvaluator evaluator, String sectionName) {
    ArrayList<Hashtable> result = new ArrayList<Hashtable>();
    
    // Row offset 1
    int rowoffset = 1;

    // Direction is S unless otherwise specified
    String defaultDirection = "S";

    boolean isEndOfSection = false;

    // Get list of landmarks type "header" in section
    Hashtable sectionLandmarks = landmarks.getLandmarkIdsForIdentifierForSectionsForType(templateName, sectionName, "header");

    // Get list of landmarks type "footer" in section
    Hashtable sectionEndLandmarks = landmarks.getLandmarkIdsForIdentifierForSectionsForType(templateName, sectionName, "footer");

    isEndOfSection = checkEndOfSection(sectionLandmarks, rowoffset, sectionEndLandmarks, landmarks, sheet, evaluator, defaultDirection);
    
    // While the end of the section has not been reached
    while (!isEndOfSection) {
      //   Initialize hashtable
      Hashtable row = new Hashtable();
      
      Enumeration sectionLandmarksKeys = sectionLandmarks.keys();
      //   For each landmark in the section
      while (sectionLandmarksKeys.hasMoreElements()) {
        String key = (String) sectionLandmarksKeys.nextElement();
      
        //     Get Cell Value
        Hashtable value = getCellValueForLandmark(key, sheet, landmarks, evaluator, defaultDirection, rowoffset);
        
        //     Add to hashtable
        row.put(key, (String)value.get("result"));
        row.put(key + "_xls2xml_row", "" + ((LandmarkMatch)value.get("lmm")).getRow());      
        row.put(key + "_xls2xml_col", "" + ((LandmarkMatch)value.get("lmm")).getCol());        
        
      //   End
      }
      //   Add hashtable to result
      result.add(row);
      
      //   Increment row offset
      rowoffset++;

      isEndOfSection = checkEndOfSection(sectionLandmarks, rowoffset, sectionEndLandmarks, landmarks, sheet, evaluator, defaultDirection);
    } // End While
                
    return result;
  }
}
