package duke;

import duke.command.ByeCommand;
import duke.command.Command;
import duke.exceptions.DukeException;
import duke.exceptions.StorageException;
import duke.task.TaskList;

//main class for the bot
public class Duke {
    private Storage storage;
    private TaskList taskList;
    private Ui ui;

    public Duke(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        try {
            this.taskList = this.storage.load();
        } catch (StorageException e) {
            this.ui.showError(e);
            //todo: ask the user if want to create new datafile, possibly deleting old data
        }
    }

    public void run() {
        ui.showWelcome();
        ui.showLine();
        ui.showList(taskList);
        ui.showLine();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = this.ui.readCommand();
                ui.showLine(); // show the divider line ("_______")
                Command c = Parser.parse(fullCommand);
                c.execute(taskList, ui, storage);
                isExit = c instanceof ByeCommand;
            } catch (DukeException e) {
                ui.showError(e);
            } finally {
                ui.showLine();
            }
        }
    }

    public static void main(String[] args) {
        new Duke("data/duke.txt").run();
    }
}