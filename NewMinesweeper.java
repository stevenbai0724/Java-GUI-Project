/*
Steven Bai & Daniel Zhang
ICS 3U7 Final Project
Jan 19, 2020


*The game that we decided to code is Minesweeper, a single player game that involves flagging hidden mines on a 10x10 grid
*Mine locations are secret and can only be deciphered through logic and the number of the tiles (displays how many mines surround that tile)
*First click is always a zero, tiles with zero are automatically cleared of their surroundings
*User wins when all 15 mines are correctly flagged (When user right clicks, shown as "F"), with no extra flags
*User can surrender at any time to reveal the mines and lose the game

*/
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class NewMinesweeper extends JFrame implements ActionListener, MouseListener{

	JFrame gameFrame;
	JPanel gamePanel;
	Container grid;
	JButton [][] buttons; //front end buttons
	int [][] secret; //back end mines and numbers
	int [][] flags; //back end flags
	final int SIDE = 10;
	final int MINE = 10;  
	final int FLAG = 11;
	int flagCount; //total num flags, this number can't exceed 15 for user to win
	int flagsLeft; //how many flags the user has left. If user places >15 flags, this will display as negative number
	JButton reset;
	JButton surrender;
	JButton test;
	boolean firstClick = true;
	ArrayList<Integer> positions;
	ArrayList<Integer> minePositions;
	

	public NewMinesweeper(){

		gameFrame = new JFrame("New Minesweeper");
        gamePanel = new JPanel();  
        gameFrame.add(gamePanel, BorderLayout.NORTH);   
		
		
        reset = new JButton("New Game");   
		reset.addActionListener(this); 
        reset.setActionCommand("Reset button"); //subject to change
        gamePanel.add(reset);
		
        surrender = new JButton("Surrender");    
		surrender.addActionListener(this);  
        surrender.setActionCommand("Surrender button"); //subject to change       
		gamePanel.add(surrender);   
		
		test = new JButton("Developer Mode");
		test.addActionListener(this);  
        test.setActionCommand("Test button"); //subject to change       
		gamePanel.add(test);   

        grid = new Container();   
		grid.setLayout(new GridLayout(SIDE,SIDE));
		secret = new int[SIDE][SIDE]; 
		flags = new int[SIDE][SIDE];
		buttons = new JButton[SIDE][SIDE];  


		//grid
		for (int i = 0; i < SIDE; i++){
			for (int j = 0; j < SIDE; j++){
                    buttons[i][j] = new JButton(); 
                    buttons[i][j].setBackground(Color.GREEN); 
					buttons[i][j].addActionListener(this);     
					buttons[i][j].addMouseListener( this );
                    buttons[i][j].setActionCommand(i + "" + j);   //coordinates of button serves as indentifier for each button, used in both actionperformed and mousepressed
                    grid.add(buttons[i][j]);    
                
			}
        }
		
		gameFrame.add(grid, BorderLayout.CENTER);
		
		gameFrame.setSize(800, 800);   
        gameFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);    
        gameFrame.setVisible(true);
        gameFrame.setResizable(false);
		
		
	}

	//flagging
	public void mousePressed( MouseEvent e){

		if (SwingUtilities.isRightMouseButton(e)){ //handles flagging if user right clicks on button
			JButton b = (JButton)e.getSource();
			String str = b.getActionCommand();

			int x = 0;
			int y = 0;

			//getting coordinates of clicked tile 
			x = Integer.parseInt(str.substring(0,1));
			y = Integer.parseInt(str.substring(str.length()-1));

			if (flags[x][y] != FLAG && buttons[x][y].isEnabled() && firstClick == false ){ //if not flagged yet
				buttons[x][y].setText("F");
				flags[x][y] = FLAG; //update back end flag array
				buttons[x][y].setFont(new Font("Arial", Font.PLAIN, 30));
				buttons[x][y].setForeground(Color.BLACK);

				//increase flagCount if user flagged a tile correctly
				if (secret[x][y] == MINE){
					flagCount ++; 
				}
				flagsLeft--; 

				System.out.println("Flags left: " + flagsLeft);

				checkWin();
			}

			else if (flags[x][y] == FLAG  && firstClick == false){ //remove flag if already flagged

				buttons[x][y].setText("");
				flags[x][y] = 0; //remove FLAG from back end array
				buttons[x][y].setFont(new Font("Arial", Font.PLAIN, 30));
				buttons[x][y].setForeground(Color.BLACK);

				
				if (secret[x][y] == MINE){
					flagCount--; 
				}

				flagsLeft++;
				System.out.println("Flags left: " + flagsLeft);

				checkWin();

			}


		}
	}

	//required methods that needs to be added with MouseListener, serves no applicable function in our game
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	
	
	public void generateMines(int x, int y ){

		positions = new ArrayList<>(); 
		minePositions = new ArrayList<>(); 
		secret = new int[SIDE][SIDE];  
		//flag counters reset when generateMines is called to reset the game
		flagCount = 0; 
		flagsLeft = 15;

        for (int i = 0; i < SIDE; i++){
            for (int j = 0; j < SIDE; j++){
                positions.add(i*100+j);   
            }
        }
	
		//storing positions of 15 random mines
		for (int i = 0; i<15; i++){
			int thisMine = (int)(Math.random() * (100 -i));
			minePositions.add(positions.get(thisMine));
			secret[(positions.get(thisMine))/100][positions.get(thisMine)%100] = MINE;
			positions.remove(thisMine);
			
		}
	
		//checking every 100 tiles
		for (int i = 0; i < SIDE; i++){
			for (int j = 0; j < SIDE; j++){

				if (secret[i][j] == MINE){
					continue;
				}
				int counter = 0;

				/*The nested for loop bellow is creating a 3x3 square with the selected tile in the center, at position [i][j]
				*This loops runs through each adjacent tile by adding/subtracting 1 to the i and/or j value
				*For example, this loop will check [i][j-1], [i][j+1], [i+1][j] and so on
				*If the value of i or j is negative or greater than 9, it means that the tile being checked is outside our grid
				*In the above case, that tile shall be skipped to avoid index out of range errors
				*/
				for (int k = -1; k< 2; k++){

					for (int l = -1; l<2; l++){
				
						if (i-k <0 || i-k >SIDE-1 || j-l<0 || j-l >SIDE -1 ){ //skip tile if it is outside of 10x10 grid 
							continue;
						}
						if (secret[i-k][j-l] == MINE){
							counter++;
						}
					
					}
				}
				secret[i][j] = counter;
		
			}
		}

		
		//using brute force to ensure that first click is ALWAYS 0, regardless of which tile
		if (secret[x][y] ==0 ){ //stop generating once tile of first click is a zero
			return;
		}
		else{ //if tile of first click is not zero, repeat the generation of mines
			generateMines(x, y);
		}
		

		

	}	

	public void chainRxn(int x, int y){

		//Same type of for loop used and explained in generate mines
		//checks surrounding tiles by creating a 3x3 square
		for (int i = -1; i<2; i++){
			for (int j = -1; j<2; j++){

				if (x-i <0 || x-i >SIDE-1 || y-j<0 || y-j >SIDE -1 ){ //skip if tile does not exists within dimensions of our grid
					continue;
				}

				else if (secret[x-i][y-j] == 0 && buttons[x-i][y-j].isEnabled()){
					buttons[x-i][y-j].setEnabled(false);
					buttons[x-i][y-j].setText("0");
					buttons[x-i][y-j].setFont(new Font("Arial", Font.PLAIN, 30));
					buttons[x-i][y-j].setForeground(Color.BLACK);
					chainRxn(x-i,y-j); //use recursion to check the adjacent tiles of the same zero tile

				}
				else{ //show the tile's number, do not contiune checking if it isn't a zero
					buttons[x-i][y-j].setEnabled(false);
					buttons[x-i][y-j].setText("" + secret[x-i][y-j]);
					buttons[x-i][y-j].setFont(new Font("Arial", Font.PLAIN, 30));
					buttons[x-i][y-j].setForeground(Color.BLACK);
				}
			}
		}
	}

	public void actionPerformed (ActionEvent e){
		
		JButton b = (JButton)e.getSource();
		String str = b.getActionCommand();

		if (str.equals("Reset button")){
			//new game
			System.out.println("-----------------------");
			for (int i = 0; i< SIDE; i++){
				for (int j = 0; j< SIDE; j++){
					buttons[i][j].setEnabled(true);
					buttons[i][j].setText("");
					flags[i][j] = 0;
				}
			}
			firstClick = true;
			flagCount = 0;
			flagsLeft = 15;

		}
		else if (str.equals("Surrender button") && firstClick ==false ){
			death();
		}

		else if (str.equals("Surrender button") && firstClick){ 
			return; //do nothing if user surrenders before first click
		}

		else if (str.equals("Test button")){
			//reveals mines and numbers without death message, simply for debugging purposes
			for (int i = 0; i< SIDE; i++){
				for (int j = 0; j< SIDE; j++){
					buttons[i][j].setEnabled(false);

					if (secret[i][j] ==MINE){
						buttons[i][j].setText("X");
						buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 30));
						buttons[i][j].setForeground(Color.BLACK);
					}
					else{
						buttons[i][j].setText("" + secret[i][j] );
						buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 30));
						buttons[i][j].setForeground(Color.BLACK);
					}
				}
			}

		}

		else{
			int buttonX = 0;
			int buttonY = 0;

			//coordinates of button must be obtained by parseInting the actionCommand because it is a string value
			buttonX = Integer.parseInt(str.substring(0,1));
			buttonY = Integer.parseInt(str.substring(str.length()-1));


			if (firstClick){//mines are generated only after the first click 
				firstClick = false;
				generateMines(buttonX, buttonY);
			}

			if (secret[buttonX][buttonY] == MINE){
				death();
			}

			//start chainRxn if user clicks on zero
			else if (secret[buttonX][buttonY] == 0 ){
				buttons[buttonX][buttonY].setEnabled(false);
				buttons[buttonX][buttonY].setText("" + secret[buttonX][buttonY]);
				buttons[buttonX][buttonY].setFont(new Font("Arial", Font.PLAIN, 30));
				buttons[buttonX][buttonY].setForeground(Color.BLACK);
				chainRxn(buttonX, buttonY);


			}
			else { //display non-zero number
				buttons[buttonX][buttonY].setEnabled(false);
				buttons[buttonX][buttonY].setText("" + secret[buttonX][buttonY]);
				buttons[buttonX][buttonY].setFont(new Font("Arial", Font.PLAIN, 30));
				buttons[buttonX][buttonY].setForeground(Color.BLACK);
			}
			
		}
		
		
	}

	public void death(){
		
		//show all mines positions if user dies or surrenders
		for (int i = 0; i < 15; i++){
			int x = minePositions.get(0)/100;
			int y = minePositions.get(0)%100;
			minePositions.remove(0);

				buttons[x][y].setEnabled(false);
				buttons[x][y].setText("X");
				buttons[x][y].setFont(new Font("Arial", Font.PLAIN, 30));
				buttons[x][y].setForeground(Color.BLACK);
			
			
		}
		//disable all buttons
		for (int i = 0; i< SIDE; i++){
			for (int j=0; j< SIDE; j++){
				secret[i][j] = 0;
				buttons[i][j].setEnabled(false); //disable all buttons
			}
		}
		JOptionPane.showMessageDialog(null,"Better luck next time!", "You Lost!",  JOptionPane.INFORMATION_MESSAGE);

	}
	public void checkWin(){

		//user wins when all mines are correctly flagged and there are no extra flags 
		if(flagCount == 15 && flagsLeft == 0){
			for (int i = 0; i< SIDE; i++){
				for (int j = 0; j<SIDE; j++){
					buttons[i][j].setEnabled(false); //disable all buttons
				}
			}
		JOptionPane.showMessageDialog(null,"You won!", "Congradulations!",  JOptionPane.INFORMATION_MESSAGE);

		}

	}
	
	public static void main (String [] args){
		new NewMinesweeper();
	}



	
	
}