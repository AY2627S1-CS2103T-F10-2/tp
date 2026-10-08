package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class StudentNameContainsKeywordsPredicateTest {
    private final Student student = new Student(new StudentName("Alex Tan"), new Telehandle("alextan"));

    @Test
    public void test_nameContainsKeyword_returnsTrue() {
        assertTrue(new StudentNameContainsKeywordsPredicate(List.of("alex")).test(student));
        assertTrue(new StudentNameContainsKeywordsPredicate(List.of("nobody", "TAN")).test(student));
    }

    @Test
    public void test_nameDoesNotContainFullKeyword_returnsFalse() {
        assertFalse(new StudentNameContainsKeywordsPredicate(List.of("Ale")).test(student));
        assertFalse(new StudentNameContainsKeywordsPredicate(List.of("Bernice")).test(student));
    }

    @Test
    public void equalsAndToString_compareKeywords() {
        StudentNameContainsKeywordsPredicate predicate =
                new StudentNameContainsKeywordsPredicate(List.of("Alex"));

        assertTrue(predicate.equals(predicate));
        assertEquals(predicate, new StudentNameContainsKeywordsPredicate(List.of("Alex")));
        assertFalse(predicate.equals(new StudentNameContainsKeywordsPredicate(List.of("Bernice"))));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("predicate"));
        assertEquals(StudentNameContainsKeywordsPredicate.class.getCanonicalName()
                + "{keywords=[Alex]}", predicate.toString());
    }
}
