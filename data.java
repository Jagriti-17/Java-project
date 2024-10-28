package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.utils.sql;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.chart.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Student Performance Analyzer");

        // Sidebar with navigation buttons (styled like a navigation bar)
        VBox sidebar = new VBox();
        sidebar.setSpacing(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Button btnAddStudent = new Button("Add Student");
        Button btnAddScores = new Button("Add Scores");
        Button btnViewPerformance = new Button("View Performance");
        Button btnRecommendations = new Button("Recommendations");
        Button btnViewPerformanceByExam = new Button("View Performance by Exam Type");  // New Button

        // Styling for sidebar buttons
        String buttonStyle = "-fx-background-color: #34495e; -````````   fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px;";
        btnAddStudent.setStyle(buttonStyle);
        btnAddScores.setStyle(buttonStyle);
        btnViewPerformance.setStyle(buttonStyle);
        btnRecommendations.setStyle(buttonStyle);
        btnViewPerformanceByExam.setStyle(buttonStyle);  // Style for new button

        sidebar.getChildren().addAll(btnAddStudent, btnAddScores, btnViewPerformance, btnRecommendations, btnViewPerformanceByExam); // Added new button

        // Main content area
        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(sidebar);
        mainLayout.setPadding(new Insets(20));

        // Enhanced Add Student Form
        GridPane addStudentForm = new GridPane();
        addStudentForm.setHgap(10);
        addStudentForm.setVgap(10);

        Label studentIDLabel = new Label("Student ID:");
        TextField studentIDField = new TextField();
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label departmentLabel = new Label("Department:");
        TextField departmentField = new TextField();
        Label yearLabel = new Label("Year:");
        ComboBox<String> yearComboBox = new ComboBox<>();
        yearComboBox.getItems().addAll("1st Year", "2nd Year", "3rd Year", "4th Year");

        Button submitStudentBtn = new Button("Add Student");

        addStudentForm.add(studentIDLabel, 0, 0);
        addStudentForm.add(studentIDField, 1, 0);
        addStudentForm.add(nameLabel, 0, 1);
        addStudentForm.add(nameField, 1, 1);
        addStudentForm.add(emailLabel, 0, 2);
        addStudentForm.add(emailField, 1, 2);
        addStudentForm.add(departmentLabel, 0, 3);
        addStudentForm.add(departmentField, 1, 3);
        addStudentForm.add(yearLabel, 0, 4);
        addStudentForm.add(yearComboBox, 1, 4);
        addStudentForm.add(submitStudentBtn, 1, 5);

        // Enhanced Add Score Form
        GridPane addScoreForm = new GridPane();
        addScoreForm.setHgap(10);
        addScoreForm.setVgap(10);

        Label studentLabel = new Label("Student:");
        ComboBox<String> studentDropdown = new ComboBox<>();
        studentDropdown.getItems().addAll("Student 1", "Student 2", "Student 3");  // Sample students
        Label subjectLabel = new Label("Subject:");
        TextField subjectField = new TextField();
        Label examTypeLabel = new Label("Exam Type:");
        ComboBox<String> examTypeComboBox = new ComboBox<>();
        examTypeComboBox.getItems().addAll("Assignment", "Quiz", "Test", "Final Exam");

        Label scoreLabel = new Label("Score:");
        TextField scoreField = new TextField();

        Button submitScoreBtn = new Button("Submit Score");

        addScoreForm.add(studentLabel, 0, 0);
        addScoreForm.add(studentDropdown, 1, 0);
        addScoreForm.add(subjectLabel, 0, 1);
        addScoreForm.add(subjectField, 1, 1);
        addScoreForm.add(examTypeLabel, 0, 2);
        addScoreForm.add(examTypeComboBox, 1, 2);
        addScoreForm.add(scoreLabel, 0, 3);
        addScoreForm.add(scoreField, 1, 3);
        addScoreForm.add(submitScoreBtn, 1, 4);

        // Bar chart for individual performance
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Subjects");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Scores");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Student Performance Overview");

        // Recommendations View
        VBox recommendationBox = new VBox();
        recommendationBox.setSpacing(10);
        recommendationBox.setPadding(new Insets(20));
        Label recommendationsTitle = new Label("Recommendations:");
        Label recommendation1 = new Label("Math: Practice more algebra.");
        Label recommendation2 = new Label("Science: Focus on understanding physics concepts.");
        recommendationBox.getChildren().addAll(recommendationsTitle, recommendation1, recommendation2);

        // Performance Chart based on Exam Type
        ComboBox<String> examTypeSelection = new ComboBox<>();
        examTypeSelection.getItems().addAll("Assignment", "Quiz", "Test", "Final Exam");

        Button showPerformanceByExamBtn = new Button("Show Performance");
        VBox performanceByExamBox = new VBox(10);
        performanceByExamBox.setAlignment(Pos.CENTER);
        performanceByExamBox.getChildren().addAll(new Label("Select Exam Type:"), examTypeSelection, showPerformanceByExamBtn);

        // Comparison Chart
        VBox comparisonBox = new VBox(10);
        comparisonBox.setAlignment(Pos.CENTER);
        ComboBox<String> studentComparisonDropdown = new ComboBox<>();
        studentComparisonDropdown.getItems().addAll("Student 1", "Student 2", "Student 3");  // Sample students for comparison
        Button showComparisonBtn = new Button("Show Comparison");
        comparisonBox.getChildren().addAll(new Label("Select Student for Comparison:"), studentComparisonDropdown, showComparisonBtn, barChart);

        // Button actions for switching views
        btnAddStudent.setOnAction(e -> mainLayout.setCenter(addStudentForm));
        btnAddScores.setOnAction(e -> mainLayout.setCenter(addScoreForm));
        btnViewPerformance.setOnAction(e -> mainLayout.setCenter(comparisonBox)); // Switch to comparison view
        btnRecommendations.setOnAction(e -> mainLayout.setCenter(recommendationBox));
        btnViewPerformanceByExam.setOnAction(e -> mainLayout.setCenter(performanceByExamBox));

        // Chart generation based on selected exam type
        showPerformanceByExamBtn.setOnAction(e -> {
            String selectedExamType = examTypeSelection.getValue();
            if (selectedExamType != null) {
                XYChart.Series<String, Number> examTypeSeries = new XYChart.Series<>();
                examTypeSeries.setName(selectedExamType);

                // Sample data for different exam types (can be replaced with real data)
                if (selectedExamType.equals("Assignment")) {
                    examTypeSeries.getData().add(new XYChart.Data<>("Math", 85));
                    examTypeSeries.getData().add(new XYChart.Data<>("Science", 90));
                    examTypeSeries.getData().add(new XYChart.Data<>("English", 95));
                } else if (selectedExamType.equals("Quiz")) {
                    examTypeSeries.getData().add(new XYChart.Data<>("Math", 78));
                    examTypeSeries.getData().add(new XYChart.Data<>("Science", 80));
                    examTypeSeries.getData().add(new XYChart.Data<>("English", 88));
                } else if (selectedExamType.equals("Test")) {
                    examTypeSeries.getData().add(new XYChart.Data<>("Math", 92));
                    examTypeSeries.getData().add(new XYChart.Data<>("Science", 75));
                    examTypeSeries.getData().add(new XYChart.Data<>("English", 82));
                } else if (selectedExamType.equals("Final Exam")) {
                    examTypeSeries.getData().add(new XYChart.Data<>("Math", 88));
                    examTypeSeries.getData().add(new XYChart.Data<>("Science", 85));
                    examTypeSeries.getData().add(new XYChart.Data<>("English", 90));
                }

                // Create a new BarChart with the selected color
                BarChart<String, Number> examTypeChart = new BarChart<>(xAxis, yAxis);
                examTypeChart.setTitle("Performance in " + selectedExamType);
                examTypeChart.getData().add(examTypeSeries);
                
                // Change chart color to bluish
                for (XYChart.Data<String, Number> data : examTypeSeries.getData()) {
                    data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            newNode.setStyle("-fx-bar-fill: #3498db;");  // Bluish color
                        }
                    });
                }

                // Show the chart in the main layout
                mainLayout.setCenter(examTypeChart);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please select an exam type!");
                alert.show();
            }
        });

        // Show comparison chart based on selected student
        showComparisonBtn.setOnAction(e -> {
            String selectedStudent = studentComparisonDropdown.getValue();
            if (selectedStudent != null) {
                XYChart.Series<String, Number> comparisonSeries = new XYChart.Series<>();
                comparisonSeries.setName(selectedStudent);

                // Sample data for comparison (can be replaced with real data)
                if (selectedStudent.equals("Student 1")) {
                    comparisonSeries.getData().add(new XYChart.Data<>("Math", 85));
                    comparisonSeries.getData().add(new XYChart.Data<>("Science", 90));
                    comparisonSeries.getData().add(new XYChart.Data<>("English", 95));
                } else if (selectedStudent.equals("Student 2")) {
                    comparisonSeries.getData().add(new XYChart.Data<>("Math", 78));
                    comparisonSeries.getData().add(new XYChart.Data<>("Science", 80));
                    comparisonSeries.getData().add(new XYChart.Data<>("English", 88));
                } else if (selectedStudent.equals("Student 3")) {
                    comparisonSeries.getData().add(new XYChart.Data<>("Math", 92));
                    comparisonSeries.getData().add(new XYChart.Data<>("Science", 75));
                    comparisonSeries.getData().add(new XYChart.Data<>("English", 82));
                }

                barChart.getData().clear();  // Clear previous data
                barChart.getData().add(comparisonSeries);  // Add the new comparison data
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please select a student for comparison!");
                alert.show();
            }
        });

     // MenuBar
     MenuBar menuBar = new MenuBar();

     // File Menu
     Menu fileMenu = new Menu("File");
     MenuItem newItem = new MenuItem("New");
     MenuItem saveItem = new MenuItem("Save");
     MenuItem openItem = new MenuItem("Open");
     MenuItem exportItem = new MenuItem("Export");
     MenuItem printItem = new MenuItem("Print");
     MenuItem preferencesItem = new MenuItem("Preferences");
     MenuItem exitItem = new MenuItem("Exit");
     fileMenu.getItems().addAll(newItem, saveItem, openItem, exportItem, printItem, preferencesItem, new SeparatorMenuItem(), exitItem);

     // Edit Menu
     Menu editMenu = new Menu("Edit");
     MenuItem undoItem = new MenuItem("Undo");
     MenuItem redoItem = new MenuItem("Redo");
     MenuItem cutItem = new MenuItem("Cut");
     MenuItem copyItem = new MenuItem("Copy");
     MenuItem pasteItem = new MenuItem("Paste");
     MenuItem deleteItem = new MenuItem("Delete");
     editMenu.getItems().addAll(undoItem, redoItem, new SeparatorMenuItem(), cutItem, copyItem, pasteItem, new SeparatorMenuItem(), deleteItem);

     // View Menu
     Menu viewMenu = new Menu("View");
     MenuItem zoomInItem = new MenuItem("Zoom In");
     MenuItem zoomOutItem = new MenuItem("Zoom Out");
     MenuItem fullScreenItem = new MenuItem("Full Screen");
     MenuItem themeItem = new MenuItem("Switch Theme");
     viewMenu.getItems().addAll(zoomInItem, zoomOutItem, new SeparatorMenuItem(), fullScreenItem, themeItem);

     // Help Menu
     Menu helpMenu = new Menu("Help");
     MenuItem userGuideItem = new MenuItem("User Guide");
     MenuItem checkForUpdatesItem = new MenuItem("Check for Updates");
     MenuItem aboutItem = new MenuItem("About");
     helpMenu.getItems().addAll(userGuideItem, checkForUpdatesItem, aboutItem);

     // Tools Menu
     Menu toolsMenu = new Menu("Tools");
     MenuItem statisticsItem = new MenuItem("Statistics");
     MenuItem importDataItem = new MenuItem("Import Data");
     MenuItem clearDataItem = new MenuItem("Clear Data");
     MenuItem backupDataItem = new MenuItem("Backup Data");
     toolsMenu.getItems().addAll(statisticsItem, importDataItem, clearDataItem, backupDataItem);

     // Adding all menus to the MenuBar
     menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, toolsMenu, helpMenu);

     mainLayout.setTop(menuBar);

        // Footer
        Label footerLabel = new Label("Version 1.0 ");
        footerLabel.setAlignment(Pos.CENTER);
        mainLayout.setBottom(footerLabel);

        // Setting the main layout
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}







