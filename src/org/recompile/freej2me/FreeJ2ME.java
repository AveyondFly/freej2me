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
	private int useFlag=0;
	private int lcdWidth;
	private int lcdHeight;
	private boolean useNokiaControls = true;
	private boolean useSiemensControls = false;
	private boolean useMotorolaControls = false;
	private boolean rotateDisplay = false;
	private int limitFPS = 0;
	private final SDLConfig  config;
	private int fps=16;
	private int showfps=0;
	private final byte []  keyPix={
		//p
		0,0,0,0,0,0,0,0,0,0,0,0,
		0,1,1,1,1,1,1,1,1,1,1,0,
		0,1,1,1,1,1,1,1,1,1,1,0,
		0,1,1,0,0,0,0,0,0,1,1,0,
		0,1,1,0,0,0,0,0,0,1,1,0,
		0,1,1,0,0,0,0,0,0,1,1,0,
		0,1,1,1,1,1,1,1,1,1,1,0,
		0,1,1,1,1,1,1,1,1,1,1,0,
		0,1,1,0,0,0,0,0,0,0,0,0,
		0,1,1,0,0,0,0,0,0,0,0,0,
		0,1,1,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,
		
		//n;
		0,0,0,0,0,0,0,0,0,0,0,0,
		0,1,1,0,0,0,0,0,0,1,1,0,
		0,1,1,1,0,0,0,0,0,1,1,0,
		0,1,1,1,1,0,0,0,0,1,1,0,
		0,1,1,0,1,1,0,0,0,1,1,0,
		0,1,1,0,0,1,1,0,0,1,1,0,
		0,1,1,0,0,0,1,1,0,1,1,0,
		0,1,1,0,0,0,0,1,1,1,1,0,
		0,1,1,0,0,0,0,0,1,1,1,0,
		0,1,1,0,0,0,0,0,0,1,1,0,
		0,1,1,0,0,0,0,0,0,1,1,0,
		0,0,0,0,0,0,0,0,0,0,0,0,

		//e
		0,0,0,0,0,0,0,0,0,0,0,0,
		0,1,1,1,1,1,1,1,1,1,1,0,
		0,1,1,1,1,1,1,1,1,1,1,0,
		0,1,1,0,0,0,0,0,0,0,0,0,
		0,1,1,0,0,0,0,0,0,0,0,0,
		0,1,1,1,1,1,1,1,1,1,1,0,
		0,1,1,1,1,1,1,1,1,1,1,0,
		0,1,1,0,0,0,0,0,0,0,0,0,
		0,1,1,0,0,0,0,0,0,0,0,0,
		0,1,1,1,1,1,1,1,1,1,1,0,
		0,1,1,1,1,1,1,1,1,1,1,0,
		0,0,0,0,0,0,0,0,0,0,0,0,

		//s
		0,0,0,0,0,0,0,0,0,0,0,0,
		0,1,1,1,1,1,1,1,1,1,1,0,
		0,1,1,1,1,1,1,1,1,1,1,0,
		0,1,1,0,0,0,0,0,0,0,0,0,
		0,1,1,0,0,0,0,0,0,0,0,0,
		0,0,0,1,1,1,1,1,1,1,0,0,
		0,0,0,1,1,1,1,1,1,1,0,0,
		0,0,0,0,0,0,0,0,0,1,1,0,
		0,0,0,0,0,0,0,0,0,1,1,0,
		0,1,1,1,1,1,1,1,1,1,1,0,
		0,1,1,1,1,1,1,1,1,1,1,0,
		0,0,0,0,0,0,0,0,0,0,0,0,
		
		//m
		0,0,0,0,0,0,0,0,0,0,0,0,
		0,1,1,1,1,1,1,1,1,1,1,0,
		0,1,1,1,1,1,1,1,1,1,1,0,
		0,1,1,0,0,1,1,0,0,1,1,0,
		0,1,1,0,0,1,1,0,0,1,1,0,
		0,1,1,0,0,1,1,0,0,1,1,0,
		0,1,1,0,0,1,1,0,0,1,1,0,
		0,1,1,0,0,1,1,0,0,1,1,0,
		0,1,1,0,0,1,1,0,0,1,1,0,
		0,1,1,0,0,1,1,0,0,1,1,0,
		0,1,1,0,0,1,1,0,0,1,1,0,
		0,0,0,0,0,0,0,0,0,0,0,0,

	};

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

		String appname="";
		String[] js=args[0].split("/");
		if(js.length>0)
		{
			if(js[js.length-1].endsWith(".jar"))
			{
				appname=js[js.length-1].substring(0,js[js.length-1].length()-4);
				System.out.println("jar file name:"+appname);
			}
		}

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

		config = new SDLConfig();
		config.init(appname+lcdWidth+lcdHeight);
		settingsChanged();

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

					if(showfps<60)
					{
						int index=useFlag*144;
						int t ;
						for(int i=0;i<12;i++)
						{
							for(int j=0;j<12;j++)
							{
								
								t= ((10 + i)*lcdWidth+(10 + j))*3;
								switch (keyPix[index+(i*12)+j])
								{
									case 0: 
										frame[t] = (byte)0x00; 
										frame[t+1] = (byte)0x00;
										frame[t+2] = (byte)0x00;
										
										break;
									case 1: 
										frame[t] = (byte)0x0; 
										frame[t+1] = (byte)0xFF;
										frame[t+2] = (byte)0xFF;
										break;
								}
							}
						}
						
						showfps+=1;
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

	private void settingsChanged()
	{
		fps = Integer.parseInt(config.settings.get("fps"));
		if(fps>0) { fps = 1000 / fps; }

		String phone = config.settings.get("phone");
		/* useNokiaControls = false;
		useSiemensControls = false;
		useMotorolaControls = false;
		Mobile.nokia = false;
		Mobile.siemens = false;
		Mobile.motorola = false; */
		useFlag=0;
		if(phone.equals("n")) { /* Mobile.nokia = true;  useNokiaControls = true; */ useFlag=1;}
		else if(phone.equals("e")) { /* Mobile.nokia = true;  useNokiaControls = true; */ useFlag=2;}
		else if(phone.equals("s")) { /* Mobile.siemens = true; useSiemensControls = true; */ useFlag=3;}
		else if(phone.equals("m")) { /* Mobile.motorola = true; useMotorolaControls = true; */ useFlag=4;}
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
				/*String[] new_args = new String[5];
				new_args[0] = "/storage/java/sdl_interface";
				new_args[1] = String.valueOf(lcdWidth);
				new_args[2] = String.valueOf(lcdHeight);
				new_args[3] = "-b";
				if (lcdWidth > lcdHeight)
					new_args[4] = "/storage/roms/bezels/java/java_h.png";
				else
					new_args[4] = "/storage/roms/bezels/java/java_v.png";*/
				//String new_args[] = {"/storage/sdl_interface", String.valueOf(lcdWidth), String.valueOf(lcdHeight), "-b", "/storage/roms/bezels/java/java_h.png"};
				//System.out.println(args[0] + args[1] + args[2]);
				
				String[] new_args={"/storage/java/sdl_interface",String.valueOf(lcdWidth),String.valueOf(lcdHeight)};

				proc = new ProcessBuilder(new_args).start();
				keys = proc.getErrorStream(); //gkdminiplus

				//keys = proc.getInputStream();
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
			private int code=0;
			private int mobikey;
			private int mobikeyN;
			private int x,y;
			private boolean press=false;

			public void run()
			{
				try // to read keys
				{
					while(true)
					{
						bin = keys.read();
						if(bin==-1) { return; }
						//~ System.out.print(" "+bin);
						din[count] = (byte)(bin & 0xFF);
						count++;
						if (count==5)
						{
							count = 0;
							//System.out.println(" ("+din[0]+") <- din[0]");

							switch(din[0] >>> 4)
							{
								case 0: //按键模式
									code = (din[1]<<24) | (din[2]<<16) | (din[3]<<8) | din[4];
									
									//System.out.println(" ("+code+") =============== Key");
									
									mobikey = getMobileKey(code); 
									break;
								case 1://触屏模式
									
									x=((din[1]<<8)&0xFF00) | (din[2]&0x00FF);
									y=((din[3]<<8)&0xFF00) | (din[4]&0x00FF);
									
									
									if(din[0] % 2 == 0)//释放
									{
										
										//System.out.println("鼠标释放x:"+x+" y:"+y);
										Mobile.getPlatform().pointerReleased(x, y);
										
										press=false;
									}
									else
									{
										if(press)
											return;
										//System.out.println("鼠标按下x:"+x+" y:"+y);
										Mobile.getPlatform().pointerPressed(x, y);
										
										press=true;
									}
									return;
								/* case 1: mobikey = getMobileKeyPad(code); break;
								case 2: mobikey = getMobileKeyJoy(code); break; */
								default: continue;
							}
							
							if (mobikey == 0) //Ignore events from keys not mapped to a phone keypad key
							{
								return; 
							}
							
							
							mobikeyN = (mobikey + 64) & 0x7F; //Normalized value for indexing the pressedKeys array
							
							
							if (din[0] % 2 == 0)
							{
								//Key released
								//System.out.println("keyReleased:  " + Integer.toString(mobikey));
								Mobile.getPlatform().keyReleased(mobikey, null);
								pressedKeys[mobikeyN] = false;
								
								//System.out.println("按键释放");
							}
							else
							{
								//Key pressed or repeated
								if (pressedKeys[mobikeyN] == false)
								{
									//System.out.println("keyPressed:  " + Integer.toString(mobikey));
									Mobile.getPlatform().keyPressed(mobikey, null);
									
									//System.out.println("按键按下");
								}
								else
								{
									//System.out.println("keyRepeated:  " + Integer.toString(mobikey));
									Mobile.getPlatform().keyRepeated(mobikey, null);
									//System.out.println("按键重复");
								}
								pressedKeys[mobikeyN] = true;
							}
							
						}
					}
				}
				catch (Exception e) { }
			}
		} // timer
	} // sdl
	
	private int getMobileKey(int keycode)
	{
		//System.out.println("按键码:"+keycode);
		if(useFlag==1)
		{
			switch(keycode)
			{
				case 0x40000052: return Mobile.NOKIA_UP;
				case 0x40000051: return Mobile.NOKIA_DOWN;
				case 0x40000050: return Mobile.NOKIA_LEFT;
				case 0x4000004F: return Mobile.NOKIA_RIGHT;
				case 0x0D: return Mobile.NOKIA_SOFT3;
				
			}
		}
		
		else if(useFlag==2)
		{
			switch(keycode)
			{
				case 0x40000052: return Mobile.NOKIA_UP;
				case 0x40000051: return Mobile.NOKIA_DOWN;
				case 0x40000050: return Mobile.NOKIA_LEFT;
				case 0x4000004F: return Mobile.NOKIA_RIGHT;
				case 0x0D: return Mobile.NOKIA_SOFT3;
				
				case 0x30: return 109;//m=0
				case 0x31: return 114;//r=1
				case 0x33: return 121;//y=3
				case 0x37: return 118;//v=7
				case 0x39: return 110;//n=9
				case 0x65: return 117;//* u
				case 0x72: return 106;//# j
			}
		}
		
		else if(useFlag==3)
		{
			switch(keycode)
			{
				case 0x40000052: return Mobile.SIEMENS_UP;
				case 0x40000051: return Mobile.SIEMENS_DOWN;
				case 0x40000050: return Mobile.SIEMENS_LEFT;
				case 0x4000004F: return Mobile.SIEMENS_RIGHT;
				case 0x71: return Mobile.SIEMENS_SOFT1;
				case 0x77: return Mobile.SIEMENS_SOFT2;
				case 0x0D: return Mobile.SIEMENS_FIRE;
			}
		}
		
		else if(useFlag==4)
		{
			switch(keycode)
			{
				case 0x40000052: return Mobile.MOTOROLA_UP;
				case 0x40000051: return Mobile.MOTOROLA_DOWN;
				case 0x40000050: return Mobile.MOTOROLA_LEFT;
				case 0x4000004F: return Mobile.MOTOROLA_RIGHT;
				case 0x71: return Mobile.MOTOROLA_SOFT1;
				case 0x77: return Mobile.MOTOROLA_SOFT2;
				case 0x0D: return Mobile.MOTOROLA_FIRE;
			}
		}
		
		
		
		//keycode是SDL对应的键盘码
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
			case 0x2A: return Mobile.KEY_STAR;//*
			case 0x23: return Mobile.KEY_POUND;//#

			case 0x40000052: return Mobile.KEY_NUM2;
			case 0x40000051: return Mobile.KEY_NUM8;
			case 0x40000050: return Mobile.KEY_NUM4;
			case 0x4000004F: return Mobile.KEY_NUM6;

			case 0x0D: return Mobile.KEY_NUM5;

			case 0x71: return Mobile.NOKIA_SOFT1; //SDLK_q
			case 0x77: return Mobile.NOKIA_SOFT2;  //SDLK_w
			case 0x65: return Mobile.KEY_STAR;  //SDLK_e
			case 0x72: return Mobile.KEY_POUND;  ////SDLK_r
			
			// Inverted Num Pad 数字小键盘
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
			
			case 0x63://c
				//切换按键模式
				
				useFlag=(useFlag+1)%5;
				if(useFlag==0)
				{
					config.settings.put("phone", "p");
				}
				else if(useFlag==1)
				{
					config.settings.put("phone", "n");
				}
				else if(useFlag==2)
				{
					config.settings.put("phone", "e");
				}
				else if(useFlag==3)
				{
					config.settings.put("phone", "s");
				}
				else if(useFlag==4)
				{
					config.settings.put("phone", "m");
				}
				showfps=0;
				config.saveConfig();
				
				break;
			case -2:
				ScreenShot.takeScreenshot(false);
				break;

			// F4 - Quit
			case -1: 
				//au.stop();
				sdl.stop();
				
				System.exit(0);
				break;

			// ESC - Quit
			case 0x1B: 
				//au.stop();
				sdl.stop();
				
				System.exit(0);
				break;
				
			// HOME - Quit
			case 0x4000004a: 
				//au.stop();
				sdl.stop();
				
				System.exit(0);
				break;
		}
		return 0;
	}
}
