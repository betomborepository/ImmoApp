package or.appimmo.betombo.appimmo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registration extends AppCompatActivity {

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_registration);
    }


    public void registerUser(View view)
    {

        TextView emailView = findViewById(R.id.login_email_value);
        TextView passwordView = findViewById(R.id.login_password_value);
        String password = passwordView.getText().toString();


        String email = emailView.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), R.string.enter_email_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),R.string.enter_password_error, Toast.LENGTH_SHORT).show();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                startActivity(new Intent(Registration.this,MainActivity.class));
                else {
                    EditText inputPassword = findViewById(R.id.login_password_value);
                    EditText inputEmail = findViewById(R.id.login_email_value);
                    inputPassword.setError(getString(R.string.error_registration));
                }
                finish();
            }
        });


    }

    public void switchToLogin(View view)
    {
        startActivity(new Intent(this, Login.class));
    }
}
