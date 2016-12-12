package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import edu.nyu.cs.cs2580.SearchEngine.Options;

public class IndexerMovie extends Indexer implements Serializable {

	private static final long serialVersionUID = -8305500004730261917L;

	// Maps movie names to their integer representation
	private BiMap<String, Integer> _movieToMovieIDIndex = HashBiMap.create();

	// Maps actor names to their integer representation
	private BiMap<String, Integer> _actorToActorIDIndex = HashBiMap.create();

	// Maps a movie ID to the list of actor IDs that have worked in the movie
	private HashMap<Integer, ArrayList<Integer>> _movieToActorsIndex = new HashMap<Integer, ArrayList<Integer>>();

	// Maps an actor ID to the list of movie IDs that he/she has acted in
	private HashMap<Integer, ArrayList<Integer>> _actorToMoviesIndex = new HashMap<Integer, ArrayList<Integer>>();

	// Maps an actor ID to the map of [actor ID, number of times] who he/she has
	// worked with
	private HashMap<Integer, HashMap<Integer, Integer>> _actorToActorsIndex = new HashMap<Integer, HashMap<Integer, Integer>>();

	// Maps a movie ID to a movie object that contains details about the movie
	private HashMap<Integer, Movie> _movieToDetailsIndex = new HashMap<Integer, Movie>();

	// Maps an actor ID to an actor object that contains details about the actor
	private HashMap<Integer, Actor> _actorToDetailsIndex = new HashMap<Integer, Actor>();

	private String actorCorpusPath, movieCorpusPath;

	public IndexerMovie(Options options) {
		super(options);
		actorCorpusPath = options._corpusPrefix + "\\actors2.txt";
		movieCorpusPath = options._corpusPrefix + "\\imdbmovielinks_new.txt";
		System.out.println("Using Indexer: " + this.getClass().getSimpleName());
	}

