public class StatisticKeyword implements Keyword
{
    private String url, phrase, conclusion;
    private int searchIndex;

    public StatisticKeyword (String source, String key, String index) {
        this(source, key);
        searchIndex = Integer.parseInt(index.trim());
    }

    public StatisticKeyword (String source, String key) {
        url = source;
        phrase = key;
        searchIndex = 0;
    }

    public String getURL() {return url.trim();}
    public String getPhrase() {return phrase;}
    public int getSearchIndex() {return searchIndex;}

    @Override
    public String getConclusion() {return conclusion;}
    @Override
    public void setConclusion(String conc) {conclusion = conc;}

    @Override
    public String recreateToken()
    {
        StringBuilder builder = new StringBuilder("");
        builder.append("``");
        builder.append("statistic;");
        builder.append(url + ";");
        builder.append(phrase + ";");
        builder.append(conclusion + ";");
        builder.append("``");
        return builder.toString();
    }


}
