package tech.arinzedroid.myjournal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.arinzedroid.myjournal.models.DiaryModel;

public class ViewItemActivity extends AppCompatActivity {

    @BindView(R.id.title_text)
    TextView TitleText;
    @BindView(R.id.date_text)
    TextView DateText;
    @BindView(R.id.detail_text)
    TextView DetailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        ButterKnife.bind(this);
        if(getIntent() != null){
            DiaryModel diaryModel = Parcels.unwrap(getIntent().getParcelableExtra("data"));
            TitleText.setText(diaryModel.getTitle());
            DetailText.setText(diaryModel.getDetail());
            DateText.setText(diaryModel.getDate());
        }
    }
}
