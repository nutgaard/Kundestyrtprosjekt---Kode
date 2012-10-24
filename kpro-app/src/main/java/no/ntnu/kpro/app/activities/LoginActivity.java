package no.ntnu.kpro.app.activities;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.app.activities.WrapperActivity;
import no.ntnu.kpro.core.service.implementation.SecurityService.UserManager;
import no.ntnu.kpro.core.model.User;

public class LoginActivity extends WrapperActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final UserManager um = new UserManager(getBaseContext());
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.login);



        final User loginUser = new User();





        //TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
        Button login = (Button) findViewById(R.id.btnLogin);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                EditText username = (EditText) findViewById(R.id.usernameEdit);
                EditText password = (EditText) findViewById(R.id.passwordEdit);
                loginUser.setName(username.getText().toString());
                Log.d("Username", username.getText().toString());
                loginUser.setPassword(password.getText().toString());
                Log.d("Password", password.getText().toString());
               // um.createUser(loginUser);

                if (true) { //um.authorize(loginUser) != null


                    Intent i = new Intent(getApplicationContext(), MainTabActivity.class);
                    startActivity(i);
                }
            }
        });
        // Listening to register new account link
//        registerScreen.setOnClickListener(new View.OnClickListener() {
// 
//            public void onClick(View v) {
//                // Switching to Register screen
//                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
//                startActivity(i);
//            }
//        });
    }
}