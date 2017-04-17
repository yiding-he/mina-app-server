package com.hyd.appserver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;

/**
 * (description)
 *
 * @author yiding.he
 */
@SuppressWarnings({"unchecked"})
public class DefaultClassHelper implements ClassHelper {

    static final Logger log = LoggerFactory.getLogger(DefaultClassHelper.class);

    public <T> List<Class<T>> findClasses(Class<T> iface, String... packageNames) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return findClasses(classLoader, iface, packageNames);
    }

    public <T> List<Class<T>> findClasses(ClassLoader classLoader, Class<T> iface, String... packageNames) {

        List<String> packages = new ArrayList<String>(Arrays.asList(packageNames));

        List<Class<T>> result = new ArrayList<Class<T>>();
        URL[] classPaths = ((java.net.URLClassLoader) classLoader).getURLs();

        for (URL classPathUrl : classPaths) {
            Enumeration files = null;
            JarFile module = null;

            // for each classpath ...
            File classPath;
            try {
                classPath = new File(classPathUrl.toURI());
            } catch (URISyntaxException e) {
                log.error("Skipping classPath '" + classPathUrl + "'", e);
                continue;
            }

            if (!classPath.exists()) {
                continue;
            }

            if (classPath.isDirectory()) {   // is our classpath a directory and jar filters are not active?
                List<String> dirListing = new ArrayList<String>();
                // get a recursive listing of this classpath
                recursivelyListDir(dirListing, classPath, new StringBuffer());
                // an enumeration wrapping our list of files
                files = Collections.enumeration(dirListing);
            } else if (classPath.getName().endsWith(".jar")) {    // is our classpath a jar?
                try {
                    // if our resource is a jar, instantiate a jarfile using the full path to resource
                    module = new JarFile(classPath);
                    // get an enumeration of the files in this jar
                    files = module.entries();
                } catch (MalformedURLException e) {
                    log.error("Skipping classPath '" + classPathUrl + "'", e);
                } catch (IOException e) {
                    log.error("Skipping classPath '" + classPathUrl + "'", e);
                }
            }

            // for each file path in our directory or jar
            while (files != null && files.hasMoreElements()) {

                // get each fileName
                String fileName = files.nextElement().toString();

                // we only want the class files
                if (fileName.endsWith(".class") && !fileName.contains("$")) {
                    // convert our full filename to a fully qualified class name
                    String className = fileName.replaceAll("/", ".").substring(0, fileName.length() - 6);
                    // skip any classes in packages not explicitly requested in our package filter          
                    if (!packages.isEmpty() && !isInPackages(packages, className)) {
                        continue;
                    }

                    // get the class for our class name
                    Class theClass;
                    try {
                        theClass = Class.forName(className, false, classLoader);
                    } catch (NoClassDefFoundError e) {
                        log.error("Skipping class '" + className + "'", e.getMessage());
                        continue;
                    } catch (ClassNotFoundException e) {
                        log.error("Skipping class '" + className + "'", e.getMessage());
                        continue;
                    }

                    if (!result.contains(theClass) && iface.isAssignableFrom(theClass)) {
                        result.add(theClass);
                    }
                }
            }

            // close the jar if it was used
            if (module != null) {
                try {
                    module.close();
                } catch (IOException ioe) {
                    // nothing to do
                }
            }

        } // end for loop

        return result;
    } // end method

    private static boolean isInPackages(List<String> packages, String className) {
        String classPackage = getPackageName(className);
        for (String p : packages) {
            if (classPackage.startsWith(p)) {
                return true;
            }
        }
        return false;
    }

    private static String getPackageName(String className) {
        if (!className.contains(".")) {
            return className;
        }
        return className.substring(0, className.lastIndexOf("."));
    }

    /**
     * Recursively lists a directory while generating relative paths. This is a helper function for findClasses.
     * Note: Uses a StringBuffer to avoid the excessive overhead of multiple String concatentation
     *
     * @param dirListing   A list variable for storing the directory listing as a list of Strings
     * @param dir          A File for the directory to be listed
     * @param relativePath A StringBuffer used for building the relative paths
     */
    private static void recursivelyListDir(List<String> dirListing, File dir, StringBuffer relativePath) {
        int prevLen; // used to undo append operations to the StringBuffer

        // if the dir is really a directory 
        if (dir.isDirectory()) {
            // get a list of the files in this directory
            File[] files = dir.listFiles();
            // for each file in the present dir
            for (File file : files) {
                // store our original relative path string length
                prevLen = relativePath.length();
                // call this function recursively with file list from present
                // dir and relateveto appended with present dir
                recursivelyListDir(dirListing, file, relativePath.append(prevLen == 0 ? "" : "/").append(file.getName()));
                //  delete subdirectory previously appended to our relative path
                relativePath.delete(prevLen, relativePath.length());
            }
        } else {
            // this dir is a file; append it to the relativeto path and add it to the directory listing
            dirListing.add(relativePath.toString());
        }
    }
}
