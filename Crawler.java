import twitter4j.TwitterException;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import java.io.IOException;
import org.jsoup.Jsoup;

/*
 * // -------------------------------------------------------------------------
/**
 *  Given keyword, map value to definition from
 *  either google or wikipedia
 *
 *  @author Braeden Sebastian
 *  @version Apr 18, 2015
 */
public class Crawler
{
    Twitter twitter;
    public Crawler() {
        twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer("Cx6vJY8z99oGfX9VluFwoLECs", "4sKG0YOKmTybyzTOQqHfHKYA0AE1mCCFe34wSxPSSXF5BAUoJI");
        AccessToken accessToken = new AccessToken("589156287-Onfo9oxLY13hy6JQZ218of3ZKjUGsLBLCoB94rOL", "KRgEf46hFQsxFn8ohWhHHalAomQGPSSpqCjR3sgzAYEjt");
        twitter.setOAuthAccessToken(accessToken);
    }

    public Keyword fetchConclusion(Keyword keyword) {
        if (keyword instanceof StatisticKeyword) return fetchStatKeyword(keyword);
        else if (keyword instanceof TwitterKeyword) return fetchTwitterKeyword(keyword);
        keyword.setConclusion("STAT NOT FOUND");
        return keyword;
    }

    private Keyword fetchTwitterKeyword(Keyword keyword) {
        TwitterKeyword twitterKeyword = (TwitterKeyword) keyword;
        Query query = new Query(twitterKeyword.getQuery());
        QueryResult result;
        try
        {
            result = twitter.search(query);
        }
        catch (TwitterException e)
        {
            // TODO Auto-generated catch block
            keyword.setConclusion("STAT NOT FOUND");
            return keyword;
        }
        for (Status status : result.getTweets()) {
            String popularity = "(" + status.getRetweetCount() + ", " + status.getFavoriteCount() + ")";
            twitterKeyword.addTableDataEntry(status.getText(), popularity, status.getCreatedAt().toString());
        }
        return twitterKeyword;
    }

    private  Keyword fetchStatKeyword(Keyword keyword) {
        StatisticKeyword kword = (StatisticKeyword) keyword;
        String url = kword.getURL().toString();
        Document doc;
        try
        {
            doc = Jsoup.connect(url).get();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            //return "Statistic Not Found";
            kword.setConclusion("STAT NOT FOUND");
            return kword;
        }
        Element elm = doc.select("div[id=content]").first();
        String[] values = elm.text().split(kword.getPhrase());
        String val = values[1];
        int startingIndex = 0;
        int endingIndex = -1;
        if (kword.getSearchIndex() == 0) {
            while (!Character.isDigit(val.charAt(startingIndex))) {startingIndex++;}
            endingIndex = startingIndex;
            while (Character.isDigit(val.charAt(endingIndex)) || val.charAt(endingIndex) == '.' ||
                val.charAt(endingIndex) == ','){
                endingIndex++;
            }
        } else {
            while (!Character.isAlphabetic(val.charAt(startingIndex))) {startingIndex++;}
            endingIndex = startingIndex;
            int wordCount = 0;
            while (wordCount < kword.getSearchIndex()) {
                if (val.charAt(endingIndex) == ' ') wordCount++;
                endingIndex++;
            }

        }
        kword.setConclusion(val.substring(startingIndex, endingIndex));
        return kword;
    }
}
