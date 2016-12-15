package project;

import java.util.Scanner;
import java.util.Vector;

public class Query {
	  public String _query = null;
	  public Vector<String> _tokens = new Vector<String>();
	  public int _movieId;
	  public String _mode;
	  public Query(String query, String mode){
		  _query = query;
		  _mode = mode;
	  }

	  public void processQuery() {
	    if (_query == null) {
	      return;
	    }
		if(_mode.equals("actor")){
			//split query by "and" or ","
			Scanner s = new Scanner(_query).useDelimiter("\\s*(\\sand\\s|,)\\s*");
			while (s.hasNext()) {
		    	_tokens.add(s.next());
		    }
		    s.close();
		}
		else if (_mode.equals("movie")){
			_tokens.add(_query);
		}
		else{
			System.out.println("NOT HANDLED");
		}
	}
}
