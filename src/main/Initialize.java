package main;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Font;

import processing.core.PApplet;

public class Initialize {
	private JFrame window;
	private JLabel pathLabel;
	private ButtonGroup buttonGroup;
	private JRadioButton[] buttons;
	private JButton fileButton, confirmButton;
	private JPanel pathPanel, numPanel, confirmPanel;
	private static JFileChooser chooser;
	final private static FileNameExtensionFilter EXTENSION_FILTER = new FileNameExtensionFilter("Midi music (*.mid)", "mid");
	final private static Font INIT_FONT = new Font("Arial", Font.BOLD, 18);
	
	public void loadGUI(int screenHeight) {
		window = new JFrame("Start Midi");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pathPanel = new JPanel();
		numPanel = new JPanel();
		confirmPanel = new JPanel();
		
		switchVisualStyle(Main.DEFAULT_VISUAL_STYLE);
		
		//For selecting midi file
		pathPanel.setLayout(new GridLayout(2, 1));
		JPanel tempPanel = new JPanel();
		fileButton = new JButton("Select a Midi File");
		fileButton.setFont(INIT_FONT);
		fileButton.addActionListener(selectFileListener);
		tempPanel.add(fileButton);
		pathPanel.add(tempPanel);
		pathLabel = new JLabel("Current file path: " + inputString, JLabel.CENTER);
		pathPanel.add(pathLabel);
		chooser = new JFileChooser();
		chooser.setFileFilter(EXTENSION_FILTER);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		
		//For selecting visual style
		numPanel.setLayout(new GridLayout(2, 1));
		JLabel tempLabel = new JLabel("Select a visual style", JLabel.CENTER);
		tempLabel.setFont(INIT_FONT);
		numPanel.add(tempLabel);
		JPanel tempPanel2 = new JPanel();
		buttons = new JRadioButton[5];
		buttonGroup = new ButtonGroup();
		for(int i=0; i<5; i++) {
			String text = (i != 0) ? i+"" : "test";
			buttons[i] = new JRadioButton(text, i == Main.DEFAULT_VISUAL_STYLE);
			buttonGroup.add(buttons[i]);
			tempPanel2.add(buttons[i]);
			buttons[i].setFont(INIT_FONT);
			int num = i;
			buttons[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					switchVisualStyle(num);
					pathLabel.setText("Current file path: " + inputString);
				}
			});
		}
		numPanel.add(tempPanel2);
		
		//For starting Processing 3
		confirmButton = new JButton("Start Processing");
		confirmButton.setFont(INIT_FONT);
		confirmButton.addActionListener(startListener);
		confirmPanel.add(confirmButton);
		
		//Start JFrame initialize window
		window.setLayout(new GridLayout(3, 1));
		window.add(pathPanel);
		window.add(numPanel);
		window.add(confirmPanel);
		window.setSize((int)(screenHeight * 0.7f), (int)(screenHeight * 0.7f));
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
	
	private String inputString;
	private File inputFile;
	
	private ActionListener selectFileListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			//Show file chooser
			chooser.showDialog(window, "Select a file");
			//Get selected file
			if(chooser.getSelectedFile()!=null) {
				inputFile = chooser.getSelectedFile();
				inputString = inputFile.getAbsolutePath();
				pathLabel.setText("Current file path: " + inputString);
				MidiReader.loadMidi(inputString);
			}
		}
	};
	
	private ActionListener startListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(inputFile == null) {
				confirmButton.setText("No Midi File Selected!");
				confirmButton.setForeground(Color.RED);
				fileButton.setForeground(Color.RED);
				return;
			}
			//Check selected visual style
			for(int n=0; n<5;n++) {
				if(buttons[n].isSelected()) {
					Main.setVisualStyle(n);
					break;
				}
			}
			//Init Processing 3
			PApplet.main("main.Main");
			//Close init window
			window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			window.dispose();
		}
	};
	
	private void switchVisualStyle(int i) {
		try {
			//Load midi files in the project folder
			String path = Main.DEFAULT_FILE_PATHS[i];
			inputFile = new File(path);
			MidiReader.loadMidi(path);
			inputString = inputFile.getAbsolutePath();
		}catch(Exception e) {
			//If the midi file is corrupted
			e.printStackTrace();
			inputFile = null;
			inputString = "";
		}
	}

}
