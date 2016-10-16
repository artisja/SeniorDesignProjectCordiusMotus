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
    private String name,number, email;
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
        numberText = (TextView) findViewById(R.id.text_number);

        nameEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && !nameEdit.getText().toString().equals("")){
                    nameText.setVisibility(GONE);
                    nameEdit.setVisibility(VISIBLE);
                }else if(!hasFocus && !nameEdit.getText().toString().equals("")) {
                    nameText.setText(nameEdit.getText().toString());
                    nameText.setVisibility(VISIBLE);
                    nameEdit.setVisibility(GONE);
                    ContactRecyclerAdapter.contactsArrayAdapter.get(ContactRecyclerAdapter.currentPositon).setName(nameText.getText().toString());
                }
            }
        });

        nameText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                    nameEdit.setText(nameText.getText().toString());
                    nameText.setVisibility(GONE);
                    nameEdit.setVisibility(VISIBLE);
                ContactRecyclerAdapter.contactsArrayAdapter.get(ContactRecyclerAdapter.currentPositon).setNumber(nameText.getText().toString());

            }
        });

        numberEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && !nameEdit.getText().toString().equals("")){
                    numberText.setVisibility(GONE);
                    numberEdit.setVisibility(VISIBLE);
                    nameText.setText(numberEdit.getText());
                }else if (!hasFocus && !nameEdit.getText().toString().equals("")) {
                    numberEdit.setVisibility(GONE);
                    numberText.setText(numberEdit.getText().toString());
                    numberText.setVisibility(VISIBLE);
                    ContactRecyclerAdapter.contactsArrayAdapter.get(ContactRecyclerAdapter.currentPositon).setNumber(numberText.getText().toString());
                }
            }
        });

        numberText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                numberEdit.setText(numberText.getText().toString());
                numberText.setVisibility(GONE);
                numberEdit.setVisibility(VISIBLE);
                ContactRecyclerAdapter.contactsArrayAdapter.get(ContactRecyclerAdapter.currentPositon).setNumber(numberText.getText().toString());

            }
        });

    }


    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() { return email; }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setEmail(String email){this.email = email;}


    public Contact getContact(){
        Contact contact = new Contact(name,number,email);
    return contact;}
}
