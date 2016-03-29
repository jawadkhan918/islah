package com.talhazk.islah.model;

public class Favorities {

	private int mId;
	private String mfvrt;
	private String mLink;
	
	
	public Favorities(int mId,String mfvrt,String mLink) {
		super();
		this.mfvrt = mfvrt;
		this.mId = mId;
		this.mLink = mLink;
	}
	
	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public String getMfvrt() {
		return mfvrt;
	}
	public void setMfvrt(String mfvrt) {
		this.mfvrt = mfvrt;
	}

	public String getLink() {
		return mLink;
	}

	public void setLink(String link) {
		mLink = link;
	}

	@Override
	public String toString() {
		return "mId=" + mId + ", mfvrt=" + mfvrt + ", mLink="
				+ mLink+"\n";
	}
	
	
}
