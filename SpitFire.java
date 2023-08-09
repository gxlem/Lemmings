import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class SpitFire extends Item{

	private static BufferedImage imageSpitFire;
	
	private static BufferedImage imageFire1;
	private static BufferedImage imageFire2;
	private static BufferedImage imageFire3;
	private static BufferedImage imageFire4;
	
	private static final int SPEEDFIRE = 30;
	
	private int fireRangeX;
	private int fireRangeY;
	private int iFire = 0;
	private int posXfire;
	private int posYfire;
	private World world;
	
	private int spitFireRhythm;
	
	private static int height;
	private static int width; 
	
	public static void loadAssets(){
		System.out.println("##############################################");
		try{
			imageSpitFire = ImageIO.read(new File("world/spitFire0.png"));;
			imageFire1 = ImageIO.read(new File("world/spitFire1.png"));
			imageFire2 = ImageIO.read(new File("world/spitFire2.png"));
			imageFire3 = ImageIO.read(new File("world/spitFire3.png"));
			imageFire4 = ImageIO.read(new File("world/spitFire4.png"));
			
		}catch(Exception e){e.printStackTrace();}
		//fixInMap();
	}
	
	public SpitFire(int posX, int posY, World world, int rhythm){
		super(posX,posY);
		this.world = world;
		spitFireRhythm = rhythm;
	}
	
	public SpitFire(int posX, int posY, World world){
		this(posX,posY,world,3);
	}
	
	public void startItem(){
		
		width = imageSpitFire.getWidth();
		height = imageSpitFire.getHeight();
		
		world.addObjectToWorld(posX-width/2, posY-height, world.GROUND_CST, imageSpitFire, 1);
		fireRangeX = imageFire1.getWidth();
		fireRangeY = imageFire1.getHeight();
		posXfire = posX-(fireRangeX/2)-(width/2);
		posYfire = posY-(height/2)+(fireRangeY/2);
	}
	
	public void fixInMap(){

	}
	
	public void update(){
		//указать размер диапазона + убить тех, кто внутри
		if (iFire<=0) iFire = spitFireRhythm*SPEEDFIRE;
		if (iFire<SPEEDFIRE) burning();
        	iFire--;
	}
	
	public void burning(){
		//убить тех, кто находится в зоне действия огня
		Lemmings[] listLemmings;
		Lemmings l;
		listLemmings = world.getLemmingsList();
        	for (int i=0;i<listLemmings.length;i++){
        		l=listLemmings[i];
			if (l.getPosX() >=  posXfire-fireRangeX/2
				&& l.getPosX() <= posXfire+fireRangeX/2
				&& l.getPosY() >= posYfire-fireRangeY
				&& l.getPosY() <= posYfire){
				if (l.alive) l.kill();
				//повысить эффективность
			}
		}
	}

	public void draw(Graphics2D g){
		if (iFire<(int)(SPEEDFIRE/4)) g.drawImage(imageFire4, posXfire-(fireRangeX/2), posYfire-(fireRangeY),null);
		else if (iFire<(int)(2*SPEEDFIRE/4)) g.drawImage(imageFire3, posXfire-(fireRangeX/2), posYfire-(fireRangeY),null);
		else if (iFire<(int)(3*SPEEDFIRE/4)) g.drawImage(imageFire2, posXfire-(fireRangeX/2), posYfire-(fireRangeY),null);
		else if (iFire<SPEEDFIRE) g.drawImage(imageFire1, posXfire-(fireRangeX/2), posYfire-(fireRangeY),null);                  
	}

	

}
