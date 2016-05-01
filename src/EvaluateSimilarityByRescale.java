import java.awt.Image;
import java.awt.image.BufferedImage;

public class EvaluateSimilarityByRescale implements EvaluateSimilarity {

	private int rescaleWidth = 16;
	private int rescaleHeight = 16;

	@Override
	public double evaluateSimilarityBetweenImage(BufferedImage img1,
			BufferedImage img2) {
		Image postImg1 = img1.getScaledInstance(rescaleWidth, rescaleHeight,
				Image.SCALE_DEFAULT);
		Image postImg2 = img2.getScaledInstance(rescaleWidth, rescaleHeight,
				Image.SCALE_DEFAULT);
		BufferedImage buffered1 = new BufferedImage(rescaleWidth,
				rescaleHeight, BufferedImage.TYPE_INT_RGB);
		buffered1.getGraphics().drawImage(postImg2, 0, 0, null);
		BufferedImage buffered2 = new BufferedImage(rescaleWidth,
				rescaleHeight, BufferedImage.TYPE_INT_RGB);
		buffered2.getGraphics().drawImage(postImg1, 0, 0, null);
		return sameCount(buffered1, buffered2);
		
	}
	private int sameCount(BufferedImage img1, BufferedImage img2){
		int[][] grayImg1 = changeImageFromRGBToGray(img1);
		int[][] grayImg2 = changeImageFromRGBToGray(img2);
		int[] hash1 = hashcode(grayImg1);
		int[] hash2 = hashcode(grayImg2);
		return sameCount(hash1, hash2);
	}
	
	private int sameCount(int[] hash1, int[] hash2){
		int count =0;
		for(int i =0; i!=hash1.length; i++){
			if(hash1[i] == hash2[i]){
				count += 1;
			}
		}
		return count;
	}
	private int[] hashcode(int[][] grayImg){
		double averageNum = average(grayImg);
		int[] result = new int[grayImg.length * grayImg[0].length];
		int ind = 0;
		for(int i = 0; i!= grayImg.length; i++){
			for(int j = 0; j != grayImg[i].length; j++){
				if(grayImg[i][j] >= averageNum){
					result[ind] = 1;
				}
				else{
					result[ind] = 0;
				}
				ind++;
			}
		}
		return result;
	}
	
	private double average(int[][] grayImg){
		double sum = 0;
		for(int i = 0; i!= grayImg.length; i++){
			for(int j = 0; j != grayImg[i].length; j++){
				sum += grayImg[i][j];
			}
		}
		return sum/(grayImg.length*grayImg[0].length);
	}
	
	private int[][] changeImageFromRGBToGray(BufferedImage img){
//		BufferedImage postImg= new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		int[][] result = new int[img.getWidth()][img.getHeight()];
		for(int i = 0; i!= img.getWidth(); i++){
			for(int j = 0; j!= img.getHeight(); j++){
				result[i][j] = rgbToY(img.getRGB(i, j));
			}
		}
		return result;
		
		
	}

	private int rgbToY(int rgb) {
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = (rgb) & 0xFF;
		return (int) (red * 0.299 + green * 0.587 + blue * 0.114);
	}
}
