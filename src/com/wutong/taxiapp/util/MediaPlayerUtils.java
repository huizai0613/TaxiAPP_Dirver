package com.wutong.taxiapp.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;

public class MediaPlayerUtils {

	public static void playerSound(byte[] soundBytes) {

		DataInputStream din = new DataInputStream(new ByteArrayInputStream(
				soundBytes));

		boolean m_keep_running = true;

		int m_out_buf_size = soundBytes.length;

		AudioTrack m_out_trk = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
				AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
				m_out_buf_size, AudioTrack.MODE_STREAM);

		// new Thread(R1).start();
		m_out_trk.play();
		m_out_trk.write(soundBytes, 0, m_out_buf_size);
		m_out_trk.stop();
		m_out_trk.release();
		try {
			din.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
