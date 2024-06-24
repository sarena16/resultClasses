package com.example.resultclasses

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resultclasses.TestResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ValueEventListener

class TestResultsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var testResultsAdapter: TestResultsAdapter
    private lateinit var database: DatabaseReference
    private lateinit var tvBestResult: TextView
    private lateinit var btnMemory: Button
    private lateinit var btnStroop: Button
    private lateinit var btnVisual: Button

    private val testResultsList = mutableListOf<TestResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_results)

        recyclerView = findViewById(R.id.recyclerViewTestResults)
        tvBestResult = findViewById(R.id.tvBestResult)
        btnMemory = findViewById(R.id.btnMemory)
        btnStroop = findViewById(R.id.btnStroop)
        btnVisual = findViewById(R.id.btnVisual)

        recyclerView.layoutManager = LinearLayoutManager(this)
        testResultsAdapter = TestResultsAdapter(testResultsList) { testResult ->
            // Handle item click here, navigate to detail activity or show details
            println("Clicked on test: ${testResult.testName}")
        }
        recyclerView.adapter = testResultsAdapter

        database = FirebaseDatabase.getInstance().reference

        btnMemory.setOnClickListener {
            fetchTestResults("memory")
        }
        btnStroop.setOnClickListener {
            fetchTestResults("stroop")
        }
        btnVisual.setOnClickListener {
            fetchTestResults("visual")
        }
    }

    private fun fetchTestResults(testType: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userTestsRef = database.child("users").child(userId ?: "").child("tests").child(testType)

        userTestsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                testResultsList.clear()
                var bestResult: TestResult? = null
                for (testSnapshot in snapshot.children) {
                    val testResult = testSnapshot.getValue(TestResult::class.java)
                    testResult?.let {
                        testResultsList.add(it)
                        if (bestResult == null || isBetterResult(it, bestResult, testType)) {
                            bestResult = it
                        }
                    }
                }
                testResultsAdapter.notifyDataSetChanged()
                tvBestResult.text = "Best Result: ${bestResult?.let { formatBestResult(it, testType) } ?: "N/A"}"
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun isBetterResult(result: TestResult, bestResult: TestResult, testType: String): Boolean {
        return when (testType) {
            "memory" -> result.score < bestResult.score // Assuming score is time in seconds
            else -> result.score > bestResult.score // For stroop and visual, higher score is better
        }
    }

    private fun formatBestResult(result: TestResult, testType: String): String {
        return when (testType) {
            "memory" -> "Time: ${result.score} seconds"
            else -> "Correct Answers: ${result.score}"
        }
    }
}
