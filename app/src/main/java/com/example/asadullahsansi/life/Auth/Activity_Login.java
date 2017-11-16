package com.example.asadullahsansi.life.Auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.asadullahsansi.life.MainActivity;
import com.example.asadullahsansi.life.R;
import com.example.asadullahsansi.life.Util.Util_Func;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class Activity_Login extends AppCompatActivity  implements View.OnClickListener{

    private FirebaseAuth _auth;

    private TextInputLayout input_layout_email, input_layout_password;
    private EditText edit_email, edit_pass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__login);

        _auth = FirebaseAuth.getInstance();

        findViewById(R.id.btn_signin).setOnClickListener(this);
        findViewById(R.id.btn_signup).setOnClickListener(this);

        input_layout_email = (TextInputLayout) findViewById(R.id.input_layout_email);
        input_layout_password = (TextInputLayout) findViewById(R.id.input_layout_password);

        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_pass = (EditText) findViewById(R.id.edit_pass);


        edit_email.addTextChangedListener(new MyTextWatcher(edit_email));
        edit_pass.addTextChangedListener(new MyTextWatcher(edit_pass));
    }

    private void Login(String email, String password) {


        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }
        if (Util_Func.isNetworkAvaliable(Activity_Login.this)) {
            final AlertDialog dialog = new SpotsDialog(Activity_Login.this, "Login Account...");
            dialog.show();
            _auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        dialog.dismiss();
                        startActivity(new Intent(Activity_Login.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    } else {
                        dialog.dismiss();
                        Util_Func.Alert(Activity_Login.this, "Wrong Email, Password", "Enter Correct Email, Password!");
                    }

                }
            });

        } else {
            Util_Func.Alert(Activity_Login.this, "No Internet Connection", "Enable Wifi or Mobile Data");

        }
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edit_email:
                    validateEmail();
                    break;
                case R.id.edit_pass:
                    validatePassword();
                    break;


            }
        }
    }


    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateEmail() {
        String email = edit_email.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            input_layout_email.setError(getString(R.string.err_msg_email));
            requestFocus(edit_email);
            return false;
        } else {
            input_layout_email.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (edit_pass.getText().toString().trim().isEmpty() || edit_pass.getText().length() < 6) {
            input_layout_password.setError(getString(R.string.err_msg_password));
            requestFocus(edit_pass);
            return false;
        } else {
            input_layout_password.setErrorEnabled(false);
        }

        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signin:
                Login(edit_email.getText().toString(), edit_pass.getText().toString());
                break;
            case R.id.btn_signup:
                startActivity(new Intent(Activity_Login.this,Activity_Register.class));
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;

        }
    }



}
