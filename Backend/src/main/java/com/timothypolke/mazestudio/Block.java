package com.timothypolke.mazestudio;

public class Block{
	
	private int rowID = 0;
	private int columnID = 0;
	private int startX = 0;
	private int startY = 0;
	private boolean open = false;
	
	public Block(int rowID, int columnID, int startX, int startY, boolean open){
		setRowID(rowID);
		setColumnID(columnID);
		setStartX(startX);
		setStartY(startY);
		setOpen(open);
	}
	
	public void setRowID(int rowID){
		this.rowID = rowID;
	}
	
	public int getRowID(){
		return rowID;
	}
	
	public void setColumnID(int columnID){
		this.columnID = columnID;
	}
	
	public int getColumnID(){
		return columnID;
	}
	
	public void setStartX(int startX){
		this.startX = startX;
	}
	
	public int getStartX(){
		return startX;
	}
	
	public void setStartY(int startY){
		this.startY = startY;
	}
	
	public int getStartY(){
		return startY;
	}
	
	public void setOpen(boolean open){
		this.open = open;
	}
	
	public boolean getOpen(){
		return open;
	}
}