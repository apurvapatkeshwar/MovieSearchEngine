package project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Vector;
import java.util.Map.Entry;

import project.SearchEngine.Options;

public class RankerFavorite extends Ranker{
	private float _betaRating = 1.0f;
	private float _betaYear = 1.0f;
	private float _betaNumReviews = 1.0f;
	ArrayList<Integer> ActorID_List = new ArrayList<Integer>();
	ArrayList<Entry<Integer, Double>> Similarity_List = new ArrayList<Entry<Integer, Double>>();
	IndexerMovie _indexerMovie;
	
	protected RankerFavorite(Options options, Indexer indexer) {
		super(options, indexer);
		System.out.println("Using Ranker: " + this.getClass().getSimpleName());
		_betaRating=options._betaValues.get("beta_rat");
		_betaYear=options._betaValues.get("beta_yr");
		_betaNumReviews=options._betaValues.get("beta_numrev");
		_indexerMovie = (IndexerMovie)_indexer;
	}
	
	@Override
	public Vector<ScoredMovie> runQuery(Query query, int numResults, String mode) {    
		Vector<String> queryV;
		Vector<ScoredMovie> all = new Vector<ScoredMovie>();
		Vector<ScoredMovie> results = new Vector<ScoredMovie>();
		ArrayList<Integer> movieList = new ArrayList<Integer>();
		queryV=query._tokens;
		
		// Search movies by Actor
		if(mode.equals("actor")){
			// Get actor IDs from query tokens
			ActorID_List = getActorIDList(queryV);
			// Exact match
		    if (ActorID_List.size() == query._tokens.size()){
		    	// Return the movie list all the actors were in
		    	movieList = _indexerMovie.getMoviesByActors(ActorID_List);
		    	for (int i = 0; i < movieList.size(); ++i) {
		    		all.add(scoreMovie(movieList.get(i)));//check if mid
		    	}
		    }
		    
		    // Similarity search
		    if (all.size()<1){
		    	all.clear();
		    	Vector<String> queryT = new Vector<String>();
		    	int actorid;
		    	//find similar actor names save in queryV
		    	for(String ActorName : queryV){
		    		actorid = _indexerMovie.getTopMatches(query._query, 10, 0.7, "movie").get(0).getKey();
		    		queryT.add(_indexerMovie.getActorNameById(actorid));
		    	}
		    	ActorID_List=getActorIDList(queryV);
		    	//get Movies common to all actors
		    	movieList=_indexerMovie.getMoviesByActors(ActorID_List);//get Movies common to all actors
		    	for (int i = 0; i < movieList.size(); ++i) {
		    		all.add(scoreMovie(movieList.get(i)));//check if mid
		    	}
		    }
		    
		    //similarity search with monogram and display union of movies
		    if (all.size()<1){
		    	all.clear();
		    	queryV.clear();
		    	ActorID_List.clear();
		    	//Vector<String> queryT = new Vector<String>();
		    	HashSet<Integer> MoviesSet = new HashSet<Integer>();
		    	ArrayList<Entry<Integer,Double>> ActorsArr = new ArrayList<Entry<Integer,Double>>();
		    	//int actorid;
		    	Scanner s = new Scanner(query._query);
				s.useDelimiter("\\s*(\\sand\\s|,|\\s)\\s*"); //split query by "and" or ,
			    while (s.hasNext()) {
			      queryV.add(s.next());
			    }
			    s.close();
		    	for(String ActorName : queryV){//find similar multiple actors names save in queryV
		    		ActorsArr = _indexerMovie.getTopMatches(query._query, 10, 0.7, "movie");
		    		for(int i=0; i<ActorsArr.size();i++){//Add all similar actors
		    			ActorID_List.clear();
		    			ActorID_List.add(ActorsArr.get(i).getKey());
		    			movieList = _indexerMovie.getMoviesByActors(ActorID_List);
		    			MoviesSet.addAll(movieList);
		    		}
		    	}
		    	
		    	for (int mid : MoviesSet) {//add the union of all movies to results
		    		all.add(scoreMovie(mid));//check if mid
		    	}
		    }
		}
		//Search movies by Movie
		else if (mode.equals("movie")){
		    int movid;
			if(_indexerMovie.getMovieIdByName(query._query)!=null){
				movid=_indexerMovie.getMovieIdByName(query._query);
				all.addElement(scoreMovie(movid));
			}
			else{
				//for(int i=0;i<all.size() && numResults;i++){
				//movid=_indexer.getTopMatches(query._query, 10, 0.7, "movie").get(i).getKey();
				movid=_indexerMovie.getTopMatches(query._query, 10, 0.7, "movie").get(0).getKey();
				all.addElement(scoreMovie(movid));
			}
		}
		Collections.sort(all, Collections.reverseOrder());
	    for (int i = 0; i < all.size() && i < numResults; ++i) {
	      results.add(all.get(i));
	    }
	    return results; 
	  }
	  
	  private ArrayList<Integer> getActorIDList(Vector<String> queryV) {
		  ActorID_List.clear();
		  for(String ActorName : queryV){
			ActorID_List.add(_indexerMovie.getActorIdByName(ActorName));
		  }
		  ActorID_List.remove(null);
		  return ActorID_List;
	  }
	  
	  private ScoredMovie scoreMovie(int mid) {
		  Double Rating;
		  Integer Year,NumRev;
		  Movie mov = _indexerMovie.getMovieDetails(mid);
		  Rating=mov.getRating();
		  Year=Integer.parseInt(mov.getYear());
		  NumRev=mov.getRatingsCount();
		  double score = _betaRating*Rating+_betaYear*Year+_betaNumReviews*NumRev;
		  return new ScoredMovie(mov, score);
	  }
}
