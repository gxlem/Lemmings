import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Miner extends Digger{



	private int directionY;
	
	//изображение для действий подрывщика
	private static BufferedImage minerImage1;
	private static BufferedImage minerImage2;
	private static BufferedImage minerImageUp;
	private static BufferedImage minerImageDown;
	
	private static BufferedImage minerReversedImage1;
	private static BufferedImage minerReversedImage2;
	private static BufferedImage minerReversedImageUp;
	private static BufferedImage minerReversedImageDown;
	

	protected static BufferedImage imageRightMiner;
	protected static BufferedImage imageRightStepMiner;
	protected static BufferedImage imageLeftMiner;
	protected static BufferedImage imageLeftStepMiner;	
	
	private int iMine;//счетчик для каждой анимации разрушения стены, используемый, чтобы знать, когда поменять изображение, а когда повлиять на карту.
	private int MINE_MAX = 30;
	private int MineCounter = 40;//счетчик для ограничения времени работы подрывщика
	
	private int radiusY;//дальность взрыва по оси Y
	private int radiusX;//диапазон взрыва по оси x
	private int stepHeight = 2;//Высота шага шахтера при движении вперед
	
	

		
	public static void loadAssets(){
		//загрузка анимации
		try{
			minerImage1 = ImageIO.read(new File("lemmings/miner1.png"));
			minerImage2 = ImageIO.read(new File("lemmings/miner2.png"));
			minerImageUp = ImageIO.read(new File("lemmings/minerUp.png"));
			minerImageDown = ImageIO.read(new File("lemmings/minerDown.png"));
			
			minerReversedImage1 = ImageIO.read(new File("lemmings/minerReverse1.png"));
			minerReversedImage2 = ImageIO.read(new File("lemmings/minerReverse2.png"));
			minerReversedImageUp = ImageIO.read(new File("lemmings/minerReverseUp.png"));
			minerReversedImageDown = ImageIO.read(new File("lemmings/minerReverseDown.png"));
			
			imageRightMiner = ImageIO.read(new File("lemmings/lemmings1Miner.png"));
			imageRightStepMiner = ImageIO.read(new File("lemmings/lemmings1stepMiner.png"));
			imageLeftMiner = ImageIO.read(new File("lemmings/lemmings2Miner.png"));
			imageLeftStepMiner = ImageIO.read(new File("lemmings/lemmings2stepMiner.png"));
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	public Miner(Lemmings l, int directionY){
		super(l);
		
		height = minerImage1.getHeight();
		width = minerImage1.getWidth();
		
		this.directionY = directionY;
		iMine = MINE_MAX;
		radiusX = width/4;
		radiusY = height;
	}
	
	


	public void move(){
	//метод move, описывающий движение подрывщика
		if (!inWorld) return;//проверить, есть ли он в мире
		if(!action){
			if (fall()) return;
			if (goAhead()) return;//если нет препятствий, то продолжает идти вперед
			this.job = World.MINER;//иначе случиться взрыв
			this.action = true;
			move();
		}else{
			if (fall()){
				//если случилось падение после начала действия, то оно завершается
				w.changeJob(this,World.WALKER);
				return;
			}
			if (iMine == 0){
				affectMap();
				iMine = MINE_MAX;//начать новую анимацию
				MineCounter--;//обновить MineCounter
				if (MineCounter == 0){
					//изменить свою работу через определенный период времени с помощью MineCounter
					w.changeJob(this,World.WALKER);
				}
				int newPosX = posX+direction*(radiusX+width/4);
				int newPosY = posY-directionY*stepHeight;
				if (!w.onBounds(newPosX,newPosY)){
					w.changeJob(this,World.WALKER);
				}
				posX += direction*(radiusX+width/4);
				posY -= directionY*stepHeight;
				//изменить свою позицию после взрыва
				return;
			}
			iMine--;//обновить счетчик анимации
			
		}
		
	
	}
	
	public boolean goAhead(){
		if (!super.walk()){
			//если юнит не можете ходить, он проверяет, сможет ли он подняться наверх.
			if (!super.climbUp()){
				//если юнит не может подняться навверх, он проверяет, может ли он спуститься вниз
				if(!super.climbDown()){
					//если юниту не удается спуститься вниз, то он проверяет, нет ли впереди препядствия
					if (super.checkForStopperWall()){
						//если впереди есть препядствие, то юнит возвращается назад и ищет обходной путь
						direction = -direction;
					}
					else{
						//если нет обходного пути, то юнит не будет совершать никаких действий
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public void drawAction(Graphics2D g){
	//метод изображения действий с использованием iMine для адаптации анимации
		if (direction == 1){
			if (iMine<(int)(1+MINE_MAX/3)){
				if (directionY == 1) g.drawImage(minerImageUp,posX-(width/2),posY-height,null);
				else g.drawImage(minerImageDown,posX-(width/2),posY-height,null);
			}
			else if (iMine<(int)(1+2*MINE_MAX/3)) g.drawImage(minerImage2,posX-(width/2),posY-height,null);
			else g.drawImage(minerImage1,posX-(width/2),posY-height,null);
		}else{
			if (iMine<(int)(1+MINE_MAX/3)){
				if (directionY == 1) g.drawImage(minerReversedImageUp,posX-(width/2),posY-height,null);
				else g.drawImage(minerReversedImageDown,posX-(width/2),posY-height,null);
			}
			else if (iMine<(int)(1+2*MINE_MAX/3)) g.drawImage(minerReversedImage2,posX-(width/2),posY-height,null);
			else g.drawImage(minerReversedImage1,posX-(width/2),posY-height,null);
		}
	}
	
	public void affectMap(){
		if (direction == 1){
			if (directionY == 1){
				for (int i = posX;i<=posX+width/2+radiusX;i++){
					for (int j = posY-stepHeight-radiusY;j<=posY-stepHeight;j++){
						if (w.canDestructPixel(i,j)){
							//изменить заминированное место, чтобы лемминги могли пройти
							w.setMapTypeAtPos(i,j,w.AIR_CST);
							w.setMapPixelColor(i,j,w.AIR_LIST.get(w.airIndex));
						}
					}
				}
			}
			else{
				for (int i = posX;i<=posX+width/2+radiusX;i++){
					for (int j = posY+stepHeight-radiusY;j<=posY+stepHeight;j++){
						if (w.canDestructPixel(i,j)){
							w.setMapTypeAtPos(i,j,w.AIR_CST);
							w.setMapPixelColor(i,j,w.AIR_LIST.get(w.airIndex));
						}
					}
				}
			}
		}
		else{
			if (directionY == 1){
				for (int i = posX;i>=posX-width/2-radiusX;i--){
					for (int j = posY-stepHeight-radiusY;j<=posY-stepHeight;j++){
						if (w.canDestructPixel(i,j)){
							w.setMapTypeAtPos(i,j,w.AIR_CST);
							w.setMapPixelColor(i,j,w.AIR_LIST.get(w.airIndex));
						}
					}
				}
			}
			else{
				for (int i = posX;i>=posX-width/2-radiusX;i--){
					for (int j = posY+stepHeight-radiusY;j<=posY+stepHeight;j++){
						if (w.canDestructPixel(i,j)){
							w.setMapTypeAtPos(i,j,w.AIR_CST);
							w.setMapPixelColor(i,j,w.AIR_LIST.get(w.airIndex));
						}
					}
				}
			}
		}
	}
	
	public void changeDirectionY(){
		directionY = -directionY;
	}

	
	public BufferedImage getImageRight(){
		return imageRightMiner;
	}
	public BufferedImage getImageRightStep(){
		return imageRightStepMiner;
	}
	public BufferedImage getImageLeft(){
		return imageLeftMiner;
	}
	public BufferedImage getImageLeftStep(){
		return imageLeftStepMiner;
	}
	
}
