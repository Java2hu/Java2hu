
package java2hu.desktop;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java2hu.allstar.AllStarGame;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SettingsWindow extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField widthField;
	private JTextField heightField;
	private JTextField samplesField;
	private JLabel lblWarning;
	private JCheckBox fullScreenCheckBox;
	
	final String numberError = "Not a valid number (No decimals/letters/symbols etc).\n\nDelete them please.";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		try
		{
			SettingsWindow dialog = new SettingsWindow(false);
			dialog.setVisible(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	boolean dontStartAfterOk = false;
	public boolean hasWarned = false;


	/**
	 * Create the dialog.
	 */
	public SettingsWindow(boolean dontStartAfterOk)
	{
		this.dontStartAfterOk = dontStartAfterOk;
		
		setTitle("Touhou All Star Settings");
		final SettingsWindow window = this;
		
		setBounds(100, 100, 521, 405);
		
		getContentPane().setLayout(null);
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 319, 495, 36);
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			
			getContentPane().add(buttonPane);
			{
				JButton okButton = new JButton("Start!");
				
				okButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						window.dispose();
						
						if(!window.dontStartAfterOk)
						{
							window.setVisible(false);
							start();
						}
						else
						{
							saveSettings();
							JOptionPane.showMessageDialog(null, "Will be applied after you restart the game.");
						}
					}
				});
				
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			
			{
				JButton cancelButton = new JButton("Exit Game");
				cancelButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						Runtime.getRuntime().exit(0);
					}
				});
				
				buttonPane.add(cancelButton);
			}
		}
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 11, 495, 36);
		getContentPane().add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblPresetResolutions = new JLabel("Set to preset resolution:");
		lblPresetResolutions.setFont(new Font("Calibri", Font.PLAIN, 12));
		panel.add(lblPresetResolutions);
		{
			JComboBox<String> comboBox = new JComboBox<String>();
			panel.add(comboBox);
			comboBox.setFont(new Font("Calibri", Font.BOLD, 12));
			
			comboBox.setModel(new DefaultComboBoxModel(new String[] {"1920x1080", "1366x768", "1280x1024", "1280x960", "1280x800", "1024x768", "800x600"}));
			comboBox.setSelectedIndex(3);
			
			comboBox.addItemListener(new ItemListener()
			{
			    @Override
				public void itemStateChanged(ItemEvent event)
			    {
			       if (event.getStateChange() == ItemEvent.SELECTED)
			       {
			    	   String newValue = (String) event.getItem();
			    	   String[] split = newValue.split("x");

			    	   widthField.setText(split[0]);
			    	   heightField.setText(split[1]);
			       }
			    } 
			});
		}
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 58, 495, 48);
		getContentPane().add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblManualResolution = new JLabel("Manual Resolution: ");
		lblManualResolution.setFont(new Font("Calibri", Font.PLAIN, 11));
		panel_1.add(lblManualResolution);
		
		widthField = new JTextField();
		widthField.setFont(new Font("Calibri", Font.PLAIN, 12));
		widthField.setText("1280");
		widthField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				check();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				check();
			}

			public void check()
			{
				String value = widthField.getText();
				
				try
				{
					int x = Integer.parseInt(value);
					int y = Integer.parseInt(heightField.getText());
					
					lblWarning.setEnabled(y < Integer.parseInt(viewportHeight.getText()) || x < Integer.parseInt(viewportWidth.getText()));
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(null, numberError);
				}
			}
		});
		
		panel_1.add(widthField);
		widthField.setColumns(10);
		
		heightField = new JTextField();
		heightField.setFont(new Font("Calibri", Font.PLAIN, 12));
		heightField.setText("960");
		heightField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				check();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				check();
			}

			public void check()
			{
				String value = heightField.getText();
				
				try
				{
					int x = Integer.parseInt(widthField.getText());
					int y = Integer.parseInt(value);
					
					lblWarning.setEnabled(y < Integer.parseInt(viewportHeight.getText()) || x < Integer.parseInt(viewportWidth.getText()));
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(null, numberError);
				}
			}
		});
		
		heightField.setColumns(10);
		panel_1.add(heightField);
		
		lblWarning = new JLabel("Warning: Resolutions below your viewport resolution can have small cropping issues.");
		lblWarning.setFont(new Font("Calibri", Font.BOLD, 12));
		lblWarning.setEnabled(false);
		panel_1.add(lblWarning);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(0, 164, 495, 48);
		getContentPane().add(panel_2);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblShaders = new JLabel("Samples");
		lblShaders.setEnabled(false);
		lblShaders.setFont(new Font("Calibri", Font.PLAIN, 12));
		panel_2.add(lblShaders);
		
		samplesField = new JTextField();
		samplesField.setEnabled(false);
		samplesField.setFont(new Font("Calibri", Font.PLAIN, 12));
		samplesField.setText("0");
		samplesField.setColumns(10);
		
		samplesField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				check();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				check();
			}

			public void check()
			{
				try
				{
					Integer.parseInt(samplesField.getText());
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(null, numberError);
				}
			}
		});
		
		panel_2.add(samplesField);
		
		JLabel lblSamplesIsThe = new JLabel("Samples is the amount of anti aliasing will be done, 4x is recommended.");
		lblSamplesIsThe.setEnabled(false);
		lblSamplesIsThe.setFont(new Font("Calibri", Font.BOLD, 12));
		panel_2.add(lblSamplesIsThe);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(0, 223, 495, 36);
		getContentPane().add(panel_3);
		panel_3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		fullScreenCheckBox = new JCheckBox("Full Screen");
		fullScreenCheckBox.setFont(new Font("Calibri", Font.PLAIN, 12));
		panel_3.add(fullScreenCheckBox);
		
		showOnStartCheckBox = new JCheckBox("Show on startup \n(Accessible through ingame menu)");
		showOnStartCheckBox.setFont(new Font("Calibri", Font.PLAIN, 12));
		showOnStartCheckBox.setSelected(true);
		panel_3.add(showOnStartCheckBox);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(0, 117, 495, 36);
		getContentPane().add(panel_4);
		panel_4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		String viewportMessage = "This game is made to run at a viewport of 1280x960\r\nYou can shrink the game size and the game will scale it's stages accordingly.\r\nBut of course, there's a lot of stuff which won't work right on other viewports.\r\nBut, it can make for fun experiments, so I left it in, enjoy!";
		
		JLabel lblViewportResolution = new JLabel("Viewport Resolution");
		lblViewportResolution.setToolTipText(viewportMessage);
		lblViewportResolution.setFont(new Font("Calibri", Font.PLAIN, 11));
		panel_4.add(lblViewportResolution);
		
		viewportWidth = new JTextField();
		viewportWidth.setToolTipText("This game is made to run at a viewport of 1280x960\r\nYou can shrink the game size and the game will scale it's stages accordingly.\r\nBut of course, there's a lot of stuff which won't work right on other viewports.\r\nBut, it can make for fun experiments, so I left it in, enjoy!");
		viewportWidth.setText("1280");
		viewportWidth.setFont(new Font("Calibri", Font.PLAIN, 12));
		viewportWidth.setColumns(10);
		viewportWidth.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				check();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				check();
			}

			public void check()
			{
				try
				{
					Integer i = Integer.parseInt(viewportWidth.getText());
					
					if(i > Integer.parseInt(widthField.getText()) && !hasWarned)
					{
						JOptionPane.showMessageDialog(null, "I can't guarantee the game will work in\nviewports higher than their screen size.\nUse at your own risk.");
						hasWarned = true;
					}
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(null, numberError);
				}
			}
		});
		panel_4.add(viewportWidth);
		
		viewportHeight = new JTextField();
		viewportHeight.setToolTipText(viewportMessage);
		viewportHeight.setText("960");
		viewportHeight.setFont(new Font("Calibri", Font.PLAIN, 12));
		viewportHeight.setColumns(10);
		viewportHeight.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				check();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				check();
			}
			
			public void check()
			{
				try
				{
					Integer i = Integer.parseInt(viewportHeight.getText());
					
					if(i > Integer.parseInt(heightField.getText()) && !hasWarned)
					{
						JOptionPane.showMessageDialog(null, "I can't guarantee the game will work in\nviewports higher than their screen size.\nUse at your own risk.");
						hasWarned = true;
					}
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(null, numberError);
				}
			}
		});
		panel_4.add(viewportHeight);
		
		JButton btnReset = new JButton("RESET");
		btnReset.setToolTipText(viewportMessage);
		panel_4.add(btnReset);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBounds(0, 272, 495, 36);
		getContentPane().add(panel_5);
		panel_5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		fpsField = new JTextField();
		
		fpsField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				check();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				check();
			}

			public void check()
			{
				try
				{
					Integer.parseInt(fpsField.getText());
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(null, numberError);
				}
			}
		});
		
		fpsField.setText("60");
		panel_5.add(fpsField);
		fpsField.setColumns(3);
		
		JLabel lblFps = new JLabel("FPS");
		panel_5.add(lblFps);
		
		loadSettings();
	}
	
	final File settingsFile = new File("settings.json");
	final Gson gson = new GsonBuilder().serializeNulls().serializeSpecialFloatingPointValues().setPrettyPrinting().create();
	private JCheckBox showOnStartCheckBox;
	private JTextField viewportWidth;
	private JTextField viewportHeight;
	private JTextField fpsField;
	
	public void loadSettings()
	{
		if(settingsFile.exists())
		{
			String json = "";
			
			try
			{
				String last = "";
				
				BufferedReader reader = new BufferedReader(new FileReader(settingsFile));
				
				while((last = reader.readLine()) != null)
					json += last;
				
				reader.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			Settings settings = gson.fromJson(json, Settings.class);
			
			widthField.setText(removeUselessDecimals(String.valueOf(settings.width)));
			heightField.setText(removeUselessDecimals(String.valueOf(settings.height)));
			viewportWidth.setText(removeUselessDecimals(String.valueOf(settings.viewportWidth)));
			viewportHeight.setText(removeUselessDecimals(String.valueOf(settings.viewportHeight)));
			samplesField.setText(removeUselessDecimals(String.valueOf(settings.samples)));
			fullScreenCheckBox.setSelected(settings.fullScreen);
			showOnStartCheckBox.setSelected(settings.showAtStartup);
			fpsField.setText(String.valueOf(settings.fps));
			
			Timer timer = new Timer();
			final SettingsWindow window = this;
			
			timer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					if(!showOnStartCheckBox.isSelected() && !dontStartAfterOk)
					{
						window.dispose();
						start();
					}
				}
			}, 200);
			
			
		}
	}
	 
	public void saveSettings()
	{
		Settings settings = new Settings();
		
		settings.width = Integer.parseInt(widthField.getText());
		settings.height = Integer.parseInt(heightField.getText());
		settings.viewportWidth = Integer.parseInt(viewportWidth.getText());
		settings.viewportHeight = Integer.parseInt(viewportHeight.getText());
		settings.samples = 0;//Integer.parseInt(samplesField.getText());
		settings.fullScreen = fullScreenCheckBox.isSelected();
		settings.showAtStartup = showOnStartCheckBox.isSelected();
		settings.fps = Integer.parseInt(fpsField.getText());
		
		String json = gson.toJson(settings);
		
		try
		{
			FileWriter writer = new FileWriter(settingsFile);
			
			writer.append(json);
			
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void start()
	{
		saveSettings();
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Touhou -  All Star Danmaku";
		
		config.width = Integer.parseInt(widthField.getText());
		config.height = Integer.parseInt(heightField.getText());
		config.samples = Integer.parseInt(samplesField.getText());
		config.fullscreen = fullScreenCheckBox.isSelected();
		config.vSyncEnabled = false;
		config.backgroundFPS = 60;
		config.foregroundFPS = Integer.parseInt(fpsField.getText());
		
		System.out.println("Starting game with \n Width: " + config.width + " - Height: " + config.height + " (" + config.width + "x" + config.height + " @ " + viewportWidth.getText() + "x" + viewportHeight.getText() + "\n Samples: " + config.samples + "x  -  Fullscreen: " + config.fullscreen);
		
		config.audioDeviceSimultaneousSources = 1000000;
		
		new LwjglApplication(new AllStarGame(Integer.parseInt(viewportWidth.getText()), Integer.parseInt(viewportHeight.getText()))
		{
			@Override
			public void onPreGameSettings()
			{
				try
				{
					SettingsWindow dialog = new SettingsWindow(true);
					dialog.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			};
		}, config);
	}
	
	public String removeUselessDecimals(String input)
	{
		if(input.endsWith(".0"))
			input = input.substring(0, input.length() - 2);
		
		return input;
	}
}
