package eu.reply.it;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Classe di configurazione, legge e fornisce le proprietà custom presenti in
 * apllication. properties
 * 
 * @author Gianpiero Sportelli (gi.sportelli@reply.it)
 *
 */
@Component
public class Configuration {
	// documentazione di spring per la configurazione esterna all'applicazione
	// https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html

	// General
	@Value("${app.wex.collection}")
	String collection;

	@Value("${app.wex.host}")
	String host;

	@Value("${app.wex.port}")
	String port;

	// Search

	@Value("${app.wex.search.enablehref}")
	String search_enablehref;

	@Value("${app.wex.search.output}")
	String search_output;

	@Value("${app.wex.search.pagesize}")
	String search_pagesize;

	// Analysis
	@Value("${app.wex.analysis.output}")
	String analysis_output;

	// Spell Correction
	@Value("${app.wex.spell.output}")
	String spell_output;

	// Type a head
	@Value("${app.wex.typeAhead.output}")
	String typeAhead_output;

	@Value("${app.wex.typeAhead.lang}")
	String typeAhead_lang;

	// Intention Preventivatore
	@Value("${app.wex.facet.preventivatore}")
	String preventivatore;

	@Value("${app.wex.facet.preventivatore.auto}")
	String auto;

	@Value("${app.wex.facet.targa}")
	String targa;

	@Value("${app.wex.facet.data}")
	String data;

	@Value("${app.wex.facet.giorno}")
	String giorno;

	@Value("${app.wex.facet.mese}")
	String mese;

	@Value("${app.wex.facet.anno}")
	String anno;

	@Value("${app.wex.facet.email}")
	String email;

	@Value("${app.wex.facet.piva}")
	String piva;

	@Value("${app.wex.preventivatore}")
	boolean active;

	@Value("${app.wex.facet.kmanno}")
	String kmanno;

	@Value("${app.wex.facet.km}")
	String km;

//	@Value("${app.wex.top}")
//	String top;
//
//	@Value("${app.wex.top.investire}")
//	String investire;
//
//	@Value("${app.wex.top.investimento}")
//	String investimento;
//
//	@Value("${app.wex.top.viaggio}")
//	String viaggio;
//
//	@Value("${app.wex.top.pensione}")
//	String pensione;

	@Value("${app.wex.collection.analysis}")
	String collection_analysis;

	@Value("${app.wex.mykey}")
	String mykey;

	@Value("${app.wex.mykey.format}")
	String mykey_format;
	
//	@Value("${app.wex.mykey.metadata}")
//	String mykey_metadata;

	public String getCollection() {
		return collection;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getSearch_EnableHref() {
		return search_enablehref;
	}

	public String getSearch_Output() {
		return search_output;
	}

	public int getSearch_PageSize() {
		return Integer.valueOf(search_pagesize);
	}

	public String getAnalysis_Output() {
		return analysis_output;
	}

	public String getSpell_Output() {
		return spell_output;
	}

	public String getTypeaHead_Output() {
		return typeAhead_output;
	}

	public String getTypeaHead_lang() {
		return typeAhead_lang;
	}

	public String getPreventivatore() {
		return preventivatore;
	}

	public String getPreventivatoreAuto() {
		return auto;
	}

	public String getTarga() {
		return targa;
	}

	public String getData() {
		return data;
	}

	public String getGiorno() {
		return giorno;
	}

	public String getMese() {
		return mese;
	}

	public String getAnno() {
		return anno;
	}

	public String getEmail() {
		return email;
	}

	public String getPiva() {
		return piva;
	}

	public boolean isActivePreventivatore() {
		return active;
	}

	/**
	 * @return the kmanno
	 */
	public String getKmanno() {
		return kmanno;
	}

	/**
	 * @return the km
	 */
	public String getKm() {
		return km;
	}

//	public String getTop() {
//		return top;
//	}
//
//	public String getInvestire() {
//		return investire;
//	}
//
//	public String getInvestimento() {
//		return investimento;
//	}
//
//	public String getViaggio() {
//		return viaggio;
//	}
//
//	public String getPensione() {
//		return pensione;
//	}


	public String getCollectionAnalysis() {
		return collection_analysis;
	}

	public List<String> getMyKeyWord() throws JSONException {
		JSONArray key = new JSONArray(mykey);
		
		List<String> array = new ArrayList<>();
		for (int i = 0; i < key.length(); i++) {
			array.add(key.getString(i));
		}
		return array;
	}

	public List<String> getMyKeyWordFormat() throws JSONException {
		JSONArray key = new JSONArray(mykey_format);
		List<String> array = new ArrayList<>();
		for (int i = 0; i < key.length(); i++) {
			JSONArray path = key.getJSONArray(i);
			String value = "";
			for (int j = 0; j < path.length(); j++) {
				value += "\"" + path.getString(j) + "\"/";
			}
			array.add(value);
		}
		return array;
	}
	
//	public List<Boolean> isMetadata() throws JSONException{
//		List<Boolean> result=new ArrayList<>();
//		JSONArray key = new JSONArray(mykey_metadata);
//		for (int i = 0; i < key.length(); i++) {
//			int value=key.getInt(i);
//			boolean flag = value==1;
//			result.add(flag);
//		}
//		return result;
//	}

}
