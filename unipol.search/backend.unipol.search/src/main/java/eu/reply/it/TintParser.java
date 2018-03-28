package eu.reply.it;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.fbk.dh.tint.runner.TintPipeline;
import eu.fbk.dh.tint.runner.TintRunner;

public class TintParser {
	
	TintPipeline pipeline;
	
	public TintParser() throws IOException{
		pipeline=new TintPipeline();
		pipeline.loadDefaultProperties();
		pipeline.load();
	}
	
	
	public JSONArray lexicalAnalysis(String text) throws IOException, JSONException{
		JSONArray result=null;
		InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		pipeline.run(stream, out, TintRunner.OutputFormat.JSON);
		String output=new String(out.toByteArray());
		JSONObject outjson=new JSONObject(output);
		if(outjson.has("sentences")){
			result=outjson.getJSONArray("sentences");
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException, JSONException{
		TintParser x=new TintParser();
		System.out.println(x.lexicalAnalysis("i topi sono belli sgèojhètt.").toString(4));
	}
	
	

}
