package database;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gregsnyder.courseappwguc196.CourseAddEditActivity;
import com.gregsnyder.courseappwguc196.CourseDetailsActivity;
import com.gregsnyder.courseappwguc196.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import entity.Course;
import utilities.DateTextFormatting;

import static utilities.Constants.COURSE_ID_KEY;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private final List<Course> mCourses;
    private final Context mContext;
    private final RecyclerContext rContext;
    private CourseSelectedListener courseSelectedListener;

    public CourseAdapter(List<Course> mCourses, Context mContext, RecyclerContext rContext, CourseSelectedListener courseSelectedListener) {
        this.mCourses = mCourses;
        this.mContext = mContext;
        this.rContext = rContext;
        this.courseSelectedListener = courseSelectedListener;
    }

    public void setCourseSelectedListener(CourseAdapter.CourseSelectedListener courseSelectedListener) {
        this.courseSelectedListener = courseSelectedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cardview_course_list, parent, false);
        return new ViewHolder(view, courseSelectedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.ViewHolder holder, int position) {
        final Course course = mCourses.get(position);
        holder.tvTitle.setText(course.getTitle());
        String startAndEnd = DateTextFormatting.cardDateFormat.format(course.getStartDate()) + " to " + DateTextFormatting.cardDateFormat.format(course.getAnticipatedEndDate());
        holder.tvDates.setText(startAndEnd);

        switch(rContext) {
            case MAIN:
                //Log.v("rContext", "rContext is " + rContext.name());
                holder.courseFab.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_edit));
                holder.courseImageBtn.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, CourseDetailsActivity.class);
                    intent.putExtra(COURSE_ID_KEY, course.getId());
                    mContext.startActivity(intent);
                });

                holder.courseFab.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, CourseAddEditActivity.class);
                    intent.putExtra(COURSE_ID_KEY, course.getId());
                    mContext.startActivity(intent);
                });
                break;
            case CHILD:
                holder.courseFab.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_delete));
                holder.courseFab.setOnClickListener(v -> {
                    if(courseSelectedListener != null){
                        courseSelectedListener.onCourseSelected(position, course);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.card_course_title)
        TextView tvTitle;
        @BindView(R.id.card_course_fab)
        FloatingActionButton courseFab;
        @BindView(R.id.card_course_dates)
        TextView tvDates;
        @BindView(R.id.btn_course_details)
        ImageButton courseImageBtn;
        CourseSelectedListener courseSelectedListener;

        public ViewHolder(View itemView, CourseSelectedListener courseSelectedListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.courseSelectedListener = courseSelectedListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            courseSelectedListener.onCourseSelected(getAdapterPosition(), mCourses.get(getAdapterPosition()));
        }
    }

    public interface CourseSelectedListener {
        void onCourseSelected(int position, Course course);
    }
}