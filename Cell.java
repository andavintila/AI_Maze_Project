import java.awt.Color;
import java.awt.Component;
import java.util.Comparator;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

//clasa folosita de coada de prioritati pentru a sorta celulele
class CellComparator implements Comparator<Cell>{
	@Override
	public int compare(Cell o1, Cell o2) {
		if (o1.value - o2.value < 0)
			return -1;
			else if (o1.value - o2.value > 0)
				return 1;
			else return 0;
	}
	
}

//clasa ce caracterizeaza o celula din labirint
class Cell {
	float code;
	float value;
	int type;
	int x;
	int y;
	boolean visited;
	boolean currentPos;
	Cell parent;
	
	public Cell(Cell c){
		this.code = c.code;
		this.value = c.value;
		this.type = c.type;
		this.x = c.x;
		this.y = c.y;
		this.visited = c.visited;
		this.parent = c.parent;
	}
	public Cell(float code, float value, int type, int x, int y) {
		super();
		this.code = code;
		this.value = value;
		this.type = type;
		this.x = x;
		this.y = y;
		this.visited = false;
		this.parent = null;
	}
	
	//codificari pentru desenarea traseului
	public String toString(){
		if (currentPos) {
			return "A";
		}
		else if (this.type == Main.free) return "";
		else if (this.type == Main.trap) return code +"";
		else if (this.type == Main.artefact) return "";
		else if (this.type == Main.hint) return "";
		else if (this.type == Main.out) return "";
		return "=====";
	}

	
}
