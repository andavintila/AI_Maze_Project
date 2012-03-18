// Alexandra Vintila
// 342C3

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

//codificari

/*	indicii NV = 11, N = 12, NE = 13, E = 14, SE = 15, S = 16, SV = 17, V = 18
	capcana 0.x
	obstacol -1
	liber 0
	artefact 10
	*/

/*	euristici
		indiciu 0
		liber 1
		capcana 1.x
		obstacol 2
		artefact -1
		*/


public class Main extends JPanel{
	static int count = 0;
	static final int NW = 11, N = 12, NE = 13, E = 14, SE = 15, S = 16, SW = 17, W = 18;
	static final int free = 0, obs = 1, trap = 2, hint = 3, artefact = 10, out = 9;
	static Cell[][] maze, tableMaze;	
	static int m, n, inX, inY;
	static boolean knowsExit = false;
	static boolean artefactFound = false;
	static int hitPoints;
	static int vision;
	static int currentPosX, currentPosY;
	static boolean missionFailed = false;
	static boolean missionAccomplished = false;
	static Vector<Cell> path = new Vector<Cell>();
	static JTable tbl;
	static JLabel hitP;
	static JFrame frm;
	
	//citirea datelor din fisier
	static void readMaze(String file){
		String str;
		try{
			BufferedReader r = new BufferedReader(new FileReader(file));
			str = r.readLine();
			hitPoints = Integer.parseInt(str);
			str = r.readLine();
			vision = Integer.parseInt(str);
			System.out.println("Vision: " + vision);
			str = r.readLine();
			String[] nr = str.split(" ");
			m = Integer.parseInt(nr[0]);
			n = Integer.parseInt(nr[1]);
			maze = new Cell[m][n];
			str = r.readLine();
			nr = str.split(" ");
			inX = Integer.parseInt(nr[0]);
			inY = Integer.parseInt(nr[1]);
			for (int i = 0; i < m; i++){
				str = r.readLine();
				nr = str.split(" ");
				for (int j = 0; j < n; j++){
					float code = Float.parseFloat(nr[j]);
					if (code == 0) maze[i][j] = new Cell(code,1,free,i,j);
					else if (code > 0 && code < 1) maze[i][j] = new Cell(code,code+1,trap,i,j);
					else if (code == -1) maze[i][j] = new Cell(code,2,obs,i,j);
					else if (code == 9) maze[i][j] = new Cell(code,1,out,i,j);
					else if (code == 10) maze[i][j] = new Cell(code,-1,artefact,i,j);
					else if (code > 10 && code < 19) maze[i][j] = new Cell(code,0,hint,i,j);
				}
			}
			r.close();
		}
		catch(Exception e){e.printStackTrace();}
	}
	static void printMaze(int x, int y){
		for (int i = 0; i < m; i++){
			for (int j = 0; j < n; j++){
				if (i == x && j == y)
					System.out.print("x ");
				else System.out.print(maze[i][j].code + " ");
				
			}
			System.out.println();
		}
		System.out.println();
	}
	static void printValues(){
		for (int i = 0; i < m; i++){
			for (int j = 0; j < n; j++){
				System.out.print(maze[i][j].value + " ");
			}
			System.out.println();
		}	
		System.out.println();
	}
	
	//modificarea prioritatilor in functie de indiciu
	static void processHint(int x, int y){
		switch((int)maze[x][y].code){
		case NW: updateValues(0,0,x,y); break;
		case N: updateValues(0,y,x,y+1); break;
		case NE: updateValues(0,y+1,x,n); break;
		case E: updateValues(x,y+1,x+1,n); break;
		case SE: updateValues(x+1,y+1,m,n); break;
		case S: updateValues(x+1,y,m,y+1); break;
		case SW: updateValues(x+1,0,m,y); break;
		case W: updateValues(x,0,x+1,y); break;
		}
	}
	
	//se modifica prioritatile doar pentru celulele ce nu sunt bombe 
	static void updateValues(int startX, int startY, int stopX, int stopY){
		for (int i = startX; i < stopX; i++)
			for (int j = startY; j < stopY; j++){
				if (maze[i][j].type != obs && maze[i][j].type != trap) maze[i][j].value--;
			}
	}
	
	//dezamorsarea capcanei
	static void processTrap(int x, int y){
		Cell c = maze[x][y];
		int result = (int)(Math.random() * (100 + 1));
		if (c.code*100 > result){
			System.out.println("Defusing failed.");
			hitPoints = hitPoints - 1;
		}
		else System.out.println("Defusing succeded.");
		frm.setTitle("Hit Points: " + hitPoints + "");
	}
	
	static void printDirectPath(Cell c){
		ArrayList<Cell> path = new ArrayList<Cell>();
		while(c.parent!=null){
			path.add(c);
			c = c.parent;
		}
		path.add(c);
		Collections.reverse(path);
		for (int k = 0; k < path.size(); k++)
			printMaze(path.get(k).x,path.get(k).y);
	
	}
	
