package pt.ipp.estg.Utils;

import java.net.URL;

/**
 * The {@code Resources} class provides utility methods for working with resources
 * such as retrieving file URLs and absolute paths from the classpath.
 * It helps in obtaining resource paths based on file names.
 *
 * @author Carlos Leite, Sergio Felix
 * @version 1.0
 */
public class Resources {
    /**
     * Retrieves the URL of a file from the resources directory based on the file name.
     *
     * @param fileName The name of the file in the resources directory.
     * @return The URL of the specified file.
     */
    private static URL getFileURLFromResources(String fileName) {
        return Resources.class.getClassLoader().getResource(fileName);
    }

    /**
     * Converts a file URL to its absolute path.
     *
     * @param fileURL The URL of the file.
     * @return The absolute path of the file.
     */
    private static String getAbsolutePathFromURL(URL fileURL) {
        return fileURL.getPath();
    }

    /**
     * Retrieves the absolute path of a file from the resources directory based on the file name.
     *
     * @param fileName The name of the file in the resources directory.
     * @return The absolute path of the specified file.
     */
    public static String getPathFromResources(String fileName) {
        URL fileURL = getFileURLFromResources(fileName);
        return getAbsolutePathFromURL(fileURL);
    }
}
