import java.awt.image.BufferedImage;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
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
	
//	    System.out.println("start");
//	    System.out.print("width");
//	    System.out.println(status.width());
//	    System.out.print("height");
//	    System.out.println(status.height());
//	    
//	    System.out.print("width");
//	    System.out.println(matOfPoint2f1.width());
//	    System.out.print("height");
//	    System.out.println(matOfPoint2f1.height());
//	    
//	    System.out.print("width");
//	    System.out.println(matOfPoint2f2.width());
//	    System.out.print("height");
//	    System.out.println(matOfPoint2f2.height());
//	    
//	    
	    MatOfPoint2f mat1_corner = new MatOfPoint2f();
	    MatOfPoint2f mat2_corner = new MatOfPoint2f();
	    
//	    System.out.println(status.height());
//	    System.out.println(status.width());
		for (int i = 0; i != status.height(); i++) {
//			System.out.println(status.get(i, 0).length);
//			System.out.println(status.get(i, 0)[0]);
			if (status.get(i, 0)[0] > 0.5) { // > 0.5 <=> != 0
				double[] temp = matOfPoint2f1.get(i, 0);
				Point tempPoint = new Point(temp);
				MatOfPoint2f tempMat = new MatOfPoint2f(tempPoint);
				mat1_corner.push_back(tempMat);
				temp = matOfPoint2f2.get(i, 0);
				tempPoint = new Point(temp);
				tempMat = new MatOfPoint2f(tempPoint);
				mat2_corner.push_back(tempMat);
				
			}
			

		}
//		System.out.println(matOfPoint2f1.dump());
//		System.out.println(matOfPoint2f2.dump());
//		System.out.println(mat1_corner.dump());
//		System.out.println(mat2_corner.dump());
	    Mat T = Video.estimateRigidTransform(mat1_corner, mat2_corner, false);
//		Mat T = Video.estimateRigidTransform(matOfPoint2f1, matOfPoint2f2, false);
//	    System.out.println(T.width());
//	    System.out.println(T.height());
//	    System.out.println(T.get(0,2)[0]);
	    double dx, dy, da;
	    if(T.width() == 3 && T.height() == 2){
	    	dx = T.get(0, 2)[0];
	    	dy = T.get(1, 2)[0];
	    	da = Math.atan2(T.get(1, 0)[0], T.get(0, 0)[0]);
	    }
	    else{
	    	dx = 0;
	    	dy = 0;
	    	da =0;
	    }
	    
	    FrameTransformation frameTransformation = new FrameTransformation(dx, dy, da);
	    
	    System.out.println(frameTransformation);
	    return frameTransformation;
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
