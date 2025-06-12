import java.awt.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.*;
import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.Videoio;

public class VideoHand extends JPanel {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private static int Width;
    private static int Length;
    private static final int timebetween = 1;
    private static final int whichCamera = 0;
    private Mat snapMat = new Mat();
    private BufferedImage bufferedImage = null;
    private volatile boolean isRunning;
    private volatile boolean isFinished;
    private long totalTime = 0;
    private Font msgFont = new Font("SansSerif", Font.PLAIN, 12);
    private VideoCapture capture;
    private HandDetector detector = null;
    private BufferedImage binaryBufferedImage = null;

    public VideoHand() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Width = screenSize.width;
        Length = screenSize.height;

        setBackground(Color.white);
        setPreferredSize(new Dimension(Width, Length));
        setLayout(new BorderLayout());
        new Thread(this::run).start();
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int buffersize = mat.channels()*mat.cols()*mat.rows();
        byte[] data = new byte[buffersize];
        mat.get(0, 0, data);
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        final byte[] targetPixels = ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(data, 0, targetPixels, 0, data.length);
        return image;
    }


    /* gpt worte this part just temporary to see if stuff works*/
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bufferedImage != null) {
                int panelWidth = getWidth();
                int panelHeight = getHeight();

                // Draw the image stretched to fit panel size
                g.drawImage(bufferedImage, 0, 0, panelWidth, panelHeight, this);

                if (detector != null) {
                    // Calculate scale factors between displayed size and image size
                    float scaleX = (float) panelWidth / bufferedImage.getWidth();
                    float scaleY = (float) panelHeight / bufferedImage.getHeight();

                    // Pass scale factors to draw method
                    detector.draw(g, scaleX, scaleY);
                }
            } else {
                g.setColor(Color.BLACK);
                g.setFont(msgFont);
                g.drawString("No image", 10, 20);
            }
        }



    /* insert display stuff here when doable*/

    public void run() {
        capture = new VideoCapture(whichCamera);
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, Width);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, Length);

        detector = new HandDetector("gloveHSV.txt", Width, Length);
        long duration;
        isRunning = true;
        isFinished = false;

        while (isRunning) {
            long startTime = System.currentTimeMillis();
            boolean grabbed = capture.read(snapMat);
            Core.flip(snapMat, snapMat, 1);
            if (grabbed) {
                bufferedImage = matToBufferedImage(snapMat);
            }
            detector.detect(snapMat);

            // After detector.detect(snapMat);
            int imageX = detector.center.x;
            int imageY = detector.center.y;

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            float scaleX = (float) panelWidth / bufferedImage.getWidth();
            float scaleY = (float) panelHeight / bufferedImage.getHeight();

            int panelX = (int)(imageX * scaleX);
            int panelY = (int)(imageY * scaleY);

            try {
                Robot robot = new Robot();
                Point panelOnScreen = this.getLocationOnScreen();
                int screenX = panelOnScreen.x + panelX;
                int screenY = panelOnScreen.y + panelY;

                robot.mouseMove(screenX, screenY);
            } catch (AWTException e) {
                e.printStackTrace();
            }

            binaryBufferedImage = matToBufferedImage(detector.getBinaryImage());
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
        capture.release();
        isRunning = false;

    }


    public void closeDown() {
        isRunning = false;
        while (!isFinished) {
            try {
                Thread.sleep(timebetween);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }


} // end of HandPanel class