package datacompression;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.*;

public class DataCompressorUI extends JFrame{
    private static JTextField urlText, userText, passText, dbText, tableText;
    private static JLabel logInfo;
    private static JPanel logPanel;
    private static String filePath = "";
    private static StringBuilder logMsg = new StringBuilder();
    private static final int COMPRESSION = 1, DECOMPRESSION = 2;
    
    public static final void init(){
        JFrame frame = new JFrame();
        frame.setTitle("HARDCompressor");
        Color primaryColor = new Color(41, 128, 185 );
        Font font = new Font("sans-serif", Font.BOLD, 18);
        Font infoFont = new Font("sans-serif", Font.BOLD, 24);
        Font inputFont = new Font("sans-serif", Font.PLAIN, 18);
        Font msgFont = new Font("sans-serif", Font.PLAIN, 12);
        JPanel mainPanel = new JPanel(); 
        mainPanel.setBackground(primaryColor);
        mainPanel.setLayout(null);
        mainPanel.setBounds(0, 0, 700, 650);
        
        JLabel dbInfoLabel = new JLabel("DATABASE INFO");
        dbInfoLabel.setFont(infoFont);
        dbInfoLabel.setForeground(Color.WHITE);
        dbInfoLabel.setBounds(100, 30, 500, 50);
        
        JLabel urlLabel = new JLabel("URL");
        urlLabel.setFont(font);
        urlLabel.setForeground(Color.WHITE);
        urlLabel.setBounds(100, 100, 200, 50);
        
        JLabel userLabel = new JLabel("USERNAME");
        userLabel.setFont(font);
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(100, 150, 200, 50);
        
        JLabel passLabel = new JLabel("PASSWORD");
        passLabel.setFont(font);
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(100, 200, 200, 50);
        
        JLabel dbLabel = new JLabel("DATABASE");
        dbLabel.setFont(font);
        dbLabel.setForeground(Color.WHITE);
        dbLabel.setBounds(100, 250, 200, 50);
        
        JLabel tableLabel = new JLabel("TABLE / FILENAME");
        tableLabel.setFont(font);
        tableLabel.setForeground(Color.WHITE);
        tableLabel.setBounds(100, 300, 200, 50);
        
        urlText = new JTextField("jdbc:mysql://localhost:3308");
        urlText.setFont(inputFont);
        urlText.setBackground(primaryColor);
        urlText.setForeground(Color.WHITE);
        urlText.setBounds(300, 100, 300, 40);
        
        userText = new JTextField();
        userText.setFont(inputFont);
        userText.setBackground(primaryColor);
        userText.setForeground(Color.WHITE);
        userText.setBounds(300, 150, 300, 40);
        
        passText = new JTextField();
        passText.setFont(inputFont);
        passText.setBackground(primaryColor);
        passText.setForeground(Color.WHITE);
        passText.setBounds(300, 200, 300, 40);
        
        dbText = new JTextField();
        dbText.setFont(inputFont);
        dbText.setBackground(primaryColor);
        dbText.setForeground(Color.WHITE);
        dbText.setBounds(300, 250, 300, 40);
        
        tableText = new JTextField();
        tableText.setFont(inputFont);
        tableText.setBackground(primaryColor);
        tableText.setForeground(Color.WHITE);
        tableText.setBounds(300, 300, 300, 40);
        
        JButton uploadBtn = new JButton("UPLOAD FILE");
        uploadBtn.setBounds(100, 380, 150, 40);
        
        JButton compressBtn = new JButton("COMPRESS FILE");
        compressBtn.setBackground(Color.WHITE);
        compressBtn.setForeground(primaryColor);
        compressBtn.setFont(font);
        compressBtn.setBounds(100, 450, 250, 50);
        
        JButton decompressBtn = new JButton("DECOMPRESS FILE");
        decompressBtn.setBackground(Color.WHITE);
        decompressBtn.setForeground(primaryColor);
        decompressBtn.setFont(font);
        decompressBtn.setBounds(400, 450, 250, 50);
        
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(Color.WHITE);
        sep2.setBounds(0, 595, 1000, 1);
        
        JLabel creditLabel = new JLabel("CREDITS:   ARPIT MANDAL      DINESH CHAND        HITESH VERMA        RAHUL PAURIYAL      DIT UNIVERSITY DEHRADUN");
        creditLabel.setFont(msgFont);
        creditLabel.setForeground(Color.WHITE);
        creditLabel.setBounds(10, 600, 1000, 10);
        
        mainPanel.add(dbInfoLabel);
        mainPanel.add(urlLabel);
        mainPanel.add(userLabel);
        mainPanel.add(passLabel);
        mainPanel.add(dbLabel);
        mainPanel.add(tableLabel);
        mainPanel.add(urlText);
        mainPanel.add(userText);
        mainPanel.add(passText);
        mainPanel.add(dbText);
        mainPanel.add(tableText);
        mainPanel.add(uploadBtn);
        mainPanel.add(compressBtn);
        mainPanel.add(decompressBtn);
        mainPanel.add(sep2);
        mainPanel.add(creditLabel);
        
        logPanel = new JPanel();
        logPanel.setBackground(Color.BLACK);
        logPanel.setLayout(null);
        JLabel logLabel = new JLabel("LOG");
        logLabel.setFont(font);
        logLabel.setForeground(Color.WHITE);
        logLabel.setBounds(10, 10, 200, 50);
        logInfo = new JLabel();
        logInfo.setFont(msgFont);
        logInfo.setForeground(Color.WHITE);
        logInfo.setBounds(10, 50, 280, 300);
        logPanel.add(logLabel);
        logPanel.add(logInfo);
        JScrollPane scroll = new JScrollPane(logPanel);
        scroll.setBounds(700, 0, 300, 650);
        
        frame.add(mainPanel);
        frame.add(scroll);
        frame.setSize(1000, 650);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        compressBtn.addActionListener((ActionEvent e)->{
            reset();
            logMsg.append("File uploaded " + filePath + "<br>");
            logMsg.append("Starting compression...<br>");
            logInfo.setText("<html>" + logMsg + "</html>");
            getInputs(COMPRESSION);
        });
        decompressBtn.addActionListener((ActionEvent e)->{
            reset();
            logMsg.append("File uploaded " + filePath + "<br>");
            logMsg.append("Starting decompression...<br>");
            logInfo.setText("<html>" + logMsg + "</html>");
            getInputs(DECOMPRESSION);
        });
        uploadBtn.addActionListener((ActionEvent e)->{
            reset();
            JFileChooser fc = new JFileChooser();
            int i = fc.showOpenDialog(logPanel);
            if(i == JFileChooser.APPROVE_OPTION){
                File file = fc.getSelectedFile();
                filePath = file.getPath();
            }
        });
    }
    
