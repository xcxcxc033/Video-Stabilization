import java.awt.image.BufferedImage;


public interface EvaluateFrameTransformation {
	public FrameTransformation evaluateMotionBetweenImage(BufferedImage img1, BufferedImage img2);
}
