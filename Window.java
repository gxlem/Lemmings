import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;


public class Window implements Updatable{

	private JFrame frame;
	private Canvas canvas;
	private BufferStrategy bs;
	
	private static int tps = 0;//счетчик времени
	
	private static World world;
	
	private MainMenu mainMenu;
	private Score score;
	private GameScene gameScene;
	private LoadingScreen loading;
	
	private Screen currentScreen;
	
	private String title;
	private int width, height;
	
	private int verticalInsets;
	private int horizontalInsets;
	
	public Window(String title, int width, int height){
		this.title = title;
		this.width = width;
		this.height = height;
		createWindow();
	}
	
	private void createWindow(){
		frame = new JFrame(title);
		canvas = new Canvas();
		resizeFrame(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);
		frame.add(canvas);
		
		canvas.createBufferStrategy(3);
		bs = canvas.getBufferStrategy();
		
		mainMenu = new MainMenu(this,MainMenu.RES_WIDTH,MainMenu.RES_HEIGHT);
		
		setCurrentScreen(mainMenu);
		verticalInsets = frame.getInsets().top + frame.getInsets().bottom; //граница окна сверху и снизу в зависимости от ОС
		horizontalInsets = frame.getInsets().right + frame.getInsets().left;
		System.out.println("Window top insets : "+verticalInsets);
		System.out.println("Window side insets : "+horizontalInsets);
		
	}

	public void setCurrentScreen(Screen screen){
		if(currentScreen !=null){
			canvas.removeMouseListener(currentScreen.getInput());
			canvas.removeMouseMotionListener(currentScreen.getInput());
		}
		currentScreen = screen;
		canvas.addMouseListener(currentScreen.getInput());
		canvas.addMouseMotionListener(currentScreen.getInput());
		canvas.setCursor(currentScreen.getInput().getCursor());
	}
	
	public JFrame getFrame(){
		return frame;
	}
	
	public Canvas getCanvas(){
		return canvas;
	}
	
	public void resizeFrame(int newWidth, int newHeight){
		frame.setSize(newWidth,newHeight);
	}
	
	public Screen getCurrentScreen(){
		return currentScreen;
	}
	


	public static int getTps(){
	//возвращает итерацию нашего времени
		return tps;			
	}
	
	public static void iterateTps(){
	//повторить время
		tps++;				
	}
	public void setWorld(World w){
	//Изменяет текущий мир
		

		this.world = w;	
	}
	
	public static World getCurrentWorld(){
		return world;
	}
	
	public void changeGameSpeed(int speed){
		if (speed == 0) return;
		currentScreen.FPS = currentScreen.defaultFPS*speed;
		currentScreen.ns = currentScreen.secondInNano/currentScreen.FPS;
		System.out.println("Changed speed to " + currentScreen.FPS);
	}
	
	public void update(){
	//обновить мир
		if(world == null) return;
		if(!world.getStarted()) return;
		if(world.getFinished()) return;
		
		world.getSpawner().update();

		boolean allDead = true;
		if(world.getLemmingsList().length <= 0) allDead = false;
		
		Lemmings l;
		for(int i=0;i<world.getLemmingsList().length;i++){
			l = world.getLemmingsList()[i];
        		l.update();
        		if (l.getAlive()) allDead = false;
        		
        	}
        	
        	currentScreen.getInput().update();
        	
        	if (world.getSpitFire() != null) world.getSpitFire().update();
        	
        	world.getOutside().update();
        	
        	world.getStats().update();
        	
        	if(allDead){
        		world.setFinished(true);

        		
        	}
	}
	
	public void draw(){
		Graphics2D g = null;
		do{
   			try{
   				g = (Graphics2D)bs.getDrawGraphics();
   				currentScreen.draw(g);
    			}
    			finally{
           			g.dispose(); //завершает использование инструмента рисования
    			}
    			bs.show(); //обновляет окно чертежа новыми
		} while (bs.contentsLost()); //пока обновление окна не завершено, начните заново
		Toolkit.getDefaultToolkit().sync(); //заставить окно обновляться, даже когда мышь не используется
	}
	
	public void newCurrentWorld(int worldID){
		moveToLoadingScreen();
		world = null;
		World w = new World(worldID);
		setWorld(w);
		w.spawnLemmings();

		
	}
	
	public void moveToLoadingScreen(){
		loading = new LoadingScreen(this,LoadingScreen.RES_WIDTH,LoadingScreen.RES_HEIGHT);
		(new Thread(loading)).start();
		setCurrentScreen(loading);
		resizeFrame(LoadingScreen.RES_WIDTH+horizontalInsets,LoadingScreen.RES_HEIGHT+verticalInsets);
		frame.setLocationRelativeTo(null);
	}
	
	public void moveToGameScene(){
		resizeFrame(world.getWidth()+horizontalInsets,world.getHeight()+GameScene.UIheight+verticalInsets);
		canvas.setSize(world.getWidth(),world.getHeight()+GameScene.UIheight);
		gameScene = new GameScene(this,world.getWidth(),world.getHeight()+GameScene.UIheight);
		setCurrentScreen(gameScene);
		world.startWorld();
		System.out.println("size frame X : "+ frame.getWidth());
		System.out.println("size frame Y : "+ frame.getHeight());
		System.out.println("size canvas X : "+ canvas.getWidth());
		System.out.println("size canvas Y : "+ canvas.getHeight());
		frame.setLocationRelativeTo(null);
	}
	
	public void moveToMainMenu(){
		setCurrentScreen(mainMenu);
		resizeFrame(MainMenu.RES_WIDTH+horizontalInsets,MainMenu.RES_HEIGHT+verticalInsets);
		frame.setLocationRelativeTo(null);
	}
	
	public void moveToScoreScreen(){
		score = new Score(this,world.getVictoryCondition(),Score.RES_WIDTH,Score.RES_HEIGHT);
		changeGameSpeed(1);
		setCurrentScreen(score);
		resizeFrame(Score.RES_WIDTH+horizontalInsets,Score.RES_HEIGHT+verticalInsets);
		frame.setLocationRelativeTo(null);
	}
	
	public void resetMap(){
		int newID = world.getID();
		world = null;
		World w = new World(newID);
		setWorld(w);
		
		w.spawnLemmings();
		moveToGameScene();
	}
	
	public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
   		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
    		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

    		Graphics2D g2d = dimg.createGraphics();
    		g2d.drawImage(tmp, 0, 0, null);
    		g2d.dispose();

    		return dimg;
}
	
	
}
