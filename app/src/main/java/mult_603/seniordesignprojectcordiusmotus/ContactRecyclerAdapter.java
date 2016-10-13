package mult_603.seniordesignprojectcordiusmotus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.R.attr.name;
import static android.R.attr.onClick;
import static mult_603.seniordesignprojectcordiusmotus.ContactActivity.isSubmitPressed;

/**
 * Created by artisja on 10/8/2016.
 */

public class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.ViewHolder>{

    public ArrayList<Contact> contactsArray;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ContactCustomView contactCustomView;
        public ViewHolder(View itemView) {
            super(itemView);
            contactCustomView = (ContactCustomView) itemView.findViewById(R.id.edit_custom);
        }
    }

    public ContactRecyclerAdapter(ArrayList<Contact> arrayList){
        contactsArray=arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_contact,parent,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        contactsArray.get(position).setName(holder.contactCustomView.getContact().getName());
        contactsArray.get(position).setNumber(holder.contactCustomView.getContact().getNumber());
    }

    @Override
    public int getItemCount() {
        return contactsArray.size();
    }
}
