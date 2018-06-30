package tech.arinzedroid.myjournal.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import tech.arinzedroid.myjournal.models.DiaryModel;
import tech.arinzedroid.myjournal.repo.AppRepo;

public class AppViewModel extends ViewModel {
    private AppRepo appRepo;
    private LiveData<ArrayList<DiaryModel>> liveDataArrayList;
    private MutableLiveData<ArrayList<DiaryModel>> mutableLiveDataArrayList = new MutableLiveData<>();


    public AppViewModel(){
       appRepo = new AppRepo();
    }

    private void addData(DiaryModel diaryModel){
        if(liveDataArrayList != null){
            ArrayList<DiaryModel> diaryModelArrayList = liveDataArrayList.getValue();
            Objects.requireNonNull(diaryModelArrayList).add(diaryModel);
            mutableLiveDataArrayList.setValue(diaryModelArrayList);
        }
    }

    private void deleteData(DiaryModel diaryModel){
        if(liveDataArrayList != null){
            ArrayList<DiaryModel> diaryModelArrayList = liveDataArrayList.getValue();
            Objects.requireNonNull(diaryModelArrayList).remove(diaryModel);
            mutableLiveDataArrayList.setValue(diaryModelArrayList);
        }
    }

    private void updateData(DiaryModel diaryModel){
        if(liveDataArrayList != null){
            ArrayList<DiaryModel> diaryModelArrayList = liveDataArrayList.getValue();
            for (int i = 0; i < Objects.requireNonNull(diaryModelArrayList).size(); i++){
                if(diaryModelArrayList.get(i).getId().equals(diaryModel.getId())){
                    diaryModelArrayList.set(i,diaryModel);
                }
            }
            mutableLiveDataArrayList.setValue(diaryModelArrayList);
        }
    }

    public LiveData<ArrayList<DiaryModel>> getDiaryItemsFromFirebase
            (@NonNull GoogleSignInAccount account, @NonNull FirebaseFirestore firebaseFirestore){
        if(liveDataArrayList == null){
            liveDataArrayList = appRepo.getDiaryFromFirebase(account,firebaseFirestore);
            mutableLiveDataArrayList.setValue(liveDataArrayList.getValue());
        }

        return liveDataArrayList;
    }

    public void addItemInFirebase(@NonNull DiaryModel diaryModel, @NonNull GoogleSignInAccount account,
                                  @NonNull FirebaseFirestore firebaseFirestore){
        appRepo.addItemToFirebase(diaryModel,account,firebaseFirestore);
        //addData(diaryModel);
    }

    public void deleteItemInFirebase(@NonNull DiaryModel diaryModel, @NonNull GoogleSignInAccount account,
                                     @NonNull FirebaseFirestore firebaseFirestore){
        appRepo.deleteItemInFirebase(diaryModel,account,firebaseFirestore);
        //deleteData(diaryModel);
    }

    public void updateItemInFirebase(@NonNull DiaryModel diaryModel, @NonNull GoogleSignInAccount account,
                                     @NonNull FirebaseFirestore firebaseFirestore){
        appRepo.updateItemInFirebase(diaryModel,account,firebaseFirestore);
        //updateData(diaryModel);
    }
}
