package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.util.Vector;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * This is the abstract Ranker class for all concrete Ranker implementations.
 *
 * Use {@link Ranker.Factory} to create your concrete Ranker implementation. Do
 * NOT change the interface in this class!
 *
 * In HW1: {@link RankerFullScan} is the instructor's simple ranker and students
 * implement four additional concrete Rankers.
 *
 * In HW2: students will pick a favorite concrete Ranker other than
 * {@link RankerPhrase}, and re-implement it using the more efficient concrete
 * Indexers.
 *
 * 2013-02-16: The instructor's code went through substantial refactoring
 * between HW1 and HW2, students are expected to refactor code accordingly.
 * Refactoring is a common necessity in real world and part of the learning
 * experience.
 *
 * @author congyu
 * @author fdiaz
 */
public abstract class Ranker {
	// Options to configure each concrete Ranker.
	protected Options _options;
	// CGI arguments user provide through the URL.
	protected CgiArguments _arguments;

	// The Indexer via which documents are retrieved, see {@code
	// IndexerFullScan}
	// for a concrete implementation. N.B. Be careful about thread safety here.
	protected Indexer _indexer;

	/**
	 * Constructor: the construction of the Ranker requires an Indexer.
	 */
	protected Ranker(Options options, CgiArguments arguments, Indexer indexer) {
		_options = options;
		_arguments = arguments;
		_indexer = indexer;
	}

	/**
	 * Processes one query.
	 * 
	 * @param query
	 *            the parsed user query
	 * @param numResults
	 *            number of results to return
	 * @return Up to {@code numResults} scored documents in ranked order
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public abstract Vector<ScoredDocument> runQuery(Query query, int numResults)
			throws ClassNotFoundException, IOException;

	/**
	 * All Rankers must be created through this factory class based on the
	 * provided {@code arguments}.
	 */
	public static class Factory {
		public static Ranker getRankerByArguments(CgiArguments arguments, Options options, Indexer indexer) {
			switch (arguments._rankerType) {
			case FULLSCAN:
				// Plug in your full scan Ranker
				break;
			case CONJUNCTIVE:
				// Plug in your conjunctive Ranker
				break;
			case FAVORITE:
				// Plug in your favorite Ranker
				break;
			case COMPREHENSIVE:
				// Plug in your comprehensive Ranker
				break;
			case COSINE:
				// Plug in your cosine Ranker
				break;
			case QL:
				// Plug in your QL Ranker
				break;
			case PHRASE:
				// Plug in your phrase Ranker
				break;
			case LINEAR:
				// Plug in your linear Ranker
				break;
			case NONE:
				// Fall through intended
			default:
				// Do nothing.
			}
			return null;
		}
	}
}