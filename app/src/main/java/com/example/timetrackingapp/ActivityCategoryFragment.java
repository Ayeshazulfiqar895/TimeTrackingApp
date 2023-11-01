package com.example.timetrackingapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityCategoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private List<ListItem> itemList;

    // Firebase Firestore references
    private FirebaseFirestore firestore;
    private CollectionReference itemsCollection;
    private FirebaseAuth auth; // Firebase Authentication
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        itemList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        adapter = new CustomAdapter(itemList, new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showUpdateItemDialog(position);
            }

            @Override
            public void onDeleteClick(int position) {
                // Remove the item from the list and Firestore
                String documentId = itemList.get(position).getDocumentId();
                itemList.remove(position);
                adapter.notifyItemRemoved(position);
                deleteItemFromFirestore(documentId);
            }
        });

        if (currentUser != null) {
            // User is logged in
            firestore = FirebaseFirestore.getInstance();
            String userId = currentUser.getUid(); // Get the user's UID
            itemsCollection = firestore.collection("users").document(userId).collection("activities");

            // Find the Button with ID "add_button"
            Button addButton = view.findViewById(R.id.add_button);

            // Set an OnClickListener for the "Add Item" Button
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddItemDialog();
                }
            });

            recyclerView = view.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

            // Retrieve items from Firestore
            retrieveItemsFromFirestore();
        } else {
            // User is not logged in, handle as needed
        }

        return view;
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add New Category");

        final EditText input = new EditText(getActivity());
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newItemText = input.getText().toString();
                if (!newItemText.isEmpty()) {
                    addItemToFirestore(newItemText);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showUpdateItemDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update Category Name");

        final EditText input = new EditText(getActivity());
        input.setText(itemList.get(position).getText());
        builder.setView(input);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedItemText = input.getText().toString();
                if (!updatedItemText.isEmpty()) {
                    updateItemInFirestore(itemList.get(position).getDocumentId(), updatedItemText);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addItemToFirestore(String text) {
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("Category name", text);

        itemsCollection.add(itemData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Item added to Firestore, retrieve it to get the document ID
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String documentId = documentReference.getId();
                                    ListItem newItem = new ListItem(R.drawable.activities, text, documentId);
                                    itemList.add(newItem);
                                    adapter.notifyItemInserted(itemList.size() - 1);
                                } else {
                                    // Handle the case where the document doesn't exist
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                    }
                });
    }

    private void retrieveItemsFromFirestore() {
        itemsCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String text = document.getString("Category name");
                            String documentId = document.getId();
                            ListItem item = new ListItem(R.drawable.activities, text, documentId);
                            itemList.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                    }
                });
    }


    private void updateItemInFirestore(String documentId, String updatedText) {
        DocumentReference itemRef = itemsCollection.document(documentId);
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("Category name", updatedText);

        itemRef.update(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Item updated in Firestore, update the local itemList
                        for (int i = 0; i < itemList.size(); i++) {
                            if (itemList.get(i).getDocumentId().equals(documentId)) {
                                // Update the item in the local list
                                itemList.get(i).setText(updatedText);
                                adapter.notifyItemChanged(i); // Notify the adapter about the change
                                break;
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                    }
                });
    }


    private void deleteItemFromFirestore(String documentId) {
        itemsCollection.document(documentId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Item deleted from Firestore
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                    }
                });
    }
}