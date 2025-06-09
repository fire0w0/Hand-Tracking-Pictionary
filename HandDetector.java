import java.awt.Font;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;


public class HandDetector {
    private static final int Scale = 2;
    private static final float SmallestArea = 600.0f;
    private static final int Max_Points = 20;
    private static final int Min_Finger_Depth = 20;
    private static final int Max_Finger_Depth = 60;
    private static final int Min_Thumb = 120;
    private static final int Max_Thumb = 200;
    private static final int Min_Index = 60;
    private static final int Max_Thumb_2 = 120;

    private int hueLower, hueUpper, satLower, satUpper, valLower, valUpper;

    private Mat scaledImage;
    private Mat recolouredImage;
    private Mat binaryImage;

    private Font msgFont;

    private Point center;
    private int amount_rotated;

    private Point[] Fingertips, ValleyPoints;
    private float[] tip_to_fold;
    private ArrayList<Point> fingertips_but_better;

    private ArrayList<FingerLabels> fingertips_better;

    public HandDetector(String Colour, int width, int height) {
        scaledImage = new Mat(height / Scale, width / Scale, org.opencv.core.CvType.CV_8UC3);
        recolouredImage = new Mat(height / Scale, width / Scale, org.opencv.core.CvType.CV_8UC3);
        binaryImage = new Mat(height / Scale, width / Scale, org.opencv.core.CvType.CV_8UC1);


        msgFont = new Font("SansSerif", Font.BOLD, 18);
        center = new Point(0,0);
        amount_rotated = 0;
        fingertips_but_better = new ArrayList<>();
        fingertips_better = new ArrayList<>();

        Fingertips = new Point[Max_Points];
        ValleyPoints = new Point[Max_Points];
        tip_to_fold = new float[Max_Points];

        setHSVRanges(Colour);
    }

    private void setHSVRanges(String Colour) {
         try {
             BufferedReader reader = new BufferedReader(new FileReader(Colour));

             String line = reader.readLine();
             String[] values = line.split("\\s+");
             hueLower = Integer.parseInt(values[0]);
             hueUpper = Integer.parseInt(values[1]);

             line = reader.readLine();
             values = line.split("\\s+");
             satLower = Integer.parseInt(values[0]);
             satUpper = Integer.parseInt(values[1]);

             line = reader.readLine();
             values = line.split("\\s+");
             valLower = Integer.parseInt(values[0]);
             valUpper = Integer.parseInt(values[1]);

             reader.close();
         }
         catch (Exception e){};
    }

    public MatOfPoint findBiggestContour (Mat binaryImage) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint largestContour = null;
        float maxArea = SmallestArea;
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

    private void extractContourInfo(MatOfPoint bigCountour, int scale) {
        Moments moments = Imgproc.moments(bigCountour);

        double m00 = moments.get_m00();
        double m10 = moments.get_m10();
        double m01 = moments.get_m01();

        if (m00 != 0) {
            int xCenter = (int) Math.round((m10 / m00) * scale);
            int yCenter = (int) Math.round((m01 / m00) * scale);
            center.setLocation(xCenter, yCenter);
        }

        double m11 = moments.get_mu11();
        double m20 = moments.get_mu20();
        double m02 = moments.get_mu02();

        amount_rotated = calculateTilt(m11, m20, m02);

        if (fingertips_but_better != null && !fingertips_but_better.isEmpty()) {
            int yTotal = 0;
            for (Point p : fingertips_but_better){
                yTotal += p.y;

            }
            int avgYFinger = yTotal / fingertips_but_better.size();
            if (avgYFinger > center.y) {
                amount_rotated += 180;
            }
    }
        amount_rotated = 180 - amount_rotated;

    }

    private int calculateTilt(double x1, double y1, double x2) {
            double diff = y1 - x2;
            if (diff == 0) {
                if (x1 == 0)
                    return 0;
                else if (x1 > 0)
                    return 45;
                else   // m11 < 0
                    return -45;
            }

            double theta = 0.5 * Math.atan2(2*x1, diff);
            int tilt = (int) Math.round( Math.toDegrees(theta));

            if ((diff > 0) && (x1 == 0))
                return 0;
            else if ((diff < 0) && (x1 == 0))
                return -90;
            else if ((diff > 0) && (x1 > 0))  // 0 to 45 degrees
                return tilt;
            else if ((diff > 0) && (x1 < 0))  // -45 to 0
                return (180 + tilt);   // change to counter-clockwise angle measure
            else if ((diff < 0) && (x1 > 0))   // 45 to 90
                return tilt;
            else if ((diff < 0) && (x1 < 0))   // -90 to -45
                return (180 + tilt);  // change to counter-clockwise angle measure

            System.out.println("Error in moments for tilt angle");
            return 0;
    }  // end of calculateTilt()


    public void detect(Mat inputImage) {
        Imgproc.resize(inputImage, scaledImage, scaledImage.size());
        Imgproc.cvtColor(scaledImage, recolouredImage, Imgproc.COLOR_BGR2HSV);
        Core.inRange(recolouredImage,
                new Scalar(hueLower, satLower, valLower),
                new Scalar(hueUpper, satUpper, valUpper),
                binaryImage);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1,1));
        Imgproc.morphologyEx(binaryImage, binaryImage, Imgproc.MORPH_OPEN, kernel);
        MatOfPoint bigContour = findBiggestContour(binaryImage);
        if (bigContour == null)
            return;
        extractContourInfo(bigContour, Scale);





    }




}
