import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.io.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.LineBorder;

import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;

public class AVPlayer {

	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;
	BufferedImage img;
	BufferedImage img_right;
	BufferedImage[] bufferedImgs;
	PlayImage playImage;
	BufferedImage rightImg;
	private double maxSimilarity = -100;
	private int maxOrder;

	// peter
	JButton btnReplay;
	JButton btnStart;
	JButton btnStop;

	// peter


	private BufferedImage readRightImg(String imgname, int width, int height){
		File file = new File(imgname);
		System.out.println(file.length() + "file");
		InputStream is;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		try {
			is = new FileInputStream(file);
			long len = file.length();
			byte[] bytes = new byte[(int)len];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0){
				offset += numRead;
			}
			int ind = 0;

			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){

					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 
					//System.out.println("r: " + r + " g "+ g + " b " + b);
					//System.out.println("RGB: " + r + " " + g + " " + b);
					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					img.setRGB(x,y,pix);
					ind++;
				}
			}
			
			
			
		
			

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Image  postImg = img.getScaledInstance(img.getWidth()/2, img.getHeight()/2, Image.SCALE_DEFAULT);
		BufferedImage buffered = new BufferedImage(img.getWidth()/2, img.getHeight()/2, BufferedImage.TYPE_INT_RGB);
		buffered.getGraphics().drawImage(postImg, 0, 0 , null);
		this.rightImg = buffered;
		return buffered;
