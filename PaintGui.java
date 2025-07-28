import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;


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
        springLayout.putConstraint(SpringLayout.NORTH, canvas, 100, SpringLayout.NORTH, canvasPanel);
        springLayout.putConstraint(SpringLayout.WEST, canvas, 100, SpringLayout.WEST, canvasPanel);
        springLayout.putConstraint(SpringLayout.EAST, canvas, -100, SpringLayout.EAST, canvasPanel);
        springLayout.putConstraint(SpringLayout.SOUTH, canvas, -100, SpringLayout.SOUTH, canvasPanel);


        // 2. Color Chooser

        JComponent root = canvas; // or your canvas

        InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = root.getActionMap();

        //final boolean[] colorChooserOpen = {false};

        /*JColorChooser colorChooser = new JColorChooser();
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
        );*/

        inputMap.put(KeyStroke.getKeyStroke('-'), "increase");
        actionMap.put("increase", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.decrease();

            }
        });


        inputMap.put(KeyStroke.getKeyStroke('+'), "decrease");
        actionMap.put("decrease", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.increase();

            }
        });



        inputMap.put(KeyStroke.getKeyStroke('T'), "starttracking");
        actionMap.put("starttracking", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                videoHand.toggletracker();
            }
        });

        ArrayList<String> prompts = new ArrayList<>();

        prompts.add("Airplane");
        prompts.add("Elephant");
        prompts.add("Pineapple");
        prompts.add("Robot");
        prompts.add("Volcano");
        prompts.add("Pirate");
        prompts.add("Snowman");
        prompts.add("Guitar");
        prompts.add("Castle");
        prompts.add("Rocket");
        prompts.add("Mr. Dutton");

        Random random = new Random();

        JLabel pictionaryLabel = new JLabel("Press Shift + G to generate a pictionary prompt (it will disappear after a 2 seconds)");
        canvasPanel.add(pictionaryLabel);
        springLayout.putConstraint(SpringLayout.NORTH, pictionaryLabel, 50, SpringLayout.NORTH, canvasPanel);
        springLayout.putConstraint(SpringLayout.WEST, pictionaryLabel, 380, SpringLayout.WEST, canvasPanel);
        inputMap.put(KeyStroke.getKeyStroke('G'), "generatePrompt");
        actionMap.put("generatePrompt", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pictionaryLabel.setText(prompts.get(random.nextInt(12)));

                Timer clearTimer = new Timer(2000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        pictionaryLabel.setText("Press Shift + G to generate a pictionary prompt (it will disappear after a 2 seconds)"); // Clear the label text
                    }
                });

                clearTimer.setRepeats(false); // Make sure it only runs once
                clearTimer.start();


            }
        });

        // Close Paint Dialog
        /*inputMap.put(KeyStroke.getKeyStroke('O'), "closeDialog");


        actionMap.put("closeDialog", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                    System.out.println("dialog setvisibled to false");
            }
        });*/



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
        springLayout.putConstraint(SpringLayout.WEST, resetButton, 200, SpringLayout.WEST, canvasPanel);

        // Description for which shortcuts to do stuff

        JLabel instructionsLabel = new JLabel("Shift + D to start and stop drawing, Shift + T to start tracking, + / - to increase / decrease stroke size");
        canvasPanel.add(instructionsLabel);
        springLayout.putConstraint(SpringLayout.NORTH, instructionsLabel, 10, SpringLayout.NORTH, canvasPanel);
        springLayout.putConstraint(SpringLayout.WEST, instructionsLabel, 380, SpringLayout.WEST, canvasPanel);



        this.getContentPane().add(canvasPanel);
    }
}