//Populate the student dropdown with student IDs from the database
private void populateStudentDropdown(ComboBox<String> studentDropdown) {
    ObservableList<String> studentIDs = FXCollections.observableArrayList();

    try (Connection conn = sql.getConnection()) {
        String query = "SELECT studentID FROM students";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                studentIDs.add(rs.getString("studentID"));
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    studentDropdown.setItems(studentIDs);
}

// Implementation for View Performance by Exam Type
private VBox createPerformanceViewForm() {
    VBox viewPerformanceForm = new VBox();
    viewPerformanceForm.setSpacing(10);

    studentLabel = new Label("Select Student:");
    studentDropdown = new ComboBox<>();
    populateStudentDropdown(studentDropdown);

    examTypeLabel = new Label("Select Exam Type:");
    examTypeDropdown = new ComboBox<>();
    examTypeDropdown.getItems().addAll("Assignment", "Quiz", "Test", "Final Exam");

    viewPerformanceBtn = new Button("View Performance");

    viewPerformanceForm.getChildren().addAll(studentLabel, studentDropdown, examTypeLabel, examTypeDropdown, viewPerformanceBtn);

    viewPerformanceBtn.setOnAction(e -> {
        if (studentDropdown.getValue() == null || examTypeDropdown.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select both a student and an exam type.");
            alert.show();
            return;
        }

        viewPerformanceByExamType(studentDropdown.getValue(), examTypeDropdown.getValue());
    });

    return viewPerformanceForm;
}


// Method to view performance by exam type and handle multiple exam entries for the same type
private void viewPerformanceByExamType(String studentID, String examType) {
    try (Connection conn = sql.getConnection()) {
        String query = "SELECT AVG(maths) AS avgMaths, AVG(english) AS avgEnglish, AVG(science) AS avgScience, AVG(it) AS avgIT FROM scores WHERE studentID = ? AND examType = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, studentID);
            ps.setString(2, examType);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double avgMathsScore = rs.getDouble("avgMaths");
                double avgEnglishScore = rs.getDouble("avgEnglish");
                double avgScienceScore = rs.getDouble("avgScience");
                double avgITScore = rs.getDouble("avgIT");

                displayPerformanceChart(avgMathsScore, avgEnglishScore, avgScienceScore, avgITScore, studentID, examType);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No scores found for this exam type.");
                alert.show();
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}


// Display performance chart based on scores
private void displayPerformanceChart(double mathsScore, double englishScore, double scienceScore, double itScore, String studentID, String examType) {
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis(0, 100, 10);  // Adjust the range from 0 to 100 for percentages

    xAxis.setLabel("Subjects");
    yAxis.setLabel("Percentage Score (%)");

    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    barChart.setTitle("Performance for " + examType + " (Student ID: " + studentID + ")");

    XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
    dataSeries.setName("Scores");

    dataSeries.getData().add(new XYChart.Data<>("Maths", mathsScore));
    dataSeries.getData().add(new XYChart.Data<>("English", englishScore));
    dataSeries.getData().add(new XYChart.Data<>("Science", scienceScore));
    dataSeries.getData().add(new XYChart.Data<>("IT", itScore));

    barChart.getData().add(dataSeries);

    // Apply colors after chart rendering
    for (XYChart.Data<String, Number> dataPoint : dataSeries.getData()) {
        dataPoint.nodeProperty().addListener((observable, oldValue, newValue) -> {
            // Assign different colors based on the exam type
            String color;
            switch (examType) {
                case "Assignment":
                    color = "#3498db";  // Blue
                    break;
                case "Quiz":
                    color = "#2ecc71";  // Green
                    break;
                case "Test":
                    color = "#e74c3c";  // Red
                    break;
                case "Final Exam":
                    color = "#f1c40f";  // Yellow
                    break;
                default:
                    color = "#34495e";  // Default grey
                    break;
            }
            newValue.setStyle("-fx-bar-fill: " + color + ";");
        });
    }

    // Update the performance view form with the chart
    performanceViewForm.getChildren().clear();
    performanceViewForm.getChildren().addAll(studentLabel, studentDropdown, examTypeLabel, examTypeDropdown, viewPerformanceBtn, barChart);
}

private VBox createRecommendForm() {
    VBox recommendationsBox = new VBox();
    recommendationsBox.setSpacing(10);

