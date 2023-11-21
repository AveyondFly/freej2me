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
package javax.microedition.media;

import java.io.InputStream;

import javax.microedition.media.tone.ToneManager;

import java.io.IOException;

import org.recompile.mobile.Mobile;

import pl.zb3.freej2me.audio.FFPlayer;
import pl.zb3.freej2me.audio.MIDIFilePlayer;
import pl.zb3.freej2me.audio.MIDIPlayer;
import pl.zb3.freej2me.audio.MIDITonePlayer;

public final class Manager
{

	public static final String TONE_DEVICE_LOCATOR = "device://tone";
	public static final String MIDI_DEVICE_LOCATOR = "device://midi";

	private static final TimeBase DEFAULT_TIMEBASE = () -> System.nanoTime() / 1000L;


	public static Player createPlayer(InputStream stream, String type) throws IOException, MediaException
	{
		
		if (!Mobile.sound) {
			return new BasePlayer();
		} else if(type.equalsIgnoreCase("audio/mid") || type.equalsIgnoreCase("audio/midi") || type.equalsIgnoreCase("audio/x-midi") || type.equalsIgnoreCase("sp-midi") || type.equalsIgnoreCase("audio/spmidi")) {
			return new MIDIFilePlayer(stream, type);
		} else if(type.equalsIgnoreCase(MIDI_DEVICE_LOCATOR)) {
			return new MIDIFilePlayer(stream, "audio/midi");
		} else if(type.equalsIgnoreCase(TONE_DEVICE_LOCATOR) || type.equalsIgnoreCase("audio/x-tone-seq")) {
			return new MIDITonePlayer(stream);
		} else {
			return new FFPlayer(stream, type);
		}
	}

	public static Player createPlayer(String locator) throws MediaException
	{
		if (Mobile.sound) {
			if (locator.equals(MIDI_DEVICE_LOCATOR)) {
				return new MIDIPlayer();
			} else if (locator.equals(TONE_DEVICE_LOCATOR)) {
				return new MIDITonePlayer();
			}
		}

		return new BasePlayer(); // not supported 
	}
	
	public static String[] getSupportedContentTypes(String str) {
		return new String[]{"audio/wav", "audio/x-wav", "audio/midi", "audio/x-midi",
				"audio/mpeg", "audio/aac", "audio/amr", "audio/amr-wb", "audio/mp3",
				"audio/mp4", "audio/mmf", "audio/x-tone-seq"};
	}

	
	public static String[] getSupportedProtocols(String content_type)
	{
		System.out.println("Get Supported Media Protocols");
		return new String[]{"device"};
	}

	public static TimeBase getSystemTimeBase() {
		return DEFAULT_TIMEBASE;
	}
	
	public synchronized static void playTone(int note, int duration, int volume)
			throws MediaException {
		if (Mobile.sound) {
			ToneManager.play(note, duration, volume);
		}
	}

}
