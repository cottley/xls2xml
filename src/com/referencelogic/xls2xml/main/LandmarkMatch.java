// Generated by delombok at Thu Oct 04 14:53:00 BOT 2012
package com.referencelogic.xls2xml.main;

public class LandmarkMatch {
	protected int row;
	protected int col;
	
	@java.lang.SuppressWarnings("all")
	public LandmarkMatch(int row, int col) {
		setRow(row);
		setCol(col);
	}
	
	@java.lang.SuppressWarnings("all")
	public int getRow() {
		return this.row;
	}
	
	@java.lang.SuppressWarnings("all")
	public int getCol() {
		return this.col;
	}
	
	@java.lang.SuppressWarnings("all")
	public void setRow(final int row) {
		this.row = row;
	}
	
	@java.lang.SuppressWarnings("all")
	public void setCol(final int col) {
		this.col = col;
	}
	
	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof LandmarkMatch)) return false;
		final LandmarkMatch other = (LandmarkMatch)o;
		if (!other.canEqual((java.lang.Object)this)) return false;
		if (this.getRow() != other.getRow()) return false;
		if (this.getCol() != other.getCol()) return false;
		return true;
	}
	
	@java.lang.SuppressWarnings("all")
	public boolean canEqual(final java.lang.Object other) {
		return other instanceof LandmarkMatch;
	}
	
	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = result * PRIME + this.getRow();
		result = result * PRIME + this.getCol();
		return result;
	}
	
	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "LandmarkMatch(row=" + this.getRow() + ", col=" + this.getCol() + ")";
	}
}