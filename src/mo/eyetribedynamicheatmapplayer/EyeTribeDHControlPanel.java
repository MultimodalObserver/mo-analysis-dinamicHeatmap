package mo.eyetribedynamicheatmapplayer;

import com.theeyetribe.clientsdk.data.GazeData;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javafx.scene.media.Media;
import javax.swing.JOptionPane;
import org.joda.time.DateTime;

/**
 *
 * @author gustavo
 */
public class EyeTribeDHControlPanel extends javax.swing.JPanel {

    int val;
    private long currentTime;
    private long starRangeTime;
    private long endRangeTime;
    private JFXPanelDH fixationPanel;
    private File outputDir;
    long timeToUpdate;

    public EyeTribeDHControlPanel(File dataFile, File mediaFile) throws FileNotFoundException {

        initComponents();
        this.currentTime = 0;
        this.starRangeTime = 0;
        this.endRangeTime = 0;
        this.timeToUpdate = 5000;
        Media media = new Media(mediaFile.toURI().toString());

        TrackingDHPanel videoPanel = (TrackingDHPanel) this.frontPanel;
        videoPanel.setupMedia(media);
        this.trackingPanel = (TrackingDHPanel) this.frontPanel;
        this.fixationPanel = this.trackingPanel.getFxPanel();

        /////////////////////////////////////////////////7
        this.dataFile = dataFile;
        this.multiplierSlider.setValue((int) ((this.trackingPanel.getFxPanel().getMultiplier() / 1.7f) * 100));

    }

