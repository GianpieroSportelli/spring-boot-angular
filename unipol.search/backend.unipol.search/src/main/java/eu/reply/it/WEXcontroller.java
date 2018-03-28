package eu.reply.it;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * RESTful controller per WEX Content Analytics v.11 gestione della Enterprise
 * Search
 * 
 * @author Gianpiero Sportelli (gi.sportelli@reply.it)
 * 
 * 
 *
 */
@RestController
public class WEXcontroller {

	// Configurazione dell'applicazione
	private final Configuration appConfiguration;

	// Logger dell'applicazione
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static TintParser parser;
	private static List<String> posOK;
	private static final int max_keyword = 5;

	/**
	 * Costruttore del controller
	 * 
	 * @param appConfig
	 *            cnfigurazione del controllore, letta
	 *            dall'application.properties associato
	 * @throws JSONException
	 */
	@Autowired
	public WEXcontroller(Configuration appConfig) throws JSONException {
		appConfiguration = appConfig;
		posOK = new ArrayList<>();
		posOK.add("n");
		posOK.add("adj");
		try {
			parser = new TintParser();
		} catch (IOException e) {
			parser = null;
			logger.error("errore init parser: \n" + e);
		}
	}

	/**
	 * Metodo che fornisce l'url per la ricerca di ICA v.11 costruito in base
	 * alle proprietà definite in application.properties
	 * 
	 * @param input
	 *            Frase di input che corrisponde alle query di ricerca
	 * @param page
	 *            Pagina della ricerca
	 * @return url ricerca
	 */
	private String getSearchEP(String input, int page) {
		String col = appConfiguration.getCollection();
		String host = appConfiguration.getHost();
		String port = appConfiguration.getPort();

		String enableHref = appConfiguration.getSearch_EnableHref();
		String output = appConfiguration.getSearch_Output();
		int pageSize = appConfiguration.getSearch_PageSize();
		String lang = appConfiguration.getTypeaHead_lang();

		String ep = "http://" + host + ":" + port + "/api/v10/search?collection=" + col + "&enableHref=" + enableHref
				+ "&output=" + output + "&pageSize=" + pageSize + "&query=" + input + "&page=" + page + "&queryLang="
				+ lang;
		return ep;
	}

	/**
	 * Metodo che fornisce l'url per la ricerca di ICA v.11 costruito in base
	 * alle proprietà definite in application.properties
	 * 
	 * @param input
	 *            Frase di input che corrisponde alle query di ricerca
	 * @param page
	 *            Pagina della ricerca
	 * @return url ricerca
	 */
	private String getSearchEP(String input, JSONObject facet, int page) {
		String col = appConfiguration.getCollection();
		String host = appConfiguration.getHost();
		String port = appConfiguration.getPort();

		String enableHref = appConfiguration.getSearch_EnableHref();
		String output = appConfiguration.getSearch_Output();
		int pageSize = appConfiguration.getSearch_PageSize();

		String ep = "http://" + host + ":" + port + "/api/v10/search?collection=" + col + "&enableHref=" + enableHref
				+ "&output=" + output + "&pageSize=" + pageSize + "&query=" + input + "&facet=" + facet.toString()
				+ "&page=" + page;
		return ep;
	}

	/**
	 * Metodo che fornisce l'url per la ricerca di ICA v.11 costruito in base
	 * alle proprietà definite in application.properties
	 * 
	 * @param input
	 *            Frase di input che corrisponde alle query di ricerca
	 * @param page
	 *            Pagina della ricerca
	 * @return url ricerca
	 */
	// private String getSearchDocEP(String input, int page) {
	// String col = appConfiguration.getDoc_collection();
	// String host = appConfiguration.getHost();
	// String port = appConfiguration.getPort();
	//
	// String enableHref = appConfiguration.getSearch_EnableHref();
	// String output = appConfiguration.getSearch_Output();
	// int pageSize = appConfiguration.getSearch_PageSize();
	//
	// String ep = "http://" + host + ":" + port + "/api/v10/search?collection="
	// + col + "&enableHref=" + enableHref
	// + "&output=" + output + "&pageSize=" + pageSize + "&query=" + input +
	// "&page=" + page;
	// return ep;
	// }

	/**
	 * Metodo che fornisce l'url per l'analisi testuale di ICA v.11 costruito in
	 * base alle proprietà definite in application.properties
	 * 
	 * @param input
	 *            Frase di input da analizzare
	 * @return url analisi
	 */
	private String getAnalysisEP(String input) {
		String col = appConfiguration.getCollectionAnalysis();
		String host = appConfiguration.getHost();
		String port = appConfiguration.getPort();

		String output = appConfiguration.getAnalysis_Output();

		String ep = "http://" + host + ":" + port + "/api/v10/analysis/text?collection=" + col + "&output=" + output
				+ "&text=" + input;
		return ep;
	}

