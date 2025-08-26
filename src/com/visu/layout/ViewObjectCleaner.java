package com.visu.layout;

import java.io.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ViewObjectCleaner {
    private static final Logger logger = Logger.getLogger(ViewObjectCleaner.class.getName());

    public static void main(String[] args) {
        String inputFile = "C:\\Users\\g7ambam23a\\OneDrive - KUKA AG\\Desktop\\ViewObjectCleaner\\First-Floor.xml";
        String outputFile = "C:\\Users\\g7ambam23a\\OneDrive - KUKA AG\\Desktop\\ViewObjectCleaner\\output_First-Floor.xml";
        String outputFileOfDeleted = "C:\\Users\\g7ambam23a\\OneDrive - KUKA AG\\Desktop\\ViewObjectCleaner\\output_DeletedViewObjects_First-Floor.xml";

        StringBuilder outputBuilder = new StringBuilder();
        StringBuilder deletedBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            boolean insideViewObject = false;
            boolean isToDelete = false;
            StringBuilder currentViewObject = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.contains("<ViewObject")) {
                    insideViewObject = true;
                    isToDelete = line.contains("classname=\"MonorailBlock");
                    currentViewObject.setLength(0); // reset buffer
                }

                if (insideViewObject) {
                    currentViewObject.append(line).append(System.lineSeparator());

                    if (line.contains("</ViewObject>")) {
                        insideViewObject = false;

                        if (isToDelete) {
                            deletedBuilder.append(currentViewObject);
                        } else {
                            outputBuilder.append(currentViewObject);
                        }
                    }
                } else {
                    outputBuilder.append(line).append(System.lineSeparator());
                }
            }

            // Save results
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                writer.write(outputBuilder.toString());
            }

            try (BufferedWriter writerDeleted = new BufferedWriter(new FileWriter(outputFileOfDeleted))) {
                writerDeleted.write(deletedBuilder.toString());
            }

            System.out.println("Cleaning done successfully.");

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error during cleaning process", e);
        }
    }
}