	//modificarea prioritatilor in cazul in care se observa artefactul sau un indiciu pe un culoar gol
	static void see(Cell c){
		boolean useful = false;
		int i;
		//south
		for(i = c.x+1; i < m; i++){
			if(maze[i][c.y].type == obs )
				break;
			if(maze[i][c.y].type == trap){
				useful = false;
				break;
			}
			if(maze[i][c.y].type == hint || maze[i][c.y].type == artefact){
				useful = true;
				break;
			}
		}
		if (useful){
			if (i >= c.x + vision && c.x+vision <= m)
				updateValues(c.x+1, c.y, c.x+vision, c.y+1);
			else updateValues(c.x+1, c.y, i, c.y+1);
		
		}
		useful = false;
		//north
		for(i = c.x-1; i >=0; i--){
			if(maze[i][c.y].type == obs )
				break;
			if( maze[i][c.y].type == trap){
				useful = false;
				break;
			}
			if(maze[i][c.y].type == hint || maze[i][c.y].type == artefact){
				useful = true;
				break;
			}
		}
		if (useful){
			if (i+1 < c.x - vision && c.x - vision >= 0)
				updateValues(c.x - vision, c.y, c.x, c.y+1);
			else updateValues(i+1, c.y, c.x, c.y+1);
		}
		useful = false;
		//east
		for(i = c.y-1; i >=0; i--){
			if(maze[c.x][i].type == obs )
				break;
			if( maze[c.x][i].type == trap){
				useful = false;
				break;
			}
			if(maze[c.x][i].type == hint || maze[c.x][i].type == artefact){
				useful = true;
				break;
			}
		}
		if (useful){
			if (i+1 < c.y -vision && c.y - vision >= 0)
				updateValues(c.x, c.y-vision, c.x+1, c.y);
			else
				updateValues(c.x, i+1, c.x+1, c.y);
		}
		useful = false;
		//west
		for(i = c.y+1; i < n; i++){
			if(maze[c.x][i].type == obs )
				break;
			if( maze[c.x][i].type == trap){
				useful = false;
				break;
			}
			if(maze[c.x][i].type == hint || maze[c.x][i].type == artefact){
				useful = true;
				break;
			}
		}
		if (useful){
			System.out.println("UPDATE");
			if (i > c.y + vision && c.y + vision <= n){
				updateValues(c.x, c.y+1, c.x+1, c.y+vision);
			}
			else
				updateValues(c.x, c.y+1, c.x+1, i);
		}
	}
	
	//procesarea celulei curente
	static void explore(Cell c) throws InterruptedException{
		Thread.sleep(500);
		if (c.type == artefact){
			artefactFound = true;
			System.out.println("Artefact found");
		}
		//daca se afla la margine si a gasit artefactul
		if ((c.type == out || c.x == 0 || c.y == 0 || c.y == n-1 || c.x == m-1) && artefactFound){
			System.out.println("Mission accomplished");
			missionAccomplished = true;
			return;
		}
		
		if (!artefactFound) {
			if (c.type == hint){
				processHint(c.x, c.y);
				c.code = 0;
				c.type = free;
				c.value = 1;
				Thread.sleep(1000);
				tbl.setValueAt(c, c.x, c.y);
			}
			
			if (c.type == trap){
				processTrap(c.x, c.y);
				c.type = free;
				c.value = 1;
				c.code = 0;
				Thread.sleep(1000);
				tbl.setValueAt(c, c.x, c.y);
			}
		}
		if (hitPoints == 0){
			frm.setTitle("Hit Points: " + hitPoints + "");
			missionFailed = true;
			System.out.println("You failed to recover the artefact."); 
			return;
		}
	}
	
	//parcurgerea labirintului
	static void bfs() throws InterruptedException
	{
		Comparator<Cell> comp = new CellComparator();
		PriorityQueue<Cell> q=new PriorityQueue<Cell>(m*n, comp);
		q.add(maze[inX][inY]);
		maze[inX][inY].visited=true;
		while(!q.isEmpty())
		{
			Cell c = q.poll();
			c.visited = true;
			c.currentPos = true;
			Thread.sleep(100);
			tbl.setValueAt(c, c.x, c.y);
			explore(c);

			if (missionAccomplished || missionFailed) return;
			Cell child=null;
			boolean visionSet = false;
			
			if (vision>1){
				see(c);
			}
		
			if (c.x > 0 && maze[c.x-1][c.y].type!=obs){
				child = maze[c.x-1][c.y];
				if (!child.visited){
					child.visited = true;
					child.parent = c;
					q.add(child);
				}
			}
			if (c.x < m-1 && maze[c.x+1][c.y].type!=obs){
				child = maze[c.x+1][c.y];
				if (!child.visited){
					child.visited = true;
					child.parent = c;
					q.add(child);
				}
			}
			if (c.y > 0 && maze[c.x][c.y-1].type!=obs){
				child = maze[c.x][c.y-1];
				if (!child.visited){
					child.visited = true;
					child.parent = c;
					q.add(child);
				}
			}
			if (c.y < n-1 && maze[c.x][c.y+1].type!=obs){
				child = maze[c.x][c.y+1];
				if (!child.visited){
					child.visited = true;
					child.parent = c;
					q.add(child);
				}
			}
			
			c.currentPos = false;
			Thread.sleep(1000);
			tbl.setValueAt(c, c.x, c.y);
		}
	
	}
	
	
	
	public static void main(String[] args) throws InterruptedException {
		if (args.length != 1) {
			System.out.println("No file with the maze.");
		}
		readMaze(args[0]);
		frm = new JFrame("");
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tableMaze = new Cell[m][n];
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				tableMaze[i][j] = new Cell (maze[i][j]);
		MyTableModel model = new MyTableModel(m, n, tableMaze);
		tbl = new JTable(model);
		
		MyTableCellRenderer renderer = new MyTableCellRenderer();
		
		renderer.setHorizontalAlignment(JLabel.CENTER);
		tbl.setDefaultRenderer(Cell.class, renderer);
		frm.add(tbl);
		frm.setSize(400,300);
		frm.setVisible(true);
		frm.setTitle("Hit Points: " + hitPoints + "");
		hitP = new JLabel("Hit Points: " + hitPoints + "");
		frm.add(hitP);
	
		
		bfs();
		
	}
}
