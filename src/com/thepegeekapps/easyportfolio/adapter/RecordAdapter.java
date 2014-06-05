package com.thepegeekapps.easyportfolio.adapter;

import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.thepegeekapps.easyportfolio.R;
import com.thepegeekapps.easyportfolio.model.Image;
import com.thepegeekapps.easyportfolio.model.Portfolio;
import com.thepegeekapps.easyportfolio.model.Record;
import com.thepegeekapps.easyportfolio.model.Video;

public class RecordAdapter extends BaseExpandableListAdapter {
	
	protected Context context;
	protected Portfolio portfolio;
	protected String[] recordTypes;
	
	public RecordAdapter(Context context, Portfolio portfolio) {
		this.context = context;
		recordTypes = context.getResources().getStringArray(R.array.portfolio_record_types);
		setPortfolio(portfolio);
	}

	@Override
	public Record getChild(int groupPosition, int childPosition) {
		if (portfolio != null) {
			List<Record> records = portfolio.asRecordList(groupPosition);
			if (records != null && !records.isEmpty())
				return records.get(childPosition);
		}
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		Record record = getChild(groupPosition, childPosition);
		return (record != null) ? record.getId() : 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		ChildViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.record_list_item, null);
			holder = new ChildViewHolder();
			holder.recordImage = (ImageView) convertView.findViewById(R.id.recordImage);
			holder.recordName = (TextView) convertView.findViewById(R.id.recordName);
			holder.recordChecked = (CheckBox) convertView.findViewById(R.id.recordChecked);
			convertView.setTag(holder);
		} else {
			holder = (ChildViewHolder) convertView.getTag();
		}
		
		if (portfolio != null) {
			List<Record> records = portfolio.asRecordList(groupPosition);
			if (records != null && !records.isEmpty()) {
				final Record record = records.get(childPosition);
				if (record != null) {
					holder.recordName.setText(record.getName());
					holder.recordChecked.setChecked(record.isChecked());
					switch (groupPosition) {
					case Record.TYPE_VIDEO:
						Video video = (Video) record;
						Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(video.getPath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
						holder.recordImage.setImageBitmap(thumbnail);
						break;
					case Record.TYPE_IMAGE:
						holder.recordImage.setImageBitmap(BitmapFactory.decodeFile(((Image) record).getPath()));
						break;
					case Record.TYPE_AUDIO:
						holder.recordImage.setImageResource(R.drawable.ic_file_3gp);
						break;
					case Record.TYPE_NOTE:
						holder.recordImage.setImageResource(R.drawable.ic_file_txt);
						break;
					case Record.TYPE_URL:
						holder.recordImage.setImageResource(R.drawable.ic_file_web);
						break;
					case Record.TYPE_DOC:
						holder.recordImage.setImageResource(getDocImageResource(record));
						break;
					}
					convertView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							record.setChecked(!record.isChecked());
							notifyDataSetChanged();
						}
					});
					convertView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
						@Override
						public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
							// hack, as onChildClick didn't trigger as list scrolled
						}
					});
				}
			}
		}

		return convertView;
	}
	
	protected int getDocImageResource(Record record) {
		if (record == null || record.getName() == null)
			return R.drawable.ic_file_file;
		int resourceId = R.drawable.ic_file_file;
		String name = record.getName();
		String ext = name.substring(name.lastIndexOf('.')+1);
		if (ext.equalsIgnoreCase("avi")) {
			resourceId = R.drawable.ic_file_avi;
		} else if (ext.equalsIgnoreCase("bmp")) {
			resourceId = R.drawable.ic_file_bmp;
		} else if (ext.equalsIgnoreCase("css")) {
			resourceId = R.drawable.ic_file_css;
		} else if (ext.equalsIgnoreCase("djvu")) {
			resourceId = R.drawable.ic_file_djvu;
		} else if (ext.equalsIgnoreCase("doc") || ext.equalsIgnoreCase("docx")) {
			resourceId = R.drawable.ic_file_docx;
		} else if (ext.equalsIgnoreCase("ico")) {
			resourceId = R.drawable.ic_file_ico;
		} else if (ext.equalsIgnoreCase("ini")) {
			resourceId = R.drawable.ic_file_ini;
		} else if (ext.equalsIgnoreCase("iso")) {
			resourceId = R.drawable.ic_file_iso;
		} else if (ext.equalsIgnoreCase("java")) {
			resourceId = R.drawable.ic_file_java;
		} else if (ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("jpg")) {
			resourceId = R.drawable.ic_file_jpg;
		} else if (ext.equalsIgnoreCase("mov")) {
			resourceId = R.drawable.ic_file_mov;
		} else if (ext.equalsIgnoreCase("mp3")) {
			resourceId = R.drawable.ic_file_mp3;
		} else if (ext.equalsIgnoreCase("mp4")) {
			resourceId = R.drawable.ic_file_mp4;
		} else if (ext.equalsIgnoreCase("mpg") || ext.equalsIgnoreCase("mpeg")) {
			resourceId = R.drawable.ic_file_mpeg;
		} else if (ext.equalsIgnoreCase("pdf")) {
			resourceId = R.drawable.ic_file_pdf;
		} else if (ext.equalsIgnoreCase("png")) {
			resourceId = R.drawable.ic_file_png;
		} else if (ext.equalsIgnoreCase("psd")) {
			resourceId = R.drawable.ic_file_psd;
		} else if (ext.equalsIgnoreCase("tiff")) {
			resourceId = R.drawable.ic_file_tiff;
		} else if (ext.equalsIgnoreCase("torrent")) {
			resourceId = R.drawable.ic_file_torrent;
		} else if (ext.equalsIgnoreCase("ttf")) {
			resourceId = R.drawable.ic_file_ttf;
		} else if (ext.equalsIgnoreCase("txt")) {
			resourceId = R.drawable.ic_file_txt;
		} else if (ext.equalsIgnoreCase("url")) {
			resourceId = R.drawable.ic_file_url;
		} else if (ext.equalsIgnoreCase("vob")) {
			resourceId = R.drawable.ic_file_vob;
		} else if (ext.equalsIgnoreCase("wav")) {
			resourceId = R.drawable.ic_file_wav;
		} else if (ext.equalsIgnoreCase("wma")) {
			resourceId = R.drawable.ic_file_wma;
		} else if (ext.equalsIgnoreCase("xls") || ext.equalsIgnoreCase("xlsx")) {
			resourceId = R.drawable.ic_file_xls;
		} else if (ext.equalsIgnoreCase("xml")) {
			resourceId = R.drawable.ic_file_xml;
		} else if (ext.equalsIgnoreCase("zip")) {
			resourceId = R.drawable.ic_file_zip;
		}
		return resourceId;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return (portfolio != null && portfolio.asRecordList(groupPosition) != null)
				? portfolio.asRecordList(groupPosition).size() : 0;
	}

	@Override
	public String getGroup(int groupPosition) {
		return recordTypes[groupPosition];
	}

	@Override
	public int getGroupCount() {
		return recordTypes.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		GroupViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.record_type_list_item, null);
			holder = new GroupViewHolder();
			holder.groupName = (TextView) convertView.findViewById(R.id.groupName);
			convertView.setTag(holder);
		} else {
			holder = (GroupViewHolder) convertView.getTag();
		}
		
		ExpandableListView list = (ExpandableListView) parent;
		list.expandGroup(groupPosition);
		
		holder.groupName.setText(recordTypes[groupPosition]);
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	public void setRecords(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
	
	class ChildViewHolder {
		ImageView recordImage;
		TextView recordName;
		CheckBox recordChecked;
	}
	
	class GroupViewHolder {
		TextView groupName;
	}
	
	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
	
	public List<Record> getCheckedRecords() {
		List<Record> records = new LinkedList<Record>();
		
		List<Record> videos = portfolio.asRecordList(Record.TYPE_VIDEO);
		if (videos != null && !videos.isEmpty())
			for (Record record : videos)
				if (record.isChecked())
					records.add(record);
		
		List<Record> images = portfolio.asRecordList(Record.TYPE_IMAGE);
		if (images != null && !images.isEmpty())
			for (Record record : images)
				if (record.isChecked())
					records.add(record);
		
		List<Record> audios = portfolio.asRecordList(Record.TYPE_AUDIO);
		if (audios != null && !audios.isEmpty())
			for (Record record : audios)
				if (record.isChecked())
					records.add(record);
		
		List<Record> notes = portfolio.asRecordList(Record.TYPE_NOTE);
		if (notes != null && !notes.isEmpty())
			for (Record record : notes)
				if (record.isChecked())
					records.add(record);
		
		List<Record> urls = portfolio.asRecordList(Record.TYPE_URL);
		if (urls != null && !urls.isEmpty())
			for (Record record : urls)
				if (record.isChecked())
					records.add(record);
		
		List<Record> docs = portfolio.asRecordList(Record.TYPE_DOC);
		if (docs != null && !docs.isEmpty())
			for (Record record : docs)
				if (record.isChecked())
					records.add(record);
		
		return records;
		
	}

}
