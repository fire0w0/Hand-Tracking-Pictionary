import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main extends JFrame {





    public VideoHand videoHand;
    public Main() {
        super("Hand Tracker");
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        videoHand = new VideoHand();
        c.add(videoHand, BorderLayout.CENTER);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}});
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) throws AWTException {
        Main mainWindow = new Main(); // this creates videoHand
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PaintGui(mainWindow.videoHand).setVisible(true);
            }
        });
    }
}
