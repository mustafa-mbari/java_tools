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

public class XMLObjectIdAnalyzer {

    // Constant for XML file name
    private static final String XML_FILE_NAME = "C:\\Users\\g7ambam23a\\OneDrive - KUKA AG\\Desktop\\test\\lg.xml";

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
            System.err.println("Error processing XML file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printResults(Map<String, Integer> objectIdCounts, Map<String, String> objectIdToClassname) {
        System.out.println("=====================================");
        System.out.println("    DUPLICATED OBJECT ID ANALYSIS    ");
        System.out.println("=====================================");
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
                System.out.printf("┌─ ObjectId: %s%n", objectId);
                System.out.printf("├─ Classname: %s%n", classname);
                System.out.printf("├─ Count: %d times%n", count);
                System.out.printf("└─ Status: %s%n", getStatusMessage(count, classname));
                System.out.println();
            }
        }

        if (!foundDuplicates) {
            System.out.println("No duplicated ObjectIds found meeting the specified criteria.");
            System.out.println();
            System.out.println("Criteria applied:");
            System.out.println("• DistanceSensor/ConveyorGroup: Must appear more than 3 times");
            System.out.println("• All other classes: Must appear more than 1 time");
        }

        System.out.println("=====================================");
        System.out.printf("Total unique ObjectIds: %d%n", objectIdCounts.size());
        System.out.printf("Total duplicated ObjectIds shown: %d%n", totalDuplicatesShown);
        System.out.println("=====================================");
    }

    private static String getStatusMessage(int count, String classname) {
        if ("DistanceSensor".equals(classname) || "ConveyorGroup".equals(classname)) {
            return String.format("DUPLICATE (Special class - threshold: >3, found: %d)", count);
        } else {
            return String.format("DUPLICATE (Regular class - threshold: >1, found: %d)", count);
        }
    }


}