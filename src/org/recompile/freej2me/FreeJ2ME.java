/*
	This file is part of FreeJ2ME.

	FreeJ2ME is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	FreeJ2ME is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with FreeJ2ME.  If not, see http://www.gnu.org/licenses/
*/
package org.recompile.freej2me;

import org.recompile.mobile.*;

import java.awt.Image;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ProcessBuilder;

import java.io.FilenameFilter;
import javax.imageio.ImageIO;
import javax.microedition.lcdui.Display;
import javax.sound.sampled.AudioSystem;
import java.util.HashMap;
import java.util.Map;

public class FreeJ2ME
{

	public static void main(String[] args)
	{
		FreeJ2ME app = new FreeJ2ME(args);
	}

	private SDL sdl;

	private int lcdWidth;
	private int lcdHeight;
	private Config config;
	private boolean useNokiaControls = true;
	private boolean useSiemensControls = false;
	private boolean useMotorolaControls = false;
	private boolean rotateDisplay = false;
	private int limitFPS = 0;

	private boolean[] pressedKeys = new boolean[128];

	private Runnable painter;

	public FreeJ2ME(String args[])
	{
		lcdWidth = 240;
		lcdHeight = 320;
		String mainClassOverride = null;
		Map<String, String> appProperties = new HashMap<>();
		Map<String, String> systemPropertyOverrides = new HashMap<>();
		Map<String, String> configOverrides = new HashMap<>();

		if (args.length > 2)
		{
			lcdWidth = Integer.parseInt(args[1]);
			lcdHeight = Integer.parseInt(args[2]);
			System.out.println("W H from parameters provided");
		} else {
			if (args[0].contains("320x240")) {
				lcdWidth = 320;
				lcdHeight = 240;
			} else if (args[0].contains("240x320")) {
				lcdWidth = 240;
				lcdHeight = 320;
			} else if (args[0].contains("128x128")) {
				lcdWidth = 128;
				lcdHeight = 128;
			} else if (args[0].contains("176x208")) {
				lcdWidth = 176;
				lcdHeight = 208;
			} else if (args[0].contains("176x220")) {
				lcdWidth = 176;
				lcdHeight = 220;
			} else if (args[0].contains("360x640")) {
				lcdWidth = 360;
				lcdHeight = 640;
			} else if (args[0].contains("640x360")) {
				lcdWidth = 640;
				lcdHeight = 360;
			}

			if (args[0].contains("motorola"))
				useNokiaControls = true;
			else if (args[0].contains("siemens"))
				useSiemensControls = true;
			else
				useNokiaControls = true;
		}
		appProperties.put("copyRight", "0");
		Mobile.setPlatform(new MobilePlatform(lcdWidth, lcdHeight));

//		config = new Config();
//		config.onChange = new Runnable() { public void run() { settingsChanged(); } };

	//	lcdWidth = Integer.parseInt(config.settings.get("width"));
	//	lcdHeight = Integer.parseInt(config.settings.get("height"));

		painter = new Runnable()
		{
			public void run()
			{
				try
				{
					// Send Frame to SDL interface
					int[] data = Mobile.getPlatform().getLCD().getRGB(0, 0, lcdWidth, lcdHeight, null, 0, lcdWidth);
					byte[] frame = new byte[data.length * 3];
					int cb = 0;
					for(int i = 0; i < data.length; i++)
					{
						frame[cb + 0] = (byte)(data[i] >> 16);
						frame[cb + 1] = (byte)(data[i] >> 8);
						frame[cb + 2] = (byte)(data[i]);
						cb += 3;
					}
					sdl.frame.write(frame);
				}
				catch (Exception e) { }
			}
		};

		Mobile.setDisplay(new Display());
		Mobile.getPlatform().setPainter(painter);

		Mobile.getPlatform().startEventQueue();

		Mobile.getPlatform().setSystemPropertyOverrides(systemPropertyOverrides);
		System.setProperty("microedition.sensor.version", "1");
		System.setProperty("microedition.platform", "Nokia 6233");
		System.setProperty("microedition.configuration", "CDLC1.1");
		System.setProperty("microedition.profiles", "MIDP2.0");
		System.setProperty("microedition.m3g.version", "1.1");
		System.setProperty("microedition.media.version", "1.0");
		System.setProperty("supports.mixing", "true");
		System.setProperty("supports.audio.capture", "false");
		System.setProperty("supports.video.capture", "false");
		System.setProperty("supports.recording", "false");
		System.setProperty("microedition.pim.version", "1.0");
		System.setProperty("microedition.io.file.FileConnection.version", "1.0");
		//System.setProperty("microedition.locale", locale.toLowerCase());
		System.setProperty("microedition.encoding", "ISO-8859-1");

		System.setProperty("device.imei", "000000000000000");
		System.setProperty("com.siemens.IMEI", "000000000000000");
		System.setProperty("com.sonyericsson.imei: IMEI", "00460101-501594-5-00");
		System.setProperty("com.nokia.mid.impl.isa.visual_radio_operator_id", "0");
		System.setProperty("com.nokia.mid.impl.isa.visual_radio_channel_freq", "0");
		System.setProperty("com.nokia.mid.ui.DirectGraphics.PIXEL_FORMAT", "565");
		System.setProperty("wireless.messaging.sms.smsc", "+8613800010000");

		//System.setProperty("user.home", Environment.getExternalStorageDirectory().getAbsolutePath());

		String file = getFormattedLocation(args[0]);
		System.out.println(file);

		if(Mobile.getPlatform().load(args[0], appProperties, mainClassOverride))
		{
	//		config.init(configOverrides);

			sdl = new SDL();
			sdl.start(args);

			Mobile.getPlatform().runJar();
		}
		else
		{
			System.out.println("Couldn't load jar...");
			System.exit(0);
		}
		if (useNokiaControls)
			Mobile.nokia = true;
		else if (useSiemensControls)
			Mobile.siemens = true;
		if (Mobile.nokia) {
			Mobile.getPlatform().addSystemProperty("microedition.platform", "Nokia6233/05.10");
		} else if (Mobile.sonyEricsson) {
			Mobile.getPlatform().addSystemProperty("microedition.platform", "SonyEricssonK750/JAVASDK");
			Mobile.getPlatform().addSystemProperty("com.sonyericsson.imei", "IMEI 00460101-501594-5-00");
		} else if (Mobile.siemens) {
			Mobile.getPlatform().addSystemProperty("com.siemens.OSVersion", "11");
			Mobile.getPlatform().addSystemProperty("com.siemens.IMEI", "000000000000000");
		}
	}

