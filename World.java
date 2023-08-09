import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.awt.Color;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;

public class World implements Renderable{ //класс, представляющий текущий мир в главном окне
	

	
	private int[][] map;//цветная карта мира, которая будет обновляться в самом начале
	private int height;//высота карты
	private int width;//ширина
	private int id;	
	private Lemmings[] list;//идентификатор
	private Item[] itemList;
	private BufferedImage mapImage;//Изображение в формате .png карты для загрузки
	
	public static final ArrayList<Color> AIR_LIST = new ArrayList<Color>();//список констант
	public static final int INVALID_POS_CST = -1;
	public static final int AIR_CST = 0;
	public static final int GROUND_CST = 1;
	public static final int WALL_RIGHT_CST = 4;
	public static final int WALL_LEFT_CST = 2;
	public static final int STOPPER_WALL_RIGHT_CST = 5;
	public static final int STOPPER_WALL_LEFT_CST = 3;
	public int airIndex;
	public static final int settingsLines = 19;
	private Spawner spawn;
	private Outside end;
	private int spawnX;
	private int spawnY;
	private SpitFire spitFire;
	private int spitFireX;
	private int spitFireY;
	private int outsideX;
	private int outsideY;
	private int outsideType;
	
	private int stopperLimit;
	private int bomberLimit;
	private int builderLimit;
	private int basherLimit;
	private int minerLimit;
	private int excavaterLimit;
	
	private int minerDirection = 1;
	
	
	
	private boolean finished = false;
	private boolean started = false;
	private int victoryCondition;
	
	public static final int WALKER = -1;
	public static final int STOPPER = 4;
	public static final int BOMBER = 0;
	public static final int BUILDER = 3;
	public static final int BASHER = 1;
	public static final int MINER = 2;
	public static final int EXCAVATER = 5;
	
