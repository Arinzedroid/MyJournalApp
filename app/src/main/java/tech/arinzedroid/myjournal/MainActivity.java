package tech.arinzedroid.myjournal;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.arinzedroid.myjournal.adapter.DiaryAdapter;
import tech.arinzedroid.myjournal.interfaces.DiaryItemClickedInterface;
import tech.arinzedroid.myjournal.models.DiaryModel;
import tech.arinzedroid.myjournal.viewmodel.AppViewModel;

public class MainActivity extends AppCompatActivity implements DiaryItemClickedInterface,
        EditItemsDialogFragment.OnButtonClickedInterface{

    @BindView(R.id.recycler_view)
    RecyclerView DiaryRV;
    @BindView(R.id.empty_text)
    TextView DefaultTv;
    @BindView(R.id.display_name)
    TextView DisplayName;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    DiaryAdapter adapter;
    ArrayList<DiaryModel> diaryModels = new ArrayList<>();

    //declare firebase firestore instance
    FirebaseFirestore firestoreDb;

    //declare google signIn account
    GoogleSignInAccount account;

    private AppViewModel appViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //allow firbase logging for debug purposes
        FirebaseFirestore.setLoggingEnabled(true);

        //initialize firestore db
        firestoreDb = FirebaseFirestore.getInstance();

        //init viewmodel
        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        //initialize google signin account
        account = GoogleSignIn.getLastSignedInAccount(this);

        //display name of current user
        DisplayName.setText(getDisplayName());

        //show progress bar while loading from server
        progressBar.setVisibility(View.VISIBLE);

        appViewModel.getDiaryItemsFromFirebase(account,firestoreDb).observe(this, diaryArraylist -> {
           if(diaryArraylist != null) {
               progressBar.setVisibility(View.GONE);
               diaryModels = diaryArraylist;
               Log.e(this.getClass().getSimpleName()," diaryModels " + diaryModels.size() +
                       " diaryArraylist" + diaryArraylist.size());
               adapter = new DiaryAdapter(MainActivity.this, diaryArraylist,
                       MainActivity.this);
               DiaryRV.setAdapter(adapter);
               if (adapter.getItemCount() == 0)
                   DefaultTv.setVisibility(View.VISIBLE);
               else
                   DefaultTv.setVisibility(View.GONE);
           }
        });

        floatingActionButton.setOnClickListener(view -> AddNewItemDialog());
    }

    private String getDisplayName(){
        if(account != null)
            return account.getDisplayName();
        else return " ";
    }

    private void AddNewItemDialog(){
        EditItemsDialogFragment dialogFragment = new EditItemsDialogFragment();
        dialogFragment.show(getSupportFragmentManager(),"EditItemsFragment");
    }

    private void modifyItemDialog(DiaryModel diaryModel, int position){
        EditItemsDialogFragment editItemsDialogFragment = EditItemsDialogFragment.newInstance(diaryModel,position);
        editItemsDialogFragment.show(getSupportFragmentManager(),"EditItemsFragment");
    }

    private void deleteItem(int position){
        DiaryModel model = adapter.deleteItem(position);
        Log.e(this.getClass().getSimpleName(),"delete diaryModels " + diaryModels.size());

        //check if item exists to delete
        if(model != null)
            appViewModel.deleteItemInFirebase(model,account,firestoreDb);
        if(adapter.getItemCount() == 0)
            DefaultTv.setVisibility(View.VISIBLE);
        else
            DefaultTv.setVisibility(View.GONE);
    }

    private void updateItem(DiaryModel diaryModel, int position){
        adapter.updateDiaryContent(diaryModel,position);
        appViewModel.updateItemInFirebase(diaryModel,account,firestoreDb);
    }

    private void AddNewItem(DiaryModel diaryModel){
        //add data to firebase db
        appViewModel.addItemInFirebase(diaryModel,account,firestoreDb);

        //update the contents of the adapter
        adapter.addDiaryContent(diaryModel);
        Log.e(this.getClass().getSimpleName(),"add diaryModels " + diaryModels.size());
        if(DefaultTv.getVisibility() == View.VISIBLE)
            DefaultTv.setVisibility(View.GONE);

    }

    private void signOutAndRevokeAccess(@NonNull GoogleSignInClient googleSignInClient){

        googleSignInClient.signOut().addOnCompleteListener(task -> revokeAccess(googleSignInClient));
    }

    private void signOutOnly(@NonNull GoogleSignInClient googleSignInClient){
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            startActivity(new Intent(MainActivity.this,LogInActivity.class));
            finish();
        });
    }

    private void showDialog(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,gso);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to clear google account data associated with this app. " +
                "This includes display name and email address. 'No' if you will install the app again")
                .setTitle("Sign Out")
                .setPositiveButton("Yes, clear data", (dialogInterface, i) ->
                        signOutAndRevokeAccess(googleSignInClient))
                .setNegativeButton("No, sign out only", (dialogInterface, i) ->
                        signOutOnly(googleSignInClient))
                .setNeutralButton("Cancel", (dialogInterface, i) -> {
                });
        builder.create().show();
    }

    private void revokeAccess(GoogleSignInClient googleSignInClient){
        googleSignInClient.revokeAccess().addOnCompleteListener(task ->{
                    Toast.makeText(getApplicationContext(),"Data cleared successfully",
                            Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this,LogInActivity.class));
            finish();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            showDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void Item(int position) {
        Log.e(this.getClass().getSimpleName(),"item diaryModels " + diaryModels.size());
        Intent intent = new Intent(this,ViewItemActivity.class);
        intent.putExtra("data", Parcels.wrap(diaryModels.get(position)));
        startActivity(intent);
    }

    @Override
    public void onDelete(int position) {
        deleteItem(position);
    }

    @Override
    public void onEdit(int position) {
        Log.e(this.getClass().getSimpleName()," edit diaryModels " + diaryModels.size());
        modifyItemDialog(diaryModels.get(position),position );
    }

    @Override
    public void onSaveClicked(DiaryModel diaryModel, int position) {
        if(diaryModel == null){
            deleteItem(position);
        }else updateItem(diaryModel,position);
    }

    @Override
    public void onAddNewClicked(DiaryModel diaryModel) {
        AddNewItem(diaryModel);
    }
}
