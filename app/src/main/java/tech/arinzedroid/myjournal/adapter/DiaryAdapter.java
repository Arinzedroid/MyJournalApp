package tech.arinzedroid.myjournal.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import tech.arinzedroid.myjournal.R;
import tech.arinzedroid.myjournal.interfaces.DiaryItemClickedInterface;
import tech.arinzedroid.myjournal.models.DiaryModel;
import tech.arinzedroid.myjournal.viewholder.DiaryViewHolder;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryViewHolder> {

    private DiaryItemClickedInterface diaryItemClickedInterface;
    private ArrayList<DiaryModel> mDiaryModel;
    private Context context;

    public DiaryAdapter(Context context, ArrayList<DiaryModel> diaryModel, DiaryItemClickedInterface diaryItemClickedInterface){
        this.diaryItemClickedInterface = diaryItemClickedInterface;
        this.mDiaryModel = diaryModel;
        this.context = context;
    }

    public DiaryModel deleteItem(int position){
        DiaryModel model = mDiaryModel.get(position);
        if(model != null){
            mDiaryModel.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,mDiaryModel.size());

        }
        return model;
    }

    public void addDiaryContent(DiaryModel diaryModel){
        int index = 0;
        mDiaryModel.add(index,diaryModel);
        Log.e(this.getClass().getSimpleName(),"Item added");
        notifyItemInserted(index);
    }

    public void updateDiaryContent(DiaryModel diaryModel, int position){
        mDiaryModel.set(position,diaryModel);
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_items_layout,parent,false);
        Log.e(this.getClass().getSimpleName(),"OnCreateViewHolderCalled");
        return new DiaryViewHolder(view,diaryItemClickedInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        DiaryModel diaryModel = this.mDiaryModel.get(position);
        holder.TitleTV.setText(diaryModel.getTitle());
        holder.DetailTv.setText(diaryModel.getDetail());
        holder.DateTv.setText(diaryModel.getDate());
    }

    @Override
    public int getItemCount() {
       return mDiaryModel.size();
    }
}
