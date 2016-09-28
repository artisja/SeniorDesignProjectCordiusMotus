package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by artisja on 9/25/2016.
 */

public class TextInputRow extends RelativeLayout {

    private  TextView contactLabel,submittedContact;
    private  EditText contactEditText;

    private String mContactLabel,mSubmittedContact;

    public TextInputRow(Context context) {
        super(context);
    }


    public TextInputRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

   public TextInputRow(Context context,AttributeSet attrs) {
       super(context, attrs);

       TypedArray a = context.getTheme().obtainStyledAttributes(
               attrs,
               R.styleable.TextInputRow,
               0, 0);
       try {

           mContactLabel = a.getString(R.styleable.TextInputRow_textInput);
           mSubmittedContact = a.getString(R.styleable.TextInputRow_textLabel);
       } finally {
           a.recycle();
       }

       LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       inflater.inflate(R.layout.text_input_row, this, true);

       contactLabel = (TextView) findViewById(R.id.label_textview_row);
       contactEditText = (EditText) findViewById(R.id.input_edittext_row);
       submittedContact = (TextView) findViewById(R.id.input_textview_row);

       contactLabel.setText(R.string.contact_label);
   }

    public void switchViews(){
        submittedContact.setText(contactEditText.getText());
        contactEditText.setVisibility(GONE);
        submittedContact.setVisibility(VISIBLE);
    }

    public EditText getContactEditText() {
        return contactEditText;
    }

    public TextView getSubmittedContact() {
        return submittedContact;
    }

    public TextView getContactLabel() {
        return contactLabel;
    }
}