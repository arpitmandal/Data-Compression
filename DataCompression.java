package datacompression;
import java.io.*;
import java.sql.*;
import java.util.*;

class CustomType{
        private String word;
        private int weight;
        public CustomType(String word, int weight){
            this.word = word;
            this.weight = weight;
        }
        public String getWord(){ return word; }
        public int getWeight(){ return weight; }
}

class NewThread extends Thread{
    @Override
    public void run(){
        DataCompression ob = new DataCompression();
        ob.createNewFile();
    }
}

public class DataCompression extends Thread {
    private static HashMap<String, Integer> frequencyMap = new HashMap<String, Integer>();
    private static HashMap<String, Character> substituteMap = new HashMap<String, Character>();
    private static ArrayList<CustomType> weightList = new ArrayList<CustomType>();
    private static ArrayList<String[]> wordsList = new ArrayList<String[]>();
    private static String[] weightListWords;
    private static int[] weightListWeights;
    private static String url, user, pass, db, table, filePath;
  
    @Override
    public void run(){
            storeDatabase();
    }
   
    //set the input params
    public static void inputData(String mUrl, String mUser, String mPass, String mDb, String mTable, String mPath){
        url = mUrl;
        user = mUser;
        pass = mPass;
        db = mDb;
        table = mTable;
        filePath = mPath;
        readFile();
    }
    
    //read the file
    private static void readFile(){
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        String line = null;
        String delimiters = "\\s+|,\\s*|\\.\\s*";
        String[] words;
        try{
            inputStream = new FileInputStream(filePath);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while((line = bufferedReader.readLine()) != null){
                words = line.split(" ");
                wordsList.add(words);
            }
            inputStream.close();
            bufferedReader.close();
            DataCompressorUI.showMsg("File read successful");
            countFrequency();
        }
        catch(Exception e){
            DataCompressorUI.showError(e.getMessage());
        }
    }
    
    //count frequency of each word
    private static void countFrequency(){
        int newFrequency;
        String[] words;
        for(int j=0; j<wordsList.size(); j++){
            words = wordsList.get(j);
            for(int i=0; i<words.length; i++){
                newFrequency = (frequencyMap.get(words[i]) == null) ? 1 : frequencyMap.get(words[i])+1;
                frequencyMap.put(words[i], newFrequency);
            }
        }
        DataCompressorUI.showMsg("Frequency count completed");
        calculateWeight();
    }
    
    //calculate weight for each word
    private static void calculateWeight(){
        int weight = 0;
        for (String i : frequencyMap.keySet()) {
            weight = i.length() * frequencyMap.get(i);
            weightList.add(new CustomType(i, weight));  
        }
        DataCompressorUI.showMsg("Weight calculated successfully");
        sortWeight();
    }
    
    //sort weight in descending order
    private static void sortWeight(){
        int max, tempWeight;
        String tempWord;
        int size = weightList.size();
        weightListWords = new String[size];
        weightListWeights = new int[size];
        for(int i=0; i<size; i++){
            weightListWords[i] = weightList.get(i).getWord();
            weightListWeights[i] = weightList.get(i).getWeight();
        }
        for(int i=0; i<size-1; i++){
            max = i;
            for(int j=i+1; j<size; j++){
                if(weightListWeights[j] > weightListWeights[max]){
                    max = j;
                }
            }
            if(max != i){
                tempWeight = weightListWeights[i];
                tempWord = weightListWords[i];
                weightListWeights[i] = weightListWeights[max];
                weightListWords[i] = weightListWords[max];
                weightListWeights[max] = tempWeight;
                weightListWords[max] = tempWord;
            }
        }
        DataCompressorUI.showMsg("Weight sorted in descending order");
        substitute();
    }
    
    //substitute the higher weighted words with special characters
    private static void substitute(){
        //extended UTF-8 code 128 to 255
        int startCode = 128;
        int index = 0;
        int length = weightListWords.length;
        while(startCode < 65535 && index < length ){
            substituteMap.put(weightListWords[index], (char) startCode);
            index++;
            startCode++;
        }  
        DataCompressorUI.showMsg("Substitution completed <br> Starting Threads ...");
        //start thread for storing data in the db
        DataCompression ob = new DataCompression();
        ob.start();
        //start thread for creating new file
        NewThread nt = new NewThread();
        nt.start();  
    }
    
    //store substitute key-value pairs in database
    private void storeDatabase(){
        char symbol;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        try{  
            Class.forName("com.mysql.jdbc.Driver");  
            con = DriverManager.getConnection(url + "/" + db, user, pass);  
            Statement stmt=con.createStatement();  
            String query = "CREATE TABLE " + table + " (id INT AUTO_INCREMENT PRIMARY KEY, word VARCHAR(50) NOT NULL, symbol VARCHAR(5) NOT NULL)";
            stmt.executeUpdate(query);
            String insertQuery = "INSERT INTO " + table + " (word, symbol) VALUES (?, ?)";
            preparedStatement = con.prepareStatement(insertQuery);
            for (String word : substituteMap.keySet()) {
                symbol = substituteMap.get(word);
                preparedStatement.setString(1, word);
                preparedStatement.setString(2, Character.toString(symbol));
                preparedStatement.execute();
            }
            preparedStatement.close();
            con.close();
            DataCompressorUI.showMsg("Data stored in database successfully");
        }catch(Exception e){
            DataCompressorUI.showError(e.getMessage());
        }    
    }
    
    //create new file
    public void createNewFile(){
        PrintWriter writer = null;
        StringBuilder sb = new StringBuilder();
        String[] words;
        char symbol;
        String line;
        for(int i=0; i<wordsList.size(); i++){
            words = wordsList.get(i);
            for (String word : substituteMap.keySet()) {
                symbol = substituteMap.get(word);
                //replace all 'word' with 'symbol' in the 'words' array
                words = replace(words, word, symbol);
            }
            //create line with words array containing special symbols
            line = createLine(words);
            sb.append(line + "\n");
        }
        try{
            int dot = filePath.indexOf('.');
            String newFile = filePath.substring(0, dot) + "c.txt";
            writer = new PrintWriter(newFile, "UTF-8");
            writer.println(sb.toString());
            writer.close();
            DataCompressorUI.showMsg("New file " + newFile);
            DataCompressorUI.showMsg("File Compressed Successfully");
        }
        catch(Exception e){
            DataCompressorUI.showError(e.getMessage());
        }
    }
    
    //replace all 'word' with 'symbol' in the 'words' array
    private String[] replace(String[] words, String word, char symbol){
        for(int i=0; i<words.length; i++){
            if(words[i].equals(word)){
                words[i] = Character.toString(symbol);
            }
        }
        return words;
    }
    
    //create line with words array containing special symbols
    private String createLine(String[] words){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<words.length; i++){
            sb.append(words[i] + " ");
        }
        return sb.toString();
    }
    
    public static void main(String[] args) {
        DataCompressorUI.init();
    } 
}
