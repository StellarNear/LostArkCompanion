package stellarnear.lost_ark_companion.Models;

import java.util.ArrayList;
import java.util.List;

public class Character{
	private String name;
	private String work;
	private int ilvl;
	private int restGuardian;
	private int restChaos;

	private List<Task> characterTasks=new ArrayList<>();

	public Character(String name,String work, int ilvl){
		this.setName(name);
		this.work=work;
		this.setIlvl(ilvl);
		this.setRestGuardian(0);
		this.setRestChaos(0);

		characterTasks.add(new Task(true, "Chaos", 2, "iconId"));
		characterTasks.add(new Task(true, "Guardian", 2, "iconId"));
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
			this.restChaos+=(2-getTaskByID("chaos").getCurrentDone())*10;
			this.restGuardian+=(2-getTaskByID("guardian").getCurrentDone())*10;
			for(Task task : this.characterTasks){
				if(task.isDaily()){
					task.reset();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Task getTaskByID(String searchID) {
		for(Task task : this.characterTasks){
			if(task.getId().equalsIgnoreCase(searchID)){
				return task;
			}
		}
		return null;
	}

	public void resetWeekly() {
		for(Task task : this.characterTasks){
			if(!task.isDaily()){
				task.reset();
			}
		}
		//other daily task will be reset here
		computeResetDaily();
	}

	public List<Task> getTasks() {
		return characterTasks;
	}

	public void setTasks(List<Task> tasks){
		this.characterTasks=tasks;
	}
}