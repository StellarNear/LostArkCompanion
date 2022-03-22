package stellarnear.lost_ark_companion.Models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Expedition {

    public List<Character> characters = new ArrayList<>();
    private String name;
    private List<Task> expeditionTasks = new ArrayList<>();
    private List<Task> commonCharacterTasks = new ArrayList<>();
    private String storedDate = null;
    private String encodedPatternDate="yyyy-MM-dd HH:mm";

    public Expedition(String name) {
        this.setName(name);
        this.commonCharacterTasks.add(new Task(true, false, "Chaos Dungeon", 2));
        this.commonCharacterTasks.add(new Task(true, false, "Guardian Raid", 2));
        this.commonCharacterTasks.add(new Task(false, false, "Silmael", 1));
        this.expeditionTasks.add(new Task(false,true,"World Boss",1));
        this.expeditionTasks.add(new Task(false,true,"Ghost Ship",1));
        this.expeditionTasks.add(new Task(false,true,"Chaos Portal",1));
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
        Collections.sort(characters, new Comparator<Character>() {
            public int compare(Character c1, Character c2) {
                return Integer.compare(c2.getIlvl(), c1.getIlvl());
            }
        });
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


        c.setTasks(commonCharacterTasks);
        this.characters.add(c);
    }


    public void createTask(Task task) {
        if (task.isCrossAccount()) {
            expeditionTasks.add(task);
        } else {
            commonCharacterTasks.add(task);
            for (Character c : this.characters) {
                c.getTasks().add(new Task(task));
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


    public LocalDateTime getStoredDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(encodedPatternDate);
        return LocalDateTime.parse(storedDate, formatter);
    }

    public void setStoredDate(LocalDateTime storedDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(encodedPatternDate);
        this.storedDate = storedDate.format(formatter);
    }
}
