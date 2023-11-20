package com.kotlinkhaos.ui.student.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kotlinkhaos.R
import com.kotlinkhaos.classes.services.StudentQuizsForCourseRes

class QuizsForCourseListAdapter(
    private var dataSet: List<StudentQuizsForCourseRes.StudentQuizDetailsRes>
) :
    RecyclerView.Adapter<QuizsForCourseListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val quizName: TextView
        val quizButton: Button

        init {
            quizName = view.findViewById(R.id.quizName)
            quizButton = view.findViewById(R.id.button)

        }


    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.quiz_course_card_layout, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val quiz = dataSet[position]
        viewHolder.quizName.text = quiz.name

        if (quiz.usersAttempt != null) {
            viewHolder.quizButton.text = "${quiz.usersAttempt.score}/10"
            viewHolder.quizButton.isEnabled = false;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun updateData(newQuizList: List<StudentQuizsForCourseRes.StudentQuizDetailsRes>) {
        dataSet = newQuizList
        notifyDataSetChanged()
    }
}