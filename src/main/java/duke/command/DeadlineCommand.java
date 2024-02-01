package duke.command;

import duke.Storage;
import duke.Ui;
import duke.task.Task;
import duke.task.TaskList;
import duke.task.TaskType;

import java.time.LocalDate;

public class DeadlineCommand extends Command {
    private String description;
    private LocalDate by;

    public DeadlineCommand(String description, LocalDate by) {
        this.description = description;
        this.by = by;
    }

    @Override
    public void execute(TaskList list, Ui ui, Storage storage) {
        Task newDeadline = Task.createTask(TaskType.DEADLINE, description, by);
        list.add(newDeadline);
        storage.save(list);
        ui.showMessage("added new deadline: " + newDeadline);
        ui.showMessage("Looks like you have " + list.countTasks() + " things left to do!");
    }
}