import java.awt.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;


public class HandDetector {
    private static final int Scale = 2;
    private static final float SmallestArea = 400.0f;
    private final int smoothingWindow = 10;
    private final List<Point> recentCenters = new ArrayList<>();
    public float maxArea;
    private Mat scaledImage;
    private Mat recolouredImage;
    private Mat binaryImage;

    private Font msgFont;
    Point center;

    public HandDetector(String Colour, int width, int height) {
        scaledImage = new Mat(height / Scale, width / Scale, org.opencv.core.CvType.CV_8UC3);
        recolouredImage = new Mat(height / Scale, width / Scale, org.opencv.core.CvType.CV_8UC3);
        binaryImage = new Mat(height / Scale, width / Scale, org.opencv.core.CvType.CV_8UC1);


        msgFont = new Font("SansSerif", Font.BOLD, 18);
        center = new Point(0,0);
    }

    private Point getSmoothedCenter(Point newCenter) {
        recentCenters.add(new Point(newCenter));
        if (Math.abs(recentCenters.getLast().x - newCenter.x) > 500  && Math.abs(recentCenters.getLast().y - newCenter.y) > 500) {
            return recentCenters.getLast();
        }
        if (recentCenters.size() > smoothingWindow) {
            recentCenters.remove(0);
        }
        int sumX = 0, sumY = 0;
        for (Point p : recentCenters) {
            sumX += p.x;
            sumY += p.y;
        }
        return new Point(sumX / recentCenters.size(), sumY / recentCenters.size());
    }


    public MatOfPoint findBiggestContour (Mat binaryImage) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint largestContour = null;
        maxArea = SmallestArea;
        for (MatOfPoint contour : contours) {
            RotatedRect box = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
            Size size = box.size;
            float area = (float)(size.width * size.height);
            if (area > maxArea) {
                maxArea = area;
                largestContour = contour;
            }
        }
        return largestContour;
    }

    public Mat getBinaryImage() {
        return binaryImage;
    }

    private void extractContourInfo(MatOfPoint bigCountour, int
            scale) {
        Rect boundingRect = Imgproc.boundingRect(bigCountour);
        int xCenter = (boundingRect.x + boundingRect.width / 2) * scale;
        int yCenter = (boundingRect.y + boundingRect.height / 2) * scale;
        center.setLocation(xCenter, yCenter);
    }

    public void detect(Mat inputImage) {
        Imgproc.resize(inputImage, scaledImage, scaledImage.size());
        Imgproc.cvtColor(scaledImage, recolouredImage, Imgproc.COLOR_BGR2HSV);
        Core.inRange(recolouredImage,
                new Scalar(0, 0, 0),
                new Scalar(179, 255, 80),
                binaryImage);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5));
        Imgproc.morphologyEx(binaryImage, binaryImage, Imgproc.MORPH_OPEN, kernel);
        MatOfPoint bigContour = findBiggestContour(binaryImage);
        if (bigContour == null)
            return;
        extractContourInfo(bigContour, Scale);


        center = getSmoothedCenter(center);

    }

    public void draw(Graphics g, float scaleX, float scaleY) {
        g.setColor(Color.GREEN);
        // scale the center coordinates to match displayed image size
        int drawX = (int) (center.x * scaleX);
        int drawY = (int) (center.y * scaleY);
        g.fillOval(drawX - 8, drawY - 8, 16, 16);
    }




}
