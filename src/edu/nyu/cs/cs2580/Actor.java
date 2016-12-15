package project;

import java.io.Serializable;

public class Actor implements Serializable {
	private static final long serialVersionUID = 6230565559933563974L;

	private int _id;
	private String _name;
	private String _picture_url;
	private String _wiki_url;

	public Actor(int id) {
		this._id = id;
	}

	public Actor(Integer id, String name, String pictureUrl, String wikiUrl) {
		this._id = id;
		this._name = name;
		this._picture_url = pictureUrl;
		this._wiki_url = wikiUrl;
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Actor ID:\t" + this._id + "\n");
		output.append("Actor name:\t" + this._name + "\n");
		output.append("Actor picture url:\t" + this._picture_url + "\n");
		output.append("Actor wiki url:\t" + this._wiki_url + "\n");
		return output.toString();
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
