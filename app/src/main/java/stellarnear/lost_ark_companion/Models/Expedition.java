package stellarnear.lost_ark_companion.Models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Expedition {

    private final List<Task> expeditionTasks = new ArrayList<>();
    private final List<Task> commonCharacterTasks = new ArrayList<>();

    public List<Character> characters = new ArrayList<>();
    private String name;
    private String storedDate = null;

    public Expedition(String name) {
        this.setName(name);

        this.commonCharacterTasks.add(new Task(true, false, "Chaos Dungeon", 2, "chaos_dungeon_ico"));
        this.commonCharacterTasks.add(new Task(true, false, "Guardian Raid", 2, "guardian_raid_ico"));
        this.commonCharacterTasks.add(new Task(false, false, "Silmael", 1, "silmael_ico"));

        this.expeditionTasks.add(new Task(true, true, "Island", 1, "island_ico"));
        this.expeditionTasks.add(new Task(true, true, "World Boss", 1, "world_boss_ico").setAppearance(Arrays.asList("Tuesday", "Friday", "Sunday")));
        this.expeditionTasks.add(new Task(false, true, "Ghost Ship", 1, "ghost_ship_ico").setAppearance(Arrays.asList("Tuesday", "Thursday", "Saturday")));
        this.expeditionTasks.add(new Task(true, true, "Chaos Portal", 1, "chaos_portal_ico").setAppearance(Arrays.asList("Monday", "Thursday", "Saturday", "Sunday")));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getExpeditionTasks() {
        Collections.sort(expeditionTasks, new Comparator<Task>() {
            @Override
            public int compare(final Task t1, final Task t2) {
                if (t1.isDaily() && !t2.isDaily()) {
                    return -1;
                } else if (!t1.isDaily() && t2.isDaily()) {
                    return 1;
                } else {
                    return t1.getName().compareToIgnoreCase(t2.getName());
                }
            }
        });
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

    public void createTaskForCharacters(Task task, List<String> selectedCharactersIds) {
        for (String charId : selectedCharactersIds) {
            Character character = getCharacterById(charId);
            character.getTasks().add(new Task(task));
        }
    }

    public void deleteTask(Task task) {
        this.expeditionTasks.removeIf(x -> x.getId().equalsIgnoreCase(task.getId()));
        this.commonCharacterTasks.removeIf(x -> x.getId().equalsIgnoreCase(task.getId()));

        for (Character c : this.characters) {
            c.getTasks().removeIf(x -> x.getId().equalsIgnoreCase(task.getId()));
        }

    }


    public LocalDate getStoredDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return LocalDate.parse(storedDate, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public void setStoredDate(LocalDate storedDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.storedDate = storedDate.format(formatter);
    }

    public Character getCharacterById(String characterId) {
        for (Character character : this.characters) {
            if (character.getId().equalsIgnoreCase(characterId)) {
                return character;
            }
        }
        return null;
    }

}
