package com.kotlinkhaos.ui.instructor.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kotlinkhaos.R

class QuizQuestionsListAdapter(
    private val dataSet: List<String>
) :
    RecyclerView.Adapter<QuizQuestionsListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val quizQuestionNumber: TextView
        val quizQuestion: TextView

        init {
            quizQuestionNumber = view.findViewById(R.id.quizQuestionNumber)
            quizQuestion = view.findViewById(R.id.quizQuestion)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.quiz_card_layout, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val question = dataSet[position]
        viewHolder.quizQuestionNumber.text =
            viewHolder.quizQuestionNumber.context.getString(
                R.string.quiz_card_question_number,
                (position + 1).toString()
            )
        viewHolder.quizQuestion.text = question
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}