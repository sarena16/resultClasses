package com.example.resultclasses
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.resultclasse.TestResult


class TestResultsAdapter(
    private val testResults: List<TestResult>,
    private val onItemClickListener: (TestResult) -> Unit
) : RecyclerView.Adapter<TestResultsAdapter.TestResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_test_result, parent, false)
        return TestResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestResultViewHolder, position: Int) {
        val testResult = testResults[position]
        holder.bind(testResult)
    }

    override fun getItemCount(): Int {
        return testResults.size
    }

    inner class TestResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTestName: TextView = itemView.findViewById(R.id.tvTestName)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(testResult: TestResult) {
            tvTestName.text = testResult.testName
            tvDate.text = "Date: ${convertTimestampToDate(testResult.timestamp)}"

            itemView.setOnClickListener {
                onItemClickListener.invoke(testResult)
            }
        }

        private fun convertTimestampToDate(timestamp: Long): String {
            val sdf = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault())
            return sdf.format(java.util.Date(timestamp * 1000))
        }
    }
}
