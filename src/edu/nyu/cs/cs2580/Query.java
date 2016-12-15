package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.util.BitSet;
import java.util.Scanner;
import java.util.Vector;

/**
 * Representation of a user query.
 * 
 * In HW1: instructors provide this simple implementation.
 * 
 * In HW2: students must implement {@link QueryPhrase} to handle phrases.
 * 
 * @author congyu
 * @auhtor fdiaz
 */
public class Query {
  public String _query = null;
  public Vector<String> _tokens = new Vector<String>();
  public int movieId;
  public Query(String query, int Mode) {
    _query = query;
  }

  public void processQuery() {
    short SearchMode= 0;//Search Mode =0 means Actor Search, 1 means Movie Search, 2 means I dont know?
    if (_query == null) {
      return;
    }
	if(SearchMode==0){//Actor
		Scanner s = new Scanner(_query);
		s.useDelimiter("\\s*(\\sand\\s|,)\\s*"); //split query by "and" or ,
	    while (s.hasNext()) {
	      _tokens.add(s.next());
	    }
	    s.close();
	}
	else if (SearchMode==1){//Movie
		_tokens.add(_query);
	}
	else if (SearchMode==2){
		System.out.println("NOT HANDLED");
	}
  }
}
