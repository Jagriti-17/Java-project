package application;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import application.utils.sql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections; // For creating ObservableList
import javafx.collections.ObservableList; // For using ObservableList


public class Main extends Application {

    private VBox performanceViewForm;
    private Label studentLabel;
    private ComboBox<String> studentDropdown;
    private Label examTypeLabel;
    private ComboBox<String> examTypeDropdown;
    private Button viewPerformanceBtn;
    private ComboBox<String> studentDropdownForScores;
    private VBox recommendationsBox;
    

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
        Button btnViewPerformanceByExam = new Button("View Performance by Exam Type");
        Button btnRecommendations = new Button("Recommendations");

        // Styling for sidebar buttons
        String buttonStyle = "-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px;";
        btnAddStudent.setStyle(buttonStyle);
        btnAddScores.setStyle(buttonStyle);
        btnViewPerformanceByExam.setStyle(buttonStyle);
        btnRecommendations.setStyle(buttonStyle);
        sidebar.getChildren().addAll(btnAddStudent, btnAddScores, btnViewPerformanceByExam, btnRecommendations);

        // Main content area
        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(sidebar);
        mainLayout.setPadding(new Insets(20));

        // Enhanced Add Student Form
        GridPane addStudentForm = createAddStudentForm(mainLayout);

        // Enhanced Add Score Form
        GridPane addScoreForm = createAddScoreForm(mainLayout);

        // Enhanced View Performance Form
        performanceViewForm = createPerformanceViewForm();
        recommendationsBox =  createRecommendForm();
        
       

