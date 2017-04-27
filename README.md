# SafeStroll

![icon2](http://i.imgur.com/FcsIgxI.png)

Android app for getting home safe.

## Documentation

### Permissions

In our project we needed to ask an array of user permissions in order to send a text, get their location, 
store data internally, make an alarm sound, as well as record video. This manifest file includes all of these permissions.

```XML
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="com.android.alarm.permission.SET_ALARM" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.SEND_SMS" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.CAMERA" />
```

### Shared Preferences

Since we were learning as we went we started to store data via shared prefrences and storing small objects we 
used a object serializer catered to android in order to store objects in shared preferences.

```Java
public class ObjectSerializer {


    public static String serialize(Serializable obj) throws IOException {
        if (obj == null) return "";
        try {
            ByteArrayOutputStream serialObj = new ByteArrayOutputStream();
            ObjectOutputStream objStream = new ObjectOutputStream(serialObj);
            objStream.writeObject(obj);
            objStream.close();
            return encodeBytes(serialObj.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object deserialize(String str) throws IOException {
        if (str == null || str.length() == 0) return null;
        try {
            ByteArrayInputStream serialObj = new ByteArrayInputStream(decodeBytes(str));
            ObjectInputStream objStream = new ObjectInputStream(serialObj);
            return objStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeBytes(byte[] bytes) {
        StringBuffer strBuf = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
            strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
        }

        return strBuf.toString();
    }

    public static byte[] decodeBytes(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i+=2) {
            char c = str.charAt(i);
            bytes[i/2] = (byte) ((c - 'a') << 4);
            c = str.charAt(i+1);
            bytes[i/2] += (c - 'a');
        }
        return bytes;
    }

}
```

#### Storing to shared preferences

In order to store the objects in Shared Preferences we needed to flatten them by the code below.

```Java
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
```

In order to retrieve our data we need to do the opposite of storing it and unflatten it to be stored again in an array list.

```Java
sharedPreferences = this.getSharedPreferences("com.bendworkin.safestroll", Context.MODE_PRIVATE);

        ArrayList<String> listViewAlarmNames = new ArrayList<>();
        ArrayList<Integer> listViewAlarmTimes = new ArrayList<>();

        listViewAlarmNames.clear();
        listViewAlarmTimes.clear();
        listViewAlarms.clear();

        //Attempts to grab stored data from SharedPreferences
        try {

            //Have to use object serializer to unflatten the data from shared preferences
            listViewAlarmNames = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("alarmTitles", ObjectSerializer.serialize(new ArrayList<String>())));

            listViewAlarmTimes = (ArrayList<Integer>) ObjectSerializer.deserialize(String.valueOf(sharedPreferences.getInt("alarmTimes", Integer.parseInt(ObjectSerializer.serialize(new ArrayList<String>())))));

        } catch (IOException e) {
            e.printStackTrace();
        }
```

### GSON Implementation

We decided to use Google's [Gson](https://github.com/google/gson) library to serialize our alarm settings objects to json. Since they could not be stored in shared preferences, we decided to permanently store them as json files on the phone.

The library was imported with `import com.google.gson.*;`, we used gradle to manage the 3rd party packages.

##### Gson Basic Example:
```Java
Gson gson = new Gson();
try (FileWriter writer = new FileWriter(json)) {
    gson.toJson(list, writer);
} catch (IOException e) {
    e.printStackTrace();
}
```

### File Storage

We stored the json file on the phone, so it remembers the settings even when you close the app.
Here is how the file is written to in the `AlarmSettingsWriter` class.

##### Writing to file:
```Java
public void toJson(ArrayList<AlarmSettings> list) {
        File root = new File(Environment.getExternalStorageDirectory().toString());
        File json = new File(root, "alarm-settings.json");
        Log.i("message", Environment.getExternalStorageDirectory().toString());
        if (!json.exists()) {
            try { json.createNewFile(); }
            catch (IOException e) { e.printStackTrace(); }
        }

        try (FileWriter writer = new FileWriter(json)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("message", root.listFiles().toString());
}
```
###### Reading from file example:
```Java
public ArrayList<AlarmSettings> fromJson() throws FileNotFoundException {
        File root = new File(Environment.getExternalStorageDirectory().toString());
        File json = new File(root, "alarm-settings.json");
        if (!json.exists()) {
            throw new FileNotFoundException("File not found");
        }
        ArrayList<AlarmSettings> list = new ArrayList<>();
        AlarmSettings[] aSettings = null;
        try {
            JsonReader reader = new JsonReader(new FileReader(json));
            aSettings = gson.fromJson(reader, AlarmSettings[].class);
            for (AlarmSettings a : aSettings) {
                list.add(a);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list;
}
```

### Pattern Lock View

We decided to use a 3rd party library for our pattern lock view. Credit to [Aritra Roy](https://github.com/aritraroy/PatternLockView) for designing a very nice lock library. The lock itself treats the dots as numbers from 0-n (n being the number of dots you choose). So verifing the password is just comparing a set of numbers.

##### Example:
```java
if (password == input) {
  //This is called in alarm activity
  if(AlarmActivity.pwPref == true){
    Intent correctPWIntent = new Intent(getApplicationContext(), AlarmActivity.class);
    startActivity(correctPWIntent);

  } else {
    Intent correctPWInt = new Intent(getApplicationContext(), MainActivity.class);
    startActivity(correctPWInt);
}
```

### App Icon

We designed the app icon, using with some inspiration from similar types of apps and services. It was a pretty simple design to throw together in Photoshop.
##### Final Design:

![icon](http://i.imgur.com/cqKV5Ur.png)




