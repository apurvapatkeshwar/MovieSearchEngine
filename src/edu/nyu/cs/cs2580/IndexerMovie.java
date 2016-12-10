package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

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

	// Maps an actor ID to the list of actor IDs who he/she has worked with
	private HashMap<Integer, ArrayList<Integer>> _actorToActorsIndex = new HashMap<Integer, ArrayList<Integer>>();

	// Maps a movie ID to a movie object that contains details about the movie
	private HashMap<Integer, Movie> _movieToDetailsIndex = new HashMap<Integer, Movie>();

	// Maps an actor ID to an actor object that contains details about the actor
	private HashMap<Integer, Actor> _actorToDetailsIndex = new HashMap<Integer, Actor>();

	private String actorCorpusPath, movieCorpusPath;

	public IndexerMovie(Options options) {
		super(options);
		actorCorpusPath = options._corpusPrefix + "\\imdbactorlinks.txt";
		movieCorpusPath = options._corpusPrefix + "\\imdbmovielinks.txt";
		System.out.println("Using Indexer: " + this.getClass().getSimpleName());
	}

	@Override
	public void constructIndex() throws FileNotFoundException, IOException {
		System.out.println("Construct index from: " + _options._corpusPrefix);

		readActorCorpus();
		readMovieCorpus();

		System.out.println("Indexed " + Integer.toString(_movieToMovieIDIndex.size()) + " movies with "
				+ Integer.toString(_actorToActorsIndex.size()) + " actors.");
		String indexFile = _options._indexPrefix + "/corpus.idx";
		System.out.println("Saving index to:\t" + indexFile);
		ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(indexFile));
		writer.writeObject(this);
		writer.close();
	}

	private void readMovieCorpus() {
		try (BufferedReader br = new BufferedReader(new FileReader(movieCorpusPath))) {
			String line, params[];
			Movie m;
			Integer movieID = 0;
			while ((line = br.readLine()) != null) {
				params = line.split("\t");

				_movieToMovieIDIndex.put(params[0], movieID);
				m = new Movie(movieID);
				String movieName = params[0] + " (" + params[1] + ")";
				m.setName(movieName);
				m.setRating(Double.parseDouble(params[2]));
				m.setDirector(params[3]);
				m.setPictureUrl(params[4]);

				if ((line = br.readLine()) != null) {
					params = line.split("\t");
					m.setDescription(params[0]);
				}

				if ((line = br.readLine()) != null) {
					// TODO Set actors
					params = line.split("\t");
				}

				_movieToDetailsIndex.put(movieID, m);
				movieID++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}

	private void readActorCorpus() {
		// TODO Auto-generated method stub
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
				+ Integer.toString(_actorToActorsIndex.size()) + " actors.");
		
		// for(Entry<String, Integer> e: _movieToMovieIDIndex.entrySet()) {
		// System.out.println(e.getKey() + "\t" + e.getValue());
		// }
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
	 * @param movieId
	 *            The ID of the movie
	 * @return Movie ID if ID is found in the inverse index, else null
	 */
	public String getMovieNameById(String movieId) {
		if (_movieToMovieIDIndex.containsValue(movieId)) {
			return _movieToMovieIDIndex.inverse().get(movieId);
		}
		return null;
	}

	/**
	 * Get the actor name for the corresponding actor ID
	 * 
	 * @param actorId
	 *            The ID of the actor
	 * @return Actor name if ID is found in the inverse index, else null
	 */
	public String getActorNameById(String actorId) {
		if (_actorToActorIDIndex.containsValue(actorId)) {
			return _actorToActorIDIndex.inverse().get(actorId);
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
		movies = _actorToMoviesIndex.get(actors.get(0));
		for (Integer actorID : actors) {
			if (movies.isEmpty()) {
				break;
			}
			if (_actorToMoviesIndex.containsKey(actorID)) {
				ArrayList<Integer> moviesList = _actorToMoviesIndex.get(actorID);
				movies.retainAll(moviesList);
			}
		}
		// HashMap<Integer, Integer> movieCount = new HashMap<Integer,
		// Integer>();
		// for (Integer actorID : actors) {
		// if (_actorToMoviesIndex.containsKey(actorID)) {
		// ArrayList<Integer> moviesList = _actorToMoviesIndex.get(actorID);
		// for (Integer movieID : moviesList) {
		// if (movieCount.containsKey(movieID)) {
		// movieCount.put(movieID, movieCount.get(movieID) + 1);
		// } else {
		// movieCount.put(movieID, 1);
		// }
		// }
		// }
		// }
		//
		// // Add all the movies which have been counted at least as much as the
		// // number of actors
		// for (Entry<Integer, Integer> e : movieCount.entrySet()) {
		// if (e.getValue() >= actors.size()) {
		// movies.add(e.getKey());
		// }
		// }
		return movies;
	}

	/**
	 * Gets a list of actor IDs that have worked with the specified actor
	 * 
	 * @param actorName
	 *            The name of the actor
	 * @return Array list of actor IDs
	 */
	public ArrayList<Integer> getActorsWhoWorkedWith(String actorName) {
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
	 * @return Array list of actor IDs
	 */
	public ArrayList<Integer> getActorsWhoWorkedWith(Integer actorID) {
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
