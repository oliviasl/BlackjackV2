import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Table extends JPanel implements ActionListener{
	
	private ArrayList<Card> deck, playerDeck, dealerDeck;
	private JButton hitButton, standButton, newGameButton, betButton;
	private JButton aceValue1Button, aceValue11Button;
	private int playerCardValue, dealerCardValue, playerPoints, dealerPoints;
	private int currentBet, balance, aceValue;
	private Font pointFont;
	private String winner, betPayout;
	private JTextField betField;
	private BufferedImage background;
	
	public Table(){
		setLayout(null);
		
		//image
		try{
			background = ImageIO.read(new File("Images/background.png"));
		} catch(IOException e) {
			System.out.println(e);
		}
		
		//decks
		deck = new ArrayList<Card>();
		String suit = "hearts";
		int valueCount = 2;
		for(int i = 0;i < 4;i ++){
			valueCount = 2;
			for(int j = 0;j < 9;j ++){
				deck.add(new Card(valueCount,String.valueOf(valueCount),suit));
				valueCount ++;
			}
			deck.add(new Card(10,"J",suit));
			deck.add(new Card(10,"Q",suit));
			deck.add(new Card(10,"K",suit));
			deck.add(new Card(11,"A",suit));
			if( i == 0 ){
				suit = "spades";
			} else if ( i == 1 ){
				suit = "clubs";
			} else if ( i == 2 ){
				suit = "diamonds";
			}
		}
		
		//player and dealer decks
		playerDeck = new ArrayList<Card>();
		dealerDeck = new ArrayList<Card>();
		
		//Jbuttons
		hitButton = new JButton("Hit");
		hitButton.setBounds(375,515,100,30);
		hitButton.addActionListener(this);
		add(hitButton);
		hitButton.setVisible(false);
		
		standButton = new JButton("Stand");
		standButton.setBounds(500,515,100,30);
		standButton.addActionListener(this);
		add(standButton);
		standButton.setVisible(false);
		
		newGameButton = new JButton("New Game");
		newGameButton.setBounds(450,515,100,30);
		newGameButton.addActionListener(this);
		add(newGameButton);
		newGameButton.setVisible(true);
		
		betButton = new JButton("Bet Amount");
		betButton.setBounds(790,325,100,30);
		betButton.addActionListener(this);
		add(betButton);
		betButton.setVisible(true);
		
		aceValue1Button = new JButton("Set Ace Value to 1");
		aceValue1Button.setBounds(760,450,175,30);
		aceValue1Button.addActionListener(this);
		add(aceValue1Button);
		
		aceValue11Button = new JButton("Set Ace Value to 11");
		aceValue11Button.setBounds(760,485,175,30);
		aceValue11Button.addActionListener(this);
		add(aceValue11Button);
		
		//JTextField
		betField = new JTextField();
		betField.setBounds(790,280,100,30);
		add(betField);
		
		//ints
		playerCardValue = 0;
		dealerCardValue = 0;	
		playerPoints = 0;
		dealerPoints = 0;
		balance = 100;
		currentBet = 5;
		aceValue = 11;
		
		//fonts
		pointFont = new Font("Arial",Font.PLAIN,20);
		
		//String
		winner = "";
		betPayout = "";
	}
	
	public Dimension getPreferredSize() {
		//Sets the size of the panel
		return new Dimension(1000,600);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		//background
		g.drawImage(background,0,0,null);
		
		//player cards
		int x = 60;
		int y = 280;
		for(int i = 0;i < playerDeck.size();i ++){
			playerDeck.get(i).drawMe(g,x,y);
			x += 90;
		}
		
		//dealer cards
		x = 60;
		y = 100;
		for(int i = 0;i < dealerDeck.size();i ++){
			dealerDeck.get(i).drawMe(g,x,y);
			x += 90;
		}
		if( !newGameButton.isVisible() ){
			g.setColor(Color.RED);
			g.fillRect(x,y,120,151);
			g.setColor(Color.BLACK);
			g.drawRect(x+10,y+10,100,130);
		}
		
		//card and point totals
		playerCardValue = calculateCardTotal(playerDeck);
		dealerCardValue = calculateCardTotal(dealerDeck);
		g.setFont(pointFont);
		g.setColor(Color.WHITE);
		g.drawString("Dealer Card Value: " + dealerCardValue,60,70);
		g.drawString("Player Card Value: " + playerCardValue,60,470);
		g.drawString("Dealer Points: " + dealerPoints,270,70);
		g.drawString("Player Points: " + playerPoints,270,470);
		
		//winner and payout message
		g.drawString(winner,780,80);
		g.drawString(betPayout,780,120);
		
		//balance and bet box
		g.setColor(Color.WHITE);
		g.fillRect(750,200,190,180);
		g.setColor(Color.RED);
		g.drawRect(750,200,190,180);
		
		//show balance and bet
		g.setColor(Color.BLACK);
		g.drawString("Balance: $" + balance,780,230);
		if( currentBet > balance && newGameButton.isVisible()){
			g.drawString("Bet is too high",780,260);
		} else if ( currentBet > 0 ){
			g.drawString("Current Bet: " + currentBet,780,260);
		} else {
			g.drawString("Please enter a bet",765,260);
		}
		
		//ace value
		g.setColor(Color.WHITE);
		g.drawString("Ace Value: " + aceValue,780,425);
		
		//automatic stand
		if( standButton.isVisible() ){
			automaticStand();
		}
		
	}
	
	public void shuffle(){
		for(int i = 0;i < deck.size();i ++){
			int randIndex = (int)(Math.random()*deck.size());
			Card holder = deck.get(i);
			deck.set(i,deck.get(randIndex));
			deck.set(randIndex,holder);
		}
	}
	
	public void actionPerformed(ActionEvent e){
		if( e.getSource() == newGameButton ){
			if( currentBet > 0 && currentBet <= balance ){
				hitButton.setVisible(true);
				standButton.setVisible(true);
				newGameButton.setVisible(false);
				newGameDeal();
				winner = "";
				betPayout = "";
				balance -= currentBet;
			}
		} else if ( e.getSource() == hitButton ){
			playerDeck.add(deck.get(0));
			deck.remove(0);
		} else if ( e.getSource() == standButton ){
			//add dealer cards
			dealerTurn();
			//check who won
			checkWinner();
			//set button visibility
			hitButton.setVisible(false);
			standButton.setVisible(false);
			newGameButton.setVisible(true);
		} else if ( e.getSource() == betButton && newGameButton.isVisible() ){
			currentBet = Integer.parseInt(betField.getText());
		} else if ( e.getSource() == aceValue1Button ){
			for(int i = 0;i < deck.size();i ++){
				if( deck.get(i).getName().equals("A") ){
					deck.get(i).setValue(1);
				}
			}
			for(int i = 0;i < dealerDeck.size();i ++){
				if( dealerDeck.get(i).getName().equals("A") ){
					dealerDeck.get(i).setValue(1);
				}
			}
			for(int i = 0;i < playerDeck.size();i ++){
				if( playerDeck.get(i).getName().equals("A") ){
					playerDeck.get(i).setValue(1);
				}
			}
			aceValue = 1;
		} else if ( e.getSource() == aceValue11Button ){
			for(int i = 0;i < deck.size();i ++){
				if( deck.get(i).getName().equals("A") ){
					deck.get(i).setValue(11);
				}
			}
			for(int i = 0;i < dealerDeck.size();i ++){
				if( dealerDeck.get(i).getName().equals("A") ){
					dealerDeck.get(i).setValue(11);
				}
			}
			for(int i = 0;i < playerDeck.size();i ++){
				if( playerDeck.get(i).getName().equals("A") ){
					playerDeck.get(i).setValue(11);
				}
			}
			aceValue = 11;
		}
		repaint();
	}
	
	public void newGameDeal(){
		//reset deck
		for(int i = 0;i < playerDeck.size();i ++){
			deck.add(playerDeck.get(i));
			playerDeck.remove(i);
			i --;
		}
		for(int i = 0;i < dealerDeck.size();i ++){
			deck.add(dealerDeck.get(i));
			dealerDeck.remove(i);
			i --;
		}
		
		shuffle();
		
		//player deck
		playerDeck.clear();
		playerDeck.add(deck.get(0));
		deck.remove(0);
		playerDeck.add(deck.get(0));
		deck.remove(0);
		
		//dealer deck
		dealerDeck.clear();
		dealerDeck.add(deck.get(0));
		deck.remove(0);
	}
	
	public int calculateCardTotal(ArrayList<Card> tempDeck){
		int total = 0;
		for(int i = 0;i < tempDeck.size();i ++){
			total += tempDeck.get(i).getValue();
		}
		return total;
	}
	
	public void dealerTurn(){
		while( dealerCardValue < 17 ){
			dealerDeck.add(deck.get(0));
			deck.remove(0);
			dealerCardValue = calculateCardTotal(dealerDeck);
		}
	}
	
	public void automaticStand(){
		if( playerCardValue > 21 || playerCardValue == 21 ){
			//check who won
			checkWinner();
			//set button visibility
			hitButton.setVisible(false);
			standButton.setVisible(false);
			newGameButton.setVisible(true);
			repaint();
		}
	}
	
	public void checkWinner(){
		if( playerCardValue > 21 ){
			winner = "Dealer Wins";
			dealerPoints ++;
			betPayout = "You lose $" + String.valueOf(currentBet);
		} else if ( playerCardValue == 21 ){
			winner = "Player Wins";
			playerPoints ++;
			balance += currentBet*2;
			betPayout = "You win $" + String.valueOf(currentBet);
		} else if ( dealerCardValue > 21 ){
			winner = "Player Wins";
			playerPoints ++;
			balance += currentBet*2;
			betPayout = "You win $" + String.valueOf(currentBet);
		} else if ( playerCardValue > dealerCardValue ){
			winner = "Player Wins";
			playerPoints ++;
			balance += currentBet*2;
			betPayout = "You win $" + String.valueOf(currentBet);
		} else if ( dealerCardValue > playerCardValue ){
			winner = "Dealer Wins";
			dealerPoints ++;
			betPayout = "Lose $" + String.valueOf(currentBet);
		} else if ( playerCardValue == dealerCardValue ){
			winner = "Tie: No Points Given";
			balance += currentBet;
			betPayout = "Bet returned";
		}
	} 
	
}