	private static void processKeyValuePairs(String[] args, Map<String, String> map, int index) {
		if (index < args.length) {
			String[] pair = args[index].split("=");
			if (pair.length == 2) {
				map.put(pair[0], pair[1]);
			}
		}
	}

	private void settingsChanged()
	{
		int w = Integer.parseInt(config.settings.get("width"));
		int h = Integer.parseInt(config.settings.get("height"));

		limitFPS = Integer.parseInt(config.settings.get("fps"));
		if(limitFPS>0) { limitFPS = 1000 / limitFPS; }
		System.out.println("settingsChanged..." + w);

		String sound = config.settings.get("sound");
		Mobile.sound = false;
		if(sound.equals("on")) { 
			if (AudioSystem.getMixerInfo().length > 0) {
				Mobile.sound = true;
			} else {
				System.out.println("no audio devices found");
			}

		}

		String phone = config.settings.get("phone");
		useNokiaControls = false;
		useSiemensControls = false;
		useMotorolaControls = false;
		Mobile.sonyEricsson = false;
		Mobile.nokia = false;
		Mobile.siemens = false;
		Mobile.motorola = false;
		if(phone.equals("Nokia")) { Mobile.nokia = true; useNokiaControls = true; }
		if(phone.equals("Siemens")) { Mobile.siemens = true; useSiemensControls = true; }
		if(phone.equals("Motorola")) { Mobile.motorola = true; useMotorolaControls = true; }
		if(phone.equals("SonyEricsson")) { Mobile.sonyEricsson = true; useNokiaControls = true; }

		String rotate = config.settings.get("rotate");
		if(rotate.equals("on")) { rotateDisplay = true; }
		if(rotate.equals("off")) { rotateDisplay = false; }

		boolean isForceFullscreen = config.settings.getOrDefault("forceFullscreen", "off").equals("on");
		boolean isForceVolatile = config.settings.getOrDefault("forceVolatileFields", "off").equals("on");
		String dgFormat = config.settings.getOrDefault("dgFormat", "default");

		System.setProperty("freej2me.forceFullscreen", isForceFullscreen ? "true" : "false");
		System.setProperty("freej2me.forceVolatileFields", isForceVolatile ? "true" : "false");

		if (dgFormat.equals("default")) {
			System.clearProperty("freej2me.dgFormat");
		} else {
			System.setProperty("freej2me.dgFormat", dgFormat);
		}

		// Create a standard size LCD if not rotated, else invert window's width and height.
		if(!rotateDisplay) 
		{
			lcdWidth = w;
			lcdHeight = h;

			Mobile.getPlatform().resizeLCD(w, h);
			
//			resize();
///			main.setSize(lcdWidth*scaleFactor+xborder , lcdHeight*scaleFactor+yborder);
		}
		else 
		{
			lcdWidth = h;
			lcdHeight = w;

			Mobile.getPlatform().resizeLCD(w, h);

//			resize();
//			main.setSize(lcdWidth*scaleFactor+xborder , lcdHeight*scaleFactor+yborder);
		}

		if (Mobile.nokia) {
			Mobile.getPlatform().addSystemProperty("microedition.platform", "Nokia6233/05.10");
		} else if (Mobile.sonyEricsson) {
			Mobile.getPlatform().addSystemProperty("microedition.platform", "SonyEricssonK750/JAVASDK");
			Mobile.getPlatform().addSystemProperty("com.sonyericsson.imei", "IMEI 00460101-501594-5-00");
		} else if (Mobile.siemens) {
			Mobile.getPlatform().addSystemProperty("com.siemens.OSVersion", "11");
			Mobile.getPlatform().addSystemProperty("com.siemens.IMEI", "000000000000000");
		}
	}

