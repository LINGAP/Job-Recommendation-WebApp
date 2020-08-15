package external;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import entity.item;
import entity.item.ItemBuilder;

public class GitHubClient {
	private static final String URL_TEMPLATE = 
			"https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
	private static final String DEFAULT_KEYWORD = "developer";
	
	public List<item> search(double lat,double lon,String keyword) {
		if(keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			keyword = URLEncoder.encode(keyword,"UTF-8");
		}catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String url = String.format(URL_TEMPLATE,keyword,lat,lon);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CloseableHttpResponse response = httpClient.execute(new HttpGet(url));
			if(response.getStatusLine().getStatusCode()!=200) {
				return new ArrayList<>();
			}
			HttpEntity entity = response.getEntity();
			if(entity==null) {
				return new ArrayList<>();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));//get content from entity, read content onto sb
			StringBuilder responseBody = new StringBuilder();
			String line = null;
			while((line=reader.readLine())!=null) {
				responseBody.append(line);
			}
			JSONArray jsonarray=new JSONArray(responseBody.toString());//convert sb to jsonArray
			return getItemList(jsonarray);//get a list to use in service
		}catch(ClientProtocolException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	private List<item> getItemList(JSONArray array){
		List<item> itemList=new ArrayList<>();
		List<String> descriptionList = new ArrayList<>();
		
		for(int i=0;i<array.length();i++) {
			JSONObject obj=array.getJSONObject(i);
			ItemBuilder builder=new item.ItemBuilder();
			builder.setItemId(obj.isNull("id")? "":obj.getString("id"));
			builder.setName(obj.isNull("title")? "":obj.getString("title"));
			builder.setAddress(obj.isNull("location")? "":obj.getString("location"));
			builder.setUrl(obj.isNull("url")? "":obj.getString("url"));
			builder.setImageUrl(obj.isNull("company_logo")? "":obj.getString("company_logo"));
			
			// We need to extract categories from description since GitHub API
						// doesn't return keywords.
			if (obj.getString("description").equals("\n")) {
				descriptionList.add(obj.getString("title"));
			} else {
				descriptionList.add(obj.getString("description"));
			}
			
			item item = builder.build();
			itemList.add(item);
		}

		// We need to get keywords from multiple text in one request since
		// MonkeyLearnAPI has a limitation on request per minute.
		String[] descriptionArray = descriptionList.toArray(new String[descriptionList.size()]); // Convert list to an array of the same type.
		List<List<String>> keywords = MonkeyLearnClient.extractKeywords(descriptionArray); // Call MonkeyLearn API.
		for (int i = 0; i < keywords.size(); ++i) {
			List<String> list = keywords.get(i);
			
			Set<String> set = new HashSet<String>(list);
			itemList.get(i).setKeywords(set);
		}
		
		return itemList;
	}
	
	public static void main(String[] args) {
		GitHubClient client = new GitHubClient();
		List<item> events = client.search(37.38, -122.08, null);
		try {
			for(item i:events) {
				JSONObject event = i.toJSONObject();
				System.out.println(event.toString(2));
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
