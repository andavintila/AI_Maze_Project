import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

//clasa folosita pentru a implementa schimbarea culorilor celulelor in functie de tipul lor
class MyTableCellRenderer extends DefaultTableCellRenderer {
	public Component getTableCellRendererComponent(JTable table,
                                          Object value,
                                          boolean isSelected,
                                          boolean hasFocus,
                                          int row,
                                          int column) {
		Component c = super.getTableCellRendererComponent(table, value,
                                   isSelected, hasFocus,
                                   row, column);


		if (((Cell)value).type == Main.trap && (row == ((Cell)value).x) && (column == ((Cell)value).y) ) {
			c.setBackground(Color.red);
		}
		if (((Cell)value).type == Main.obs)
			c.setBackground(Color.gray);
		if (((Cell)value).type == Main.free)
			c.setBackground(Color.white);
		if (((Cell)value).type == Main.artefact){
			c.setBackground(Color.pink);
		}
		if (((Cell)value).type == Main.hint){
			c.setBackground(Color.blue);
		}
		return c;
	}
}