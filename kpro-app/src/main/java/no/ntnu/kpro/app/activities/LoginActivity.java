package no.ntnu.kpro.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import no.ntnu.kpro.app.R;

public class LoginActivity extends WrapperActivity {

    EditText txtUserName;
    EditText txtPassword;
    Button btnLogin;
    Button btnCancel;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        txtUserName = (EditText) this.findViewById(R.id.txtUname);
        txtPassword = (EditText) this.findViewById(R.id.txtPwd);
        btnLogin = (Button) this.findViewById(R.id.btnLogin);
        btnCancel = (Button) this.findViewById(R.id.btnCancel);
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if ((txtUserName.getText().toString()).equals(txtPassword.getText().toString())) {
                   
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
