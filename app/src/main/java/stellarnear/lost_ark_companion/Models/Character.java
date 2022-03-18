package stellarnear.lost_ark_companion.Models;

import java.util.ArrayList;
import java.util.List;

import stellarnear.lost_ark_companion.Log.CustomLog;

public class Character {
    private String name;
    private String work;
    private Integer ilvl;
    private int restGuardian;
    private int restChaos;
    private String id;
    private String workId;

    private final CustomLog log = new CustomLog(this.getClass());

	private List<Task> characterTasks = new ArrayList<>();

    public Character(String name, String work, Integer ilvl) {
        this.setName(name);
        this.setWork(work);
        this.setIlvl(ilvl);
        this.setRestGuardian(0);
        this.setRestChaos(0);
        this.setId(name.replace(" ", "_").toLowerCase());
        this.setWorkId(work.replace(" ", "_").toLowerCase());
        characterTasks.add(new Task(true, "Chaos", 2, "iconId"));
        characterTasks.add(new Task(true, "Guardian", 2, "iconId"));
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

    public int getRestChaos() {
        return restChaos;
    }

    public void setRestChaos(int restChaos) {
        this.restChaos = restChaos;
        ExpeditionManager.saveToDB();
    }

    public int getRestGuardian() {
        return restGuardian;
    }

    public void setRestGuardian(int restGuardian) {
        this.restGuardian = restGuardian;
        ExpeditionManager.saveToDB();
    }

    public int getIlvl() {
        return ilvl;
    }

    public void setIlvl(int ilvl) {
        this.ilvl = ilvl;
        ExpeditionManager.saveToDB();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        ExpeditionManager.saveToDB();
    }

    public void computeResetDaily() {
        try {
            this.restChaos += (2 - getTaskByID("chaos").getCurrentDone()) * 10;
            this.restGuardian += (2 - getTaskByID("guardian").getCurrentDone()) * 10;
        } catch (Exception e) {
            log.err("Could not set rest bar for chaos and guardian", e)
        }
        for (Task task : this.characterTasks) {
            if (task.isDaily()) {
                task.reset();
            }
        }
    }

    private Task getTaskByID(String searchID) {
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
        this.characterTasks = tasks;
    }
}