package com.example.messaging;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.messaging.Adapters.UserAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserProfile> users;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference reference = db.collection("Users");
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        users = new ArrayList<>();

        readUsers();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void readUsers() {



//        reference.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        users.clear();
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                            UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
//
//                            assert firebaseUser != null;
//                            if (!userProfile.getUid().equals(firebaseUser.getUid())) {
//                                users.add(userProfile);
//                            }
//                        }
//
//                        userAdapter = new UserAdapter(getContext(), users, false);
//                        recyclerView.setAdapter(userAdapter);
//                        userAdapter.notifyDataSetChanged();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getContext(),"Could not display Users", Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, "onFailure: "+e.getLocalizedMessage());
//                    }
//                });

        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(),"Could not display Users", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: "+e.getLocalizedMessage());
                    return;
                }
                users.clear();
                assert queryDocumentSnapshots != null;
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);

                    assert firebaseUser != null;
                    if (!userProfile.getUid().equals(firebaseUser.getUid())) {
                        users.add(userProfile);
                    }
                }

                userAdapter = new UserAdapter(getContext(), users, false);
                recyclerView.setAdapter(userAdapter);
                userAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuItem searchItem = menu.findItem(R.id.search_btn);
        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setQueryHint("Search by name...");
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchChats(newText);
                    return false;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_btn:
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    private void searchChats(final String newText) {
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(getContext(),"Could not display Users", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onFailure: "+e.getLocalizedMessage());
                            return;
                        }
                        users.clear();
                        assert queryDocumentSnapshots != null;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);

                            assert firebaseUser != null;
                            if (!userProfile.getUid().equals(firebaseUser.getUid())) {
                                if (userProfile.getName().toLowerCase().contains(newText.toLowerCase()))
                                users.add(userProfile);
                            }
                        }

                        userAdapter = new UserAdapter(getContext(), users, false);
                        recyclerView.setAdapter(userAdapter);
                        userAdapter.notifyDataSetChanged();
                    }
                });
    }
}
