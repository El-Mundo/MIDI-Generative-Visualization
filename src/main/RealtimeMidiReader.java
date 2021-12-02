package main;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class RealtimeMidiReader implements Receiver {
	//This reader receives commands from playing MIDI to allow real time display
	
	Sequencer sequencer;
	long tickLength;
	int totalChannels;
	Main parent;
	
	public RealtimeMidiReader(Sequencer sequencer, Main parent) {
		this.sequencer = sequencer;
		this.tickLength = sequencer.getTickLength();
		this.totalChannels = this.getSequenceChannels();
		System.out.println("Real time MIDI reader started!\nChannels: " + this.totalChannels + "\n"
				+ "Tick length: " + this.tickLength + "\n");
		this.parent = parent;
	}
	
	public void start() throws MidiUnavailableException {
		sequencer.getTransmitter().setReceiver(this);
	}

	@Override
	public void send(MidiMessage message, long timeStamp) {
		if(message instanceof ShortMessage) {
	        ShortMessage sm = (ShortMessage) message;
	        int channel = sm.getChannel();

	        if(sm.getCommand() == MidiConverter.NOTE_ON) {
	        	int key = sm.getData1();
	        	parent.startNote(key, channel);
	        	System.out.println("Channel " + channel + ": " + key);
	        }else if (sm.getCommand() == MidiConverter.NOTE_OFF) {
	        	System.out.println("END NOTE #" + channel);
	        	parent.endNote(channel);
	        }
	    }
	}
	
	public long getCurrentTick() {
		return sequencer.getTickPosition();
	}

	@Override
	public void close() {
		sequencer.close();
		Main.endPdfStream();
	}
	
	private int getSequenceChannels() {
		int biggestChannel = 0;
		for (Track track : this.sequencer.getSequence().getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if (sm.getCommand() == 0x90 || sm.getCommand() == 0x80) {
                    	int chn = sm.getChannel();
                        if(chn > biggestChannel) {
                        	biggestChannel = chn;
                        }
                    }
                }
            }
		}
		return biggestChannel;
	}
	
}
