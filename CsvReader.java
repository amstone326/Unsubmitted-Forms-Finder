import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by amstone326 on 3/29/17.
 */
public abstract class CsvReader<T> {

    private String csvFilename;
    private List<T> resultOfParse;

    public CsvReader(String filename) {
        this.csvFilename = filename;
        resultOfParse = new ArrayList<T>();
    }

    public abstract T processRow(String csvRow);

    public List<T> readAndParseFile() {
        BufferedReader fileReader = null;
        boolean isHeaderLine = true;
        try {
            String line;
            fileReader = new BufferedReader(new FileReader(csvFilename));
            while ((line = fileReader.readLine()) != null) {
                if (!isHeaderLine) {
                    T rowResult = processRow(line);
                    if (rowResult != null) {
                        resultOfParse.add(rowResult);
                    }
                }
                isHeaderLine = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultOfParse;
    }
}
