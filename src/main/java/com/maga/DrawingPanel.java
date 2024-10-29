package com.maga;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

class DrawingPanel extends JPanel {
    private static final long serialVersionUID = 1L; // to fix some strange compilaton warning

    public Tool selectedTool = Tool.PENCIL;
    public Color currentColor = Color.BLACK;
    public transient BufferedImage canvas;
    public int brushSize = 5;

    public double zoomFactor = 1;

    private Point mousePos;
    private Stack<BufferedImage> canvasStack = new Stack<BufferedImage>();
    private Stack<BufferedImage> undoStack = new Stack<BufferedImage>();

    public DrawingPanel(int width, int height) {
        // initializes the canvas based on initial panel size
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        clearCanvas();

        // mouse listeners for drawing
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
                // should probably add a size limit to the stack but have to figure out how to do it without breaking the (First In Last Out) rules of the stack
                // if (canvasStack.size() > 100) {
                //     canvasStack.remove(canvasStack.size() - 1);
                // }
            }
        };

        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);

        
        // adds undo and redo on ctrl+z and ctrl+y
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
        
        // sets mouse position to 0,0
        mousePos = new Point(0,0);
    }
    
    // canvas methods
    public void clearCanvas() {
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvasStack.addLast(copyCanvas(canvas));
        g2d.dispose();
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
        
        // shows file chooser to save the canvas
        JFileChooser fileChooser = new JFileChooser(String.format("C:\\Users\\%s\\Pictures", userName));
        fileChooser.setDialogTitle("Save Image");

        int userSelection = fileChooser.showSaveDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                // saves as PNG
                ImageIO.write(canvas, "png", new File(fileToSave.getAbsolutePath() + ".png"));
                JOptionPane.showMessageDialog(parent, "Image saved successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Error saving image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void undo() {
        if (!canvasStack.isEmpty()) {
            BufferedImage undoneCanvas = canvasStack.pop();
            if (canvasStack.isEmpty()) {
                clearCanvas();
            } else {
                canvas = copyCanvas(canvasStack.peek());
            }
            undoStack.addLast(undoneCanvas);
            repaint(); // repaint after undo
        }
    }
    public void redo() {
        if (!undoStack.isEmpty()) {
            BufferedImage redoneCanvas = undoStack.pop();
            canvas = redoneCanvas;
            canvasStack.addLast(redoneCanvas);
            repaint(); // repaint after redo
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
    public void setTool(Tool tool) {
        selectedTool = tool;
    }

    private void drawOnCanvas(Point point, Color color) {
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(color);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, color.getAlpha() / 255.0f));

        if (brushSize == 1) {
            g2d.fillRect(point.x, point.y, 1, 1);
        } else {
            g2d.fillOval(point.x - brushSize / 2, point.y - brushSize / 2, brushSize, brushSize);
        }
        g2d.dispose();
    }
    private void eraseOnCanvas(Point point) {
        Graphics2D g2d = canvas.createGraphics();
        g2d.setComposite(AlphaComposite.Clear); // set composite to clear for erasing
        g2d.fillOval(point.x - brushSize / 2, point.y - brushSize / 2, brushSize, brushSize);
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

    public Point toCanvasPoint(Point screenPoint) {
        int offsetX = (getWidth() - (int)(canvas.getWidth() * zoomFactor)) / 2; // center the canvas horizontally
        int offsetY = (getHeight() - (int)(canvas.getHeight() * zoomFactor)) / 2; // center the canvas vertically
        int x = (int) (screenPoint.x / zoomFactor) - offsetX;
        int y = (int) (screenPoint.y / zoomFactor) - offsetY;
        return new Point(x, y);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension((int) (canvas.getWidth() * zoomFactor), (int) (canvas.getHeight() * zoomFactor));
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(zoomFactor, zoomFactor);

        int offsetX = (getWidth() - (int)(canvas.getWidth() * zoomFactor)) / 2; // center the canvas horizontally
        int offsetY = (getHeight() - (int)(canvas.getHeight() * zoomFactor)) / 2; // center the canvas vertically
        g2d.drawImage(canvas, offsetX, offsetY, null);

        g2d.drawArc(mousePos.x - brushSize/2 + offsetX, mousePos.y - brushSize/2 + offsetY, brushSize, brushSize, 0, 360);

        g2d.setFont(new Font("Default", 0, (int) (30/zoomFactor)));

        // TODO: replace this with a label at the tool bar, or make it so that the text doesnt disappear when you zoom in
        g2d.drawString(String.format("( %d, %d )", mousePos.x, mousePos.y), (int) (30/zoomFactor), (int) (30/zoomFactor));

        g2d.dispose();
    }
}