	/**
	 * Metodo che fornisce l'url per la spell correction di ICA v.11 costruito
	 * in base alle proprietà definite in application.properties
	 * 
	 * @param input
	 *            Frase di input su cui effettuare la spell correction
	 * @return url spell correction
	 */
	private String getSpellEP(String input) {
		String col = appConfiguration.getCollection();
		String host = appConfiguration.getHost();
		String port = appConfiguration.getPort();

		String output = appConfiguration.getSpell_Output();

		String ep = "http://" + host + ":" + port + "/api/v10/query/spell?collection=" + col + "&output=" + output
				+ "&query=" + input;
		return ep;
	}

	/**
	 * Metodo che fornisce l'url del type a head (suggerimento query) di ICA
	 * v.11 costruito in base alle proprietà definite in application.properties
	 * 
	 * @param input
	 *            Frase di input che corrisponde alle query di ricerca
	 * @return url type a head query
	 */
	private String getATypeaHeaedEP(String input) {
		String col = appConfiguration.getCollection();
		String host = appConfiguration.getHost();
		String port = appConfiguration.getPort();

		String output = appConfiguration.getTypeaHead_Output();
		String lang = appConfiguration.getTypeaHead_lang();

		String ep = "http://" + host + ":" + port + "/api/v10/query/typeahead?collection=" + col + "&output=" + output
				+ "&queryLang=" + lang + "&prefix=" + input;
		return ep;
	}

	/**
	 * Metodo per testare il logger di Spring boot
	 * 
	 * @return Stringa di test
	 */
	@CrossOrigin
	@RequestMapping("/logger")
	String index() {
		logger.debug("This is a debug message");
		logger.info("This is an info message");
		logger.warn("This is a warn message");
		logger.error("This is an error message");
		return "logger help in console";
	}

	@CrossOrigin
	@RequestMapping("/configuration-test")
	String configurationTest() {
		String result = "";
		result += "collection:" + appConfiguration.getCollection() + "\n";
		result += "host:" + appConfiguration.getHost() + "\n";
		result += "port:" + appConfiguration.getPort() + "\n";
		result += "search.enablehref:" + appConfiguration.getSearch_EnableHref() + "\n";
		result += "search.output:" + appConfiguration.getSearch_Output() + "\n";
		result += "search.pagesize:" + appConfiguration.getSearch_PageSize() + "\n";
		result += "analysis.output:" + appConfiguration.getAnalysis_Output() + "\n";
		result += "spell.output:" + appConfiguration.getSpell_Output() + "\n";
		result += "typeAhead.output:" + appConfiguration.getTypeaHead_Output() + "\n";
		result += "typeAhead.lang:" + appConfiguration.getTypeaHead_lang() + "\n";
		result += "preventivatore:" + appConfiguration.isActivePreventivatore() + "\n";
		result += "facet.preventivatore:" + appConfiguration.getPreventivatore() + "\n";
		result += "facet.preventivatore.auto:" + appConfiguration.getPreventivatoreAuto() + "\n";
		result += "facet.targa:" + appConfiguration.getTarga() + "\n";
		result += "facet.data:" + appConfiguration.getData() + "\n";
		result += "facet.giorno:" + appConfiguration.getGiorno() + "\n";
		result += "facet.mese:" + appConfiguration.getMese() + "\n";
		result += "facet.anno:" + appConfiguration.getAnno() + "\n";
		result += "facet.email:" + appConfiguration.getEmail() + "\n";
		result += "facet.piva:" + appConfiguration.getPiva() + "\n";
		result += "facet.kmanno:" + appConfiguration.getKmanno() + "\n";
		result += "facet.km:" + appConfiguration.getKm() + "\n";
		String mykey="<error my keyword>";
		try {
			List<String> listMyKey=appConfiguration.getMyKeyWord();
			mykey="[";
			for(String key:listMyKey){
				mykey+=key+",";
			}
			mykey+="]";
		} catch (JSONException e) {
			e.printStackTrace();
		}
		result += "app.wex.mykey:" + mykey + "\n";
		String mykeyf="<error my keywordFormat>";
		try {
			List<String> listMyKeyF=appConfiguration.getMyKeyWordFormat();
			mykeyf="[";
			for(String key:listMyKeyF){
				mykeyf+=key+",";
			}
			mykeyf+="]";
		} catch (JSONException e) {
			e.printStackTrace();
		}
		result += "app.wex.mykey.format:" + mykeyf + "\n";
		return "<pre>" + result + "</pre>";
	}

	/**
	 * Endpoint get per testare il servizio di Sprng boot
	 * 
	 * @param name
	 *            stringa utilizzata nel test
	 * @return Json object contente la stringa inserita
	 * @throws JSONException
	 */
	@CrossOrigin
	@GetMapping("/wex-test")
	public String test(@RequestParam(required = false, defaultValue = "World") String name) throws JSONException {
		logger.info("==== in test ====");
		logger.info("name: " + name);
		return new JSONObject().accumulate("value", name).toString();
	}

