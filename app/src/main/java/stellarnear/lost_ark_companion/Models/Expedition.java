package stellarnear.lost_ark_companion.Models;

import java.util.ArrayList;
import java.util.List;

public class Expedition {

    private String name;

    private List<Task> expeditionTasks = new ArrayList<>();
    private List<Task> commonCharacterTasks = new ArrayList<>();

    public static List<Character> characters = new ArrayList<>(); //todo inti from settings


    public Expedition(String name) {
        this.setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<Character> getCharacters() {
        //TODO order by ilvl
        return characters;
    }

    public void resetDaily() {
        for (Task task : this.expeditionTasks) {
            if (task.isDaily()) {
                task.reset();
            }
        }
        for (Character character : Expedition.getCharacters()) {
            character.computeResetDaily();
        }
        ExpeditionManager.saveToDB();
    }

    private void saveExpedition() {
    }

    public void resetWeekly() {
        for (Task task : this.expeditionTasks) {
            task.reset();
        }
        resetDaily();
    }


    public void createCharacter(Character c) {
        c.setTasks(new ArrayList(commonCharacterTasks));
        this.characters.add(c);
    }


    public void createTask(Task task) {
        if (task.isCrossAccount()) {
            expeditionTasks.add(task);
        } else {
            for (Character c : this.characters) {
                c.getTasks().add(task);
            }
        }
    }


}
