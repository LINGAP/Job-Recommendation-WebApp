package external;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.ExtractRequestBody;
import entity.ExtractResponseItem;
import entity.Extraction;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.monkeylearn.ExtraParam;
import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;

public class MonkeyLearnClient {
	private static final String EXTRACT_URL = "https://api.monkeylearn.com/v3/extractors/ex_YCya9nrn/extract/";
	private static final String API_KEY = "064c0f579cb5632e23806aaa6739abbac47395c6";// make sure change it to your api key.
  	public static void main(String[] args) {

		String[] textList = {
			"Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit’s full-body look.", };
		List<Set<String>> words = extractKeywords(textList);
		for (Set<String> ws : words) {
			for (String w : ws) {
				System.out.println(w);
			}
				System.out.println();
		}
	}
	
	public static List<Set<String>> extractKeywords(String[] text) {//mk processor
		// Use the API key from your account
		MonkeyLearn ml = new MonkeyLearn(API_KEY);
		ObjectMapper mapper = new ObjectMapper();
		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

		HttpPost request = new HttpPost(EXTRACT_URL);
		request.setHeader("Content-type", "application/json");
		request.setHeader("Authorization", "Token " +API_KEY);

		ExtractRequestBody body = new ExtractRequestBody(text, 3);
		String jsonBody;
		try {
			jsonBody = mapper.writeValueAsString(body);
		} catch (JsonProcessingException e) {
			return Collections.emptyList();
		}

		try {
			request.setEntity(new StringEntity(jsonBody));
		} catch (UnsupportedEncodingException e) {
			return Collections.emptyList();
		}

		ResponseHandler<List<Set<String>>> responseHandler = response -> {
			if (response.getStatusLine().getStatusCode() != 200) {
				return Collections.emptyList();
			}
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return Collections.emptyList();
			}
			ExtractResponseItem[] results = mapper.readValue(entity.getContent(), ExtractResponseItem[].class);
			List<Set<String>> keywordList = new ArrayList<>();
			for (ExtractResponseItem result : results) {
				Set<String> keywords = new HashSet<>();
				for (Extraction extraction : result.extractions) {
					keywords.add(extraction.parsedValue);
				}
				keywordList.add(keywords);
			}
			return keywordList;
		};

		try {
			return closeableHttpClient.execute(request, responseHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();

	}

}
