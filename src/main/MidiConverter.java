package main;

import java.util.ArrayList;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/* Code in this file is adapted from sample on StackOverFlow.
 * Credits to: Sami Koivu
 * @see https://stackoverflow.com/questions/3850688/reading-midi-files-in-java
 */
public class MidiConverter {
	final public static int NOTE_ON = 0x90;
    final public static int NOTE_OFF = 0x80;
    final public static String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    public static ArrayList<MidiNote> notes = new ArrayList<MidiNote>();
    private static MidiNote[] channelNotes = new MidiNote[16];
    //Initialize to avoid null pointer
    public static MidiSong song = new MidiSong(1, 1);
    
    public static void convertMidi(Sequence sequence) {
    	int biggestChannel = 0;
    	try {
	        int trackNumber = 0;
	        for (Track track : sequence.getTracks()) {
	            trackNumber++;
	            System.out.println("Track " + trackNumber + ": size = " + track.size());
	            System.out.println();
	            for (int i=0; i < track.size(); i++) {
	                MidiEvent event = track.get(i);
	                MidiMessage message = event.getMessage();
	                if (message instanceof ShortMessage) {
	                    ShortMessage sm = (ShortMessage) message;
	                    if (sm.getCommand() == NOTE_ON) {
	                    	int chn = sm.getChannel();
	                        notes.add(new MidiNote(sm.getData1(), chn, event.getTick()));
	                        if(chn > biggestChannel) {
	                        	biggestChannel = chn;
	                        }
	                    } else if (sm.getCommand() == NOTE_OFF) {
	                    	int chn = sm.getChannel();
	                        if(channelNotes[chn] != null) {
	                        	channelNotes[chn].setNote(event.getTick());
	                        }
	                    } else {
	                        System.out.println("Command:" + sm.getCommand());
	                    }
	                } else {
	                    System.out.println("Other message: " + message.getClass());
	                }
	            }
	            System.out.println();
	        }
        }catch (Exception e) {
			e.printStackTrace();
		}
    	song = new MidiSong(sequence.getTickLength(), biggestChannel + 1);
    	System.out.println("Total notes: "+notes.size());
    	System.out.println(song);
    }
    
    public static class MidiNote {
		public int channel, key;
		public int octave;
		public String noteName;
		public long startTick, tickLength;
		
		//Called when note starts
		public MidiNote(int key, int channel, long startTick) {
			this.channel = channel;
			this.key = key;
			this.octave = (key / 12) - 1;
	        this.noteName = NOTE_NAMES[key % 12];
	        this.startTick = startTick;
	        this.tickLength = 0;
	        //For getting position of ending tick
	        channelNotes[channel] = this;
	        System.out.println(this);
		}
		
		//Called when note cuts off
		public void setNote(long endTick) {
			this.tickLength = endTick - this.startTick;
			System.out.println(this);
		}
		
		@Override
		public String toString() {
			return "Chn"+channel+", tick"+startTick+"-"+(startTick+tickLength)+", "+noteName+octave+".";
		}
		
	}
    
    public static class MidiSong {
    	public long totalTicks;
    	public int totalChannels;
    	
    	public MidiSong(long totalTicks, int  totalChannels) {
    		this.totalTicks = totalTicks;
    		this. totalChannels =  totalChannels;
    	}
    	
    	@Override
    	public String toString() {
			return "Song tick length "+totalTicks+", "+totalChannels+" channels.";
    	}
    }
    
}
