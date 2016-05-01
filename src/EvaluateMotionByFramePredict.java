import java.awt.image.BufferedImage;


public class EvaluateMotionByFramePredict implements EvaluateMotion{
	
	private int blockWidth;
	private int blockHeight;
	private int predictWidth;
	private int predictHeight;
	public EvaluateMotionByFramePredict(int blockWidth, int blockHeight, int predictWidth, int predictHeight) {
		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;
		this.predictWidth = predictWidth;
		this.predictHeight = predictHeight;
	}
	@Override
	public double evaluateMotionBetweenImage(BufferedImage img1,
			BufferedImage img2) {
		return getDiffBetweenImage(img1, img2);
	}
	
	
	
	public double getDiffBetweenImage(BufferedImage img1, BufferedImage img2){
		
		double value = 0;
		for(int i = 0; i!= img1.getWidth(); i+= blockWidth){
			for(int j = 0; j!= img1.getHeight(); j+= blockHeight){
				MotionPredict motionPredict = getMotionPredict(img1, img2, i, j);
				value += motionPredict.dx + motionPredict.dy;
			}
		}
		return value;
	}
	
	public MotionPredict getMotionPredict(BufferedImage img1, BufferedImage img2, int locationX, int locationY){
		double minDiff = 100000;
		MotionPredict motionPredict = new MotionPredict(0, 0, minDiff);
		for(int i = -predictWidth; i <= predictWidth; i++){
			for(int j = -predictHeight; j <= predictHeight; j++){
				if(locationX + i >= 0 && locationX + i + blockWidth <= img1.getWidth() && locationY + j >= 0 && locationY + j + blockHeight <= img1.getHeight())
				{
//					BufferedImage block1 = img1.getSubimage(locationX+i, locationY+j, blockWidth, blockHeight);
//					BufferedImage block2 = img2.getSubimage(locationX, locationY,blockWidth, blockHeight);
//					double temp = getDiffBetweenBlock(block1, block2);
					double temp = getDiffBetweenBlock(img1, img2, locationX+i, locationY+j, locationX, locationY);
					if(temp < minDiff){
						minDiff = temp;
						motionPredict = new MotionPredict(i, j, temp);
					}
				}
			}
		}
		return motionPredict;
	}
	public double getDiffBetweenBlock(BufferedImage block1, BufferedImage block2){
		double value = 0;
		for(int i = 0; i!= block1.getWidth(); i++){
			for(int j = 0; j != block2.getHeight(); j++){
				value += getDiffBetweenPixel(block1.getRGB(i, j), block2.getRGB(i, j));
			}
		}
		return value;
	}
	
	public double getDiffBetweenBlock(BufferedImage img1, BufferedImage img2, int locationX1, int locationY1, int locationX2, int locationY2){
		double value = 0;
		for(int i = 0; i!= blockWidth; i++){
			for(int j = 0; j != blockHeight; j++){
				value += getDiffBetweenPixel(img1.getRGB(i+locationX1, j+locationY1), img2.getRGB(i+locationX2, j+locationY2));
			}
		}
		return value;
	}
	public BufferedImage[] divideIntoBlock(BufferedImage img){
		BufferedImage[] imgs = new BufferedImage[img.getWidth() * img.getHeight() / (this.blockHeight * this.blockWidth)];
		int currentPos = 0;
		for(int i = 0; i< img.getWidth(); i+= this.blockWidth){
			for(int j = 0 ; j < img.getHeight(); j += this.blockHeight){
//				System.out.println(i);
//				System.out.println(j);
//				System.out.println(blockWidth);
//				System.out.println(img.getWidth());
				imgs[currentPos] = img.getSubimage(i, j, this.blockWidth, this.blockHeight);
				currentPos++;
			}
		}
		return imgs;
	}	
	
	public double getDiffBetweenPixel(int rgb1, int rgb2) {
		int alpha1 = (rgb1 >> 24) & 0xFF;
		int red1 = (rgb1 >> 16) & 0xFF;
		int green1 = (rgb1 >> 8) & 0xFF;
		int blue1 = (rgb1) & 0xFF;
		int alpha2 = (rgb2 >> 24) & 0xFF;
		int red2 = (rgb2 >> 16) & 0xFF;
		int green2 = (rgb2 >> 8) & 0xFF;
		int blue2 = (rgb2) & 0xFF;

		return Math.abs(red1 - red2) + Math.abs(green1 - green2)
				+ Math.abs(blue1 - blue2);
	}

}
