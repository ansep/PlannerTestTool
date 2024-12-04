package it.uniroma1.plannertests;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0) {
            // Command-line mode
            CommandLineApp.run(args);
        } else {
            // GUI mode
            Application.launch(MainApp.class, args);
        }
    }
}
