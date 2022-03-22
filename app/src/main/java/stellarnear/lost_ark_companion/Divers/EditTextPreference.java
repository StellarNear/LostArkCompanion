package stellarnear.lost_ark_companion.Divers;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;


// Cette classe est la pour refresh les values en direct dans les settigns

public class EditTextPreference extends android.preference.EditTextPreference {
    public EditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public EditTextPreference(Context context) {
        super(context);
    }

    public EditTextPreference(Context context, int typeClassText) {
        super(context);
        getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        getEditText().setSelectAllOnFocus(true);
    }

    @Override
    public CharSequence getSummary() {
        String summary = super.getSummary().toString();
        return String.format(summary, getText());
    }
}