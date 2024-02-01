package duke;

import duke.command.*;
import duke.exceptions.DukeException;
import duke.exceptions.IllegalParamException;
import duke.exceptions.MissingInfoException;
import duke.exceptions.ParserException;
import duke.task.Task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

//converts the input string from user to a duke.command.Command object
public class Parser {
    /**
     * Parses user input and converts it to a duke. Command object.
     * The duke.Command object can then be executed to perform the duke.command.
     *
     * @param input user input as a string
     * @return a duke.command.Command object
     * @throws DukeException specific exception class will depend on error encountered
     * @see Command
     */
    public static Command parse(String input) throws DukeException {
        String clean = input.trim().toLowerCase();
        String[] tokens = input.trim().split("\\s+", 2);

        if (tokens.length == 0) {
            throw new ParserException("Master, please give me a duke.command!");
        } else if (clean.equals("bye")) {
            return new ByeCommand();
        } else if (clean.equals("list")) {
            return new ListCommand();
        }

        String command = tokens[0].toLowerCase();

        switch (command) {
        case "mark":
            return parseMarkCommand(tokens[1]);
        case "unmark":
            return parseUnmarkCommand(tokens[1]);
        case "delete":
            return parseDeleteCommand(tokens[1]);
        case "todo":
            return parseTodoCommand(tokens[1]);
        case "deadline":
            return parseDeadlineCommand(tokens[1]);
        case "event":
            return parseEventCommand(tokens[1]);
        default:
            throw new ParserException("I dont understand you!"
                    + " Please be dont scold me and be gentle with me! Try again!");
        }
    }

    private static Command parseMarkCommand(String tokens) throws IllegalParamException {
        try {
            int taskId = Integer.parseInt(tokens.trim());
            return new MarkCommand(taskId);
        } catch (NumberFormatException e) {
            throw new IllegalParamException("I dont know which duke.command.task you are trying to mark! Try a number");
        }
    }

    private static Command parseUnmarkCommand(String tokens) throws IllegalParamException {
        try {
            int taskId = Integer.parseInt(tokens.trim());
            return new UnmarkCommand(taskId);
        } catch (NumberFormatException e) {
            throw new IllegalParamException("I dont know which duke.command.task you are trying to unmark! Try a number");
        }
    }

    private static Command parseDeleteCommand(String tokens) throws IllegalParamException{
        try {
            int taskId = Integer.parseInt(tokens.trim());
            return new DeleteCommand(taskId);
        } catch (NumberFormatException e) {
            throw new IllegalParamException("I dont know which duke.command.task you are trying to delete! Try a number");
        }
    }

    private static Command parseTodoCommand(String tokens) throws MissingInfoException {
        if (tokens.trim().equals("")) {
            throw new MissingInfoException("Bro u gotta describe the duke.command.task!");
        }
        String taskName = tokens.trim();
        return new ToDoCommand(taskName);
    }

    private static Command parseDeadlineCommand(String tokens) throws MissingInfoException,
                                                                      ParserException {
        String[] parts = tokens.split("/by");

        if (parts.length != 2) {
            throw new ParserException("Looks like you are missing '/by'! Use '/by' to tell me the deadline!");
        }

        String task = parts[0].trim();
        String deadlineString = parts[1].trim();

        //check if fields have values
        if (task.isEmpty()) {
            throw new MissingInfoException("You have to tell me the description too! Or I cant remember it!");
        } else if (deadlineString.isEmpty()) {
            throw new MissingInfoException("You need a deadline! Or you will never get to it!");
        }

        //parse string
        try {
            LocalDate deadline = LocalDate.parse(deadlineString, Task.getDateFormat());
            return new DeadlineCommand(task, deadline);
        } catch (DateTimeParseException e) {
            throw new ParserException("Invalid date/time format for the deadline!"
                    + "Please use a dd MMM yyyy format (e.g. 21 Jan 2000).");
        }
    }

    private static Command parseEventCommand(String tokens) throws DukeException {
        String[] parts = tokens.split("/from|/to");

        if (parts.length != 3) {
            throw new ParserException("Missing /from or /to! Use those to indicate times!");
        }

        String description = parts[0].trim();
        String startString = parts[1].trim();
        String endString = parts[2].trim();

        if (tokens.indexOf("/from") > tokens.indexOf("/to")) {
            String temp = startString;
            startString = endString;
            endString = temp;
        }

        if (description.isEmpty()) {
            throw new MissingInfoException("Tasks needs a name! Or I cant remember it!");
        } else if (startString.isEmpty()) {
            throw new MissingInfoException("Please tell me when it starts!");
        } else if (endString.isEmpty()) {
            throw new MissingInfoException("Please tell me when it ends!");
        }

        //parse string
        try {
            LocalDate startDate = LocalDate.parse(startString, Task.getDateFormat());
            LocalDate endDate = LocalDate.parse(endString, Task.getDateFormat());

            return new EventCommand(description, startDate, endDate);
        } catch (DateTimeParseException e) {
            throw new ParserException("Invalid date/time format for the deadline!"
                    + "Please use a dd MMM yyyy format (e.g. 21 Jan 2000).");
        }

    }
}