package com.thepegeekapps.easyportfolio.util;

import java.util.List;

import com.thepegeekapps.easyportfolio.model.Group;
import com.thepegeekapps.easyportfolio.model.Image;
import com.thepegeekapps.easyportfolio.model.Portfolio;

public class Utils {
	
	public static String[] getPortfolioNames(List<Portfolio> portfolios) {
		if (portfolios == null || portfolios.isEmpty())
			return new String[] {""};
		String[] names = new String[portfolios.size()];
		for (int i=0; i<portfolios.size(); i++)
			names[i] = portfolios.get(i).getName();
		if (names.length == 0)
			names = new String[] {""};
		return names;
	}
	
	public static String[] getGroupNames(List<Group> groups) {
		if (groups == null || groups.isEmpty())
			return new String[] {""};
		String[] names = new String[groups.size()];
		for (int i=0; i<groups.size(); i++)
			names[i] = groups.get(i).getName();
		if (names.length == 0)
			names = new String[] {""};
		return names;
	}
	
	public static String[] getImagesPathAsStringArray(List<Image> images) {
		if (images == null || images.isEmpty())
			return new String[] {};
		String[] items = new String[images.size()];
		for (int i=0; i<images.size(); i++)
			items[i] = images.get(i).getPath();
		return items;
	}
	
	public static final String getReadableFilesize(long bytes) {
		String s = " B";
		String size = bytes + s;
		
		while (bytes > 1024) {
			if (s.equals(" B"))	s = " KB";
			else if (s.equals(" KB")) s = " MB";
			else if (s.equals(" MB")) s = " GB";
			else if (s.equals(" GB")) s = " TB";
			
			size = (bytes / 1024) + "." + (bytes % 1024) + s;
			bytes = bytes / 1024;
		}
		
		return size;
	}

}
