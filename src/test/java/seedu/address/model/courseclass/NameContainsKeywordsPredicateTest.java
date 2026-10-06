package seedu.address.model.courseclass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class NameContainsKeywordsPredicateTest {
    @Test
    public void test_matchesWholeWordsIgnoringCaseAndAnyKeyword() {
        CourseClass courseClass = new CourseClass(new Name("Tutorial F10-2"), new CourseCode("CS2103T"), Set.of());
        assertTrue(new NameContainsKeywordsPredicate(List.of("missing", "tutorial")).test(courseClass));
        assertFalse(new NameContainsKeywordsPredicate(List.of("Tutor")).test(courseClass));
        assertFalse(new NameContainsKeywordsPredicate(List.of()).test(courseClass));
    }

    @Test
    public void equality_comparesKeywords() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("F10-2"));
        assertTrue(predicate.equals(predicate));
        assertEquals(predicate, new NameContainsKeywordsPredicate(List.of("F10-2")));
        assertFalse(predicate.equals(new NameContainsKeywordsPredicate(List.of("F10-3"))));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("predicate"));
        assertEquals(NameContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=[F10-2]}",
                predicate.toString());
    }
}
