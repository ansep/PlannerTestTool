/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.controller;

import it.uniroma1.plannertests.PlannerThread;
import it.uniroma1.plannertests.command.AbstractPlannerCommand;
import it.uniroma1.plannertests.model.Museo;
import it.uniroma1.plannertests.writer.AbstractWriter;
import it.uniroma1.plannertests.writer.NewPddlWriter;
import it.uniroma1.plannertests.writer.PddlGroundedWriter;
import it.uniroma1.plannertests.writer.ParametricPddlWriter;
import it.uniroma1.plannertests.writer.ReportWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

public class MainController implements Initializable {

    private String folderPath;
    private String domainPath;
    private String problemPath;

    @FXML
    private TextField numeroCollegamenti;
    @FXML
    private Button decrementaCollegamenti;
    @FXML
    private Button incrementaCollegamenti;
    @FXML
    private TextField numeroStanze;
    @FXML
    private Button decrementaStanze;
    @FXML
    private Button incrementaStanze;
    @FXML
    private Button decrementaAttrazioni;
    @FXML
    private Button incrementaAttrazioni;
    @FXML
    private TextField numeroAttrazioni;
    @FXML
    private Button decrementaVisite;
    @FXML
    private Button incrementaVisite;
    @FXML
    private TextField numeroVisite;
    @FXML
    private Button generaPddlOld;
    @FXML
    private Button ffOld;
    @FXML
    private Button symbaOld;
    @FXML
    private Button SymbaNew;
    @FXML
    private Button generaPddlNew;
    @FXML
    private Button ffNew;
    @FXML
    private Button generaPddlParametric;
    @FXML
    private TextField pddlPath;
    @FXML
    private Button pddlPathButton;
    @FXML
    private TextField symbaPath;
    @FXML
    private Button symbaPathButton;
    @FXML
    private TextField fdPath;
    @FXML
    private Button fdPathButton;
    @FXML
    private Button generaReportButton;
    @FXML
    private ProgressIndicator progress;

    private AbstractPlannerCommand command;

    public static Integer planners = 0b000;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void decrementaCollegamentiClicked(ActionEvent event) {
        if (incrementaCollegamenti.isDisable()) {
            incrementaCollegamenti.setDisable(false);
        }
        int collegamenti = Integer.parseInt(numeroCollegamenti.getText());
        if (collegamenti > 1) {
            collegamenti -= 1;
            if (collegamenti == 1)
                decrementaCollegamenti.setDisable(true);
            numeroCollegamenti.setText(Integer.toString(collegamenti));
        }
    }

    @FXML
    private void incrementaCollegamentiClicked(ActionEvent event) {
        if (decrementaCollegamenti.isDisable()) {
            decrementaCollegamenti.setDisable(false);
        }

        int collegamenti = Integer.parseInt(numeroCollegamenti.getText());
        if (collegamenti < 3) {
            collegamenti += 1;
            if (collegamenti == 3)
                incrementaCollegamenti.setDisable(true);
            numeroCollegamenti.setText(Integer.toString(collegamenti));
            decrementaStanze.setDisable(true);
            incrementaStanze.setDisable(false);
            numeroStanze.setText("5");
            decrementaAttrazioni.setDisable(true);
            incrementaAttrazioni.setDisable(false);
            numeroAttrazioni.setText("50");
            decrementaVisite.setDisable(true);
            incrementaVisite.setDisable(false);
            numeroVisite.setText("10");
        }
    }

    @FXML
    private void decrementaStanzeClicked(ActionEvent event) {
        if (incrementaStanze.isDisable()) {
            incrementaStanze.setDisable(false);
        }
        int stanze = Integer.parseInt(numeroStanze.getText());
        if (stanze > 5) {
            stanze -= 5;
            if (stanze == 5)
                decrementaStanze.setDisable(true);
            numeroStanze.setText(Integer.toString(stanze));
        }
    }

    @FXML
    private void incrementaStanzeClicked(ActionEvent event) {
        if (decrementaStanze.isDisable()) {
            decrementaStanze.setDisable(false);
        }

        int stanze = Integer.parseInt(numeroStanze.getText());
        if (stanze < 30) {
            stanze += 5;
            if (stanze == 30)
                incrementaStanze.setDisable(true);
            numeroStanze.setText(Integer.toString(stanze));
            decrementaAttrazioni.setDisable(true);
            incrementaAttrazioni.setDisable(false);
            numeroAttrazioni.setText("50");
            decrementaVisite.setDisable(true);
            incrementaVisite.setDisable(false);
            numeroVisite.setText("10");
        }
    }

    @FXML
    private void decrementaAttrazioniClicked(ActionEvent event) {
        if (incrementaAttrazioni.isDisable()) {
            incrementaAttrazioni.setDisable(false);
        }
        int attrazioni = Integer.parseInt(numeroAttrazioni.getText());
        if (attrazioni > 50) {
            attrazioni -= 50;
            if (attrazioni == 50)
                decrementaAttrazioni.setDisable(true);
            numeroAttrazioni.setText(Integer.toString(attrazioni));
        }
    }

    @FXML
    private void incrementaAttrazioniClicked(ActionEvent event) {
        if (decrementaAttrazioni.isDisable()) {
            decrementaAttrazioni.setDisable(false);
        }
        int attrazioni = Integer.parseInt(numeroAttrazioni.getText());
        if (attrazioni < 200) {
            attrazioni += 50;
            if (attrazioni == 200)
                incrementaAttrazioni.setDisable(true);
            numeroAttrazioni.setText(Integer.toString(attrazioni));
            decrementaVisite.setDisable(true);
            incrementaVisite.setDisable(false);
            numeroVisite.setText("10");
        }
    }

