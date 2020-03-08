package com.example.messaging;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class ProfileFragment extends Fragment {

    private TextView name, email, tag, mobile, about;
    private ImageView profPic;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userReference = db.collection("Users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        name = view.findViewById(R.id.textViewName);
        email = view.findViewById(R.id.textViewEmail);
        tag = view.findViewById(R.id.textViewTag);
        mobile = view.findViewById(R.id.textViewMobile);
        about = view.findViewById(R.id.textViewAbout);
        profPic = view.findViewById(R.id.imgProfilePic);

        userReference.document(firebaseAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Failed to get user info", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onEvent: "+e.getLocalizedMessage());
                    return;
                }

                UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
                name.setText(userProfile.getName());
                email.setText(userProfile.getEmail());
                tag.setText("#".concat(userProfile.getTag()));
                mobile.setText(userProfile.getMobile());
                Glide.with(getActivity()).load(userProfile.getImageURL()).into(profPic);
            }
        });


        return view;
    }
}
