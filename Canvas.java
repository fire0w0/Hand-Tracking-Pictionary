import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.awt.Graphics2D;


public class Canvas extends JPanel{


    private final static int STROKE_SIZE = 8; // CHANGE STROKE SIZE HERE


    // used to draw a line between points
    private List<ColorPoint> currentPath;


    //color of the dots
    private Color color;
    // location of the dots
    private int x, y;
    // canvas width and height
    private int canvasWidth, canvasHeight;


    public Canvas(int targetWidth, int targetHeight){
        super();
        setPreferredSize(new Dimension(targetWidth, targetHeight));
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));


        // init vars
        canvasWidth = targetWidth;
        canvasHeight = targetHeight;


        MouseAdapter na = new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                // get current mouse lcation
                x = e.getX();
                y = e.getY();


                //draw in current mouse location
                Graphics g = getGraphics();
                g.setColor(color);
                g.fillRect(x, y, STROKE_SIZE, STROKE_SIZE);
                g.dispose();


                // start current path
                currentPath = new ArrayList<>(25);
                currentPath.add(new ColorPoint(x, y, color));


            }


            @Override
            public void mouseReleased(MouseEvent e) {
                // reset the current path
                currentPath = null;
            }


            @Override
            public void mouseDragged(MouseEvent e) {
                // get current location
                x = e.getX();
                y = e.getY();


                // used to be able to draw a line
                Graphics2D g2d = (Graphics2D) getGraphics();
                g2d.setColor(color);
                if(!currentPath.isEmpty()){
                    ColorPoint prevPoint = currentPath.get(currentPath.size() - 1);
                    g2d.setStroke(new BasicStroke(STROKE_SIZE));


                    // connect the current point to the previous point to draw a line
                    g2d.drawLine(prevPoint.getX(), prevPoint.getY(), x, y);


                }
                g2d.dispose();


                // add the new point to the path
                ColorPoint nextPoint = new ColorPoint(e.getX(), e.getY(), color);
                currentPath.add(nextPoint);
            }
        };


        addMouseListener(na);
        addMouseMotionListener(na);


    }


    public void setColor(Color color){
        this.color = color;
    }


    public void resetCanvas(){
        // clear all rectangles
        Graphics g = getGraphics();
        g.clearRect(0, 0, canvasWidth, canvasHeight);
        g.dispose();


        // reset the path
        currentPath = null;


        repaint();
        revalidate();
    }


}





