package com.visu.layout;
// Constant for XML file name
//private static final String XML_FILE_NAME = "C:\\Users\\g7ambam23a\\OneDrive - KUKA AG\\Desktop\\test\\First-Floor.xml";

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XMLObjectIdAnalyzer {

    private static final Logger logger = Logger.getLogger(XMLObjectIdAnalyzer.class.getName());

    // Constant for XML file name
    private static final String XML_FILE_NAME = "C:\\Users\\g7ambam23a\\OneDrive - KUKA AG\\Desktop\\test\\First-Floor.xml";

    // ANSI Color codes for terminal output
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";
    private static final String BOLD = "\u001B[1m";
    private static final String BRIGHT_RED = "\u001B[91m";
    private static final String BRIGHT_GREEN = "\u001B[92m";
    private static final String BRIGHT_YELLOW = "\u001B[93m";
    private static final String BRIGHT_BLUE = "\u001B[94m";
    private static final String BRIGHT_PURPLE = "\u001B[95m";
    private static final String BRIGHT_CYAN = "\u001B[96m";
    private static final String BRIGHT_WHITE = "\u001B[97m";

    public static void main(String[] args) {
        try {
            // Parse XML file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(XML_FILE_NAME));

            // Get all ViewObject elements
            NodeList viewObjects = document.getElementsByTagName("ViewObject");

            // Maps to store ObjectId counts and their corresponding classnames
            Map<String, Integer> objectIdCounts = new HashMap<>();
            Map<String, String> objectIdToClassname = new HashMap<>();

            // Process each ViewObject
            for (int i = 0; i < viewObjects.getLength(); i++) {
                Element viewObject = (Element) viewObjects.item(i);
                String classname = viewObject.getAttribute("classname");

                // Find PROPERTY element with name="ObjectId"
                NodeList properties = viewObject.getElementsByTagName("PROPERTY");
                for (int j = 0; j < properties.getLength(); j++) {
                    Element property = (Element) properties.item(j);
                    if ("ObjectId".equals(property.getAttribute("name"))) {
                        String objectId = property.getAttribute("value");

                        // Count occurrences
                        objectIdCounts.put(objectId, objectIdCounts.getOrDefault(objectId, 0) + 1);
                        objectIdToClassname.put(objectId, classname);
                        break;
                    }
                }
            }

            // Print results
            printResults(objectIdCounts, objectIdToClassname);

        } catch (Exception e) {
            System.err.println(BRIGHT_RED + "Error processing XML file: " + e.getMessage() + RESET);
            logger.log(Level.SEVERE, "Exception occurred while processing XML file", e);
        }
    }

    private static void printResults(Map<String, Integer> objectIdCounts, Map<String, String> objectIdToClassname) {
        System.out.println(BOLD + BRIGHT_CYAN + "=====================================" + RESET);
        System.out.println(BOLD + BRIGHT_CYAN + "    DUPLICATED OBJECT ID ANALYSIS    " + RESET);
        System.out.println(BOLD + BRIGHT_CYAN + "=====================================" + RESET);
        System.out.println();

        // Use TreeMap to sort results alphabetically
        Map<String, Integer> sortedCounts = new TreeMap<>(objectIdCounts);

        boolean foundDuplicates = false;
        int totalDuplicatesShown = 0;

        for (Map.Entry<String, Integer> entry : sortedCounts.entrySet()) {
            String objectId = entry.getKey();
            int count = entry.getValue();
            String classname = objectIdToClassname.get(objectId);

            boolean shouldPrint = false;

            // Apply different rules based on classname
            if ("DistanceSensor".equals(classname) || "ConveyorGroup".equals(classname)) {
                // For DistanceSensor and ConveyorGroup, show only if count > 3
                if (count > 3) {
                    shouldPrint = true;
                }
            } else {
                // For all other classnames, show if count > 1 (duplicated)
                if (count > 1) {
                    shouldPrint = true;
                }
            }

            if (shouldPrint) {
                foundDuplicates = true;
                totalDuplicatesShown++;
                System.out.printf(BLUE + "┌─ " + BRIGHT_YELLOW + "ObjectId: " + BRIGHT_GREEN + "%s" + RESET + "%n", objectId);
                System.out.printf(BLUE + "├─ " + BRIGHT_PURPLE + "Classname: " + WHITE + "%s" + RESET + "%n", classname);
                System.out.printf(BLUE + "├─ " + CYAN + "Count: " + BRIGHT_RED + "%d times" + RESET + "%n", count);
                System.out.printf(BLUE + "└─ " + YELLOW + "Status: " + RESET + "%s" + RESET + "%n", getStatusMessage(count, classname));
                System.out.println();
            }
        }

        if (!foundDuplicates) {
            System.out.println(BRIGHT_RED + "No duplicated ObjectIds found meeting the specified criteria." + RESET);
            System.out.println();
            System.out.println(BRIGHT_YELLOW + "Criteria applied:" + RESET);
            System.out.println(CYAN + "• DistanceSensor/ConveyorGroup: Must appear more than 3 times" + RESET);
            System.out.println(CYAN + "• All other classes: Must appear more than 1 time" + RESET);
        }

        System.out.println(BOLD + BRIGHT_CYAN + "=====================================" + RESET);
        System.out.printf(GREEN + "Total unique ObjectIds: " + BRIGHT_WHITE + "%d" + RESET + "%n", objectIdCounts.size());
        System.out.printf(PURPLE + "Total duplicated ObjectIds shown: " + BRIGHT_WHITE + "%d" + RESET + "%n", totalDuplicatesShown);
        System.out.println(BOLD + BRIGHT_CYAN + "=====================================" + RESET);
    }

    private static String getStatusMessage(int count, String classname) {
        if ("DistanceSensor".equals(classname) || "ConveyorGroup".equals(classname)) {
            return BRIGHT_RED + "DUPLICATE " + RESET + WHITE + "(Special class - threshold: >3, found: " + BRIGHT_RED + count + WHITE + ")" + RESET;
        } else {
            return BRIGHT_GREEN + "DUPLICATE " + RESET + WHITE + "(Regular class - threshold: >1, found: " + BRIGHT_GREEN + count + WHITE + ")" + RESET;
        }
    }


}