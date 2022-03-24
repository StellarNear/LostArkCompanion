package stellarnear.lost_ark_companion.Models;

public class RefreshManager {
    private static RefreshManager instance = null;
    private OnRefreshEventListener mListener;
    private boolean wasAnimated = false;

    private RefreshManager() {
    }

    public static RefreshManager getRefreshManager() {
        if (instance == null) {
            instance = new RefreshManager();
        }
        return instance;
    }

    public boolean wasAnimated() {
        return wasAnimated;
    }

    public void isAnimated() {
        this.wasAnimated = true;
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }

    public void triggerRefresh() {
        if (mListener != null) {
            mListener.onEvent();
        }
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }

}
