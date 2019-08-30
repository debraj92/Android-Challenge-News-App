package news.agoda.com.sample.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class NetworkRequestProcessorTest {

    @Mock
    NewsDataBaseController mNewsDB;

    @Mock
    NetworkProcessor mNextNextworkProcessor;

    @InjectMocks
    NetworkRequestProcessor mNetworkRequestProcessor;

    String url = "https://api.myjson.com/bins/nl6jh";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        mNetworkRequestProcessor = null;
    }

    /**
     * Test if response from valid url can be handled properly.
     */
    @Test
    public void fetchFromValidURL() {
       String response = mNetworkRequestProcessor.fetchFromURL(url);
       assertTrue(response != null && !response.isEmpty());
    }

    /**
     * Test if invalid urls are handled gracefully.
     */
    @Test
    public void fetchFromInvalidURL() {
        url = "https://api.myjson.com/bins/nl6";
        String response = mNetworkRequestProcessor.fetchFromURL(url);
        assertTrue(response == null);
    }
}