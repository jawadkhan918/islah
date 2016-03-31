package com.talhazk.islah.model;

public class Favorities {

	private int mId;
	private String mfvrt;
	private String mLink;
	private String mCatId;
	private String ayatNo;

	public Favorities(int mId,String mfvrt,String catId,String ayatNo,String mLink) {
		super();
		this.mfvrt = mfvrt;
		this.mId = mId;
		this.mCatId = catId;
		this.mLink = mLink;
		this.ayatNo = ayatNo;
	}

	public String getAyatNo() {
		return ayatNo;
	}

	public void setAyatNo(String ayatNo) {
		this.ayatNo = ayatNo;
	}

	public String getmCatId() {
		return mCatId;
	}

	public void setmCatId(String mCatId) {
		this.mCatId = mCatId;
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
