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

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class ChatsFragment extends Fragment {

    private RecyclerView chatsView;

    private UserAdapter userAdapter;
    private List<UserProfile> users;
    private List<String> usersList;

    FirebaseUser firebaseUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference chatsReference = db.collection("Messages");
    CollectionReference usersReference = db.collection("Users");
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        chatsView = view.findViewById(R.id.chats_view);
        chatsView.setHasFixedSize(true);
        chatsView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();

//        chatsReference.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        usersList.clear();
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                            Chat chat = documentSnapshot.toObject(Chat.class);
//
//                            if (chat.getSender().equals(firebaseUser.getUid())) {
//                                usersList.add(chat.getReceiver());
//                            }
//                            if (chat.getReceiver().equals(firebaseUser.getUid())) {
//                                usersList.add(chat.getSender());
//                            }
//                        }
//                        Log.d(TAG, "onSuccess: "+usersList);
//                        readChats();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getContext(), "Error retrieving chat users", Toast.LENGTH_SHORT).show();
//                    }
//                });

        chatsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "onEvent: " + e.getLocalizedMessage());
                    return;
                }

                usersList.clear();
                assert queryDocumentSnapshots != null;
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Chat chat = documentSnapshot.toObject(Chat.class);

                    if (chat.getSender().equals(firebaseUser.getUid())) {
                        usersList.add(chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(firebaseUser.getUid())) {
                        usersList.add(chat.getSender());
                    }
                }
                Log.d(TAG, "onSuccess: " + usersList);
                readChats();
            }

        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

    private void searchChats(final String newText) {
        usersReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Error retrieving user chats", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onEvent: "+e.getLocalizedMessage());
                }

                users.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);

                    // display a user from chats
                    for (String id : usersList) {
                        if (userProfile.getUid().equals(id)) {
                            if (users.size() != 0) {
                                for (UserProfile userProfile1 : users) {
                                    if (!userProfile.getUid().equals(userProfile1.getUid())) {
                                        if (userProfile.getName().toLowerCase().contains(newText.toLowerCase()))
                                        users.add(userProfile);
                                    }
                                }
                            } else {
                                if (userProfile.getName().toLowerCase().contains(newText.toLowerCase()))
                                users.add(userProfile);
                            }
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(), users, true);
                chatsView.setAdapter(userAdapter);
            }
        });
        chatsView.setAdapter(userAdapter);
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

    private void readChats() {
        users = new ArrayList<>();

//        usersReference.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        users.clear();
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                            UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
//
//                            // display a user from chats
//                            for (String id : usersList) {
//                                if (userProfile.getUid().equals(id)) {
//                                    if (users.size() != 0) {
//                                        for (UserProfile userProfile1 : users) {
//                                            if (!userProfile.getUid().equals(userProfile1.getUid())) {
//                                                users.add(userProfile);
//                                            }
//                                        }
//                                    } else {
//                                        users.add(userProfile);
//                                    }
//                                }
//                            }
//                        }
//
//                        userAdapter = new UserAdapter(getContext(), users, true);
//                        chatsView.setAdapter(userAdapter);
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getContext(), "Error retrieving user chats", Toast.LENGTH_SHORT).show();
//                    }
//                });

        usersReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Error retrieving user chats", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onEvent: "+e.getLocalizedMessage());
                }

                users.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);

                    // display a user from chats
                    for (String id : usersList) {
                        if (userProfile.getUid().equals(id)) {
                            if (users.size() != 0) {
                                for (UserProfile userProfile1 : users) {
                                    if (!userProfile.getUid().equals(userProfile1.getUid())) {
                                        users.add(userProfile);
                                    }
                                }
                            } else {
                                users.add(userProfile);
                            }
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(), users, true);
                chatsView.setAdapter(userAdapter);
            }
        });
        chatsView.setAdapter(userAdapter);
    }

}
