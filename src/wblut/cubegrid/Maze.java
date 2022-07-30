package wblut.cubegrid;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public class Maze {
	  int layers, rows, columns, unvisited;
	  Cell[][][] cells;
	  PApplet home;
	  public Maze(int layers, int rows, int columns,PApplet home) {
	    this.layers=layers;
	    this.rows=rows;
	    this.columns=columns;
	    this.home=home;
	    initGrid();
	    configureCells();
	    AldousBroder();
	  }

	  void initGrid() {
	    cells=new Cell[layers][rows][columns];
	    for (int l=0; l<layers; l++) {
	      for (int r=0; r<rows; r++) {
	        for (int c=0; c<columns; c++) {
	          cells[l][r][c]=new Cell(l, r, c);
	        }
	      }
	    }
	  }

	  void configureCells() {
	    unvisited=layers*rows*columns;
	    for (int l=0; l<layers; l++) {
	      for (int r=0; r<rows; r++) {
	        for (int c=0; c<columns; c++) {
	          cells[l][r][c].N=getCell(l, r-1, c);
	          cells[l][r][c].S=getCell(l, r+1, c);
	          cells[l][r][c].W=getCell(l, r, c-1);
	          cells[l][r][c].E=getCell(l, r, c+1);
	          cells[l][r][c].U=getCell(l+1, r, c);
	          cells[l][r][c].D=getCell(l-1, r, c);
	        }
	      }
	    }
	     for (int l=layers/3; l<2*layers/3; l++) {
	      for (int r=rows/3; r<2*rows/3; r++) {
	        for (int c=columns/3; c<2*columns/3; c++) {
	          cells[l][r][c].visited=true;
	     unvisited--;
	        }
	      }
	    }
	  }

	  public Cell getCell(int layer, int row, int column) {
	    if (layer>=0 && layer<layers && row>=0 && row<rows && column>=0&column<columns) {
	      return cells[layer][row][column];
	    }
	    return null;
	  }

	  Cell getRandomCell() {
	    return getCell((int)home.random(layers), (int)home.random(rows), (int)home.random(columns));
	  }

	  void AldousBroder() {
	    Cell cell= getRandomCell();
	    if(cell.visited){     
	    }else{
	    cell.visited=true;
	     unvisited--;
	    }
	    cell.label=0;
	    Cell neighbor;
	 
	    List<Cell> neighbors=new ArrayList<Cell>();
	    while (unvisited>0) {
	      cell.getNeighbors(neighbors);
	      neighbor=neighbors.get((int)home.random(neighbors.size()));
	      if (!neighbor.visited) {
	        cell.link(neighbor);
	        neighbor.visited=true;
	        unvisited--;
	      }
	      cell=neighbor;
	    }
	  }

	  public boolean isLinked(int l1, int r1, int c1, int l2, int r2, int c2) {
	    Cell cell1=getCell(l1, r1, c1);
	    Cell cell2=getCell(l2, r2, c2);
	    return cell1!=null && cell2!=null && cell1.isLinked(cell2);
	  }
	}


	