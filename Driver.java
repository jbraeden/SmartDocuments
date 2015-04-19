
public class TwitterKeyword implements Keyword
{
    private String queryString, conclusion;
    private int dataCounter, figureNumber;
    String[][] tableData;

    public TwitterKeyword(String qString) {
        queryString = qString;
        tableData = new String[5][3];
        dataCounter = 0;
        figureNumber = 1;
        conclusion = "(See Figure " + figureNumber + ". for twitter data)";
    }

    public void setFigureNumber(int number) {
        figureNumber = number;
        conclusion = "(See Figure " + figureNumber + ". for twitter data)";
    }


    @Override
    public String getConclusion()
    {
        return conclusion;
    }

    @Override
    public void setConclusion(String conc)
    {
        conclusion = conc;
    }

    @Override
    public String recreateToken()
    {
        StringBuilder builder = new StringBuilder("");
        builder.append("``");
        builder.append("twitter;");
        builder.append(queryString + ";");
        builder.append(conclusion + ";");
        builder.append("``");
        return builder.toString();
    }

    public String getQuery() {
        return queryString;
    }

    public void addTableDataEntry(String text, String popularity, String created) {
        if (dataCounter < 5) {
            tableData[dataCounter][0] = String.copyValueOf(text.toCharArray());
            tableData[dataCounter][1] = String.copyValueOf(popularity.toCharArray());
            tableData[dataCounter][2] = String.copyValueOf(created.toCharArray());
            dataCounter++;
        }
    }

    public String[] getTableEntry(int row) {
        if (tableData[row][0] == null) return null;
        return tableData[row];
    }

    public int getDataCounter() {
        return dataCounter;
    }

    public String[][] getTableData() {
        return tableData;
    }
}
