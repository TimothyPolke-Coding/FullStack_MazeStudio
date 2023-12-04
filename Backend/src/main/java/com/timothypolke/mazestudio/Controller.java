package com.timothypolke.mazestudio;

import java.awt.Color;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class Controller{	
	
	private Maze maze = null;
	private byte[] bytes;	
	
	@GetMapping("")
	public byte[] processMaze(@RequestParam("color") String color, @RequestParam("solution") String solution, @RequestParam("version") String version){
		int x = 0;
		int y = 0;
	
		Color color1 = null;
		Color color2 = null;
		
		if (version.equals("new")){
			if (color.equals("light")){
				color1 = new Color(0, 0, 0);
				color2 = new Color(255, 255, 255);
			}
			else if (color.equals("dark")){
				color1 = new Color(255, 255, 255);
				color2 = new Color(0, 0, 0);
			}
			
			maze = new Maze(50, 50, color1, color2);
			
			if (solution.equals("on")){
				maze.redraw(color1, color2, maze.getHighlight());
			}
			else if (solution.equals("off")){
				maze.redraw(color1, color2, color2);
			}
		}
		else if (version.equals("current")){
			if (color.equals("light")){
				color1 = new Color(0, 0, 0);
				color2 = new Color(255, 255, 255);
			}
			else if (color.equals("dark")){
				color1 = new Color(255, 255, 255);
				color2 = new Color(0, 0, 0);
			}
			
			if (solution.equals("on")){
				maze.redraw(color1, color2, maze.getHighlight());
			}
			else if (solution.equals("off")){
				maze.redraw(color1, color2, color2);
			}
		}	
		
		bytes = maze.processImage();
		
		System.out.println("received successfully..");
		
		return bytes;
	}
}



