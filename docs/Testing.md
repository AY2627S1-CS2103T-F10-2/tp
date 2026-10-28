---
layout: page
title: Testing guide
---

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Running tests

You can run tests in two ways.

* **Method 1: Using IntelliJ JUnit test runner**
  * To run all tests, right-click on the `src/test/java` folder and choose `Run 'All Tests'`
  * To run a subset of tests, you can right-click on a test package,
    test class, or a test and choose `Run 'ABC'`
* **Method 2: Using Gradle**
  * Open a console and run the command `gradlew clean test` (Mac/Linux: `./gradlew clean test`)

<div markdown="span" class="alert alert-secondary">:link: **Link**: Read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html) to learn more about using Gradle.
</div>

--------------------------------------------------------------------------------------------------------------------

## Types of tests

This project has three types of tests:

1. *Unit tests* target the lowest-level methods and classes.<br>
   For example: `seedu.address.commons.StringUtilTest`
1. *Integration tests* check how multiple code units work together; the individual units are assumed to work.<br>
   For example: `seedu.address.storage.StorageManagerTest`
1. *Hybrid tests* combine unit and integration testing. These tests check both the individual units and how they work together.<br>
   For example: `seedu.address.logic.LogicManagerTest`

--------------------------------------------------------------------------------------------------------------------

## Test-case design heuristics

Test inputs should be selected from the specification using the following CS2103T heuristics. These rules apply to
unit, parser, integration, and manual test cases.

1. **Equivalence partitioning (EP):** Identify every input partition that can produce a different observable result.
   Use one representative from each partition instead of repeating many values from the same partition.
2. **Boundary value analysis (BVA):** Prefer values at, just below, and just above each relevant boundary. This
   includes the smallest and largest accepted values, the first rejected values, empty values, and collection sizes
   such as empty, one item, one below capacity, and full.
3. **Each valid input at least once in a positive test:** Every valid partition must appear in at least one test whose
   expected result is successful. A valid value hidden in a negative test does not cover that partition.
4. **Invalid inputs individually before combinations:** When a test has multiple inputs, keep all other inputs valid
   while testing one invalid input. Only add tests with multiple invalid inputs after the individual invalid cases are
   covered, when the interaction itself is meaningful.
5. **Input combinations:** Use the smallest suitable strategy. At least once is the default; use all-pairs or all
   combinations only when the interaction risk justifies the extra cases.

For each test case, record the chosen partition or boundary and keep the expected result precise. For example, the
`Telehandle` tests cover the length boundaries `4/5/32/33`, the whitespace partition, and the `@` prefix partition;
the `add` parser tests keep the invalid field isolated while the other command fields remain valid.
