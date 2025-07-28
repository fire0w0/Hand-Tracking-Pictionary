import java.awt.*;
import java.awt.image.BufferedImage;
import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class VideoHand{
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
    private boolean tracking = false;

    public VideoHand() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Width = screenSize.width;
        Length = screenSize.height;
        new Thread(this::run).start();
    }

    public void toggletracker(){
        tracking = !tracking;
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


    public void run() {
        capture = new VideoCapture(whichCamera);
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, Width);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, Length);

        detector = new HandDetector("gloveHSV.txt", Width, Length, Main.videoHand);
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


            if (tracking) {
                try {
                    Robot robot = new Robot();

                    robot.mouseMove(imageX, imageY);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }

            binaryBufferedImage = matToBufferedImage(detector.getBinaryImage());


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