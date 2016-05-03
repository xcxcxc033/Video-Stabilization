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

	private int maxKeyPoint = 200;
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
	    Imgproc.goodFeaturesToTrack(mat1, matOfPoint1, maxKeyPoint, qualityLevelInImage, minDistanceInImage);

	    MatOfByte status = new MatOfByte();
	    MatOfFloat err = new MatOfFloat();
	    MatOfPoint2f matOfPoint2f1 = new MatOfPoint2f(matOfPoint1.toArray());
	    MatOfPoint2f matOfPoint2f2 = new MatOfPoint2f();
	    Video.calcOpticalFlowPyrLK(mat1, mat2, matOfPoint2f1, matOfPoint2f2, status, err);
	
    
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
	    Mat transform = Video.estimateRigidTransform(mat1_corner, mat2_corner, false);

	    double dx, dy, da;
	    if(transform.width() == 3 && transform.height() == 2){
	    	dx = transform.get(0, 2)[0];
	    	dy = transform.get(1, 2)[0];
	    	da = Math.atan2(transform.get(1, 0)[0], transform.get(0, 0)[0]);
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
	
	private BufferedImage transformMatToBufferedImage(Mat mat){
		BufferedImage img = new BufferedImage(mat.height(), mat.width(), BufferedImage.TYPE_INT_RGB);
		System.out.println(mat);
		System.out.println(mat.width());
		System.out.println(mat.height());
		
		for(int i =0; i < img.getWidth(); i++){
			for(int j = 0; j < img.getHeight(); j++){
				double[] temp = mat.get(i, j);
//				System.out.println(temp);
//				System.out.println(temp.length);
				int red = (int) temp[0];
				int green = (int) temp[1];
				int blue = (int) temp[2];
				int rgb = 0xff000000 | (red & 0xff) << 16 | (green& 0xff) << 8 | (blue & 0xff);
				img.setRGB(i, j, rgb);
			}
		}
	     
	    return img; 
	}
	@Override
	public BufferedImage applyTransformToImage(BufferedImage img,
			FrameTransformation frameTransformation) {
		Mat transform = new Mat(2,3, CvType.CV_64F);
		
		transform.put(0, 0, Math.cos(frameTransformation.getDa()));
		transform.put(0, 1, -Math.sin(frameTransformation.getDa()));
		transform.put(1, 0, Math.sin(frameTransformation.getDa()));
		transform.put(1, 1, Math.cos(frameTransformation.getDa()));
		
		transform.put(0, 2, frameTransformation.getDx());
		transform.put(1, 2, frameTransformation.getDy());

		Mat cur = transformBufferedImageToMat(img);
		Mat cur2 = new Mat();
		Imgproc.warpAffine(cur, cur2, transform, cur.size());
		System.out.println(cur2.width());
		System.out.println(cur.height());
		System.out.println(cur2);
		
		return transformMatToBufferedImage(cur2);
	}

}