	private static String getFormattedLocation(String loc)
	{
		if (loc.startsWith("file://") || loc.startsWith("http://"))
			return loc;

		File file = new File(loc);
		if(!file.exists() || file.isDirectory())
		{
			System.out.println("File not found...");
			System.exit(0);
		}

		return "file://" + file.getAbsolutePath();
	}

	private class SDL
	{
		private Timer keytimer;
		private TimerTask keytask;

		private Process proc;
		private InputStream keys;
		public OutputStream frame;

		public void start(String args[])
		{
			try
			{
				String[] new_args = new String[5];
				new_args[0] = "/storage/sdl_interface";
				new_args[1] = String.valueOf(lcdWidth);
				new_args[2] = String.valueOf(lcdHeight);
				new_args[3] = "-b";
				if (lcdWidth > lcdHeight)
					new_args[4] = "/storage/roms/bezels/java/java_h.png";
				else
					new_args[4] = "/storage/roms/bezels/java/java_v.png";
				//String new_args[] = {"/storage/sdl_interface", String.valueOf(lcdWidth), String.valueOf(lcdHeight), "-b", "/storage/roms/bezels/java/java_h.png"};
				//System.out.println(args[0] + args[1] + args[2]);

				proc = new ProcessBuilder(new_args).start();

				keys = proc.getInputStream();
				frame = proc.getOutputStream();

				keytimer = new Timer();
				keytask = new SDLKeyTimerTask();
				keytimer.schedule(keytask, 0, 5);
			}
			catch (Exception e)
			{
				System.out.println("Failed to start sdl_interface");
				System.out.println(e.getMessage());
				System.exit(0);
			}
		}

		public void stop()
		{
			proc.destroy();
			keytimer.cancel();
		}

		private class SDLKeyTimerTask extends TimerTask
		{
			private int bin;
			private byte[] din = new byte[6];
			private int count = 0;
			private int code;
			private int mobikey;
			private int mobikeyN;

