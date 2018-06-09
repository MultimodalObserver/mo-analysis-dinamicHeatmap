/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.eyetribedynamicheatmapplayer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author gustavo
 */
public class Fixation {

    private int radio;
    private int id;
    private Fixation previous;
    private Fixation next;
    private int originalX;
    private int originalY;
    private int x;
    private int y;
    private Double relativeX;
    private Double relativeY;
    private int increment;

    long startUixTimeStamp;
    long endUixTimeStamp;
    long start;
    long end;

    public Fixation(int x, int y, long startUixTimeStamp) {
        this.originalX = x;
        this.originalY = y;
        this.x = x;
        this.y = y;
        this.startUixTimeStamp = startUixTimeStamp;
        this.endUixTimeStamp = startUixTimeStamp;
    }

    public Fixation(Double x, Double y, long startUixTimeStamp) {

        this.relativeX = x;
        this.relativeY = y;
        this.startUixTimeStamp = startUixTimeStamp;
        this.endUixTimeStamp = startUixTimeStamp;
        this.next = null;
        this.increment = 1;
    }

    public void paint(BufferedImage image) {

        Double currentX = image.getWidth() * relativeX;
        Double currentY = image.getHeight() * relativeY;

        Graphics2D g = (Graphics2D) image.getGraphics();

//        g.setColor(new Color(Color.BLUE.getRed(),Color.BLUE.getGreen(),Color.BLUE.getBlue(),100));
        g.setStroke(new BasicStroke((float) 0.0));
        g.fillOval(currentX.intValue() - 50, currentY.intValue() - 50, 50, 50);

        g.setColor(new Color(Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue(), 0.5f));
        // g.setColor(Color.WHITE);
        g.setFont(new Font("default", Font.BOLD, 12));
        g.drawString(String.valueOf(this.id), currentX.intValue() - 50 + 22, currentY.intValue() - 50 + 30);

        if (this.previous != null) {
            Double lastX = image.getWidth() * this.previous.getRelativeX();
            Double lastY = image.getHeight() * this.previous.getRelativeY();
            //  g.setColor(new Color(Color.BLUE.getRed(),Color.BLUE.getGreen(),Color.BLUE.getBlue(),100));

            g.drawLine(currentX.intValue() - 50 + 22, currentY.intValue() - 50 + 30, lastX.intValue() - 50 + 22, lastY.intValue() - 50 + 30);
        }

    }

    public void update(BufferedImage image) {

        Double currentX = image.getWidth() * relativeX;
        Double currentY = image.getHeight() * relativeY;

        Graphics2D g = (Graphics2D) image.getGraphics();
        //g.setColor(new Color(Color.BLUE.getRed(),Color.BLUE.getGreen(),Color.BLUE.getBlue(),100));
        g.setStroke(new BasicStroke((float) 1));

        g.setColor(new Color(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), g.getColor().getAlpha() / 2));
        //g.setComposite(AlphaComposite.SrcOver.derive(0.5f));

