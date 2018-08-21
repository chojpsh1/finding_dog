package com.example.minyoung.finding_dog.Fragment;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minyoung.finding_dog.LoginActivity;
import com.example.minyoung.finding_dog.MainActivity;
import com.example.minyoung.finding_dog.R;
import com.example.minyoung.finding_dog.SingerItem2;
import com.example.minyoung.finding_dog.SingerItemView2;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by minyoung on 2018-06-28.
 */

public class search_fragment extends Fragment {
    Context mContext;
    View view;
    static ListView listView;
    SingerAdapter adapter;
    ArrayList<String> dog_species;
    ArrayList<String> dog_location;
    ArrayList<String> dog_feature;

    static SingerAdapter new_adapter;
    static ArrayList<String> dog_species2;
    static ArrayList<String> dog_location2;
    static ArrayList<String> dog_feature2;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference firebaseDatabaseRef;

    FirebaseStorage firebaseStorage;
    StorageReference firebaseStorageRef;

    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    private static final int MAX_DIMENSION = 1200;
    private static final String CLOUD_VISION_API_KEY = "AIzaSyD_NGKmBcL37cg9ivSMUt0BWN8LiZb-n5A";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final int MAX_LABEL_RESULTS = 5;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 데이터베이스 Instance 생성
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabaseRef = firebaseDatabase.getReference();


        // 스토리지 Instance 생성
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseStorageRef = firebaseStorage.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_search, container, false);
        mContext = container.getContext();
        listView = (ListView) view.findViewById(R.id.listView);

        dog_species = new ArrayList<String>();
        dog_location = new ArrayList<String>();
        dog_feature = new ArrayList<String>();



        Button search = (Button) view.findViewById(R.id.search_btn);
        search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //카메라 버튼 클릭시
                if (PermissionUtils.requestPermission(getActivity(), GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                            GALLERY_IMAGE_REQUEST);


                }
            }
        });

        //losestate가 true인 강아지들의 정보를 리스트로 출력
        adapter = new SingerAdapter();

        DatabaseReference mConditionRef=firebaseDatabaseRef.child("User");
        mConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                while(child.hasNext()) {
                    DataSnapshot temp=child.next();
                    String key=temp.child("LoseState").getValue().toString();

                    if(key.equals("True")){
                        dog_species.add(temp.child("species").getValue().toString());
                        dog_location.add(temp.child("location").getValue().toString());
                        dog_feature.add(temp.child("feature").getValue().toString());
                        temp.child("uid").getValue().toString();
                    }
                }
                /*Hard Coding*/
                for(int i = 0; i < dog_species.size(); i++) {
                    adapter.addItem(new SingerItem2(dog_species.get(i), dog_location.get(i), dog_feature.get(i), i + 2131230871));
                }
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

        return view;
    }

    //결과 처리
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if(requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null){
            uploadImage(data.getData());
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri),
                                MAX_DIMENSION);

                callCloudVision(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(mContext, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(mContext, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = mContext.getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(mContext.getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("LABEL_DETECTION");
                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }
    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<MainActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(MainActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(final String result) {
            MainActivity activity = mActivityWeakReference.get();
            dog_species2=new ArrayList<String>();
            dog_location2=new ArrayList<String>();
            dog_feature2=new ArrayList<String>();
            new_adapter = new SingerAdapter();

            if (activity != null && !activity.isFinishing()) {
                TextView imageDetail = activity.findViewById(R.id.species);

                //종에 따른 강아지 리스트 다시 출력
                DatabaseReference database2 = FirebaseDatabase.getInstance().getReference();
                DatabaseReference mConditionRef2=database2.child("User");

                mConditionRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                        while(child.hasNext()) {
                            DataSnapshot temp=child.next();
                            String key=temp.child("species").getValue().toString();
                            String state=temp.child("LoseState").getValue().toString();
                            boolean got = key.contains(result);
                            if(got&&state.equals("True")){
                                dog_species2.add(temp.child("species").getValue().toString());
                                dog_location2.add(temp.child("location").getValue().toString());
                                dog_feature2.add(temp.child("feature").getValue().toString());
                            }

                        }
                        /*Hard Coding*/
                        int temp = 2131230873;
                        for(int i = 0; i < dog_species2.size(); i++) {
//                            Log.e("images",String.valueOf(R.drawable.dog11));
//                            Log.e("images",String.valueOf(R.drawable.dog12));
//                            Log.e("images",String.valueOf(R.drawable.dog13));
//                            Log.e("images",String.valueOf(R.drawable.dog14));
//                            Log.e("images",String.valueOf(R.drawable.dog15));
//                            Log.e("images",String.valueOf(R.drawable.dog16));
                            /*Hard Coding*/
                            new_adapter.addItem(new SingerItem2(dog_species2.get(i), dog_location2.get(i), dog_feature2.get(i), temp));
                            /*Hard Coding*/
                            temp += 2;
                        }
                        listView.setAdapter(new_adapter);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });
                imageDetail.setText(result);
                //*********리스트 선택 했을 때 채팅 연결하기*********//
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        SingerItem2 item = (SingerItem2) new_adapter.getItem(position);

                        Toast.makeText(getApplicationContext(),"선택 : "+item.getName(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
        TextView textSpecies = getActivity().findViewById(R.id.species);
        textSpecies.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask((MainActivity) getActivity(), prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }
    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("");

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                switch(label.getDescription()) {
                    case "dog like mammal":
                    case "dog":
                    case "dog breed":
                    case "nose":
                    case "puppy":
                    case "dog breed group":
                    case "snout":
                    case "close up":
                        break;
                    default:
                        message.append(String.format(Locale.US, "%s", label.getDescription()));
//                        message.append(" / ");
                }
            }
        } else {
            message.append("nothing");
        }

        return message.toString();
    }

    static class SingerAdapter extends BaseAdapter {
        ArrayList<SingerItem2> items = new ArrayList<SingerItem2>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(SingerItem2 item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            SingerItemView2 view = new SingerItemView2(getApplicationContext());

            SingerItem2 item = items.get(position);
            view.setName(item.getName());
            view.setLocation(item.getLocation());
            view.setFeature(item.getFeature());
            view.setImage(item.getResId());

            return view;
        }
    }

}