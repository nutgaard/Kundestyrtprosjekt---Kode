package no.ntnu.kpro.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import no.ntnu.kpro.app.R;
import no.ntnu.kpro.core.model.ModelProxy.IUser;
import no.ntnu.kpro.core.model.User;
import no.ntnu.kpro.core.service.implementation.SecurityService.UserManager;

public class LoginActivity extends WrapperActivity {
    final static String TAG = "KPRO-GUI-LOGIN";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        final UserManager um = new UserManager(getBaseContext());
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.login);

        Button login = (Button) findViewById(R.id.btnLogin);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final User loginUser = new User();
                EditText username = (EditText) findViewById(R.id.usernameEdit);
                EditText password = (EditText) findViewById(R.id.passwordEdit);
                loginUser.setName(username.getText().toString());
                loginUser.setPassword(password.getText().toString());
                Log.d(TAG, "Trying to log in");
                IUser user = getServiceProvider().login(loginUser);

                if (user != null) { //um.authorize(loginUser) != null
                    Log.d(TAG, "Login successful");
                    Intent i = new Intent(getApplicationContext(), MainTabActivity.class);
                    startActivity(i);
                } else {
                    Log.d(TAG, "Login failed");
                    TextView txtLoginMessage = (TextView) findViewById(R.id.txtLoginMessage);
                    txtLoginMessage.setText("Incorrect login information");
                }
            }
        });
    }
}