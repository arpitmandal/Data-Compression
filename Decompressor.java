package datacompression;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class Decompressor extends Thread{
    private static String mUrl, mUser, mPass, mDb, mTable, mPath;
    private static HashMap<String, String> symbolTable = new HashMap<String, String>();
    private static ArrayList<String[]> symbolList = new ArrayList<String[]>();
    
    @Override
    public void run(){
        connectDb();
        readFile();
    }
    
    //set input data to member variables
    public static final void inputData(String url, String user, String pass, String db, String table, String path){
        mUrl = url;
        mUser = user;
        mPass = pass;
        mDb = db;
        mTable = table;
        mPath = path;
        //start thread to read database and file
        Decompressor ob = new Decompressor();
        ob.start();
    }
    
    //connect to database
    private void connectDb(){
        Connection con = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(mUrl + "/" + mDb, mUser, mPass);
            Statement st = con.createStatement();
            String query = "SELECT * FROM " + mTable;
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                //key - symbol, value - word
                symbolTable.put(rs.getString("symbol"), rs.getString("word"));
            }
            con.close();
            DataCompressorUI.showMsg("Database read successful");
        }
        catch(Exception e){
            DataCompressorUI.showError(e.getMessage());
        }
    }
    
    //read file
    private void readFile(){
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        String delimiters = "\\s+|,\\s*|\\.\\s*";
        String line = null;
        String[] symbols;
        try{
            inputStream = new FileInputStream(mPath);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-16"));
            while((line = bufferedReader.readLine()) != null){
                symbols = line.split(" ");
                symbolList.add(symbols);
            }
            inputStream.close();
            bufferedReader.close();
            DataCompressorUI.showMsg("File read successful");
            substitute();
        }
        catch(Exception e){
            DataCompressorUI.showError(e.getMessage());
        }
    }
    
    //substitute symbols with the words
    private void substitute(){
        ArrayList<String[]> newLines = new ArrayList<String[]>();
        String[] symbols;
        String word;
        for(int i=0; i<symbolList.size(); i++){
            symbols = symbolList.get(i);
            //get key-value pairs from symbolTable
            for(String symbol : symbolTable.keySet()){
                word = symbolTable.get(symbol);
                //replace symbol from the words array with the word
                symbols = replace(symbols, symbol, word);
            }
            newLines.add(symbols);
        }
        DataCompressorUI.showMsg("Substitution completed");
        merge(newLines);
    }
    
    //replace symbol in the symbols[] with the word
    private String[] replace(String[] symbols, String symbol, String word){
        for(int i=0; i<symbols.length; i++){
            if(symbols[i].trim().length() > 0){
                if(symbols[i].equals(symbol)){
                    symbols[i] = word;
                }
            } 
        }
        return symbols;
    }
    
    //merge the symbolList into a String.
    private void merge(ArrayList<String[]> lines){
        StringBuilder sb = new StringBuilder();
        String[] words;
        for(int i=0; i<lines.size(); i++){
            words = lines.get(i);
            for(int j=0; j<words.length; j++){
                sb.append(words[j]);
                sb.append(" ");
            }
            sb.append("\n");
        }
        DataCompressorUI.showMsg("Merge Completed");
        writeFile(sb.toString());
    }
    
    //write data into the file
    private void writeFile(String lines){
        PrintWriter writer = null;
        int dot = mPath.lastIndexOf(".");
        String fileName = mPath.substring(0, dot-1) + "d.txt";
        try{
            writer = new PrintWriter(fileName, "UTF-8");
            writer.println(lines);
            writer.close();
            DataCompressorUI.showMsg("New file " + fileName);
            DataCompressorUI.showMsg("File Decompressed Successfully");
        }
        catch(Exception e){
            DataCompressorUI.showError(e.getMessage());
        }
    } 
}