			public void run()
			{
				try // to read keys
				{
					while(true)
					{
						bin = keys.read();
						if(bin==-1) { return; }
						if(bin != 16 && bin != 17 && count == 0)
							continue;
						//System.out.print(" "+bin);
						din[count] = (byte)(bin & 0xFF);
						count++;
						if (count==5)
						{
							count = 0;
							code = (din[1]<<24) | (din[2]<<16) | (din[3]<<8) | din[4];
							//System.out.println(" ("+code+") <- Key");
							mobikey = getMobileKeyRGB30(code);
							/*
							switch(din[3] >>> 1)
							{
								case 0: mobikey = getMobileKey(code); break;
								case 1: mobikey = getMobileKeyPad(code); break;
								case 2: mobikey = getMobileKeyJoy(code); break;
								default: continue;
							}*/
							//System.out.println(" ("+ mobikey +") <- mobikey");
							if (mobikey == 0) //Ignore events from keys not mapped to a phone keypad key
							{
								return; 
							}
							mobikeyN = (mobikey + 64) & 0x7F; //Normalized value for indexing the pressedKeys array
							if (din[0] == 16)
							{
								//Key released
								//~ System.out.println("keyReleased:  " + Integer.toString(mobikey));
								Mobile.getPlatform().keyReleased(mobikey, null);
								pressedKeys[mobikeyN] = false;
							}
							else if (din[0] == 17)
							{
								//Key pressed or repeated
								if (pressedKeys[mobikeyN] == false)
								{
									//~ System.out.println("keyPressed:  " + Integer.toString(mobikey));
									Mobile.getPlatform().keyPressed(mobikey, null);
								}
								else
								{
									//~ System.out.println("keyRepeated:  " + Integer.toString(mobikey));
									Mobile.getPlatform().keyRepeated(mobikey, null);
								}
								pressedKeys[mobikeyN] = true;
							}
						}
					}
				}
				catch (Exception e) { }
			}
		} // timer

		private int getMobileKeyRGB30(int keycode)
		{
			switch(keycode & 0xffff)
			{
				case 2: return Mobile.KEY_NUM0;
				case 4: return Mobile.KEY_NUM1;
				case 13: return Mobile.KEY_NUM2;
				case 5: return Mobile.KEY_NUM3;
				case 15: return Mobile.KEY_NUM4;
				case 3: return Mobile.KEY_NUM5;
				case 16: return Mobile.KEY_NUM6;
				case 6: return Mobile.KEY_NUM7;
				case 14: return Mobile.KEY_NUM8;
				case 7: return Mobile.KEY_NUM9;
				case 0: return Mobile.KEY_STAR;
				case 1: return Mobile.KEY_POUND;

				case 8: return Mobile.NOKIA_SOFT1;
				case 9: return Mobile.NOKIA_SOFT2;

				//case 4: return Mobile.GAME_A;
				//case 5: return Mobile.GAME_B;
				//case 6: return Mobile.NOKIA_END;
				//case 7: return Mobile.NOKIA_SEND;

				// ESC - Quit
				case 11: System.exit(0);

				case 12: ScreenShot.takeScreenshot(false);

				/*
				case : return Mobile.GAME_UP;
				case : return Mobile.GAME_DOWN;
				case : return Mobile.GAME_LEFT;
				case : return Mobile.GAME_RIGHT;
				case : return Mobile.GAME_FIRE;

				case : return Mobile.GAME_A;
				case : return Mobile.GAME_B;
				case : return Mobile.GAME_C;
				case : return Mobile.GAME_D;

				// Nokia //
				case : return Mobile.NOKIA_UP;
				case : return Mobile.NOKIA_DOWN;
				case : return Mobile.NOKIA_LEFT;
				case : return Mobile.NOKIA_RIGHT;
				case : return Mobile.NOKIA_SOFT1;
				case : return Mobile.NOKIA_SOFT2;
				case : return Mobile.NOKIA_SOFT3;
				*/
			}
			return Mobile.KEY_NUM5;
		}

