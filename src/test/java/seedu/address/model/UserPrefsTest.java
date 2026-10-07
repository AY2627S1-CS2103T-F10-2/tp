package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;

public class UserPrefsTest {

    @Test
    public void equality_copyAndGuiSettings() {
        UserPrefs prefs = new UserPrefs();
        UserPrefs copy = new UserPrefs(prefs);
        assertTrue(prefs.equals(prefs));
        assertEquals(prefs, copy);
        assertEquals(prefs.hashCode(), copy.hashCode());
        assertFalse(prefs.equals(null));
        assertFalse(prefs.equals("preferences"));
        copy.setGuiSettings(new GuiSettings(800, 700, 20, 30));
        assertFalse(prefs.equals(copy));
        assertEquals("Gui Settings : " + prefs.getGuiSettings(), prefs.toString());
        assertThrows(NullPointerException.class, () -> new UserPrefs(null));
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        UserPrefs userPref = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPref.setGuiSettings(null));
    }

}
