package external;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

public class GitHubClient {
	private static final String URL_TEMPLATE = 
			"https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
	private static final String DEFAULT_KEYWORD = "developer";

	/**
	 * get List<Item> by lat,lon and key words
	 * @param lat
	 * @param lon
	 * @param keyword
	 * @return
	 */
	public List<Item> search(double lat, double lon, String keyword) {
		if(keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			//encode to url perceived pattern
			keyword = URLEncoder.encode(keyword,"UTF-8");
		}catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String url = String.format(URL_TEMPLATE,keyword,lat,lon);
		CloseableHttpClient httpClient = HttpClients.createDefault();

		ResponseHandler<List<Item>> responseHandler = httpResponse -> {
			if (httpResponse.getStatusLine().getStatusCode()!=200){
				return Collections.emptyList();
			}
			HttpEntity entity = httpResponse.getEntity();
			if(entity==null) {
				return Collections.emptyList();
			}

			ObjectMapper mapper = new ObjectMapper();
			return Arrays.asList(mapper.readValue(entity.getContent(), Item[].class));//get a list to use in service
		};

		try {
			return httpClient.execute(new HttpGet(url),responseHandler);
		}catch(Exception e) {
			e.printStackTrace();
		}

		return Collections.emptyList();
	}
	
	private List<Item> getItemList(JSONArray array){
		List<Item> itemList=new ArrayList<>();
		List<String> descriptionList = new ArrayList<>();
		
		for(int i=0;i<array.length();i++) {
			JSONObject obj=array.getJSONObject(i);
			ItemBuilder itemBuilder =new ItemBuilder();
			itemBuilder.id(obj.isNull("id")? "":obj.getString("id"));
			itemBuilder.title(obj.isNull("title")? "":obj.getString("title"));
			itemBuilder.location(obj.isNull("location")? "":obj.getString("location"));
			itemBuilder.url(obj.isNull("url")? "":obj.getString("url"));
			itemBuilder.companyLogo(obj.isNull("company_logo")? "":obj.getString("company_logo"));
			
			// We need to extract categories from description since GitHub API
						// doesn't return keywords.
			if (obj.getString("description").equals("\n")) {
				descriptionList.add(obj.getString("title"));
			} else {
				descriptionList.add(obj.getString("description"));
			}
			
			Item item = itemBuilder.build();
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
		List<Item> events = client.search(37.38, -122.08, null);
		try {
			for(Item i:events) {
				System.out.println(i.toString());
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
