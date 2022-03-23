package stellarnear.lost_ark_companion.Models;

public class RefreshManager {
    private static RefreshManager instance=null;
    private OnRefreshEventListener mListener;
    private boolean wasAnimated=false;

    public boolean wasAnimated(){
        return wasAnimated;
    }

    public void isAnimated(){
        this.wasAnimated=true;
    }


    public static RefreshManager getRefreshManager(){
        if(instance==null){
            instance=new RefreshManager();
        }
        return instance;
    }

    private RefreshManager(){
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }

    public void triggerRefresh(){
        if(mListener!=null){
            mListener.onEvent();
        }
    }

}
