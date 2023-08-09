import java.util.ArrayList;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public abstract class Lemmings extends Thing{//Класс Лемминги (будет абстрактным)

	

	protected int id;//идентификатор лемминга
	public static World w;
	protected int direction;//направление движения: -1 если влево, +1 если вправо
	public static int nbLemmings = 0;//статический счетчик леммингов
	protected int job;
	protected boolean action;//какое действие: 0, если нет более позднего класса Action
	protected boolean alive;//мертвый или живой
	protected boolean spawned;
	protected boolean inWorld;//если он вышел на поле
	protected boolean outside;//если ему удалось выбраться
	protected int iDeath = 0;
	protected boolean bombDeath = false;
	protected int height;//Размер изображения стандартного лемминга
	protected int width;//Ширина изображения стандартного лемминга
	protected boolean inAir = false;//Логическое значение, чтобы узнать, падают ли лемминги
	protected int iWalk = 0;//итерация, которая запускает движущуюся анимацию
	protected int iFall = 0;
	protected static int maxFall;
	
	protected static BufferedImage imageRight;
	protected static BufferedImage imageRightStep;
	protected static BufferedImage imageLeft;
	protected static BufferedImage imageLeftStep;
	
	


	
	
	protected static BufferedImage deathFirst;
	protected static BufferedImage deathSecond;
		
	protected int bombCountdown=-1;
	protected static BufferedImage boom1;
	protected static BufferedImage boom2;
	protected static BufferedImage boom3;
	protected static BufferedImage boom4;
	protected static BufferedImage boom5;
	protected static BufferedImage imageExplosion;
	public static final int bombRadius = 25;
	


	public Lemmings(int posX, int posY){
		super(posX,posY);
		nbLemmings++;//Увеличение общего количества леммингов
		this.id = nbLemmings;
		direction = 1;//изначально они двигаются вправо
		id = nbLemmings;
		outside = false;//логическое значение, чтобы узнать, принял ли он изначально false
		inWorld = false;//изначально лемминга нет в мире до спавна
		alive = true;
		action = false;//Класс действия
		spawned = false;
		job = World.WALKER;
		
		this.width = imageRight.getWidth();//получаем ширину и высоту лемминга
		this.height = imageRight.getHeight();
		maxFall = 10*height;
		
	}	
	
	public static void loadAssets(){
		try{
			
			imageRight = ImageIO.read(new File("lemmings/lemmings1.png"));//восстановить изображения Ходоков в разных состояниях
			imageRightStep = ImageIO.read(new File("lemmings/lemmings1step.png"));
			imageLeft = ImageIO.read(new File("lemmings/lemmings2.png"));
			imageLeftStep = ImageIO.read(new File("lemmings/lemmings2step.png"));
			
			
			imageExplosion = ImageIO.read(new File("lemmings/explosion.png"));
			
			
			deathFirst = ImageIO.read(new File("lemmings/death1.png"));
			deathSecond = ImageIO.read(new File("lemmings/death2.png"));
			
			boom1 = ImageIO.read(new File("lemmings/bombEffect1.png"));
			boom2 = ImageIO.read(new File("lemmings/bombEffect2.png"));
			boom3 = ImageIO.read(new File("lemmings/bombEffect3.png"));
			boom4 = ImageIO.read(new File("lemmings/bombEffect4.png"));
			boom5 = ImageIO.read(new File("lemmings/bombEffect5.png"));
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	public Lemmings(Lemmings l){
		this(l.posX,l.posY);
		this.id = l.id;
		iFall = l.iFall;
		direction = l.direction;
		inWorld = l.inWorld;
		iDeath = l.iDeath;
		inAir = l.inAir;
		iWalk = l.iWalk;
		alive = l.alive;
		spawned = l.spawned;
		bombCountdown = l.bombCountdown;		
	}

	
	public abstract void move();

	public String toString(){
		return "Lemmings number "+id;
	}
	
	public void draw(Graphics2D g){
		if(drawDeath(g)) return;
		if(!inWorld) return;
		if(!alive) return;
		drawMove(g);
		if (action) drawAction(g);
		drawBomb(g);
	}
	
	public abstract void drawAction(Graphics2D g);
	
	public void drawBomb(Graphics2D g){
		if(bombCountdown>0){
			g.setColor(Color.white);
			g.setFont(new Font("default", Font.BOLD, 14));
			if (Window.getTps()-bombCountdown<60){
				g.drawString(""+5,posX-width/2,posY-2*height);
				g.drawImage(boom1, posX-imageRight.getWidth(),posY-imageRight.getHeight(),null);
			}
			else if (Window.getTps()-bombCountdown<120){
				g.drawString(""+4,posX-width/2,posY-2*height);
				g.drawImage(boom2, posX-imageRight.getWidth(),posY-imageRight.getHeight(),null);
			}
			else if (Window.getTps()-bombCountdown<180){
				g.drawString(""+3,posX-width/2,posY-2*height);
				g.drawImage(boom3, posX-imageRight.getWidth(),posY-imageRight.getHeight(),null);
			}
			else if (Window.getTps()-bombCountdown<240){
				g.drawString(""+2,posX-width/2,posY-2*height);
				g.drawImage(boom4, posX-imageRight.getWidth(),posY-imageRight.getHeight(),null);
			}
			else if (Window.getTps()-bombCountdown<300){
				g.drawString(""+1,posX-width/2,posY-2*height);
				g.drawImage(boom5, posX-imageRight.getWidth(),posY-imageRight.getHeight(),null);
			}
		}
	}
	
	public void drawMove(Graphics2D g){
		if(action) return;
		if (direction == 1){
			if((Window.getTps()-iWalk)%10 > 5 && !inAir){		
				g.drawImage(getImageRightStep(),posX-imageRightStep.getWidth()/2,posY-imageRightStep.getHeight(),null);
			}
			else g.drawImage(getImageRight(),posX-imageRight.getWidth()/2,posY-imageRightStep.getHeight(),null);
		}
		else {
			if((Window.getTps()-iWalk)%10 > 5 && !inAir){
				g.drawImage(getImageLeftStep(),posX-imageLeftStep.getWidth()/2,posY-imageRightStep.getHeight(),null);
			}
			else g.drawImage(getImageLeft(),posX-imageLeft.getWidth()/2,posY-imageRightStep.getHeight(),null);
		}
	}
	
	public boolean drawDeath(Graphics2D g){
	
		if (iDeath != 0){
			if (bombDeath){
				g.drawImage(imageExplosion,posX-imageExplosion.getWidth()/2,posY-imageExplosion.getHeight()/2,null);
			}
			else{
				//if(getClass().toString().contains("class Stopper")) System.out.println("death");
				if (iDeath >= 20) g.drawImage(deathFirst,posX-imageRight.getWidth()/2,posY-imageRight.getHeight(),null);
				else g.drawImage(deathSecond,posX-imageRight.getWidth()/2,posY-imageRight.getHeight(),null);
				
			}
			iDeath--;
			return true;
		}
		return false;
	}
	
	public void update(){
		if (!alive) return;
		bomb();
		move();
	}
	
	public boolean fall(){
		
		for (int i=0;i<(imageRight.getWidth()/2);i++){
			if(w.getPos(posX-i,posY+1)!=World.AIR_CST || w.getPos(posX+i,posY+1)!=World.AIR_CST){
				if (iFall<maxFall && posY+2<=w.getHeight()) {
					iFall = 0;
				}
				else kill();
				inAir = false;
				return false;
			}
		}
		
		posY++;
		inAir = true;
		iWalk = Window.getTps();
		iFall++;
		action = false;
		return true;	
	}
	
	
	public boolean walk(){
	//Функция, которая заставляет леммингов ходить
		for (int i =0;i<(height);i++){
			if(w.getPos(posX+direction*(imageRight.getWidth()/2),posY-i)!=0 
			&& w.getPos(posX+direction*(imageRight.getWidth()/2),posY-i)!=direction+3
			&& w.getPos(posX+direction*(imageRight.getWidth()/2),posY-i)!=direction+4){//Убедитесь, что для ходьбы проходит вся высота тела.
				return false;
			}
		}
		posX+=direction;
		return true;//заранее, потому что нет препятствий
	}
	
	public boolean climbDown(){
	//Функция, которая пытается спустить лемминга
		int i;
		for (i=1;i<imageRight.getHeight()/2;i++){
			if(w.getPos(posX+direction*(imageRight.getWidth()/2),posY+i)==0 && w.getPos(posX+direction*(imageRight.getWidth()/2)+(direction*3*(imageRight.getWidth()/2)),i+posY-imageRight.getHeight()+1)==0){	
				for (int j =1;j<i;j++){
					if(w.getPos(posX+direction*(imageRight.getWidth()/2),posY-i)!=0){
					//Проверяем, нет ли возвышенного препятствия, иначе возвращаем false
						return false;
					}
				}
			//if проверяет, может ли спуститься юнит и что будет, когда он спустится
				posX+=direction;
				posY+=i;
				return true;
			}
		}
		
		return false;
		
	}
	
	public boolean climbUp(){
		int i,j;
		for (i =(imageRight.getHeight()/2);i<imageRight.getHeight();i++){
			if(w.getPos(posX+direction*(imageRight.getWidth()/2),posY-i)!=0 && w.getPos(posX+direction*(imageRight.getWidth()/2),posY-i)!=direction+3){
			//Проверяем, нет ли возвышенности, иначе возвращаем false
				return false;
			}
		}
		for (i =(imageRight.getHeight())/2+1;i>=0;i--){
			if(w.getPos(posX+direction*(imageRight.getWidth()/2),posY-i)!=0 && w.getPos(posX+direction*(imageRight.getWidth()/2),posY-i)!=direction+3){
			//Смотрим на размер ступеньки и поднимаемся на нее
				for (j =i+1;j<i+imageRight.getHeight();j++){
					if(w.getPos(posX+direction*(imageRight.getWidth()/2),posY-j)!=0 && w.getPos(posX+direction*(imageRight.getWidth()/2),posY-j)!=direction+3){
					//Проверяем, нет ли возвышенности, иначе возвращаем false
						return false;
					}
				}
				posX+=direction;
				posY-=i+1;
				return true;
			}
		}
		return false;
		
	}
	
	
	public boolean bomb(){
		if(bombCountdown == -1) return false;
		int time = Window.getTps()-bombCountdown;
		if(time>300 && inWorld){
			System.out.println("direction : "+direction);
			for (int i = posX-bombRadius;i<posX+bombRadius;i++){
				for (int j = posY-bombRadius;j<posY+bombRadius;j++){
					if (w.getPos(i,j)>=1 
					&& w.getPos(i,j)!=3 
					&& w.getPos(i,j)!=5
					&& (i-posX)*(i-posX)+(j-posY)*(j-posY)<=bombRadius*bombRadius){
						w.setMapTypeAtPos(i,j,w.AIR_CST);
						w.setMapPixelColor(i,j,w.AIR_LIST.get(w.airIndex));
					}
				}
			}
			if (this instanceof Affecter) ((Affecter)this).resetMap();
			bombDeath = true;
			kill();
			bombCountdown =-1;
		}
		return true;
	}
	
	public void startBomb(){
		bombCountdown = Window.getTps();
	}
	
	public void spawn(){
		inWorld = true;
		spawned = true;
	}
	
	public void win(){
		if (this instanceof Affecter){
			((Affecter)this).resetMap();
		}
		inWorld = false;
		
	}
	
	public void kill(){
		
		alive = false;
		inWorld = false;
		iDeath = 40;
		if (this instanceof Affecter){
			((Affecter)this).resetMap();
		}
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getDirection(){
		return direction;
	}
	
	public void setPosX(int posX){
		this.posX = posX;	
	}
	
	public void setPosY(int posY){
		this.posY = posY;
	}
	
	public boolean getInWorld(){
		return inWorld;
	}
	
	public int getJob(){
		return job;
	}
	
	public int getId(){
		return id;
	}
	
	public boolean getAlive(){
		return alive;
	}
	
	public boolean getSpawned(){
		return spawned;
	}
	
	public long getBombCountdown(){
		return bombCountdown;
	}
	
	public abstract BufferedImage getImageRight();
	public abstract BufferedImage getImageRightStep();
	public abstract BufferedImage getImageLeft();
	public abstract BufferedImage getImageLeftStep();
	
	
}