	Stats stats;
	

	
	public World(int id){
		this.id = id;
		loadWorld();
	}
	

	
	public void loadWorld(){
		try{
			mapImage = ImageIO.read(new File("world/world"+id+".png")); //читаем изображение карты и сохраняем его по id
		}catch(Exception e){e.printStackTrace();}
		
		this.width = mapImage.getWidth();
		this.height = mapImage.getHeight();
		fillMap();
		initAirCst();
		setSettings();
		
		for (int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				Color mapXY = getColor(i,j,mapImage);
  				if(AIR_LIST.contains(mapXY)){
  					map[i][j] = AIR_CST;
  				}
  				else {
  					map[i][j] = GROUND_CST;
  				}
			}
		}
		stats = new Stats(this);
	}
	
	public void startWorld(){
		for (Item i:itemList){
			i.startItem();
		}
		started = true;
	}
	
	public void setSettings(){
		BufferedReader br = null;
		FileReader fr = null;
		int[] settings = new int[settingsLines];
		try{
			fr = new FileReader("world/world"+id+"settings.txt");
			br = new BufferedReader(fr);
			String currentLine;
			for (int i=0;i<settingsLines;i++){
				currentLine = br.readLine();
				settings[i] = Integer.parseInt(currentLine);
			}
			spawnX = settings[0];
			spawnY = settings[1];
			spawn = new Spawner(spawnX,spawnY,settings[5]);
			outsideType = settings[2];
			outsideX = settings[3];
			outsideY = settings[4];
			loadLemmings(settings[6]);
			end = new Outside(outsideX,outsideY,list,this,outsideType);
			airIndex = settings[7];
			victoryCondition = settings[8];
			stopperLimit = settings[9];
			bomberLimit = settings[10];
			builderLimit = settings[11];
			basherLimit = settings[12];
			minerLimit = settings[13];
			excavaterLimit = settings[14];
			
			if( settings[15]>=1){
				spitFireX = settings[17];
				spitFireY = settings[18];
				spitFire = new SpitFire(spitFireX, spitFireY, this, settings[16]);
				
				itemList = new Item[3];
				itemList[0] = spawn;
				itemList[1] = end;
				itemList[2] = spitFire;
			}
			else{
				itemList = new Item[2];
				itemList[0] = spawn;
				itemList[1] = end;
			}

			
			
			
		}catch (IOException e){e.printStackTrace();}
		finally{

			try{
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			}catch (IOException e2) {e2.printStackTrace();}
		}
	}
	
	public void loadLemmings(int nb){
		Lemmings.w = this;
		list = new Lemmings[nb];
		for (int i=0;i<nb;i++){
			addLemmings(i,new Walker(spawnX,spawnY));
			
		}
		/*for (int j=0;j<nb;j++){
			if ((j%2)==1) {
				int id = list[j].id;
				Lemmings[] tab = new Lemmings[1];
				//end.removeLemmingFromList(id);
				list[j] = list[j].changeJob(STOPPER);
				tab[0] = list[j];
				//end.addLemmings(tab);
			}
		}*/
		
	}
	
	/*public void son(){
		try{
			Media song = new Media(new File("world/LemmingsMusic.mp3").toURI().toString());
			MediaPlayer mediaPlayer = new MediaPlayer(song);
			mediaPlayer.play();
		}catch(Exception e){e.printStackTrace();}
		
	}*/
	
	public void initAirCst(){
	//инициализируйте константы, чтобы иметь больше вариантов выбора
		AIR_LIST.add(new Color(97,172,191));
		AIR_LIST.add(new Color(0,0,0));
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getID(){
		return id;
	}
	
	public void fillMap(){
		map = new int[width][height];
		for (int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				map[i][j] = 0;
			}
		}
	}
	
	public int getPos(int posX, int posY){
	//Функция, которая в зависимости от x и y возвращает 1, если цвет поля x,y считается площадью
		if (onBounds(posX,posY)) return map[posX][posY];
		return INVALID_POS_CST;
  		
	}
	
	public void setMapTypeAtPos(int posX, int posY, int TYPE_CST){
	//эта функция используется ниже
		map[posX][posY] = TYPE_CST;
	}
	
	public void setMapPixelColor(int posX, int posY, Color c){
	//эта функция будет использоваться для изменения пикселя
		mapImage.setRGB(posX,posY,c.getRGB());
	}
	
	public void replaceGroundWithBackground(int posX, int posY){
	//эта функция будет использоваться для уничтожения земли
		setMapPixelColor(posX,posY,AIR_LIST.get((int)(Math.random()*(AIR_LIST.size()+1))));
	}
	
	public Color getColor(int posX, int posY, BufferedImage image){
	//возвращает цвет карты в posX, posY
		int color;					
		int red;
		int green;
		int blue;
		color =  image.getRGB(posX,posY);
		red   = (color & 0x00ff0000) >> 16;
		green = (color & 0x0000ff00) >> 8;
		blue  =  color & 0x000000ff;
		return new Color(red,green,blue);
	}
	
	
	public boolean addObjectToWorld(int posX, int posY, int type_CST, BufferedImage image, int direction){
		if (posX>=width || posX<0 || posY<0 || posY >=height || posX+image.getWidth()>=width || posY+image.getHeight()>=height) return false;
		if (direction == 1){
			for(int i = posX;i<posX+image.getWidth();i++){
				for(int j = posY;j<posY+image.getHeight();j++){
					if (getPos(i,j)==1) return false;
					else setMapTypeAtPos(i,j,type_CST);
					setMapPixelColor(i,j,getColor(i-posX,j-posY,image));
				}
			}
		}
		else{
			for(int i = posX+image.getWidth()-1;i>=posX;i--){
				for(int j = posY+image.getHeight()-1;j>=posY;j--){
					if (getPos(i,j)==1) return false;
					else setMapTypeAtPos(i,j,type_CST);
					setMapPixelColor(i,j,getColor(i-posX,j-posY,image));
				}
			}
		}
		return true;
		
	}
	
	public void draw(Graphics2D g){
		g.drawImage(mapImage,0,0,null);
		
	}

	public int getSpawnX(){
		return spawnX;	
	}
	
	public int getSpawnY(){
		return spawnY;	
	}
	public int getOutsideX(){
		return outsideX;	
	}
	public int getOutsideY(){
		return outsideY;	
	}
	
	public void spawnLemmings(){
		spawn.addLemmings(list);
	}
	
	public void spiteFireLemmingsFilling(){
		spitFire.addLemmings(list);
	}
	
	public Lemmings[] getLemmingsList(){
		return list;
	}

	public Spawner getSpawner(){
		return spawn;
	}
	
	public SpitFire getSpitFire(){
		return spitFire;
	}
	
	public Outside getOutside(){
		return end;
	}
	
	public boolean getFinished(){
		return finished;
	}
	
	public boolean getStarted(){
		return started;
	}
	
	public void setMinerDirection(int directionY){
		minerDirection = directionY;
	}
	
	public int getVictoryCondition(){
		return victoryCondition;
	}
	public void setFinished(boolean finished){
		this.finished = finished;
	}
	
	public Stats getStats(){
		return stats;
	}
	
	public String getLemmingsJob(int state){
		if (state == BOMBER) return "Bomber";
		else if (state == BUILDER) return "Builder";
		else if (state == BASHER) return "Basher";
		else if (state == STOPPER) return "Stopper";
		else if (state == MINER) return "Miner";
		else if (state == EXCAVATER) return "Excavater";
		else return "Walker";
	}
	
	public int getLemmingsLimit(int state){
		if (state == BOMBER) return bomberLimit;
		else if (state == BUILDER) return builderLimit;
		else if (state == BASHER) return basherLimit;
		else if (state == STOPPER) return stopperLimit;
		else if (state == MINER) return minerLimit;
		else if (state == EXCAVATER) return excavaterLimit;
		else return -20;
	}
	
	public boolean canDestructPixel(int posX, int posY){
		if (!onBounds(posX,posY)) return false;
		if (getPos(posX,posY)==INVALID_POS_CST || getPos(posX,posY)==3 || getPos(posX,posY)==5) return false;
		return true;
	}
	
	public boolean onBounds(int posX, int posY){
		if (posX<width && posX>=0 && posY<height && posY >=0) return true;
		return false;
	}
	

	
	public void addLemmings(int i,Lemmings l){
		list[i] = l;
		sortLemmings();
	}
	
	public void sortLemmings(){
		if (list[list.length-1] == null) return;
		int index = 0;
		for (int i=0;i<list.length;i++){
			Lemmings l = list[i];
			if (list[index].getJob()>l.getJob()){
				reverseLemmings(i,index);
				System.out.println("reverse "+i+" and "+index);
				index = i;
			}
		}
	}
	
	public void reverseLemmings(int i, int j){
		Lemmings lTemp = list[j];
		list[j] = list[i];
		list[i] = lTemp;
	}
	
	public void replaceLemmings(Lemmings l, Lemmings l2){
		for (int i=0;i<list.length;i++){
			if(l.getId()==list[i].getId()){
				addLemmings(i,l2);
				return;
			}		
		}
	}
	
	public void printList(){
		for (int i=0;i<list.length;i++){
			if(list[i]!=null){
				System.out.println("i : "+i+" | "+list[i].toString());
			}
		}
	}
	
	
	public void changeJob(Lemmings l,int state){
		Lemmings newLemming = null;
		if(state == WALKER){
			newLemming = new Walker(l);
			System.out.println("changeJob to WALKER");
		}
		
		else if(state == STOPPER){
			if(stopperLimit>0){
				stopperLimit--;
				newLemming = new Stopper(l);
				System.out.println("changeJob to STOPPER");
			}
			
		}
		else if(state == BOMBER){
			if(bomberLimit>0){
				bomberLimit--;
				l.startBomb();
			}
			return;
			
		}
		else if(state == BUILDER){
			if(builderLimit>0){
				builderLimit--;
				newLemming = new Builder(l);
				System.out.println("changeJob to BUILDER");
			}
		}
		else if(state == BASHER){ 
			if(basherLimit>0){
				basherLimit--;
				newLemming = new Basher(l);
				System.out.println("changeJob to BASHER");
			}
		}
		else if(state == MINER){ 
			if(minerLimit>0){
				minerLimit--;
				newLemming = new Miner(l,minerDirection);
				System.out.println("changeJob to MINER");
			}
		}
		else if(state == EXCAVATER){ 
			if(excavaterLimit>0){
				excavaterLimit--;
				newLemming = new Excavater(l);
				System.out.println("changeJob to EXCAVATER");
			}
		}
		else{
			System.out.println("Error: job not created.");
		}
		if(l instanceof Affecter){
			System.out.println(l.toString()+" | reset the map");
			((Affecter)l).resetMap();
		}
		int index = INVALID_POS_CST;
		for(int i=0;i<list.length;i++){
			if (list[i].getId()==l.getId()){
				index = i;
				break;
			}
		}
		if((index == INVALID_POS_CST) || (newLemming==null)){
			System.out.println("Out of lemmings of this type");
			return;
		}
		replaceLemmings(list[index],newLemming);
        	Lemmings[] tab = new Lemmings[1];
		tab[0] = newLemming;
		spawn.removeLemmingFromList(l.getId());
		spawn.addLemmings(tab);
		end.removeLemmingFromList(l.getId());
		end.addLemmings(tab);
	}
	
}	









