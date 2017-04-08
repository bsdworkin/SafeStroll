package com.bendworkin.safestroll;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;


public class AddContact extends AppCompatActivity {

    private EditText nameEditText;
    private EditText phoneNumberEditText;
    private String newName;
    private String newPhoneNumber;
    private SSContact newContact;

    public void backToEditContact(View view){
        Intent intent = new Intent(getApplicationContext(), EditContacts.class);

        startActivity(intent);
    }


    public void onClickAddContact(View view){

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        phoneNumberEditText = (EditText)findViewById(R.id.phoneNumberEditText);

        String nullCheck = null;
        String nullCheckPhone = null;

        nullCheck = nameEditText.getText().toString();
        nullCheckPhone = nameEditText.getText().toString();

        //Making sure the text fields are not empty
        if(nullCheck.matches("") || nullCheckPhone.matches("")){

            //Notifies user to input data
            Toast.makeText(this, "Please Enter A Name And Number", Toast.LENGTH_LONG).show();
            return;

        }else {

            newName = nameEditText.getText().toString();
            newPhoneNumber = phoneNumberEditText.getText().toString();

            //Creates contact to store user data
            SSContact newContact = new SSContact(newName, newPhoneNumber);

            //Edits the static arraylist and notifies the adapter of the change
            EditContacts.myContacts.add(newContact);
            EditContacts.adapter.notifyDataSetChanged();

            //Grabs the shared prefs of the app
            SharedPreferences sharedPreferences = this.getSharedPreferences("com.bendworkin.safestroll",Context.MODE_PRIVATE);

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

            //Notifies the user that the new contact was added
            Toast.makeText(this, "Contact Added!", Toast.LENGTH_LONG).show();

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);


    }
}
