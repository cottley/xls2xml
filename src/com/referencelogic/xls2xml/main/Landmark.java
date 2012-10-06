// Generated by delombok at Sat Oct 06 05:52:34 AST 2012
package com.referencelogic.xls2xml.main;

public class Landmark {
	protected String value;
	protected String id;
	protected String direction;
	protected String distance;
	protected String collectionType;
	protected String collectionId;
	protected String collectionIdentifier;
	protected String collectionSection;
	protected String row;
	protected String col;
	protected String sheetNo;
	
	@java.lang.SuppressWarnings("all")
	public Landmark() {
	}
	
	@java.lang.SuppressWarnings("all")
	public String getValue() {
		return this.value;
	}
	
	@java.lang.SuppressWarnings("all")
	public String getId() {
		return this.id;
	}
	
	@java.lang.SuppressWarnings("all")
	public String getDirection() {
		return this.direction;
	}
	
	@java.lang.SuppressWarnings("all")
	public String getDistance() {
		return this.distance;
	}
	
	@java.lang.SuppressWarnings("all")
	public String getCollectionType() {
		return this.collectionType;
	}
	
	@java.lang.SuppressWarnings("all")
	public String getCollectionId() {
		return this.collectionId;
	}
	
	@java.lang.SuppressWarnings("all")
	public String getCollectionIdentifier() {
		return this.collectionIdentifier;
	}
	
	@java.lang.SuppressWarnings("all")
	public String getCollectionSection() {
		return this.collectionSection;
	}
	
	@java.lang.SuppressWarnings("all")
	public String getRow() {
		return this.row;
	}
	
	@java.lang.SuppressWarnings("all")
	public String getCol() {
		return this.col;
	}
	
	@java.lang.SuppressWarnings("all")
	public String getSheetNo() {
		return this.sheetNo;
	}
	
	@java.lang.SuppressWarnings("all")
	public void setValue(final String value) {
		this.value = value;
	}
	
	@java.lang.SuppressWarnings("all")
	public void setId(final String id) {
		this.id = id;
	}
	
	@java.lang.SuppressWarnings("all")
	public void setDirection(final String direction) {
		this.direction = direction;
	}
	
	@java.lang.SuppressWarnings("all")
	public void setDistance(final String distance) {
		this.distance = distance;
	}
	
	@java.lang.SuppressWarnings("all")
	public void setCollectionType(final String collectionType) {
		this.collectionType = collectionType;
	}
	
	@java.lang.SuppressWarnings("all")
	public void setCollectionId(final String collectionId) {
		this.collectionId = collectionId;
	}
	
	@java.lang.SuppressWarnings("all")
	public void setCollectionIdentifier(final String collectionIdentifier) {
		this.collectionIdentifier = collectionIdentifier;
	}
	
	@java.lang.SuppressWarnings("all")
	public void setCollectionSection(final String collectionSection) {
		this.collectionSection = collectionSection;
	}
	
	@java.lang.SuppressWarnings("all")
	public void setRow(final String row) {
		this.row = row;
	}
	
	@java.lang.SuppressWarnings("all")
	public void setCol(final String col) {
		this.col = col;
	}
	
