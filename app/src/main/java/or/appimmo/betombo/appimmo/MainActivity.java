package or.appimmo.betombo.appimmo;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebauseAuth;
    private FirebaseStorage mFirebaseStorage;
    private TextView mTextMessage;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    replaceMainFragment(new Home());setTitle("Home");
                    return true;
                case R.id.navigation_search:
                    replaceMainFragment(new List());setTitle("Search");
                    return true;
                case R.id.navigation_notifications:
                    replaceMainFragment(new Settings()); setTitle("Settings");
                    return true;
            }
            return false;
        }

    };
    private Uri imageUri;


    private  void replaceMainFragment( android.app.Fragment fragment)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String ID = getIntent().getStringExtra("id");
        boolean isUpload = getIntent().getStringExtra("upload") != null;
        boolean isUploaded = getIntent().getStringExtra("uploaded") != null;
        boolean isSetting = getIntent().getStringExtra("settings") != null;
        boolean isNotification = getIntent().getStringExtra("notificationImmObject") != null;
        if(ID != null)
        {
            initDetailsFragment(ID);
        }else if(isUpload)
        {
            replaceMainFragment(new Upload());
            setTitle("Upload");
        }
        else if(isUploaded)
        {
            replaceMainFragment(new List());
            setTitle("List");
        }
        else if(isSetting)
        {
            replaceMainFragment(new List());
            setTitle("List");
        }
        else if(isNotification)
        {
            Detail detailFrag = new Detail();
            String idImmoObject =  getIntent().getStringExtra("notificationImmObject");
            detailFrag.InitializeViewContent(idImmoObject);


            replaceMainFragment(detailFrag);
            setTitle("Detail");
        }
        else{

            replaceMainFragment(new Home());
            setTitle("Home");
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebauseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mDatabase.child("house").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot child : dataSnapshot.getChildren())
                {
                    ImmoObject immo = child.getValue(ImmoObject.class);

                    if(immo != null)
                        notifyChangeOnHouse(immo);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }



    private void notifyChangeOnHouse(ImmoObject immo)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.immo_house)
                        .setContentTitle("Update on house" + immo.getName())
                        .setContentText("Adresse:" + immo.getAdresse() + ", Status:" + immo.getStatus() );


        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("notificationImmObject", immo.getID());

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        mBuilder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
                int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        MenuItem item =  menu.findItem(R.id.toolbar_search);
        SearchView sv = (SearchView) item.getActionView();

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                mDatabase.child("house").orderByChild("name").startAt(s).endAt(s + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        RecyclerView rclv = findViewById(R.id.list_container);
                        ArrayList<ImmoObject> immoData = new ArrayList<>();
                        if(rclv != null)
                        {
                            for(DataSnapshot house : dataSnapshot.getChildren())
                            {
                                ImmoObject immo = house.getValue(ImmoObject.class);
                                immoData.add(immo);

                            }
                            ImmoAdapter adapter = new ImmoAdapter(immoData);
                            rclv.setAdapter(adapter);
                        }
                        else
                        {
                            //todo ch

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }


    public  void switchToUpload(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("upload", "upload");

        startActivity(intent);
    }


    public  void createHouse(View view)
    {
        TextView txt = findViewById(R.id.immo_name);
        TextView adress = findViewById(R.id.immo_adresse_label);
        TextView date = findViewById(R.id.immo_date);
        TextView status = findViewById(R.id.immo_status);
        TextView price = findViewById(R.id.immo_price);
        TextView description = findViewById(R.id.immo_description);
        RatingBar rating = findViewById(R.id.ratingBar);


        //à décommentere dans la version final log
        String email = "user@testEmail.com";
        String contact = this.getString(R.string.immo_empty_value);
        FirebaseUser user = mFirebauseAuth.getCurrentUser();
        if(user != null)
        {
            String id  = user.getUid();
            User userData = getUserByID(id);
            contact = userData != null ? userData.getContact() : this.getString(R.string.immo_empty_value) ;
        }

        //envoyer les données
        String key = mDatabase.child("child").push().getKey();
        ImmoObject immo = new ImmoObject(key,rating.getNumStars(), "immo_images_" + UUID.randomUUID().toString(), txt.getText().toString(), adress.getText().toString(), contact, false, email, date.getText().toString(), description.getText().toString());
        mDatabase.child("house").child(key).setValue(immo);

        uploadImage(immo.getImage());

    }


    private User getUserByID(String id)
    {
        final User[] user = new User[1];

        mDatabase.child("users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user[0] = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return user[0];
    }


    private int PICK_IMAGE_REQUEST = 118;
    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null ) {
            ContentResolver contentResolver;
            contentResolver = getContentResolver();
            Uri filePath = data.getData();
            try {
                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                 Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

               Matrix matrix = new Matrix();
                matrix.postRotate(90);
                selectedImage = Bitmap.createBitmap(selectedImage, 0, 0, selectedImage.getWidth(), selectedImage.getHeight(), matrix, true);

                ImageView imageView = findViewById(R.id.upload_image);
                imageView.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(String imageName) {

        if(imageUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref =   mFirebaseStorage.getReference().child("images/"+ imageName);
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();



                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            intent.putExtra("uploaded", "uploaded");
                            startActivity(intent);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }



     public void showDetailsActivity(View view)
    {
        TextView v = view.findViewById(R.id.immo_id_receiver);
        String id = v.getText().toString();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id", id);
        startActivity( intent);
    }

    public void initDetailsFragment(String id)
    {
        Detail detail = new Detail();
        detail.InitializeViewContent(id);
        replaceMainFragment(detail);
        setTitle("Details");
    }

    public void updateConfig (View v)
    {
        Spinner spinner = findViewById(R.id.spinner);

        String lang = getResources().getStringArray(R.array.langage_array_value)[spinner.getSelectedItemPosition()];


        setLocale(lang);

        Intent intent = new Intent(v.getContext(), MainActivity.class);
        intent.putExtra("settings", "settings");
        startActivity(intent);
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);


    }

}
