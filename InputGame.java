import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class InputGame extends Input{

	private Toolkit tk = Toolkit.getDefaultToolkit();
	private static BufferedImage imageCurseurSelect;
	private static BufferedImage imageCurseurInit;
	private static BufferedImage imageCurseurSelectRed;
	private static BufferedImage imageCurseurInitRed;
	
	private int frameWidth;
	private int frameHeight;
	
	private Cursor cursorGame;
	
	private Cursor CurseurInit;
	private Cursor CurseurSelect;
	private Cursor CurseurInitRed;
	private Cursor CurseurSelectRed;
	
	private double ratioX = 1.0;
	private double ratioY = 1.0;
	
	private GameScene gs;
	
	public InputGame(Window w, GameScene gs){
		super(w);
		
		this.gs = gs;
		
		frameWidth = w.getCanvas().getWidth();
		System.out.println("frameWidth init : "+frameWidth);
		frameHeight = w.getCanvas().getHeight();
		System.out.println("frameHeight init : "+frameHeight);
		
		CurseurInit = tk.createCustomCursor( imageCurseurInit, new Point(imageCurseurInit.getWidth()/2,imageCurseurInit.getHeight()/2), "Pointeur" );
		CurseurSelect = tk.createCustomCursor( imageCurseurSelect, new Point(imageCurseurSelect.getWidth()/2,imageCurseurSelect.getHeight()/2), "Pointeur" );
		CurseurInitRed = tk.createCustomCursor( imageCurseurInitRed, new Point(imageCurseurInitRed.getWidth()/2,imageCurseurInitRed.getHeight()/2), "Pointeur" );
		CurseurSelectRed = tk.createCustomCursor( imageCurseurSelectRed, new Point(imageCurseurSelectRed.getWidth()/2,imageCurseurSelectRed.getHeight()/2), "Pointeur" );
		cursorGame = CurseurInit;
	}
	
	public static void loadAssets(){
		try{
			imageCurseurSelect = ImageIO.read(new File("cursor/cursorSelect.png"));
			imageCurseurInit = ImageIO.read(new File("cursor/cursorInit.png"));
			imageCurseurSelectRed = ImageIO.read(new File("cursor/cursorSelectRed.png"));
			imageCurseurInitRed = ImageIO.read(new File("cursor/cursorInitRed.png"));
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	public void setCursor(Cursor cursor){
		w.getCanvas().setCursor(cursor);
	}
	
	public Cursor getCursorGame(){
		return cursorGame;
	}
	
	public int getCapacityClicSetter(){
		return gs.getSkillBar().getCapacityClicSetter();
	}
	
	public void setCapacityClicSetter(int capacity){
		gs.getSkillBar().setCapacityClicSetter(capacity);
	}
	
	public boolean lemmingsInRange(Lemmings l){
		int lemmingsLimitYUpper = (int)(l.getPosY()-1.20*1.5*l.getHeight());
		int lemmingsLimitYLower = (int)(l.getPosY()+1.20*0.5*l.getHeight());
		int lemmingsLimitXUpper = (int)(l.getPosX()-1.20*l.getWidth());
		int lemmingsLimitXLower = (int)(l.getPosX()+1.20*l.getWidth());
		return (l.getInWorld() && lemmingsLimitYUpper<posYmouse  && lemmingsLimitYLower>posYmouse && lemmingsLimitXUpper<posXmouse  && lemmingsLimitXLower>posXmouse);
	}
	
	public void update(){
		boolean cursorOnLemmings = false;
		Lemmings l;
		for(int i=0;i<w.getCurrentWorld().getLemmingsList().length;i++){
			l = w.getCurrentWorld().getLemmingsList()[i];
			if (lemmingsInRange(l)){
				cursorOnLemmings = true;
			}
		}
		if (cursorOnLemmings){
        	if (getCapacityClicSetter()==-1) setCursor( CurseurSelect );
        	else setCursor( CurseurSelectRed );
        }
        else{
        	if (getCapacityClicSetter()==-1) setCursor( CurseurInit );
        	else setCursor( CurseurInitRed );
        }
	}
	
	public void draw(Graphics2D g){
		gs.getSkillBar().drawSelectZone(g,posXmouse,posYmouse);
		if (w.getCanvas().getWidth()!=frameWidth || w.getCanvas().getHeight()!=frameHeight){
			
			
			frameWidth = w.getCanvas().getWidth();
			frameHeight = w.getCanvas().getHeight();
			
			ratioX = (frameWidth*1.0)/gs.getWidth();
			ratioY = (frameHeight*1.0)/gs.getHeight();
			//обновить изображения, перезаписав их
			
			if(gs.getWidth() != frameWidth || gs.getHeight() != frameHeight){
				BufferedImage imageCurseurInitTemp = Window.resize(imageCurseurInit, (int)(imageCurseurInit.getWidth()*ratioX),(int)(imageCurseurInit.getHeight()*ratioY));
				BufferedImage imageCurseurSelectTemp = Window.resize(imageCurseurSelect, (int)(imageCurseurSelect.getWidth()*ratioX),(int)(imageCurseurSelect.getHeight()*ratioY));
				BufferedImage imageCurseurInitRedTemp = Window.resize(imageCurseurInitRed, (int)(imageCurseurInitRed.getWidth()*ratioX),(int)(imageCurseurInitRed.getHeight()*ratioY));
				BufferedImage imageCurseurSelectRedTemp = Window.resize(imageCurseurSelectRed, (int)(imageCurseurSelectRed.getWidth()*ratioX),(int)(imageCurseurSelectRed.getHeight()*ratioY));
				CurseurInit = tk.createCustomCursor( imageCurseurInitTemp, new Point(imageCurseurInitTemp.getWidth()/2,imageCurseurInitTemp.getHeight()/2), "Pointeur" );
				CurseurSelect = tk.createCustomCursor( imageCurseurSelectTemp, new Point(imageCurseurSelectTemp.getWidth()/2,imageCurseurSelectTemp.getHeight()/2), "Pointeur" );
				CurseurInitRed = tk.createCustomCursor( imageCurseurInitRedTemp, new Point(imageCurseurInitRedTemp.getWidth()/2,imageCurseurInitRedTemp.getHeight()/2), "Pointeur" );
				CurseurSelectRed = tk.createCustomCursor( imageCurseurSelectRedTemp, new Point(imageCurseurSelectRedTemp.getWidth()/2,imageCurseurSelectRedTemp.getHeight()/2), "Pointeur" );
				}
			else{
				CurseurInit = tk.createCustomCursor( imageCurseurInit, new Point(imageCurseurInit.getWidth()/2,imageCurseurInit.getHeight()/2), "Pointeur" );
				CurseurSelect = tk.createCustomCursor( imageCurseurSelect, new Point(imageCurseurSelect.getWidth()/2,imageCurseurSelect.getHeight()/2), "Pointeur" );
				CurseurInitRed = tk.createCustomCursor( imageCurseurInitRed, new Point(imageCurseurInitRed.getWidth()/2,imageCurseurInitRed.getHeight()/2), "Pointeur" );
				CurseurSelectRed = tk.createCustomCursor( imageCurseurSelectRed, new Point(imageCurseurSelectRed.getWidth()/2,imageCurseurSelectRed.getHeight()/2), "Pointeur" );
				}
			cursorGame = CurseurInit;
		}	
	}
	
	public boolean fastForwardPressed(World world, int posXclic, int posYclic){
		if (posXclic >=world.getWidth()-40 && posXclic <=world.getWidth()-10 && posYclic>=world.getHeight()+20 && posYclic<=world.getHeight()+50){
			if (gs.FPS == 60) w.changeGameSpeed(2);
			else if(gs.FPS == 120) w.changeGameSpeed(1);
			return true;
		}
		return false;
	}
	

        
        public void updateButtons(){
        	changeResetMapButton();
        	changeFastForwardButton();
        	changeArrowButton();
        }
        
        public void changeArrowButton(){
       		World world = w.getCurrentWorld();
        	if (posXmouse >= gs.getSkillBar().getArrowPosX() && posXmouse <= gs.getSkillBar().getArrowPosX()+gs.getSkillBar().getArrowWidth() && posYmouse >= world.getHeight()+gs.getSkillBar().getArrowPosY() && posYmouse <= world.getHeight()+gs.getSkillBar().getArrowPosY()+gs.getSkillBar().getArrowHeight()){
        		gs.getSkillBar().setArrowHovered(true);
        	}
        	else{
        		gs.getSkillBar().setArrowHovered(false);
        	}
        }
        public void changeResetMapButton(){
        	World world = w.getCurrentWorld();
        	if(posXmouse >= world.getWidth()-40 && posXmouse <=world.getWidth()-10 && posYmouse>=world.getHeight()+gs.getSkillBar().getCapacityWidth() && posYmouse <=world.getHeight()+90){
        		gs.getSkillBar().setResetMapButtonHovered(true);
        	}
        	else{
            		gs.getSkillBar().setResetMapButtonHovered(false);
        	}
        }
        
        public void changeFastForwardButton(){
        	World world = w.getCurrentWorld();
        	if(posXmouse >= world.getWidth()-40 && posXmouse <=world.getWidth()-10 && posYmouse>=world.getHeight()+20 && posYmouse <=world.getHeight()+50){
        		gs.getSkillBar().setFastForwardButtonHovered(true);
        	}
        	else{
            		gs.getSkillBar().setFastForwardButtonHovered(false);
        	}
        }
        
        public void mouseMoved(MouseEvent e){
        	posXmouse = (int)(e.getX()/ratioX);
        	posYmouse = (int)(e.getY()/ratioY);
        	updateButtons();
        }
        
        public boolean arrowPressed(World world, int posXclic, int posYclic){
        	if (gs.getSkillBar().getPosXCapacity(World.MINER)<0) return false;
        	if (posXmouse >= gs.getSkillBar().getArrowPosX() && posXmouse <= gs.getSkillBar().getArrowPosX()+gs.getSkillBar().getArrowWidth() && posYmouse >= world.getHeight()+gs.getSkillBar().getArrowPosY() && posYmouse <= world.getHeight()+gs.getSkillBar().getArrowPosY()+gs.getSkillBar().getArrowHeight()){
        		gs.getSkillBar().changeMinerDirection();
        		return true;
        	}
        	return false;
        }
        
        public void mouseClicked(MouseEvent e) {
	//Вызывается при щелчке мышью на компоненте.
		int posXclic = (int)(e.getX()/ratioX);
		int posYclic = (int)(e.getY()/ratioY);
		
		World world = w.getCurrentWorld();
		
		if(world == null) return;
		if (resetMapPressed(world, posXclic, posYclic)) return;
		if (fastForwardPressed(world, posXclic, posYclic)) return;
		if (arrowPressed(world, posXclic, posYclic)) return;
		
		for (int i=0;i<SkillBar.nbJobs;i++){
			if (gs.getSkillBar().getPosXCapacity(i)>=0){
				if (posXclic > gs.getSkillBar().getPosXCapacity(i) && posXclic < gs.getSkillBar().getPosXCapacity(i)+gs.getSkillBar().getCapacityWidth()
				&& posYclic > world.getHeight()+gs.getSkillBar().getPosYCapacity() && posYclic < world.getHeight()+gs.getSkillBar().getPosYCapacity()+gs.getSkillBar().getCapacityWidth()){
					setCapacityClicSetter(i);
					return;
				}
			}
		}
		
		int posXlem;
		int posYlem;	
		Lemmings l;
		for(int i=0;i<world.getLemmingsList().length;i++){
			l = world.getLemmingsList()[i];
			posXlem = l.getPosX();
			posYlem = l.getPosY();	
			//этот if должен быть таким же, как тот, который показывает, находится ли курсор на лемминге
        		if (lemmingsInRange(l)){
        			if ( World.WALKER != l.getJob() && e.getButton()==3 && l.getInWorld()){ 
        			//если метод getButton возвращает 3, это щелчок левой кнопкой мыши
        				world.changeJob(l,World.WALKER);
					System.out.println("turn into WALKER");
        				return;
        			}
        			else if (World.STOPPER != l.getJob() && getCapacityClicSetter() == World.STOPPER && l.getInWorld() && e.getButton()==1){
        			//если метод getButton возвращает 1, это щелчок левой кнопкой мыши
        				world.changeJob(l,World.STOPPER);
					System.out.println("turn into STOPPER");
					return;
        			}
        			else if ( getCapacityClicSetter() == World.BOMBER && l.getBombCountdown()==-1 && l.getInWorld()){
        				System.out.println("turn into BOMBER");
        				world.changeJob(l,World.BOMBER);
        				return; 
        				
        			}
        			else if (getCapacityClicSetter() == World.BUILDER && l.getInWorld() && e.getButton()==1){
        				world.changeJob(l,World.BUILDER);
					System.out.println("turn into Builder");
					return;
				}
				else if (World.BASHER != l.getJob() && getCapacityClicSetter() == World.BASHER && l.getInWorld() && e.getButton()==1){
        				world.changeJob(l,World.BASHER);
					System.out.println("turn into BASHER");
					return;
				}
				else if (World.MINER != l.getJob() && getCapacityClicSetter() == World.MINER && l.getInWorld() && e.getButton()==1){
        			world.setMinerDirection(gs.getSkillBar().getMinerDirection());
        			world.changeJob(l,World.MINER);
					System.out.println("turn into MINER");
					return;
				}else if (World.EXCAVATER != l.getJob() && getCapacityClicSetter() == World.EXCAVATER && l.getInWorld() && e.getButton()==1){
        				world.changeJob(l,World.EXCAVATER);
					System.out.println("turn into EXCAVATER");
					return;
				}	
        	}
        }
        if (posYmouse >= world.getHeight()){
        	setCapacityClicSetter(-1);
        }
	}
}
