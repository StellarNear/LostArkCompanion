package stellarnear.lost_ark_companion.Models;


public class OneLineDisplayCharacter {




	public static LinearLayout getOneLine(final Character character){
		LinearLayout line= new LinearLayout(); // from ressource (charchater_line)

		// create layout avec
		// linear horizontal (logo + nom + ilvl)
		// autre layout (progress bar)
		ImageView chaosBar = getProgressBar(R.id.chaos_bar);
		// surchargé pour chaos
		// surchargé pour gardien
	// linear horizontal avec chaque task element
	LinearLayout tasks = new LinearLayout();//horizontal
	for(Task task: character.getTasks()){
		LinearLayout taskElement = ElementTaskDisplay.getTaskElement(task);
		taskElement.setOnLongClickListner(new EventListener() {

			@Override
			public void handleEvent(Event evt) {
				character.getTasks().remove(task);
				ExpeditionManager.saveToDB();
				refreshUI();
			}

		});

	}

	}

	private Object getDrawable(String id) {
		return null;
	}

	private ImageView getProgressBar(String id ){
		final ImageView image = dialogView.findViewById();
        image.post(new Runnable() {
            @Override
            public void run() {
                ImageView imgHealth = dialogView.findViewById(id);
                ViewGroup.LayoutParams para= (ViewGroup.LayoutParams) imgHealth.getLayoutParams();
                int oriWidth=image.getMeasuredWidth();
                int oriHeight=image.getMeasuredHeight();
                int height=(int) (oriHeight*0.355); //c'est le rapport entre le haut gargouille et la barre
                Double coef = (double) aquene.getCurrentResourceValue("resource_hp")/aquene.getAllResources().getResource("resource_hp").getMax();
                if(coef<0d){coef=0d;} //pour les hp negatif
                if(coef>1d){coef=1d;}
                para.width=(int) (coef*oriWidth);
                para.height=height;
                imgHealth.setLayoutParams(para);
                if(coef>=0.75){
                    imgHealth.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_health_ok));
                } else if (coef <0.75 && coef >=0.5){
                    imgHealth.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_health_abovehalf));
                } else if (coef <0.5 && coef >=0.25){
                    imgHealth.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_health_underhalf));
                } else {
                    imgHealth.setImageDrawable(mC.getDrawable(R.drawable.bar_gradient_health_notok));
                }
            }
        });
		return image;
	}

}
