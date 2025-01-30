/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package notekeeper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class NoteKeeperController {
    @FXML private TableView<Note> tableNotes;
    @FXML private TableColumn<Note, String> colTitle;
    @FXML private TableColumn<Note, String> colDate;
    @FXML private TextField txtTitle;
    @FXML private TextArea txtContent;
    @FXML private Label lblNoteContent;

    private Connection conn;
    private ObservableList<Note> notesList = FXCollections.observableArrayList();
    private int selectedNoteId = -1;

    public void initialize() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        connectDatabase();
        loadNotes();

        tableNotes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedNoteId = newSelection.getId();
                txtTitle.setText(newSelection.getTitle());
                txtContent.setText(newSelection.getContent());
                lblNoteContent.setText(newSelection.getContent());
            }
        });
    }

    private void connectDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/notekeeper", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to connect to database.");
        }
    }

    private void loadNotes() {
        notesList.clear();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM notes ORDER BY updated_at DESC")) {
            while (rs.next()) {
                notesList.add(new Note(rs.getInt("id"), rs.getString("title"), rs.getString("content"), rs.getString("updated_at")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableNotes.setItems(notesList);
    }

    @FXML
    private void addNote() {
        String title = txtTitle.getText().trim();
        String content = txtContent.getText().trim();
        if (title.isEmpty() || content.isEmpty()) {
            showAlert("Error", "Title and content cannot be empty.");
            return;
        }
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO notes (title, content) VALUES (?, ?)")) {
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.executeUpdate();
            loadNotes();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateNote() {
        if (selectedNoteId == -1) {
            showAlert("Error", "Select a note to update.");
            return;
        }
        try (PreparedStatement stmt = conn.prepareStatement("UPDATE notes SET title=?, content=? WHERE id=?")) {
            stmt.setString(1, txtTitle.getText().trim());
            stmt.setString(2, txtContent.getText().trim());
            stmt.setInt(3, selectedNoteId);
            stmt.executeUpdate();
            loadNotes();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteNote() {
        if (selectedNoteId == -1) {
            showAlert("Error", "Select a note to delete.");
            return;
        }
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM notes WHERE id=?")) {
            stmt.setInt(1, selectedNoteId);
            stmt.executeUpdate();
            loadNotes();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtTitle.clear();
        txtContent.clear();
        lblNoteContent.setText("(Click a note to view)");
        selectedNoteId = -1;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
