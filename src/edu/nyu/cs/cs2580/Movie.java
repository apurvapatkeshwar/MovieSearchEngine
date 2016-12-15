package project;

import java.io.Serializable;
import java.util.ArrayList;

public class Movie implements Serializable{
	private static final long serialVersionUID = -6632244441411231281L;

	private int _movieid;
	private String _name;
	private ArrayList<String> _genres;
	private String _description;
	private Double _rating;
	private Integer _ratings_count;
	private String _director;
	private String _picture_url;
	private String _wiki_url;

	public Movie(int id) {
		_movieid = id;
	}

	public Movie(Integer id, String name, ArrayList<String> genres, String description, Double rating,
			Integer ratingsCount, String director, String pictureUrl, String wikiUrl) {
		this._movieid = id;
		this._name = name;
		this._genres = genres;
		this._description = description;
		this._rating = rating;
		this._ratings_count = ratingsCount;
		this._director = director;
		this._picture_url = pictureUrl;
		this._wiki_url = wikiUrl;
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Movie ID:\t" + this._movieid + "\n");
		output.append("Name:\t" + this._name + "\n");
		output.append("Genres:\t" + this._genres + "\n");
		output.append("Description:\t" + this._description + "\n");
		output.append("Rating:\t" + this._rating + " / " + this._ratings_count + " reviews\n");
		output.append("Director:\t" + this._director + "\n");
		output.append("Picture url:\t" + this._picture_url + "\n");
		output.append("Wiki url:\t" + this._wiki_url + "\n");
		return output.toString();
	}

	public Integer getId() {
		return _movieid;
	}

	public void setId(Integer id) {
		this._movieid = id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public ArrayList<String> getGenres() {
		return _genres;
	}

	public void setGenres(ArrayList<String> genres) {
		this._genres = genres;
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

	public Integer getRatingsCount() {
		return _ratings_count;
	}

	public void setRatingsCount(Integer ratingsCount) {
		this._ratings_count = ratingsCount;
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
