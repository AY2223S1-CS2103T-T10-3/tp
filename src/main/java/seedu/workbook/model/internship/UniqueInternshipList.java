package seedu.workbook.model.internship;

import static java.util.Objects.requireNonNull;
import static seedu.workbook.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import seedu.workbook.model.internship.exceptions.DuplicateInternshipException;
import seedu.workbook.model.internship.exceptions.InternshipNotFoundException;

/**
 * A list of internships that enforces uniqueness between its elements and does
 * not allow nulls.
 * A internship is considered unique by comparing using
 * {@code Internship#isSameInternship(Internship)}.
 *
 * As such, adding and updating of internships uses
 * Internship#isSameInternship(Internship) for equality so as to ensure that the
 * internship being added or updated is unique in terms of identity in the
 * UniqueInternshipList.
 *
 * However, the removal of an internship uses Internship#equals(Object) so as to
 * ensure that the internship with exactly the same fields will be removed.
 *
 * Supports a minimal set of list operations.
 *
 * @see Internship#isSameInternship(Internship)
 */
public class UniqueInternshipList implements Iterable<Internship> {

    private final ObservableList<Internship> baseInternalList = FXCollections.observableArrayList();
    private final ObservableList<Internship> unmodifiableBaseInternalList = FXCollections
            .unmodifiableObservableList(baseInternalList);

    private ObservableList<Internship> uiFacingInternalList = FXCollections.observableArrayList();
    private final ObservableList<Internship> unmodifiableUiFacingInternalList = FXCollections
            .unmodifiableObservableList(uiFacingInternalList);

    // wrapper on baseInternalList, is automatically updated whenever
    // baseInternalList is updated, and is always in sorted order w.r.t
    // InternshipComparator
    private final SortedList<Internship> internalListSortedByDate = new SortedList<>(baseInternalList,
            new InternshipComparator());

    private boolean currentlySortedByDate = false;

    /**
     * Returns true if the list contains an equivalent internship as the given
     * argument.
     */
    public boolean contains(Internship toCheck) {
        requireNonNull(toCheck);
        return baseInternalList.stream().anyMatch(toCheck::isSameInternship);
    }

    /**
     * Adds an Internship to the list.
     * The Internship must not already exist in the list.
     */
    public void add(Internship toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateInternshipException();
        }
        baseInternalList.add(toAdd);
        refreshUiFacingInternalList();
    }

    /**
     * Replaces the Internship {@code target} in the list with
     * {@code editedInternship}.
     * {@code target} must exist in the list.
     * The Internship identity of {@code editedInternship} must not be the same as
     * another existing Internship in the list.
     */
    public void setInternship(Internship target, Internship editedInternship) {
        requireAllNonNull(target, editedInternship);

        int index = baseInternalList.indexOf(target);
        if (index == -1) {
            throw new InternshipNotFoundException();
        }

        if (!target.isSameInternship(editedInternship) && contains(editedInternship)) {
            throw new DuplicateInternshipException();
        }

        baseInternalList.set(index, editedInternship);
        refreshUiFacingInternalList();
    }

    /**
     * Removes the equivalent Internship from the list.
     * The Internship must exist in the list.
     */
    public void remove(Internship toRemove) {
        requireNonNull(toRemove);
        if (!baseInternalList.remove(toRemove)) {
            throw new InternshipNotFoundException();
        }

        refreshUiFacingInternalList();
    }

    public void setInternships(UniqueInternshipList replacement) {
        requireNonNull(replacement);
        baseInternalList.setAll(replacement.baseInternalList);
        refreshUiFacingInternalList();
    }

    /**
     * Replaces the contents of this list with {@code Internships}.
     * {@code Internships} must not contain duplicate Internships.
     */
    public void setInternships(List<Internship> internships) {
        requireAllNonNull(internships);
        if (!internshipsAreUnique(internships)) {
            throw new DuplicateInternshipException();
        }

        baseInternalList.setAll(internships);
        refreshUiFacingInternalList();
    }

    /**
     * Toggles sorting by date.
     */
    public void sortByDate() {
        // changes active view to the sorted view
        currentlySortedByDate = true;
        refreshUiFacingInternalList();
    }

    /**
     * Resets order of list, aka back to order in which Internships were added.
     */
    public void resetOrder() {
        currentlySortedByDate = false;
        refreshUiFacingInternalList();
    }

    /**
     * (Re)Populates the base uiFacingInternalList with the correct internalList
     * based on sort flag.
     * Called whenever underlying `baseInternalList` has been modified, and allows
     * user to see changes even if in sorted view.
     * Recall that UI components are observing the unmodifiable wrapper on
     * this `uiFacingInternalList`.
     *
     * @see #asUiFacingUnmodifiableObservableList()
     */
    private void refreshUiFacingInternalList() {
        if (currentlySortedByDate) {
            uiFacingInternalList.setAll(internalListSortedByDate);
        } else {
            uiFacingInternalList.setAll(baseInternalList);
        }
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     * To be used/observed by UI components.
     *
     * @see MainWindow#fillInnerParts()
     */
    public ObservableList<Internship> asUiFacingUnmodifiableObservableList() {
        return unmodifiableUiFacingInternalList;
    }

    /**
     * Returns the default ordered list as an unmodifiable {@code ObservableList}
     * For saving and other operations that are order independent.
     */
    public ObservableList<Internship> asUnmodifiableBaseList() {
        return unmodifiableBaseInternalList;
    }

    @Override
    public Iterator<Internship> iterator() {
        return baseInternalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueInternshipList // instanceof handles nulls
                        && baseInternalList.equals(((UniqueInternshipList) other).baseInternalList));
    }

    @Override
    public int hashCode() {
        return baseInternalList.hashCode();
    }

    /**
     * Returns true if {@code internships} contains only unique internships.
     */
    private boolean internshipsAreUnique(List<Internship> internships) {
        for (int i = 0; i < internships.size() - 1; i++) {
            for (int j = i + 1; j < internships.size(); j++) {
                if (internships.get(i).isSameInternship(internships.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
