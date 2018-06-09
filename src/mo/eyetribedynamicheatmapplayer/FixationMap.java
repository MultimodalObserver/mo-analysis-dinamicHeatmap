/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.eyetribedynamicheatmapplayer;

import com.theeyetribe.clientsdk.data.GazeData;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javafx.embed.swing.JFXPanel;
import javax.swing.JPanel;

/**
 *
 * @author gustavo
 */
public class FixationMap {

    private BufferedImage map;
    private Color color;
    private Graphics2D graphics;
    private int opacity;

    private int initialRadio;
    private int radioIncrement;

    private Fixation lastFixation;
    private Fixation firstFixation;
    private int fixationCount;

    private Double originalWidth;
    private Double originalHeight;
    private Double width;
    private Double height;

    private boolean fixationIsActive;
    private int limit;

    public FixationMap(int width, int height, Color color) {

        this.map = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.color = color;
        this.graphics = this.map.createGraphics();
        this.graphics.setColor(color);
        this.originalWidth = new Double(width);
        this.originalHeight = new Double(height);
        this.width = this.originalWidth;
        this.height = this.originalHeight;
        this.lastFixation = null;
        this.fixationCount = 0;
        this.fixationIsActive = false;
        this.initialRadio = 10;
        this.radioIncrement = 2;
        this.opacity = 100;
        this.limit = -1;
    }

    public FixationMap(Double width, Double height, Color color) {

        this.map = new BufferedImage(width.intValue(), height.intValue(), BufferedImage.TYPE_INT_ARGB);
        this.color = color;
        this.graphics = this.map.createGraphics();
        this.graphics.setColor(color);
        this.originalWidth = width;
        this.originalHeight = height;
        this.width = this.originalWidth;
        this.height = this.originalHeight;
        this.lastFixation = null;
        this.fixationCount = 0;
        this.fixationIsActive = false;
        this.initialRadio = 10;
        this.radioIncrement = 2;
        this.opacity = 100;
        this.limit = -1;
    }

    public void setInitialRadio(int initialRadio) {
        this.initialRadio = initialRadio;
    }

    public void addData(GazeData data) {

        if (data.isFixated) {
            if (this.lastFixation == null || !this.fixationIsActive) {
                addFixation(data);
                this.fixationIsActive = true;
            } else {
                incrementCurrentFixation(data);
            }

        } else {
            this.fixationIsActive = false;
        }

    }

    public void addFixation(GazeData data) {

        if (this.lastFixation == null) {
            this.lastFixation = new Fixation(
                    data.smoothedCoordinates.x / this.originalWidth, //deberian ser orginal wiidth y height
                    data.smoothedCoordinates.y / this.originalHeight,
                    data.timeStamp
            );
            if (this.limit == -1) {
                this.lastFixation.paint(map, new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity));
            } else {
                this.cleanMap();
                this.lastFixation.paintLast(map, new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity), opacity, limit);
            }
        } else {
            this.lastFixation.setNext(new Fixation(
                    data.smoothedCoordinates.x / this.originalWidth,
                    data.smoothedCoordinates.y / this.originalHeight,
                    data.timeStamp
            )
            );
            this.lastFixation = this.lastFixation.getNext();
            if (this.limit == -1) {
                this.lastFixation.paint(map, new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity));
            } else {
                this.cleanMap();
                this.lastFixation.paintLast(map, new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity), opacity, limit);
            }
        }

    }

    private void incrementCurrentFixation(GazeData data) {
        this.lastFixation.increment(2);
        this.lastFixation.setEndUixTimeStamp(data.timeStamp);
        this.lastFixation.update(map, new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity / 2));
    }

    private Double getRelativeX(int x) {
        return x * this.width / this.originalWidth;
    }

    private Double getRelativeY(int y) {
        return y * this.height / this.originalWidth;
    }

    private Double getRelativeX(Double x) {
        return x * this.width / this.originalWidth;
    }

    private Double getRelativeY(Double y) {
        return y * this.height / this.originalWidth;
    }

    public void resize(int width, int height) {
        //   this.map = resizeImage(this.map, width, height);

        if (this.width != width || this.height != height) {
            this.width = new Double(width);
            this.height = new Double(height);

            // this.cleanMap();
            repaint();
        }
    }

    public void repaint() {

        this.cleanMap();

        if (this.lastFixation != null) {

            if (this.limit == -1) {
                Fixation fix = this.lastFixation;
                if (fix == null) {
                    return;
                }

                while (fix != null) {

                    fix.paint(this.map, new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity));
                    for (int i = 0; i < fix.getIncrement(); i++) {
                        fix.update2(this.map, new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity / 4), i);
                    }
                    fix = fix.getPrevious();
                }
            } else {
                this.lastFixation.paintLast(map, new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity), opacity, limit);
            }
        }

    }

    public void paintToPanel(JPanel panel) {

        Graphics2D panelGraphics = (Graphics2D) panel.getGraphics();
        panelGraphics.drawImage(map, 0, 0, panel);
    }

    public void paintToPanel(JFXPanel panel) {

        Graphics2D panelGraphics = (Graphics2D) panel.getGraphics();
        panelGraphics.drawImage(map, 0, 0, panel);
    }

    public void paintToImage(BufferedImage image) {
        Graphics2D imageGraphics = (Graphics2D) image.getGraphics();
        if (imageGraphics == null) {
            imageGraphics = image.createGraphics();
        }
        imageGraphics.drawImage(map, 0, 0, null);

    }

    private BufferedImage resizeImage(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        //g2d.dispose();
        return dimg;
    }

    public void cleanMap() {
        this.map = new BufferedImage(this.width.intValue(), this.height.intValue(), BufferedImage.TYPE_INT_ARGB);
    }

    public Fixation getLastFixation() {
        return lastFixation;
    }

    public BufferedImage getMap() {
        return map;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        this.repaint();
    }

    public void setOpacity(Double opacity) {
        this.opacity = new Double(255 * opacity).intValue();
        this.repaint();
    }

    public void setLimit(int limit) {
        this.limit = limit;

        if (limit == -1 && this.lastFixation != null) {
            //this.cleanMap();
            this.repaint();
        }
    }

    public void reset() {
        this.cleanMap();
        this.lastFixation = null;
        this.firstFixation = null;
    }

}
