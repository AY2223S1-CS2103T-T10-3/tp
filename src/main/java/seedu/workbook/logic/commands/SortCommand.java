package seedu.workbook.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.workbook.model.Model;

/**
 * Sorts all internships in WorkBook by Date, with the nearest upcoming Date at
 * the top.
 */
public class SortCommand extends Command {

    /** Command word to execute the sort command */
    public static final String COMMAND_WORD = "sort";

    /** Message string displaying successful execution of the sort command */
    public static final String MESSAGE_SUCCESS = "Sorted internship applications by date and time.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.sortInternshipListByDate();
        // undo behaviour doesn't really work well for now due to how we manipulate
        // internalList in `UniqueInternshipList.java`
        model.commitWorkBook();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
