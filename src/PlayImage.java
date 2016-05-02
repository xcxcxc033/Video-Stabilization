import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class PlayImage {
	private int width = 480;
	private int height = 270;
	private int current = 0;
	private int loadFrame = 100;
	private int loadedFrame = -1;
	private Object currentLock = new Object();
	private final int intervalTime = 66;// 66;
	private TimerTask updateFrameTimerTask;
	private Thread imgs;
	private Integer[] locks;
	private BufferedImage[] bufferedImgs;
	private int last = -1;
	private String filename;
	private int motionlessFrame = 0;
	private double[] evaluateMotionResult;
	private double[] evaluateSimilarityResult;
	private double[] evaluateSimilarityByColorResult;
	public FrameTransformation[] frameTransformationResult;
	public FrameTransformation[] newPrevToCurResult;
	private int diffByColorThreshold = 100000;
	private boolean processFinished = false;
	private int[] frameNumberToPlay;
	private double[] evaluateSenceChangeResult;
	private int SMOOTHING_RADIUS = 30; // In frames. The larger the more stable the video, but less reactive to sudden panning
	private int HORIZONTAL_BORDER_CROP = 20; // In pixels. Crops the border to reduce the black borders from stabilisation being too noticeable.
	private BufferedImage[] adjustedImages;
	private int debugProcessRatio = 10;


	// peter
	public int[][][] histogram = new int[4][4][4];
	public int[][][] nextHistogram = new int[4][4][4];
	public int frameRate = 15;
	public int secForScene = 5;
	public int SceneFrameCounter = 0;
	public boolean findNextScene = false;
	public int senceDiff = 0;
	public int senceThreshold = 5000000;
	public int[] sumFrame;
	public int numOfFrame = 0;
	public int sumIndex = 0;
	public int[] senceChangeFrame;
	public int senceChangeIndex = 0;
	private boolean finished = false;
	private BufferedImage compareImage;
	// peter
	
	

	public PlayImage(final String filename) {
		
		this.filename = filename;
		File file = new File(filename);
		

		// long len = file.length();
		long len = width * height * 3;
		bufferedImgs = new BufferedImage[(int) (file.length() / len)];

		// peter
		sumFrame = new int[(int) (file.length() / len)];
		senceChangeFrame = new int[(int) (file.length() / len)];
		senceChangeFrame[0] = 0;
		// peter

		locks = new Integer[bufferedImgs.length];
		for (int i = 0; i != locks.length; i++) {
			locks[i] = new Integer(i);
		}
		
		Thread read = new Thread() {
			public void run() {
				PlayImage.this.allFrames(filename);
				
				PlayImage.this.frameTransformationResult = PlayImage.this.getEvaluateFrameTransformationResult();
				PlayImage.this.newPrevToCurResult = PlayImage.this.getNewPrevToCurTransformResult(0, frameTransformationResult.length/debugProcessRatio);
				PlayImage.this.adjustedImages =PlayImage.this.adjustFrames();
//				PlayImage.this.evaluateSimilarityResult = PlayImage.this.getEvaluateSimilarityResult();
//				for (int i = 0; i != PlayImage.this.evaluateSimilarityResult.length; i++) {
//					System.out.printf("%d: %f\n", i, PlayImage.this.evaluateSimilarityResult[i]);
//				}
//				int maxOrder = PlayImage.this.findMaxOrder(PlayImage.this.evaluateSimilarityResult);
//				PlayImage.this.evaluateSimilarityByColorResult  = PlayImage.this.getEvaluateSimilarityByColorResult(maxOrder);
//
//				PlayImage.this.frameNumberToPlay = PlayImage.this.generateFrameNumberToPlay();
//				System.out.println("frameNumberToPlay");
//				System.out.println(PlayImage.this.frameNumberToPlay[0]);
////				for (int i = 0; i != PlayImage.this.frameNumberToPlay.length; i++) {
////					System.out.println(PlayImage.this.frameNumberToPlay[i]);
////				}
//				CommunicateVariables communicateVariables = CommunicateVariables.getSingular();
//				communicateVariables.imageIndexInput(frameNumberToPlay);
//				while(! communicateVariables.finished()){
//					try {
//						Thread.sleep(10);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				
//				frameNumberToPlay = communicateVariables.getIndex();
////				for (int i = 0; i != PlayImage.this.frameNumberToPlay.length; i++) {
////					System.out.println(PlayImage.this.frameNumberToPlay[i]);
////				}
				processFinished = true;
				System.out.println(processFinished);

			}
		}; 
		read.start();

	}
	public BufferedImage[] adjustFrames(){
		BufferedImage[] result = new BufferedImage[bufferedImgs.length];
		for(int i = 0 ; i!= bufferedImgs.length/debugProcessRatio; i++){
			result[i] = adjustFrame(bufferedImgs[i], newPrevToCurResult[i]);
		}
		return result;
	}
	public BufferedImage adjustFrame(BufferedImage img, FrameTransformation frameTransformation){
		BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		for(int i = 0; i != img.getWidth(); i++){
			for(int j = 0; j != img.getHeight(); j++){
				int tempX = (int) (i+frameTransformation.getDx());
				int tempY = (int) (j + frameTransformation.getDy());
				if( tempX >= 0 && tempX< img.getWidth() && tempY >= 0 && tempY < img.getHeight()){
					img2.setRGB(tempX, tempY, img.getRGB(i, j));
				}
			}
		}
		return img2;
	}
	
	public double findMax(double[] value){
		double max = value[0];
		for(int i = 0; i!= value.length; i++){
			if(max < value[i]){
				max = value[i];
			}
		}
		return max;
	}
	public int findMaxOrder(double[] value){
		double max = value[0];
		int order = 0;
		for(int i = 0; i!= value.length; i++){
			if(max < value[i]){
				max = value[i];
				order = i;
			}
		}
		return order;
	}
	
	public int getCurrent(){
		synchronized (currentLock) {
			return this.current;
		}
	}
	
	
	public double[] getSenceChangeResult(){
		double[] result = new double[bufferedImgs.length];
		List<Integer> temp = new ArrayList<>();
		temp.add(0);
		for(int i = 0; i != senceChangeFrame.length; i++){
			if(senceChangeFrame[i] != 0){
				temp.add(senceChangeFrame[i]);
				System.out.println(senceChangeFrame[i]);
			}
		}
		
		for(int i = 0; i != temp.size(); i++){
			for(int j = 0 ; j != frameRate * secForScene; j++){
				if(i+j < result.length){
					result[temp.get(i)+j] = 500;
				}
			}
		}
		
		return result;
	}

	public int[] generateFrameNumberToPlay(double valve) {
		double[] evaluateValue = getEvaluateValue();
		int[] temp = new int[bufferedImgs.length];
		int current = 0;
		for (int i = 0; i != evaluateValue.length; i++) {
			if (evaluateValue[i] > valve) {
				temp[current] = i;
				current++;
			}
		}
		System.out.println(current);
		return temp;
	}
	
	public int[] generateFrameNumberToPlay() {
		double[] evaluateValue = getEvaluateValue();
		int[] temp = new int[bufferedImgs.length];
		int current = 0;
		double max = -1000;
		int order = 0;
		for (int i = 0; i != evaluateValue.length; i++) {
			if (evaluateValue[i] > max) {
				max = evaluateValue[i];
				order = i;
			}
		}
		
		int start = order;
		while(start > 1){
			if(evaluateSimilarityByColorResult[start] > diffByColorThreshold){
				break;
			}
			else{
				start-=1;
			}
		}
		int end = order+1;
		while(end < evaluateValue.length){
			if(evaluateSimilarityByColorResult[end-1] > diffByColorThreshold){
				break;
			}
			else{
				end+=1;
			}
		}
		for(int i = 0; i != end-start; i++){
			temp[i] = i+start;
		}
//		System.out.println(current);
		System.out.printf("Start: %d", start);
		System.out.printf("End: %d", end);
		return temp;
	}

	public double[] getEvaluateValue() {
		double[] result = new double[bufferedImgs.length];
		for(int i = 0; i != result.length; i++){
			result[i] = evaluateSimilarityResult[i];
		}
		return result;
	}
	

	public BufferedImage getCurrentImageProcessed() {
		if (this.processFinished == false) {
			return null;
		}
		synchronized (currentLock) {
			
			if (current >= bufferedImgs.length) {
				this.finished = true;
				return null;
			}
			synchronized (locks[current]) {
				if (last == current) {
					
					return null;
				} else {
					last = current;
//					return bufferedImgs[current];
					return adjustedImages[current]; 
				}

			}
		}
	}

	public double[] getEvaluateSimilarityByColorResult(int order){
		EvaluateSimilarity evaluateSimilarity = new EvaluateSimilarityByColor();
		double[] result = new double[bufferedImgs.length];
		result[0] = 0;
		System.out.println("Color");
		for (int i = 0; i < bufferedImgs.length; i++) {
			result[i] = evaluateSimilarity.evaluateSimilarityBetweenImage(
					bufferedImgs[i], bufferedImgs[order]);
			System.out.printf("%d: %f\n", i, result[i]);
		}
		return result;
	}
	public double[] getEvaluateSimilarityResult(){
		EvaluateSimilarity evaluateSimilarity = new EvaluateSimilarityByRescale();
		double[] result = new double[bufferedImgs.length];
		result[0] = 0;
		for (int i = 0; i < bufferedImgs.length; i++) {
			result[i] = evaluateSimilarity.evaluateSimilarityBetweenImage(
					bufferedImgs[i], compareImage);
		}
		
		
		return result;
	}
	
	public FrameTransformation[] getEvaluateFrameTransformationResult(){
		EvaluateFrameTransformation evaluateFrameTransformation = new EvaluateFrameTransformationByOpenCV();
		FrameTransformation[] result = new FrameTransformation[bufferedImgs.length];
		result[0] = new FrameTransformation(0, 0, 0);
		for (int i = 1; i < bufferedImgs.length/debugProcessRatio; i++) {
			System.out.println(i);
			System.out.println(bufferedImgs[i-1]);
			result[i] = evaluateFrameTransformation.evaluateMotionBetweenImage(
					bufferedImgs[i-1], bufferedImgs[i]);
		}
		
		
		return result;
	}
	
	public FrameTransformation[] getNewPrevToCurTransformResult(int start, int end){
		double a = 0;
		double x = 0;
		double y = 0;
		List<FrameTransformation> trajectory = new ArrayList<>();
		
		for(int i = start; i< end; i++){
			x += frameTransformationResult[i].getDx();
			y += frameTransformationResult[i].getDy();
			a += frameTransformationResult[i].getDa();
			
			trajectory.add(new FrameTransformation(x, y, a));
		}
		
		List<FrameTransformation> smoothed_trajectory = new ArrayList<>();
		
		for(int i = 0; i < end-start; i++){
			double sum_x = 0;
			double sum_y = 0;
			double sum_a = 0;
			int count = 0;
			for(int j=-SMOOTHING_RADIUS; j <= SMOOTHING_RADIUS; j++){
				if(i+j>=0 && i+j < end-start){
					sum_x += trajectory.get(i+j).getDx();
					sum_y += trajectory.get(i+j).getDy();
					sum_a += trajectory.get(i+j).getDa();
					
					count++;
				}
			}
			double avg_a = sum_a /count;
			double avg_x = sum_x/count;
			double avg_y = sum_y / count;
			smoothed_trajectory.add(new FrameTransformation(avg_x,avg_y, avg_a));
		}
		
		List<FrameTransformation> new_prev_to_cur_transform = new ArrayList<>();
		a = 0;
		x = 0;
		y = 0;
		
		for(int i = 0; i < end-start; i++){
			x+=  frameTransformationResult[i+start].getDx();
			y+=  frameTransformationResult[i+start].getDy();
			a+=  frameTransformationResult[i+start].getDa();
			
			double diff_x = smoothed_trajectory.get(i).getDx() - x;
			double diff_y = smoothed_trajectory.get(i).getDy() - y;
			double diff_a = smoothed_trajectory.get(i).getDa() - a;
			
			double dx = frameTransformationResult[i+start].getDx() + diff_x; 
			double dy = frameTransformationResult[i+start].getDy() + diff_y; 
			double da = frameTransformationResult[i+start].getDa() + diff_a; 
			
			new_prev_to_cur_transform.add(new FrameTransformation(dx, dy, da));
		}
		FrameTransformation[] result = new FrameTransformation[new_prev_to_cur_transform.size()];
		for(int i = 0 ;i != result.length; i++){
			result[i] = new_prev_to_cur_transform.get(i);
		}
		return result;
		
	}
	public double[] getEvaluateMotionResult() {
		// EvaluateMotion evaluateMotion = new EvaluateMotionByFramePredict(15,
		// 15, 5, 5);
		EvaluateMotion evaluateMotion = new EvaluateMotionByCompareAverageValueInBlock(
				15, 15, 500);

		double[] result = new double[bufferedImgs.length];
		result[0] = 0;
		for (int i = 1; i < bufferedImgs.length; i++) {
//			System.out.println(bufferedImgs[0]);
//			System.out.println(bufferedImgs[1]);
			result[i] = evaluateMotion.evaluateMotionBetweenImage(
					bufferedImgs[i - 1], bufferedImgs[i]);
//			System.out.println(i);
//			System.out.println(result[i]);
		}
		double temp = 0;
		for (int i = 0; i < bufferedImgs.length; i+= frameRate) {
			for(int j = 0; j != frameRate; j++){
				if(temp < result[i+j]){
					temp= result[i+j];
				}
			}
			for(int j = 0; j != frameRate; j++){
				result[i+j] = temp;
			}
		}
		
		return result;
	}
	
//	public BufferedImage readRightImg(){
//		
//	}

	public BufferedImage getFirstImage() {

		BufferedImage img = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		InputStream is = null;

		File file = new File(this.filename);

		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// long len = file.length();
		long len = width * height * 3;
		bufferedImgs = new BufferedImage[(int) (file.length() / len)];

		byte[] bytes = new byte[(int) len];

		int offset = 0;
		int numRead = 0;
		try {
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int ind = 0;
		for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {

				byte a = 0;
				byte r = bytes[ind];
				byte g = bytes[ind + height * width];
				byte b = bytes[ind + height * width * 2];

				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8)
						| (b & 0xff);
				// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
				img.setRGB(x, y, pix);
				ind++;
			}
		}
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return img;

	}

	public void allFrames(String filename) {

		try {
			File file = new File(filename);
			InputStream is = new FileInputStream(file);
			allFrames(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public boolean isFinished(){
		return finished;
	}

	
	public void allFrames(InputStream is) {

		try {
			for (int i = 0; i < bufferedImgs.length; i++) {
				synchronized (locks[i]) {
					if (bufferedImgs[i] == null) {
						BufferedImage temp = readNextFrame(is);
						bufferedImgs[i] = temp;
						loadedFrame = i;
					}

					
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}

	public BufferedImage readNextFrame(InputStream is) throws IOException {

		BufferedImage img = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		// long len = file.length();
		long len = width * height * 3;
		byte[] bytes = new byte[(int) len];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		int ind = 0;

		if (findNextScene) {
			SceneFrameCounter = 0;
//			System.out.println("findNextScene" + findNextScene);

			for (int y = 0; y < height; y++) {

				for (int x = 0; x < width; x++) {

					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind + height * width];
					byte b = bytes[ind + height * width * 2];
					// peter
					int red = r;
					if (r < 0)
						red = red + 256;
					int green = g;
					if (g < 0)
						green = green + 256;
					int blue = b;
					if (b < 0)
						blue = blue + 256;

					r = (byte) red;
					g = (byte) green;
					b = (byte) blue;

					nextHistogram[red / 64][green / 64][blue / 64]++;

					int pix = 0xff000000 | ((r & 0xff) << 16)
							| ((g & 0xff) << 8) | (b & 0xff);
					// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x, y, pix);
					ind++;
				}
			}
			for (int i = 0; i < histogram.length; i++)
				for (int j = 0; j < histogram[i].length; j++)
					for (int p = 0; p < histogram[i][j].length; p++) {
						senceDiff = Math.abs(histogram[i][j][p]
								- nextHistogram[i][j][p]);
					}
			if (senceDiff >= senceThreshold) {
				findNextScene = false;
				for (int i = 0; i < histogram.length; i++)
					for (int j = 0; j < histogram[i].length; j++)
						for (int p = 0; p < histogram[i][j].length; p++) {
							histogram[i][j][p] = nextHistogram[i][j][p];
						}
				senceChangeIndex++;
				senceChangeFrame[senceChangeIndex] = numOfFrame;
			}
			senceDiff = 0;

		} else {
			for (int y = 0; y < height; y++) {

				for (int x = 0; x < width; x++) {

					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind + height * width];
					byte b = bytes[ind + height * width * 2];
					// peter
					if (SceneFrameCounter >= frameRate * secForScene) {
						int red = r;
						if (r < 0)
							red = red + 256;
						int green = g;
						if (g < 0)
							green = green + 256;
						int blue = b;
						if (b < 0)
							blue = blue + 256;

						r = (byte) red;
						g = (byte) green;
						b = (byte) blue;

						histogram[red / 64][green / 64][blue / 64]++;
						findNextScene = true;
					}

					// peter
					// for(int i = 0; i < histogram.length; i++)
					// for(int j = 0; j < histogram[i].length; j++)
					// for(int p = 0; p < histogram[i][j].length; p++){
					//
					// }

					int pix = 0xff000000 | ((r & 0xff) << 16)
							| ((g & 0xff) << 8) | (b & 0xff);
					// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x, y, pix);
					ind++;

				}
			}
			sumFrame[sumIndex] = numOfFrame;
			sumIndex++;
			SceneFrameCounter++;
//			System.out.println(SceneFrameCounter);
		}

		numOfFrame++;
		return img;

	}

	public BufferedImage getImg(int order) {
		if (order >= bufferedImgs.length) {
			return null;
		}
		synchronized (locks[order]) {
			return bufferedImgs[order];
		}
	}

	public BufferedImage getCurrentImg() {
		if (this.processFinished == false) {
			return null;
		}
		synchronized (currentLock) {
			
			if (current >= bufferedImgs.length) {
				this.finished = true;
				return null;
			}
			synchronized (locks[current]) {
				if (last == current) {
					
					return null;
				} else {
					last = current;
					return bufferedImgs[current];
//					return adjustedImages[current]; 
				}

			}
		}
	}

	public BufferedImage getCurrentImgScenery() {
		synchronized (currentLock) {
			if (current >= bufferedImgs.length) {
				return null;
			}
			synchronized (locks[current]) {
				if (last == current) {
					return null;
				} else {
					last = current;
					return bufferedImgs[sumFrame[current]];
				}

			}
		}
	}

	public void start() {
		PlayImage.this.current = 0;

		if (updateFrameTimerTask != null) {
			updateFrameTimerTask.cancel();
		}
		java.util.Timer updateFrameTimer = new java.util.Timer();
		updateFrameTimerTask = new TimerTask() {
			public void run() {

				synchronized (PlayImage.this.currentLock) {
//					System.out.printf("%d: PlayImage\n",PlayImage.this.current);
					PlayImage.this.current++;


				}

			}
		};
		System.out.println("start");
		updateFrameTimer.scheduleAtFixedRate(updateFrameTimerTask, 0,
				PlayImage.this.intervalTime);

	}

	public void pause() {
		if (updateFrameTimerTask != null) {
			updateFrameTimerTask.cancel();
		}
	}

	public void startOrContinue() {
		if (updateFrameTimerTask == null) {
			this.start();
		} else {
			this.avContinue();
		}
	}

	public void avContinue() {
		java.util.Timer updateFrameTimer = new java.util.Timer();
		updateFrameTimerTask = new TimerTask() {
			public void run() {

				synchronized (PlayImage.this.currentLock) {
					PlayImage.this.current++;

				}

			}
		};
		updateFrameTimer.scheduleAtFixedRate(updateFrameTimerTask, 0,
				PlayImage.this.intervalTime);
	}

}
