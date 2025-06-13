import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class PaintGui extends JFrame {
    private final VideoHand videoHand;

    public PaintGui(VideoHand videoHand){
        super("Paint GUI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.videoHand = videoHand;
        setPreferredSize(new Dimension(1500, 1000));
        pack();
        setLocationRelativeTo(null);
        addGuiComponents();
    }


    private void addGuiComponents(){
        //JPANEL configs
        JPanel canvasPanel = new JPanel();
        SpringLayout springLayout = new SpringLayout();
        canvasPanel.setLayout(springLayout);


        // 1. Canvas
        Canvas canvas = new Canvas(1500, 950);
        canvasPanel.add(canvas);
        springLayout.putConstraint(SpringLayout.NORTH, canvas, 50, SpringLayout.NORTH, canvasPanel);


        // 2. Color Chooser

        JComponent root = canvas; // or your canvas

        InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = root.getActionMap();

        //final boolean[] colorChooserOpen = {false};

        JColorChooser colorChooser = new JColorChooser();
        JDialog dialog = JColorChooser.createDialog(
                canvas,                 // parent component
                "Choose a Color",     // dialog title
                false,                 // modal
                colorChooser,         // color chooser component
                e -> {
                    Color c = colorChooser.getColor();
                    if(c != null) {
                        canvas.setColor(c);  // set color of brush
                    }

                },
                null                  // Cancel listener (can also add one)
        );

        inputMap.put(KeyStroke.getKeyStroke('P'), "openColorPicker");
        actionMap.put("openColorPicker", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (dialog.isVisible()) {
                    dialog.setVisible(false);
                    return;
                }

                dialog.setVisible(true);
                /*else if(dialog.isShowing()){
                    dialog.dispose();
                    //colorChooserOpen[0] = false;
                    System.out.println("this should say false: " + dialog.isShowing());
                }*/
            }
        });

        inputMap.put(KeyStroke.getKeyStroke('T'), "openColorPicker");
        actionMap.put("openColorPicker", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                videoHand.toggletracker();
            }
        });

        // Close Paint Dialog
        inputMap.put(KeyStroke.getKeyStroke('O'), "closeDialog");


        actionMap.put("closeDialog", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                    System.out.println("dialog setvisibled to false");
            }
        });



        JButton chooseColorButton = new JButton("Choose color");
        chooseColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(null, "Select a color", Color.BLACK);
                chooseColorButton.setBackground(c);
                canvas.setColor(c);
            }
        });
        canvasPanel.add(chooseColorButton);
        springLayout.putConstraint(SpringLayout.NORTH, chooseColorButton, 10, SpringLayout.NORTH, canvasPanel);
        springLayout.putConstraint(SpringLayout.WEST, chooseColorButton, 25, SpringLayout.WEST, canvasPanel);




        // 3. Reset Button

        inputMap.put(KeyStroke.getKeyStroke('R'), "reset");


        actionMap.put("reset", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.resetCanvas();
            }
        });

        JButton resetButton= new JButton("Reset");
        resetButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                canvas.resetCanvas();
            }
        });
        canvasPanel.add(resetButton);
        //springLayout.putConstraint(SpringLayout.NORTH, resetButton, 10, SpringLayout.NORTH, canvasPanel);
        //springLayout.putConstraint(SpringLayout.WEST, resetButton, 150, SpringLayout.WEST, canvasPanel);
        springLayout.putConstraint(SpringLayout.NORTH, resetButton, 10, SpringLayout.NORTH, canvasPanel);
        springLayout.putConstraint(SpringLayout.WEST, resetButton, 275, SpringLayout.WEST, canvasPanel);

        // Description for which shortcuts to do stuff

        JLabel instructionsLabel = new JLabel("Shift + D to start and stop drawing \n Shift + R to reset the canvas \n Shift + P to open color picker");
        canvasPanel.add(instructionsLabel);
        springLayout.putConstraint(SpringLayout.NORTH, instructionsLabel, 10, SpringLayout.NORTH, canvasPanel);
        springLayout.putConstraint(SpringLayout.WEST, instructionsLabel, 525, SpringLayout.WEST, canvasPanel);



        this.getContentPane().add(canvasPanel);
    }
}
