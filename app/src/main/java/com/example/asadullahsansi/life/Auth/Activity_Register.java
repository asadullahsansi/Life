package com.example.asadullahsansi.life.Auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asadullahsansi.life.AboutMe.Activity_About;
import com.example.asadullahsansi.life.R;
import com.example.asadullahsansi.life.Util.Util_Func;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dmax.dialog.SpotsDialog;

/**
 * Created by asadullahsansi on 11/11/17.
 */

public class Activity_Register extends AppCompatActivity implements View.OnClickListener {


    private EditText edit_pass, edit_email, edit_name, edit_username, edit_date;
    private TextInputLayout input_layout_email, input_layout_password;


    private FirebaseAuth _auth;


    private RadioGroup radioGroup;
    private Spinner spinnerUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__register);


        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_pass = (EditText) findViewById(R.id.edit_pass);

        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_date = (EditText) findViewById(R.id.edit_date);
        edit_date.addTextChangedListener(textChangeListener);

        input_layout_email = (TextInputLayout) findViewById(R.id.input_layout_email);
        input_layout_password = (TextInputLayout) findViewById(R.id.input_layout_password);


        //textchangelistener
        edit_email.addTextChangedListener(new MyTextWatcher(edit_email));
        edit_pass.addTextChangedListener(new MyTextWatcher(edit_pass));


        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        spinnerUser = (Spinner) findViewById(R.id.spinnerUsers);
        final List<String> listCategory = new ArrayList<String>();
        listCategory.add("Select Username being supporting");
        spinnerUser.setSelection(0);


        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listCategory);
        spinnerUser.setAdapter(adapterCategory);


        radioGroup.setOnCheckedChangeListener(radioCheckChangeListener);


        findViewById(R.id.btn_register).setOnClickListener(this);

        _auth = FirebaseAuth.getInstance();


    }


    private RadioGroup.OnCheckedChangeListener radioCheckChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.radioUser)
                spinnerUser.setVisibility(View.GONE);
            else if (checkedId == R.id.radioSupporter)
                spinnerUser.setVisibility(View.VISIBLE);

        }
    };


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_register:
                RegisterAccount(edit_email.getText().toString(), edit_pass.getText().toString());
                break;

        }

    }


    private void RegisterAccount(String email, String pass) {
        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }


        if (Util_Func.isNetworkAvaliable(Activity_Register.this)) {
            final AlertDialog dialog = new SpotsDialog(Activity_Register.this, "Creating new Account...");
            dialog.show();
            _auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        saveData();
                    } else {
                        dialog.dismiss();
                        Util_Func.Alert(Activity_Register.this, "Email Error", "Email already exist, use different!");
                    }

                }
            });

        } else {
            Util_Func.Alert(Activity_Register.this, "No Internet Connection", "Enable Wifi or Mobile Data");
        }
    }


    private void saveData() {
        Toast.makeText(this, "Save Data Successfully", Toast.LENGTH_SHORT).show();

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


    TextWatcher textChangeListener = new TextWatcher() {
        private String current = "";
        private String ddmmyyyy = "DDMMYYYY";
        private Calendar cal = Calendar.getInstance();

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8) {
                    clean = clean + ddmmyyyy.substring(clean.length());
                } else {

                    int day = Integer.parseInt(clean.substring(0, 2));
                    int mon = Integer.parseInt(clean.substring(2, 4));
                    int year = Integer.parseInt(clean.substring(4, 8));

                    mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                    cal.set(Calendar.MONTH, mon - 1);
                    year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                    cal.set(Calendar.YEAR, year);

                    day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                    clean = String.format("%02d%02d%02d", day, mon, year);
                }

                clean = String.format("%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                edit_date.setText(current);
                edit_date.setSelection(sel < current.length() ? sel : current.length());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return true;
    }


}
