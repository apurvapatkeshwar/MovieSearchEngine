package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.nyu.cs.cs2580.SearchEngine.Options;

public class MovieURLextractor {
	protected Options _options = null;
	private static Document docimdb,docwiki,docimdbCast,docMov;
	private static String corpusPrefix="data/wiki",urlimdb,urlwiki,urlcast,urlmov,genre,descr,line,movie,year,title,rating,userrev,picurl,director;
	private static String[] mov, actorList;
	private static Elements details;
	private static PrintWriter writer;
	private static int yearint;
	private static HashSet<String> ActorsSet=new HashSet<String>();
	
	public MovieURLextractor(Options options){
		_options=options;
		corpusPrefix=_options._corpusPrefix;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException{
		String corpusFile = corpusPrefix+"/Category-Lists_of_American_films_by_year.htm";		//add in conf option
	    String output1 = corpusPrefix+"/listofurls.txt", output2 = corpusPrefix+"/lmv.txt", output3 = corpusPrefix+"/imdbmovielinks.txt", output4 = corpusPrefix+"/actorlist.txt";  //add in options folder
	    
	      //extract urls of the lists with movies
	      extractURL(corpusFile, output1);
	      //extract movie names from the lists
	      extractMoviesN(output1, output2);
	      //find imdb links to these movies
	      extractIMDB(output2, output3);
	      removeDUP(output3);
	      //find pics for unique actors
	      extractPICS(output3.replace(".txt", "-NODUP.txt"), output4);//72000
	}
	
	private static void extractPICS(String corpusFile, String output) throws IOException, InterruptedException{
		int i=0;
		writer = new PrintWriter(new BufferedWriter(new FileWriter(output, true)));
		File fileDir = new File(corpusFile);
        BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF-8"));
        //Create unique set
        
     try{
        while((line =bfr.readLine())!= null){
        	line=bfr.readLine();
        	line=bfr.readLine();
        	actorList = line.split("\t");
        	Collections.addAll(ActorsSet, actorList);        	
        }
        ActorsSet.remove("");
        ActorsSet.remove(null);
        
		for (String actor : ActorsSet) {
			i++;
        	try{//Check 1st way
			urlwiki="https://en.wikipedia.org/wiki/"+actor.replace(" ", "_");
        	docwiki = Jsoup.connect(URI.create(urlwiki).toASCIIString()).timeout(10000).get();
			details=docwiki.select("#mw-content-text > table.infobox.biography.vcard > tbody > tr:nth-child(1) > th > span");
			if (actor.equals(details.first().text())){
				details=docwiki.select("#mw-content-text > div.thumb.tright > div > a > img, #mw-content-text > table.infobox.biography.vcard > tbody > tr:nth-child(2) > td > a > img");
				try{
					picurl=details.first().attr("src");
				}catch(Exception e){
					picurl="null";
					}
				}
        	}
			catch(Exception e){//check if disambig page
				details=docwiki.select("table#disambigbox > tbody > tr > td.mbox-text > a:nth-child(1)");
				try{
					if (details.first().text().equals("disambiguation")){
					try{//check disambig url
    					urlwiki=urlwiki+"_(actor)";
    		        	docwiki = Jsoup.connect(URI.create(urlwiki).toASCIIString()).timeout(10000).get();
    		        	details=docwiki.select("#mw-content-text > table.infobox.biography.vcard > tbody > tr:nth-child(1) > th > span");
    					if (actor.equals(details.first().text())){
    						details=docwiki.select("#mw-content-text > div.thumb.tright > div > a > img, #mw-content-text > table.infobox.biography.vcard > tbody > tr:nth-child(2) > td > a > img");
    						try{
    							picurl=details.first().attr("src");
    						}catch(Exception e2){
    							picurl="null";
    							}
    						}
    				}catch(Exception e1){
    					try{
        					urlwiki=urlwiki.replace("_(actor)", "")+"_(actress)";
        		        	docwiki = Jsoup.connect(URI.create(urlwiki).toASCIIString()).timeout(10000).get();
        		        	details=docwiki.select("#mw-content-text > table.infobox.biography.vcard > tbody > tr:nth-child(1) > th > span");
        					if (actor.equals(details.first().text())){
        						details=docwiki.select("#mw-content-text > div.thumb.tright > div > a > img, #mw-content-text > table.infobox.biography.vcard > tbody > tr:nth-child(2) > td > a > img");
        						try{
        							picurl=details.first().attr("src");
        						}catch(Exception e2){
        							picurl="null";
        							}
        						}
    					}catch(Exception e3){
        							urlwiki="null";
        							picurl="null";
        						}
    				}
				}
				}catch(Exception e1){
					urlwiki="null";
					picurl="null";
				}
				}
        	writer.println(actor+"\t"+picurl+"\t"+urlwiki);
        	System.out.println(i);
        	Thread.sleep(100);
         }
      	}catch(Exception e){
        	System.out.println("Exception thrown : Saving file");
		    bfr.close();
		    writer.close();
        }
        bfr.close();
	    writer.close();
}
	private static void extractIMDB(String corpusFile, String output) throws IOException, InterruptedException{
		writer = new PrintWriter(new BufferedWriter(new FileWriter(output, true)));
		File fileDir = new File(corpusFile);
        BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF-8"));
        int count = 0;
		final int maxTries = 5;
		boolean done=false;
			
		try{
		while((line = bfr.readLine()) != null) {
			done=false;
			count=0;
			rating="";
			picurl="";
			director="";
			genre="";
			descr="";
			mov=line.split("@"); 
			movie=mov[0].replace(" ", "%20");
			year=mov[1];
			yearint=Integer.parseInt(year);
			urlwiki="https://en.wikipedia.org"+mov[2];
			urlimdb="http://www.imdb.com/search/title?countries=us&release_date="+year+","+year+"&title="+movie+"&title_type=feature";
			//try max of 5 times
			while(!done){
				try{
					docimdb = Jsoup.connect(URI.create(urlimdb).toASCIIString()).timeout(10000).get();
					details=docimdb.select("#main > div > div > div");
					if (!details.first().text().equals("No results.")){
						getMovie();
						//print to file
						printMovie();
							
						}
						else{					//COMMENT LATER
							urlimdb="http://www.imdb.com/search/title?title="+movie+"&release_date="+(yearint-1)+","+(yearint+1)+"&title_type=feature";
							docimdb = Jsoup.connect(URI.create(urlimdb).toASCIIString()).timeout(5000).get();
							System.out.println("CHECKING ALTERNATELY "+mov[0]);
							details=docimdb.select("#main > div > div > div");
							if (!details.first().text().equals("No results.")){
								getMovie();
								details=docimdb.select("#main > div > div > div.lister-list > div > div.lister-item-content > h3 > span.lister-item-year.text-muted.unbold");
								year=details.first().text();
								year=year.substring(1,5);
								printMovie();
							}
							else{
								System.out.println("NO RESULTS FOUND - SKIPPING MOVIE");
								}
						}
					done=true;
					}
					catch (SocketTimeoutException e){
						if (++count == maxTries) {
							System.out.println("I timed out! Not saving "+movie);
							done=true;
							bfr.close();
							writer.close();
						}
					}
				Thread.sleep(500);//delay not to overload imdb
				}
			}
		}
		catch (Exception e) {
		    System.out.println("Exception thrown : Saving file");
		    bfr.close();
		    writer.close();
		}
		bfr.close();
		writer.close();
	}
	
	private static void getMovie() throws IOException {
		details=docimdb.select("#main > div > div > div.lister-list > div:nth-child(1) > div.lister-item-content > h3 > a");
		title=details.first().text();
		urlmov=details.first().attr("href");
		urlcast=parseimdbUrl(urlmov);
		docimdbCast = Jsoup.connect(URI.create(urlcast).toASCIIString()).timeout(10000).get();
		details = docimdbCast.select("#fullcredits_content > table.cast_list > tbody > tr > td.itemprop > a > span");
		getCast(details);
		try{
			details=docimdb.select("#main > div > div > div.lister-list > div:nth-child(1) > div.lister-item-content > p:nth-child(4)");
			descr=details.first().text();
			if (descr.startsWith("Director")){
				details=docimdb.select("#main > div > div > div.lister-list > div:nth-child(1) > div.lister-item-content > p:nth-child(3)");
				descr=details.first().text();
			}
			if (descr.equals("Add a Plot")){
				descr="null";
			}
		}catch(Exception e){
			descr="null";
		}
		
		try{
			details=docimdb.select("#main > div > div > div.lister-list > div:nth-child(1) > div.lister-item-content > p:nth-child(2) > span.genre");
			genre=details.first().text();
		}catch(Exception e){
			genre="null";
		}
		
		try{
			details=docimdb.select("#main > div > div > div.lister-list > div:nth-child(1) > div.lister-item-content > p:nth-child(5) > a:nth-child(1)");
			director=details.first().text();
		}catch(Exception e){
			try{
				details=docimdb.select("#main > div > div > div.lister-list > div:nth-child(1) > div.lister-item-content > p:nth-child(4) > a:nth-child(1)");
				director=details.first().text();
			}catch(Exception e1){
				
				director="null";
			}
		}
		try{
			docwiki = Jsoup.connect(urlwiki).timeout(10000).get();
			details=docwiki.select("#mw-content-text > table > tbody > tr:nth-child(2) > td > a > img");
			picurl=details.first().attr("src");
		}catch(Exception e){
			picurl="null";
		}
		try{
			details=docimdb.select("#main > div > div > div.lister-list > div:nth-child(1) > div.lister-item-content > div > div.inline-block.ratings-imdb-rating > strong");
			rating=details.first().text();
			try{
				docMov = Jsoup.connect("http://www.imdb.com/"+urlmov).timeout(10000).get();
				details=docMov.select("#title-overview-widget > div.vital > div.title_block > div > div.ratings_wrapper > div.imdbRating > a > span");
				userrev=details.first().text();
			}catch(Exception e){
				userrev = "0";
			}
		}catch (Exception e){
			rating="0.0";
			userrev="0";
		}
	}
	private static void printMovie() {
		writer.println(title+"\t"+year+"\t"+genre+"\t"+rating+"\t"+userrev+"\t"+director+"\t"+picurl+"\t"+urlwiki);
		writer.println(descr);
		for(String actor : actorList){
			writer.print(actor+"\t");
		}
		writer.println();
	}
	private static void getCast(Elements details) {
		actorList = new String[details.size()];
		int i=0;
		for(Element e : details){
			actorList[i]=e.text();
			i++;
		}
	}

	private static String parseimdbUrl(String attr) {
		String[] parts = attr.split("/");
		String x = parts[2];
		String castUrl="http://www.imdb.com/title/"+x+"/fullcredits?ref_=tt_cl_sm";
		return castUrl;
	}
	
	private static void removeDUP(String corpusFile) throws IOException{
		writer = new PrintWriter(new BufferedWriter(new FileWriter(corpusFile.replace(".txt", "-NODUP.txt"))));
		String line2,line3;
		HashSet<String> MoviesList = new HashSet<String>();
		String[] movList;
		File fileDir = new File(corpusFile);
        BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF-8"));
        try{
        while((line =bfr.readLine())!= null){
        	movList=line.split("\t");
        	if (!MoviesList.contains(movList[0]+movList[1])){
        	MoviesList.add(movList[0]+movList[1]);
        	line2=bfr.readLine();
        	line3=bfr.readLine();
        	writer.println(line);
        	writer.println(line2);
        	writer.println(line3);
        	}else{
        		line2=bfr.readLine();
            	line3=bfr.readLine();
        	}
        }
        }
        catch(Exception e){
        	writer.close();
        	bfr.close();
        }
        writer.close();
        bfr.close();
	}

	private static void extractMoviesN(String corpusFile, String output) throws IOException, InterruptedException{
		writer = new PrintWriter(new BufferedWriter(new FileWriter(output)));
		File fileDir = new File(corpusFile);
        BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF-8"));
		Document doc;
		boolean inList=false;
		String url,year;
		while((url = bfr.readLine()) != null) {
		doc = Jsoup.connect(url).timeout(10000).get();
		year = url.substring(url.length() - 4);
		Elements links = doc.select("#mw-content-text > table.wikitable> tbody > tr > td:nth-child(1) > i > a");
		for(Element elem : links){
			writer.println(elem.text().replace("\"","")+"@"+year+"@"+elem.attr("href"));
		}
       Thread.sleep(500); //delay not to overload wiki
    } 
		bfr.close();
		writer.close();

	}
	
	
	private static void extractURL(String corpusFile, String output) throws IOException{
		File cfile= new File(corpusFile);
		PrintWriter writer = new PrintWriter(output, "UTF-8");
		String urlPrefix= "https://en.wikipedia.org";
        Document dochtml = Jsoup.parse(cfile, "UTF-8", "");
        Elements links = dochtml.select("li > a");
        for(Element elem : links){
        	        if (elem.text().startsWith("List of American films of 19") | elem.text().startsWith("List of American films of 20") ) {        	            
        	        	writer.println(urlPrefix+elem.attr("href"));
        	        }
                }
        writer.close();
	}
}