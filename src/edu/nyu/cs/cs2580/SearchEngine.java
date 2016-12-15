package project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

public class SearchEngine {

  public static class Options {
    // The parent path where the corpus resides.
    public String _corpusPrefix = null;
    
    // The parent path where the constructed index resides.
    public String _indexPrefix = null;
    
    // The specific Indexer to be used.
    public String _indexerType = null;
    
    // The name of movie corpus file.
    public String _movieCorpus = null;
    
    // The name of actor corpus file.
    public String _actorCorpus = null;
    
    // The parameter to be used in Ranker.
    private static final String[] BETA_PARAMS={
    		"beta_rat", "beta_yr", "beta_numrev"
    };
    public Map<String, Float> _betaValues;

    public Options(String optionsFile) throws IOException {
      // Read options from the file.
      BufferedReader reader = new BufferedReader(new FileReader(optionsFile));
      Map<String, String> options = new HashMap<String, String>();
      String line = null;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) {
          continue;
        }
        String[] vals = line.split(":", 2);
        if (vals.length < 2) {
          reader.close();
          Check(false, "Wrong option: " + line);
        }
        options.put(vals[0].trim(), vals[1].trim());
      }
      reader.close();
      
      // Populate global options.
      _corpusPrefix = options.get("corpus_prefix");
      Check(_corpusPrefix != null, "Missing option: corpus_prefix!");
      _indexPrefix = options.get("index_prefix");
      Check(_indexPrefix != null, "Missing option: index_prefix!");
      
      // Populate specific options.
      _indexerType = options.get("indexer_type");
      Check(_indexerType != null, "Missing option: indexer_type!");
      
      _movieCorpus = options.get("movie_corpus");
      Check(_movieCorpus != null, "Missing option: movie_corpus!");
      _actorCorpus = options.get("actor_corpus");
      Check(_actorCorpus != null, "Missing option: actor_corpus!");
      
      // Populate specific parameter for ranker
      _betaValues = new HashMap<String, Float>();
      for(String s: BETA_PARAMS){
    	  _betaValues.put(s, Float.parseFloat(options.get(s)));
      }
    }
  }
  
  public static Options OPTIONS = null;
  
  public Indexer indexer;
  
  public SearchEngine() throws ClassNotFoundException, IOException{
	  //OPTIONS = new Options(System.getProperty("user.dir")+"conf/engine.conf");
	  
	  if(indexer == null){
		  indexer = Indexer.Factory.getIndexerByOption(SearchEngine.OPTIONS);
		  Check(indexer != null, "Indexer " + SearchEngine.OPTIONS._indexerType + " not found!");	  
	  }
	  indexer.loadIndex();
  }
  
  /**
   * Prints {@code msg} and exits the program if {@code condition} is false.
   */
  public static void Check(boolean condition, String msg) {
    if (!condition) {
      System.err.println("Fatal error: " + msg);
      System.exit(-1);
    }
  }
  
}
