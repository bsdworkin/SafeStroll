package com.bendworkin.safestroll;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;



public class EditContacts extends AppCompatActivity {

    static SSContactAdapter adapter;
    static ArrayList<SSContact> myContacts = new ArrayList<>();

    SharedPreferences sharedPreferences;



    public void contactsToMainMenu(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(intent);
    }

    public void addNewContact(View view){
        Intent intent2 = new Intent(getApplicationContext(), AddContact.class);

        startActivity(intent2);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contacts);

        //To give a listview a variable to work with
        ListView myListView  = (ListView)findViewById(R.id.contactsList);

        //Getting the stored information from the app
        sharedPreferences = this.getSharedPreferences("com.bendworkin.safestroll", Context.MODE_PRIVATE);


        //Dynamic Arrays to hold SSContact info
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> phoneNumbers = new ArrayList<>();

        //Clearing the names and each opening so there is not duplicates
        names.clear();
        phoneNumbers.clear();
        myContacts.clear();

        //Attempts to grab stored data from SharedPreferences
        try {

            //Have to use object serializer to unflatten the data from shared preferences
            names = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("names", ObjectSerializer.serialize(new ArrayList<String>())));

            phoneNumbers = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("phoneNumbers", ObjectSerializer.serialize(new ArrayList<String>())));

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Making sure there is data to add to array list
        if (names.size() > 0 && phoneNumbers.size() > 0 ) {

            if (names.size() == phoneNumbers.size()) {

                for (int i = 0; i < names.size(); i++) {

                    //creating a SSContact object for each contact stored
                    myContacts.add(new SSContact(names.get(i), phoneNumbers.get(i)));

                }


            }
        } else {

            myContacts.add(new SSContact("Push me to delete", ""));

        }

        //SSContactAdapter is a ArrayAdapter class to add objects to the list view
        adapter = new SSContactAdapter(this, myContacts);

        //Adds the adapted array list and sets listview
        myListView.setAdapter(adapter);

        //On click listener to be able to delete the contact
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                //Alert dialog to ensure user wants to delete contact
                AlertDialog.Builder show = new AlertDialog.Builder(EditContacts.this);
                        show.setIcon(R.drawable.logo36);
                        show.setTitle("Delete Contact?");
                        show.setMessage("Are you sure you would like to delete this contact from you SSContact List?");

                        //Allows user to delete contact
                        show.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //To delete contact from listview in EditContact
                                SSContact toRemove = adapter.getItem(position);
                                adapter.remove(toRemove);
                                adapter.notifyDataSetChanged();

                                //To change the permanent data stored and restore it
                                try {

                                    ArrayList<String> names = new ArrayList<>();
                                    ArrayList<String> phoneNumbers = new ArrayList<>();

                                    //Advanced for loop to restore and add the new contact to the lists
                                    for(SSContact contact: EditContacts.myContacts){

                                        names.add(contact.name);
                                        phoneNumbers.add(contact.phoneNumber);

                                    }


                                    //Adds the lists to the shared prefs to be stored permanently
                                    sharedPreferences.edit().putString("names", ObjectSerializer.serialize(names)).apply();
                                    sharedPreferences.edit().putString("phoneNumbers", ObjectSerializer.serialize(phoneNumbers)).apply();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        show.setNegativeButton("No", null);
                        show.show();

            }
        });


    }

}
