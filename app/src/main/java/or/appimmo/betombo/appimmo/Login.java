package or.appimmo.betombo.appimmo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
    }

    public void signIn(View view)
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

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                startActivity(new Intent(Login.this,MainActivity.class));
                else
                {
                   // startActivity(new Intent(Login.this, Login.class));
                    EditText inputPassword = findViewById(R.id.login_password_value) ;
                    EditText inputEmail = findViewById(R.id.login_email_value) ;
                    inputPassword.setError(getString(R.string.error_login));
                }
                finish();
            }
        });
    }

    public void switchToRegister(View view)
    {
        startActivity(new Intent(this, Registration.class));
    }
}
