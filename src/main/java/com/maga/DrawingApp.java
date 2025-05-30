package com.maga;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.maga.model.Tool;
import com.maga.view.DrawingPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DrawingApp extends JFrame {
    private DrawingPanel drawingPanel;
    private JToolBar toolBar;

    public DrawingApp(int canvasWidth, int canvasHeight) {
        ImageIcon pencilIcon = new ImageIcon(getClass().getClassLoader().getResource("assets/pencil.png"));
        ImageIcon eraserIcon = new ImageIcon(getClass().getClassLoader().getResource("assets/eraser.png"));
        ImageIcon fillBucketIcon = new ImageIcon(getClass().getClassLoader().getResource("assets/fillBucket.png"));

        setTitle("Drawing App by maga");
        setIconImage(pencilIcon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(canvasWidth + 100, canvasHeight + 100);


        drawingPanel = new DrawingPanel(canvasWidth, canvasHeight);
        JScrollPane scrollPane = new JScrollPane(drawingPanel); // makes canvas scrollable
        scrollPane.setAutoscrolls(true);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        scrollPane.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                if (e.getPreciseWheelRotation() < 0) {
                    drawingPanel.zoomIn();
                } else {
                    drawingPanel.zoomOut();
                }
                // TODO: the scroll pane doesnt set the view position to where the mouse is positioned,
                //       it does at the beginning and then it zooms into the top left corner for some reason
                scrollPane.getViewport().setViewPosition(getMousePosition());
                e.consume(); // prevents scrolling while zooming
            } else {
                // TODO: for some reason when you zoom in, the scroll bar (the vertical one) is still scrolling,
                //       its hardly noticeable but pretty sure it happens
                scrollPane.dispatchEvent(e);
            }
        });

        
        toolBar = new JToolBar();
        toolBar.setLayout(new GridBagLayout());
        
        JButton pencilButton = new JButton("Pencil");
        pencilButton.setIcon(new ImageIcon(pencilIcon.getImage().getScaledInstance(30, 30, 0)));
        pencilButton.addActionListener(e -> drawingPanel.setTool(Tool.PENCIL));
        
        JButton eraserButton = new JButton("Eraser");
        eraserButton.setIcon(new ImageIcon(eraserIcon.getImage().getScaledInstance(30, 30, 0)));
        eraserButton.addActionListener(e -> drawingPanel.setTool(Tool.ERASER));
        
        JButton fillButton = new JButton("Fill Bucket");
        fillButton.setIcon(new ImageIcon(fillBucketIcon.getImage().getScaledInstance(30, 30, 0)));
        fillButton.addActionListener(e -> drawingPanel.setTool(Tool.FILL_BUCKET));
        
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            drawingPanel.clearCanvas();
            drawingPanel.repaint();
        });
        
        JButton colorPickerButton = new JButton();
        colorPickerButton.setBackground(drawingPanel.currentColor);
        colorPickerButton.setPreferredSize(new Dimension(30, 30));
        colorPickerButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Select Brush Color", drawingPanel.currentColor);
            if (newColor != null) {
                drawingPanel.currentColor = newColor;
                colorPickerButton.setBackground(newColor);
            }
        });
        
        JSlider brushSizeSlider = new JSlider();
        brushSizeSlider.setValue(drawingPanel.brushSize);
        brushSizeSlider.setMinimum(1);
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
        saveButton.addActionListener(e -> drawingPanel.saveCanvas(this));

        JButton openButton = new JButton("Open");
        openButton.addActionListener(e -> drawingPanel.openCanvas(this));
        
        toolBar.add(colorPickerButton);
        toolBar.add(pencilButton);
        toolBar.add(eraserButton);
        toolBar.add(fillButton);
        toolBar.add(brushSizeSlider);
        toolBar.add(clearButton);        
        toolBar.add(saveButton);
        toolBar.add(openButton);
        

        add(scrollPane, BorderLayout.CENTER);
        add(toolBar, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }
}
