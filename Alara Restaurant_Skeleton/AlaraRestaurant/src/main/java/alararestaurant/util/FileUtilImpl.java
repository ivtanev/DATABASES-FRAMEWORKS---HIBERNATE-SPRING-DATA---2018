package alararestaurant.util;

import java.io.*;

public class FileUtilImpl implements FileUtil {
    @Override
    public String readFile(String filePath) throws IOException {
        StringBuilder fileContent = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));

        String line;

        while((line = bufferedReader.readLine())!=null){
            fileContent.append(line).append(System.lineSeparator());
        }

        return fileContent.toString().trim();
    }
}
