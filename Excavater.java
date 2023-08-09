import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Excavater extends Digger{
	/*этот класс является подклассом леммингов, его задача - разрушать препятствия на своем пути*/
	
	

	//анимация разрушения
	private static BufferedImage excavaterImage0;
	private static BufferedImage excavaterImage1;
	private static BufferedImage excavaterImage2;
	private static BufferedImage excavaterImage3;   
	
	//основные изображения леммингов изменены на его цвет
	private static BufferedImage imageRightExcavater;
	private static BufferedImage imageRightStepExcavater;
	private static BufferedImage imageLeftExcavater;
	private static BufferedImage imageLeftStepExcavater;
	     
	private int iExca;//счетчик для каждой анимации рытья земли, используемый, чтобы знать, когда изменить изображение и когда повлиять на карту.
	private static final int iExca_MAX = 40;//константа, чтобы знать, в каком состоянии анимации мы находимся, чем ниже, тем быстрее будет анимация.
	private static final int DIGG_DEEP = 2;	//глубина рытья за итерацию
	



	public static void loadAssets(){
		//Загрузка анимации рытья
		try{
			excavaterImage0 = ImageIO.read(new File("lemmings/excavater0.png"));
			excavaterImage1 = ImageIO.read(new File("lemmings/excavater1.png"));
			excavaterImage2 = ImageIO.read(new File("lemmings/excavater2.png"));
			excavaterImage3 = ImageIO.read(new File("lemmings/excavater3.png"));
			
			imageRightExcavater = ImageIO.read(new File("lemmings/lemmings1Excavater.png"));
			imageRightStepExcavater = ImageIO.read(new File("lemmings/lemmings1stepExcavater.png"));
			imageLeftExcavater = ImageIO.read(new File("lemmings/lemmings2Excavater.png"));
			imageLeftStepExcavater = ImageIO.read(new File("lemmings/lemmings2stepExcavater.png"));
		}catch(Exception e){e.printStackTrace();}
	}
	
	public Excavater(Lemmings l){
		super(l);
		height = excavaterImage0.getHeight();
		width = excavaterImage0.getWidth();
	}




	public void move(){
		//метод move, описывающий, как движется экскаватор
		if (!inWorld) return;
		if(!action){//раскопки еще не начаты
			if (fall()) return;
			this.job = World.EXCAVATER;//затем он начинает свою работу
			this.action = true;
		}else{//если он копает землю
			if (fall()){
				w.changeJob(this,World.WALKER);
				return;
			}
			if (iExca == 0){//во время своей работы он копает землю, когда его анимация еще не закончена
				affectMap();
				iExca = iExca_MAX;//и его анимация перезапускается
			}
			else if( iExca == (int)(iExca_MAX/4)) affectMap();//он также копает 3/4 анимации
			iExca--;//обновляет свой счетчик анимаций
			
		}
		
	
	}
	
	public void drawAction(Graphics2D g){
		//Метод drawAction описывает способ анимации рытья во время его работы.
		if (iExca<(int)(iExca_MAX/4)) g.drawImage(excavaterImage3,posX-(width/2),posY-height,null);//этап 4
		else if (iExca<(int)(2*iExca_MAX/4)) g.drawImage(excavaterImage2,posX-(width/2),posY-height,null);//этап 3
		else if (iExca<(int)(3*iExca_MAX/4)) g.drawImage(excavaterImage1,posX-(width/2),posY-height,null);//этап 2
		else g.drawImage(excavaterImage0,posX-(width/2),posY-height,null);//этап 1
	}
	
	
	public void affectMap(){
		//Метод effectMap копает землю.
		for (int i=-width/2;i<=width/2;i++){
			for (int j = 1; j<= DIGG_DEEP ;j++){
				if (w.getPos(posX+i,posY+j)==World.INVALID_POS_CST
					|| w.getPos(posX+i,posY+j)==World.STOPPER_WALL_LEFT_CST//или экскаватор копает стопорную стенку
					|| w.getPos(posX+i,posY+j)==World.STOPPER_WALL_RIGHT_CST){
					w.changeJob(this,World.WALKER);//он останавливает свою работу
					return;
				}
				w.setMapTypeAtPos(posX+i,posY+j,w.AIR_CST);//иначе он копает ниже себя
				w.setMapPixelColor(posX+i,posY+j,w.AIR_LIST.get(w.airIndex));
			}
		}
		posY = posY+DIGG_DEEP;
	}
	
	public void resetMap(){}//Экскаватор не может восстановить свои действия

	public BufferedImage getImageRight(){
		return imageRightExcavater;
	}
	public BufferedImage getImageRightStep(){
		return imageRightStepExcavater;
	}
	public BufferedImage getImageLeft(){
		return imageLeftExcavater;
	}
	public BufferedImage getImageLeftStep(){
		return imageLeftStepExcavater;
	}
	
}
