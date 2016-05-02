import java.awt.image.BufferedImage;

import org.opencv.core.Core;
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
	
	public EvaluateFrameTransformationByOpenCV() {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	}
	@Override
	public FrameTransformation evaluateMotionBetweenImage(BufferedImage img1,
			BufferedImage img2) {
		Mat mat_temp1 = transformBufferedImageToMat(img1);
		Mat mat1 = new Mat();
	    Imgproc.cvtColor(mat_temp1, mat1, Imgproc.COLOR_RGB2GRAY);
	    Mat mat_temp2 = transformBufferedImageToMat(img2);
		Mat mat2 = new Mat();
	    Imgproc.cvtColor(mat_temp2, mat2, Imgproc.COLOR_RGB2GRAY);
	    MatOfPoint matOfPoint1 = new MatOfPoint(); 
	    Imgproc.goodFeaturesToTrack(mat1, matOfPoint1, maxCornersInImage, qualityLevelInImage, minDistanceInImage);

	    MatOfByte status = new MatOfByte();
	    MatOfFloat err = new MatOfFloat();
	    MatOfPoint2f matOfPoint2f1 = new MatOfPoint2f(matOfPoint1.toArray());
	    MatOfPoint2f matOfPoint2f2 = new MatOfPoint2f();
	    Video.calcOpticalFlowPyrLK(mat1, mat2, matOfPoint2f1, matOfPoint2f2, status, err);
	
//	    System.out.println(status.width());
//	    System.out.println(status.height());
//	    
//	    Mat mat1_corner = new Mat();
//	    Mat mat2_corner = new Mat();
//	    
//	    System.out.println(status.height());
//	    System.out.println(status.width());
//		for (int i = 0; i != status.height(); i++) {
//
////			System.out.println(i);
////			System.out.println(status.get(i, 0).length);
////			System.out.println(status.get(i, 0)[0]);
//			if (status.get(i, 0)[0] != 0) {
//				mat1_corner.push_back(matOfPoint2f1);
//				mat2_corner.push_back(matOfPoint2f2);
////				System.out.println(matOfPoint2f1.dump());
////				System.out.println(matOfPoint2f2.dump());
//			}

//		}
	    
	    return null;
	}
	
	private Mat transformBufferedImageToMat(BufferedImage img){
		Mat mat = Mat.eye(img.getWidth(), img.getHeight(), CvType.CV_8UC3);
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
