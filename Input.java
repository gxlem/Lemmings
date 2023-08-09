import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public abstract class Input implements MouseListener,MouseMotionListener,Updatable, Renderable{
	
	protected Window w;
	protected int posXmouse = 0;
	protected int posYmouse = 0;
	
	protected Cursor cursor;
	
	public Input(Window w){
		this.w = w;
		cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	}
	
	public Cursor getCursor(){
		return cursor;
	}
	
	public static void loadAssets(){}
	
	public abstract void update();
	
	public abstract void draw(Graphics2D g); 
	
	public abstract void mouseClicked(MouseEvent e); 
	
	public boolean resetMapPressed(World world, int posXclic, int posYclic){
		if (posXclic >=world.getWidth()-40 && posXclic <=world.getWidth()-10 && posYclic>=world.getHeight()+60 && posYclic<=world.getHeight()+90){
			w.resetMap();
			return true;
		}
		return false;
	}
	
	public abstract void updateButtons();
	
	public void mouseMoved(MouseEvent e) {
        //каждый ход возвращает событие
        	posXmouse = e.getX();
        	posYmouse = e.getY();
        	updateButtons();
   	}
	
	public void mousePressed(MouseEvent e) {
	//Вызывается при нажатии кнопки мышки на компоненте
	}


	public void mouseReleased(MouseEvent e) {
	//Вызывается при отпускании кнопки мышки на компоненте
	}
	
	public void mouseEntered(MouseEvent e) {
	//Вызывается, когда мышь использует компонент
	}
	
	public void mouseExited(MouseEvent e) {
	//Вызывается, когда мышка не использует компонент
	}

    	public void mouseDragged(MouseEvent e) {}
	
}
