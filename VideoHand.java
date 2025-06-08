import java.awt.*;
import javax.swing.*;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.IplImage;

public class VideoHand extends JPanel implements Runnable {
    private static final int Width = 640;
    private static final int Length = 480;
    private static final int timebetween = 100;
    private static final int whichCamera = 0;
    private Frame snapIm = null;
    private volatile boolean isRunning;
    private volatile boolean isFinished;
    private int imageCount = 0;
    private long totalTime = 0;
    private Font msgFont = new Font("SansSerif", Font.PLAIN, 12);

    private HandDetector handfinder = null;

    public VideoHand() {
        setBackground(Color.white);
        setPreferredSize(new Dimension(Width, Length));
        setLayout(new BorderLayout());
        new Thread(this).start();

    }

    private FrameGrabber startGrabber(int camera) throws FrameGrabber.Exception {
        FrameGrabber grabber = null;
        try {
            grabber = FrameGrabber.createDefault(camera);
        } catch (FrameGrabber.Exception e) {
            System.err.println("Could not create FrameGrabber instance.");
            throw new RuntimeException(e);
        }
        grabber.setFormat("dshow");       // using DirectShow
        grabber.setImageWidth(WIDTH);     // default is too small: 320x240
        grabber.setImageHeight(HEIGHT);
        grabber.start();
        return grabber;
    }

    private Frame getSnapImage(FrameGrabber grabber, int ID) {
        Frame snap = null;
        try {
            snap = grabber.grab();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
        return snap;
    }

    private void closeGrabber(FrameGrabber grabber, int ID) {
        try {
            grabber.stop();
            grabber.release();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
    }



    /* gpt worte this part just temporary to see if stuff works*/
        @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (snapIm != null) {
            // Convert JavaCV Frame to Java Image
            Java2DFrameConverter converter = new Java2DFrameConverter();
            Image img = converter.getBufferedImage(snapIm);

            if (img != null) {
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        } else {
            // Draw a "No Image" message if there's nothing to display yet
            g.setColor(Color.BLACK);
            g.setFont(msgFont);
            g.drawString("No image", 10, 20);
        }
    }


    /* insert display stuff here when doable*/

    public void run() {
        FrameGrabber videograbber = null;
        try {
            videograbber = startGrabber(whichCamera);
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
        /* insert detector stuff here*/
        long duration;
        isRunning = true;
        isFinished = false;
        while (isRunning) {
            long startTime = System.currentTimeMillis();
            snapIm = getSnapImage(videograbber, whichCamera);
            imageCount++;
            repaint();

            /* insert more handdetector stuff here */
            duration = System.currentTimeMillis() - startTime;
            totalTime += duration;
            if (duration < timebetween){
                try {
                    Thread.sleep(timebetween-duration);  // wait until DELAY time has passed
                }
                catch (Exception ex) {}
            }
        }
        closeGrabber(videograbber, whichCamera);
        isFinished = true;
    }





    public void closeDown()
    {
        isRunning = false;
        while (!isFinished) {
            try {
                Thread.sleep(timebetween);
            }
            catch (Exception ex) {}
        }
    }


} // end of HandPanel class