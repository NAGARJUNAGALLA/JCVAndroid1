package com.jcv.mocktests.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.jcv.mocktests.data.model.Course
import com.jcv.mocktests.data.model.Question
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.net.URL

class CbtRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    suspend fun fetchCourses(): List<Course> {
        val snapshot = db.collection("exams").document("testList").get().await()
        val list = snapshot.get("tests") as? List<Map<String, Any>> ?: emptyList()
        return list.map { map ->
            Course(
                sheetId = map["sheetId"] as? String ?: "",
                sheetName = map["sheetName"] as? String ?: "",
                title = map["title"] as? String ?: "",
                fee = map["fee"] ?: 0,
                expiryDate = map["expiryDate"] as? String,
                description = map["description"] as? String
            )
        }
    }

    // Fetch Pro Questions from Firestore
    suspend fun fetchProCourseQuestions(courseId: String): Map<String, Map<String, List<Question>>> {
        val doc = db.collection("pro_course_questions").document(courseId).get().await()
        val testsData = doc.get("tests") as? Map<String, Map<String, List<Map<String, Any>>>> ?: return emptyMap()
        
        val resultMap = mutableMapOf<String, MutableMap<String, List<Question>>>()
        for ((testName, sections) in testsData) {
            val secMap = mutableMapOf<String, List<Question>>()
            for ((secName, qList) in sections) {
                secMap[secName] = qList.mapIndexed { idx, q ->
                    Question(
                        id = idx + 1,
                        text = q["text"] as? String ?: "",
                        options = (q["options"] as? List<*>)?.map { it.toString() } ?: emptyList(),
                        correct = (q["correct"] as? Long)?.toInt() ?: 0
                    )
                }
            }
            resultMap[testName] = secMap
        }
        return resultMap
    }

    // Save final scores to Firestore with merge
    suspend fun saveExamScore(userId: String, name: String, email: String, testKey: String, score: Int, maxMarks: Int) {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        val data = hashMapOf(
            "studentId" to userId,
            "name" to name,
            "email" to email,
            "examHistory" to hashMapOf(
                testKey to hashMapOf(
                    "score" to score,
                    "maxMarks" to maxMarks,
                    "date" to today
                )
            )
        )
        db.collection("student_scores").document(userId).set(data, SetOptions.merge()).await()
    }
}
