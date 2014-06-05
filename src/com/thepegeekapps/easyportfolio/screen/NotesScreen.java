package com.thepegeekapps.easyportfolio.screen;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.thepegeekapps.easyportfolio.R;
import com.thepegeekapps.easyportfolio.database.DatabaseHelper;
import com.thepegeekapps.easyportfolio.model.Note;

public class NotesScreen extends BaseScreen implements OnClickListener {
	
	protected EditText noteText;
	protected Button saveBtn;
	
	protected int portfolioId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notes_screen);
		setScreenTitle(R.string.notes);
		getIntentData();
		initializeViews();
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_PORTFOLIO_ID)) {
			portfolioId = intent.getIntExtra(DatabaseHelper.FIELD_PORTFOLIO_ID, 0);
		}
	}
	
	protected void initializeViews() {
		noteText = (EditText) findViewById(R.id.noteText);
		
		saveBtn = getRightBtn();
		saveBtn.setVisibility(View.VISIBLE);
		saveBtn.setText(R.string.save);
		saveBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.rightBtn) {
			String text = noteText.getText().toString();
			if (text != null && !text.equals("")) {
				String filename = (text.length() > 25 ) ? text.substring(0, 25) : text;
				String path = saveNote(filename, text);
				Note note = new Note(0, portfolioId, filename, path, text);
				dbManager.addNote(note);
			}
			setResult(RESULT_OK);
			finish();
		}
	}
	
	protected String saveNote(String filename, String text) {
		String path = null;
		try {
			File notesDir = new File(Environment.getExternalStorageDirectory(), "/easyportfolio/note/");
			notesDir.mkdirs();
			
			File file = new File(notesDir, filename+".txt");
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = text.getBytes();
			fos.write(buffer);
			fos.flush();
			fos.close();
			path = file.getAbsolutePath();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

}
