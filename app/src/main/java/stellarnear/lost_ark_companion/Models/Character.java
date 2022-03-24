package stellarnear.lost_ark_companion.Models;

import java.util.ArrayList;
import java.util.List;

import stellarnear.lost_ark_companion.Log.CustomLog;

public class Character {
    private String name;
    private String work;
    private Integer ilvl;
    private String id;
    private String workId;

    private final transient CustomLog log = new CustomLog(this.getClass());

    private List<Task> characterTasks = new ArrayList<>();

    public Character(String name, String work, int ilvl) {
        this.setName(name);
        this.setWork(work);
        this.setIlvl(ilvl);
        this.setId(name.replace(" ", "_").toLowerCase());
        this.setWorkId(work.replace(" ", "_").toLowerCase());
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getWorkId() {
        return this.workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getWork() {
        return this.work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public int getIlvl() {
        return ilvl;
    }

    public void setIlvl(int ilvl) {
        this.ilvl = ilvl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void computeResetDaily() {

        for (Task task : this.characterTasks) {
            if (task.isDaily()) {
                task.reset();
            }
        }
    }

    public Task getTaskByID(String searchID) {
        for (Task task : this.characterTasks) {
            if (task.getId().equalsIgnoreCase(searchID)) {
                return task;
            }
        }
        return null;
    }

    public void resetWeekly() {
        for (Task task : this.characterTasks) {
            if (!task.isDaily()) {
                task.reset();
            }
        }
        //other daily task will be reset here
        computeResetDaily();
    }

    public List<Task> getTasks() {
        return characterTasks;
    }

    public void setTasks(List<Task> tasks) {
        this.characterTasks = new ArrayList<>();
        for (Task task : tasks) {
            this.characterTasks.add(new Task(task));
        }

    }

}