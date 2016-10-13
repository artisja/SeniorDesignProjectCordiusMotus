package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by artisja on 10/12/2016.
 */

public class ContactCustomView extends LinearLayout {
    private String name,number;
    private EditText nameEdit,numberEdit;
    private TextView nameText,numberText;

    public ContactCustomView(Context context) {
        super(context);
    }

    public ContactCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ContactCustomView,
                0,0);
        inflate(getContext(),R.layout.contact_custom_view,this);
        try {
            name = a.getString(R.styleable.ContactCustomView_nameLabel);
            number = a.getString(R.styleable.ContactCustomView_phoneLabel);
        }finally {
            a.recycle();
        }

        nameEdit = (EditText) findViewById(R.id.edit_name);
        numberEdit = (EditText) findViewById(R.id.edit_number);
        nameText = (TextView) findViewById(R.id.text_name);
        numberText = (TextView) findViewById(R.id.text_name);

        nameEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    name = nameEdit.getText().toString();
                    nameText.setText(name);
                }
            }
        });


        numberEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    number = numberEdit.getText().toString();
                    numberText.setText(number);
                }
            }
        });

    }


    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Contact getContact(){
        Contact contact = new Contact(name,number);
    return contact;}
}
