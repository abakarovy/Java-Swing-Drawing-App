package com.maga;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;

public class DrawingApp {
    
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                System.out.println(System.getProperty("java.class.path"));

                ImageIcon pencilIcon = new ImageIcon(getClass().getClassLoader().getResource("assets/pencil.png"));
                ImageIcon eraserIcon = new ImageIcon(getClass().getClassLoader().getResource("assets/eraser.png"));
                ImageIcon fillBucketIcon = new ImageIcon(getClass().getClassLoader().getResource("assets/fillBucket.png"));

                JFrame frame = new JFrame("Drawing App by maga");
                frame.setIconImage(pencilIcon.getImage());;
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);
        
                DrawingPanel drawingPanel = new DrawingPanel(1600, 1200);
                JScrollPane scrollPane = new JScrollPane(drawingPanel); // Scrollable canvas
                scrollPane.getVerticalScrollBar().setUnitIncrement(20);
                scrollPane.getHorizontalScrollBar().setUnitIncrement(20);

                scrollPane.addMouseWheelListener(e -> {
                    if (e.isControlDown()) {
                        if (e.getPreciseWheelRotation() < 0) {
                            drawingPanel.zoomIn();
                        } else {
                            drawingPanel.zoomOut();
                        }
                        e.consume(); // Prevent scrolling while zooming
                    } else {
                        // Allow scrolling when Ctrl is not pressed
                        scrollPane.dispatchEvent(e);
                    }
                });
        
                // Button panel
                JToolBar buttonPanel = new JToolBar();
                buttonPanel.setLayout(new GridBagLayout());
    
                JButton pencilButton = new JButton("Pencil");
                pencilButton.setIcon(new ImageIcon(pencilIcon.getImage().getScaledInstance(30, 30, 0)));
                pencilButton.addActionListener(e -> drawingPanel.selectedTool = Tool.PENCIL);
                
                JButton eraserButton = new JButton("Eraser");
                eraserButton.setIcon(new ImageIcon(eraserIcon.getImage().getScaledInstance(30, 30, 0)));
                eraserButton.addActionListener(e -> drawingPanel.selectedTool = Tool.ERASER);
                
                JButton fillBucketButton = new JButton("Fill Bucket");
                fillBucketButton.setIcon(new ImageIcon(fillBucketIcon.getImage().getScaledInstance(30, 30, 0)));
                fillBucketButton.addActionListener(e -> drawingPanel.selectedTool = Tool.FILL_BUCKET);
                
                JButton clearButton = new JButton("Clear");
                clearButton.addActionListener(e -> {
                    drawingPanel.clearCanvas();
                    drawingPanel.repaint();
                });
        
                JButton colorPickerButton = new JButton();
                colorPickerButton.setBackground(drawingPanel.currentColor);
                colorPickerButton.setPreferredSize(new Dimension(30, 30));
                colorPickerButton.addActionListener(e -> {
                    Color newColor = JColorChooser.showDialog(frame, "Select Brush Color", drawingPanel.currentColor);
                    if (newColor != null) {
                        drawingPanel.currentColor = newColor;
                        colorPickerButton.setBackground(newColor);
                    }
                });
        
                JSlider brushSizeSlider = new JSlider();
                brushSizeSlider.setValue(drawingPanel.brushSize);
                brushSizeSlider.setMinimum(5);
                brushSizeSlider.setBorder(new TitledBorder("Brush Size"));
                brushSizeSlider.setMaximumSize(new Dimension(200, 40));
                brushSizeSlider.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        JSlider slider = (JSlider) e.getSource();
                        drawingPanel.brushSize = slider.getValue();
                    }
                });
        
                JButton saveButton = new JButton("Save");
                saveButton.addActionListener(e -> drawingPanel.saveCanvas(frame));
        

                buttonPanel.add(colorPickerButton);
                buttonPanel.add(pencilButton);
                buttonPanel.add(eraserButton);
                buttonPanel.add(fillBucketButton);
                buttonPanel.add(brushSizeSlider);
                buttonPanel.add(clearButton);        
                buttonPanel.add(saveButton);

                // Layout: scroll pane on top, buttons below
                frame.setLayout(new BorderLayout());
                frame.add(scrollPane, BorderLayout.CENTER);
                frame.add(buttonPanel, BorderLayout.SOUTH);
        
                frame.setVisible(true);
            }
            
        });
    }
}