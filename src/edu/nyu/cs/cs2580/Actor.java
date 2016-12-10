package edu.nyu.cs.cs2580;

import java.io.Serializable;

public class Actor implements Serializable {

	private static final long serialVersionUID = 6230565559933563974L;

	private Integer _id;
	private String _name;
	private String _picture_url;
	private String _wiki_url;

	public Actor(Integer id) {
		this._id = id;
	}

	public Actor(Integer id, String name, String pictureUrl) {
		this._id = id;
		this._name = name;
		this._picture_url = pictureUrl;
	}

	public Integer getId() {
		return _id;
	}

	public void setId(Integer id) {
		this._id = id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public String getPictureUrl() {
		return _picture_url;
	}

	public void setPictureUrl(String pictureUrl) {
		this._picture_url = pictureUrl;
	}

	public String getWikiUrl() {
		return _wiki_url;
	}

	public void setWikiUrl(String wikiUrl) {
		this._wiki_url = wikiUrl;
	}
}
