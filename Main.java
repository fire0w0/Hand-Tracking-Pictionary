import java.awt.*;
import javax.swing.*;

public class Main{
    public static VideoHand videoHand;
    public Main() {
        videoHand = new VideoHand();
    }

    public static void main(String[] args) throws AWTException {
        new Main(); // this creates videoHand
        SwingUtilities.invokeLater(new Runnable() {
            @Override

            public void run() {

                new PaintGui(videoHand).setVisible(true);
            }
        });
    }
}
