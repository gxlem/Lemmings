import java.util.ArrayList;
/*
==================== MAIN =================
*/
public class Game{
	
	public static void main(String[] args){
		
		Window UI = new Window("Lemmings",600,400);//Создание интерфейса для окон
		Screen currentScreen = UI.getCurrentScreen();
		long lastTime = System.nanoTime();
		double delta = 0;
		int updates = 0;
		int frames = 0;
		long timer = System.currentTimeMillis();
		while(true){
			long now = System.nanoTime();
			delta += (now - lastTime)/currentScreen.ns;
			lastTime = now;
			if(delta>=1){
				UI.update();
				updates++;
				UI.iterateTps();
				delta--;
			}
			UI.draw();
			frames++;
			
			
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				System.out.println("ticks : "+updates+" | FPS : "+frames);
				updates = 0;
				frames = 0;
			}
		}

	}
}