	/**
	 * Endpoint post per testare il servizio di Sprng boot
	 * 
	 * @param name
	 *            stringa utilizzata nel test
	 * @return Json object contenente la stringa di test
	 * @throws JSONException
	 */
	@CrossOrigin
	@PostMapping("/wex-test-api")
	public String testPOST(@RequestParam(required = false, defaultValue = "World") String name) throws JSONException {
		logger.info("==== in test API ====");
		logger.info("name: " + name);
		return new JSONObject().accumulate("value", name).toString();
	}

	/**
	 * Endpoint get per validare il risultato della suggestion
	 * 
	 * @param input
	 *            String su cui richiedere la suggestion
	 * @return Json object contente la suggestion in base al input
	 * @throws JSONException
	 */
	@CrossOrigin
	@GetMapping("/suggestion")
	public @ResponseBody String getSuggestion(@RequestParam(required = true) String input) throws JSONException {
		String result = "";
		try {
			result = "<pre>" + callSuggestionAPI(input).toString().replaceAll("#", "") + "</pre>";
		} catch (IOException e) {
			logger.error("Errore in call Suggestion!!!");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * End point post per la suggestion
	 * 
	 * @param input
	 *            frase di input su cui si richiede la suggestion
	 * @return Json object contentente la suggestion
	 * @throws JSONException
	 */
	@CrossOrigin
	@PostMapping(value = "/suggestionAPI", produces = "application/json")
	public @ResponseBody String getSuggestionJSON(@RequestParam(required = true) String input) throws JSONException {
		String result = "";
		try {
			JSONObject res = callSuggestionAPI(input);
			result = res.toString().replaceAll("#", "");
		} catch (IOException e) {
			logger.error("Errore in call Suggestion API!!!");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Metodo che richiama i servizi rest per la suggestion di ICA v.11 Utilizza
	 * il metodo callTypeAhead per costruire le suggestion
	 * 
	 * @param input
	 *            frase di inupt su cui si richiede la suggestion
	 * @return Json object contenente le suggestion per la frase di input
	 * @throws IOException
	 *             Si verifica un eccezione quando si verificano problemi nella
	 *             comunicazione con il server di ricerca di ICA v.11
	 * @throws JSONException
	 */

	private JSONObject callSuggestionAPI(String input) throws IOException, JSONException {
		// provo con il servizio TypeAhead
		// input = input.toLowerCase();
		logger.info("==== Suggestion Service ====");
		logger.info("INPUT: " + input);

		JSONObject result = callTypeHead(input);

		if (result.isNull("es_apiResponse")) {
			logger.info("No Type a Head result!!");
			// se non ottengo risultati provo a cercare suggestion per l'ultimo
			// token
			String newInput = "";
			// splitto per lo spazio
			String[] array_input = input.split(" ");

			if (array_input.length > 1) {
				// se ci sono token identifico l'ultimo
				String last_token = array_input[array_input.length - 1];

				// costruisco il prefisso
				for (int i = 0; i < array_input.length - 1; i++) {
					newInput += array_input[i] + " ";
				}

				logger.info("PREFIX: " + newInput);
				logger.info("LAST token: " + last_token);

				JSONObject type = callSuggestionAPI(last_token);

				if (!type.isNull("es_apiResponse")) {
					String typeAhead_str = type.getJSONObject("es_apiResponse").get("es_queryTypeAhead").toString();
					if (typeAhead_str.charAt(0) == '{') {
						typeAhead_str = '[' + typeAhead_str + ']';
					}

					JSONArray typeAhead = new JSONArray(typeAhead_str);
					List<String> suggestion_ext = new ArrayList<String>();

					for (int i = 0; i < typeAhead.length(); i++) {
						JSONObject es_suggestion = typeAhead.getJSONObject(i);

						String suggestion_str = es_suggestion.get("es_suggestion").toString();

						if (suggestion_str.charAt(0) == '{') {
							suggestion_str = '[' + suggestion_str + ']';
						}

						JSONArray suggestion = new JSONArray(suggestion_str);

						for (int j = 0; j < suggestion.length(); j++) {
							String text = suggestion.getJSONObject(j).getString("#text");// .toLowerCase();
							if (!suggestion_ext.contains(text)) {
								suggestion_ext.add(text);
							}
						}
					}
					JSONObject es_queryTypeAhead = new JSONObject();
					JSONObject es_suggestion = new JSONObject();
					es_suggestion.accumulate("type", "Craft");
					for (String token : suggestion_ext) {
						String typeahead = newInput + token;
						JSONObject text = new JSONObject();
						text.accumulate("#text", typeahead);
						es_suggestion.accumulate("es_suggestion", text);
					}
					es_queryTypeAhead.accumulate("es_queryTypeAhead", es_suggestion);
					result.put("es_apiResponse", es_queryTypeAhead);
				}
			}

		} else {
			logger.info("Suggestion Service output: " + result + " for input: " + input);

		}
		return result;
	}

	/**
	 * Endpoint ricerca del servizio restful di ricerca
	 * 
	 * @param input
	 *            frase di input che corrisponde alla query
	 * @param page
	 *            pagina della ricerca di interesse
	 * @return Json object contente la pagina della ricerca richiesta
	 * @throws JSONException
	 */
	@CrossOrigin
	@PostMapping(value = "/searchAPI", produces = "application/json")
	public @ResponseBody String getSearchJSON(@RequestParam(required = true) String input,
			@RequestParam(required = false, defaultValue = "1") int page) throws JSONException {
		String result = "";
		try {
			JSONObject res = callSearchAPI(input, page);
			result = res.toString();
		} catch (IOException e) {
			logger.error("Errore in call Search API!!!");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Endpoint get per testare la ricerca
	 * 
	 * @param input
	 *            frase di input che corrisponde alle query
	 * @param page
	 *            numero della pagina richiesta
	 * @return Json Object della pagina di risultato della ricerca
	 */
	@CrossOrigin
	@GetMapping("/search")
	public @ResponseBody String getSearchResult(@RequestParam(required = true) String input,
			@RequestParam(required = false, defaultValue = "1") int page) {
		String result = "";
		try {
			result = "<pre>" + callSearchAPI(input, page).toString(4) + "</pre>";
		} catch (JSONException | IOException e) {
			logger.error("Errore in call Search!!!");
			e.printStackTrace();
		}
		return result;
	}

//	@CrossOrigin
//	@GetMapping("/search-test")
//	public @ResponseBody String getSearchTestResult(@RequestParam(required = true) String query,
//			@RequestParam(required = true) String facet, @RequestParam(required = false, defaultValue = "1") int page) {
//		String result = "";
//		try {
//			String url = "";
//			// + "&linguistic=exact"
//			if (facet.equals("null")) {
//				url = getSearchEP(query, page);
//				url = url.replaceAll(" ", "%20");
//			} else {
//				JSONObject facetJSON = new JSONObject();
//				facetJSON.accumulate("id", facet.replaceAll("<>", "|").replaceAll(" ", "%20"));
//				url = getSearchEP(query, facetJSON, page);
//				url = url.replaceAll(" ", "%20");
//			}
//			URL search = new URL(url);
//			HttpURLConnection connection = (HttpURLConnection) search.openConnection();
//			connection.setRequestMethod("POST");
//			BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream()), "UTF-8"));
//			String serverOutput = "";
//			String line = null;
//			while ((line = br.readLine()) != null) {
//				serverOutput += line;
//			}
//			logger.info("CALL " + search);
//			logger.debug("Server Output Search API: " + serverOutput);
//
//			result = new JSONObject(serverOutput).toString(4);
//			result = "<pre>" + result + "</pre>";
//		} catch (JSONException | IOException e) {
//			logger.error("Errore in call Search!!!");
//			e.printStackTrace();
//		}
//		return result;
//	}

	/**
	 * Metodo che si occupa di chiamare l'end point dela servzio di ricerca
	 * offerto da ICA v.11
	 * 
	 * @param input
	 *            frase di input che corrisponde alla query di ricerca
	 * @param page
	 *            numero della pagina dei risultati della ricerca
	 * @return Json Object contenente la pagina del risultato della ricerca
	 * @throws IOException
	 *             si verifica un'eccezione quando ci sono problemi nella
	 *             comunicazione con ICA v.11
	 * @throws JSONException
	 */
	private JSONObject callSearchAPI(String input, int page) throws IOException, JSONException {
		JSONObject result = null;
		logger.info("== SEARCH Start ==");
		JSONObject analysis = deep_analysis(input);
		if (analysis.getString("it_type").equals("prev")) {
			result = analysis;
		} else {
			JSONObject es_query = analysis.getJSONObject("es_query");
			String query = es_query.getString("query");
			String url = null;
			URL search = null;
			if (es_query.has("facet")) {
				url = getSearchEP(query, es_query.getJSONObject("facet"), page);
			} else {
				url = getSearchEP(query, page);
			}
			url = url.replaceAll(" ", "%20");
			search = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) search.openConnection();
			connection.setRequestMethod("POST");
			logger.info("CALL " + search);
			BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream()), "UTF-8"));
			String serverOutput = "";
			String line = null;
			while ((line = br.readLine()) != null) {
				serverOutput += line;
			}

			logger.debug("Server Output Search API: " + serverOutput);

			result = new JSONObject(serverOutput);

			if (result.has("es_apiResponse")) {
				JSONObject api_response = result.getJSONObject("es_apiResponse");
				if (api_response.has("es_result")) {
					String api_result_str = api_response.get("es_result").toString();
					if (api_result_str.charAt(0) == '{') {
						api_result_str = '[' + api_result_str + ']';
					}
					JSONArray api_result = new JSONArray(api_result_str);
					api_response.remove("es_result");

					for (int i = 0; i < api_result.length(); i++) {
						JSONObject element = api_result.getJSONObject(i);
						String ibmsc_field_str = element.get("ibmsc_field").toString();

						if (ibmsc_field_str.charAt(0) == '{') {
							ibmsc_field_str = '[' + ibmsc_field_str + ']';
						}

						JSONArray ibmsc_field = new JSONArray(ibmsc_field_str);

						for (int j = 0; j < ibmsc_field.length(); j++) {
							JSONObject field = ibmsc_field.getJSONObject(j);
							if (field.getString("id").equals("filepath")) {
								String link = field.getString("#text");
								JSONObject obj_link = new JSONObject();
								obj_link.accumulate("href", link);
								JSONArray es_link = element.getJSONArray("es_link");
								es_link.put(0, obj_link);
								break;
							}
						}

						api_response.accumulate("es_result", element);
					}

				}
			}
			result.put("it_type", "search");
			result.put("it_pagesize", appConfiguration.getSearch_PageSize());
		}
		// logger.info("\n" + result.toString(4));
		logger.info("== SEARCH Stop ==");
		return result;
	}

	// public JSONObject struct_analysis(String input) throws IOException,
	// JSONException {
	// JSONObject result = null;
	// logger.info("==== STRUCT ANALYSIS ====");
	// JSONObject analysis = callAnalysis(input);
	// logger.debug("\n" + analysis.toString(4));
	// Map<String, List<Element>> struct = new HashMap<>();
	// if (analysis.has("metadata") &&
	// analysis.getJSONObject("metadata").has("textfacets")) {
	// logger.info("--> has textfacets");
	// String textfactes_str =
	// analysis.getJSONObject("metadata").get("textfacets").toString();
	// if (textfactes_str.charAt(0) != '[') {
	// textfactes_str = '[' + textfactes_str + ']';
	// }
	// JSONArray textfacets = new JSONArray(textfactes_str);
	// for (int i = 0; i < textfacets.length(); i++) {
	// JSONObject facet = textfacets.getJSONObject(i);
	// String key = "";
	// JSONArray path = facet.getJSONArray("path");
	// for (int k = 0; k < path.length(); k++) {
	// key += path.getString(k);
	// if (k + 1 != path.length()) {
	// key += ".";
	// }
	// }
	// if (struct.containsKey(key)) {
	// List<Element> list = struct.get(key);
	// list.add(new Element(facet.getString("keyword"), facet.getInt("begin"),
	// facet.getInt("end")));
	// Collections.sort(list);
	// } else {
	// List<Element> list = new ArrayList<>();
	// list.add(new Element(facet.getString("keyword"), facet.getInt("begin"),
	// facet.getInt("end")));
	// Collections.sort(list);
	// struct.put(key, list);
	// }
	// }
	// result = new JSONObject(struct);
	// } else {
	// result = new JSONObject();
	// }
	// logger.info("==== END STRUCT ANALYSIS ====");
	// return result;
	// }

	public JSONObject deep_analysis(String input) throws IOException, JSONException {
		JSONObject result = null;
		logger.info("==== DEEP ANALYSIS ====");
		JSONObject analysis = callAnalysis(input);
		logger.debug("\n" + analysis.toString(4));
		Map<String, List<Element>> struct = new HashMap<>();
		if (analysis.has("metadata") && analysis.getJSONObject("metadata").has("textfacets")) {
			logger.info("--> has textfacets");
			String textfactes_str = analysis.getJSONObject("metadata").get("textfacets").toString();
			if (textfactes_str.charAt(0) != '[') {
				textfactes_str = '[' + textfactes_str + ']';
			}
			JSONArray textfacets = new JSONArray(textfactes_str);
			logger.info("WATSON ANALYSIS: \n" + textfacets.toString(4));
			for (int i = 0; i < textfacets.length(); i++) {
				JSONObject facet = textfacets.getJSONObject(i);
				String key = "";
				JSONArray path = facet.getJSONArray("path");
				for (int k = 0; k < path.length(); k++) {
					key += path.getString(k);
					if (k + 1 != path.length()) {
						key += ".";
					}
				}
				if (struct.containsKey(key)) {
					List<Element> list = struct.get(key);
					list.add(new Element(facet.getString("keyword"), facet.getInt("begin"), facet.getInt("end")));
					Collections.sort(list);
				} else {
					List<Element> list = new ArrayList<>();
					list.add(new Element(facet.getString("keyword"), facet.getInt("begin"), facet.getInt("end")));
					Collections.sort(list);
					struct.put(key, list);
				}
			}

			if (struct.containsKey(appConfiguration.getData())) {
				List<Element> dates = struct.get(appConfiguration.getData());
				List<Element> giorni = struct.get(appConfiguration.getGiorno());
				List<Element> mesi = struct.get(appConfiguration.getMese());
				List<Element> anni = struct.get(appConfiguration.getAnno());
				for (Element data : dates) {
					int[] begin = new int[3];
					int[] end = new int[3];
					String g = "";
					String m = "";
					String a = "";
					for (Element giorno : giorni) {
						if (giorno.getBegin() >= data.getBegin() && giorno.getEnd() <= data.getEnd()) {
							if (begin[0] > giorno.getBegin()) {
								begin[0] = giorno.getBegin();
								end[0] = giorno.getEnd();
								g = giorno.getKey();
							} else if (g.equals("")) {
								begin[0] = giorno.getBegin();
								end[0] = giorno.getEnd();
								g = giorno.getKey();
							}
						}
					}

					for (Element mese : mesi) {
						if (mese.getBegin() >= data.getBegin() && mese.getEnd() <= data.getEnd()) {
							if (begin[1] > mese.getBegin() && end[0] < mese.getBegin()) {
								begin[1] = mese.getBegin();
								end[1] = mese.getEnd();
								m = mese.getKey();
							} else if (m.equals("")) {
								begin[1] = mese.getBegin();
								end[1] = mese.getEnd();
								m = mese.getKey();
							}
						}
					}

					for (Element anno : anni) {
						if (anno.getBegin() >= data.getBegin() && anno.getEnd() <= data.getEnd()) {
							if (begin[2] > anno.getBegin() && end[1] < anno.getBegin()) {
								begin[2] = anno.getBegin();
								end[2] = anno.getEnd();
								a = anno.getKey();
							} else if (a.equals("")) {
								begin[2] = anno.getBegin();
								end[2] = anno.getEnd();
								a = anno.getKey();
							}
						}
					}

					data.setkey(g + "/" + m + "/" + a);
				}
				struct.remove(appConfiguration.getGiorno());
				struct.remove(appConfiguration.getMese());
				struct.remove(appConfiguration.getAnno());
			} else {
				struct.remove(appConfiguration.getGiorno());
				struct.remove(appConfiguration.getMese());
				struct.remove(appConfiguration.getAnno());
			}

			if (struct.containsKey(appConfiguration.getKmanno())) {
				List<Element> kmannos = struct.get(appConfiguration.getKmanno());
				List<Element> kms = struct.get(appConfiguration.getKm());
				for (Element kmanno : kmannos) {
					int begin = 0;
					String k = "";

					for (Element km : kms) {
						if (km.getBegin() >= kmanno.getBegin() && km.getEnd() <= kmanno.getEnd()) {
							if (begin > km.getBegin()) {
								k = km.getKey();
								begin = km.getBegin();
							} else if (k.equals("")) {
								k = km.getKey();
								begin = km.getBegin();
							}
						}
					}

					kmanno.setkey(k);
				}

				struct.remove(appConfiguration.getKm());
			} else {
				struct.remove(appConfiguration.getKm());
			}

			if (struct.containsKey(appConfiguration.getPreventivatore())) {
				result = new JSONObject();
				result.put("it_type", "prev");
				List<Element> list = struct.get(appConfiguration.getPreventivatore());
				if (list.get(0).getKey().equals(appConfiguration.getPreventivatoreAuto())) {
					JSONObject es_apiResponse = new JSONObject();
					es_apiResponse.accumulate("type", "auto");

					if (struct.containsKey(appConfiguration.getKmanno())) {
						es_apiResponse.accumulate("kmanno", struct.get(appConfiguration.getKmanno()).get(0).getKey());
					}

					if (struct.containsKey(appConfiguration.getEmail())) {
						es_apiResponse.accumulate("email", struct.get(appConfiguration.getEmail()).get(0).getKey());
					}

					if (struct.containsKey(appConfiguration.getData())) {
						es_apiResponse.accumulate("date", struct.get(appConfiguration.getData()).get(0).getKey());
					}

					if (struct.containsKey(appConfiguration.getTarga())) {
						es_apiResponse.accumulate("targa",
								struct.get(appConfiguration.getTarga()).get(0).getKey().toUpperCase());
					}

					if (struct.containsKey(appConfiguration.getPiva())) {
						es_apiResponse.accumulate("piva", struct.get(appConfiguration.getPiva()).get(0).getKey());
					}
					result.accumulate("es_apiResponse", es_apiResponse);
				}
			} else {
				result = new JSONObject();
				result.put("it_type", "search");
				JSONObject query = cunstructSemanticQuery(struct, input);
				result.accumulate("es_query", query);
			}

		} else {
			result = new JSONObject();
		}
		logger.info("==== END DEEP ANALYSIS ====");
		return result;
	}

	private JSONObject cunstructSemanticQuery(Map<String, List<Element>> struct, String input) {
		JSONObject result = new JSONObject();
		String query = input.replaceAll("[_{},.\"£$%&/(!)-=\\*°#§-]", " ").replaceAll("\\?", " ");
		List<Integer> begin = new ArrayList<>();
		List<Integer> end = new ArrayList<>();
		printStructure(struct);
		String facet_query = null;
		List<Element> concept = new ArrayList<>();

		try {
			List<String> mykeyword = appConfiguration.getMyKeyWord();
			List<String> format = appConfiguration.getMyKeyWordFormat();
			for (int i = 0; i < mykeyword.size(); i++) {
				String key = mykeyword.get(i);
				if (struct.containsKey(key)) {
					List<Element> elements = struct.get(key);
					for (Element e : elements) {
						if (!begin.contains(e.begin) && !end.contains(e.end)) {
							if (facet_query == null) {
								facet_query = "keyword::/" + format.get(i) + "\"" + e.getKey() + "\"";
							} else {
								facet_query = "(" + facet_query + ") AND keyword::/" + format.get(i) + "\"" + e.getKey()
										+ "\"";
							}
							concept.add(e);
//per la nuova versione Taxonomy
							begin.add(e.begin);
							end.add(e.end);
//provare ad eliminare questo!!
						}
					}

				}
			}

			query = lemFilter(query, posOK, concept, max_keyword);
			if (facet_query != null) {
				query = query + " AND " + facet_query;
			}
			result.accumulate("query", query);

		} catch (JSONException e) {
			logger.error(
					"app.wex.mykey formattato male, formattare il parametro come un JSONArray contenente stringhe!!!");
			e.printStackTrace();
		}

		return result;
	}

	private String lemFilter(String query, List<String> posOK, List<Element> concept, int max_keyword) {
		String result = query;
		String newQuery = null;
		int n_element = 0;
		int max = 0;
		Map<String, Integer> foud = new HashMap<>();

		if (parser != null) {
			try {
				JSONArray lemming = parser.lexicalAnalysis(query);
				for (int i = 0; i < lemming.length(); i++) {
					JSONObject sent = lemming.getJSONObject(i);
					JSONArray tokens = sent.getJSONArray("tokens");
					logger.info(tokens.toString(4));
					for (int j = 0; j < tokens.length(); j++) {
						JSONObject token = tokens.getJSONObject(j);
						String word = token.getString("word");
						int begin = token.getInt("characterOffsetBegin");
						int end = token.getInt("characterOffsetEnd");
						String concept_term = null;
						boolean concept_find = false;

						for (Element e : concept) {
							if (e.getBegin() == begin &&  end == e.getEnd()) {
								concept_term = e.getKey();
								if (!word.equals(concept_term)) {
									concept_find=true;
									word =/*"("+*/  word + " OR " + concept_term /*+")"*/;
								}
								//break;
							}
						}
						
						if(concept_find){
							word="("+word+")";
						}
						
						boolean add = false;
						if (token.has("full_morpho")) {
							String morph = token.getString("full_morpho");
							String[] split = morph.split(" ");
							for (int k = 1; k < split.length; k++) {
								String[] morph_pos = split[k].split("[+]");
								if (morph_pos.length > 1) {
									String pos = morph_pos[1];
									if (posOK.contains(pos)) {
										if (concept_find) {
											foud.put(word, 0);
										} else {
											foud.put(word, k);
											if (max < k) {
												max = k;
											}
										}
										add = true;
										n_element++;
										break;
									}
								}
							}
						}

						if (!add && concept_term != null) {
							// if (newQuery == null) {
							// newQuery = concept_term;
							// } else {
							// newQuery += " AND " + concept_term;
							// }
							n_element++;
							foud.put(word, 0);
						}
					}
				}

				while (n_element > max_keyword) {
					System.out.println("troppi elementi");
					System.out.println("max: " + max);
					int new_max = 0;
					boolean one_removed = false;
					String rem = null;
					for (String key : foud.keySet()) {
						if (foud.get(key) == max && !one_removed) {
							rem = key;
							one_removed = true;
						} else {
							if (new_max < foud.get(key)) {
								new_max = foud.get(key);
							}
						}
					}
					max = new_max;
					System.out.println("new max: " + max);
					if (!one_removed) {
						break;
					} else {
						System.out.println("remove :" + rem);
						foud.remove(rem);
						n_element--;
					}
				}

				for (String key : foud.keySet()) {
					if (newQuery == null) {
						newQuery = key;
					} else {
						newQuery += " AND " + key;
					}
				}

			} catch (IOException e) {
				logger.error("errore nell'inizializzazione Tint parser!!!");
				e.printStackTrace();
			} catch (JSONException e) {
				logger.error("errore nella creazione JSONArray risultato analisi Tint Parser!!!");
				e.printStackTrace();
			}
		}
		if (newQuery != null) {
			result = newQuery;
		}
		logger.info("query after lemming filter: " + result);
		return result;
	}

	private void printStructure(Map<String, List<Element>> struct) {
		logger.info("---->ANALYSIS STRUCTURE");
		for (String key : struct.keySet()) {
			List<Element> list = struct.get(key);
			logger.info(key);
			for (Element e : list) {
				logger.info("   " + e);
			}
		}
	}

	@CrossOrigin
	@GetMapping("/deep-analysis")
	public @ResponseBody String getDeepAnalysisResult(@RequestParam(required = true) String input)
			throws JSONException, IOException {
		return "<pre>" + deep_analysis(input).toString(4) + "</pre>";
	}

	// @CrossOrigin
	// @GetMapping("/struct-analysis")
	// public @ResponseBody String
	// getStrictAnalysisResult(@RequestParam(required = true) String input)
	// throws JSONException, IOException {
	// return "<pre>" + struct_analysis(input).toString(4) + "</pre>";
	// }

	/**
	 * Enpoint get per testare l'analisi di una frase
	 * 
	 * @param input
	 *            frase di input da analizzare
	 * @return Json object risultato dell'analisi
	 */
	@CrossOrigin
	@GetMapping("/analysis")
	public @ResponseBody String getAnalysisResult(@RequestParam(required = true) String input) {
		String result = "";
		try {
			result = "<pre>" + callAnalysis(input).toString(4) + "</pre>";
		} catch (JSONException | IOException e) {
			logger.error("Errore in call Analysis!!!");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Metodo che si occupa di chiamare il servizio di ICA v.11 per l'analisi
	 * 
	 * @param input
	 *            frase d input da analizzare
	 * @return Json object risultato dell'analisi
	 * @throws IOException
	 *             si verifica un eccezione quando ci sono problemi nel
	 *             comunicare con il servizio di analisi di ICA v.11
	 * @throws JSONException
	 */
	private JSONObject callAnalysis(String input) throws IOException, JSONException {
		// input = input.toLowerCase();
		logger.info("==== Analysis Service ====");
		JSONObject result = null;

		input = input.replaceAll(" ", "+");
		URL analysis = new URL(getAnalysisEP(input));
		HttpURLConnection connection = (HttpURLConnection) analysis.openConnection();
		connection.setRequestMethod("POST");
		BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream()), "UTF-8"));
		String serverOutput = "";
		String line = null;
		while ((line = br.readLine()) != null) {
			serverOutput += line;
		}
		logger.info("CALL " + analysis);
		logger.debug("Server Output Analysis: " + serverOutput);
		result = new JSONObject(serverOutput);
		return result;
	}

	/**
	 * End point get utilizzato per testare il servizio di spell checking
	 * 
	 * @param input
	 *            frase su cui effettuare lo spell checking
	 * @return Json object contenente il risultato dello spell checking
	 */
	@CrossOrigin
	@GetMapping("/spell")
	public @ResponseBody String getSpellCorrectionResult(@RequestParam(required = true) String input) {
		String result = "";
		try {
			result = "<pre>" + callSpellCorrection(input).toString(4) + "</pre>";
		} catch (JSONException | IOException e) {
			logger.error("Errore in call Spell Correction!!!");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Metodo che si occupa di comunicare con il servizio di spell checking di
	 * ICA v.11
	 * 
	 * @param input
	 *            frase d input su cui effettuare lo spell checking
	 * @return Json object risultato de servizio di spell checking
	 * @throws IOException
	 *             si verifica un eccezione quando ci sono problemi di
	 *             comunicazione con il server ICA v11
	 * @throws JSONException
	 */
	private JSONObject callSpellCorrection(String input) throws IOException, JSONException {
		// input = input.toLowerCase();
		logger.info("==== Spell Correction Service ====");
		JSONObject result = null;

		input = input.replaceAll(" ", "+");
		URL spell_correction = new URL(getSpellEP(input));
		HttpURLConnection connection = (HttpURLConnection) spell_correction.openConnection();
		connection.setRequestMethod("POST");
		BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream()), "UTF-8"));
		String serverOutput = "";
		String line = null;
		while ((line = br.readLine()) != null) {
			serverOutput += line;
		}
		logger.info("CALL " + spell_correction);
		logger.debug("Server Output Spell correction: " + serverOutput);

		result = new JSONObject(serverOutput);
		return result;
	}