	@java.lang.SuppressWarnings("all")
	public void setSheetNo(final String sheetNo) {
		this.sheetNo = sheetNo;
	}
	
	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof Landmark)) return false;
		final Landmark other = (Landmark)o;
		if (!other.canEqual((java.lang.Object)this)) return false;
		final java.lang.Object this$value = this.getValue();
		final java.lang.Object other$value = other.getValue();
		if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
		final java.lang.Object this$id = this.getId();
		final java.lang.Object other$id = other.getId();
		if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
		final java.lang.Object this$direction = this.getDirection();
		final java.lang.Object other$direction = other.getDirection();
		if (this$direction == null ? other$direction != null : !this$direction.equals(other$direction)) return false;
		final java.lang.Object this$distance = this.getDistance();
		final java.lang.Object other$distance = other.getDistance();
		if (this$distance == null ? other$distance != null : !this$distance.equals(other$distance)) return false;
		final java.lang.Object this$collectionType = this.getCollectionType();
		final java.lang.Object other$collectionType = other.getCollectionType();
		if (this$collectionType == null ? other$collectionType != null : !this$collectionType.equals(other$collectionType)) return false;
		final java.lang.Object this$collectionId = this.getCollectionId();
		final java.lang.Object other$collectionId = other.getCollectionId();
		if (this$collectionId == null ? other$collectionId != null : !this$collectionId.equals(other$collectionId)) return false;
		final java.lang.Object this$collectionIdentifier = this.getCollectionIdentifier();
		final java.lang.Object other$collectionIdentifier = other.getCollectionIdentifier();
		if (this$collectionIdentifier == null ? other$collectionIdentifier != null : !this$collectionIdentifier.equals(other$collectionIdentifier)) return false;
		final java.lang.Object this$collectionSection = this.getCollectionSection();
		final java.lang.Object other$collectionSection = other.getCollectionSection();
		if (this$collectionSection == null ? other$collectionSection != null : !this$collectionSection.equals(other$collectionSection)) return false;
		final java.lang.Object this$row = this.getRow();
		final java.lang.Object other$row = other.getRow();
		if (this$row == null ? other$row != null : !this$row.equals(other$row)) return false;
		final java.lang.Object this$col = this.getCol();
		final java.lang.Object other$col = other.getCol();
		if (this$col == null ? other$col != null : !this$col.equals(other$col)) return false;
		final java.lang.Object this$sheetNo = this.getSheetNo();
		final java.lang.Object other$sheetNo = other.getSheetNo();
		if (this$sheetNo == null ? other$sheetNo != null : !this$sheetNo.equals(other$sheetNo)) return false;
		return true;
	}
	
	@java.lang.SuppressWarnings("all")
	public boolean canEqual(final java.lang.Object other) {
		return other instanceof Landmark;
	}
	
	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		final java.lang.Object $value = this.getValue();
		result = result * PRIME + ($value == null ? 0 : $value.hashCode());
		final java.lang.Object $id = this.getId();
		result = result * PRIME + ($id == null ? 0 : $id.hashCode());
		final java.lang.Object $direction = this.getDirection();
		result = result * PRIME + ($direction == null ? 0 : $direction.hashCode());
		final java.lang.Object $distance = this.getDistance();
		result = result * PRIME + ($distance == null ? 0 : $distance.hashCode());
		final java.lang.Object $collectionType = this.getCollectionType();
		result = result * PRIME + ($collectionType == null ? 0 : $collectionType.hashCode());
		final java.lang.Object $collectionId = this.getCollectionId();
		result = result * PRIME + ($collectionId == null ? 0 : $collectionId.hashCode());
		final java.lang.Object $collectionIdentifier = this.getCollectionIdentifier();
		result = result * PRIME + ($collectionIdentifier == null ? 0 : $collectionIdentifier.hashCode());
		final java.lang.Object $collectionSection = this.getCollectionSection();
		result = result * PRIME + ($collectionSection == null ? 0 : $collectionSection.hashCode());
		final java.lang.Object $row = this.getRow();
		result = result * PRIME + ($row == null ? 0 : $row.hashCode());
		final java.lang.Object $col = this.getCol();
		result = result * PRIME + ($col == null ? 0 : $col.hashCode());
		final java.lang.Object $sheetNo = this.getSheetNo();
		result = result * PRIME + ($sheetNo == null ? 0 : $sheetNo.hashCode());
		return result;
	}
	
	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "Landmark(value=" + this.getValue() + ", id=" + this.getId() + ", direction=" + this.getDirection() + ", distance=" + this.getDistance() + ", collectionType=" + this.getCollectionType() + ", collectionId=" + this.getCollectionId() + ", collectionIdentifier=" + this.getCollectionIdentifier() + ", collectionSection=" + this.getCollectionSection() + ", row=" + this.getRow() + ", col=" + this.getCol() + ", sheetNo=" + this.getSheetNo() + ")";
	}
}