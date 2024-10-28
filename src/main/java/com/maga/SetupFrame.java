package com.maga;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SetupFrame extends JFrame {
    private JTextField widthField;
    private JTextField heightField;

    public SetupFrame() {
        setTitle("Canvas Setup");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Create input fields
        JLabel widthLabel = new JLabel("Canvas Width:");
        JLabel heightLabel = new JLabel("Canvas Height:");
        widthField = new JTextField("1200", 10); // Default width
        heightField = new JTextField("800", 10); // Default height

        // Create Start button
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

        // Arrange components in a grid
        setLayout(new GridLayout(3, 2, 5, 5));
        add(widthLabel);
        add(widthField);
        add(heightLabel);
        add(heightField);
        add(startButton);

        setVisible(true);
    }

    private void openDrawingApp(int width, int height) {
        new DrawingApp(width, height); // Open the main drawing app with specified dimensions
        dispose(); // Close the setup window
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SetupFrame::new);
    }
}