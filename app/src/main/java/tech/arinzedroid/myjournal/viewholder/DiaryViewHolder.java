package tech.arinzedroid.myjournal.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import tech.arinzedroid.myjournal.R;
import tech.arinzedroid.myjournal.interfaces.DiaryItemClickedInterface;

public class DiaryViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

    @BindView(R.id.item_title)
    public TextView TitleTV;
    @BindView(R.id.item_details)
    public TextView DetailTv;
    @BindView(R.id.delete)
    ImageButton DeleteBtn;
    @BindView(R.id.edit)
    ImageButton EditBtn;
    @BindView(R.id.item_date)
    public TextView DateTv;
    @BindView(R.id.diary_items_layout)
    View ItemsLayout;

    private DiaryItemClickedInterface diaryItemClickedInterface;

    public DiaryViewHolder(View itemView, DiaryItemClickedInterface diaryItemClickedInterface) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        ItemsLayout.setOnClickListener(this);
        DeleteBtn.setOnClickListener(this);
        EditBtn.setOnClickListener(this);
        this.diaryItemClickedInterface = diaryItemClickedInterface;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.diary_items_layout :{
                diaryItemClickedInterface.Item(this.getLayoutPosition());
                break;
            }
            case R.id.delete:{
                diaryItemClickedInterface.onDelete(this.getLayoutPosition());
                break;
            }
            case R.id.edit:{
                diaryItemClickedInterface.onEdit(this.getLayoutPosition());
                break;
            }
            default: break;
        }
    }
}
