package com.referencelogic.xls2xml.main;

public class Landmark {

  public String value;
  public String id;
  public String direction;
  public String distance;
  public String collectionType;
  public String collectionId;
  public String collectionScope;
  public String collectionSection;

  public String toString() {
    return "[" + value + "," + id + "," + direction + "," + distance + "," + collectionType + "," + collectionId + "," + collectionScope + "," + collectionSection + "]";
  }

}
