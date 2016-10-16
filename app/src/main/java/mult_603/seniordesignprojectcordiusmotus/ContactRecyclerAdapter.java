package mult_603.seniordesignprojectcordiusmotus;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by artisja on 10/8/2016.
 */

public class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.ViewHolder>{

    public static ArrayList<Contact> contactsArrayAdapter;
    public static int currentPositon;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ContactCustomView contactCustomView;

        public ViewHolder(View itemView) {
            super(itemView);
            contactCustomView = (ContactCustomView) itemView.findViewById(R.id.contact_view);
        }

        public TextView getNameText(){
           return (TextView) contactCustomView.findViewById(R.id.text_name);
        }

        public TextView getNumberText(){
            return (TextView) contactCustomView.findViewById(R.id.text_number);
        }
    }

    public ContactRecyclerAdapter(ArrayList<Contact> arrayList){
        contactsArrayAdapter = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_contact,parent,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        currentPositon=position;
        contactsArrayAdapter.get(position).setName(holder.getNameText().getText().toString());
        contactsArrayAdapter.get(position).setNumber(holder.getNumberText().getText().toString());
    }

    @Override
    public int getItemCount() {
        return contactsArrayAdapter.size();
    }


}
