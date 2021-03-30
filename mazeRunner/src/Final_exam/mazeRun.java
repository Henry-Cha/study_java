package Final_exam;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;	
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;


public class mazeRun extends JFrame {

	Thread clock;
	private static int N=15;
	private static int[][] maze = new int[N][N];
	boolean[][] map;
	int runGame;                                    //0=탈출시작전, 1=탈출시작, 2=탈출종료
	int runnerX,runnerY;
	String P1,P2;
	double timer;
	ImageIcon title;
	boolean hide;
	
	private static int WAY = 0;
	private static int WALL = 1;
	private static int BLOCK = 2;
	private static int PATH = 3;
	
	JButton makeButton, checkButton, hideButton, startButton,reButton;
	JPanel titlePanel, buttonPanel, resultPanel;
	JTextField nameField1,nameField2;
	JLabel msg,timeLabel,titleLabel;
	JMenuBar menuBar;
	JMenu helpMenu;
	JMenuItem aboutGame;
	MyPanel mypanel;
	ActionListener alistener1;
	MouseListener mlistener1;
	KeyListener klistener1;
	
	mazeRun()
	{		
		JFrame f = new JFrame("미로탈출게임");
		f.setLayout(new BorderLayout());  	

		titlePanel = new JPanel();
		buttonPanel = new JPanel();
		title = new ImageIcon("title.gif");
		nameField1 = new JTextField("제작자 이름",4);
		checkButton = new JButton("미로 완성");
		hideButton = new JButton("맵가리기");
		nameField2 = new JTextField("탈출자 이름",4);
		startButton = new JButton("탈출 시작");		
		msg = new JLabel("마우스 클릭으로 벽을 그리세요.");
		timeLabel = new JLabel();
		reButton = new JButton("다시하기");
		 titleLabel = new JLabel(title);
		makeButton = new JButton("미로 만들기");	
	    menuBar= new JMenuBar();
	    helpMenu= new JMenu("Help");
	    aboutGame= new JMenuItem("About");
	    
	    msg.setForeground(Color.red);
	    
        f.setJMenuBar(menuBar);
        menuBar.add(helpMenu);	    
        aboutGame.setActionCommand("aboutGame");
        aboutGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog jd = new JDialog(f, "About");
				JLabel jdl = new JLabel("made by sungwon");
				jd.add(jdl);
				jd.setSize(100,100);
				jd.setVisible(true);
				
			}});
        
        helpMenu.add(aboutGame);
        
	    makeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				titlePanel.removeAll();
				mypanel = new MyPanel(); 
				
				f.add("Center",mypanel);
				msg.setVisible(true);

				f.revalidate();
				f.repaint();
			}} );
	    
	    titlePanel.setLayout(null);
	    makeButton.setIcon(new ImageIcon("button.gif"));
	    makeButton.setBounds(290, 300, 140, 50);
	    titlePanel.add(makeButton);		
	    titleLabel.setBounds(0,0,730,730);
	    titlePanel.add(titleLabel); 
		buttonPanel.add(msg);
		buttonPanel.add(nameField1);
		buttonPanel.add(checkButton);
		buttonPanel.add(hideButton);
		buttonPanel.add(nameField2);
		buttonPanel.add(startButton);
		buttonPanel.add(timeLabel);							    
		buttonPanel.add(reButton);
				
		msg.setVisible(false);
		checkButton.setEnabled(false);
		startButton.setEnabled(false);
		reButton.setVisible(false);
		hideButton.setEnabled(false);
		
		f.add("Center", titlePanel);
		f.add("South", buttonPanel);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(722, 809);
		f.setVisible(true);
	}
	
	public static void main(String[] args) {
		new mazeRun();
	}
	
	class MyPanel extends JPanel  {
		BufferedImage off;
		BufferedImage runner,door;
		Graphics offG;
		
		MyPanel()
		{
			mlistener1 = new myMouseListener(); 
			
			init();
			
			checkButton.addActionListener(new ActionListener() {        //미로완성 클릭 시 탈출 검사
				@Override
				public void actionPerformed(ActionEvent e) {
					for(int i=0; i<N; i++){
		                for(int j=0; j<N; j++){
			                if(map[i][j]){
			                	maze[i][j] = WALL;
			                }else{
			                	maze[i][j] = WAY;
			                }
		                }
		            }
					findMazePath(0,0);
					if(maze[N-1][N-1]==3) {
						System.out.println("탈출 가능한 미로");                     //탈출가능해야 시작가능
						hideButton.setEnabled(true);
						msg.setText("맵가리기 클릭시 수정 불가");
					}
					else {
						msg.setText("탈출 불가능한 미로!!");
						System.out.println("탈출 불가능한 미로");
					}
				}} );
			
			hideButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					startButton.setEnabled(true);
					checkButton.setEnabled(false);
					hideButton.setEnabled(false);
					removeMouseListener(mlistener1);                               //맵가리면 맵변경 불가능
					hideMap();	
					repaint();
				}} );
			
			startButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					runGame = 1;
					P1=nameField1.getText();
					P2=nameField2.getText();
					hideButton.setEnabled(false);
					checkButton.setEnabled(false);
					startButton.setEnabled(false);
					nameField1.setEnabled(false);
					nameField2.setEnabled(false);
					requestFocus();
					klistener1 = (KeyListener) new MyKeyHandler();
					addKeyListener(klistener1);
					drawRunner();
				}} );	
			ActionListener alistener1 = e -> {			
				init();
			};
			reButton.addActionListener(alistener1);
			
			
		}

		class myMouseListener implements MouseListener{
			
			public void mousePressed(MouseEvent e)
			{
				int X= e.getX(); // 마우스 커서의 x 좌표
				int Y= e.getY(); // 마우스 커서의 y 좌표
				map[X/47][Y/47]= !map[X/47][Y/47];
				msg.setText("마우스 클릭으로 벽을 그리세요.");
				if(map[0][0] || map[N-1][N-1])
				{
					msg.setText("시작점과 출구에는 벽을 그릴수 없습니다!");
					map[0][0] = false;
					map[N-1][N-1] = false;
				}					
				startButton.setEnabled(false);                                   //맵 변경시 다시 검사해야 시작가능
				hideButton.setEnabled(false);
				repaint(); // paint() 호출
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}	
		}
		
		public void init()
		{

			off = new BufferedImage(710, 710, BufferedImage.TYPE_INT_RGB);
			offG= off.getGraphics();
			offG.setColor(Color.white);
	    	offG.fillRect(0,0,710,710);
	    	
	    	runGame = 0;
	    	hide = false;
	    	
	    	checkButton.setEnabled(true);
	    	nameField1.setEnabled(true);
	    	nameField2.setEnabled(true);
		
			try {
				runner = ImageIO.read(new File(mazeRun.class.getResource("runner.gif").getPath()));
				door = ImageIO.read(new File(mazeRun.class.getResource("door.gif").getPath()));
			} catch (IOException e) {
			}
			
			map= new boolean[N][N];
			runnerX = 0;
			runnerY = 0;
			timer= 0.00;
			
			for(int x=0; x<N; x++){
				for(int y=0; y<N; y++){
					map[x][y]= false;
				}	
			}
			addMouseListener(mlistener1);	
			start();
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(off, 0, 0, null);
		}	
		
		public void start()
		{
			if(clock==null){
				clock= new Thread(new Runnable() {
							@Override
							public void run()
							{

								System.out.println("스레드런");	
								
								do{  
							      try{
							    	  Thread.sleep(10);
							      }catch(InterruptedException ie) {}
							      switch(runGame)
							      {
							      case 0:
							    	  offG.setColor(Color.white);
							    	  offG.fillRect(0,0,710,710);
							    	  if(!hide)							   
							    		  drawMap();
							    	  else
							    		  hideMap();
							    	  drawDoor();
							    	  drawRunner();
							    	  reButton.setVisible(false);
							    	  break;
							      case 1:
									  removeMouseListener(mlistener1);
							    	  offG.setColor(Color.white);
							    	  offG.fillRect(0,0,710,710);
							    	  hideMap();
							    	  drawDoor();
							    	  drawRunner();								
							    	  reButton.setVisible(false);
							    	  timer+=0.01;
							    	  timer =Double.parseDouble(String.format("%.2f", timer));
							    	  timeLabel.setText(timer+"s");
							    	  checkEscape();
							    	  break; 
							      }
						    	  buttonPanel.revalidate();
						    	  buttonPanel.repaint();
							      repaint();
							    }while(runGame!=2);
								
								System.out.println("런게임2");
						    	  removeKeyListener(klistener1);							    
						    	  reButton.setVisible(true);
						    	  offG.setColor(Color.white);
						    	  offG.fillRect(0,0,710,710);
						    	  drawMap();
						    	  offG.setColor(Color.blue);
						    	  offG.setFont(new Font("궁서",Font.BOLD,20));
						    	  offG.drawString(P1+"님이 만든 미로", 240, 320);
						    	  offG.drawString(P2+"님의 탈출하는데 "+timer+"초 걸렸습니다.", 150, 355);
						    	  buttonPanel.revalidate();
						    	  buttonPanel.repaint();
							      repaint();
						    	  escape();
						    	  
							}}
						);
					clock.start();
					System.out.println("스레드스타트호출");	
			}
		}
		
		public void hideMap()
		{
			hide = true;
			offG.setColor(Color.black);
			offG.fillRect(0,0,710,710);			
			
			for(int i=0; i<N; i++){
                for(int j=0; j<N; j++){
                	offG.setColor(Color.gray);
                    offG.drawRect(i*47, j*47, 47, 47);
                }
            }
			repaint();
			
		} 
				
 		public void drawMap()
        {
            for(int i=0; i<N; i++){
                for(int j=0; j<N; j++){
	                if(map[i][j]){
	                    offG.setColor(Color.black);
	                    offG.fillRect(i*47, j*47, 47, 47);
	                }else{
	                    offG.setColor(Color.black);
	                    offG.drawRect(i*47, j*47, 47, 47);
	                }
                }
            }
            repaint();
        }
		
		public void drawRunner()
		{
			offG.drawImage(runner,runnerX*47,runnerY*47,this);
			repaint();
		}
		
		public void drawDoor()
		{
			offG.drawImage(door, 47*(N-1),47*(N-1),this);
			repaint();
		}
		
		public void checkEscape()
		{
				runGame= (runnerX==(N-1)&&runnerY==(N-1))?2:1;
		}
		
		public void escape()
		{
			 try
		        {
		            AudioInputStream ais = AudioSystem.getAudioInputStream(new File("musical001.wav"));          //효과음 재생
		            Clip clip = AudioSystem.getClip();
		            clip.stop();
		            clip.open(ais);
		            clip.start();
		            
		        }
		        catch (Exception ex)
		        {
		            System.out.println(ex);
		        }
			 
			if((clock!=null)&&(clock.isAlive())){
				clock=null;
				}
			
			try {
			save();
			System.out.println("세이브");
			} catch (Exception e1) {
			}
			
		}
		
		public void save() throws Exception {
			File file = new File("mazeResult.txt");
			if(file.exists()==false) { 
				FileOutputStream fos = new FileOutputStream(file);
				Writer writer = new OutputStreamWriter(fos);
				savewrite(writer);
				fos.close();
				}
			else {
				FileInputStream fis = new FileInputStream(file);
				Reader reader = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(reader);
				List<String> tmp = new ArrayList<String>();
				while(br.ready()) {
					tmp.add(br.readLine());	
				}
				FileOutputStream fos = new FileOutputStream(file);
				Writer writer = new OutputStreamWriter(fos);
				for(int i=0;i<tmp.size();i++) {
					writer.write(tmp.get(i));
					writer.write("\n");
				}
				savewrite(writer);
				br.close();
				reader.close();
				fis.close();
				fos.close();
			}
		}


		public void savewrite(Writer writer) throws IOException {
			writer.write(P1+"이 만든 미로	->	");
			writer.write(P2+"이 탈출하는데 걸린 시간 "+timer);
			writer.write("\n");
			writer.flush();
			writer.close();
		}
		
		public boolean findMazePath(int x, int y)                     //미로 탈출가능 검사
		{
			if(x<0||y<0||x>=N||y>=N)       //맵 밖이면 실패
				return false;
			else if(maze[x][y] != WAY)     //길이 아니면 실패
				return false;
			else if(x==N-1 && y==N-1) 
			{   
				maze[x][y] =  PATH;       //출구도착 통과
				return true;
			}
			else                         //정상 범위 내 출구가 아닌 길에서 재귀함수 호출
			{
				maze[x][y]=PATH;         //검사한 길은 패스로 변경
				if(findMazePath(x-1,y)|| findMazePath(x,y+1) || findMazePath(x+1,y) || findMazePath(x,y-1))    //상하좌우 칸 이동하며 검사
				{
					return true;
				}
				maze[x][y] = BLOCK;     //출구에 도착하지 못한 길은 막힌길
				return false;
			}
		} 
		
		class MyKeyHandler extends KeyAdapter
	    {
	        public void keyPressed(KeyEvent e)
	        {
	            int keyCode= (int)e.getKeyCode();

	            if(keyCode==KeyEvent.VK_LEFT){
	                if(checkMove(-1,0))
	                	runnerX-=1;
	            }

	            if(keyCode==KeyEvent.VK_RIGHT){
	            	if(checkMove(1,0))
	                	runnerX+=1;
	            }

	            if(keyCode==KeyEvent.VK_DOWN){
	            	if(checkMove(0,1))
	                	runnerY+=1;
	            }

	            if(keyCode==KeyEvent.VK_UP){
	            	if(checkMove(0,-1))
	                	runnerY-=1;
	            }

	            mypanel.drawRunner();
	            mypanel.repaint();
	        }

	        public boolean checkMove(int x,int y)
	        {

	                if(((runnerX+x)>=0)&&((runnerX+x)<N)&&((runnerY+y)>=0)&&((runnerY+y)<N))
	                	if(!map[runnerX+x][runnerY+y]==true)
	                    	return true;
	                
	                return false;               
	        }

	    }
	}	
	
	

}
