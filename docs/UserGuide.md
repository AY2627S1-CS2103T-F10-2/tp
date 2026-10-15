---
layout: page
title: User Guide
---

AddressBook Level 3 (AB3) is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, AB3 can help you manage contacts faster than traditional GUI applications.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a contact named `John Doe` to the Address Book.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[t/TAG]…​` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to the address book.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​`

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of tags, including zero.
</div>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal`

### Listing all persons: `list`

Shows a list of all persons in the address book.

Format: `list`

### Editing a person: `edit`

Edits an existing person in the address book.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `t/` without a tag after it.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

### Locating persons by name: `find`

Finds persons whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Only full words match; for example, `Han` does not match `Hans`.
* Persons matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Deleting a person: `delete`

Deletes the specified person from the address book.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd person in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st person in the results of the `find` command.

### Adding a class: `aclass`

Adds a class group to the system.

Format: `aclass n/NAME c/COURSE_CODE [t/TAG]…​`

* `NAME` is the class group name.
* `COURSE_CODE` identifies the module containing the class group.
* `t/TAG` is optional and can be provided multiple times.
* The `n/` and `c/` prefixes are required and can be entered in either order. Each can be used only once.
* A class is considered a duplicate when it has the same name and course code as an existing class, regardless of tags.
* After a class is added, it appears in the class list and is saved automatically.

Examples:
* `aclass n/F10-2 c/CS2103T` adds a class group without tags.
* `aclass n/F10-2 c/CS2103T t/tutorial t/odd-week` adds a class group with two tags.

If the required prefixes are missing, duplicated, or contain invalid values, the application shows the command format
and does not add the class. Attempting to add an existing class shows `This class already exists in the system.`

### Removing a student from a class group: `dstudent`

Removes a student's enrolment from one class group without removing the student's enrolments in other classes.
The student is identified by their telehandle, while the class group and module identify the enrolment to remove.
The module name is required so that class groups with the same name in different modules are distinguished.

Format: `dstudent t/TELEHANDLE c/CLASS_GROUP m/MODULE_NAME`

* `TELEHANDLE` identifies the student.
* `CLASS_GROUP` identifies the tutorial, laboratory, or recitation group.
* `MODULE_NAME` identifies the module containing the class group.
* All three prefixes are required. The prefixes can be entered in any order, but each prefix can only be used once.
* The command first displays the matching student and asks for confirmation before changing any data.
* Type `yes` to remove the enrolment, or `no` to cancel without changing any data.
* If the input is neither `yes` nor `no`, the application asks for a valid confirmation again and keeps the deletion pending.
* If the class group or enrolment cannot be found, an error is shown and no data is changed.

Example:

1. Enter `dstudent t/birdman c/T05 m/CS2103T`.<br>
   The application displays `Remove John Doe (@birdman) from T05 in CS2103T? Type yes to confirm or no to cancel.`
1. Enter `yes` to remove `@birdman` from `T05` in `CS2103T`.<br>
   The application displays `Student @birdman has been removed from T05 in CS2103T`.

To cancel the operation, enter `no` after the confirmation prompt. The application displays
`Student deletion cancelled. No records were changed.`

### Adding a note to a student: `note`

Adds a timestamped teaching observation to a student enrolled in a specific class group.
The module name is required so that class groups with the same name in different modules are distinguished.

Format: `note t/TELEHANDLE c/CLASS_GROUP m/MODULE_NAME o/OBSERVATION`

* `TELEHANDLE` identifies the student.
* `CLASS_GROUP` identifies the tutorial, laboratory, or recitation group.
* `MODULE_NAME` identifies the module containing the class group.
* `OBSERVATION` is the non-blank note to record.
* `o/OBSERVATION` must be the final parameter. Everything after `o/` is treated as observation text, so the
  observation may contain prefix-like text such as `m/ath`.
* The date and time are generated automatically when the command is executed.
* Notes appear below the student's name and Telegram handle in the selected class. They are displayed in recorded
  order, from oldest to newest, for example `[14 Oct 2026, 21:30] Needs follow-up`. Long observations wrap within the
  student card.

Example:

* `note t/birdman c/T05 m/CS2103T o/Needs follow-up on tutorial participation` records the observation for
  `@birdman` in `T05` of `CS2103T`.

### Finding enrolled students by name: `fstudent`

Finds students enrolled in any class whose names contain at least one of the supplied keywords.
Each result includes the student's Telegram handle, class group, and module so that students with the same name
can be distinguished.

Format: `fstudent KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive.
* Only full words in student names match; for example, `Alex` matches `Alex Tan`, but `Ale` does not.
* When several keywords are supplied, a student matching any keyword is returned.
* A student enrolled in multiple classes is shown once for each enrolment.

Examples:

* `fstudent Alex` finds every enrolled student with `Alex` as a full word in their name.
* `fstudent Alex Bernice` finds students whose names contain either `Alex` or `Bernice`.

### Finding classes by class-group name: `fclass`

Finds class groups whose names contain at least one of the supplied keywords and displays the matching classes.

Format: `fclass KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive and matches full words.
* When several keywords are supplied, a class matching any keyword is returned.

Example:

* `fclass T05 L01` finds class groups whose names contain either `T05` or `L01`.

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook automatically saves data after every command. You do not need to save manually.

### Editing the data file

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, AddressBook starts with an empty address book at the next run. The invalid file remains on disk until you run a command (AddressBook saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend t/colleague`
**Add class** | `aclass n/NAME c/COURSE_CODE [t/TAG]…​`<br> e.g., `aclass n/F10-2 c/CS2103T t/tutorial`
**Clear** | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Remove student from class** | `dstudent t/TELEHANDLE c/CLASS_GROUP m/MODULE_NAME`<br> e.g., `dstudent t/birdman c/T05 m/CS2103T`, followed by `yes`
**Add student note** | `note t/TELEHANDLE c/CLASS_GROUP m/MODULE_NAME o/OBSERVATION`<br> e.g., `note t/birdman c/T05 m/CS2103T o/Needs follow-up`
**Find student** | `fstudent KEYWORD [MORE_KEYWORDS]`<br> e.g., `fstudent Alex Bernice`
**Find class** | `fclass KEYWORD [MORE_KEYWORDS]`<br> e.g., `fclass T05 L01`
**Edit** | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`<br> e.g., `edit 2 n/James Lee e/jameslee@example.com`
**Find** | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List** | `list`
**Help** | `help`