        g.drawOval(currentX.intValue() - 50 - increment / 2, currentY.intValue() - 50 - increment / 2, 50 + increment, 50 + increment);
        g.setColor(new Color(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), g.getColor().getAlpha() * 2));

    }

    public void paint(BufferedImage image, Color color) {

        Double currentX = image.getWidth() * relativeX;
        Double currentY = image.getHeight() * relativeY;

        Graphics2D g = (Graphics2D) image.getGraphics();

        g.setColor(color);
        g.setStroke(new BasicStroke((float) 0.0));
        g.fillOval(currentX.intValue() - 50, currentY.intValue() - 50, 50, 50);

        g.setColor(new Color(Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue(), 0.5f));
        // g.setColor(Color.WHITE);
        g.setFont(new Font("default", Font.BOLD, 12));
        g.drawString(String.valueOf(this.id), currentX.intValue() - 50 + 22, currentY.intValue() - 50 + 30);

        if (this.previous != null) {
            Double lastX = image.getWidth() * this.previous.getRelativeX();
            Double lastY = image.getHeight() * this.previous.getRelativeY();
            //  g.setColor(new Color(Color.BLUE.getRed(),Color.BLUE.getGreen(),Color.BLUE.getBlue(),100));

            g.drawLine(currentX.intValue() - 50 + 22, currentY.intValue() - 50 + 30, lastX.intValue() - 50 + 22, lastY.intValue() - 50 + 30);
        }
    }

    public void paintWithoutLine(BufferedImage image, Color color) {

        Double currentX = image.getWidth() * relativeX;
        Double currentY = image.getHeight() * relativeY;

        Graphics2D g = (Graphics2D) image.getGraphics();

        g.setColor(color);
        g.setStroke(new BasicStroke((float) 0.0));
        g.fillOval(currentX.intValue() - 50, currentY.intValue() - 50, 50, 50);

        g.setColor(new Color(Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue(), 0.5f));
        // g.setColor(Color.WHITE);
        g.setFont(new Font("default", Font.BOLD, 12));
        g.drawString(String.valueOf(this.id), currentX.intValue() - 50 + 22, currentY.intValue() - 50 + 30);

    }

    public void paintLast(BufferedImage image, Color color, int opacity, int fixationsCount) {

        int count = fixationsCount;
        Fixation fix = this;

        //fix.paint(image, color);
        while (fix != null && count > 0) {
            if (count == 1) {
                fix.paintWithoutLine(image, color);
                for (int i = 0; i < fix.increment; i++) {
                    fix.update2(image, new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity / 4), i);
                }
                fix = fix.previous;

                count--;
            } else {
                fix.paint(image, color);
                fix = fix.previous;

                if (fix != null) {
                    for (int i = 0; i < fix.increment; i++) {
                        fix.update2(image, new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity / 4), i);
                    }
                }

                count--;
            }
        }

        /*   if(fix!=null){

            while(fix.previous!=null&&count<=0){
                fix = fix.previous;
                count--;
            }

            for(count=count; count<fixationsCount; count ++){
                System.out.println("fase3");

                fix.paint(image,color);
                fix = fix.next;
                
                for(int i= 0 ; i<fix.increment ; i++){
                    fix.update(image, color);
                }
                
            }
        }*/
    }

    public void update(BufferedImage image, Color color) {

        Double currentX = image.getWidth() * relativeX;
        Double currentY = image.getHeight() * relativeY;

        Graphics2D g = (Graphics2D) image.getGraphics();
        //g.setColor(new Color(Color.BLUE.getRed(),Color.BLUE.getGreen(),Color.BLUE.getBlue(),100));
        g.setStroke(new BasicStroke((float) 1));

        g.setColor(color);
        //g.setComposite(AlphaComposite.SrcOver.derive(0.5f));

        g.drawOval(currentX.intValue() - 50 - increment / 2, currentY.intValue() - 50 - increment / 2, 50 + increment, 50 + increment);
//        g.setColor(new Color(Color.BLUE.getRed(),Color.BLUE.getGreen(),Color.BLUE.getBlue(),g.getColor().getAlpha()*2));        

    }

    public void update2(BufferedImage image, Color color, int increment) {

        Double currentX = image.getWidth() * relativeX;
        Double currentY = image.getHeight() * relativeY;

        Graphics2D g = (Graphics2D) image.getGraphics();
        //g.setColor(new Color(Color.BLUE.getRed(),Color.BLUE.getGreen(),Color.BLUE.getBlue(),100));
        g.setStroke(new BasicStroke((float) 1));

        g.setColor(color);
        //g.setComposite(AlphaComposite.SrcOver.derive(0.5f));

        g.drawOval(currentX.intValue() - 50 - increment / 2, currentY.intValue() - 50 - increment / 2, 50 + increment, 50 + increment);
        // g.setColor(new Color(Color.BLUE.getRed(),Color.BLUE.getGreen(),Color.BLUE.getBlue(),g.getColor().getAlpha()*2));        

    }

    public void increment(int increment) {
        this.radio = this.radio + increment;
        this.increment = increment + this.increment;
    }

    public int getRadio() {
        return radio;
    }

    public int getId() {
        return id;
    }

    public Fixation getNext() {
        return next;
    }

    public Double getRelativeX() {
        return relativeX;
    }

    public Double getRelativeY() {
        return relativeY;
    }

    public long getStartUixTimeStamp() {
        return startUixTimeStamp;
    }

    public long getEndUixTimeStamp() {
        return endUixTimeStamp;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public void setRadio(int radio) {
        this.radio = radio;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPrevious(Fixation previous) {
        this.previous = previous;
        this.id = previous.id + 1;
    }

    public void setNext(Fixation next) {
        this.next = next;
        next.setPrevious(this);
    }

    public void setRelativeX(Double relativeX) {
        this.relativeX = relativeX;
    }

    public void setRelativeY(Double relativeY) {
        this.relativeY = relativeY;
    }

    public void setStartUixTimeStamp(long startUixTimeStamp) {
        this.startUixTimeStamp = startUixTimeStamp;
    }

    public void setEndUixTimeStamp(long endUixTimeStamp) {
        this.endUixTimeStamp = endUixTimeStamp;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public Fixation getPrevious() {
        return previous;
    }

    public int getIncrement() {
        return increment;
    }

    public long getDuration() {

        return this.endUixTimeStamp - this.startUixTimeStamp;

    }

}
