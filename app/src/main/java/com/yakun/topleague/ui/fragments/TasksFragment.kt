package com.yakun.topleague.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.yakun.topleague.R
import com.yakun.topleague.firestore.FirestoreClass
import com.yakun.topleague.models.Course
import com.yakun.topleague.models.Task
import com.yakun.topleague.ui.activities.AddCourseActivity
import com.yakun.topleague.ui.activities.AddTaskActivity
import com.yakun.topleague.ui.adapters.MyCoursesListAdapter
import com.yakun.topleague.ui.adapters.MyTasksListAdapter
import com.yakun.topleague.utils.Constants
import kotlinx.android.synthetic.main.fragment_tasks.*

class TasksFragment : BaseFragment() {
    private var admin = 0
    private var depth = 0
    private var selectedCourseID = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (depth == 0) {
            getCoursesListFromFireStore()
        } else {
            getTasksListFromFireStore(selectedCourseID)
        }
        adminCheck()
    }

    fun successCoursesListFromFireStore(coursesList: ArrayList<Course>) {
        // Hide Progress dialog.
        hideProgressDialog()

        if (coursesList.size > 0) {
            rv_my_theory_items.visibility = View.VISIBLE
            tv_no_records_found.visibility = View.GONE

            rv_my_theory_items.layoutManager = LinearLayoutManager(activity)
            rv_my_theory_items.setHasFixedSize(true)

            val adapterProducts = MyCoursesListAdapter(
                requireActivity(),
                coursesList,
                this@TasksFragment,
                admin
            )
            rv_my_theory_items.adapter = adapterProducts
        } else {
            rv_my_theory_items.visibility = View.GONE
            tv_no_records_found.visibility = View.VISIBLE
        }
    }

    private fun getCoursesListFromFireStore() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FirestoreClass().getCoursesList(this@TasksFragment)
    }

    fun adminCheck() {
        val prefs: SharedPreferences = requireActivity().getSharedPreferences(
            "admin", Context.MODE_PRIVATE)
        val adminNum = prefs.getInt("admin", 0)
        admin = adminNum
        if (admin > 0) {
            iv_add.visibility = View.VISIBLE
            iv_add.setOnClickListener {
                if (depth == 0) {
                    startActivity(Intent(activity, AddCourseActivity::class.java))
                } else {
                    val intent = Intent(activity, AddTaskActivity()::class.java)
                    intent.putExtra(Constants.SELECTED_COURSE, selectedCourseID)
                    startActivity(intent)
                }
            }
        }
    }

    /**
     * A function that will call the delete function of FirestoreClass that will delete the product added by the user.
     *
     * @param taskID To specify which product need to be deleted.
     */
    fun deleteTask(taskID: String) {
        showAlertDialogToDeleteCourse(taskID, Constants.TASKS)
    }

    /**
     * A function that will call the delete function of FirestoreClass that will delete the product added by the user.
     *
     * @param courseID To specify which product need to be deleted.
     */
    fun deleteCourse(courseID: String) {
        showAlertDialogToDeleteCourse(courseID, Constants.COURSES)
    }

    /**
     * A function to notify the success result of product deleted from cloud firestore.
     */
    fun deleteSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.record_delete_success_message),
            Toast.LENGTH_SHORT
        ).show()

        // Get the latest products list from cloud firestore.
        if (depth == 0) {
            getCoursesListFromFireStore()
        } else {
            getTasksListFromFireStore(selectedCourseID)
        }
    }
    // END

    /**
     * A function to show the alert dialog for the confirmation of delete product from cloud firestore.
     */
    private fun showAlertDialogToDeleteCourse(recordID: String, collection: String) {
        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Call the function of Firestore class.
            FirestoreClass().deleteRecord(this@TasksFragment, recordID, collection)

            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun depthDown(courseID: String, courseName: String) {
        selectedCourseID = courseID
        tv_title.text = courseName.uppercase()
        depth = 1
        getTasksListFromFireStore(courseID)
        iv_back.visibility = View.VISIBLE
        iv_back.setOnClickListener { depthUp() }
    }

    fun depthUp() {
        selectedCourseID = ""
        depth = 0
        getCoursesListFromFireStore()
        iv_back.visibility = View.GONE
        tv_title.text = "TASKS"
    }

    private fun getTasksListFromFireStore(courseID: String) {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FirestoreClass().getTasksList(this@TasksFragment, courseID)
    }

    /**
     * A function to get the successful lecture list from cloud firestore.
     *
     * @param tasksList Will receive the lecture list from cloud firestore.
     */
    fun successTasksListFromFireStore(tasksList: ArrayList<Task>) {

        // Hide Progress dialog.
        hideProgressDialog()

        if (tasksList.size > 0) {
            rv_my_theory_items.visibility = View.VISIBLE
            tv_no_records_found.visibility = View.GONE

            rv_my_theory_items.layoutManager = LinearLayoutManager(activity)
            rv_my_theory_items.setHasFixedSize(true)

            val adapterProducts = MyTasksListAdapter(
                requireActivity(),
                tasksList,
                this@TasksFragment,
                admin
            )
            rv_my_theory_items.adapter = adapterProducts
        } else {
            rv_my_theory_items.visibility = View.GONE
            tv_no_records_found.visibility = View.VISIBLE
        }
    }
}