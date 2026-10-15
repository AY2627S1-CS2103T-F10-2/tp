package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.StudentNameContainsKeywordsPredicate;
import seedu.address.model.student.Telehandle;

public class FindStudentCommandTest {
    private final StudentNameContainsKeywordsPredicate alexPredicate =
            new StudentNameContainsKeywordsPredicate(List.of("Alex"));

    @Test
    public void execute_matchingStudents_reportsDetailsAndClassMembership() {
        Model model = new ModelManager();
        CourseClass tutorial = new CourseClass(new Name("T05"), new CourseCode("CS2103T"), Set.of());
        CourseClass lab = new CourseClass(new Name("L01"), new CourseCode("CS2101"), Set.of());
        tutorial.addStudent(new Student(new StudentName("Alex Tan"), new Telehandle("alextan")));
        tutorial.addStudent(new Student(new StudentName("Bernice Yu"), new Telehandle("bernice")));
        lab.addStudent(new Student(new StudentName("Alex Lim"), new Telehandle("alexlim")));
        model.addCourseClass(tutorial);
        model.addCourseClass(lab);

        String feedback = new FindStudentCommand(alexPredicate).execute(model).getFeedbackToUser();

        assertEquals(String.format(FindStudentCommand.MESSAGE_MATCHES_FOUND, 2,
                "1. Alex Tan (@alextan) — T05, CS2103T" + System.lineSeparator()
                        + "2. Alex Lim (@alexlim) — L01, CS2101"), feedback);
    }

    @Test
    public void execute_noMatchingStudents_reportsNoMatches() {
        assertEquals(FindStudentCommand.MESSAGE_NO_MATCHES,
                new FindStudentCommand(alexPredicate).execute(new ModelManager()).getFeedbackToUser());
    }

    @Test
    public void equalsAndToString_comparePredicate() {
        FindStudentCommand command = new FindStudentCommand(alexPredicate);
        assertTrue(command.equals(command));
        assertEquals(command, new FindStudentCommand(
                new StudentNameContainsKeywordsPredicate(List.of("Alex"))));
        assertFalse(command.equals(new FindStudentCommand(
                new StudentNameContainsKeywordsPredicate(List.of("Bernice")))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("command"));
        assertEquals(FindStudentCommand.class.getCanonicalName() + "{predicate=" + alexPredicate + "}",
                command.toString());
    }
}
