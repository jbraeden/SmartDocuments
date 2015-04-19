import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.BreakClear;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import java.io.FileInputStream;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/*
 * // -------------------------------------------------------------------------
/**
 *  Class will represent single word document.
 *  Steps:
 *  1. Extract all keywords
 *  2. Map keywords to value
 *  3. Recreate/Modify existing file
 *
 *  @author Braeden Sebastin
 *  @version Apr 18, 2015
 */
public class SmartFile
{
    private String filename, text;
    private XWPFDocument document;
    private XWPFDocument finalDocument;
    private XWPFWordExtractor extractor;
    String[] tokens;
    Keyword[] keywords;

    public SmartFile(String file)  throws Exception{
        document = new XWPFDocument(new FileInputStream(file) );
        extractor = new XWPFWordExtractor(document);
        filename = file.toString();
        extractKeywords();

    }

    private void extractKeywords() {
        text = extractor.getText();
        tokens = text.split("``");
        keywords = new Keyword[tokens.length];
        int keywordCount = 0;
        for (int index = 1; index < tokens.length; index+=2) {
            keywords[keywordCount] = parseKeyword(tokens[index]);
            keywordCount++;
       }
    }

    private Keyword parseKeyword(String input) {
        String[] parts = input.split(";");
        String type = parts[0];
        switch(type) {
            case "statistic":
                return new StatisticKeyword(parts[1], parts[2], parts[3]);
            case "twitter":
                return new TwitterKeyword(parts[1]);
            default: return null;
        }
    }

    public void printTokens() {
        for (int index = 0; index < tokens.length; index++) {
            System.out.println(index + ") " + tokens[index]);
        }
    }

    public void printKeywords() {
        for (int index = 0; index < keywords.length; index++) {
            System.out.println(keywords[index]);
        }
    }

    public Keyword[] getKeywords() {return keywords;}

    public String createURL(String word) {
        String urlString = word.trim().replace(" ", "_");
        return urlString.trim();
    }

    public String composeFile(boolean clean) {
        int keywordCounter = 0;
        StringBuilder builder = new StringBuilder("");
        for (int index = 0; index < tokens.length; index++) {
            if (index % 2 == 0) {
                //Index is NOT a keyword
                builder.append(tokens[index]);
            } else {
                //Index is a keyword
                if (clean && keywords[keywordCounter] != null) {
                    builder.append(keywords[keywordCounter].getConclusion());
                } else if (keywords[keywordCounter] != null){
                    builder.append(keywords[keywordCounter].recreateToken());
                }
                keywordCounter++;
            }
        }

        try
        {
            String fDocumentName = filename.substring(0, filename.length() - 5) + "CLEAN.docx";
            finalDocument = new XWPFDocument();
            FileOutputStream output = new FileOutputStream(new File(fDocumentName));
            XWPFRun run = finalDocument.createParagraph().createRun();
            run.setText(builder.toString());
            for (int i = 0; i < keywords.length; i++) {
                if (keywords[i] != null && keywords[i] instanceof TwitterKeyword) {
                    document.createParagraph().createRun().setText("New Figure");
                    document.createParagraph().createRun().addBreak(BreakType.PAGE);
                    TwitterKeyword Tkeyword = (TwitterKeyword) keywords[i];
                    XWPFTable table = finalDocument.createTable();
                    XWPFTableRow rowOne = table.getRow(0);
                    rowOne.getCell(0).setText("Tweet Text (" + Tkeyword.getQuery() + ")");
                    rowOne.createCell().setText("Popularity (Retweets, Favorites)");
                    rowOne.createCell().setText("Date Created");

                    for(int count = 0; count < Tkeyword.getDataCounter(); count++) {
                        XWPFTableRow newRow = table.createRow();
                        newRow.getCell(0).setText(Tkeyword.getTableData()[count][0]);
                        newRow.getCell(1).setText(Tkeyword.getTableData()[count][1]);
                        newRow.getCell(2).setText(Tkeyword.getTableData()[count][2]);
                    }

                }
            }
            finalDocument.write(output);
            output.close();
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return builder.toString();
    }

    public void configureFile(String text) throws IOException {
        String fDocumentName = filename.substring(0, filename.length() - 5) + "CLEAN.docx";
        finalDocument = new XWPFDocument();
        FileOutputStream output = new FileOutputStream(new File(fDocumentName));
        XWPFRun run = finalDocument.createParagraph().createRun();
        run.setText(text);

        TwitterKeyword keyword = null;
        for (int i = 0; i < keywords.length; i++) {
            if (keywords[i] instanceof TwitterKeyword)
                keyword = (TwitterKeyword) keywords[i];
        }
        if (keyword == null) return;
        XWPFTable table = finalDocument.createTable();
        XWPFTableRow rowOne = table.getRow(0);
        rowOne.getCell(0).setText("Tweet Text");
        rowOne.createCell().setText("Popularity (Retweets, Favorites)");
        rowOne.createCell().setText("Date Created");

        for(int count = 0; count < keyword.getDataCounter(); count++) {
            XWPFTableRow newRow = table.createRow();
            newRow.getCell(0).setText(keyword.getTableData()[count][0]);
            newRow.createCell().setText(keyword.getTableData()[count][1]);
            newRow.createCell().setText(keyword.getTableData()[count][2]);
        }


        finalDocument.write(output);
        output.close();
    }

    public void updateFileKeywords(Keyword[] kwords) {
        for (int index = 0; index < kwords.length && keywords[index] != null; index++) {
            //----------------------------HANDLE TWITTER DATA---------------------------
            keywords[index].setConclusion(kwords[index].getConclusion());
        }
    }


}
