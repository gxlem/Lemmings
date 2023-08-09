import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Walker extends Lemmings{//Класс Walker (будет абстрактным)


	public Walker(int posX, int posY){
		super(posX,posY);
	}

	public Walker(Lemmings l){
		super(l);
	}
	

		
	public void move(){
	//перемещать лемминга по миру
		if (!inWorld) return;
		if (fall()) return;
		if (walk()) return;
		if (climbUp()) return;		
		if (climbDown()) return;
		direction = -direction;
	}
	
	public void drawAction(Graphics2D g){}
	
	public BufferedImage getImageRight(){
		return imageRight;
	}
	public BufferedImage getImageRightStep(){
		return imageRightStep;
	}
	public BufferedImage getImageLeft(){
		return imageLeft;
	}
	public BufferedImage getImageLeftStep(){
		return imageLeftStep;
	}
	
}
