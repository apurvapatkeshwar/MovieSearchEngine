package edu.nyu.cs.cs2580;

/**
 * Document with score.
 * 
 * @author fdiaz
 * @author congyu
 */
class ScoredDocument implements Comparable<ScoredDocument> {
  private Document _doc;
  private double _score,_pageR;
  private int _numViews;

  public ScoredDocument(Document doc, double score,double pageR, int numViews) {
    _doc = doc;
    _score = score;
    _pageR = pageR;
    _numViews = numViews;
  }
 
  public Document returnDoc(){
	  return _doc;
  }
  
  public String asTextResult() {
    StringBuffer buf = new StringBuffer();
    buf.append(_doc._docid).append("\t");
    buf.append(_doc.getTitle()).append("\t");
    buf.append(_score);
    buf.append("\t").append(_pageR);
    buf.append("\t").append(_numViews);
    return buf.toString();
  }

  /**
   * @CS2580: Student should implement {@code asHtmlResult} for final project.
   */
  public String asHtmlResult() {
    return "";
  }

  @Override
  public int compareTo(ScoredDocument o) {
    if (this._score == o._score) {
      return 0;
    }
    return (this._score > o._score) ? 1 : -1;
  }
}