    public EyeTribeDHControlPanel(File dataFile, File mediaFile, File outputDir) throws FileNotFoundException {

        initComponents();
        this.currentTime = 0;
        this.starRangeTime = 0;
        this.endRangeTime = 0;
        Media media = new Media(mediaFile.toURI().toString());
        this.outputDir = outputDir;
        this.timeToUpdate = 5000;

        TrackingDHPanel videoPanel = (TrackingDHPanel) this.frontPanel;
        videoPanel.setupMedia(media);
        this.trackingPanel = (TrackingDHPanel) this.frontPanel;
        this.fixationPanel = this.trackingPanel.getFxPanel();

        /////////////////////////////////////////////////7
        this.dataFile = dataFile;
        this.multiplierSlider.setValue((int) ((this.trackingPanel.getFxPanel().getMultiplier() / 1.7f) * 100));

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        frontPanel = new mo.eyetribedynamicheatmapplayer.TrackingDHPanel();
        exportMapButton = new javax.swing.JButton();
        updateTimeLabel = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        secondsLabel1 = new javax.swing.JLabel();
        intervalSpinner = new javax.swing.JSpinner();
        secondsLabel2 = new javax.swing.JLabel();
        intervalCheckBox = new javax.swing.JCheckBox();
        TransparencySlider = new javax.swing.JSlider();
        multiplierSlider = new javax.swing.JSlider();
        transparencyLabel = new javax.swing.JLabel();
        multiplierLabel = new javax.swing.JLabel();

        frontPanel.setMinimumSize(new java.awt.Dimension(200, 200));
        frontPanel.setPreferredSize(new java.awt.Dimension(811, 366));
        frontPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                frontPanelComponentResized(evt);
            }
        });

        javax.swing.GroupLayout frontPanelLayout = new javax.swing.GroupLayout(frontPanel);
        frontPanel.setLayout(frontPanelLayout);
        frontPanelLayout.setHorizontalGroup(
            frontPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        frontPanelLayout.setVerticalGroup(
            frontPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 368, Short.MAX_VALUE)
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("i18n/mo/analysis/dynamicHeatmapPlugin/dynamicHeatmapControlPanel"); // NOI18N
        exportMapButton.setText(bundle.getString("saveImage")); // NOI18N
        exportMapButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exportMapButtonMouseClicked(evt);
            }
        });

        updateTimeLabel.setText(bundle.getString("updateTime")); // NOI18N

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(5, 1, null, 1));
        jSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1StateChanged(evt);
            }
        });

        secondsLabel1.setText("(s)");

        intervalSpinner.setModel(new javax.swing.SpinnerNumberModel(10, null, null, 1));
        intervalSpinner.setEnabled(false);
        intervalSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                intervalSpinnerStateChanged(evt);
            }
        });

        secondsLabel2.setText("(s)");

        intervalCheckBox.setText(bundle.getString("interval")); // NOI18N
        intervalCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                intervalCheckBoxMouseClicked(evt);
            }
        });

        TransparencySlider.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                TransparencySliderMouseDragged(evt);
            }
        });

        multiplierSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                multiplierSliderMouseReleased(evt);
            }
        });

        transparencyLabel.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        transparencyLabel.setText(bundle.getString("transparency")); // NOI18N

        multiplierLabel.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        multiplierLabel.setText(bundle.getString("multiplier")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(updateTimeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(secondsLabel1)
                .addGap(37, 37, 37)
                .addComponent(intervalCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(intervalSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(secondsLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(transparencyLabel)
                    .addComponent(multiplierLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(TransparencySlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(multiplierSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(exportMapButton)
                .addContainerGap(104, Short.MAX_VALUE))
            .addComponent(frontPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 822, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(frontPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(updateTimeLabel)
                            .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(secondsLabel1)
                            .addComponent(intervalSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(secondsLabel2)
                            .addComponent(intervalCheckBox)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(exportMapButton)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(TransparencySlider, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(transparencyLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(multiplierSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(multiplierLabel))))))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void frontPanelComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_frontPanelComponentResized

    }//GEN-LAST:event_frontPanelComponentResized

    private void exportMapButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exportMapButtonMouseClicked
        String now = DateTime.now().toString();
        File outputFile = new File(outputDir.getPath(), "heatMap_" + now.substring(0, now.indexOf("T")) + "_" + ".png");
        this.fixationPanel.mapToFile(outputFile);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("i18n/mo/analysis/dynamicHeatmapPlugin/dynamicHeatmapPluginDialogs");
        JOptionPane.showMessageDialog(frontPanel, bundle.getString("imageSaved") + outputFile.getPath());
    }//GEN-LAST:event_exportMapButtonMouseClicked

    private void jSpinner1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner1StateChanged
        int i = (int) this.jSpinner1.getValue() * 1000;
        this.timeToUpdate = i;
    }//GEN-LAST:event_jSpinner1StateChanged

    private void intervalCheckBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_intervalCheckBoxMouseClicked
        if (this.intervalCheckBox.isSelected()) {
            this.intervalSpinner.setEnabled(true);
            this.fixationPanel.setInterval((int) this.intervalSpinner.getValue() * 1000);
        } else {
            this.intervalSpinner.setEnabled(false);
            this.fixationPanel.resetInterval();
        }
    }//GEN-LAST:event_intervalCheckBoxMouseClicked

    private void intervalSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_intervalSpinnerStateChanged
        this.fixationPanel.setInterval((int) this.intervalSpinner.getValue() * 1000);
    }//GEN-LAST:event_intervalSpinnerStateChanged

    private void multiplierSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_multiplierSliderMouseReleased
        this.trackingPanel.getFxPanel().setMultiplierHeatmapAndUpdate(((float) this.multiplierSlider.getValue() / 100) * 1.7f);
        this.repaint();
    }//GEN-LAST:event_multiplierSliderMouseReleased

    private void TransparencySliderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TransparencySliderMouseDragged
        this.fixationPanel.setHeatMapOpacity((float) this.TransparencySlider.getValue() / 100);
        this.repaint();
    }//GEN-LAST:event_TransparencySliderMouseDragged

    public void setOffset(long offset) {
        this.trackingPanel.getFxPanel().setOffset(offset);
    }

    public void setHeatMap(BufferedImage image) {
        this.trackingPanel.getFxPanel().setHeatMap(image);
    }

    public void playVideo() {
        this.trackingPanel.getFxPanel().playVideo();
    }

    public void playData(long time) {

    }

    public void addData(GazeData data) {
        this.trackingPanel.getFxPanel().addData(data);
    }

    public void addDataWithoutAois(GazeData data) {
        this.trackingPanel.getFxPanel().addDataWithoutAois(data);
    }

    public void pauseVideo() {
        this.trackingPanel.getFxPanel().pauseVideo();
    }

    public void setDataFromHeatMap(ArrayList<GazeData> data) {
        this.fixationPanel.setDataFromHeatMap(data);
    }

    public void pauseData() {

    }

    public void seekVideo(long time) {
        this.fixationPanel.seek(time);
    }

    public void seekData(long time) {

    }

    public void stop() {
        this.trackingPanel.getFxPanel().stop();
    }

    public void reset() {
        this.trackingPanel.getFxPanel().reset();
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public void setTime(long start, long end) {
        this.fixationPanel.setStartTime(start);
        this.fixationPanel.setEndTime(end);
    }

    public JFXPanelDH getJFXPanel() {
        return this.fixationPanel;
    }

    public long getTimeToUpdate() {
        return timeToUpdate;
    }

    public void playWhitLimit(Long limit) {
        this.trackingPanel.getFxPanel().playToLimit(limit);
    }

    public void cleanLastPlayLimit() {
        this.trackingPanel.getFxPanel().cleanLastPlayLimit();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider TransparencySlider;
    private javax.swing.JButton exportMapButton;
    private javax.swing.JPanel frontPanel;
    private javax.swing.JCheckBox intervalCheckBox;
    private javax.swing.JSpinner intervalSpinner;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JLabel multiplierLabel;
    private javax.swing.JSlider multiplierSlider;
    private javax.swing.JLabel secondsLabel1;
    private javax.swing.JLabel secondsLabel2;
    private javax.swing.JLabel transparencyLabel;
    private javax.swing.JLabel updateTimeLabel;
    // End of variables declaration//GEN-END:variables
    private boolean playing;
    private TrackingDHPanel trackingPanel;
    private File dataFile;

}
