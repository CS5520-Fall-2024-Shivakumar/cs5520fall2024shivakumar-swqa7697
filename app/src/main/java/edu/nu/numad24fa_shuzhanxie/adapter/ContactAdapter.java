package edu.nu.numad24fa_shuzhanxie.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.nu.numad24fa_shuzhanxie.R;
import edu.nu.numad24fa_shuzhanxie.models.Contact;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<Contact> contacts;
    private OnContactClickListener clickListener;
    private OnContactLongClickListener longClickListener;

    public ContactAdapter(List<Contact> contacts, OnContactClickListener clickListener, OnContactLongClickListener longClickListener) {
        this.contacts = contacts;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public interface OnContactClickListener {
        void onContactClick(Contact contact);
    }

    public interface OnContactLongClickListener {
        void onContactLongClick(Contact contact);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.bind(contacts.get(position));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView phone;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_contact_name);
            phone = itemView.findViewById(R.id.text_contact_phone);
        }

        private void bind(final Contact contact) {
            name.setText(contact.getName());
            phone.setText(contact.getPhone());

            itemView.setOnClickListener(v -> clickListener.onContactClick(contact));
            itemView.setOnLongClickListener(v -> {
                longClickListener.onContactLongClick(contact);
                return true;
            });
        }
    }
}
