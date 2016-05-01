package AndroidMaze;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;

public class LeaderboardManager {
    private FileOutputStream _writer;
    private FileInputStream _reader;
    private Context _context;
    private String _filename;
    private String[][] _records;
    private final int TOP = 20; // Best 20 records.

    public String[][] getRecords(){
        return _records;
    }

    public LeaderboardManager(Context context, final String filename){
        _context = context;
        _filename = filename;
    }

    public void loadRecords(){
        _records = new String[TOP][2];
        int rowIndex = 0;
        int sbyte; // Single byte;
        String row = "";

        try {
            _reader = _context.openFileInput(_filename);
            while ((sbyte = _reader.read()) != -1){
                if ((char)sbyte == '\n'){
                    addRecord(rowIndex, row);
                    rowIndex++;
                    row = "";
                }
                else {
                    row += (char) sbyte;
                }
            }
            _reader.close();
        } catch (Exception e) { }

        sortRecords();
    }

    public void saveRecords(){
        try {
            _writer = _context.openFileOutput(_filename, Context.MODE_PRIVATE);

            for (int i = 0; i < TOP; i++){
                if (_records[i][0] == "" || _records[i][0] == null)
                    break;

                _writer.write((_records[i][0] + " " + _records[i][1] + "\n").getBytes());
            }
        } catch (Exception e) { }
        finally {
            try {
                _writer.close();
            } catch (Exception e) { }
        }
    }

    public void addRecord(int index, String row){
        int lastSpace = row.lastIndexOf(' ');
        String name = row.substring(0, lastSpace);
        String time = row.substring(lastSpace+1); // Time in seconds
        
        _records[index][0] = name;
        _records[index][1] = time;
    }

    public void sortRecords(){ // Selection Sort
        int n;
        String tmp;
        
        for (int i = 0; i < TOP; i++){
            if (_records[i][1] == null)
                break;

            n = i;
            for (int j = i+1; j < TOP; j++){
                if (_records[j][1] == null)
                    break;

                if (Integer.parseInt(_records[j][1]) < Integer.parseInt(_records[n][1]))
                    n = j;
            }
            if (n != i){
                tmp = _records[n][0];
                _records[n][0] = _records[i][0];
                _records[i][0] = tmp;
                tmp = _records[n][1];
                _records[n][1] = _records[i][1];
                _records[i][1] = tmp;
            }
        }
    }

}
