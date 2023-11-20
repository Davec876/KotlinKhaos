package com.kotlinkhaos.ui.student.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
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
        val context = viewHolder.itemView.context // Get context from the itemView
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val quiz = dataSet[position]
        viewHolder.quizName.text = quiz.name

        if (quiz.usersAttempt != null) {
            viewHolder.quizButton.text = "${quiz.usersAttempt.score}/10"
            viewHolder.quizButton.isEnabled = false
            // Change text color to grey
            viewHolder.quizButton.setTextColor(ContextCompat.getColor(context, R.color.gray))

            // Change stroke color to grey
            (viewHolder.quizButton as? MaterialButton)?.strokeColor =
                ContextCompat.getColorStateList(context, R.color.gray)
        } else {
            viewHolder.quizButton.text =
                context.getString(R.string.quiz_course_card_start_quiz_button)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun updateData(newQuizList: List<StudentQuizsForCourseRes.StudentQuizDetailsRes>) {
        // We want the latest quiz to be the first quiz in the dataset
        dataSet = newQuizList.reversed()
        notifyDataSetChanged()
    }
}