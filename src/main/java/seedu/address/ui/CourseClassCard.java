package seedu.address.ui;

import java.util.Comparator;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.person.Person;
import seedu.address.model.student.Student;

/**
 * A UI component that displays information of a {@code CourseClass}.
 */
public class CourseClassCard extends UiPart<Region> {

    private static final String FXML = "CourseClassListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final CourseClass courseClass;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label courseCode;
    @FXML
    private Label studentCount;
    @FXML
    private FlowPane tags;
    @FXML
    private VBox studentsBox;

    /**
     * Creates a {@code CourseClassCard} with the given {@code CourseClass} and index to display.
     */
    public CourseClassCard(CourseClass courseClass, int displayedIndex) {
        super(FXML);
        this.courseClass = courseClass;
        id.setText(displayedIndex + ". ");
        name.setText(courseClass.getName().fullName);
        courseCode.setText(courseClass.getCourseCode().value);
        refreshStudents();

        courseClass.getStudents().getPersonList().addListener(
                (ListChangeListener<Person>) change -> refreshStudents());
        courseClass.getStudentList().addListener(
                (ListChangeListener<Student>) change -> refreshStudents());

        courseClass.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
    }

    /**
     * Refreshes the student count and the student cards shown inside this class card.
     *
     * <p>The legacy person list is retained for brownfield compatibility, while students added through the
     * current enrolment model are displayed using {@link StudentCard}.</p>
     */
    private void refreshStudents() {
        ObservableList<Person> legacyStudents = courseClass.getStudents().getPersonList();
        ObservableList<Student> enrolledStudents = courseClass.getStudentList();
        int legacyStudentCount = courseClass.getStudents().getPersonList().size();
        int enrolledStudentCount = courseClass.getStudentList().size();
        studentCount.setText("Students: " + (legacyStudentCount + enrolledStudentCount));

        studentsBox.getChildren().clear();
        for (int i = 0; i < legacyStudents.size(); i++) {
            studentsBox.getChildren().add(
                    new PersonCard(legacyStudents.get(i), i + 1).getRoot());
        }
        for (int i = 0; i < enrolledStudents.size(); i++) {
            studentsBox.getChildren().add(
                    new StudentCard(enrolledStudents.get(i), legacyStudents.size() + i + 1).getRoot());
        }
    }
}
