import java.awt.image.BufferedImage;

public class EvaluateSimilarityByColor implements EvaluateSimilarity {

	@Override
	public double evaluateSimilarityBetweenImage(BufferedImage img1, BufferedImage img2) {
		// TODO Auto-generated method stub
		int[][][] Indexhistogram = new int[4][4][4];
		int[][][] histogram = new int[4][4][4];
		int height = img1.getHeight();
		int width = img1.getWidth();
		int senceDiff = 0;
		
		for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {
				int pix = img1.getRGB(x, y);
				int red = (pix>>16)&0xFF;
				if (red < 0) red = red+256;
				int green = (pix>>8)&0xFF;
				if (green < 0) green = green+256;
				int blue = (pix)&0xFF;
				if (blue < 0) blue = blue+256;
				
				int pix2 = img2.getRGB(x, y);
				int red2 = (pix2>>16)&0xFF;
				if (red2 < 0) red2 = red2+256;
				int green2 = (pix2>>8)&0xFF;
				if (green2 < 0) green2 = green2+256;
				int blue2 = (pix2)&0xFF;
				if (blue2 < 0) blue2 = blue2+256;
				
				
				
				Indexhistogram[red / 64][green / 64][blue / 64]++;
				histogram[red2 / 64][green2 / 64][blue2 / 64]++;
			}
		}
		
		for(int i = 0; i < histogram.length; i++)
          for(int j = 0; j < histogram[i].length; j++)
              for(int p = 0; p < histogram[i][j].length; p++){
              	senceDiff += Math.abs(histogram[i][j][p] - Indexhistogram[i][j][p]);
              }	
		
		System.out.println(senceDiff);
		return senceDiff;
	}

}
