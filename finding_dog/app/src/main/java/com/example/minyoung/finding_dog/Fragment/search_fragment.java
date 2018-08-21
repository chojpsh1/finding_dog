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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
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
import java.io.File;
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
    Uri filePath;
    ImageView imageViewUpload;
    View view;
    EditText editText;
    static ListView listView;
    SingerAdapter adapter;
    ArrayList<String> dog_species;
    ArrayList<String> dog_location;
    ArrayList<String> dog_feature;

    static SingerAdapter new_adapter;
    static ArrayList<String> dog_species2;
    static ArrayList<String> dog_location2;
    static ArrayList<String> dog_feature2;
    static ArrayList<Uri> image_uri;
    static ArrayList<String> image_user;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference firebaseDatabaseRef;

    static FirebaseStorage firebaseStorage;
    StorageReference firebaseStorageRef;

    private FirebaseAuth mAuth;
    private static FirebaseUser user;

    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    private static final int MAX_DIMENSION = 1200;
    private static final String CLOUD_VISION_API_KEY = "";
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

        // 로그인 계정정보
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_search, container, false);
        mContext = container.getContext();
        listView = (ListView) view.findViewById(R.id.listView);

        dog_species = new ArrayList<String>();
        dog_location = new ArrayList<String>();
        dog_feature = new ArrayList<String>();
        image_uri = new ArrayList<Uri>();
        image_user = new ArrayList<String>();


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
                        image_user.add(temp.child("uid").getValue().toString());
                    }

                }
                for(int i = 0; i < dog_species.size(); i++) {

                    StorageReference storageRef = firebaseStorage.getReferenceFromUrl("gs://chatting-ed067.appspot.com").child("UID").child(image_user.get(i).toString());
                    File localFile = null;
                    try {
                        localFile = File.createTempFile(image_user.get(i).toString(), "jpg");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    storageRef.getFile(localFile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    // Successfully downloaded data to local file
                                    // ...
                                    long a = taskSnapshot.getBytesTransferred();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle failed download
                            // ...
                        }
                    });
                    adapter.addItem(new SingerItem2(dog_species.get(i), dog_location.get(i), dog_feature.get(i), R.drawable.img1));
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
                        for(int i = 0; i < dog_species2.size(); i++) {
                            StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://chatting-ed067.appspot.com");
                            //다운로드할 파일을 가르키는 참조 만들기
                            StorageReference pathReference = storageReference.child("UID").child(user.getEmail() + ".JPEG");

                            //Url을 다운받기
                            final int finalI = i;
                            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(getApplicationContext(), "주인 찾기 완료 : "+ uri, Toast.LENGTH_SHORT).show();
                                    Log.e("search_fragment", "주인 찾기 완료");
                                    new_adapter.addItem(new SingerItem2(dog_species2.get(finalI), dog_location2.get(finalI), dog_feature2.get(finalI), R.drawable.profile));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("search_fragment", "주인 찾기 실패");
                                    Toast.makeText(getApplicationContext(), "주인 찾기 실패", Toast.LENGTH_SHORT).show();
                                }
                            });
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

    //upload the file
    private void uploadFile() {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //Unique한 파일명을 만들자.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            String filename = formatter.format(now) + ".jpg";
            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = firebaseStorage.getReferenceFromUrl("gs://chatting-ed067.appspot.com").child("UID").child(filename);

            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                            Toast.makeText(mContext, "업로드 완료!", Toast.LENGTH_LONG).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(mContext, "업로드 실패!", Toast.LENGTH_LONG).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                            double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(mContext, "선택된 사진 파일이 없습니다.", Toast.LENGTH_LONG).show();
        }
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
                        message.append("\n");
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
            final Context context = viewGroup.getContext();

            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.singer_item, viewGroup, false);

            SingerItem2 item = items.get(position);

            TextView textView1 = convertView.findViewById(R.id.dog_species);
            textView1.setText(item.getName());
            TextView textView2 = convertView.findViewById(R.id.dog_location);
            textView2.setText(item.getLocation());
            TextView textView3 = convertView.findViewById(R.id.dog_feature);
            textView3.setText(item.getFeature());
            ImageView imageView = convertView.findViewById(R.id.imageView);
            imageView.setImageURI(image_uri.get(position));

            return convertView;
        }
    }

}