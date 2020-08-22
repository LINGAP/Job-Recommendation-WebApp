package external;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

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
			List<Item> items = Arrays.asList(mapper.readValue(entity.getContent(), Item[].class));//get a list to use in service
			extractKeywords(items);
			return items;
		};

		try {
			return httpClient.execute(new HttpGet(url),responseHandler);
		}catch(Exception e) {
			e.printStackTrace();
		}

		return Collections.emptyList();
	}

	private void extractKeywords(List<Item> items) {

		String[] descriptions = items.stream()
				.map(Item::getDescription)
				.toArray(String[]::new);

		List<Set<String>> keywordList = MonkeyLearnClient.extractKeywords(descriptions);
		for (int i = 0; i < items.size(); i++) {
			items.get(i).setKeywords(keywordList.get(i));
		}
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
