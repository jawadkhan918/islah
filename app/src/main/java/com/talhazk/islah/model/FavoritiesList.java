package com.talhazk.islah.model;

import java.util.ArrayList;

public class FavoritiesList {

	private ArrayList<Favorities> mFvrt;
	private static FavoritiesList sFavoritiesList;
	
	private FavoritiesList() {
		mFvrt = new ArrayList<Favorities>();
	}
	public static FavoritiesList get() {
		if (sFavoritiesList == null) {
			sFavoritiesList = new FavoritiesList();
		}
		return sFavoritiesList;
	}
	public ArrayList<Favorities> getFavorities() {
		return mFvrt;
	}

	public Boolean getFvrt(int fvrt) {

		for (Favorities m : mFvrt) {
			if (m.getId() == fvrt)
				return true;
		}
		return false;
	}

	public void addFavorities(Favorities newFvrt) {
		mFvrt.add(newFvrt);
	}

	public void deleteMember(Favorities m) {
		mFvrt.remove(m);

	}
public void clearFavorities(){
		
		mFvrt.clear(); 
		
	}

}
