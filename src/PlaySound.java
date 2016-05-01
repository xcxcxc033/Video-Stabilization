//package org.wikijava.sound.playWave;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.DataLine.Info;

/**
 * 
 * <Replace this with a short description of the class.>
 * 
 * @author Giulio
 */
public class PlaySound {

    private InputStream waveStream;
    private String filename;
    int readBytes = 0;
    

    private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
    byte[] buffer = null;
    /**
     * CONSTRUCTOR
     */
    public PlaySound(byte[] waveStream, String filename) {
	//this.waveStream = new BufferedInputStream(waveStream);
    	this.buffer = waveStream;
    	this.filename = filename;
    }

    //Peter
    
    SourceDataLine dataLine = null;
    Clip clip = null;
    //Peter
    
    
	public void play() throws PlayWaveException, IOException {

	AudioInputStream audioInputStream = null;
	AudioInputStream wav_file_format = null;
	File audio = null;
	try {
	    //audioInputStream = AudioSystem.getAudioInputStream(this.waveStream);
		audio = new File(this.filename); 
	    wav_file_format = AudioSystem.getAudioInputStream(audio);
	    //AudioSystem.getAudio
	    
	    clip = AudioSystem.getClip();
	} catch (UnsupportedAudioFileException e1) {
	    throw new PlayWaveException(e1);
	} catch (IOException e1) {
	    throw new PlayWaveException(e1);
	} catch (LineUnavailableException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	// Obtain the information about the AudioInputStream
	
	//AudioFormat audioFormat = audioInputStream.getFormat();
	//Info info = new Info(SourceDataLine.class, audioFormat);

	// opens the audio channel
	//SourceDataLine dataLine = null; //Peter Delete
	
//	for(int i = 0; i < buffer.length/10; i++){
//		System.out.println(buffer[i] + "  " + i);
//	}
	System.out.println(audio.length());
	System.out.println(Integer.MAX_VALUE);
	
	
	try {
		clip.open(wav_file_format.getFormat(), buffer ,0,(int) audio.length());
//	    dataLine = (SourceDataLine) AudioSystem.getLine(info);
//	    dataLine.open(audioFormat, this.EXTERNAL_BUFFER_SIZE);
	} catch (LineUnavailableException e1) {
	    throw new PlayWaveException(e1);
	}


    }
	
	public void startOrResume(){
		if(clip == null){
			try {
				this.play();
			} catch (PlayWaveException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			this.Resume();
		}
	}
	//peter
		public void Stop(){
//			dataLine.stop();
			clip.stop();
			System.out.println("stop");
		}
		
		public void Resume(){
			clip.start();
			
//			dataLine.start();
			System.out.println("resume");
		}
		//peter
		
		
	
}
