package seedu.address.model.courseclass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;

public class CourseClassValueTest {
    @Test
    public void courseCode_validValues_acceptsSupportedCharacters() {
        for (String code : new String[] {"CS2103T", "CS 2103T", "#", "@", "$", "CS #@$ 2103"}) {
            assertTrue(CourseCode.isValidCourseCode(code), code);
            assertEquals(code, new CourseCode(code).value);
        }
    }

    @Test
    public void courseCode_invalidValues_rejectsBlankAndUnsupportedCharacters() {
        assertThrows(NullPointerException.class, () -> new CourseCode(null));
        for (String code : new String[] {"", " ", "CS-2103T", "CS*", "CS\n2103", "CS\t2103"}) {
            assertFalse(CourseCode.isValidCourseCode(code), code);
            assertThrows(IllegalArgumentException.class, () -> new CourseCode(code));
        }
    }

    @Test
    public void name_validValues_acceptsAccentsAndClassNames() {
        for (String name : new String[] {"F10-2", "Class 1", "École", "O'Brien", "Class/Tutorial"}) {
            assertTrue(Name.isValidName(name), name);
            assertEquals(name, new Name(name).fullName);
        }
    }

    @Test
    public void name_invalidValues_rejectsBlankAndUnsupportedCharacters() {
        assertThrows(NullPointerException.class, () -> new Name(null));
        for (String name : new String[] {"", " ", " F10-2", "Class#", "Class\n1"}) {
            assertFalse(Name.isValidName(name), name);
            assertThrows(IllegalArgumentException.class, () -> new Name(name));
        }
    }

    @Test
    public void parseValues_trimsAndReportsConstraints() throws Exception {
        assertEquals(new Name("F10-2"), ParserUtil.parseCourseClassName("  F10-2  "));
        assertEquals(new CourseCode("CS2103T"), ParserUtil.parseCourseCode("  CS2103T  "));
        assertEquals(Name.MESSAGE_CONSTRAINTS,
                assertThrows(ParseException.class, () -> ParserUtil.parseCourseClassName("!")).getMessage());
        assertEquals(CourseCode.MESSAGE_CONSTRAINTS,
                assertThrows(ParseException.class, () -> ParserUtil.parseCourseCode("!")).getMessage());
        assertThrows(NullPointerException.class, () -> ParserUtil.parseCourseClassName(null));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseCourseCode(null));
    }

    @Test
    public void equalsAndHashCode_compareValues() {
        Name name = new Name("F10-2");
        assertEquals(name, new Name("F10-2"));
        assertEquals(name.hashCode(), new Name("F10-2").hashCode());
        assertNotEquals(name, new Name("F10-3"));
        assertNotEquals(name, null);
        assertNotEquals(name, "F10-2");
        CourseCode code = new CourseCode("CS2103T");
        assertEquals(code, new CourseCode("CS2103T"));
        assertEquals(code.hashCode(), new CourseCode("CS2103T").hashCode());
        assertNotEquals(code, new CourseCode("CS2101"));
        assertNotEquals(code, null);
        assertNotEquals(code, "CS2103T");
    }
}
