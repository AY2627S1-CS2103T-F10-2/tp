package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;
import seedu.address.model.tag.Tag;

public class JsonAdaptedCourseClassTest {
    @Test
    public void toModelType_invalidNameCodeOrTags_rejectsMalformedData() {
        for (String name : new String[] {null, "", "invalid*"}) {
            assertThrows(IllegalValueException.class, () ->
                    new JsonAdaptedCourseClass(name, "CS2103T", null, null).toModelType());
        }
        for (String code : new String[] {null, "", "invalid*"}) {
            assertThrows(IllegalValueException.class, () ->
                    new JsonAdaptedCourseClass("F10-2", code, null, null).toModelType());
        }
        assertThrows(IllegalValueException.class, () -> new JsonAdaptedCourseClass("F10-2", "CS2103T",
                Arrays.asList((JsonAdaptedTag) null), null).toModelType());
        assertThrows(IllegalValueException.class, () -> new JsonAdaptedCourseClass("F10-2", "CS2103T",
                List.of(new JsonAdaptedTag("invalid*")), null).toModelType());
    }

    @Test
    public void toModelType_missingOptionalFieldsAndDuplicateTags_preservesValidData() throws Exception {
        CourseClass restored = new JsonAdaptedCourseClass("F10-2", "CS2103T", null, null).toModelType();
        assertEquals(new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"), Set.of()), restored);
        assertTrue(restored.getStudents().getPersonList().isEmpty());
        restored = new JsonAdaptedCourseClass("F10-2", "CS2103T",
                List.of(new JsonAdaptedTag("tutorial"), new JsonAdaptedTag("tutorial")), null).toModelType();
        assertEquals(Set.of(new Tag("tutorial")), restored.getTags());
    }
}
