package tech.arinzedroid.myjournal;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.arinzedroid.myjournal.models.DiaryModel;

public class EditItemsDialogFragment extends DialogFragment {

    private static final String DIARY_ITEM = "DIARY_ITEM";
    private static final String POSITION = "POSITION";
    private DiaryModel diaryModel;
    private int position;
    @BindView(R.id.details)
    TextView DetailsTv;
    @BindView(R.id.diary_title)
    TextView TitleTv;
    @BindView(R.id.add_btn)
    Button AddBtn;

    private OnButtonClickedInterface onButtonClickedInterface;

    public EditItemsDialogFragment(){

    }

    public static EditItemsDialogFragment newInstance(DiaryModel diaryModel, int position){
        EditItemsDialogFragment fragment = new EditItemsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(DIARY_ITEM, Parcels.wrap(diaryModel));
        args.putInt(POSITION,position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       // Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        if(getArguments() != null){
            this.diaryModel = Parcels.unwrap(getArguments().getParcelable(DIARY_ITEM));
            this.position = getArguments().getInt(POSITION);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = layoutInflater.inflate(R.layout.fragment_edit_items,container,false);
        ButterKnife.bind(this,view);
        if(diaryModel != null){
            TitleTv.setText(diaryModel.getTitle());
            DetailsTv.setText(diaryModel.getDetail());
            AddBtn.setOnClickListener(edit -> editItem(diaryModel,TitleTv.getText().toString(),
                    DetailsTv.getText().toString(),this.position));
        }else {
            AddBtn.setText("Add New Item");
            AddBtn.setOnClickListener(add -> addNewItem(TitleTv.getText().toString(),
                    DetailsTv.getText().toString()));
        }
        return view;

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof OnButtonClickedInterface){
            onButtonClickedInterface = (OnButtonClickedInterface) context;
        } else throw new RuntimeException("Must implement interface in calling object");
    }

    @Override
    public void onDetach(){
        super.onDetach();
        onButtonClickedInterface = null;
    }

    private void addNewItem(String title, String detail){
        if(!title.isEmpty() || !detail.isEmpty()){
            DiaryModel diaryModel = new DiaryModel();
            diaryModel.setTitle(!title.isEmpty()? title:" ");
            diaryModel.setDetail(!detail.isEmpty()? detail:" ");
            onButtonClickedInterface.onAddNewClicked(diaryModel);
            dismiss();
        }else{
            dismiss();
        }
    }
    private void editItem(DiaryModel diaryModel, String title, String detail, int position){
        if(!title.isEmpty() || !detail.isEmpty()){
            diaryModel.setTitle(!title.isEmpty()? title:" ");
            diaryModel.setDetail(!detail.isEmpty()? detail:" ");
            onButtonClickedInterface.onSaveClicked(diaryModel,position);
            dismiss();
        }else {
            onButtonClickedInterface.onSaveClicked(null,position);
            dismiss();
        }
    }

    public interface OnButtonClickedInterface {
        void onSaveClicked(DiaryModel diaryModel,int position);
        void onAddNewClicked(DiaryModel diaryModel);
    }
}