    private static final void getInputs(int type){
        String url = urlText.getText();
        String user = userText.getText();
        String pass = passText.getText();
        String db = dbText.getText();
        String table = tableText.getText();
  
        if(url.equals("") || user.equals("") || filePath.equals("") || db.equals("") || table.equals("")){
            logMsg.append("Invalid Inputs <br>");
            logInfo.setText("<html>" + logMsg + "</html>");
            logInfo.setForeground(Color.RED);
            return;
        }
        if(type == COMPRESSION){
            DataCompression.inputData(url, user, pass, db, table, filePath);
        }else{
            Decompressor.inputData(url, user, pass, db, table, filePath);
        }
        
    }
    
    private static final void reset(){
        logMsg = new StringBuilder();
        logInfo.setText("");
        logInfo.setForeground(Color.WHITE);
        logPanel.setVisible(false);
        logPanel.setVisible(true);
    }
    
    public final static void showError(String error){
        logMsg.append(error + "<br> Process Failed! <br>");
        logInfo.setText("<html>" + logMsg + "</html>");
        logInfo.setForeground(Color.RED);
        logPanel.setVisible(false);
        logPanel.setVisible(true);
    }
    
    public final static void showMsg(String msg){
        logMsg.append(msg + "<br>");
        logInfo.setText("<html>" + logMsg + "</html>");
        logInfo.setForeground(Color.WHITE);
        logPanel.setVisible(false);
        logPanel.setVisible(true);
    }
}

