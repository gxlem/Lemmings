import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;


public class Stopper extends Lemmings implements Affecter{
/*этот класс является подклассом леммингов, его задача - заставить других леммингов изменить направление*/


	

	private static BufferedImage image0;
	private static BufferedImage image1;
	private static BufferedImage image2;
	private static BufferedImage image3;
	
	//основные изображения леммингов изменены на его цвет
	private static BufferedImage imageRightStopper;
	private static BufferedImage imageRightStepStopper;
	private static BufferedImage imageLeftStopper;
	private static BufferedImage imageLeftStepStopper;
	
	private static final int STOP_BEGIN_MAX = 20;//константа, чтобы знать, в каком состоянии блокируется начальная анимация, чем ниже, тем быстрее будет анимация.
	private int iStopBegin;//счетчик запуска блокирующей анимации

	private static final int STOP_MAX = 80;//константа, чтобы знать, в каком состоянии анимации блокировки мы находимся, чем ниже, тем быстрее будет анимация.
	private int iStop;//счетчик блокировки анимации
	



	public static void loadAssets(){
		try{
			image0 = ImageIO.read(new File("lemmings/stopper0.png"));
			image1 = ImageIO.read(new File("lemmings/stopper1.png"));
			image2 = ImageIO.read(new File("lemmings/stopper2.png"));
			image3 = ImageIO.read(new File("lemmings/stopper3.png"));
			
			imageRightStopper = ImageIO.read(new File("lemmings/lemmings1Stopper.png"));
			imageRightStepStopper = ImageIO.read(new File("lemmings/lemmings1stepStopper.png"));
			imageLeftStopper = ImageIO.read(new File("lemmings/lemmings2Stopper.png"));
			imageLeftStepStopper = ImageIO.read(new File("lemmings/lemmings2stepStopper.png"));
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	public Stopper(Lemmings l){
		super(l);
		
		this.height = image0.getHeight();
		this.width = image0.getWidth();
	}

	
	public void move(){
	//метод move, описывающий способ перемещения стопора
		if (!inWorld) return;
		if (!alive) return;
		if(!action){
			if (fall()) return;
			if (haveEnoughPlace()){
				this.job = World.STOPPER;
				this.action = true;
				iStopBegin = STOP_BEGIN_MAX;
				iStop = STOP_MAX;
				affectMap();
				direction = -direction;
			}
			else{//если ему не хватило места
				if (walk()) return;//он ищет это место пешком
				if (climbUp()) return;//забираясь наверх
				if (climbDown()) return;//или спускаться вниз
				direction = -direction;	//или даже изменить направление, пока он не найдет достаточно места
			}
			
		}
		else{
			if (fall()){//если он может внезапно упасть (пример: бомба)
				posY--;	//сброс карты в соответствии со своей старой позицией
				resetMap();
				posY++;
				w.changeJob(this,World.WALKER);
				return;
			}
			if (iStop == 0) iStop = STOP_MAX;
			else iStop--;
		}
		
	}
	
	public void drawAction(Graphics2D g){
		//Метод drawAction описывает, как Башер рисуется во время своей работы.
		//начинает блокировать анимацию
		if (iStopBegin>0){		
			g.drawImage(image0,posX-width/2,posY-height,null);
			iStopBegin--;
		}
		//анимированный блок
		else if (iStop<=STOP_MAX/4) g.drawImage(image1,posX-width/2,posY-height,null);
		else if (iStop<=2*STOP_MAX/4) g.drawImage(image3,posX-width/2,posY-height,null);
		else if (iStop<=3*STOP_MAX/4) g.drawImage(image2,posX-width/2,posY-height,null);
		else g.drawImage(image3,posX-width/2,posY-height,null);
	}
	
	public void affectMap(){
		if(!alive) return;
		for(int i = 0;i<height;i++) {
			w.setMapTypeAtPos(posX-(width/2),posY-i,w.STOPPER_WALL_LEFT_CST);
			
			w.setMapTypeAtPos(posX+(width/2),posY-i,w.STOPPER_WALL_RIGHT_CST);
		}
	}
	
	public void resetMap(){
		for(int i = 0;i<height;i++){
			w.setMapTypeAtPos(posX-(width/2),posY-i,w.AIR_CST);
			
			w.setMapTypeAtPos(posX+(width/2),posY-i,w.AIR_CST);
		}
	}
	
	public boolean haveEnoughPlace(){
		//Метод haveEnoughPlace проверяет, достаточно ли места в этой позиции стопора, чтобы заблокировать другие
		for (int i=posX-(width/2);i<=posX+(width/2);i++){
			for (int j=posY-height;j<=posY;j++){
				if(w.getPos(i,j) != World.AIR_CST) return false;
			}
		}

		return true;
	}
	
	
	public BufferedImage getImageRight(){
		return imageRightStopper;
	}
	public BufferedImage getImageRightStep(){
		return imageRightStepStopper;
	}
	public BufferedImage getImageLeft(){
		return imageLeftStopper;
	}
	public BufferedImage getImageLeftStep(){
		return imageLeftStepStopper;
	}
}
