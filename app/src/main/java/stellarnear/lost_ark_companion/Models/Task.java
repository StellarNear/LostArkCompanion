package stellarnear.lost_ark_companion.Models;

public class Task {

	private boolean daily;
	private boolean corssAccount;
	private String name;
	private int occurance;
	private int currentDone;
	private String id;

	public Task(boolean daily, boolean crossAccount,String name, int occurance) {
		this.daily = daily;
		this.crossAccount=crossAccount;
		this.name = name;
		this.occurance = occurance;
		this.currentDone=0;
		this.id = name.replace(" ","_").toLowerCase();
	}

	public String getId() {
		return id;
	}

	public void setIconId(String iconId) {
		this.id = iconId;
	}

	public int getOccurance() {
		return occurance;
	}

	public void setOccurance(int occurance) {
		this.occurance = occurance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCrossAccount() {
		return crossAccount;
	}

	public boolean isDaily() {
		return daily;
	}

	public void setDaily(boolean daily) {
		this.daily = daily;
		ExpeditionManager.saveToDB();
	}

	public int getCurrentDone() {
		return this.currentDone;
	}

	public void addDone() {
		this.currentDone++;
		if(this.currentDone>this.occurance){
			this.currentDone=this.occurance;
		}
	}

	public void reset() {
		this.currentDone=0;
	}

	public void cancelOne() {
		this.currentDone--;
		if(this.currentDone<0){
			this.currentDone=0;
		}
	}



}
