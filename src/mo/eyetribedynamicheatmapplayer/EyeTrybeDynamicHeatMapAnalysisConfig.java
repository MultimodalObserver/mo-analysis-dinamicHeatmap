/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.eyetribedynamicheatmapplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import static mo.analysis.NotesAnalysisPlugin.logger;
import mo.analysis.PlayableAnalyzableConfiguration;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;
import mo.visualization.Playable;

/**
 *
 * @author gustavo
 */
public class EyeTrybeDynamicHeatMapAnalysisConfig implements PlayableAnalyzableConfiguration {

    String name;
    private final String[] creators = {"mo.eyetracker.capture.TheEyeTribeRecorder"};
    private ArrayList<File> files;
    private EyeTribeDHPlayer player;
    private File dir;
    private File mediaFile;
    private File projectFolder;

    public EyeTrybeDynamicHeatMapAnalysisConfig(String name) {
        this.name = name;
        this.files = new ArrayList<File>();
    }

    public EyeTrybeDynamicHeatMapAnalysisConfig() {
        this.files = new ArrayList<File>();
    }

    @Override
    public void setupAnalysis(File stageFolder, ProjectOrganization org, Participant p) {

        File[] dirs = org.getLocation().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                if (name.equals(p.folder)) {
                    return true;
                }
                return false;
            }
        });

        //this.dir = new File(org.getLocation().getAbsolutePath()+"\\"+p.folder);
        if (dirs.length == 1) {
            this.dir = dirs[0];
        } else {
            this.dir = null;
        }

        //this.dir = new File(org.getLocation().getAbsolutePath()+"\\"+p.folder);
        java.util.ResourceBundle dialogBundle = java.util.ResourceBundle.getBundle("i18n/mo/analysis/dynamicHeatmapPlugin/dynamicHeatmapPluginDialogs");
        JOptionPane.showMessageDialog(null, dialogBundle.getString("selectVideoScreenDialog"));

        SelectMediaFileDialog s = new SelectMediaFileDialog(this.dir);
        Boolean accepted = s.showDialog();

        if (accepted) {
            this.mediaFile = s.getSelectedFile();
        }
    }

    @Override
    public void startAnalysis() {
        if (this.mediaFile != null) {
            this.player = new EyeTribeDHPlayer(this.files.get(this.files.size() - 1), mediaFile, new File(this.dir, "analysis"));
        }
    }

    @Override
    public void cancelAnalysis() {
        Thread.currentThread().interrupt();
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public File toFile(File parent) {
        File f = new File(parent, "eyetribe-dynamicheatmap-analysis_" + this.name + ".xml");
        try {
            f.createNewFile();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return f;
    }

    @Override
    public Configuration fromFile(File file) {
        String fileName = file.getName();

        if (fileName.contains("_") && fileName.contains(".")) {
            String name = fileName.substring(fileName.indexOf("_") + 1, fileName.lastIndexOf("."));
            EyeTrybeDynamicHeatMapAnalysisConfig config = new EyeTrybeDynamicHeatMapAnalysisConfig(name);
            return config;
        }
        return null;
    }

    @Override
    public List<String> getCompatibleCreators() {
        return asList(creators);
    }

    @Override
    public void addFile(File file) {
        if (!files.contains(file)) {
            files.add(file);
        }
    }

    @Override
    public void removeFile(File file) {
        File toRemove = null;
        for (File f : files) {
            if (f.equals(file)) {
                toRemove = f;
            }
        }

        if (toRemove != null) {
            files.remove(toRemove);
        }
    }

    @Override
    public Playable getPlayer() {
        if (this.player == null) {
            this.player = new EyeTribeDHPlayer(this.files.get(this.files.size() - 1), mediaFile, new File(this.dir, "analysis"));
        }
        return this.player;
    }
}
