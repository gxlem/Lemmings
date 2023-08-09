import java.util.ArrayList;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Builder extends Lemmings implements Affecter{
	/*этот класс является подклассом леммингов, его задача состоит в том, чтобы строить лестницы на своем пути вперед до лестницы nbSteps*/
	
	//builder images
	private static BufferedImage builderImage0;
	private static BufferedImage builderImage1;
	private static BufferedImage builderImage2;
	private static BufferedImage builderImage3;
	private static BufferedImage builderImageReverse0;
	private static BufferedImage builderImageReverse1;
	private static BufferedImage builderImageReverse2;
	private static BufferedImage builderImageReverse3;
	
	//изображение строителя
	private static BufferedImage builderWait0;
	private static BufferedImage builderWait1;
	private static BufferedImage builderWait2;
	private static BufferedImage builderWait3;
	
	private static BufferedImage buildStep;//картинка лестницы
	private boolean changeJobBool = false;//логическое значение, чтобы узнать, должен ли он перейти на Уокера
	private boolean changedDirection = false;//boolean, чтобы знать, должен ли он изменить направление, в котором он строит
	
	private int nbSteps = 20;//счетчик количества лестниц, которые он строит
	
	private static final int BUILD_MAX = 40;//константа, чтобы знать, в каком состоянии анимации мы находимся, чем ниже, тем быстрее будет анимация.
	private int iBuild;//анимированное состояние
	
	private static final int WAIT_MAX = 150;//то же самое для анимации ожидания ожидания
	private int iWait;
	private String nbStepsString = ""+nbSteps;//количество оставшихся ступеней String отображается в верхней части конструктора

	
	public static void loadAssets(){
		//загрузка ресурсов Builder
		try{
			builderImage0 = ImageIO.read(new File("lemmings/builder0.png"));
			builderImage1 = ImageIO.read(new File("lemmings/builder1.png"));
			builderImage2 = ImageIO.read(new File("lemmings/builder2.png"));
			builderImage3 = ImageIO.read(new File("lemmings/builder3.png"));
			
			builderImageReverse0 = ImageIO.read(new File("lemmings/builderReverse0.png"));
			builderImageReverse1 = ImageIO.read(new File("lemmings/builderReverse1.png"));
			builderImageReverse2 = ImageIO.read(new File("lemmings/builderReverse2.png"));
			builderImageReverse3 = ImageIO.read(new File("lemmings/builderReverse3.png"));
			
			builderWait0 = ImageIO.read(new File("lemmings/builderWait0.png"));
			builderWait1 = ImageIO.read(new File("lemmings/builderWait1.png"));
			builderWait2 = ImageIO.read(new File("lemmings/builderWait2.png"));
			builderWait3 = ImageIO.read(new File("lemmings/builderWait3.png"));
			
			buildStep = ImageIO.read(new File("lemmings/buildstep2.png"));
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	public Builder(Lemmings l){
		super(l);
		height = builderImage0.getHeight();
		width = builderImage0.getWidth();
	}

	
	public void move(){
		//метод move, описывающий, как Builder перемещается
		if (!action){//если Строитель еще не приступил к работе
			if (!inWorld) return;
			if (fall()) return;
			int posXTemp = searchStairsMiddlePosition();
			if (posXTemp != World.INVALID_POS_CST){
				posX = posXTemp;
				System.out.println("adjusted position on stairs");
			}
			else System.out.println("no stairs");
			
			this.action = true;//тогда он может начать свою работу
			this.job = World.BUILDER;
			iBuild = BUILD_MAX;
			iWait = WAIT_MAX;
		}
		else{//если он строит лестницу
			if (fall()){//и он может упасть тогда он остановит свою работу
				w.changeJob(this,w.WALKER);
				return;
			}
			if(nbSteps==0){//если он достиг nbSteps лестницы, он также останавливает свою работу
				if(iWait>0) iWait--;//после того, как его анимация ожидания завершена
				else w.changeJob(this,w.WALKER);
				return;
			}
			
			if (!haveEnoughPlaceAbove()){//если он находится под потолком, он останавливает свою работу, чтобы не застрять.
				w.changeJob(this,w.WALKER);
				return;
			}
			if(iBuild == 0){//если его анимация строительства сделана,
				affectMap();//он ставит лестницу
				if (changeJobBool){	//и останавливает свою работу, если он может подняться.
					w.changeJob(this,w.WALKER);
					return;
				}
				nbSteps--;//количество оставшихся ступенек обновлено
				nbStepsString = ""+nbSteps;//строки обновлены
				if (!changedDirection){//если необходимо изменить направление из-за препятствия, он делает это
					posX+=direction*buildStep.getWidth();//заменив себя на лестнице под теми, которые он только что поставил
					posY-=buildStep.getHeight();
				}
				else changedDirection = false;
				
				iBuild = BUILD_MAX;//перезапустите анимацию, когда закончит
			}
			else iBuild--;//обновление анимации
		}
		
	}
	
	public void drawAction(Graphics2D g){
		//Метод drawAction описывает способ анимации Строителя во время его работы.
		
		//рисование анимации ожидания в конце задания
		if (nbSteps==0){
			if(iWait>12*WAIT_MAX/13) g.drawImage(builderWait0,posX-(width/2),posY-height,null);
			else if (iWait>11*WAIT_MAX/13) g.drawImage(builderWait1,posX-(width/2),posY-height,null);
			else if (iWait>10*WAIT_MAX/13) g.drawImage(builderWait2,posX-(width/2),posY-height,null);
			else if (iWait>9*WAIT_MAX/13) g.drawImage(builderWait3,posX-(width/2),posY-height,null);
			else if (iWait>8*WAIT_MAX/13) g.drawImage(builderWait2,posX-(width/2),posY-height,null);
			else if (iWait>7*WAIT_MAX/13) g.drawImage(builderWait1,posX-(width/2),posY-height,null);
			else if (iWait>6*WAIT_MAX/13) g.drawImage(builderWait0,posX-(width/2),posY-height,null);
			else if (iWait>5*WAIT_MAX/13) g.drawImage(builderWait1,posX-(width/2),posY-height,null);
			else if (iWait>4*WAIT_MAX/13) g.drawImage(builderWait2,posX-(width/2),posY-height,null);
			else if (iWait>3*WAIT_MAX/13) g.drawImage(builderWait3,posX-(width/2),posY-height,null);
			else if (iWait>2*WAIT_MAX/13) g.drawImage(builderWait2,posX-(width/2),posY-height,null);
			else if (iWait>WAIT_MAX/13) g.drawImage(builderWait1,posX-(width/2),posY-height,null);
			else g.drawImage(builderWait0,posX-(width/2),posY-height,null);
			return;
		}
		//мультипликационная анимация здания
		if (direction == 1){
			if (iBuild<BUILD_MAX/4) g.drawImage(builderImage3,posX-(width/2),posY-height,null);
			else if (iBuild<2*BUILD_MAX/4) g.drawImage(builderImage2,posX-(width/2),posY-height,null);
			else if (iBuild<3*BUILD_MAX/4) g.drawImage(builderImage1,posX-(width/2),posY-height,null);
			else g.drawImage(builderImage0,posX-(width/2),posY-height,null);
		}
		else{
			if (iBuild<BUILD_MAX/4) g.drawImage(builderImageReverse3,posX-(width/2),posY-height,null);
			else if (iBuild<2*BUILD_MAX/4) g.drawImage(builderImageReverse2,posX-(width/2),posY-height,null);
			else if (iBuild<3*BUILD_MAX/4) g.drawImage(builderImageReverse1,posX-(width/2),posY-height,null);
			else g.drawImage(builderImageReverse0,posX-(width/2),posY-height,null);
		}
		//чертеж количества оставшихся ступеней
		g.setColor(Color.white);
		g.setFont(new Font("default", Font.BOLD, 12));
		if (direction == 1) g.drawString(nbStepsString,posX,posY-height);
		else g.drawString(nbStepsString,posX-width/2,posY-height);
		
		
	}
	
	public void affectMap(){
		//Метод effectMap для построения лестницы
		int type_CST;//выбрать тип лестницы
		if (direction==1) type_CST = w.WALL_LEFT_CST;//влево или вправо в зависимости от направления
		else type_CST = w.WALL_RIGHT_CST;
		
		if (w.getPos(posX+direction*buildStep.getWidth(),posY-buildStep.getHeight())!= World.AIR_CST	//если он строит на противоположной лестнице
			&& w.getPos(posX+direction*buildStep.getWidth(),posY-buildStep.getHeight())!= type_CST){	//тогда лестница в этом месте наземная
			type_CST = w.GROUND_CST;
		}
		int startX;//исходное место для рисования лестницы
		if (direction == 1) startX = posX+buildStep.getWidth()/2;//в зависимости от направления
		else startX = posX-3*buildStep.getWidth()/2;
		if (!w.addObjectToWorld(startX,posY-buildStep.getHeight(), type_CST, buildStep, direction)){//если лестница была загорожена стеной
			int newPosX = checkLastValidPosX();//получить ближайшую позицию к этой стене
			if (newPosX !=World.INVALID_POS_CST){//и проверьте, является ли это действительной позицией
				int temPosX = posX;//переместить его туда
				int temPosY = posY;
				posX = newPosX;
				posY -= buildStep.getHeight();
				//w.setMapPixelColor(posX,posY,Color.yellow);
				if (super.climbUp()){//и попробуйте подняться
					changeJobBool = true;//что приведет к смене работы
					return;
				}
				else{//если он не может взобраться на него
					posX = temPosX;//вернуть предыдущее положение лестницы
					posY = temPosY;
					direction = -direction;
					changedDirection = true;
				}
			}
			else changeJobBool = true;
			
		}
		return;
	}
	
	public int checkLastValidPosX(){
		//Метод checkLastValidPosX ищет ближайшую позицию к стене в направлении, в котором смотрит Строитель.
		if (direction == 1){
			for (int i = posX;i<=posX+2*buildStep.getWidth();i++){//в 2 раза больше ширины лестницы
				if(w.getPos(i,posY) == World.GROUND_CST){
					return i-1;//i-1, чтобы получить предыдущую позицию
				}
			}
		}
		else{
			for (int i = posX;i>=posX-2*buildStep.getWidth();i--){
				if(w.getPos(i,posY) == World.GROUND_CST){
					return i-1;
				}
			}
		}
		return World.INVALID_POS_CST;//если нет разумного ближайшего положения к стене
	}
	
	public int searchStairsMiddlePosition(){
		//Метод searchStairsMiddlePosition возвращает хорошую позицию для начала строительства лестницы.
		int posLeftStairs = World.INVALID_POS_CST;//левое и правое положение лестницы
		int posRightStairs = World.INVALID_POS_CST;//инициализирован недействительным
		for (int i = posX-buildStep.getWidth(); i<=posX+buildStep.getWidth();i++){//проверить вокруг posX
			if (w.getPos(i,posY+1) == World.WALL_LEFT_CST || w.getPos(i,posY+1) == World.WALL_RIGHT_CST){//для левого и правого края лестницы
				if (posLeftStairs == World.INVALID_POS_CST) posLeftStairs = i;//и получить их
				else posRightStairs = i;
			}
		}
		if (posLeftStairs == World.INVALID_POS_CST || posRightStairs == World.INVALID_POS_CST) return World.INVALID_POS_CST;//если границы не найдены, вернуть недействительный
		//w.setMapPixelColor(posLeftStairs,posY+1,Color.yellow);
		//w.setMapPixelColor(posRightStairs,posY+1,Color.yellow);
		return (posLeftStairs+posRightStairs)/2 +1;//иначе вернуть среднее положение
	}
	
	public void resetMap(){}//Строитель не может удалить изменения, которые он применил
	
	public boolean haveEnoughPlaceAbove(){
		//Метод haveEnoughPlaceAbove проверяет, нет ли у Строителя потолка, преграждающего ему путь во время строительства.
		int i = posX;
		for (int j = posY-height;j>posY-height-buildStep.getHeight();j--){
			//w.setMapPixelColor(i,j,Color.red);
			if (w.getPos(i,j) == World.INVALID_POS_CST || w.getPos(i,j) == World.GROUND_CST) return false;//проверяет, вне мира ли это стена
		}
		return true;
	}
	
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
