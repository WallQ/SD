package pt.ipp.estg.Utils;

import java.net.URL;

public class Resources {
    private static URL getFileURLFromResources(String fileName) {
        return Resources.class.getClassLoader().getResource(fileName);
    }

    private static String getAbsolutePathFromURL(URL fileURL) {
        return fileURL.getPath();
    }

    public static String getPathFromResources(String fileName) {
        URL fileURL = getFileURLFromResources(fileName);
        return getAbsolutePathFromURL(fileURL);
    }
}
