import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.io.File;

public class Driver extends JPanel implements ActionListener
{
    static JFrame jframe;
    JFileChooser fileChooser;
    JTextArea logText;
    JButton selectButton;
    JCheckBox checkButton;
    JPanel jpanel;

    public Driver() {
        super(new BorderLayout());
        logText = new JTextArea(5, 20);
        logText.setMargin(new Insets(5, 5, 5, 5));
        logText.setEditable(false);
        JScrollPane scroll = new JScrollPane(logText);
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        selectButton = new JButton("Browse Files");
        selectButton.addActionListener(this);
        checkButton = new JCheckBox("Clean");
        checkButton.addActionListener(this);
        jpanel  = new JPanel();
        //jpanel.setLayout(new LinearLayout());
        jpanel.add(selectButton);
        jpanel.add(checkButton);

        add(scroll);
        add(jpanel);
    }


    public static void createGUI() {
        jframe = new JFrame("Smart Document");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.add(new Driver());
        jframe.pack();
        jframe.setVisible(true);
    }


    //TODO: Handle file explorer
   public static void main(String[] args)throws Exception
   {
       if (args.length == 0) {
           createGUI();
           return;
       }
       //Otherwise, iterate through args an get values
       //Update GUI with some statistics???



   }

   public static void runSolver(File[] files, boolean clean) throws Exception {
       Crawler crawler = new Crawler();
       for (int fileCount = 0; fileCount < files.length; fileCount++) {
           SmartFile file = new SmartFile(files[fileCount].getAbsolutePath());
           boolean cleanCompose = clean;
           Keyword[] keywords = file.getKeywords();
           for (int index = 0; index < keywords.length && keywords[index] != null; index++) {
               keywords[index] = crawler.fetchConclusion(keywords[index]);
           }
           file.updateFileKeywords(keywords);
           file.composeFile(true);
           System.out.println("Succeussfully written");
           //System.out.println(file.composeFile(cleanCompose));
       }
   }

@Override
public void actionPerformed(ActionEvent e)
{
    if (e.getSource() == selectButton) {
        int choice = fileChooser.showOpenDialog(Driver.this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            if (files.length == 0) {
                File file = fileChooser.getSelectedFile();
                files = new File[] {file};
            }
            try
            {
                jframe.dispose();
                jframe.setVisible(false);
                runSolver(files, checkButton.isSelected());
            }
            catch (Exception e1)
            {
                // TODO Auto-generated catch block
                System.out.println("Unable to open files");
                e1.printStackTrace();
            }


        }
    }
    // TODO Auto-generated method stub

}

}
