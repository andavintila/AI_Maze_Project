import javax.swing.table.AbstractTableModel;

//model pentru tabelul ce ajuta la desenarea labirintului
//si la actualizarea valorilor din tabel si a culorilor
class MyTableModel extends AbstractTableModel {
    private String[] columnNames = null;
    private Object[][] data = null;
    int m, n;
    MyTableModel(int m, int n, Cell[][] maze){
    	this.m = m;
    	this.n = n;
    	this.data = maze;
    }
    public int getColumnCount() {
        return n;
    }

    public int getRowCount() {
        return m;
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);

    }
}