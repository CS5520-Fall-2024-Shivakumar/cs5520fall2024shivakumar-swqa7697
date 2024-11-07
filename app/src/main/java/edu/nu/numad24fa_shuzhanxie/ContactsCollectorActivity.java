package edu.nu.numad24fa_shuzhanxie;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.nu.numad24fa_shuzhanxie.adapter.ContactAdapter;
import edu.nu.numad24fa_shuzhanxie.models.Contact;

public class ContactsCollectorActivity extends AppCompatActivity {
    private static final String CONTACT_STORAGE_PATH = "contacts.json";

    private List<Contact> contacts;
    private ContactAdapter contactAdapter;
    private RecyclerView recyclerView;
    private TextView noContactIndicator;
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contacts_collector);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contacts_collector), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recycler_view_contacts);
        noContactIndicator = findViewById(R.id.text_no_contact);

        // Initialize contacts
        contacts = new ArrayList<>();
        loadContacts();
        toggleNoContactIndicator();

        contactAdapter = new ContactAdapter(contacts, this::makeCall, this::showModifyContactDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactAdapter);

        // Set onClick listener for add contact button
        FloatingActionButton fab = findViewById(R.id.fab_contact_add);
        fab.setOnClickListener(v -> showAddContactDialog());
    }

    private void makeCall(Contact contact) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + contact.getPhone()));
        startActivity(intent);
    }

    private void showAddContactDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_contact_add, null);
        EditText nameEdit = dialogView.findViewById(R.id.edit_text_name);
        EditText phoneEdit = dialogView.findViewById(R.id.edit_text_phone);

        new AlertDialog.Builder(this)
                .setTitle("Add Contact")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameEdit.getText().toString();
                    String phone = phoneEdit.getText().toString();
                    if (!name.isEmpty() && !phone.isEmpty()) {
                        int contactPos = addContact(new Contact(name, phone));
                        showEditSnackbar(contactPos);
                    } else {
                        Toast.makeText(this, "Invalid name or phone number",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditSnackbar(int contactPos) {
        Snackbar.make(recyclerView, "Contact Added", Snackbar.LENGTH_LONG)
                .setAction("Edit", v -> showEditContactDialog(contactPos))
                .show();
    }

    private void showUndoSnackbar(Contact contact, int contactPos) {
        Snackbar.make(recyclerView, "Contact Deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> addContact(contact, contactPos))
                .show();
    }

    private int addContact(Contact contact) {
        contacts.add(contact);
        int contactPos = contacts.size() - 1;
        contactAdapter.notifyItemInserted(contactPos);
        toggleNoContactIndicator();
        storeContacts();
        return contactPos;
    }

    private void addContact(Contact contact, int contactPos) {
        contacts.add(contactPos, contact);
        contactAdapter.notifyItemInserted(contactPos);
        toggleNoContactIndicator();
        storeContacts();
    }

    private void editContact(int contactPos, String newName, String newPhone) {
        if (!newName.isEmpty() && !newPhone.isEmpty()) {
            contacts.get(contactPos).setName(newName);
            contacts.get(contactPos).setPhone(newPhone);
            contactAdapter.notifyItemChanged(contactPos);
            storeContacts();
        } else {
            Toast.makeText(this, "Invalid name or phone number",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteContact(int contactPos) {
        if (contactPos >= 0 && contactPos < contacts.size()) {
            contacts.remove(contactPos);
            contactAdapter.notifyItemRemoved(contactPos);
            contactAdapter.notifyItemRangeChanged(contactPos, contacts.size());
            toggleNoContactIndicator();
            storeContacts();
        }
    }

    private void showModifyContactDialog(Contact contact) {
        new AlertDialog.Builder(this)
                .setTitle("Choose Action")
                .setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
                    int contactPos = contacts.indexOf(contact);
                    if (contactPos != -1) {
                        if (which == 0) {
                            showEditContactDialog(contactPos);
                        } else if (which == 1) {
                            deleteContact(contactPos);
                            showUndoSnackbar(contact, contactPos);
                        }
                    }
                })
                .show();
    }

    private void showEditContactDialog(int contactPos) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_contact_add, null);
        EditText nameEdit = dialogView.findViewById(R.id.edit_text_name);
        EditText phoneEdit = dialogView.findViewById(R.id.edit_text_phone);

        nameEdit.setText(contacts.get(contactPos).getName());
        phoneEdit.setText(contacts.get(contactPos).getPhone());

        new AlertDialog.Builder(this)
                .setTitle("Edit Contact")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameEdit.getText().toString();
                    String phone = phoneEdit.getText().toString();
                    editContact(contactPos, name, phone);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void storeContacts() {
        String json = gson.toJson(contacts);

        try (FileOutputStream fos = openFileOutput(CONTACT_STORAGE_PATH, Context.MODE_PRIVATE)) {
            fos.write(json.getBytes());
        } catch (IOException e) {
            Log.e("Contact Collector", "IOException", e);
        }
    }

    private void loadContacts() {
        contacts = new ArrayList<>();
        File file = new File(getFilesDir(), CONTACT_STORAGE_PATH);

        if (file.exists()) {
            try (FileInputStream fis = openFileInput(CONTACT_STORAGE_PATH);
                 InputStreamReader isr = new InputStreamReader(fis);
                 BufferedReader reader = new BufferedReader(isr)) {

                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                Contact[] contactArray = gson.fromJson(json.toString(), Contact[].class);
                if (contactArray != null) {
                    contacts = new ArrayList<>(Arrays.asList(contactArray));
                }
            } catch (IOException e) {
                Log.e("Contact Collector", "IOException", e);
                contacts = new ArrayList<>();
            }
        }
    }

    private void toggleNoContactIndicator() {
        if (contacts.isEmpty()) {
            noContactIndicator.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noContactIndicator.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}