package news.agoda.com.sample.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import news.agoda.com.sample.viewmodel.MediaEntity;
import news.agoda.com.sample.viewmodel.NewsEntity;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class NetworkResponseProcessorTest {

    @Mock
    NewsDataBaseController mNewsDB;

    @InjectMocks
    NetworkResponseProcessor mNetworkResponseProcessor;

    private static final String PATH = "src/test/java/news/agoda/com/sample/model/";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        mNetworkResponseProcessor = null;
    }

    /**
     * Test if the server response is parsed correctly and we get the list of news.
     * @throws IOException
     */
    @Test
    public void executeExpectedResponseFromServer() throws IOException {
        String responseFromServer = readFromFile(PATH +"response_valid.txt");
        ArrayList<NewsEntity> list = mNetworkResponseProcessor.execute(responseFromServer);
        assertNotNull(list);
    }

    /**
     * Check if the server response creates a similar object as we expect
     * @throws IOException
     */
    @Test
    public void checkIfResultArrayMatchesExpected() throws IOException {
        // read the server response from a file
        String responseFromServer = readFromFile(PATH + "response_single.txt");
        // obtain the list of news
        ArrayList<NewsEntity> list = mNetworkResponseProcessor.execute(responseFromServer);
        // convert to array
        NewsEntity[] newsEntityArr = list.toArray(new NewsEntity[list.size()]);
        // get expected array of news entity for this server response.
        NewsEntity[] expectedEntities = getExpectedResult();

        // check if they match.
        assertArrayEquals(expectedEntities,newsEntityArr);
    }

    /**
     * Check if we can handle the case where there are no multimedias and empty string
     * is provided in the multimedia field.
     * @throws IOException
     */
    @Test
    public void checkResultWithNoMediaList() throws IOException {
        String responseFromServer = readFromFile(PATH + "response_no_media.txt");
        ArrayList<NewsEntity> list = mNetworkResponseProcessor.execute(responseFromServer);
        /**
         * We need to check that if the server response has multimedia as empty string we are
         * able to parse the response correctly (not null) and also the empty string is converted
         * to a size-zero list.
         */
        assertNotNull(list);
        assertTrue(list.get(0).getMediaEntityList().size() == 0);
    }

    private NewsEntity[] getExpectedResult() {
        NewsEntity news = new NewsEntity();
        news.setSection("Business Day");
        news.setByline("By NOAM SCHEIBER");
        news.setPublishedDate("2015-08-18T04:00:00-5:00");
        news.setSubsection("");
        news.setSummary("Top-tier employers may be changing their official policies in a nod to " +
                "work-life balance, but brutal competition remains an inescapable component of " +
                "workers' daily lives.");
        news.setUrl("http://www.nytimes.com/2015/08/18/business/work-policies-may-be-kinder-" +
                "but-brutal-competition-isnt.html");
        news.setTitle("Work Policies May Be Kinder, but Brutal Competition Isn't");

        MediaEntity mediaEntity = new MediaEntity();
        mediaEntity.setUrl("http://static01.nyt.com/images/2015/08/18/business/18EMPLOY/18EMPLOY-thumbStandard.jpg");
        mediaEntity.setFormat("Standard Thumbnail");
        mediaEntity.setHeight(75);
        mediaEntity.setWidth(75);
        mediaEntity.setType("image");
        mediaEntity.setSubType("photo");
        mediaEntity.setCaption("People eating at the Brave Horse Tavern on the Amazon campus in Seattle in June.");
        mediaEntity.setCopyright("Matthew Ryan Williams for The New York Times");

        ArrayList<MediaEntity> mediaEntities = new ArrayList<>();
        mediaEntities.add(mediaEntity);
        news.setMediaEntityList(mediaEntities);

        return new NewsEntity[] {news};

    }

    private String readFromFile(String fileName) throws IOException {
        InputStream is = new FileInputStream(fileName);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();

        while(line != null){
            sb.append(line).append("\n");
            line = buf.readLine();
        }

        return sb.toString();
    }
}