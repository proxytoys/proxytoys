package minimesh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;


/**
 * Facade for underlying filesystem.
 * 
 * @author Joe Walnes
 */
public class FileSystem {

    public char[] readFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            try {
                char[] data = new char[(int)file.length()];
                reader.read(data);
                return data;
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            throw new FileSystemException("Cannot read data from " + file.getName(), e);
        }
    }

    public void copyAllFiles(File sourceDirectory, File targetDirectory, String suffixesToExclude) {
        StringTokenizer tokenizer = new StringTokenizer(suffixesToExclude, ",");
        String[] badSuffixes = new String[tokenizer.countTokens()];
        for (int i = 0; i < badSuffixes.length; i++) {
            badSuffixes[i] = tokenizer.nextToken();
        }
        File[] files = sourceDirectory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            boolean fileShouldBeCopied = file.isFile();
            for (int j = 0; j < badSuffixes.length; j++) {
                String badSuffix = badSuffixes[j];
                if (file.getName().endsWith("." + badSuffix)) {
                    fileShouldBeCopied = false;
                }
            }
            if (fileShouldBeCopied) {
                copyFile(file, new File(targetDirectory, file.getName()));
            }
        }
    }

    private void copyFile(File source, File destination) {
        try {
            InputStream sourceStream = new FileInputStream(source);
            OutputStream destinationStream = new FileOutputStream(destination);
            int c;
            while((c = sourceStream.read()) != -1) {
                destinationStream.write(c);
            }
            sourceStream.close();
            destinationStream.close();
        } catch (IOException e) {
            throw new FileSystemException("Cannot copy " + source + " to " + destination, e);
        }
    }

    public static class FileSystemException extends RuntimeException {
        public FileSystemException(String message, Throwable throwable) {
            super(message);
        }
    }
}
