import java.awt.image.BufferedImage;



public class EvaluateMotionByCompareAverageValueInBlock implements EvaluateMotion{
	private int blockWidth;
	private int blockHeight;
	private int threshold;
	public EvaluateMotionByCompareAverageValueInBlock(int blockWidth, int blockHeight, int threshold) {
		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;
		this.threshold = threshold;
	}
	@Override
	public double evaluateMotionBetweenImage(BufferedImage img1,
			BufferedImage img2) {
		return getExceedThresholdBlock(img1, img2);
	}
	
	public int getExceedThresholdBlock(BufferedImage img1, BufferedImage img2){
		BufferedImage[] blocks1 = divideIntoBlock(img1);
		BufferedImage[] blocks2 = divideIntoBlock(img2);
		int value = 0;
		for(int i = 0; i!= blocks1.length; i++){
			value += getDiffBetweenBlock(blocks1[i], blocks2[i])> threshold?1:0;
		}
		return value;
	}
	
	public double getDiffBetweenImage(BufferedImage img1, BufferedImage img2){
		BufferedImage[] blocks1 = divideIntoBlock(img1);
		BufferedImage[] blocks2 = divideIntoBlock(img2);
		double value = 0;
		for(int i = 0; i!= blocks1.length; i++){
			value += getDiffBetweenBlock(blocks1[i], blocks2[i]);
		}
		return value;
	}
	public double getDiffBetweenBlock(BufferedImage block1, BufferedImage block2){
		double[] rgbs1 = averageInBlock(block1);
		double[] rgbs2 = averageInBlock(block2);
		double value = 0;
		for(int i = 0; i!= rgbs1.length; i++){
			value += Math.abs(rgbs1[i] - rgbs2[i]);
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
	
	public double[] averageInBlock(BufferedImage block){
		double[] rgbs = new double[3];
		double rs = 0;
		double gs = 0;
		double bs = 0;
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
		return rgbs;
	}
	
	

}
