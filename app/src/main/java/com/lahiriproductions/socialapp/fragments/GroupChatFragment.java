package com.lahiriproductions.socialapp.fragments;

import static com.lahiriproductions.socialapp.utils.Controller.sendNotification;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.adapter.GroupChatAdapter;
import com.lahiriproductions.socialapp.models.GroupChat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = GroupChatFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context mContext;

    private EditText etGroupChat;
    private RecyclerView rvGroupChat;
    private ImageButton ibGroupChatSend;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private GroupChatAdapter groupChatAdapter;
    private List<GroupChat> groupChatList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    public GroupChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupChatFragment newInstance(String param1, String param2) {
        GroupChatFragment fragment = new GroupChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();

        etGroupChat = view.findViewById(R.id.etGroupChat);
        rvGroupChat = view.findViewById(R.id.rvGroupChat);
        ibGroupChatSend = view.findViewById(R.id.ibGroupChatSend);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl(getString(R.string.storage_reference_url));

        groupChatAdapter = new GroupChatAdapter(mContext, groupChatList);
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setStackFromEnd(true);
        rvGroupChat.setLayoutManager(linearLayoutManager);
        rvGroupChat.setAdapter(groupChatAdapter);

        mDatabase.child("group_chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    GroupChat groupChat = snapshot.getValue(GroupChat.class);
                    groupChatList.add(groupChat);
                    groupChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ibGroupChatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group_message = etGroupChat.getText().toString();

                if (group_message.isEmpty()) {
                    Toast.makeText(getActivity(), "Cannot send empty message", Toast.LENGTH_SHORT).show();
                } else {
                    String group_chat_id = mDatabase.child("group_chat").push().getKey();
                    HashMap<String, Object> mDataMap = new HashMap<>();
                    mDataMap.put("user_id", currentUser.getUid());
                    mDataMap.put("group_chat_id", group_chat_id);
                    mDataMap.put("group_message", group_message);
                    mDataMap.put("timestamp", System.currentTimeMillis());
                    mDatabase.child("group_chat").child(group_chat_id).setValue(mDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendNotification(mDatabase, currentUser.getUid(), group_message);
                                etGroupChat.setText("");
                            }
                        }
                    });
                }
            }
        });

    }


}