	/**
	 * Endpoint get utilizzato per testare il servizio di suggerimento Type a
	 * head
	 * 
	 * @param input
	 *            frase di input su cui vogliamo ottenere suggerimenti
	 * @return Json object contente i suggerimenti
	 */
	@CrossOrigin
	@GetMapping("/typeahead")
	public @ResponseBody String getTypeHeadResult(@RequestParam(required = true) String input) {
		String result = "";
		try {
			result = "<pre>" + callTypeHead(input).toString(4) + "</pre>";
		} catch (JSONException | IOException e) {
			logger.error("Errore in call Type Head!!!");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Metodo che si occupa di chiamare il servizio di ICA v11 di type a head
	 * (suggestion)
	 * 
	 * @param input
	 *            frase di input su cui vogliamo ottenere suggerimenti
	 * @return Json object contenente i suggerimenti ottenuti da ICA v11
	 * @throws IOException
	 *             si verifica un eccezione quando ci sono problemi nel
	 *             comunicare con il server di ICA v11
	 * @throws JSONException
	 */
	private JSONObject callTypeHead(String input) throws IOException, JSONException {
		// input = input.toLowerCase();
		logger.info("==== Type A Head Service ====");
		JSONObject result = null;

		input = input.replaceAll(" ", "+");
		URL type_head = new URL(getATypeaHeaedEP(input));
		HttpURLConnection connection = (HttpURLConnection) type_head.openConnection();
		connection.setRequestMethod("POST");
		BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream()), "UTF-8"));
		String serverOutput = "";
		String line = null;
		while ((line = br.readLine()) != null) {
			serverOutput += line;
		}
		logger.info("CALL " + type_head);
		logger.debug("Server Output Type a Head: " + serverOutput);

		result = new JSONObject(serverOutput);
		return result;
	}

}
