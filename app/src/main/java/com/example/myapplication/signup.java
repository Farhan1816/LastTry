package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class signup extends Fragment implements View.OnClickListener{

    FirebaseAuth auth;
    FirebaseFirestore db;
    public signup() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_signup, container, false);
        TextView signup = view.findViewById(R.id.LoginButtonSignUp);
        signup.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.LoginButtonSignUp) {

            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            login lll = new login(); // Instantiate the fragment
            fragmentTransaction.replace(R.id.fragmentContainer, lll);
            fragmentTransaction.commit();
        }
        else if(v.getId()==R.id.SignUp)
        {
            Log.d("debug", "hello");
            EditText first = v.findViewById(R.id.FirstName);
            String firstName = first.toString();
            String lastName = v.findViewById(R.id.LastName).toString();
            String username = v.findViewById(R.id.Username).toString();
            String password = v.findViewById(R.id.Password).toString();
            String confirm = v.findViewById(R.id.ConfirmPassword).toString();
            if(firstName.length()==0 || lastName.length()==0 || username.length()==0 || password.length()==0 || confirm.length()==0)
            {
                Toast.makeText(getContext(), "Please Fill Up All The Options", Toast.LENGTH_SHORT).show();
            }
            else if(password!=confirm)
            {
                Toast.makeText(getContext(), "Passwords Do Not Match!", Toast.LENGTH_SHORT).show();
            }
            else{
                auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String userId = task.getResult().getUser().getUid();
                            saveSignUpInfo(userId, firstName, lastName, username, password);
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            login lll = new login(); // Instantiate the fragment
                            fragmentTransaction.replace(R.id.fragmentContainer, lll);
                            fragmentTransaction.commit();
                        }
                        else {
                            Toast.makeText(getContext(), "Error! Try Again!", Toast.LENGTH_SHORT).show();
                            // An error occurred while creating the user;
                        }
                    }
                });
            }
        }

    }

    private void saveSignUpInfo(String userId, String first, String last, String user, String pass) {
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // Initialize Firestore

        // Reference to the "Users" collection
        CollectionReference usersRef = db.collection("Users");

        // Document reference for the user document
        DocumentReference userRef = usersRef.document(userId);

        // Create a Map to represent the user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("FirstName", first);
        userData.put("LastName", last);
        userData.put("Username", user);
        userData.put("Password", pass);

        // Set the user data in the Firestore document
        userRef.set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Account Created!", Toast.LENGTH_SHORT).show();

                        // Document has been successfully written to Firestore
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error! Could Not Save Information!", Toast.LENGTH_SHORT).show();
                        // Handle the error if the document write fails
                    }
                });
    }
}