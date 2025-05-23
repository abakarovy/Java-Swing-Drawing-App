package com.maga.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.maga.DrawingApp;

public class SetupFrame extends JFrame {
    private JTextField widthField;
    private JTextField heightField;

    public SetupFrame() {
        setTitle("Canvas Setup");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centers the window

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // creates labels and text fields
        JLabel widthLabel = new JLabel("Canvas Width:");
        JLabel heightLabel = new JLabel("Canvas Height:");
        widthField = new JTextField("1200", 10); // default width
        heightField = new JTextField("800", 10); // default height
        
        // creates start buttons
        JButton startButton = new JButton("Start Drawing");
        startButton.addActionListener(e -> {
            try {
                int width = Integer.parseInt(widthField.getText());
                int height = Integer.parseInt(heightField.getText());
                openDrawingApp(width, height);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(SetupFrame.this, "Please enter valid integers for width and height.");
            }
        });

        panel.setLayout(new GridLayout(3, 2, 5, 5));
        panel.add(widthLabel);
        panel.add(widthField);
        panel.add(heightLabel);
        panel.add(heightField);
        panel.add(startButton);
        
        add(panel);

        setVisible(true);
    }

    private void openDrawingApp(int width, int height) {
        new DrawingApp(width, height); // opens the main drawing app with specified dimensions
        dispose(); // closes the setup frame
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SetupFrame::new);
    }
}