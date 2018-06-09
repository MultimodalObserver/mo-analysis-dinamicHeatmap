/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.eyetribedynamicheatmapplayer;

import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import com.theeyetribe.clientsdk.data.GazeData;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import static mo.analysis.NotesAnalysisPlugin.logger;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.DockablesRegistry;
import mo.visualization.Playable;

public class EyeTribeDHPlayer implements Playable {

    private long start;
    private long end;
    private EyeTribeDHControlPanel mainPanel;
    private File dataFile;
    private File mediaFile;
    private ArrayList<GazeData> data;
    private GazeData current;
    private int currentCount;
    private boolean videoIsPlaying;
    private boolean isFinalized;
    private long offset;

    private long updateHeatMapTime;
    private long nextUpdate;

    private long currentTime;
    private BufferedImage nextHeatMapImage;

    private Boolean isSync;
    private ArrayList<Long> frames;
    private int frameCount;

    private Thread heatMapMonitor;
    private boolean firstPlay;
    private Boolean pause;

    public EyeTribeDHPlayer(File dataFile, File mediaFile, File outputFolder) {

        this.dataFile = dataFile;
        this.mediaFile = mediaFile;

        this.data = readData(dataFile);
        this.start = this.data.get(0).timeStamp;
        this.end = this.data.get(this.data.size() - 1).timeStamp;
        this.current = this.data.get(0);
        this.currentCount = 0;
        this.videoIsPlaying = false;
        this.isFinalized = false;
        this.offset = -1;
        this.updateHeatMapTime = 5000;
        //this.nextUpdate = this.updateHeatMapTime;
        this.nextUpdate = 0;
        this.currentTime = 0;
        this.isSync = false;
        this.frameCount = 0;
        this.firstPlay = true;
        this.heatMapMonitor = null;
        this.pause = false;

        String framesFileName = mediaFile.getName().substring(0, mediaFile.getName().lastIndexOf(".")) + "-frames.txt";

        File framesFile = new File(mediaFile.getParentFile(), framesFileName);
        if (framesFile != null) {
            if (framesFile.exists()) {
                this.frames = this.loadFrames(framesFile);
            }
        }

        EyeTribeDHControlPanel panel;
        try {

            panel = new EyeTribeDHControlPanel(dataFile, mediaFile, outputFolder);
            this.mainPanel = panel;

            SwingUtilities.invokeLater(() -> {
                try {
                    DockableElement e = new DockableElement();
                    e.add(panel);

                    e.setTitleText(this.dataFile.getName() + "-" + this.mediaFile.getName());
                    e.addVetoClosingListener(new CVetoClosingListener() {
                        @Override
                        public void closing(CVetoClosingEvent cvce) {
                        }

                        @Override
                        public void closed(CVetoClosingEvent cvce) {
                            isFinalized = true;
                            heatMapMonitor = null;
                        }
                    }
                    );

                    DockablesRegistry.getInstance().addAppWideDockable(e);

                } catch (Exception ex) {
                    logger.log(Level.INFO, null, ex);
                }
            });

        } catch (FileNotFoundException ex) {
            Logger.getLogger(EyeTribeDHPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        t = 0;

        this.mainPanel.setDataFromHeatMap(data);
        this.mainPanel.setOffset(this.start);
        this.offset = this.start;
        this.mainPanel.setTime(start, end);
        this.mainPanel.getJFXPanel().setStartRange(start);

    }

    private ArrayList<GazeData> readData(File file) {

        FileReader fr;
        BufferedReader br;
        GazeData aux;
        ArrayList<GazeData> data = new ArrayList<GazeData>();

        try {

            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line;

            line = br.readLine();

            while (line != null) {

                data.add(this.parseDataFromLine(line));
                line = br.readLine();
            }

            return data;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(EyeTribeDHPlayer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EyeTribeDHPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private GazeData parseDataFromLine(String line) {
        String[] parts = line.split(" ");
        GazeData data = new GazeData();
        for (String part : parts) {

            try {

                String[] keyNValue = part.split(":");
                String k = keyNValue[0];
                String v = keyNValue[1];

                switch (k) {
                    case "t":
                        data.timeStamp = Long.parseLong(v);
                        break;
                    case "fx":
                        data.isFixated = Boolean.parseBoolean(v);
                        break;
                    case "sm":
                        data.smoothedCoordinates.x = Double.parseDouble(v.split(";")[0]);
                        data.smoothedCoordinates.y = Double.parseDouble(v.split(";")[1]);
                        break;
                    case "rw":
                        data.rawCoordinates.x = Double.parseDouble(v.split(";")[0]);
                        data.rawCoordinates.y = Double.parseDouble(v.split(";")[1]);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                logger.log(
                        Level.WARNING,
                        "Error reading part <{0}> line <{1}>:{2}",
                        new Object[]{part, line, e});
            }
        }

        return data;
    }

    @Override
    public long getStart() {
        return start;
    }

    @Override
    public long getEnd() {
        return end;
    }

    @Override
    public void play(long millis) {

        this.currentTime = millis;
        this.updateHeatMapTime = this.mainPanel.getTimeToUpdate();

        if (this.offset < 0) {
            this.offset = millis;

            this.mainPanel.setOffset(offset);
        }

        if (!this.videoIsPlaying) {
            if (isSync) {
                if (frameCount < 0) {
                    frameCount = this.getActualFrameCount(millis);
                }
                if (frameCount < frames.size()) {
                    if (millis == frames.get(frameCount)) {
                        this.mainPanel.playWhitLimit(frames.get(frameCount) - offset);

                        if (this.currentTime - this.start >= this.nextUpdate) {
                            this.nextUpdate = this.nextUpdate + this.updateHeatMapTime;
                            this.mainPanel.getJFXPanel().updateHeatMap();
                        }

                        frameCount++;
                    }
                }
            } else {

                if (!this.isSync) {
                    if (this.heatMapMonitor == null) {
                        this.heatMapMonitor = new Thread(() -> {

                            boolean created = false;
                            while (!this.isFinalized && !this.pause) {
                                //System.out.println("heatmap monitor");
                                /*if(this.isSync){
                            synchronized(this.isSync){
                                try {
                                    this.isSync.wait();
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(EyeTribeDHPlayer.class.getName()).log(Level.SEVERE, null, ex);
                                }     
                            }
                        }*/
 /*         System.out.println("monitoring...");
                        if(pause){
                        System.out.println("monitor paused");                            
                            synchronized(this.pause){
                                try {
                                    this.pause.wait();
                                    System.out.println("que onda ctm");
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(EyeTribeDHPlayer.class.getName()).log(Level.SEVERE, null, ex);
                                }     
                            }              
                        }
                        else{*/
                                if (!created) {
                                    System.out.println("creating new heatmap...(if_1)");
                                    long startTimeCreating = System.currentTimeMillis();
                                    this.nextHeatMapImage = this.mainPanel.getJFXPanel().updateHeatMapToImage();
                                    created = true;
                                    long timeCreating = System.currentTimeMillis() - startTimeCreating;
                                    try {

                                        Thread.sleep(new Double((this.updateHeatMapTime - timeCreating) * 0.5).longValue());

                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(EyeTribeDHPlayer.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    System.out.println("heatmap created");
                                }
                                System.out.println("heatmap monitor waiting...");
                                if (this.currentTime - this.start >= this.nextUpdate) {
                                    System.out.println("(this.currentTime - this.start >= this.nextUpdate)?:" + (this.currentTime - this.start) + ">=" + this.nextUpdate);

                                    System.out.println("replacing heatmap ...(if_2)");
                                    this.nextUpdate = this.nextUpdate + this.updateHeatMapTime;
                                    this.mainPanel.getJFXPanel().setHeatMap(nextHeatMapImage);
                                    created = false;
                                    System.out.println("heatmap replaced");
                                }
                                //}
                            }

                        });

                        mainPanel.getJFXPanel().setEndRange(millis + this.updateHeatMapTime);
                        this.nextUpdate = this.updateHeatMapTime;
                        this.heatMapMonitor.start();

                    }
                    //this.nextUpdate = this.updateHeatMapTime;
                }

                this.mainPanel.playVideo();
                this.videoIsPlaying = true;

            }

        }

        if (!this.isFinalized) {
            while (millis >= this.current.timeStamp) {

                this.currentCount++;
                if (this.currentCount < this.data.size()) {
                    this.current = this.data.get(this.currentCount);
                    this.mainPanel.addData(current);

                } else {
                    //esto es para salir del while sin utilizar un break
                    millis = 0;
                }
            }
        } else {
            this.isFinalized = false;
            this.updateHeatMapTime = 0;
            this.firstPlay = true;
            play(this.start);
        }

        mainPanel.setCurrentTime(millis);
        mainPanel.getJFXPanel().setEndRange(millis + this.updateHeatMapTime);//sumar tiempo de actualizacion

    }

    @Override
    public void pause() {

        //this.pause = true;
        //this.heatMapMonitor = null;
        if (this.videoIsPlaying & !isSync) {
            this.mainPanel.pauseVideo();
            this.videoIsPlaying = false;
        }
        this.mainPanel.pauseData();
        this.mainPanel.setCurrentTime(this.current.timeStamp);
    }

    @Override
    public void seek(long millis) {

        this.mainPanel.reset();
        this.current = this.getCompatibleData(data, millis);

        if (!this.videoIsPlaying) {
            this.mainPanel.playVideo();
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(EyeTribeDHPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.mainPanel.pauseVideo();
        }
        if (isSync) {
            this.mainPanel.cleanLastPlayLimit();
            this.frameCount = this.getActualFrameCount(millis);
            //reconstruir data en modo sync
            if (this.currentTime < millis) {

                this.nextUpdate = this.end + 10000;

                long mod = ((millis - offset) % this.updateHeatMapTime);
                long lastUpdate = millis - mod;

                System.out.print("mod: " + mod);

                long nextUpdateTimestamp = lastUpdate + this.updateHeatMapTime;
                System.out.println("lastupdate + nextupdate : " + (lastUpdate - this.offset) + ":" + (nextUpdateTimestamp - this.offset));

                mainPanel.setCurrentTime(millis);
                mainPanel.getJFXPanel().setEndRange(lastUpdate);//sumar tiempo de actualizacion            
                this.mainPanel.getJFXPanel().setHeatMap(this.nextHeatMapImage = this.mainPanel.getJFXPanel().updateHeatMapToImage());
                this.mainPanel.getJFXPanel().setEndRange(nextUpdateTimestamp);//sumar tiempo de actualizacion                            
                this.nextHeatMapImage = this.nextHeatMapImage = this.mainPanel.getJFXPanel().updateHeatMapToImage();
                this.nextUpdate = nextUpdateTimestamp - this.offset;

            } else {

                this.currentTime = millis;
                long mod = ((millis - offset) % this.updateHeatMapTime);
                long lastUpdate = millis - mod;

                System.out.print("mod: " + mod);

                long nextUpdateTimestamp = lastUpdate + this.updateHeatMapTime;
                System.out.println("lastupdate + nextupdate : " + (lastUpdate - this.offset) + ":" + (nextUpdateTimestamp - this.offset));

                mainPanel.setCurrentTime(millis);
                mainPanel.getJFXPanel().setEndRange(lastUpdate);//sumar tiempo de actualizacion            
                this.mainPanel.getJFXPanel().setHeatMap(this.nextHeatMapImage = this.mainPanel.getJFXPanel().updateHeatMapToImage());
                this.mainPanel.getJFXPanel().setEndRange(nextUpdateTimestamp);//sumar tiempo de actualizacion                            
                this.nextHeatMapImage = this.nextHeatMapImage = this.mainPanel.getJFXPanel().updateHeatMapToImage();
                this.nextUpdate = nextUpdateTimestamp - this.offset;

            }

        } else {
            System.out.println("update bucle data");
            //reconstruir mapa en modo normal
            if (this.currentTime < millis) {

                this.nextUpdate = this.end + 10000;

                long mod = ((millis - offset) % this.updateHeatMapTime);
                long lastUpdate = millis - mod;

                System.out.print("mod: " + mod);

                long nextUpdateTimestamp = lastUpdate + this.updateHeatMapTime;
                System.out.println("lastupdate + nextupdate : " + (lastUpdate - this.offset) + ":" + (nextUpdateTimestamp - this.offset));

                mainPanel.setCurrentTime(millis);
                mainPanel.getJFXPanel().setEndRange(lastUpdate);//sumar tiempo de actualizacion            
                this.mainPanel.getJFXPanel().setHeatMap(this.nextHeatMapImage = this.mainPanel.getJFXPanel().updateHeatMapToImage());
                this.mainPanel.getJFXPanel().setEndRange(nextUpdateTimestamp);//sumar tiempo de actualizacion                            
                this.nextHeatMapImage = this.nextHeatMapImage = this.mainPanel.getJFXPanel().updateHeatMapToImage();
                this.nextUpdate = nextUpdateTimestamp - this.offset;

            } else {

                this.currentTime = millis;
                long mod = ((millis - offset) % this.updateHeatMapTime);
                long lastUpdate = millis - mod;

                System.out.print("mod: " + mod);

                long nextUpdateTimestamp = lastUpdate + this.updateHeatMapTime;
                System.out.println("lastupdate + nextupdate : " + (lastUpdate - this.offset) + ":" + (nextUpdateTimestamp - this.offset));

                mainPanel.setCurrentTime(millis);
                mainPanel.getJFXPanel().setEndRange(lastUpdate);//sumar tiempo de actualizacion            
                this.mainPanel.getJFXPanel().setHeatMap(this.nextHeatMapImage = this.mainPanel.getJFXPanel().updateHeatMapToImage());
                this.mainPanel.getJFXPanel().setEndRange(nextUpdateTimestamp);//sumar tiempo de actualizacion                            
                this.nextHeatMapImage = this.nextHeatMapImage = this.mainPanel.getJFXPanel().updateHeatMapToImage();
                this.nextUpdate = nextUpdateTimestamp - this.offset;

            }

        }
        this.mainPanel.seekVideo(millis - offset);

    }

    @Override
    public void stop() {
        this.mainPanel.stop();
        this.videoIsPlaying = false;
        this.isFinalized = true;
        this.heatMapMonitor = null;
        this.currentCount = 0;
        this.current = this.data.get(currentCount);
        //this.nextUpdate = this.updateHeatMapTime;
        this.nextUpdate = 0;
        this.frameCount = 0;
        if (this.isSync) {
            this.mainPanel.cleanLastPlayLimit();
        }

    }

    ////////////////////////////////7
    private long t;

    public long getT() {
        return t;
    }

    public void sumT(long i) {
        t = t + i;
    }

    public GazeData getCompatibleData(ArrayList<GazeData> data, long millis) {

        for (int i = 0; i < data.size(); i++) {

            this.mainPanel.addDataWithoutAois(data.get(i));
            if (data.get(i).timeStamp >= millis) {
                this.currentCount = i;
                return data.get(i);
            }
        }

        return null;
    }

    @Override
    public void sync(boolean bln) {
        this.isSync = bln;
        if (!isSync) {
            this.frameCount = -1;
            this.mainPanel.cleanLastPlayLimit();
            /*if(this.videoIsPlaying){
                this.mainPanel.playVideo();
            }*/
            synchronized (this.isSync) {
                this.isSync.notify();
            }

        } else {

        }
    }

    private ArrayList<Long> loadFrames(File file) {

        ArrayList<Long> framesReads = new ArrayList<Long>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line = reader.readLine();

            while (line != null) {

                framesReads.add(Long.parseLong(line));
                line = reader.readLine();
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(EyeTribeDHPlayer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EyeTribeDHPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return framesReads;
    }

    public int getActualFrameCount(Long currentTime) {
        int i = 0;
        for (Long frame : frames) {
            if (currentTime <= frame) {
                return i;
            }
            i++;
        }
        return i;
    }

}
