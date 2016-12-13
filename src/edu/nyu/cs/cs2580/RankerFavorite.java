package edu.nyu.cs.cs2580;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Vector;
import java.util.Map.Entry;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

public class RankerFavorite extends Ranker {
	private float _betaRating = 1.0f;
	private float _betaYear = 1.0f;
	private float _betaNumReviews = 1.0f;
	ArrayList<Integer> ActorID_List;
	ArrayList<Entry<Integer, Double>> Similarity_List = new ArrayList<Entry<Integer, Double>>();
	protected RankerFavorite(Options options, CgiArguments arguments, Indexer indexer) {
		super(options, arguments, indexer);
		System.out.println("Using Ranker: " + this.getClass().getSimpleName());
		_betaRating=options._betaValues.get("beta_rat");//include in engine.conf
		_betaYear=options._betaValues.get("beta_yr");
		_betaNumReviews=options._betaValues.get("beta_numrev");
  }

@Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults, int mode) {    
    Vector<String> queryV;
	Vector<ScoredDocument> all = new Vector<ScoredDocument>();
	Vector<ScoredDocument> results = new Vector<ScoredDocument>();
	ArrayList<Integer> movieL = new ArrayList<Integer>();
	queryV=query._tokens;
	
	if(mode==0){//Query Type is Actor ID- Add Movies
	    
		ActorID_List=getActorIDList(queryV);//get Actor IDs from query tokens
	    if (ActorID_List.size()==query._tokens.size()){//exact match search
	    movieL=_indexer.getMoviesByActors(ActorID_List);//get Movies common to all actors
	    	for (int i = 0; i < movieL.size(); ++i) {
	    		all.add(scoreDocument(movieL.get(i)));//check if mid
	    	}
	    }
	    
	    
	    if (all.size()<1){//similarity search
	    	all.clear();
	    	Vector<String> queryT = new Vector<String>();
	    	int actorid;
	    	for(String ActorName : queryV){//find similar actor names save in queryV
	    		actorid=_indexer.getTopMatches(query._query, 10, 0.7, "movie").get(0).getKey();
	    		queryT.add(_indexer.getActorNameById(actorid));
	    	}
	    	ActorID_List=getActorIDList(queryV);
	    	movieL=_indexer.getMoviesByActors(ActorID_List);//get Movies common to all actors
	    	for (int i = 0; i < movieL.size(); ++i) {
	    		all.add(scoreDocument(movieL.get(i)));//check if mid
	    	}
	    }
	    
	    
	    if (all.size()<1){//similarity search with monogram and display union of movies
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
	    		ActorsArr=_indexer.getTopMatches(query._query, 10, 0.7, "movie");
	    		for(int i=0; i<ActorsArr.size();i++){//Add all similar actors
	    			ActorID_List.clear();
	    			ActorID_List.add(ActorsArr.get(i).getKey());
	    			movieL=_indexer.getMoviesByActors(ActorID_List);
	    			MoviesSet.addAll(movieL);
	    		}
	    	}
	    	
	    	for (int mid : MoviesSet) {//add the union of all movies to results
	    		all.add(scoreDocument(mid));//check if mid
	    	}
	    }
	}
	else if (mode==1){//Query Type is Movie Id - Return 1 Movie
	    int movid;
		if(_indexer.getMovieIdByName(query._query)!=null){
			movid=_indexer.getMovieIdByName(query._query);
			all.addElement(scoreDocument(movid));
		}
		else{
			//for(int i=0;i<all.size() && numResults;i++){
			//movid=_indexer.getTopMatches(query._query, 10, 0.7, "movie").get(i).getKey();
				movid=_indexer.getTopMatches(query._query, 10, 0.7, "movie").get(0).getKey();
				all.addElement(scoreDocument(movid));
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
		ActorID_List.add(_indexer.getActorIdByName(ActorName));
	}
	  ActorID_List.remove(null);
	  return ActorID_List;
  }
  
//MAKE CHANGES
private ScoredDocument scoreDocument(int mid) {
	Double Rating;
	Integer Year,NumRev;
    Movie mov = _indexer.getMovieDetails(mid);
    Rating=mov.getRating();
    Year=Integer.parseInt(mov.getYear());
    NumRev=mov.getRatingsCount();
    double score = _betaRating*Rating+_betaYear*Year+_betaNumReviews*NumRev;
    return new ScoredDocument(mov, score);
  }
}