package com.referencelogic.xls2xml.main;

import java.io.File;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.CellValue;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STRawGroupDir;
import org.stringtemplate.v4.ST;

import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;

public class Xls2xmlConverter implements Runnable {
  protected File file;
  protected XMLConfiguration config;
  private static final Logger log = Logger.getLogger( Xls2xmlConverter.class );
  protected boolean isDebugging;
  protected LandmarkList landmarks;

  Xls2xmlConverter(File file, XMLConfiguration config, LandmarkList landmarks) {
    this.file = file;
    this.config = config;
    this.landmarks = landmarks;
  }

  public String process2xml() {
    String result = "";
    String sourcefilenametag = config.getString("conversion.tags.sourcefilename");
    String templatedir = config.getString("conversion.template.path");

    //result += "<" + sourcefilenametag + "><![CDATA[" + file.toString() + "]]></" + sourcefilenametag + ">";
    try {
      Workbook input = WorkbookFactory.create(file);
      
      log.debug("Processing " + file.toString() + " as an Excel file.");
      
      FormulaEvaluator evaluator = input.getCreationHelper().createFormulaEvaluator();
      
      String templateName = "";

      STGroup g = new STRawGroupDir(templatedir, '$','$');
      
      // Go through each sheet
      for(int sheetno=0; sheetno<input.getNumberOfSheets();sheetno++) {
      
        Sheet sheet = input.getSheetAt(sheetno);
        
        LandmarkMatchList lml = new LandmarkMatchList(landmarks.size());
        
        for (Row row : sheet) {
        
          // Go through each cell
          for (Cell cell : row) {
            String cellvalue = "";
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    cellvalue = cell.getRichStringCellValue().getString();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellvalue = "" + cell.getDateCellValue();
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
                          cellvalue = "" + cellValue.getNumberValue();
                          break;
                      case Cell.CELL_TYPE_STRING:
                          cellvalue = "" + cellValue.getStringValue();
                          break;
                    }
                  
                    break;
                default:                    
            }
            
            if (!cellvalue.equals("")) {
              log.trace("Cell value is: " + cellvalue + " [Row,Col]=[" + cell.getRowIndex() + "," + cell.getColumnIndex() + "]");
              log.trace("Matching landmarks: " + landmarks.getLandmarksFor(cellvalue));
              
              // Does Cell contents match a landmark?
              lml.addMatches(landmarks.getLandmarksFor(cellvalue), cell.getRowIndex(), cell.getColumnIndex());
            } else {
              log.trace("Cell value is blank. [Row,Col]=[" + cell.getRowIndex() + "," + cell.getColumnIndex() + "]");
            }
          }
        }
        
        templateName = lml.getTemplateName(landmarks);
        
        if (!templateName.equals("")) {
      
          ST st = g.getInstanceOf(templateName);
 
          if (st != null) {

            // Set landmark name to value of cell given direction and distance
            
            st.add("title", "Welcome To StringTemplate");
            st.add("name", "World");
            st.add("friends", "Ter");
            st.add("friends", "Kunle");
            st.add("friends", "Micheal");
            st.add("friends", "Marq");
          
            result += st.render();
          } else {
            log.error("Unable to load template " + templateName + ".st! Cannot render data to template.");
          }
        
        }
        
      }
      
        
          


    } catch (IOException ioe) {
      log.error("Unable to open " + file.toString() + " as an Excel file.", ioe);
    } catch (InvalidFormatException ife) {
      log.error("Unable to open " + file.toString() + ". Format not recognized as Excel. ", ife);    
    }
    return result;
  }
    
  public void run() {
      String sourceDir = config.getString("source.path");
      String destDir = config.getString("destination.path");
      String sourceFilePath = file.toString();
      if (isDebugging) { log.debug("Processing " + sourceFilePath); }
      // Destination file is same directory in output with xml
      String destFilePath = sourceFilePath.substring(sourceDir.length());
      if (destFilePath.startsWith(File.separator)) { destFilePath = destFilePath.substring(1); } 
      // Replace extensions with .xml
      destFilePath = FilenameUtils.removeExtension(destFilePath) + ".xml";  
      File destFile = new File(destDir, destFilePath);
      destFilePath = destFile.toString();
      try {
        FileUtils.touch(destFile);
        if (isDebugging) { log.debug("Created destination file: " + destFilePath); }
        // Put some XML in the file
        BufferedWriter output = new BufferedWriter(new FileWriter(destFile));
        output.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
        output.newLine();
        String roottag = config.getString("conversion.tags.root");
        output.append("<" + roottag + ">");
        output.newLine();
        output.append(process2xml());
        output.append("</" + roottag + ">");
        output.close();        
      } catch (IOException ioe) {
        log.error("Could not create destination file: " + destFilePath, ioe);
      }

  }
  
}
