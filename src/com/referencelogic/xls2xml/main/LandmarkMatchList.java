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
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");

    String cellvalue = "";

            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    cellvalue = cell.getRichStringCellValue().getString();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellvalue = "" + format.format(cell.getDateCellValue());
                    } else {
                        cellvalue = "" + cell.getNumericCellValue();
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    cellvalue = "" + cell.getBooleanCellValue();
                    break;
                case Cell.CELL_TYPE_FORMULA:
                
                    CellValue cellValue = evaluator.evaluate(cell);

                    switch (cellValue.getCellType()) {
                      case Cell.CELL_TYPE_BOOLEAN:
                          cellvalue = "" + cellValue.getBooleanValue();
                          break;
                      case Cell.CELL_TYPE_NUMERIC:
                          if (!isDouble(dataFormatter.formatCellValue(cell, evaluator))) {
                            cellvalue = "" + format.format(DateUtil.getJavaDate(cellValue.getNumberValue()));
                          } else {
                            cellvalue = "" + cellValue.getNumberValue();
                          }
                          break;
                      case Cell.CELL_TYPE_STRING:
                          cellvalue = "" + cellValue.getStringValue();
                          break;
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
    
    result = new LandmarkMatch(row, col);

    return result;
  }


  public Hashtable getCellValueForLandmark(String landmarkId, Sheet sheet, LandmarkList landmarks, FormulaEvaluator evaluator) {
    Hashtable result = new Hashtable();    
    result.put("result", "");

    try {
      int landmarkNumber = landmarks.getLandmarkNumberFromId(landmarkId);

      // Get match index for landmark
      ArrayList<Cell> landmarkCellMatches = matches[landmarkNumber];

      // Get cell of landmark
      for (Cell cell : landmarkCellMatches) {

        // If the cell is for the sheet
        if (cell.getSheet().equals(sheet)) {

          // Get updated X,y location
          LandmarkMatch lmm = getDataLocationUsingLandmark(landmarks.getLandmark(landmarkNumber), cell);
          result.put("lmm", lmm);

          // Get value from sheet - row, col
          result.put("result", getCellValue(CellUtil.getCell(CellUtil.getRow(lmm.getRow(), sheet), lmm.getCol()), evaluator));
          
          break;
        }

      }
    } catch (Exception e) {
      log.warn("Unable to get cell value based on landmark " + landmarkId, e);
    }

    return result;
  }

  public Hashtable getCellTemplateValues(String templateName, Sheet sheet, LandmarkList landmarks, FormulaEvaluator evaluator, String sourcefilename, int sheetno) {
    Hashtable result = new Hashtable();

    log.debug("Getting landmarks for template with type cell. Template: " + templateName); 
    // Get landmarks for template with type cell 
    Hashtable cellLandmarks = landmarks.getLandmarkIdsForIdentifier(templateName, "cells");

    Enumeration cellLandmarksKeys = cellLandmarks.keys();
    
    result.put("xls2xml_sourcefilename", sourcefilename);
    result.put("xls2xml_sheetno", "" + sheetno);      
    
    while (cellLandmarksKeys.hasMoreElements()) {
      String key = (String) cellLandmarksKeys.nextElement();
      Hashtable value = getCellValueForLandmark(key, sheet, landmarks, evaluator);
      log.debug("Getting cell template key and value - [" + key + "," + value + "]");
      result.put(key, (String)value.get("result"));
      result.put(key + "_xls2xml_row", "" + ((LandmarkMatch)value.get("lmm")).getRow());      
      result.put(key + "_xls2xml_col", "" + ((LandmarkMatch)value.get("lmm")).getCol());      
    }

    return result;
  }
}
