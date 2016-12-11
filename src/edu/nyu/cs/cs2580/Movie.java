package edu.nyu.cs.cs2580;

import java.io.Serializable;

public class Movie implements Serializable {

	private static final long serialVersionUID = -6632244441411231281L;

	private Integer _id;
	private String _name;
	private String _description;
	private Double _rating;
	private String _director;
	private String _picture_url;
	private String _wiki_url;

	public Movie(Integer id) {
		this._id = id;
	}

	public Movie(Integer id, String name, String description, Double rating, String director, String pictureUrl,
			String wikiUrl) {
		this._id = id;
		this._name = name;
		this._description = description;
		this._rating = rating;
		this._director = director;
		this._picture_url = pictureUrl;
		this._wiki_url = wikiUrl;
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Movie ID:\t" + this._id + "\n");
		output.append("Movie name:\t" + this._name + "\n");
		output.append("Movie description:\t" + this._description + "\n");
		output.append("Movie rating:\t" + this._rating + "\n");
		output.append("Movie director:\t" + this._director + "\n");
		output.append("Movie picture url:\t" + this._picture_url + "\n");
		output.append("Movie wiki url:\t" + this._wiki_url + "\n");
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

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		this._description = description;
	}

	public Double getRating() {
		return _rating;
	}

	public void setRating(Double rating) {
		this._rating = rating;
	}

	public String getDirector() {
		return _director;
	}

	public void setDirector(String director) {
		this._director = director;
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

	public String getYear() {
		int nameSize = _name.length();
		if (nameSize < 5) {
			return null;
		}
		return _name.substring(nameSize - 5, nameSize - 1);
	}
}
