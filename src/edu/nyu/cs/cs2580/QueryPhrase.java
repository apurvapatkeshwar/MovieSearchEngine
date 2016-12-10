package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @CS2580: implement this class for HW2 to handle phrase. If the raw query is
 *          ["new york city"], the presence of the phrase "new york city" must
 *          be recorded here and be used in indexing and ranking.
 */
public class QueryPhrase extends Query {

	public Vector<Query> phrase = new Vector<Query>();

	public QueryPhrase(String query) {
		super(query);
	}

	@Override
	public void processQuery() {
		if (_query == null) {
			return;
		}

		String localq = _query;
		String phrs;
		// System.out.println(localq+"in process query before process");
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(localq);
		localq = localq.replace("\"", " ");
		while (m.find()) {
			phrs = m.group(1);
			Query qph = new Query(phrs);
			qph.processQuery();
			phrase.add(qph);
		}

		Scanner s = new Scanner(localq);
		while (s.hasNext()) {

			String text = s.next();
			_tokens.add(text);
			// System.out.println(text+" Inside scanner");
		}

		s.close();

	}
}