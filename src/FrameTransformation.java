public class FrameTransformation {
	private double dx;
	private double dy;
	private double da;

	public double getDx() {
		return dx;
	}

	public void setDx(double dx) {
		this.dx = dx;
	}

	public double getDy() {
		return dy;
	}

	public void setDy(double dy) {
		this.dy = dy;
	}

	public double getDa() {
		return da;
	}

	public void setDa(double da) {
		this.da = da;
	}

	public FrameTransformation(double dx, double dy, double da) {
		super();
		this.dx = dx;
		this.dy = dy;
		this.da = da;
	}

	@Override
	public String toString() {
		return "FrameTransformation [dx=" + dx + ", dy=" + dy + ", da=" + da
				+ "]";
	}

}