//		return getSmallImage(img,2,2);
		
	}
	
	private BufferedImage getSmallImage(BufferedImage img, int w, int h){
		BufferedImage[] imgs = divideIntoBlock(img, w, h);
		BufferedImage newImg = new BufferedImage(img.getWidth()/w, img.getHeight()/h, BufferedImage.TYPE_INT_RGB );
		int ind = 0;
		for(int x = 0; x < newImg.getWidth(); x++){
			for(int y = 0; y < newImg.getHeight(); y++){
				
				int pix =averageInBlock(imgs[ind]);
				newImg.setRGB(x,y,pix);
				ind++;
			}
		}
		return newImg;
		
		
	}
	
	private BufferedImage[] divideIntoBlock(BufferedImage img, int w, int h){
		BufferedImage[] imgs = new BufferedImage[img.getWidth() * img.getHeight() /( w * h)];
		int currentPos = 0;
		for(int i = 0; i< img.getWidth(); i+= w){
			for(int j = 0 ; j < img.getHeight(); j += h){
//				System.out.println(i);
//				System.out.println(j);
//				System.out.println(blockWidth);
//				System.out.println(img.getWidth());
				imgs[currentPos] = img.getSubimage(i, j, w, h);
				currentPos++;
			}
		}
		return imgs;
	}	
	
	public int averageInBlock(BufferedImage block){
		int[] rgbs = new int[3];
		int rs = 0;
		int gs = 0;
		int bs = 0;
		for(int i = 0; i < block.getWidth(); i += 1){
			for(int j = 0; j < block.getHeight(); j+= 1){
				int rgb = block.getRGB(i, j);
				int alpha = (rgb >> 24) & 0xFF;
				int red =   (rgb >> 16) & 0xFF;
				int green = (rgb >>  8) & 0xFF;
				int blue =  (rgb      ) & 0xFF;
				rs += red;
				gs += green;
				bs += blue;
			}
		}
		rgbs[0] = rs/(block.getWidth()*block.getHeight());
		rgbs[1] = gs/(block.getWidth()*block.getHeight());
		rgbs[2] = bs/(block.getWidth()*block.getHeight());
		int pix = 0xff000000 | ((rgbs[0] & 0xff) << 16) | ((rgbs[1] & 0xff) << 8) | (rgbs[2] & 0xff);
		
		return pix;
	}

	public void initialize(String[] args) {

				
		
		img_right = readRightImg(args[2],1280,720);
		this.playImage = new PlayImage(args[0], img_right);
		
		
		img = playImage.getFirstImage();
		// Use labels to display the images

		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		JLabel lbText1 = new JLabel("Video: " + args[0]);
		lbText1.setHorizontalAlignment(SwingConstants.LEFT);
		JLabel lbText2 = new JLabel("Audio: " + args[1]);
		lbText2.setHorizontalAlignment(SwingConstants.LEFT);
		lbIm1 = new JLabel(new ImageIcon(img));
		lbIm2 = new JLabel(new ImageIcon(img_right));
		

		JLabel btnMainLabel = new JLabel();

		JLabel last = new JLabel("last");
		// JLabel button_stop = new JLabel("btn_stop");

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(lbText1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbText2, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		frame.getContentPane().add(lbIm1, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 2;
		frame.getContentPane().add(lbIm2, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 3;

		frame.getContentPane().add(btnMainLabel, c);

		frame.pack();
		frame.setVisible(true);
		
		frame.setSize(1200, 650);


		ButtonLayOut btnLayOut = new ButtonLayOut();
		btnMainLabel.setPreferredSize(new Dimension(300, 60));
		btnReplay = btnLayOut.initButton(btnMainLabel, 150, 60, "replay.png");
		btnStart = btnLayOut.initButton(btnMainLabel, 200, 60, "start.png");
		btnStop = btnLayOut.initButton(btnMainLabel, 150, 60, "stop.png");

		btnMainLabel.setBorder(new LineBorder(Color.green, 0));
		btnMainLabel.setLayout(new BorderLayout());

		btnMainLabel.add(btnReplay, BorderLayout.WEST);
		btnMainLabel.add(btnStart, BorderLayout.CENTER);
		btnMainLabel.add(btnStop, BorderLayout.EAST);
		setBtnListener();
		

	}

	public void updateFrame() {
		// System.out.print(100000000);
//		// System.out.printf("%d, ", current);
//		EvaluateSimilarity evaluateSimilarity = new EvaluateSimilarityByRescale();
		System.out.println(new Date());

		try {
			
		

			while (true) {
//				BufferedImage img = playImage.getCurrentImg();
				BufferedImage img = playImage.getCurrentImageProcessed();
				while (img == null) {
//					img = playImage.getCurrentImg();
					img =  playImage.getCurrentImageProcessed();
					Thread.sleep(10);

				}
//				double similarity = evaluateSimilarity.evaluateSimilarityBetweenImage(img, rightImg);
//				System.out.printf("%f, %f\n",similarity, maxSimilarity);
//				if(maxSimilarity < similarity){
//					maxSimilarity = similarity;
//					maxOrder = playImage.getCurrent();
//				}
				
//				System.out.println(img);
				lbIm1.setIcon(new ImageIcon(img));
				img = null;
//				if(playImage.isFinished()){
//					break;
//				}
			}
//			BufferedImage img = playImage.getImg(maxOrder);
//			
//			System.out.println(maxSimilarity);
//			System.out.println(maxOrder);
//			
//			lbIm1.setIcon(new ImageIcon(img));

			

		}

		catch (InterruptedException e) {

		}
	}

	// peter

	PlaySound playSound;

	// peter

	public void playWAV(String filename) {
		
		
		// opens the inputStream
		FileInputStream inputStream1;
		InputStream inputStream2;
		byte[] buffer = null;
		try {
			System.out.println("step - 1");
			inputStream1 = new FileInputStream(filename);
			//inputStream1 = new FileInputStream("a.wav");
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		File file = new File(filename);
		
	
		System.out.println("step - 2");
		calculate_audio audio = new calculate_audio();
		buffer = audio.calculate_audio(inputStream1,(int)file.length());
/**		
		FileInputStream stream = null;
		try {
			stream = new FileInputStream("a.wav");
			//stream = new FileInputStream("a.wav");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
*/		
		
//		System.out.println("test");
		// initializes the playSound Object
		playSound = new PlaySound(buffer,filename);
//		System.out.println(playSound);
		// plays the sound
		try {
			playSound.play();
		} catch (PlayWaveException | IOException e) {
			e.printStackTrace();
			return;
		}
		
	}

	public void setBtnListener() {
		btnStart.addActionListener(new ActionListener() {
			boolean is_pause = false;

			@Override
			public void actionPerformed(ActionEvent e) {

				// start button was clicked
				if (!is_pause) {
					AVPlayer.this.playImage.startOrContinue();
					btnStart.setIcon(ButtonLayOut.ChangeImgSize(new ImageIcon(
							"pause.png"), 60, 60));
//					playSound.startOrResume();
					is_pause = true;
				} else {
					AVPlayer.this.playImage.pause();
					btnStart.setIcon(ButtonLayOut.ChangeImgSize(new ImageIcon(
							"start.png"), 60, 60));
//					playSound.Stop();
					is_pause = false;
				}

			}
		});
	}

	public static void main(final String[] args) {
		if (args.length < 2) {
			System.err
					.println("usage: java -jar AVPlayer.jar [RGB file] [WAV file]");
			return;
		}

		final AVPlayer ren = new AVPlayer();
		ren.initialize(args);
		
		

		Thread updateFrameThread = new Thread() {
			public void run() {
				ren.updateFrame();
			}
		};
		updateFrameThread.start();

		ren.playWAV(args[1]);

	}

}