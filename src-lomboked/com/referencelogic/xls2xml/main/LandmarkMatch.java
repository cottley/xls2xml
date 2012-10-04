package com.referencelogic.xls2xml.main;

@lombok.Data
public class LandmarkMatch {

  protected int row;
  protected int col;

  @java.lang.SuppressWarnings("all")
	public LandmarkMatch(int row, int col) {
    setRow(row);
    setCol(col);
	}
}