        // Sidebar button actions
        btnAddStudent.setOnAction(e -> mainLayout.setCenter(addStudentForm));
        btnAddScores.setOnAction(e -> mainLayout.setCenter(addScoreForm));
        btnRecommendations.setOnAction(e -> mainLayout.setCenter(recommendationsBox));
        btnViewPerformanceByExam.setOnAction(e -> mainLayout.setCenter(performanceViewForm)); 
        
        
        // Footer
        Label footerLabel = new Label("Version 1.0 ");
        footerLabel.setAlignment(Pos.CENTER);
        mainLayout.setBottom(footerLabel);
        
   
        // Set the main layout
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


       
    // Implementation for Add Student Form
    private GridPane createAddStudentForm(BorderPane mainLayout) {
        GridPane addStudentForm = new GridPane();
        addStudentForm.setHgap(10);
        addStudentForm.setVgap(10);

        Label studentIDLabel = new Label("Student ID:");
        TextField studentIDField = new TextField();
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
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
        addStudentForm.add(yearLabel, 0, 4);
        addStudentForm.add(yearComboBox, 1, 4);
        addStudentForm.add(submitStudentBtn, 1, 5);

        submitStudentBtn.setOnAction(e -> {
            if (studentIDField.getText().isEmpty() || nameField.getText().isEmpty() || emailField.getText().isEmpty() || yearComboBox.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all the fields.");
                alert.show();
                return;
            }
        

            try (Connection conn = sql.getConnection()) {
                String checkQuery = "SELECT COUNT(*) FROM students WHERE studentID = ?";
                try (PreparedStatement checkPs = conn.prepareStatement(checkQuery)) {
                    checkPs.setString(1, studentIDField.getText());
                    ResultSet rs = checkPs.executeQuery();
                    rs.next();

                    if (rs.getInt(1) > 0) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Student ID already exists! Please enter a unique ID.");
                        alert.show();
                    } else {
                        String query = "INSERT INTO students (studentID, name, email, year) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement ps = conn.prepareStatement(query)) {
                            ps.setString(1, studentIDField.getText());
                            ps.setString(2, nameField.getText());
                            ps.setString(3, emailField.getText());
                            ps.setString(4, yearComboBox.getValue());
                            ps.executeUpdate();

                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Student added successfully!");
                            alert.show();
                            clearStudentFormFields(studentIDField, nameField, emailField, yearComboBox);

                            // Update the student dropdown in the Add Scores section immediately
                            populateStudentDropdown(studentDropdownForScores);
                            // Update student dropdown for View Performance
                            populateStudentDropdown(studentDropdown);
                            
                         
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        return addStudentForm;
    }

    // Implementation for Add Score Form with validation for scores
    private GridPane createAddScoreForm(BorderPane mainLayout) {
        GridPane addScoreForm = new GridPane();
        addScoreForm.setHgap(10);
        addScoreForm.setVgap(10);

        Label studentLabel = new Label("Student:");
        studentDropdownForScores = new ComboBox<>();
        populateStudentDropdown(studentDropdownForScores); 

        Label mathsLabel = new Label("Maths Score:");
        TextField mathsField = new TextField();
        Label englishLabel = new Label("English Score:");
        TextField englishField = new TextField();
        Label scienceLabel = new Label("Science Score:");
        TextField scienceField = new TextField();
        Label itLabel = new Label("IT Score:");
        TextField itField = new TextField();

        Label examTypeLabel = new Label("Exam Type:");
        ComboBox<String> examTypeComboBox = new ComboBox<>();
        examTypeComboBox.getItems().addAll("Assignment", "Quiz", "Test", "Final Exam");

        Button submitScoreBtn = new Button("Submit Score");

        addScoreForm.add(studentLabel, 0, 0);
        addScoreForm.add(studentDropdownForScores, 1, 0);
        addScoreForm.add(mathsLabel, 0, 1);
        addScoreForm.add(mathsField, 1, 1);
        addScoreForm.add(englishLabel, 0, 2);
        addScoreForm.add(englishField, 1, 2);
        addScoreForm.add(scienceLabel, 0, 3);
        addScoreForm.add(scienceField, 1, 3);
        addScoreForm.add(itLabel, 0, 4);
        addScoreForm.add(itField, 1, 4);
        addScoreForm.add(examTypeLabel, 0, 5);
        addScoreForm.add(examTypeComboBox, 1, 5);
        addScoreForm.add(submitScoreBtn, 1, 6);

        submitScoreBtn.setOnAction(e -> {
            if (studentDropdownForScores.getValue() == null ||
                    mathsField.getText().isEmpty() ||
                    englishField.getText().isEmpty() ||
                    scienceField.getText().isEmpty() ||
                    itField.getText().isEmpty() ||
                    examTypeComboBox.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all fields.");
                alert.show();
                return;
            }

            try {
                int mathsScore = Integer.parseInt(mathsField.getText());
                int englishScore = Integer.parseInt(englishField.getText());
                int scienceScore = Integer.parseInt(scienceField.getText());
                int itScore = Integer.parseInt(itField.getText());

                // Validate the score range (0-100)
                if (mathsScore < 0 || mathsScore > 100 || englishScore < 0 || englishScore > 100 ||
                        scienceScore < 0 || scienceScore > 100 || itScore < 0 || itScore > 100) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Scores must be between 0 and 100.");
                    alert.show();
                    return;
                }

                try (Connection conn = sql.getConnection()) {
                    String query = "INSERT INTO scores (studentID, maths, english, science, it, examType) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(query)) {
                        ps.setString(1, studentDropdownForScores.getValue());
                        ps.setInt(2, mathsScore);
                        ps.setInt(3, englishScore);
                        ps.setInt(4, scienceScore);
                        ps.setInt(5, itScore);
                        ps.setString(6, examTypeComboBox.getValue());

                        ps.executeUpdate();

                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Score added successfully!");
                        alert.show();
                        clearScoreFormFields(studentDropdownForScores, mathsField, englishField, scienceField, itField, examTypeComboBox);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid numeric scores.");
                alert.show();
            }
        });

        return addScoreForm;
    }

    // Helper methods to clear fields
    private void clearStudentFormFields(TextField studentIDField, TextField nameField, TextField emailField, ComboBox<String> yearComboBox) {
        studentIDField.clear();
        nameField.clear();
        emailField.clear();
        yearComboBox.setValue(null);
    }

    private void clearScoreFormFields(ComboBox<String> studentDropdownForScores, TextField mathsField, TextField englishField, TextField scienceField, TextField itField, ComboBox<String> examTypeComboBox) {
        studentDropdownForScores.setValue(null);
        mathsField.clear();
        englishField.clear();
        scienceField.clear();
        itField.clear();
        examTypeComboBox.setValue(null);
    }

    // Populate the student dropdown with student IDs from the database
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

        // Add the initial form elements to the VBox
        viewPerformanceForm.getChildren().addAll(studentLabel, studentDropdown, examTypeLabel, examTypeDropdown, viewPerformanceBtn);

        // Empty VBox to hold the chart (dynamically updated)
        VBox chartContainer = new VBox();
        viewPerformanceForm.getChildren().add(chartContainer);

        // Button action to fetch and display performance
        viewPerformanceBtn.setOnAction(e -> {
            if (studentDropdown.getValue() == null || examTypeDropdown.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please select both a student and an exam type.");
                alert.show();
                return;
            }

            // View the performance and update the chart
            viewPerformanceByExamType(studentDropdown.getValue(), examTypeDropdown.getValue(), chartContainer);
        });

        return viewPerformanceForm;
    }

    // Method to view performance by exam type and handle multiple exam entries for the same type
    private void viewPerformanceByExamType(String studentID, String examType, VBox chartContainer) {
        // Clear the chart container before displaying new data
        chartContainer.getChildren().clear();

        try (Connection conn = sql.getConnection()) {
            String query = "SELECT maths, english, science, it FROM scores WHERE studentID = ? AND examType = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, studentID);
                ps.setString(2, examType);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int mathsScore = rs.getInt("maths");
                    int englishScore = rs.getInt("english");
                    int scienceScore = rs.getInt("science");
                    int itScore = rs.getInt("it");

                    // Create a chart to display the scores
                    CategoryAxis xAxis = new CategoryAxis();
                    xAxis.setLabel("Subjects");
                    NumberAxis yAxis = new NumberAxis();
                    yAxis.setLabel("Scores");

                    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                    barChart.setTitle("Performance in " + examType);

                    XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
                    dataSeries.setName("Scores");
                    dataSeries.getData().add(new XYChart.Data<>("Maths", mathsScore));
                    dataSeries.getData().add(new XYChart.Data<>("English", englishScore));
                    dataSeries.getData().add(new XYChart.Data<>("Science", scienceScore));
                    dataSeries.getData().add(new XYChart.Data<>("IT", itScore));

                    barChart.getData().add(dataSeries);

                    // Add the chart to the chart container
                    chartContainer.getChildren().add(barChart);
                    for (XYChart.Data<String, Number> dataPoint : dataSeries.getData()) {
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
                        dataPoint.getNode().setStyle("-fx-bar-fill: " + color + ";");
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "No scores found for the selected student and exam type.");
                    alert.show();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

   

    // Update the performance chart with new data
    private void updatePerformanceChart(double mathsScore, double englishScore, double scienceScore, double itScore, String studentID, String examType, VBox chartContainer) {
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

        
        // Clear the previous chart and update with the new chart
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(barChart);
    }

 // Method to create the Recommendations UI
    private VBox createRecommendForm() {
        VBox recommendForm = new VBox();
        recommendForm.setSpacing(10);
        
        Label studentLabel = new Label("Select Student:");
        ComboBox<String> studentDropdown = new ComboBox<>();
        populateStudentDropdown(studentDropdown);

        Label recommendationLabel = new Label("Weak Subjects Recommendations:");
        ListView<String> recommendationList = new ListView<>();
        
        Button generateRecommendationsBtn = new Button("Generate Recommendations");
        
        recommendForm.getChildren().addAll(studentLabel, studentDropdown, recommendationLabel, recommendationList, generateRecommendationsBtn);
        
        generateRecommendationsBtn.setOnAction(e -> {
            String selectedStudent = studentDropdown.getValue();
            if (selectedStudent == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a student.");
                alert.show();
                return;
            }

            // Generate recommendations based on student's weak subjects
            generateRecommendations(selectedStudent, recommendationList);
        });

        return recommendForm;
    }

    // Method to generate recommendations
    private void generateRecommendations(String studentID, ListView<String> recommendationList) {
        recommendationList.getItems().clear();

        try (Connection conn = sql.getConnection()) {
            String query = "SELECT AVG(maths) AS avgMaths, AVG(english) AS avgEnglish, AVG(science) AS avgScience, AVG(it) AS avgIT " +
                           "FROM scores WHERE studentID = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, studentID);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    double avgMaths = rs.getDouble("avgMaths");
                    double avgEnglish = rs.getDouble("avgEnglish");
                    double avgScience = rs.getDouble("avgScience");
                    double avgIT = rs.getDouble("avgIT");

                    StringBuilder recommendations = new StringBuilder();
                    boolean allExcellent = true;

                    if (avgMaths < 50) {
                        recommendations.append("Math: Practice more algebra and geometry.\n");
                        allExcellent = false;
                    }
                    if (avgEnglish < 50) {
                        recommendations.append("English: Work on grammar and comprehension skills.\n");
                        allExcellent = false;
                    }
                    if (avgScience < 50) {
                        recommendations.append("Science: Focus on physics and chemistry fundamentals.\n");
                        allExcellent = false;
                    }
                    if (avgIT < 50) {
                        recommendations.append("IT: Improve programming and computer science concepts.\n");
                        allExcellent = false;
                    }

                    if (allExcellent) {
                        recommendationList.getItems().add("Excellent scores! Keep practicing and challenging yourself.");
                    } else if (recommendations.length() == 0) {
                        recommendationList.getItems().add("No recommendations. The student is performing well.");
                    } else {
                        for (String rec : recommendations.toString().split("\n")) {
                            recommendationList.getItems().add(rec);
                        }
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "No scores found for this student.");
                    alert.show();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

   


    public static void main(String[] args) {
        launch(args);
    }
}
