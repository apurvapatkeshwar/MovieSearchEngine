package project;

import java.util.Vector;

import project.SearchEngine.Options;

public abstract class Ranker {
  // Options to configure each concrete Ranker.
  protected Options _options;
  
  protected Indexer _indexer;

  /**
   * Constructor: the construction of the Ranker requires an Indexer.
   */
  protected Ranker(Options options, Indexer indexer){
	  _options = options;
	  _indexer = indexer;
  }

  protected Ranker(Indexer indexer){
	  _indexer = indexer;
  }
  
  /**
   * Processes one query.
   * @param query the parsed user query
   * @param numResults number of results to return
   * @param mode search according to mode
   * @return Up to {@code numResults} scored documents in ranked order
   */
  public abstract Vector<ScoredMovie> runQuery(Query query, int numResults, String mode);
  
  /**
   * All Rankers must be created through this factory class based on the
   * provided {@code arguments}.
   */
  public static class Factory {
    public static Ranker getRanker(Options options, Indexer indexer){
  	  return new RankerFavorite(options, indexer);
    }
  }

  	
}
