package com.timothypolke.mazestudio;

import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class Maze extends BufferedImage{
	
	private int xCells;
	private int yCells;
	
	private Color highlight = null;
	private Color foreground = null;
	private Color background = null;
	
	final private int boundarySize = 1;
	final private int cellSize = 5;
	
	private Graphics2D graphics = null;
	
	private Block[][] verticalWalls = null;
	private Block[][] horizontalWalls = null;
	private Block[][] cells = null;
	
	private ArrayList<String> directions = null;
	private ArrayList<String> route = null;
	private ArrayList<int[]> path = null;
	private ArrayList<Block> history = null;
	private int x = 0;
	private int y = 0;
	
	public Maze(int xCells, int yCells, Color foreground, Color background){
		super((xCells * 5) + ((xCells * 1) + 1), (yCells * 5) + ((yCells * 1) + 1), BufferedImage.TYPE_INT_RGB);	
		
		this.xCells = xCells;
		this.yCells = yCells;
		
		this.foreground = foreground;
		this.background = background;
		
		this.highlight = new Color(255, 255, 0);
		
		graphics = createGraphics();
		
		create();
		solve();
	}
	
	public void create(){
		verticalWalls = new Block[yCells][xCells + 1];
		horizontalWalls = new Block[yCells + 1][xCells];
		cells = new Block[yCells][xCells];
		
		ArrayList<Block> visitedCells = new ArrayList();

		createVerticalWalls(xCells + 1, yCells);
		createHorizontalWalls(xCells, yCells + 1);
		createCells();
		
		visitedCells.add(cells[0][0]);
		while(determinePresenceOfClosedCells() == true){
			chooseDirection(visitedCells.get(visitedCells.size() - 1).getColumnID(), visitedCells.get(visitedCells.size() - 1).getRowID(), visitedCells, populateDirections());
		}	
	}
	
	public void solve(){
		directions = new ArrayList();
		directions.add("N");
		directions.add("S");
		directions.add("W");
		directions.add("E");
		
		history = new ArrayList();
		history.add(cells[0][0]);
		
		route = new ArrayList();
		route.add("x");
		
		while (traverseVisited(yCells - 1, xCells - 1) != true)
		{
			if (forwardMotionNorth() == false || forwardMotionSouth() == false || forwardMotionWest() == false || forwardMotionEast() == false){
			} 
			else if (backwardMotionNorth() == true || backwardMotionSouth() == true || backwardMotionWest() == true || backwardMotionEast() == true){
			}				
		}
		
		route.add("x");
		
		createPath();
	}

	private void createVerticalWalls(int xVerticalWalls, int yVerticalWalls){
		for (int iy = 0; iy < yVerticalWalls; iy++){
			for (int ix = 0; ix < xVerticalWalls; ix++){
				verticalWalls[iy][ix] = new Block(iy, ix, ix * cellSize + ix * boundarySize, iy * cellSize + iy * boundarySize + boundarySize, false);
			}
		}
	}
	
	private void createHorizontalWalls(int xHorizontalWalls, int yHorizontalWalls){
		for (int iy = 0; iy < yHorizontalWalls; iy++){
			for (int ix = 0; ix < xHorizontalWalls; ix++){
				horizontalWalls[iy][ix] = new Block(iy, ix, ix * cellSize + ix * boundarySize + boundarySize, iy * cellSize + iy * boundarySize, false);
			}
		}
	}
	
	private void createCells(){
		for (int iy = 0; iy < yCells; iy++){
			for (int ix = 0; ix < xCells; ix++){
				cells[iy][ix] = new Block(iy, ix, ix * cellSize + ix * boundarySize + boundarySize, iy * cellSize + iy * boundarySize + boundarySize, true);
			}
		}
	}
	
	private void chooseDirection(int posX, int posY, ArrayList<Block> visitedCells, ArrayList<Integer> uncheckedDirections){
		Random rand = new Random();
		int direction = 0;
		int xChange = 0;
		int yChange = 0;
		int xResult = 0;
		int yResult = 0;
		boolean type = false;
		loop:
		while(uncheckedDirections.size() > 0){
			direction = uncheckedDirections.get(rand.nextInt(uncheckedDirections.size()));
			
			if (direction == 0){
				xChange = posX;
				yChange = posY - 1;
				xResult = posX;
				yResult = posY;
				type = true;
			}
			else if (direction == 1){
				xChange = posX;
				yChange = posY + 1;
				xResult = posX;
				yResult = posY + 1;
				type = true;
			}
			else if (direction == 2){
				xChange = posX - 1;
				yChange = posY;
				xResult = posX;
				yResult = posY;
				type = false;
			}
			else if (direction == 3){
				xChange = posX + 1;
				yChange = posY;
				xResult = posX + 1;
				yResult = posY;
				type = false;
			}
		
			try	{
				if (checkVisitedStatus(cells[yChange][xChange], visitedCells) == false){
					openWall(xResult, yResult, type);
				}
				visitedCells.add(cells[yChange][xChange]);
				uncheckedDirections.remove(findIndexOfRandom(uncheckedDirections, direction));
				break loop;
			}
			catch (ArrayIndexOutOfBoundsException e){
			}
		}
	}

	private void openWall(int posX, int posY, boolean type){
		if (type == false){
			verticalWalls[posY][posX].setOpen(true);
		}
		if (type == true){
			horizontalWalls[posY][posX].setOpen(true);
		}
	}
	
	private boolean determinePresenceOfClosedCells(){
		boolean present = false;
		loop:
		for (int y = 0; y < yCells; y++){
			for (int x = 0; x < xCells; x++){
				if (checkClosedWalls(cells[y][x]) == true){
					present = true;
					break loop;
				}
			}
		}
		return present;
	}
	
	private boolean checkClosedWalls(Block cell){
		boolean closed = false;
		if (horizontalWalls[cell.getRowID()][cell.getColumnID()].getOpen() == false && horizontalWalls[cell.getRowID() + 1][cell.getColumnID()].getOpen() == false && verticalWalls[cell.getRowID()][cell.getColumnID()].getOpen() == false && verticalWalls[cell.getRowID()][cell.getColumnID() + 1].getOpen() == false){
			closed = true;
		}
		return closed;
	}
	
	private boolean checkVisitedStatus(Block cell, ArrayList<Block> visitedCells){
		boolean result = false;
		loop:
		for (int i = 0; i < visitedCells.size(); i++){
			if (visitedCells.get(i).getColumnID() == cell.getColumnID() && visitedCells.get(i).getRowID() == cell.getRowID())  {
				result = true;
				break loop;
			}
		}
		return result;
	}
	
	private ArrayList<Integer> populateDirections(){
		ArrayList<Integer> uncheckedDirections = new ArrayList();
		uncheckedDirections.add(0);
		uncheckedDirections.add(1);
		uncheckedDirections.add(2);
		uncheckedDirections.add(3);
		return uncheckedDirections;
	}
	
	private int findIndexOfRandom(ArrayList directions, int randInt){
		int index = 0;
		loop:
		for (int i = 0; i < directions.size(); i++){
			if (directions.get(i).equals(randInt)){
				index = i;
				break loop;
			}
		}
		return index;
	}
	
	private boolean forwardMotionNorth(){
		boolean failed = true;
		try {
			if (traverseVisited(y - 1, x) == false && isWallOpen(horizontalWalls[y][x]) == true){
				route.add(directions.get(0));
				history.add(cells[y - 1][x]);
				x = x;
				y = y - 1;
				failed = false;
			}
		}
		catch (IndexOutOfBoundsException e){
		}
		return failed;
	}
	
	private boolean forwardMotionSouth(){
		boolean failed = true;
		try {
			if (traverseVisited(y + 1, x) == false && isWallOpen(horizontalWalls[y + 1][x]) == true){
				route.add(directions.get(1));
				history.add(cells[y + 1][x]);
				x = x;
				y = y + 1;
				failed = false;
			}
		}
		catch (IndexOutOfBoundsException e){
		}
		return failed;
	}
	
	private boolean forwardMotionWest(){
		boolean failed = true;
		try {
			if (traverseVisited(y, x - 1) == false && isWallOpen(verticalWalls[y][x]) == true){
				route.add(directions.get(2));
				history.add(cells[y][x - 1]);
				x = x - 1;
				y = y;
				failed = false;
			}
		}
		catch (IndexOutOfBoundsException e){
		}
		return failed;
	}
	
	private boolean forwardMotionEast(){
		boolean failed = true;
		try {
			if (traverseVisited(y, x + 1) == false && isWallOpen(verticalWalls[y][x + 1]) == true){
				route.add(directions.get(3));
				history.add(cells[y][x + 1]);
				x = x + 1;
				y = y;
				failed = false;
			}
		}
		catch (IndexOutOfBoundsException e){
		}
		return failed;
	}
		
	private boolean backwardMotionNorth(){
		boolean match = false;
		if (String.valueOf(route.get(route.size() - 1)).equals(directions.get(1))){
			x = x;
			y = y - 1;
			route.remove(route.size() - 1);
			route.trimToSize();
			match = true;
		}
		return match;
	}
	
	private boolean backwardMotionSouth(){
		boolean match = false;
		if (String.valueOf(route.get(route.size() - 1)).equals(directions.get(0))){
			x = x;
			y = y + 1;
			route.remove(route.size() - 1);
			route.trimToSize();
			match = true;
		}
		return match;
	}
	
	private boolean backwardMotionWest(){
		boolean match = false;
		if (String.valueOf(route.get(route.size() - 1)).equals(directions.get(3))){
			x = x - 1;
			y = y;	
			route.remove(route.size() - 1);
			route.trimToSize();
			match = true;
		}
		return match;
	}
	
	private boolean backwardMotionEast(){
		boolean match = false;
		if (String.valueOf(route.get(route.size() - 1)).equals(directions.get(2))){
			x = x + 1;
			y = y;
			route.remove(route.size() - 1);
			route.trimToSize();
			match = true;
		}
		return match;
	}
	
	private boolean isWallOpen(Block wall){
		boolean wallOpen = false;
		if (wall.getOpen() == true){
			wallOpen = true;
		}
		return wallOpen;
	}
	
	private boolean traverseVisited(int y, int x){
		boolean found = false;
		loop:
		for (int i = 0; i < history.size(); i++){
			if (history.get(i).getColumnID() == x && history.get(i).getRowID() == y){
				found = true;
				break loop;
			}
		}
		return found;
	}
	
	private void createPath(){
		path = new ArrayList();
		path.add(new int[]{0,0});
		for (int i = 0; i < route.size(); i++){
			if (String.valueOf(route.get(i)).equals(directions.get(0))){
				path.add(new int[]{path.get(i-1)[0],path.get(i-1)[1]-1});
			}
			else if (String.valueOf(route.get(i)).equals(directions.get(1))){
				path.add(new int[]{path.get(i-1)[0],path.get(i-1)[1]+1});
			}
			else if (String.valueOf(route.get(i)).equals(directions.get(2))){
				path.add(new int[]{path.get(i-1)[0]-1,path.get(i-1)[1]});
			}
			else if (String.valueOf(route.get(i)).equals(directions.get(3))){
				path.add(new int[]{path.get(i-1)[0]+1,path.get(i-1)[1]});
			}
		}
		path.add(new int[]{xCells - 1,yCells - 1});
	}
	
	public void highlightCell(Color color, int posX, int posY, int type){
		graphics.setColor(color);
		if (type == 0){
			graphics.fillRect(horizontalWalls[posY][posX].getStartX(), horizontalWalls[posY][posX].getStartY(), cellSize, boundarySize);
		}
		else if (type == 1){
			graphics.fillRect(verticalWalls[posY][posX].getStartX(), verticalWalls[posY][posX].getStartY(), boundarySize, cellSize);
		}
		else if (type == 2){
			graphics.fillRect(cells[posY][posX].getStartX(), cells[posY][posX].getStartY(), cellSize, cellSize);
		}
	}
	
	private void drawBoundary(Color color){
		graphics.setColor(color);
		for (int iy = 0; iy <= yCells; iy++){
			for (int ix = 0; ix <= xCells; ix++){
				graphics.fillRect(ix * cellSize + ix * boundarySize, iy * cellSize + iy * boundarySize, boundarySize, boundarySize);
			}
		}
	}
	
	public void redraw(Color foreground, Color background, Color highlight){
		for (int iy = 0; iy < yCells + 1; iy++){
			for (int ix = 0; ix < xCells; ix++){
				if (horizontalWalls[iy][ix].getOpen() == true){
					highlightCell(background, ix, iy, 0);
				}
				else if(horizontalWalls[iy][ix].getOpen() == false){
					highlightCell(foreground, ix, iy, 0);
				}
			}
		}
		
		for (int iy = 0; iy < yCells; iy++){
			for (int ix = 0; ix < xCells + 1; ix++){
				if (verticalWalls[iy][ix].getOpen() == true){
					highlightCell(background, ix, iy, 1);
				}
				else if(verticalWalls[iy][ix].getOpen() == false){
					highlightCell(foreground, ix, iy, 1);
				}
			}
		}
	
		for (int iy = 0; iy < yCells; iy++){
			for (int ix = 0; ix < xCells; ix++){
				if (isCellInPath(ix, iy) == false){
					highlightCell(background, ix, iy, 2);
				}
				else if(isCellInPath(ix, iy) == true){
					highlightCell(highlight, ix, iy, 2);
				}
			}
		}
		
		drawBoundary(foreground);
		
		highlightCell(highlight, 0, 0, 1);
		highlightCell(highlight, xCells, yCells - 1, 1);
	}
	
	private boolean isCellInPath(int x, int y){
		boolean result = false;
		for (int i = 0; i < path.size(); i++){
			if (path.get(i)[0] == x && path.get(i)[1] == y){
				result = true;
				break;
			}
		}
		return result;
	}
	
	public byte[] processImage(){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] imageInByteArray = null;
		try{
			ImageIO.write(this, "jpg", baos);
			baos.flush();
			imageInByteArray = baos.toByteArray();
			baos.close();
		}
		catch(IOException e){
		}
		return imageInByteArray;
	}

	public Color getHighlight(){
		return this.highlight;
	}
}