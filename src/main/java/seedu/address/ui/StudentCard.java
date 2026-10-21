package seedu.address.ui;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.student.Note;
import seedu.address.model.student.Student;

/**
 * A UI component that displays the details of a {@code Student} enrolled in a course class.
 */
public class StudentCard extends UiPart<Region> {

    private static final String FXML = "StudentListCard.fxml";
    private static final DateTimeFormatter NOTE_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH);

    /** The student represented by this card. */
    public final Student student;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label telehandle;
    @FXML
    private VBox notesBox;

    /**
     * Creates a {@code StudentCard} with the given student and index to display.
     *
     * @param student student to display
     * @param displayedIndex index displayed beside the student
     */
    public StudentCard(Student student, int displayedIndex) {
        super(FXML);
        this.student = student;
        if (displayedIndex % 2 == 0) {
            cardPane.getStyleClass().add("student-row-alternate");
        }
        id.setText(displayedIndex + ". ");
        name.setText(student.getName().fullName);
        telehandle.setText("@" + student.getTelehandle().value);
        displayNotes();
    }

    /** Displays the student's notes in their stored order. */
    private void displayNotes() {
        boolean hasNotes = !student.getNotes().isEmpty();
        notesBox.setManaged(hasNotes);
        notesBox.setVisible(hasNotes);

        for (int i = 0; i < student.getNotes().size(); i++) {
            Note note = student.getNotes().get(i);
            String timestamp = NOTE_TIMESTAMP_FORMAT.format(note.getTimestamp());
            Label noteLabel = new Label(String.format("%d. [%s] %s", i + 1, timestamp, note.getText()));
            noteLabel.setWrapText(true);
            noteLabel.setMinWidth(0);
            noteLabel.setPrefWidth(0);
            noteLabel.setMaxWidth(Double.MAX_VALUE);
            noteLabel.getStyleClass().add("student-note");
            notesBox.getChildren().add(noteLabel);
        }
    }
}
