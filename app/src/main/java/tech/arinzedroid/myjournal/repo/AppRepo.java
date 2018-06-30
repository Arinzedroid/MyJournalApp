package tech.arinzedroid.myjournal.repo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import tech.arinzedroid.myjournal.models.DiaryModel;

public class AppRepo {

    private static final String DATE = "DATE";
    private static final String TITLE = "TITLE";
    private static final String DETAIL = "DETAIL";
    private static final String ID = "ID";

    public AppRepo(){

    }

    public LiveData<ArrayList<DiaryModel>> getDiaryFromFirebase(@NonNull GoogleSignInAccount account,
                                                                FirebaseFirestore firebaseFirestore){
        final MutableLiveData<ArrayList<DiaryModel>> arrayListMutableLiveData = new MutableLiveData<>();
        ArrayList<DiaryModel> diaryModelArrayList = new ArrayList<>();
        firebaseFirestore.collection(Objects.requireNonNull(account.getEmail()))
                .orderBy(DATE, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //check is getting diary item was successful
                        if (task.isSuccessful()) {
                            //loop through task results
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //deserialize firestore document into a DiaryModel data class
                                diaryModelArrayList.add(document.toObject(DiaryModel.class));
                                Log.e(this.getClass().getSimpleName(), document.getId() + " => " + document.getData());
                            }
                            //start a background thread that assigns diary items arraylist
                            // to your mutablelivedata object
                            arrayListMutableLiveData.postValue(diaryModelArrayList);

                        } else {
                            Log.e(this.getClass().getSimpleName(), "Error getting documents: ", task.getException());
                        }
                    }
                });

        return arrayListMutableLiveData;

    }

    public void addItemToFirebase(@NonNull DiaryModel diaryModel, @NonNull GoogleSignInAccount account,
                                  @NonNull FirebaseFirestore firebaseFirestore){
        //create a new item with id
        Map<String,Object> item = new HashMap<>();
        item.put(ID,diaryModel.getId());
        item.put(DATE,diaryModel.getDate());
        item.put(TITLE,diaryModel.getTitle());
        item.put(DETAIL,diaryModel.getDetail());
        String id = diaryModel.getId();

        firebaseFirestore.collection(Objects.requireNonNull(account.getEmail())).document(id)
                .set(item)
                .addOnSuccessListener(aVoid -> Log.e(this.getClass().getSimpleName(),
                        "Item added to db "))
                .addOnFailureListener(e -> Log.e(this.getClass().getSimpleName(),
                        "Error occurred ",e));
    }

    public void updateItemInFirebase(@NonNull DiaryModel diaryModel,@NonNull GoogleSignInAccount account,
                                     @NonNull FirebaseFirestore firebaseFirestore){
        //create a new item with id
        Map<String,Object> item = new HashMap<>();
        item.put(TITLE,diaryModel.getTitle());
        item.put(DETAIL,diaryModel.getDetail());
        String id = diaryModel.getId();

        //get a reference to the doc you want to update
        DocumentReference Doc = firebaseFirestore.collection(Objects.requireNonNull(account.getEmail())).document(id);

        Doc.update(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(this.getClass().getSimpleName(),
                                "FireStore Db updated successfully with id " + Doc.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(this.getClass().getSimpleName(),"Error occurred ", e);
                    }
                });
    }

    public void deleteItemInFirebase(@NonNull DiaryModel diaryModel, @NonNull GoogleSignInAccount account,
                                     @NonNull FirebaseFirestore firebaseFirestore){
        //delete item from the firestore db
        //check if signedIn email is not null else use id of diaryItem
        firebaseFirestore.collection(Objects.requireNonNull(account.getEmail()))
                .document(diaryModel.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(this.getClass().getSimpleName(),"FireStore Db updated successfully with id ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(this.getClass().getSimpleName(),"Error occurred ", e);
                    }
                });


    }
}
