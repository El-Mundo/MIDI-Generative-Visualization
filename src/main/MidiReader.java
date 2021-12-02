package main;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

public class MidiReader {
	private static Sequencer sequencer;
	private static Sequence sequence;
	private static File midiFile;
	private static RealtimeMidiReader realtimeReader;
	
	public static void initSequencer() {
		try {
			sequencer = MidiSystem.getSequencer();
			if (sequencer == null) {
	            System.err.println("Sequencer device not supported");
	            return;
			}
			sequencer.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadMidi(String path) {
			try {
				midiFile = new File(path);
				sequence = MidiSystem.getSequence(midiFile);
				sequencer.setSequence(sequence);
			} catch (InvalidMidiDataException | IOException e) {
				e.printStackTrace();
			}
	}
	
	public static void playMidi() {
		if(midiFile == null) return;
		if(!Main.streamPDF) {
			MidiConverter.convertMidi(sequence);
		}
        sequencer.start();
	}
	
	public static void streamMidi(Main parent) {
		realtimeReader = new RealtimeMidiReader(sequencer, parent);
		try {
			realtimeReader.start();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public static RealtimeMidiReader getReatimeMidiReader() {
		return realtimeReader;
	}
	
	public static void checkStreamEnd() {
		if(sequencer.getTickPosition() >= sequence.getTickLength()) {
			realtimeReader.close();
		}
	}
	
	public static String getAbsolutePath() {
		return midiFile.getAbsolutePath();
	}
	
}
