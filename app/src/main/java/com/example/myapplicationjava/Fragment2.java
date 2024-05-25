package com.example.myapplicationjava;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class Fragment2 extends Fragment {
    private EditText loginEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;
    private Button registerButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2, container, false);
        loginEditText = view.findViewById(R.id.email_field);
        passwordEditText = view.findViewById(R.id.password_field);
        repeatPasswordEditText=view.findViewById(R.id.password_field2);
        registerButton = view.findViewById(R.id.send_button);

        registerButton.setOnClickListener(v -> {
            String login = loginEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String repeatPassword = repeatPasswordEditText.getText().toString();
            ((MainActivity) getActivity()).registerUser(login, password,repeatPassword);
        });
        return view;
    }
}