	@Override
	public void constructIndex() throws FileNotFoundException, IOException {
		System.out.println("Construct index from: " + _options._corpusPrefix);

		readActorCorpus();
		readMovieCorpus();
		buildActorToMoviesIndex();
		buildActorToActorsIndex();
		removeNullEntries();

		printIndexStats();
		runTests();

		System.out.println("Indexed " + Integer.toString(_movieToMovieIDIndex.size()) + " movies with "
				+ Integer.toString(_actorToActorIDIndex.size()) + " actors.");
		String indexFile = _options._indexPrefix + "/corpus.idx";
		System.out.println("Saving index to:\t" + indexFile);
		ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(indexFile));
		writer.writeObject(this);
		writer.close();
	}

	private void readActorCorpus() {
		try (BufferedReader br = new BufferedReader(new FileReader(actorCorpusPath))) {
			String line, params[];
			Actor a;
			Integer actorID = 0;
			while ((line = br.readLine()) != null) {
				params = line.split("\t");

				_actorToActorIDIndex.put(params[0], actorID);
				a = new Actor(actorID);
				a.setName(params[0]);

				String pictureUrl = params[1].equalsIgnoreCase("null") ? null : params[1];
				a.setPictureUrl(pictureUrl);

				String wikiUrl = params[2].equalsIgnoreCase("null") ? null : params[2];
				a.setWikiUrl(wikiUrl);

				_actorToDetailsIndex.put(actorID, a);
				actorID++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}

	private void readMovieCorpus() {
		try (BufferedReader br = new BufferedReader(new FileReader(movieCorpusPath))) {
			String line, params[];
			Movie m;
			Integer movieID = 0;
			// HashSet<String> actors = new HashSet<String>();

			while ((line = br.readLine()) != null) {
				params = line.split("\t");

				// Map the movie with its integer representation
				_movieToMovieIDIndex.put(params[0], movieID);
				m = new Movie(movieID);
				String movieName = params[0] + " (" + params[1] + ")";
				m.setName(movieName);

				// Create the movie object and map it to the movie ID
				String genreList = params[2].equalsIgnoreCase("null") ? null : params[2];
				ArrayList<String> genres;
				try {
					genres = new ArrayList<String>(Arrays.asList(genreList.split("\\s*,\\s*")));
				} catch (Exception e) {
					genres = null;
				}
				m.setGenres(genres);

				String ratingStr = params[3].equalsIgnoreCase("null") ? null : params[3];
				Double rating;
				try {
					rating = Double.parseDouble(ratingStr);
				} catch (Exception e) {
					rating = null;
				}
				m.setRating(rating);

				String ratingsCountStr = params[4].equalsIgnoreCase("null") ? null : params[4];
				Integer ratingsCount;
				try {
					ratingsCount = Integer.parseInt(ratingsCountStr);
				} catch (Exception e) {
					ratingsCount = null;
				}
				m.setRatingsCount(ratingsCount);

				String director = params[5].equalsIgnoreCase("null") ? null : params[5];
				m.setDirector(director);

				String pictureUrl = params[6].equalsIgnoreCase("null") ? null : params[6];
				m.setPictureUrl(pictureUrl);

				String wikiUrl = params[7].equalsIgnoreCase("null") ? null : params[7];
				m.setWikiUrl(wikiUrl);

				if ((line = br.readLine()) != null) {
					params = line.split("\t");
					String description = params[0].equalsIgnoreCase("null") ? null : params[0];
					m.setDescription(description);
				}

				_movieToDetailsIndex.put(movieID, m);

				// Get actor IDs and map them to the movie ID
				if ((line = br.readLine()) != null) {
					params = line.split("\t");
					ArrayList<Integer> actorIDs = new ArrayList<Integer>();
					if (!params[0].isEmpty() && !params[0].equalsIgnoreCase("null")) {
						for (String actor : params) {
							if (_actorToActorIDIndex.containsKey(actor)) {
								Integer actorID = _actorToActorIDIndex.get(actor);
								actorIDs.add(actorID);
							}
							// actors.add(actor);
						}
					}
					_movieToActorsIndex.put(movieID, actorIDs);
				}

				movieID++;
			}
			// writeActorsCorpus(actors);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}

	private void writeActorsCorpus(HashSet<String> actors) {
		try {
			File fout = new File(_options._corpusPrefix + "\\actors2.txt");
			FileOutputStream fos;

			fos = new FileOutputStream(fout);

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			int i = 0;
			for (String actor : actors) {
				String op = actor + "\thttps://en.wikipedia.org/img/Pic_of_Actor" + i
						+ "\thttps://en.wikipedia.org/wiki/Actor_" + i;

				bw.write(op);
				bw.newLine();
				i++;
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void buildActorToMoviesIndex() {
		for (Entry<Integer, ArrayList<Integer>> e : _movieToActorsIndex.entrySet()) {
			Integer movieID = e.getKey();
			ArrayList<Integer> actorIDs = e.getValue();
			ArrayList<Integer> movieList;
			for (Integer actorID : actorIDs) {
				if (_actorToMoviesIndex.containsKey(actorID)) {
					movieList = _actorToMoviesIndex.get(actorID);
				} else {
					movieList = new ArrayList<Integer>();
				}
				movieList.add(movieID);
				_actorToMoviesIndex.put(actorID, movieList);
			}
		}
	}

	private void buildActorToActorsIndex() {
		for (Entry<Integer, ArrayList<Integer>> e : _actorToMoviesIndex.entrySet()) {
			Integer actorID = e.getKey();
			ArrayList<Integer> movieIDs = e.getValue();
			ArrayList<Integer> actorList;
			HashMap<Integer, Integer> actorSet = new HashMap<Integer, Integer>();

			for (Integer movieID : movieIDs) {
				if (_movieToActorsIndex.containsKey(movieID)) {
					actorList = _movieToActorsIndex.get(movieID);
					for (Integer actor : actorList) {
						if (actorSet.containsKey(actor)) {
							actorSet.put(actor, actorSet.get(actor) + 1);
						} else {
							actorSet.put(actor, 1);
						}
					}
				}
			}

			// removing the current actor from co-actor set
			actorSet.remove(actorID);

			_actorToActorsIndex.put(actorID, actorSet);
		}
	}

	private void removeNullEntries() {
		_movieToMovieIDIndex.remove(null);
		_actorToActorIDIndex.remove(null);
		_movieToActorsIndex.remove(null);
		_actorToMoviesIndex.remove(null);
		_actorToActorsIndex.remove(null);
		_movieToDetailsIndex.remove(null);
		_actorToDetailsIndex.remove(null);

		_movieToMovieIDIndex.remove("");
		_actorToActorIDIndex.remove("");

	}

	@Override
	public void loadIndex() throws IOException, ClassNotFoundException {
		String indexFile = _options._indexPrefix + "/corpus.idx";
		System.out.println("Load index from: " + indexFile);

		// read from the index file
		ObjectInputStream reader = new ObjectInputStream(new FileInputStream(indexFile));
		IndexerMovie loaded = (IndexerMovie) reader.readObject();

		this._movieToMovieIDIndex = loaded._movieToMovieIDIndex;
		this._actorToActorIDIndex = loaded._actorToActorIDIndex;
		this._movieToActorsIndex = loaded._movieToActorsIndex;
		this._actorToMoviesIndex = loaded._actorToMoviesIndex;
		this._actorToActorsIndex = loaded._actorToActorsIndex;
		this._movieToDetailsIndex = loaded._movieToDetailsIndex;
		this._actorToDetailsIndex = loaded._actorToDetailsIndex;

		reader.close();
		loaded = null;
		System.out.println(Integer.toString(_movieToMovieIDIndex.size()) + " movies loaded with "
				+ Integer.toString(_actorToActorIDIndex.size()) + " actors.");

		printIndexStats();
		runTests();
	}

	private void printIndexStats() {
		System.out.println("_movieToMovieIDIndex size:\t" + this._movieToMovieIDIndex.size());
		System.out.println("_actorToActorIDIndex size:\t" + this._actorToActorIDIndex.size());
		System.out.println("_movieToActorsIndex size:\t" + this._movieToActorsIndex.size());
		System.out.println("_actorToMoviesIndex size:\t" + this._actorToMoviesIndex.size());
		System.out.println("_actorToActorsIndex size:\t" + this._actorToActorsIndex.size());
		System.out.println("_movieToDetailsIndex size:\t" + this._movieToDetailsIndex.size());
		System.out.println("_actorToDetailsIndex size:\t" + this._actorToDetailsIndex.size());
	}

	private void runTests() {
		Random r = new Random();
		ArrayList<String> movieNames = new ArrayList<String>();
		ArrayList<String> actorNames = new ArrayList<String>();
		for (int i = 0; i < 5; i++) {
			movieNames.add(_movieToMovieIDIndex.inverse().get(r.nextInt(_movieToMovieIDIndex.size())));
			actorNames.add(_actorToActorIDIndex.inverse().get(r.nextInt(_actorToActorIDIndex.size())));
		}
		testAPIs(movieNames, actorNames);
	}

	private void testAPIs(ArrayList<String> movieNames, ArrayList<String> actorNames) {
		Random r = new Random();
		String movieName = movieNames.get(r.nextInt(movieNames.size()));
		String actorName = actorNames.get(r.nextInt(actorNames.size()));

		// Test 1
		Integer movieID = getMovieIdByName(movieName);
		System.out.println("Movie ID for " + movieName + " is:\t" + movieID);
		System.out.println("Movie Name for " + movieID + " is:\t" + getMovieNameById(movieID));

		Movie m = getMovieDetails(movieID);
		System.out.println("Movie details:\n" + m);

		ArrayList<Integer> actors = getActorsByMovieName(movieName);
		System.out.println("Actors working in the movie " + movieName + " are:\t" + actors);
		for (Integer actorID : actors) {
			System.out.print(getActorNameById(actorID) + "\t");
		}
		System.out.println();

		actors = getActorsByMovieID(movieID);
		System.out.println("Actors working in the movie " + movieID + " are:\t" + actors);
		for (Integer actorID : actors) {
			System.out.print(getActorNameById(actorID) + "\t");
		}
		System.out.println();
		ArrayList<Integer> actorsList = new ArrayList<Integer>();
		actorsList.addAll(actors);

		// Test 2
		System.out.println("\n\n");
		Integer actorID = getActorIdByName(actorName);
		System.out.println("Actor ID for " + actorName + " is:\t" + actorID);
		System.out.println("Actor Name for " + actorID + " is:\t" + getActorNameById(actorID));

		Actor a = getActorDetails(actorID);
		System.out.println("Actor details:\n" + a);

		System.out.println("Movies using actor ID:\t");
		actors = new ArrayList<Integer>();
		actors.add(actorID);
		ArrayList<Integer> movies = getMoviesByActors(actors);
		for (Integer mID : movies) {
			System.out.print(getMovieNameById(mID) + "\t");
		}
		System.out.println();

		System.out.println("Actors who worked with " + actorID + ":\t");
		HashMap<Integer, Integer> actorSet = getActorsWhoWorkedWith(actorID);
		for (Entry<Integer, Integer> actor : actorSet.entrySet()) {
			System.out.print(getActorNameById(actor.getKey()) + "(" + actor.getValue() + " times)\t");
		}
		System.out.println();

		System.out.println("Actors who worked with " + actorName + ":\t");
		actorSet = getActorsWhoWorkedWith(actorName);
		for (Entry<Integer, Integer> actor : actorSet.entrySet()) {
			System.out.print(getActorNameById(actor.getKey()) + "(" + actor.getValue() + " times)\t");
		}
		System.out.println("\n");

		Integer times = r.nextInt(actorsList.size());
		for (int i = 0; i < times; i++) {
			actorsList.remove(r.nextInt(actorsList.size()));
		}
		System.out.println("Movies featuring " + actorsList + " are:\t");
		movies = getMoviesByActors(actorsList);
		for (Integer mID : movies) {
			System.out.print(getMovieNameById(mID) + "\t");
		}
		System.out.println();
	}

	/**
	 * Get the movie ID for the corresponding movie name
	 * 
	 * @param movieName
	 *            The name of the movie
	 * @return Movie ID if name is found in the index, else null
	 */
	public Integer getMovieIdByName(String movieName) {
		if (_movieToMovieIDIndex.containsKey(movieName)) {
			return _movieToMovieIDIndex.get(movieName);
		}
		return null;
	}

	/**
	 * Get the actor ID for the corresponding actor name
	 * 
	 * @param actorName
	 *            The name of the actor
	 * @return Actor ID if name is found in the index, else null
	 */
	public Integer getActorIdByName(String actorName) {
		if (_actorToActorIDIndex.containsKey(actorName)) {
			return _actorToActorIDIndex.get(actorName);
		}
		return null;
	}

	/**
	 * Get the movie name for the corresponding movie ID
	 * 
	 * @param movieID
	 *            The ID of the movie
	 * @return Movie ID if ID is found in the inverse index, else null
	 */
	public String getMovieNameById(Integer movieID) {
		if (_movieToMovieIDIndex.containsValue(movieID)) {
			return _movieToMovieIDIndex.inverse().get(movieID);
		}
		return null;
	}

	/**
	 * Get the actor name for the corresponding actor ID
	 * 
	 * @param actorID
	 *            The ID of the actor
	 * @return Actor name if ID is found in the inverse index, else null
	 */
	public String getActorNameById(Integer actorID) {
		if (_actorToActorIDIndex.containsValue(actorID)) {
			return _actorToActorIDIndex.inverse().get(actorID);
		}
		return null;
	}

	/**
	 * Gets a list of movie IDs that all the actors have worked together in
	 * 
	 * @param actors
	 *            Array list of actor IDs
	 * @return Array list of movie IDs
	 */
	public ArrayList<Integer> getMoviesByActors(ArrayList<Integer> actors) {
		ArrayList<Integer> movies = new ArrayList<Integer>();
		// movies = _actorToMoviesIndex.get(actors.get(0));
		// for (Integer actorID : actors) {
		// if (movies.isEmpty()) {
		// return new ArrayList<Integer>();
		// }
		// if (_actorToMoviesIndex.containsKey(actorID)) {
		// ArrayList<Integer> moviesList = _actorToMoviesIndex.get(actorID);
		// movies.retainAll(moviesList);
		// }
		// }
		HashMap<Integer, Integer> movieCount = new HashMap<Integer, Integer>();
		for (Integer actorID : actors) {
			if (_actorToMoviesIndex.containsKey(actorID)) {
				ArrayList<Integer> moviesList = _actorToMoviesIndex.get(actorID);
				for (Integer movieID : moviesList) {
					if (movieID != null) {
						if (movieCount.containsKey(movieID)) {
							movieCount.put(movieID, movieCount.get(movieID) + 1);
						} else {
							movieCount.put(movieID, 1);
						}
					}
				}
			}
		}

		// Add all the movies which have been counted at least as much as the
		// number of actors
		for (Entry<Integer, Integer> e : movieCount.entrySet()) {
			if (e.getValue() >= actors.size()) {
				movies.add(e.getKey());
			}
		}
		return movies;
	}

	/**
	 * Gets a list of actor IDs that have worked with the specified actor
	 * 
	 * @param actorName
	 *            The name of the actor
	 * @return Map of actor IDs and respective counts
	 */
	public HashMap<Integer, Integer> getActorsWhoWorkedWith(String actorName) {
		Integer actorID = getActorIdByName(actorName);
		if (actorID != null) {
			return getActorsWhoWorkedWith(actorID);
		}
		return null;
	}

	/**
	 * Gets a list of actor IDs that have worked with the specified actor
	 * 
	 * @param actorID
	 *            The ID of the actor
	 * @return Map of actor IDs and respective counts
	 */
	public HashMap<Integer, Integer> getActorsWhoWorkedWith(Integer actorID) {
		if (_actorToActorsIndex.containsKey(actorID)) {
			return _actorToActorsIndex.get(actorID);
		}
		return null;
	}

	/**
	 * Gets a list of actor IDs that have worked in the movie
	 * 
	 * @param movieName
	 *            The name of the movie
	 * @return Array list of actor IDs
	 */
	public ArrayList<Integer> getActorsByMovieName(String movieName) {
		Integer movieID = getMovieIdByName(movieName);
		if (movieID != null) {
			return getActorsByMovieID(movieID);
		}
		return null;
	}

	/**
	 * Gets a list of actor IDs that have worked in the movie
	 * 
	 * @param movieName
	 *            The ID of the movie
	 * @return Array list of actor IDs
	 */
	public ArrayList<Integer> getActorsByMovieID(Integer movieID) {
		if (_movieToActorsIndex.containsKey(movieID)) {
			return _movieToActorsIndex.get(movieID);
		}
		return null;
	}

	/**
	 * Get the details of the specified movie
	 * 
	 * @param movieID
	 *            The ID of the movie
	 * @return Movie object containing details of the movie
	 */
	public Movie getMovieDetails(Integer movieID) {
		if (_movieToDetailsIndex.containsKey(movieID)) {
			return _movieToDetailsIndex.get(movieID);
		}
		return null;
	}

	/**
	 * Get the details of the specified actor
	 * 
	 * @param actorID
	 *            The ID of the actor
	 * @return Actor object containing details of the actor
	 */
	public Actor getActorDetails(Integer actorID) {
		if (_actorToDetailsIndex.containsKey(actorID)) {
			return _actorToDetailsIndex.get(actorID);
		}
		return null;
	}
	
	

	@Override
	public Document getDoc(int docid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document nextDoc(Query query, int docid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int corpusDocFrequencyByTerm(String term) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int corpusTermFrequency(String term) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int documentTermFrequency(String term, int docid) {
		// TODO Auto-generated method stub
		return 0;
	}
}
