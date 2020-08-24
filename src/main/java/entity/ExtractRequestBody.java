package entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExtractRequestBody {
    public String[] data;

    @JsonProperty("max_keywords")
    public int maxKeywords;

    public ExtractRequestBody(String[] data, int maxKeywords) {
        this.data = data;
        this.maxKeywords = maxKeywords;
    }
}
