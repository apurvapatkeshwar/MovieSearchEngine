package project;

import java.io.IOException;

import project.SearchEngine.Options;

public abstract class Indexer {
  // Options to configure each concrete Indexer, do not serialize.
  protected Options _options = null;

  // Provided for serialization.
  public Indexer() { }

  // The real constructor
  public Indexer(Options options) {
    _options = options;
  }


  /**
   * Called when the SearchEngine is in {@code Mode.INDEX} mode. Subclass must
   * construct the index from the provided corpus at {@code corpus_prefix}.
   * 
   * Document processing must satisfy the following:
   *   1) Non-visible page content is removed, e.g., those inside <script> tags
   *   2) Tokens are stemmed with Step 1 of the Porter's algorithm
   *   3) No stop word is removed, you need to dynamically determine whether to
   *      drop the processing of a certain inverted list.
   *
   * The index must reside at the directory of index_prefix, no other data can
   * be stored (either in a hidden file or in a temporary directory). We will
   * construct your index on one machine and move the index to a different
   * machine for serving, so do NOT try to play tricks. 
   */
  public abstract void constructIndex() throws IOException;

  /**
   * Called exactly once when the SearchEngine is in {@code Mode.SERVE} mode.
   * Subclass must load the index at {@code index_prefix} to be ready for
   * serving the search traffic.
   * 
   * You must load the index from the constructed index above, do NOT try to
   * reconstruct the index from the corpus. When the search engine is run in
   * serve mode, it will NOT have access to the corpus, all grading for serve
   * mode will be done with the corpus removed from the machine.
   */
  public abstract void loadIndex() throws IOException, ClassNotFoundException;


  /**
   * All Indexers must be created through this factory class based on the
   * provided {@code options}.
   */
  public static class Factory {
    public static Indexer getIndexerByOption(Options options) {
      if (options._indexerType.equals("movie")) {
        return new IndexerMovie(options);
      }
      return null;
    }
  }
  
  public static void main(String args[]) throws Exception{
	  SearchEngine.OPTIONS = new Options("conf/engine.conf");
	  Indexer indexer = Indexer.Factory.getIndexerByOption(SearchEngine.OPTIONS);  
	  indexer.constructIndex();
  }
  
}
