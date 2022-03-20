package stellarnear.lost_ark_companion.Models;

import java.util.ArrayList;
import java.util.List;

public class Expedition {

    public List<Character> characters = new ArrayList<>(); //todo inti from settings
    private String name;
    private List<Task> expeditionTasks = new ArrayList<>();
    private List<Task> commonCharacterTasks = new ArrayList<>();
    private String storedDate="";


    public Expedition(String name) {
        this.setName(name);
        this.commonCharacterTasks.add(new Task(true,false,"Chaos Dungeon",2));
        this.commonCharacterTasks.add(new Task(true,false,"Guardian Raid",2));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getExpeditionTasks() {
        return expeditionTasks;
    }

    public List<Task> getCommonCharacterTasks() {
        return commonCharacterTasks;
    }

    public List<Character> getCharacters() {
        //TODO order by ilvl
        return characters;
    }

    public void resetDaily() {
        for (Task task : this.expeditionTasks) {
            if (task.isDaily()) {
                task.reset();
            }
        }
        for (Character character : this.characters) {
            character.computeResetDaily();
        }

    }

    public void resetWeekly() {
        for (Task task : this.expeditionTasks) {
            task.reset();
        }
        for (Character c : this.characters) {
            c.resetWeekly();
        }
    }


    public void createCharacter(Character c) {
        c.setTasks(new ArrayList(commonCharacterTasks));
        this.characters.add(c);
    }


    public void createTask(Task task) {
        if (task.isCrossAccount()) {
            expeditionTasks.add(task);
        } else {
            commonCharacterTasks.add(task);
            for (Character c : this.characters) {
                c.getTasks().add(task);
            }
        }
    }

    public void deleteTask(Task task) {
        this.expeditionTasks.removeIf(x -> x.getId().equalsIgnoreCase(task.getId()));
        this.commonCharacterTasks.removeIf(x -> x.getId().equalsIgnoreCase(task.getId()));

        for (Character c : this.characters) {
            c.getTasks().removeIf(x -> x.getId().equalsIgnoreCase(task.getId()));
        }

    }


    public String getStoredDate() {
        return storedDate;
    }

    public void setStoredDate(String storedDate) {
        this.storedDate = storedDate;
    }
}
