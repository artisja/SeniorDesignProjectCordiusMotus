package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import mult_603.seniordesignprojectcordiusmotus.AddContactView;

import static mult_603.seniordesignprojectcordiusmotus.R.layout.add_contact;

/**
 * Created by Wes on 10/1/16.
 */
public class AddContactView extends LinearLayout{ //View {
    private View add;
    private ImageView userImg;
    private TextView name;
    private TextView phone;
    private TextView email;

    public AddContactView(Context context, AttributeSet attrs){
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ContactList, 0, 0);
        try {
            String nameLbl = a.getString(R.styleable.ContactList_nameLabel);
            String phoneLbl = a.getString(R.styleable.ContactList_phoneLabel);
            String emailLbl = a.getString(R.styleable.ContactList_emailLabel);
        }
        finally {
            a.recycle();
        }

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(add_contact, this, true);
//
        name = (TextView) this.findViewById(R.id.contact_name);
        phone = (TextView) this.findViewById(R.id.contact_phone);
        email = (TextView) this.findViewById(R.id.contact_email);

        name.setText("This is a name");
        // Invalidate & Request Layout if changing this view.
    }

    public AddContactView(Context context){
        super(context, null);
    }
}
