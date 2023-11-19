package com.kotlinkhaos.ui.instructor.home.quizDetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kotlinkhaos.R
import com.kotlinkhaos.classes.services.InstructorQuizsForCourseRes

class UserAttemptListAdapter(
    private var dataSet: List<InstructorQuizsForCourseRes.InstructorQuizDetailsRes.UserAttempt>,
    private val clickListener: (String) -> Unit
) :
    RecyclerView.Adapter<UserAttemptListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView
        val score: TextView
        val viewAttemptButton: Button

        init {
            userName = view.findViewById(R.id.quizAttemptUserName)
            score = view.findViewById(R.id.quizAttemptScore)
            viewAttemptButton = view.findViewById(R.id.viewUserAttemptButton)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.quiz_user_attempt_card_layout, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val attempt = dataSet[position]
        viewHolder.userName.text = attempt.studentId
        viewHolder.score.text = "${attempt.score}/10"
        viewHolder.viewAttemptButton.setOnClickListener {
            clickListener(attempt.studentId)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}