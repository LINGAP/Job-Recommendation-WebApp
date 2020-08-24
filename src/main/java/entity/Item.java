package entity;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item {
	private String id;
	private String title;
	private String location;
	private String companyLogo;
	private String url;
	private String description;
	private Set<String> keywords;

	private boolean favorite;
	
	private Item() {
//		this.id = builder.id;
//		this.title = builder.title;
//		this.location = builder.location;
//		this.companyLogo = builder.companyLogo;
//		this.url = builder.url;
//		this.keywords = builder.keywords;
//		this.description = builder.description;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("title")
	public String getTitle() {
		return title;
	}

	@JsonProperty("location")
	public String getLocation() {
		return location;
	}
	@JsonProperty("company_logo")
	public String getCompanyLogo() {
		return companyLogo;
	}
	@JsonProperty("url")
	public String getUrl() {
		return url;
	}

	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Item item = (Item) o;
		return Objects.equals(id, item.id) &&
				Objects.equals(title, item.title) &&
				Objects.equals(location, item.location) &&
				Objects.equals(companyLogo, item.companyLogo) &&
				Objects.equals(url, item.url) &&
				Objects.equals(description, item.description) &&
				Objects.equals(keywords, item.keywords);
	}

	@Override
	public String toString() {
		return "Item{" +
				"id='" + id + '\'' +
				", title='" + title + '\'' +
				", location='" + location + '\'' +
				", companyLogo='" + companyLogo + '\'' +
				", url='" + url + '\'' +
				", description='" + description + '\'' +
				", keywords=" + keywords +
				'}';
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, title, location, companyLogo, url, description, keywords);
	}

	public static class ItemBuilder {
		private String id;
		private String title;
		private String location;
		private String companyLogo;
		private String url;
		private String description;
		private Set<String> keywords;
		private boolean favorite;

		public Item build() {
			Item item = new Item();
			item.id = id;
			item.title = title;
			item.location = location;
			item.companyLogo = companyLogo;
			item.url = url;
			item.description = description;
			item.keywords = keywords;
			item.favorite = favorite;
			return item;
		}

		public ItemBuilder setFavorite(Boolean favorite){
			this.favorite = favorite;
			return this;
		}
		
		public ItemBuilder id(String itemId) {
			this.id = itemId;
			return this;
		}

		public ItemBuilder title(String name) {
			this.title = name;
			return this;
		}

		public ItemBuilder location(String address) {
			this.location = address;
			return this;
		}

		public ItemBuilder companyLogo(String imageUrl) {
			this.companyLogo = imageUrl;
			return this;
		}

		public ItemBuilder url(String url) {
			this.url = url;
			return this;
		}

		public ItemBuilder setKeywords(Set<String> keywords) {
			this.keywords = keywords;
			return this;
		}

		public ItemBuilder description(String description) {
			this.description = description;
			return this;
		}


	}
	

}
