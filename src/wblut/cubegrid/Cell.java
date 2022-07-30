package wblut.cubegrid;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class Cell {
	  int layer, row, column;
	  Cell N, S, E, W, U, D;
	  public LinkedHashSet<Cell> links;
	  boolean visited;
	  int label;
	  Cell(int layer, int row, int column) {
	    this.layer=layer;
	    this.row=row;
	    this.column=column;
	    links= new LinkedHashSet<Cell>();
	    visited=false;
	  }

	  void link(Cell cell, boolean bidirectional) {
	    links.add(cell);
	    if (bidirectional) cell.link(this, false);
	  }

	  void unlink(Cell cell, boolean bidirectional) {
	    links.remove(cell);
	    if (bidirectional) cell.unlink(this, false);
	  }

	  void link(Cell cell) {
	    link(cell, true);
	  }

	  void unlink(Cell cell) {
	    unlink(cell, true);
	  }

	  boolean isLinked(Cell cell) {
	    return links.contains(cell);
	  }

	  Collection<Cell> getLinks() {
	    return links;
	  }

	  void getNeighbors(List<Cell> neighbors) {
	    neighbors.clear();
	    if (U!=null) neighbors.add(U);
	    if (D!=null) neighbors.add(D);
	    if (N!=null) neighbors.add(N);
	    if (S!=null) neighbors.add(S);
	    if (E!=null) neighbors.add(E);
	    if (W!=null) neighbors.add(W);
	  }
	}

