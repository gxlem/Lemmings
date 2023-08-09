import java.awt.Color;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Basher extends Digger{
	/*этот класс является подклассом леммингов, его задача - разрушать стены на своем пути впереди*/
	

	
	//изображение Башера
	private static BufferedImage basherImage0;
	private static BufferedImage basherImage1;
	private static BufferedImage basherImage2;
	private static BufferedImage basherImage3;
	
	private static BufferedImage basherImage0reverse;
	private static BufferedImage basherImage1reverse;
	private static BufferedImage basherImage2reverse;
	private static BufferedImage basherImage3reverse;
	
	
	//основные изображения леммингов изменены на его цвет
	private static BufferedImage imageRightBasher;
	private static BufferedImage imageRightStepBasher;
	private static BufferedImage imageLeftBasher;
	private static BufferedImage imageLeftStepBasher;
	
	private int iBash;//счетчик для каждой анимации удара о стену, используемый, чтобы знать, когда изменить изображение и когда повлиять на карту.
	private static final int iBASH_MAX = 15;//константа, чтобы знать, в каком состоянии анимации мы находимся, чем ниже, тем быстрее будет анимация.
	
	private static final int bashWidth = 3;
	private static final int rangeBash = 4;
	


	
	public static void loadAssets(){
		try{
			basherImage0 = ImageIO.read(new File("lemmings/basher0.png"));
			basherImage1 = ImageIO.read(new File("lemmings/basher1.png"));
			basherImage2 = ImageIO.read(new File("lemmings/basher2.png"));
			basherImage3 = ImageIO.read(new File("lemmings/basher3.png"));
			
			basherImage0reverse = ImageIO.read(new File("lemmings/basher0reverse.png"));
			basherImage1reverse = ImageIO.read(new File("lemmings/basher1reverse.png"));
			basherImage2reverse = ImageIO.read(new File("lemmings/basher2reverse.png"));
			basherImage3reverse = ImageIO.read(new File("lemmings/basher3reverse.png"));
			
			imageRightBasher = ImageIO.read(new File("lemmings/lemmings1Basher.png"));
			imageRightStepBasher = ImageIO.read(new File("lemmings/lemmings1stepBasher.png"));
			imageLeftBasher = ImageIO.read(new File("lemmings/lemmings2Basher.png"));
			imageLeftStepBasher = ImageIO.read(new File("lemmings/lemmings2stepBasher.png"));
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	public Basher(Lemmings l){
		//Конструктор Башера, скопированный с бывшего лемминга
		super(l);//копии формируют признаки лемминга
		height = basherImage0.getHeight();//обновляет высоту
		width = basherImage0.getWidth();//обновляет ширину
	}



	public void move(){
		//метод move, описывающий, как двигается Башер
		
		if (!inWorld) return;//если Башер еще не бьет стену
		if(!action){//проверьте, может ли он упасть или двигаться вперед
			if (fall()) return;										
			if (goAhead()) return;
			this.job = World.BASHER;
			this.action = true;//если он может начать бить стену, начните свою работу.
			iBash = iBASH_MAX;
		}
		else{
			if (fall()){//если он бьет стену, проверьте, может ли он упасть
				System.out.println(toString()+" is falling.");
				w.changeJob(this,World.WALKER);//если он может, поменяй на Уокера
				return;
			}
			if (iBash == 0){//иначе, если он достиг конца своей анимации, переместите его на bashWidth пикселей в его направлении
				posX+=bashWidth*direction;//и начать новую анимацию
				iBash = iBASH_MAX;
				if (goAhead()){//если он не может ходить, а это значит, что его работа сделана
					w.changeJob(this,World.WALKER);
					return;
				}
			}
			else affectMap();//во время анимации вызывается effectMap, чтобы бить стены
			iBash--;//обновить счетчик анимации
			
		}
		
	
	}
	
	public boolean goAhead(){
		//Метод goAhead говорит, может ли Башер двигаться вперед (если может, значит, его работа выполнена), а если может, то он будет.
		
		if (super.walk()){//если он может двигаться вперед
			if (action) System.out.println(toString()+" is walking.");
			return true;
		}
		else if (super.climbUp()){//или он может подняться
			if (action) System.out.println(toString()+" is climbing up.");
			return true;
		}
		else if (super.climbDown()){//или он может спуститься
			if (action) System.out.println(toString()+" is climbing down.");
			return true;
		}
		else if (super.checkForStopperWall()){//или препятствием является Стопорная стена (которую он не должен считать препятствием)
			direction = -direction;//затем он может двигаться вперед (или менять направление, если это Стопорная стена)
			if (action) System.out.println(toString()+" is changing direction due to stopper wall");
			return true;
		}
		else if (!w.onBounds(posX+direction*(imageRight.getWidth()/2),posY)){	//или он достиг мировых пределов
			direction = -direction;
			System.out.println(toString()+" is changing direction due to map limits");
			return true;
		}
		return false;
	}
	
	public void drawAction(Graphics2D g){
		//Метод drawAction описывает, как Башер рисуется во время своей работы.
	
		if (direction == 1){
			if (iBash<=(int)(iBASH_MAX/(4*1.0))) g.drawImage(basherImage2,posX-(width/2),posY-height,null);//Этап 3
			else if (iBash<=(int)(2*iBASH_MAX/(4*1.0))) g.drawImage(basherImage1,posX-(width/2),posY-height,null);//Этап 2
			else if (iBash<=(int)(3*iBASH_MAX/(4*1.0))) g.drawImage(basherImage0,posX-(width/2),posY-height,null);//Этап 1
			else g.drawImage(basherImage3,posX-(width/2),posY-height,null);// Этап 4
		}else{
			if (iBash<=(int)(iBASH_MAX/(4*1.0))) g.drawImage(basherImage2reverse,posX-(width/2),posY-height,null);
			else if (iBash<=(int)(2*iBASH_MAX/(4*1.0))) g.drawImage(basherImage1reverse,posX-(width/2),posY-height,null);
			else if (iBash<=(int)(3*iBASH_MAX/(4*1.0))) g.drawImage(basherImage0reverse,posX-(width/2),posY-height,null);
			else g.drawImage(basherImage3reverse,posX-(width/2),posY-height,null);
		}
	}
	
	
	public void affectMap(){
		//Метод effectMap копает землю
		int diggYend = height;//копать конец позиции Y
		int diggYstart = 0;//копать начало позиции Y
		int diggX = bashWidth;
		
		if (iBash == (int)(iBASH_MAX/(4*1.0))){//если 3 стадия
			diggYend = (int)(1+height/3);
		}
		else if (iBash == (int)(2*iBASH_MAX/(4*1.0))){//если 2 стадия
			diggYstart = (int)(1+height/3);  
			diggYend = (int)(2*height/3);
			diggX = (3*bashWidth)/2;
		}
		else if (iBash == (int)(3*iBASH_MAX/(4*1.0))){//если стадия 1
			diggYstart = (int)(2*height/3);
		}
		else return;//4 этап ничего не делает
		for (int i=rangeBash;i<diggX+rangeBash;i++){//применение модификаций мира
			for (int j = diggYstart; j<=diggYend;j++){
				w.setMapTypeAtPos(posX+direction*i,posY-j,w.AIR_CST);
				w.setMapPixelColor(posX+direction*i,posY-j,w.AIR_LIST.get(w.airIndex));
			}
		}
	}
	
	public void resetMap(){} //Башер не может удалить примененные им модификации
	
	public BufferedImage getImageRight(){
		return imageRightBasher;
	}
	public BufferedImage getImageRightStep(){
		return imageRightStepBasher;
	}
	public BufferedImage getImageLeft(){
		return imageLeftBasher;
	}
	public BufferedImage getImageLeftStep(){
		return imageLeftStepBasher;
	}
	
}
