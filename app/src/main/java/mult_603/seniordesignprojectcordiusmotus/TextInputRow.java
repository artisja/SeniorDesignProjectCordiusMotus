package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by artisja on 9/25/2016.
 */

public class TextInputRow extends LinearLayout {

    private TextView contactLabel,submittedContact;
    private EditText contactEditText;

    private String mContactLabel,mSubmittedContact;

    public TextInputRow(Context context) {
        super(context);
    }


    public TextInputRow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TextInputRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

   public TextInputRow(Context context,AttributeSet attrs){
        super(context, attrs);

       TypedArray a =context.getTheme().obtainStyledAttributes(
               attrs,
               R.styleable.TextInputRow,
               0,0);
       try {

           mContactLabel = a.getString(R.styleable.TextInputRow_textInput);
           mSubmittedContact = a.getString(R.styleable.TextInputRow_textLabel);
       }finally {
           a.recycle();
       }


    }
}
