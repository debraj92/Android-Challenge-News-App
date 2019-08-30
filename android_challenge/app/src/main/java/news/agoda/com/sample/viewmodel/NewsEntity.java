package news.agoda.com.sample.viewmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * This represents a news item.
 */
public class NewsEntity {

    @SerializedName("section")
    public String section;
    @SerializedName("subsection")
    public String subsection;
    @SerializedName("title")
    public String title;
    @SerializedName("abstract")
    public String summary;
    @SerializedName("url")
    public String url;
    @SerializedName("byline")
    public String byline;
    @SerializedName("published_date")
    public String publishedDate;

    @SerializedName("multimedia")
    public List<MediaEntity> mediaEntityList;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSubsection() {
        return subsection;
    }

    public void setSubsection(String subsection) {
        this.subsection = subsection;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getByline() {
        return byline;
    }

    public void setByline(String byline) {
        this.byline = byline;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public List<MediaEntity> getMediaEntityList() {
        return mediaEntityList;
    }

    public void setMediaEntityList(List<MediaEntity> mediaEntityList) {
        this.mediaEntityList = mediaEntityList;
    }

    /**
     * Override the equals method to compare two NewsEntities correctly. This is needed for Unit
     * Testing.
     *
     * @param obj    The other News entity.
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NewsEntity) {
            NewsEntity news = (NewsEntity) obj;

            /**
             * Check if all the values of the news entity matches correctly.
             */
            boolean areValuesEqual = getPublishedDate().equals(news.getPublishedDate()) &&
                    getSection().equals(news.getSection()) &&
                    getSubsection().equals(news.getSubsection()) &&
                    getSummary().equals(news.getSummary()) &&
                    getTitle().equals(news.getTitle()) &&
                    getByline().equals(news.getByline()) &&
                    getUrl().equals(news.getUrl());

            if(!areValuesEqual) return false;

            /**
             *  We will now have to compare the all the media entities in the list of the media entities.
             *  All the values should match for all the media entities.
             */
            List<MediaEntity> newsList = news.getMediaEntityList();
            if(newsList.size() != mediaEntityList.size()) return false;

            for(int i=0; i<mediaEntityList.size();i++) {
                if(!areMediaValuesEqual(mediaEntityList.get(i),newsList.get(i))) {
                    // media entity values dont match
                    return false;
                }
            }
            // all media entity values have matched
            return true;
        }
        return false;
    }

    /**
     * Checks if two media entities have same values
     * @param mediaEntity1    The first media entity to compare.
     * @param mediaEntity2    The second media entity to compare.
     * @return    True if two media entities are equal.
     */
    private boolean areMediaValuesEqual(MediaEntity mediaEntity1, MediaEntity mediaEntity2) {
        return mediaEntity1.getCopyright().equals(mediaEntity2.getCopyright()) &&
         mediaEntity1.getFormat().equals(mediaEntity2.getFormat()) &&
         mediaEntity1.getHeight() == mediaEntity2.getHeight() &&
         mediaEntity1.getCaption().equals(mediaEntity2.getCaption()) &&
         mediaEntity1.getWidth() == mediaEntity2.getWidth() &&
         mediaEntity1.getSubType().equals(mediaEntity2.getSubType()) &&
         mediaEntity1.getType().equals(mediaEntity2.getType()) &&
         mediaEntity1.getUrl().equals(mediaEntity2.getUrl());
    }

}
