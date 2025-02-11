package it.uniroma1.plannertests;

import org.apache.commons.cli.*;
import org.json.JSONObject;
import it.uniroma1.plannertests.writer.*;
import it.uniroma1.plannertests.model.Museo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;

public class CommandLineApp {

    public static void run(String[] args) {
        // Command-line mode
        Options options = new Options();

        Option outputOption = Option.builder("o")
                .longOpt("output")
                .hasArg()
                .desc("Directory where to save the PDDL files")
                .required()
                .build();
        options.addOption(outputOption);

        // Define other options similarly, setting required() where appropriate
        options.addOption(Option.builder("r").longOpt("rooms").hasArg().desc("Number of rooms").build());
        options.addOption(
                Option.builder("a").longOpt("attractions").hasArg().desc("Number of attractions").build());
        options.addOption(
                Option.builder("l").longOpt("links").hasArg().desc("Number of links between rooms").build());
        options.addOption(
                Option.builder("v").longOpt("visits").hasArg().desc("Number of attractions to visit").required()
                        .build());
        // options.addOption(Option.builder("i").longOpt("input").hasArg().desc("Input
        // museum.properties file").build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);

            String outputDirectory = cmd.getOptionValue("output");
            // String inputFilePath = cmd.getOptionValue("input");

            Museo museo;
            int numberOfVisits = Integer.parseInt(cmd.getOptionValue("visits"));
            // if (inputFilePath != null) {
            // museo = Museo.getInstanceFromFile(inputFilePath);
            // if (numberOfVisits > 0) {
            // generatePddlFiles(museo, numberOfVisits, outputDirectory);
            // } else {
            // System.err.println("Invalid number of visits: " + numberOfVisits);
            // System.exit(1);
            // }
            // } else
            if (
            // inputFilePath == null &&
            cmd.getOptionValue("rooms") == null ||
                    cmd.getOptionValue("attractions") == null ||
                    cmd.getOptionValue("links") == null) {
                System.err.println("Missing required options: rooms, attractions, links");
                formatter.printHelp("java -jar PlannerTests.jar", options);
                System.exit(1);
            } else {
                int numberOfRooms = Integer.parseInt(cmd.getOptionValue("rooms"));
                System.out.println("numberOfRooms: " + numberOfRooms);
                int numberOfAttractions = Integer.parseInt(cmd.getOptionValue("attractions"));
                System.out.println("numberOfAttractions: " + numberOfAttractions);
                int numberOfLinks = Integer.parseInt(cmd.getOptionValue("links"));
                System.out.println("numberOfLinks: " + numberOfLinks);
                if (numberOfRooms > 0 && numberOfAttractions > 0 && numberOfLinks > 0 && numberOfLinks < 4
                        && numberOfVisits > 0) {
                    System.out.println("Creating museum with " + numberOfRooms + " rooms, " + numberOfAttractions
                            + " attractions, " + numberOfLinks + " links, and " + numberOfVisits + " visits.");
                    museo = Museo.getInstance(numberOfLinks, numberOfRooms, numberOfAttractions);
                    generatePddlFiles(museo, numberOfVisits, outputDirectory);
                } else {
                    System.err.println("Invalid number of rooms, attractions, links, or visits.");
                    System.exit(1);
                }
            }
        } catch (MissingOptionException e) {
            System.err.println("Missing required options: " + e.getMissingOptions());
            formatter.printHelp("java -jar PlannerTests.jar", options);
            System.exit(1);
        } catch (ParseException e) {
            System.err.println("Failed to parse command-line arguments.");
            formatter.printHelp("java -jar PlannerTests.jar", options);
            System.exit(1);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generatePddlFiles(Museo museo, int numberOfVisits, String outputDirectory) throws IOException {
        WriteTimer timer = new WriteTimer();

        // Parametric PDDL
        timer.start();
        ParametricPddlWriter parametricWriter = new ParametricPddlWriter(museo, numberOfVisits, outputDirectory);
        parametricWriter.writeDomain();
        String outputPathParametric = parametricWriter.writeProblem();
        long duration = timer.stop();
        System.out.println(
                "Parametric PDDL files generated successfully in " + duration + "ms and saved in "
                        + outputPathParametric);
        writeGenerationInfo(parametricWriter.getFolderPath(), "parametric", museo, numberOfVisits, duration);

        // Grounded PDDL
        timer.start();
        PddlGroundedWriter groundedWriter = new PddlGroundedWriter(museo, numberOfVisits, outputDirectory);
        groundedWriter.writeDomain();
        String outputPathGroundedOld = groundedWriter.writeProblem();
        duration = timer.stop();
        System.out.println(
                "Grounded PDDL files generated successfully in " + duration + "ms and saved in "
                        + outputPathGroundedOld);
        writeGenerationInfo(groundedWriter.getFolderPath(), "old", museo, numberOfVisits, duration);

        // New Grounded PDDL
        timer.start();
        NewPddlWriter newWriter = new NewPddlWriter(museo, numberOfVisits, outputDirectory);
        newWriter.writeDomain();
        String outputPathGroundedNew = newWriter.writeProblem();
        duration = timer.stop();
        System.out.println(
                "New Grounded PDDL files generated successfully in " + duration + "ms and saved in "
                        + outputPathGroundedNew);
        writeGenerationInfo(newWriter.getFolderPath(), "new", museo, numberOfVisits, duration);
    }

    private static void writeGenerationInfo(String pddlOutputPath, String type, Museo museo, int visite, long duration)
            throws IOException {
        // Define the generation_info.json file path
        File jsonFile = new File(pddlOutputPath, "generation_info.json");

        // Create a new JSONObject and add the generation time
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("type", type);
        jsonObj.put("number_of_rooms", museo.getOpenRooms());
        jsonObj.put("number_of_attractions", museo.getNumeroAttrazioni());
        jsonObj.put("number_of_links", museo.getCollegamenti());
        jsonObj.put("number_of_visits", visite);
        jsonObj.put("generation_time_ms", duration);

        // Write the JSON object to generation_info.json using FileWriter
        try (FileWriter fileWriter = new FileWriter(jsonFile)) {
            fileWriter.write(jsonObj.toString(4)); // Indent with 4 spaces for readability
        }

        System.out.println("Generation info written to " + jsonFile.getAbsolutePath());
    }

}
