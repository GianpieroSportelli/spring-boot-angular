package eu.reply.it;

import org.json.JSONObject;

public class Element implements Comparable<Element> {
	String key;
	int begin;
	int end;
	
	public Element(String keyword,int begin,int end){
		key=keyword;
		this.begin=begin;
		this.end=end;
	}
	
	public int compareTo(Element e){
		return new Integer(begin).compareTo(e.begin);
	}
	
	public int compareToEnd(Element e){
		return new Integer(end).compareTo(e.end);
	}

	public String getKey() {
		return key;
	}
	
	public void setkey(String k){
		key=k;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}
	
	public String toString(){
		return "{key:"+key+", begin:"+begin+", end:"+end+"}";
	}
}
