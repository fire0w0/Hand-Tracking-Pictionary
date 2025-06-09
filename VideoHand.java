import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.Videoio;

public class VideoHand extends JPanel {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private static final int Width = 640;
    private static final int Length = 480;
    private static final int timebetween = 100;
    private static final int whichCamera = 0;
    private Mat snapMat = new Mat();
    private BufferedImage bufferedImage = null;
    private volatile boolean isRunning;
    private volatile boolean isFinished;
    private int imageCount = 0;
    private long totalTime = 0;
    private Font msgFont = new Font("SansSerif", Font.PLAIN, 12);
    private VideoCapture capture;


    public VideoHand() {
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
                g.drawImage(bufferedImage, 0, 0, getWidth(), getHeight(), this);
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

        long duration;
        isRunning = true;
        isFinished = false;

        while (isRunning) {
            long startTime = System.currentTimeMillis();
            boolean grabbed = capture.read(snapMat);
            if (grabbed) {
                bufferedImage = matToBufferedImage(snapMat);
            }
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