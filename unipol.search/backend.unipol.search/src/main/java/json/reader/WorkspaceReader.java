package json.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class WorkspaceReader {
	private static final String pos="Unknown";
	public static void main(String[] args) throws IOException, JSONException {
		// TODO Auto-generated method stub
		String path_save="/home/administrator/Watson Enterprise Search/Unipol Contest POC/";
		String path="/home/administrator/Watson Enterprise Search/Unipol Contest POC/Progetto IBM/workspace-Unipol-b2c-search.json";
		File workSpace=new File(path);
		BufferedReader buff=new BufferedReader(new FileReader(workSpace));
		String line=null;
		
		String json="";
		while((line=buff.readLine())!=null){
			json+=line+"\n";
		}
		JSONObject obj=new JSONObject(json);
		//System.out.println(obj.toString(4));
		
		JSONArray entities=obj.getJSONArray("entities");
		for(int i=0;i<entities.length();i++){
			JSONObject entity=entities.getJSONObject(i);
			String file_name=entity.getString("entity");
			System.out.println("------>"+file_name);
			JSONArray values=entity.getJSONArray("values");
			String corp="";
			for(int j=0;j<values.length();j++){
				JSONObject value=values.getJSONObject(j);
				String form=value.getString("value");
				System.out.print(form);
				JSONArray synonyms=value.getJSONArray("synonyms");
				for(int k=0;k<synonyms.length();k++){
					if(k==0){
						form+="|";
						System.out.print("|");
					}
					String syn=synonyms.getString(k);
					form+=syn;
					System.out.print(syn);
					if(k+1!=synonyms.length()){
						System.out.print("|");
						form+="|";
					}
				}
				System.out.println("");
				
				form+=","+pos;
				corp+=form+"\n";
			}
			File file_to_save=new File(path_save+file_name+".csv");
			PrintWriter out=new PrintWriter(new FileOutputStream(file_to_save));
			out.println(corp.toLowerCase());
			out.close();
		}
	}

}
