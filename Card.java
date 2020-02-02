import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Card {
	
	private int value;
	private String name, suit;
	private Font font;
	private BufferedImage suitImage;
	
	public Card(int value, String name, String suit){
		this.value = value;
		this.name = name;
		this.suit = suit;
		
		//font
		font = new Font("Arial",Font.PLAIN,50);
		
		//buffered image
		if( suit.equals("hearts") ){
			try{
				suitImage = ImageIO.read(new File("Images/hearts.png"));
			} catch(IOException e) {
				System.out.println(e);
			}
		} else if ( suit.equals("spades") ){
			try{
				suitImage = ImageIO.read(new File("Images/spades.png"));
			} catch(IOException e) {
				System.out.println(e);
			}
		} else if ( suit.equals("clubs") ){
			try{
				suitImage = ImageIO.read(new File("Images/clubs.png"));
			} catch(IOException e) {
				System.out.println(e);
			}
		} else if ( suit.equals("diamonds") ){
			try{
				suitImage = ImageIO.read(new File("Images/diamonds.png"));
			} catch(IOException e) {
				System.out.println(e);
			}
		}
	}
	
	public void drawMe(Graphics g, int x, int y){
		//draw card outline
		g.setColor(Color.WHITE);
		g.fillRect(x,y,120,150);
		g.setColor(Color.BLACK);
		g.drawRect(x,y,120,150);

		//draw the name
		g.setFont(font);
		if( suit.equals("hearts") || suit.equals("diamonds") ){
			g.setColor(Color.RED);
		} else if ( suit.equals("spades") || suit.equals("clubs") ){
			g.setColor(Color.BLACK);
		}
		if( name.equals("10") ){
			g.drawString(name,x+28,y+98);
		} else {
			g.drawString(name,x+44,y+98);
		}
		
		//draw suit image
		g.drawImage(suitImage,x+2,y+2,null);
	}
	
	public int getValue(){
		return value;
	}
	
	public String getName(){
		return name;
	}

	public void setValue(int num){
		value = num;
	}
	
}
