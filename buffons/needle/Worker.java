package buffons.needle;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;


public class Worker extends Thread{
	
	private int repeat;
	private double a,l,realL;
	private int hit,current;
	private Graphics g;
	private Graphics2D graph_g;
	private volatile boolean done=false;
	private volatile boolean stop=true;
	private boolean animate;
	private JTextField pi,m,n;
	private double precizion;
	private double currentPrecizion=10;
	private Point last_dot;
	private JTextField error_panel;
	private int dx;
	private int dx_graph;
	private RandomGenerator rg=new RandomGenerator();
	private double formula()
	{	
		if (hit==0) return 0;
		else return (double)2*realL*current/((double)hit*a);
	}
	private double formula1()
	{	
		if (hit==0) return 0;
		else return (double)2*current/(double)hit;//(double)2*hit/(double)current;
	}
	private JPanel animation,graph;
	//private int xPixel(double x, double y) { return (int) (x*animation.getWidth()); }
	//private int yPixel(double x, double y) { return (int) ((ymax-y)/(ymax-ymin)*getHeight()); }
	public Worker(int rpt,double aa,double ll,JPanel an,boolean animat,JTextField jtx,JPanel graphPanel,JTextField mm,JTextField nn,double precizi,JTextField error_Pane,int dxx)
	{
		dx=dxx;
		error_panel=error_Pane;
		last_dot=new Point(0,0);
		precizion=precizi;
		pi=jtx;
		a=aa;
		m=mm;
		n=nn;
		realL=l=ll;
		graph=graphPanel;
		animate=animat;
		repeat=rpt;
		//graph=gr;
		animation=an;
		g=animation.getGraphics();
		graph_g=(Graphics2D) graph.getGraphics();
		for (int xLine=0;xLine<=animation.getWidth();xLine+=dx)
			g.drawLine(xLine,0, xLine, an.getHeight());
		//g.drawLine(2*an.getWidth()/3,0, 2*an.getWidth()/3, an.getHeight());
		
		graph_g.drawLine(0, 0, 0, graph.getHeight()-1);
		graph_g.drawLine(0, graph.getHeight()-1, graph.getWidth()-1,graph.getHeight()-1);
		graph_g.translate(0.0,graph.getHeight());
		graph_g.scale(1.0,-1.0);
		graph_g.setColor(Color.RED);
		graph_g.drawLine(0, (int)(graph.getHeight()*1/2), graph.getWidth(), (int)(graph.getHeight()*1/2));
		graph_g.setColor(Color.BLACK);
		l=l*an.getWidth()/(a*Math.floor((animation.getWidth()/dx)));
	}
	private boolean between(int first,int second, int between)
	{
		if (first>=between && second<=between || first<=between && second>=between) return true;
		return false;
	}
	public void calculateAndPaint(int xxx,int yyy)
	{
		//System.out.println("USAO U CALCULATE");
		int x,y;
		if (xxx==-1 && yyy==-1)
		{
		double xx=rg.getNumber(),yy=rg.getNumber();

		//int x=(int) (animation.getWidth()*0.2+xx*(animation.getWidth()*0.6)),y=(int) (animation.getHeight()*0.2+yy*(animation.getHeight()*0.6));
		//int x=(int) (xx*(animation.getWidth()+dx/2)-dx/2),y=(int) (yy*animation.getHeight());
		x=(int) (xx*animation.getWidth());
		y=(int) (yy*animation.getHeight());
		}
		else
		{x=xxx; y=yyy;}
		double angle=rg.getNumber()*2*Math.PI;
		int x1=(int) (x+Math.cos(angle)*l),y1=(int) (y+Math.sin(angle)*l);
		//if (y>=animation.getHeight() || y<=0|| y1>=animation.getHeight() ||y1<=0 ||  x>=animation.getWidth() ||x<=0 || x1>=animation.getWidth()|| x1<=0) return;
		if (y>animation.getHeight()-1 && y1>animation.getHeight()-1 || y<0 && y1<0 || x>animation.getWidth()-1 && x1>animation.getWidth()-1 || x<0 && x1<0) return;
		current++; 
		//if (between(x,x1,(int)(animation.getWidth())/3) || between(x,x1,(int)(2*animation.getWidth())/3) || between(x,x1,0) || between(x,x1,animation.getWidth()))
		
		
		for (int xLine=0;xLine<=animation.getWidth();xLine+=dx)
			if (between(x,x1,xLine)) {hit++;g.setColor(Color.RED);}
		
		
		if (animate)
		{
		
			g.drawLine(x, y, x1, y1);
			g.setColor(Color.BLACK);
			
		}
		m.setText(hit+"");
		n.setText(current+"");
		double num=formula();
		
		System.out.println(num+"");
		
		currentPrecizion=Math.abs(Math.PI-num);
		error_panel.setText(currentPrecizion+"");
		
		Point newDot=new Point(current*graph.getWidth()/repeat,(int) ((int)((num*graph.getHeight())/(2*Math.PI))));
		System.out.println(newDot.x+" "+newDot.y);
		graph_g.drawLine(last_dot.x,last_dot.y,newDot.x,newDot.y);
		last_dot=newDot;
		newDot=null;
		pi.setText(num+"");
	}
	
	public void run()
	{
		try{
			//System.out.println("USAO U RUN");
		for (int i=0;  !interrupted();i++)
		{
			if (precizion!=-1 && currentPrecizion<=precizion) break;
			
			if (i>=repeat)
				if (precizion==-1) break;
			
			synchronized (this) {
				//System.out.println("USAO U THIS");
				while (!stop) {System.out.println("USAO U WAIT");wait();}
			}
			calculateAndPaint(-1,-1);
			//sleep(500);
		}
		}catch (InterruptedException ie){}
		done=false;
		//System.out.println("Izasao iz RUNa");
	}
	public synchronized void pause()
	{
		stop=false;
	}
	public synchronized void finish()
	{
		interrupt();
		//System.out.println("Poslao Interrupt");
		done=true;
		notifyAll();

	}
	public synchronized boolean done()
	{
		return done;
	}
	public synchronized void goOn()
	{
		stop=true;
		notifyAll();
	}
}
