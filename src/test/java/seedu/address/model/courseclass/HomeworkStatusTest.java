package seedu.address.model.courseclass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class HomeworkStatusTest {
    @Test
    public void fromSerializedValue_acceptsPersistedAndEnumStyleValues() {
        assertEquals(HomeworkStatus.NOT_SET, HomeworkStatus.fromSerializedValue("not-set").orElseThrow());
        assertEquals(HomeworkStatus.PENDING, HomeworkStatus.fromSerializedValue("PENDING").orElseThrow());
        assertEquals(HomeworkStatus.COMPLETED, HomeworkStatus.fromSerializedValue("completed").orElseThrow());
        assertEquals(HomeworkStatus.COMPLETED, HomeworkStatus.fromSerializedValue("COMPLETED").orElseThrow());
    }

    @Test
    public void fromSerializedValue_invalidValue_returnsEmpty() {
        assertTrue(HomeworkStatus.fromSerializedValue(null).isEmpty());
        assertTrue(HomeworkStatus.fromSerializedValue("unknown").isEmpty());
    }
}
