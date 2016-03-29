package com.talhazk.islah.model;

import java.util.ArrayList;

public class AyatsList {

	private ArrayList<Ayats> mDialogues;
	private static AyatsList sDialoguesList;

	private AyatsList() {
		mDialogues = new ArrayList<Ayats>();
	}
	public static AyatsList get() {
		if (sDialoguesList == null) {
			sDialoguesList = new AyatsList();
		}
		return sDialoguesList;
	}
	public ArrayList<Ayats> getDialogues() {
		return mDialogues;
	}

	public Boolean getDial(String cat) {

		for (Ayats m : mDialogues) {
			if (m.getAyatTitle().equals(cat))
				return true;
		}
		return false;
	}

	public String getDialogueById(int cat) {

		for (Ayats m : mDialogues) {
			if (m.getAyatId()==cat)
				return m.getIslahAudio();
		}
		return "";
	}

	

	public void addDialogue(Ayats newCat) {
		mDialogues.add(newCat);
	}

	public void deleteMember(Ayats m) {
		mDialogues.remove(m);

	}
public void clearDialogues(){
		
		mDialogues.clear(); 
		
	}

}
