package com.example.uploadimage;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.*;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Upload_image_Fragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button uploadBtn;
    private ImageView imageView;
    private ProgressBar progressBar;

    private DatabaseReference root;
    private StorageReference reference;
    private Uri imageUri;

    public Upload_image_Fragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_image, container, false);

        // Initialize Firebase references
        root = FirebaseDatabase.getInstance().getReference("Image");
        reference = FirebaseStorage.getInstance().getReference();

        // Initialize UI elements
        uploadBtn = view.findViewById(R.id.UploadImage);
        imageView = view.findViewById(R.id.imageView);
        progressBar = view.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        // Set up button listeners
        uploadBtn.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadToFirebase(imageUri);
            } else {
                Toast.makeText(getActivity(), "Please Select Image", Toast.LENGTH_SHORT).show();
            }
        });

        imageView.setOnClickListener(v -> openImageChooser());

        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void uploadToFirebase(Uri uri) {
        if (uri != null) {
            // Create a unique file name
            StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));

            fileRef.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                            .addOnSuccessListener(downloadUri -> {
                                Model model = new Model(downloadUri.toString());
                                String modelId = root.push().getKey();
                                root.child(modelId).setValue(model);
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getActivity(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                imageView.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);
                            })
                    )
                    .addOnProgressListener(snapshot -> progressBar.setVisibility(View.VISIBLE))
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getActivity(), "Uploading Failed !!", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}
