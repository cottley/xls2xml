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
//import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
//import org.apache.poi.ss.usermodel.CellValue;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STRawGroupDir;
import org.stringtemplate.v4.ST;

import java.util.List;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.ArrayList;

import org.apache.commons.configuration.HierarchicalConfiguration;

public class Xls2xmlConverter implements Runnable {
  protected File file;
  protected XMLConfiguration config;
  private static final Logger log = Logger.getLogger( Xls2xmlConverter.class );
  protected boolean isDebugging;
  protected LandmarkList landmarks;
  protected boolean ignoreExisting;

  Xls2xmlConverter(File file, XMLConfiguration config, LandmarkList landmarks, boolean ignoreExisting) {
    this.file = file;
    this.config = config;
    this.landmarks = landmarks;
    this.ignoreExisting = ignoreExisting;
  }

  public String process2xml() {
    String result = "";
    //String sourcefilenametag = config.getString("conversion.tags.sourcefilename");
    String sourcefilename = file.getAbsolutePath();
    try { 
      sourcefilename = file.getCanonicalPath();
    } catch (IOException ioe) {
      log.warn("Could not get cannonical path for file!", ioe);
    }
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
        
        log.debug("Processing sheet #" + sheetno + ": " + sheet.getSheetName());
        
        LandmarkMatchList lml = new LandmarkMatchList(landmarks.size());
        
        for (Row row : sheet) {
        
          // Go through each cell
          for (Cell cell : row) {
            String cellvalue = lml.getCellValue(cell, evaluator);
            
            if (!cellvalue.equals("")) {
              log.trace("Cell value is: " + cellvalue + " [Row,Col]=[" + cell.getRowIndex() + "," + cell.getColumnIndex() + "]");
              log.trace("Matching landmarks: " + landmarks.getLandmarksFor(cellvalue));
              
              // Does Cell contents match a landmark?
              lml.addMatches(landmarks.getLandmarksFor(cellvalue), cell);
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
            
            Hashtable templateValues = lml.getCellTemplateValues(templateName, sheet, landmarks, evaluator, sourcefilename, sheetno);
            Enumeration templateValuesKeys = templateValues.keys();
    
            while (templateValuesKeys.hasMoreElements()) {
              String key = (String) templateValuesKeys.nextElement();
              st.add(key, (String)templateValues.get(key));
            }
           
            ArrayList<String> sectionNames = lml.getSectionNamesForTemplate(templateName, landmarks);
            for (String sectionName : sectionNames) {
                        
              ArrayList<Hashtable> sectionrows = lml.getSectionRows(templateName, sheet, landmarks, evaluator, sectionName); 
            
              st.add(sectionName, sectionrows);
            }
            
            result += st.render();
          } else {
            log.error("Unable to load template " + templateName + ".st! Cannot render data to template while processing " 
              + file.toString() + " sheet number " + sheetno);
          }
        
        }
        
      }
      
    } catch (IOException ioe) {
      log.error("Unable to open " + file.toString() + " as an Excel file.", ioe);
    } catch (InvalidFormatException ife) {
      log.error("Unable to open " + file.toString() + ". Format not recognized as Excel. ", ife);    
    } catch (IllegalArgumentException iae) {
      log.error("Unable to open " + file.toString() + " as an Excel file.", iae);    
    } catch (Exception e) {
      log.error("Unable to open " + file.toString() + " as an Excel file.", e);    
    }
    return result;
  }
    
  public void run() {
      String sourceDir = config.getString("source.path");
      String destDir = config.getString("destination.path");
      String sourceFilePath = file.toString();
      Xls2xmlStats.setThreadFileProcess(Thread.currentThread().getName(), sourceFilePath);
      if (isDebugging) { log.debug("Processing " + sourceFilePath); }
      // Destination file is same directory in output with xml
      String destFilePath = sourceFilePath.substring(sourceDir.length());
      if (destFilePath.startsWith(File.separator)) { destFilePath = destFilePath.substring(1); } 
      // Replace extensions with .xml
      destFilePath = FilenameUtils.removeExtension(destFilePath) + ".xml";  
      File destFile = new File(destDir, destFilePath);
      destFilePath = destFile.toString();
      try {
        if (ignoreExisting && (FileUtils.sizeOf(destFile) > 0)) {
          log.debug("Ignoring the recreation of file: " + destFilePath);
          log.debug("Filesize is: " + FileUtils.sizeOf(destFile));
        } else {
          FileUtils.touch(destFile);
        
          if (isDebugging) { log.debug("Created destination file: " + destFilePath); }
          // Put some XML in the file
        
          String processedXML = process2xml();
        
          BufferedWriter output = new BufferedWriter(new FileWriter(destFile));

          // Only write data to the file if there is data from the processing
          if (!processedXML.equals("")) {
            output.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
            output.newLine();
            String roottag = config.getString("conversion.tags.root");
            output.append("<" + roottag + ">");
            output.newLine();
            output.append(processedXML);
            output.append("</" + roottag + ">");
          }
        
          output.close();
        
        }
      } catch (IOException ioe) {
        log.error("Could not create destination file: " + destFilePath, ioe);
      }
      
      Xls2xmlStats.recordFileProcessed();
      Xls2xmlStats.setThreadFileProcess(Thread.currentThread().getName(), "");
      log.info("STATISTICS: " + Xls2xmlStats.status());

  }
  
}
