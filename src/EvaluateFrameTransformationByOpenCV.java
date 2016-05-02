import java.awt.image.BufferedImage;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;


public class EvaluateFrameTransformationByOpenCV implements EvaluateFrameTransformation{

	private int maxCornersInImage = 200;
	private double qualityLevelInImage = 0.01;
	private double minDistanceInImage = 30;
	@Override
	public FrameTransformation evaluateMotionBetweenImage(BufferedImage img1,
			BufferedImage img2) {
		Mat mat_temp1 = transformBufferedImageToMat(img1);
		Mat mat1 = null;
	    Imgproc.cvtColor(mat_temp1, mat1, Imgproc.COLOR_RGB2GRAY);
	    Mat mat_temp2 = transformBufferedImageToMat(img1);
		Mat mat2 = null;
	    Imgproc.cvtColor(mat_temp2, mat2, Imgproc.COLOR_RGB2GRAY);
	    MatOfPoint matOfPoint1 = null; 
	    Imgproc.goodFeaturesToTrack(mat1, matOfPoint1, maxCornersInImage, qualityLevelInImage, minDistanceInImage);
	    MatOfPoint matOfPoint2 = null;
	    Imgproc.goodFeaturesToTrack(mat2, matOfPoint2, maxCornersInImage, qualityLevelInImage, minDistanceInImage);
	    MatOfByte status = null;
	    MatOfFloat err = null;
	    MatOfPoint2f matOfPoint2f1 = new MatOfPoint2f(matOfPoint1.toArray());
	    MatOfPoint2f matOfPoint2f2 = new MatOfPoint2f(matOfPoint2.toArray());
	    
	    Video.calcOpticalFlowPyrLK(mat1, mat2, matOfPoint2f1, matOfPoint2f2, status, err);
	    
	    
	    return null;
	}
	
	private Mat transformBufferedImageToMat(BufferedImage img){
		Mat mat = new Mat(img.getWidth(), img.getHeight(), CvType.CV_8UC3);
		for(int i =0; i < img.getWidth(); i++){
			for(int j = 0; j < img.getHeight(); j++){
				int rgb = img.getRGB(i, j);
				byte red = (byte) ((rgb >> 16) & 0xFF);
				byte green = (byte) ((rgb >> 8) & 0xFF);
				byte blue = (byte) ((rgb) & 0xFF);
				mat.put(i,j, new byte[]{red,green,blue});
			}
		}
	     
	    return mat; 
	}

}
