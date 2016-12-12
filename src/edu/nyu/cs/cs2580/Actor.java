package edu.nyu.cs.cs2580;

import java.io.Serializable;

public class Actor extends Document implements Serializable {

	private static final long serialVersionUID = 6230565559933563974L;

	private String _name;
	private String _picture_url;
	private String _wiki_url;

	public Actor(int id) {
		super(id);
	}

	public Actor(Integer id, String name, String pictureUrl, String wikiUrl) {
		super(id);
		this._name = name;
		this._picture_url = pictureUrl;
		this._wiki_url = wikiUrl;
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Actor ID:\t" + this._docid + "\n");
		output.append("Name:\t" + this._name + "\n");
		output.append("Picture url:\t" + this._picture_url + "\n");
		output.append("Wiki url:\t" + this._wiki_url + "\n");
		return output.toString();
	}

	public Integer getId() {
		return _docid;
	}

	public void setId(Integer id) {
		this._docid = id;
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
