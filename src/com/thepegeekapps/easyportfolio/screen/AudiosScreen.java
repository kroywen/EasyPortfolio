package com.thepegeekapps.easyportfolio.screen;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thepegeekapps.easyportfolio.R;
import com.thepegeekapps.easyportfolio.database.DatabaseHelper;
import com.thepegeekapps.easyportfolio.model.Audio;

public class AudiosScreen extends BaseScreen implements OnClickListener {
	
	public static final int STATE_NONE = 0;
	public static final int STATE_RECORDING = 1;
	public static final int STATE_PLAYING = 2;
	public static final int STATE_PAUSED = 3;
	
	protected Button saveBtn;
	
	protected ImageView recordBtn;
	protected ImageView playBtn;
	protected ImageView stopBtn;
	protected ImageView repeatBtn;
	protected ImageView pencil;
	protected TextView audioName;
	protected ImageView mic;
	
	protected Audio audio;
	protected int portfolioId;
	protected String filename;
	
	protected MediaRecorder recorder;
	protected MediaPlayer player;
	
	protected int state;
	protected boolean looping;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audios_screen);
		setScreenTitle(R.string.audio_files);
		getIntentData();
		audio = new Audio(0, portfolioId, getString(R.string.new_audio), null);
		initializeViews();
		setState(STATE_NONE);
		setLooping(false);
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_PORTFOLIO_ID)) {
			portfolioId  =intent.getIntExtra(DatabaseHelper.FIELD_PORTFOLIO_ID, 0);
		}
	}
	
	protected void initializeViews() {
		saveBtn = (Button) findViewById(R.id.rightBtn);
		saveBtn.setText(R.string.save);
		saveBtn.setVisibility(View.VISIBLE);
		saveBtn.setOnClickListener(this);
		
		audioName = (TextView) findViewById(R.id.name);
		
		mic = (ImageView) findViewById(R.id.mic);
		mic.setVisibility(View.INVISIBLE);
		
		recordBtn = (ImageView) findViewById(R.id.recordBtn);
		recordBtn.setOnClickListener(this);
		
		playBtn = (ImageView) findViewById(R.id.playBtn);
		playBtn.setOnClickListener(this);
		
		stopBtn = (ImageView) findViewById(R.id.stopBtn);
		stopBtn.setOnClickListener(this);
		
		repeatBtn = (ImageView) findViewById(R.id.repeatBtn);
		repeatBtn.setOnClickListener(this);
		
		pencil = (ImageView) findViewById(R.id.pencil);
		pencil.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rightBtn:
			save();
			break;
		case R.id.recordBtn:
			if (state == STATE_NONE)
				startRecording();
			break;
		case R.id.stopBtn:
			if (state == STATE_RECORDING)
				stopRecording();
			else if (state == STATE_PLAYING)
				stopPlaying();
			break;
		case R.id.playBtn:
			if (state == STATE_NONE && filename != null)
				startPlaying();
			else if (state == STATE_PLAYING)
				pausePlaying();
			else if (state == STATE_PAUSED)
				resumePlaying();
			break;
		case R.id.repeatBtn:
			setLooping(!looping);
			break;
		case R.id.pencil:
			if (filename != null)
				showEditAudioNameDialog();
			break;
		}
	}
	
	protected void save() {
		if (audio.getName() != null) {
			dbManager.addAudio(audio);
			setResult(RESULT_OK);
			finish();
		} else {
			Toast.makeText(this, R.string.no_audio_recorded, Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void startRecording() {
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		File audioDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/easyportfolio/audio/");
		audioDir.mkdirs();
		filename = audioDir.getAbsolutePath() + "/" + audio.toString()+".3gp";
		audio.setPath(filename);
		File file = new File(audioDir, audio.toString()+".3gp");
		recorder.setOutputFile(file.getAbsolutePath());
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
        	recorder.prepare();
        } catch (IOException e) {
        	e.printStackTrace();
        }
	    recorder.start();
	    
	    setState(STATE_RECORDING);
	    mic.setVisibility(View.VISIBLE);
    }
	
	protected void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        
        setState(STATE_NONE);
        mic.setVisibility(View.INVISIBLE);
        
        audio.setName(getString(R.string.new_audio));
        audioName.setText(audio.getName());
    }
	
	protected void startPlaying() {
        player = new MediaPlayer();
        player.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer player) {
				if (!player.isPlaying())
					player.start();
			}
		});
        player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer player) {
				stopPlaying();
				if (looping)
					startPlaying();
			}
		});
        while (true) {
            try {
                Log.d("MediaPlayer", "Making sure it is in IDLE state...");
                player.reset();
                Log.d("MediaPlayer", "Setting Data Source...");
                player.setDataSource(filename);
                Log.d("MediaPlayer", "Now initialized. Preparing it.");
                player.prepare();
                break;
            }
            catch (IllegalArgumentException e) {
                Log.i("MediaPlayer", "IllegalArgumentException...");
            }
            catch (IllegalStateException e) {
                Log.i("MediaPlayer", "IllegalStateException...");
            }
            catch (IOException e) {
                Log.i("MediaPlayer", "IOException...");
            }
        }
		player.start();
		setState(STATE_PLAYING);
    }

	protected void stopPlaying() {
        player.release();
        player = null;
        setState(STATE_NONE);
    }
	
	protected void pausePlaying() {
		if (player != null && player.isPlaying()) {
			player.pause();
			setState(STATE_PAUSED);
		}
	}
	
	protected void resumePlaying() {
		if (player != null && !player.isPlaying()) {
			player.start();
			setState(STATE_PLAYING);
		}
	}
	
	protected void setState(int state) {
		this.state = state;
		if (state == STATE_NONE) {
			playBtn.setImageResource(R.drawable.play_btn);
			recordBtn.setImageResource(R.drawable.record_btn_default);
		} else if (state == STATE_PLAYING) {
			playBtn.setImageResource(R.drawable.pause_btn);
			recordBtn.setImageResource(R.drawable.record_btn_default);
		} else if (state == STATE_PAUSED) {
			playBtn.setImageResource(R.drawable.play_btn);
			recordBtn.setImageResource(R.drawable.record_btn_default);
		} else if (state == STATE_RECORDING) {
			playBtn.setImageResource(R.drawable.play_btn);
			recordBtn.setImageResource(R.drawable.record_btn_selected);
		}
	}
	
	protected void setLooping(boolean looping) {
		this.looping = looping;
		if (player != null)
			player.setLooping(looping);
		if (looping)
			repeatBtn.setImageResource(R.drawable.reset_btn_selected);
		else
			repeatBtn.setImageResource(R.drawable.reset_btn_default);
	}
	
	protected void showEditAudioNameDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll.setPadding(10, 10, 10, 10);
		
		final EditText input = new EditText(this);
		input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		input.setHint(R.string.video_name);
		if (audio != null)
			input.setText(audio.getName());
		
		ll.addView(input);
		
		builder
		    .setTitle(R.string.enter_new_name)
			.setView(ll)
			.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {	
				public void onClick(DialogInterface dialog, int which) {
					if (audio != null) {
						String name = input.getText().toString();
						name = (name != null && !name.equals("")) ? name : getString(R.string.new_audio);
						audio.setName(name);
						audioName.setText(name);
					}					
					dialog.dismiss();
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create()
			.show();
	}

}