    @FXML
    private void decrementaVisiteClicked(ActionEvent event) {
        if (incrementaVisite.isDisable()) {
            incrementaVisite.setDisable(false);
        }
        int visite = Integer.parseInt(numeroVisite.getText());
        if (visite > 10) {
            visite -= 5;
            if (visite == 10)
                decrementaVisite.setDisable(true);
            numeroVisite.setText(Integer.toString(visite));
        }
    }

    @FXML
    private void incrementaVisiteClicked(ActionEvent event) {
        if (decrementaVisite.isDisable()) {
            decrementaVisite.setDisable(false);
        }
        int visite = Integer.parseInt(numeroVisite.getText());
        if (visite < 30) {
            visite += 5;
            if (visite == 30)
                incrementaVisite.setDisable(true);
            numeroVisite.setText(Integer.toString(visite));
        }
    }

    @FXML
    private void selectPddlPath(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Seleziona la cartella per i PDDL");
        File file = chooser.showDialog(pddlPathButton.getScene().getWindow());
        if (file != null && file.isDirectory()) {
            pddlPath.setText(file.getPath());
        }
    }

    @FXML
    private void selectFDPath(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Seleziona la cartella con gli script");
        File file = chooser.showDialog(pddlPathButton.getScene().getWindow());
        if (file != null && file.isDirectory()) {
            System.out.println(file.getPath());
            fdPath.setText(file.getPath());
        }
    }

    @FXML
    private void generaPddlOldClicked(ActionEvent event) {
        int collegamenti = Integer.parseInt(numeroCollegamenti.getText());
        int stanze = Integer.parseInt(numeroStanze.getText());
        int attrazioni = Integer.parseInt(numeroAttrazioni.getText());
        int visite = Integer.parseInt(numeroVisite.getText());
        Museo museo = Museo.getInstance(collegamenti, stanze, attrazioni);
        try {
            AbstractWriter writer = new PddlGroundedWriter(museo, visite, pddlPath.getText());
            folderPath = writer.getFolderPath();
            domainPath = writer.writeDomain();
            problemPath = writer.writeProblem();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ffOld.setDisable(false);
        symbaOld.setDisable(false);
        ffNew.setDisable(true);
        SymbaNew.setDisable(true);
        planners = 0b000;
        generaReportButton.setDisable(true);
        // numeroAttrazioni.setText("50");
    }

    @FXML
    private void ffOldClicked(ActionEvent event) {
        execute("FD_FF");
    }

    @FXML
    private void symbaOldClicked(ActionEvent event) {
        execute("SYMBA2");
    }

    @FXML
    private void generaPddlNewClicked(ActionEvent event) {
        int collegamenti = Integer.parseInt(numeroCollegamenti.getText());
        int stanze = Integer.parseInt(numeroStanze.getText());
        int attrazioni = Integer.parseInt(numeroAttrazioni.getText());
        int visite = Integer.parseInt(numeroVisite.getText());
        Museo museo = Museo.getInstance(collegamenti, stanze, attrazioni);
        try {
            AbstractWriter writer = new NewPddlWriter(museo, visite, pddlPath.getText());
            folderPath = writer.getFolderPath();
            domainPath = writer.writeDomain();
            problemPath = writer.writeProblem();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ffNew.setDisable(false);
        SymbaNew.setDisable(false);
        ffOld.setDisable(true);
        symbaOld.setDisable(true);
        planners = 0b000;
        generaReportButton.setDisable(true);
        // numeroAttrazioni.setText("50");
    }

    @FXML
    private void ffNewClicked(ActionEvent event) {
        execute("FD_FF");
    }

    @FXML
    private void SymbaNewClicked(ActionEvent event) {
        execute("SYMBA2");
    }

    @FXML
    private void generaPddlParametricClicked(ActionEvent event) {
        int collegamenti = Integer.parseInt(numeroCollegamenti.getText());
        int stanze = Integer.parseInt(numeroStanze.getText());
        int attrazioni = Integer.parseInt(numeroAttrazioni.getText());
        int visite = Integer.parseInt(numeroVisite.getText());
        System.out.println(collegamenti + " " + stanze + " " + attrazioni + " " + visite);
        Museo museo = Museo.getInstance(collegamenti, stanze, attrazioni);
        try {
            AbstractWriter writer = new ParametricPddlWriter(museo, visite, pddlPath.getText());
            folderPath = writer.getFolderPath();
            domainPath = writer.writeDomain();
            problemPath = writer.writeProblem();
        } catch (IOException e) {
            e.printStackTrace();
        }
        planners = 0b000;
        generaReportButton.setDisable(true);
        // numeroAttrazioni.setText("50");
    }

    private void execute(String planner) {
        String path = fdPath.getText();
        System.out.println(path);
        String exec = "/run_" + planner;
        StringBuilder sb = new StringBuilder();
        sb.append(path)
                .append(exec).append(" ")
                .append(domainPath).append(" ")
                .append(problemPath);
        System.out.println(sb.toString());

        PlannerThread thread = new PlannerThread(sb.toString(), planner, folderPath, generaReportButton, progress);
        thread.start();
    }

    @FXML
    private void generaReport(ActionEvent event) {
        new ReportWriter(folderPath).writeReport();
    }

    /*
     * private void enableReport() {
     * if(fd && symba2)
     * generaReportButton.setDisable(false);
     * }
     */

}
