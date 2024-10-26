package com.maga;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DrawingPanel extends JPanel {
    private static final long serialVersionUID = 1L; // Add this line
    // public enum Tool { PENCIL, ERASER, FILL_BUCKET }

    public Tool selectedTool = Tool.PENCIL;
    public Color currentColor = Color.BLACK;
    public transient BufferedImage canvas;
    private transient BufferedImage patternImage;
    public int brushSize = 5;

    public double zoomFactor = 1;

    private Point mousePos;
    private Stack<BufferedImage> canvasStack = new Stack<BufferedImage>();
    private Stack<BufferedImage> undoStack = new Stack<BufferedImage>();

    // Constants for centering the canvas
    private static final int INITIAL_CANVAS_WIDTH = 1600;
    private static final int INITIAL_CANVAS_HEIGHT = 1200;

    public DrawingPanel() {
        // Initialize the canvas based on initial panel size
        canvas = new BufferedImage(INITIAL_CANVAS_WIDTH, INITIAL_CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB); // Larger initial canvas
        clearCanvas();

        try {
            patternImage = ImageIO.read(new File("assets/opacityPattern.png"));
        } catch (IOException e) {
            patternImage = copyCanvas(canvas);
        }

        // Mouse listeners for drawing
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePos = toCanvasPoint(e.getPoint());
                applyCurrentTool(mousePos);
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mousePos = toCanvasPoint(e.getPoint());
                // if (selectedTool == Tool.FILL_BUCKET) {
                //     setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                // }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mousePos = toCanvasPoint(e.getPoint());
                applyCurrentTool(mousePos);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                canvasStack.addLast(copyCanvas(canvas));
                // if (canvasStack.size() > 100) {
                //     canvasStack.remove(canvasStack.size() - 1);
                // }
            }
        };

        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);

        mousePos = new Point(0,0);

        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl Z"), "undo");
        this.getActionMap().put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });

        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl Y"), "redo");
        this.getActionMap().put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        });
    }

    public void clearCanvas() {
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g2d.dispose();
    }

    public void undo() {
        if (!canvasStack.isEmpty()) {
            BufferedImage undoneCanvas = canvasStack.pop();
            if (canvasStack.isEmpty()) {
                clearCanvas();
                // canvas = createEmptyCanvas();
            } else {
                canvas = copyCanvas(canvasStack.peek());
            }
            undoStack.addLast(undoneCanvas);
            repaint(); // Repaint after undo
        }
    }

    public void redo() {
        if (!undoStack.isEmpty()) {
            BufferedImage redoneCanvas = undoStack.pop();

            // if (undoStack.isEmpty()) {
            //     clearCanvas();
            // } else {
                canvas = redoneCanvas;
            // }
            canvasStack.addLast(redoneCanvas);
            repaint(); // Repaint after undo
        }
    }

    public double zoomIn() {
        zoomFactor += 0.1;
        revalidate();
        repaint();

        return zoomFactor;
    }

    public double zoomOut() {
        if (zoomFactor > 0.1) {
            zoomFactor -= 0.1;
            revalidate();
            repaint();
        }
        return zoomFactor;
    }

    private BufferedImage copyCanvas(BufferedImage canvas) {
        BufferedImage newCanvas = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = newCanvas.createGraphics();
        g2d.drawImage(canvas, 0,0, null);
        g2d.dispose();

        return newCanvas;
    }

    public void saveCanvas(Component parent) {
        String userName = System.getProperty("user.name");
        // Show file chooser to save the canvas
        JFileChooser fileChooser = new JFileChooser(String.format("C:\\Users\\%s\\Pictures", userName));
        fileChooser.setDialogTitle("Save Image");

        int userSelection = fileChooser.showSaveDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                // Save as PNG
                ImageIO.write(canvas, "png", new File(fileToSave.getAbsolutePath() + ".png"));
                JOptionPane.showMessageDialog(parent, "Image saved successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Error saving image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applyCurrentTool(Point point) {
        switch (selectedTool) {
            case PENCIL:
                drawOnCanvas(point, currentColor);
                break;
            case ERASER:
                eraseOnCanvas(point);
                break;
            case FILL_BUCKET:
                floodFill(point, currentColor);
                break;
        }
    }

    private void drawOnCanvas(Point point, Color color) {
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(color);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, color.getAlpha() / 255.0f));
        g2d.fillOval(point.x - brushSize / 2, point.y - brushSize / 2, brushSize, brushSize);
        g2d.dispose();
    }

    private void eraseOnCanvas(Point point) {
        Graphics2D g2d = canvas.createGraphics();
        g2d.setComposite(AlphaComposite.Clear); // Set composite to clear for erasing
        g2d.fillOval(point.x - brushSize / 2, point.y - brushSize / 2, brushSize, brushSize);
        drawPatternOnErasedArea(point, brushSize);
        g2d.dispose();
    }

    private void floodFill(Point start, Color targetColor) {
        int targetRGB = canvas.getRGB(start.x, start.y);
        int replacementRGB = targetColor.getRGB();
        if (targetRGB == replacementRGB) return;

        Queue<Point> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            Point point = queue.poll();
            int x = point.x;
            int y = point.y;

            if (x < 0 || x >= canvas.getWidth() || y < 0 || y >= canvas.getHeight()) continue;
            if (canvas.getRGB(x, y) == targetRGB) {
                canvas.setRGB(x, y, replacementRGB);
                queue.add(new Point(x + 1, y));
                queue.add(new Point(x - 1, y));
                queue.add(new Point(x, y + 1));
                queue.add(new Point(x, y - 1));
            }
        }
    }

    private Point toCanvasPoint(Point screenPoint) {
        int offsetX = (getWidth() - (int)(canvas.getWidth() * zoomFactor)) / 2; // Center the canvas horizontally
        int offsetY = (getHeight() - (int)(canvas.getHeight() * zoomFactor)) / 2; // Center the canvas vertically
        int x = (int) (screenPoint.x / zoomFactor) - offsetX;
        int y = (int) (screenPoint.y / zoomFactor) - offsetY;
        return new Point(x, y);
    }

    private void drawPatternOnErasedArea(Point point, int size) {
        Graphics2D g2d = canvas.createGraphics();
        g2d.setComposite(AlphaComposite.SrcOver); // Use SrcOver to blend the pattern
        
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                // Only draw the pattern in the erased area (where the original color was fully transparent)
                int canvasX = point.x + x - size / 2;
                int canvasY = point.y + y - size / 2;

                if (canvasX >= 0 && canvasX < canvas.getWidth() && canvasY >= 0 && canvasY < canvas.getHeight()) {
                    Color pixelColor = new Color(patternImage.getRGB(x % patternImage.getWidth(), y % patternImage.getHeight()), true);
                    if (pixelColor.getAlpha() > 0) {
                        canvas.setRGB(canvasX, canvasY, pixelColor.getRGB());
                    }
                }
            }
        }
        g2d.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(zoomFactor, zoomFactor);

        int offsetX = (getWidth() - (int)(canvas.getWidth() * zoomFactor)) / 2; // Center the canvas horizontally
        int offsetY = (getHeight() - (int)(canvas.getHeight() * zoomFactor)) / 2; // Center the canvas vertically
        g2d.drawImage(canvas, offsetX, offsetY, null);

        g2d.drawArc(mousePos.x - brushSize/2 + offsetX, mousePos.y - brushSize/2 + offsetY, brushSize, brushSize, 0, 360);

        g2d.setFont(new Font("Default", 0, (int) (30/zoomFactor)));
        g2d.drawString(String.format("( %d, %d )", mousePos.x, mousePos.y), (int) (30/zoomFactor), (int) (30/zoomFactor));

        g2d.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension((int) (canvas.getWidth() * zoomFactor), (int) (canvas.getHeight() * zoomFactor));
    }
}