		private int getMobileKey(int keycode)
		{
			switch(keycode)
			{
				case 0x30: return Mobile.KEY_NUM0;
				case 0x31: return Mobile.KEY_NUM1;
				case 0x32: return Mobile.KEY_NUM2;
				case 0x33: return Mobile.KEY_NUM3;
				case 0x34: return Mobile.KEY_NUM4;
				case 0x35: return Mobile.KEY_NUM5;
				case 0x36: return Mobile.KEY_NUM6;
				case 0x37: return Mobile.KEY_NUM7;
				case 0x38: return Mobile.KEY_NUM8;
				case 0x39: return Mobile.KEY_NUM9;
				case 0x2A: return Mobile.KEY_STAR;
				case 0x23: return Mobile.KEY_POUND;

				case 0x40000052: return Mobile.KEY_NUM2;
				case 0x40000051: return Mobile.KEY_NUM8;
				case 0x40000050: return Mobile.KEY_NUM4;
				case 0x4000004F: return Mobile.KEY_NUM6;

				case 0x0D: return Mobile.KEY_NUM5;

				case 0x71: return Mobile.NOKIA_SOFT1;
				case 0x77: return Mobile.NOKIA_SOFT2;
				case 0x65: return Mobile.KEY_STAR;
				case 0x72: return Mobile.KEY_POUND;


				// Inverted Num Pad
				case 0x40000059: return Mobile.KEY_NUM7; // SDLK_KP_1
				case 0x4000005A: return Mobile.KEY_NUM8; // SDLK_KP_2
				case 0x4000005B: return Mobile.KEY_NUM9; // SDLK_KP_3
				case 0x4000005C: return Mobile.KEY_NUM4; // SDLK_KP_4
				case 0x4000005D: return Mobile.KEY_NUM5; // SDLK_KP_5
				case 0x4000005E: return Mobile.KEY_NUM6; // SDLK_KP_6
				case 0x4000005F: return Mobile.KEY_NUM1; // SDLK_KP_7
				case 0x40000060: return Mobile.KEY_NUM2; // SDLK_KP_8
				case 0x40000061: return Mobile.KEY_NUM3; // SDLK_KP_9
				case 0x40000062: return Mobile.KEY_NUM0; // SDLK_KP_0


				// F4 - Quit
				case -1: System.exit(0);

				// ESC - Quit
				case 0x1B: System.exit(0);

				case 112: ScreenShot.takeScreenshot(true);

				/*
				case : return Mobile.GAME_UP;
				case : return Mobile.GAME_DOWN;
				case : return Mobile.GAME_LEFT;
				case : return Mobile.GAME_RIGHT;
				case : return Mobile.GAME_FIRE;

				case : return Mobile.GAME_A;
				case : return Mobile.GAME_B;
				case : return Mobile.GAME_C;
				case : return Mobile.GAME_D;

				// Nokia //
				case : return Mobile.NOKIA_UP;
				case : return Mobile.NOKIA_DOWN;
				case : return Mobile.NOKIA_LEFT;
				case : return Mobile.NOKIA_RIGHT;
				case : return Mobile.NOKIA_SOFT1;
				case : return Mobile.NOKIA_SOFT2;
				case : return Mobile.NOKIA_SOFT3;
				*/
			}
			return 0;
		}

		private int getMobileKeyPad(int keycode)
		{
			switch(keycode)
			{
				//  A:1 B:0 X: 3 Y:2 L:4 R:5 St:6 Sl:7
				case 0x03: return Mobile.KEY_NUM0;
				case 0x02: return Mobile.KEY_NUM5;
				case 0x00: return Mobile.KEY_STAR;
				case 0x01: return Mobile.KEY_POUND;

				case 0x04: return Mobile.KEY_NUM1;
				case 0x05: return Mobile.KEY_NUM3;

				case 0x06: return Mobile.NOKIA_SOFT1;
				case 0x07: return Mobile.NOKIA_SOFT2;
			}
			return 0;
		}

		private int getMobileKeyJoy(int keycode)
		{
			switch(keycode)
			{
				case 0x04: return Mobile.KEY_NUM2;
				case 0x01: return Mobile.KEY_NUM4;
				case 0x02: return Mobile.KEY_NUM6;
				case 0x08: return Mobile.KEY_NUM8;
			}
			return 0;
		}

	} // sdl

}
