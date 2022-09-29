package com.yakun.topleague.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yakun.topleague.models.*
import com.yakun.topleague.ui.activities.*
import com.yakun.topleague.ui.fragments.ProfileFragment
import com.yakun.topleague.ui.fragments.TasksFragment
import com.yakun.topleague.ui.fragments.TheoryFragment
import com.yakun.topleague.utils.Constants
import java.lang.Exception
import java.util.*


class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user",
                    e
                )
            }
    }

    /**
     * A function to get the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getUserDetails(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val user = document.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(
                    Constants.TOPLEAGUE_PREFERENCES,
                    Context.MODE_PRIVATE
                )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SplashActivity -> {
                        activity.setAdmin(user.admin)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    fun getUserDetails(fragment: Fragment) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)!!

                when (fragment) {
                    is ProfileFragment -> {
                        fragment.userDetailsSuccess(user)
                    }

                }
            }
            .addOnFailureListener { e ->
                when (fragment) {
                    is ProfileFragment -> {
                        fragment.hideProgressDialog()
                    }

                }
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

    // A function to upload the image to the cloud storage.
    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {
        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }

                            is AddLectureActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }

                            is AddTaskActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }

                            is AddCourseActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }

                    is AddLectureActivity -> {
                        activity.hideProgressDialog()
                    }

                    is AddCourseActivity -> {
                        activity.hideProgressDialog()
                    }

                    is AddTaskActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    /**
     * A function to make an entry of the user's product in the cloud firestore database.
     */
    fun uploadRecords(activity: Activity, recordInfo: Any, collection: String) {
        val documentReference: DocumentReference = mFireStore.collection(collection).document()
        documentReference
            .set(recordInfo, SetOptions.merge())
            .addOnSuccessListener {
                when (activity) {
                    is AddLectureActivity -> {
                        activity.lectureUploadSuccess(documentReference.id)
                    }

                    is AddTaskActivity -> {
                        activity.taskUploadSuccess(documentReference.id)
                    }

                    is AddCourseActivity -> {
                        activity.courseUploadSuccess()
                    }
                }
                // Here call a function of base activity for transferring the result to it.
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is AddLectureActivity -> {
                        activity.hideProgressDialog()
                    }

                    is AddTaskActivity -> {
                        activity.hideProgressDialog()
                    }

                    is AddCourseActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details.",
                    e
                )
            }
    }

    /**
     * A function to get the products list from cloud firestore.
     *
     * @param fragment The fragment is passed as parameter as the function is called from fragment and need to the success result.
     */
    fun getLecturesList(fragment: TheoryFragment, courseID: String) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.LECTURES)
            .whereEqualTo(Constants.COURSE_ID, courseID)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { documents ->
                // Here we have created a new instance for Products ArrayList.
                val lecturesList: ArrayList<Lecture> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in documents.documents) {

                    val lecture = i.toObject(Lecture::class.java)
                    lecture!!.lecture_id = i.id

                    lecturesList.add(lecture)
                }
                lecturesList.sortBy { it.title }
                fragment.successLecturesListFromFireStore(lecturesList)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                fragment.hideProgressDialog()
            }
    }

    fun getTasksList(fragment: TasksFragment, courseID: String) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.TASKS)
            .whereEqualTo(Constants.COURSE_ID, courseID)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { documents ->
                // Here we have created a new instance for Products ArrayList.
                val taskList: ArrayList<Task> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in documents.documents) {

                    val task = i.toObject(Task::class.java)
                    task!!.task_id = i.id

                    taskList.add(task)
                }
                taskList.sortBy { it.title }
                fragment.successTasksListFromFireStore(taskList)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                fragment.hideProgressDialog()
            }
    }

    fun getCoursesList(fragment: Fragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.COURSES)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { documents ->
                // Here we have created a new instance for Products ArrayList.
                val coursesList: ArrayList<Course> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in documents.documents) {

                    val course = i.toObject(Course::class.java)
                    course!!.course_id = i.id

                    coursesList.add(course)
                }

                when (fragment) {
                    is TheoryFragment -> {
                        fragment.successCoursesListFromFireStore(coursesList)
                    }
                    is TasksFragment -> {
                        fragment.successCoursesListFromFireStore(coursesList)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (fragment) {
                    is TheoryFragment -> {
                        fragment.hideProgressDialog()
                    }
                    is TasksFragment -> {
                        fragment.hideProgressDialog()
                    }
                }

                Log.e("Get Product List", "Error while getting product list.", e)
            }
    }

    /**
     * A function to delete the product from the cloud firestore.
     */
    fun deleteRecord(fragment: Fragment, recordID: String, collection: String) {
        mFireStore.collection(collection)
            .document(recordID)
            .delete()
            .addOnSuccessListener {

                when (fragment) {
                    is TheoryFragment -> {
                        fragment.deleteSuccess()
                    }
                    is TasksFragment -> {
                        fragment.deleteSuccess()
                    }
                }
                // Notify the success result to the base class.
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                when (fragment) {
                    is TheoryFragment -> {
                        fragment.hideProgressDialog()
                    }
                    is TasksFragment -> {
                        fragment.hideProgressDialog()
                    }
                }

                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product.",
                    e
                )
            }
    }

    /**
     * A function to get the product details based on the product id.
     */
    fun getInfoBlocksForRecord(activity: Activity, recordID: String, collection: String) {
        mFireStore.collection(collection)
            .whereEqualTo("record_id", recordID)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { documents ->
                // Here we have created a new instance for Products ArrayList.
                val infoBlocksList: ArrayList<InfoBlock> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in documents.documents) {
                    val infoBlock = i.toObject(InfoBlock::class.java)
                    infoBlock!!.info_block_id = i.id

                    infoBlocksList.add(infoBlock)
                }
                infoBlocksList.sortBy { it.number }
                when (activity) {
                    is TheoryDetailsActivity -> {
                        activity.theoryDetailsSuccess(infoBlocksList)
                    }
                    is TaskDetailsActivity -> {
                        activity.taskDetailsSuccess(infoBlocksList)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is TheoryDetailsActivity -> {
                        activity.hideProgressDialog()
                    }
                    is TaskDetailsActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun uploadSomeImagesToCloudStorage(
        activity: Activity,
        infoBlockList: ArrayList<InfoBlock>,
        imageType: String,
        imgCount: Int
    ) {
        var counter = 0
        for (i in 0 until infoBlockList.size) {
            val infoBlock = infoBlockList[i]
            if (infoBlock.number >= 0 && infoBlock.is_img) {
                val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                    imageType + System.currentTimeMillis() + "."
                            + Constants.getFileExtension(
                        activity,
                        infoBlock.image_uri
                    )
                )

                sRef.putFile(infoBlock.image_uri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // The image upload is success
                        Log.e(
                            "Firebase Image URL",
                            taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                        )

                        // Get the downloadable url from the task snapshot
                        taskSnapshot.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { uri ->
                                Log.e("Downloadable Image URL", uri.toString())
                                infoBlock.image = uri.toString()
                                counter++
                                if (counter == imgCount) {
                                    when (activity) {
                                        is AddLectureActivity -> {
                                            activity.someImagesUploadSuccess(infoBlockList)
                                        }
                                        is AddTaskActivity -> {
                                            activity.someImagesUploadSuccess(infoBlockList)
                                        }
                                    }
                                }
                            }
                    }
                    .addOnFailureListener { exception ->
                        when (activity) {
                            is AddLectureActivity -> {
                                activity.hideProgressDialog()
                            }
                            is AddTaskActivity -> {
                                activity.hideProgressDialog()
                            }
                        }
                        Log.e(
                            activity.javaClass.simpleName,
                            exception.message,
                            exception
                        )
                    }
            }
        }
        if (imgCount <= 0) {
            when (activity) {
                is AddLectureActivity -> {
                    activity.someImagesUploadSuccess(infoBlockList)
                }
                is AddTaskActivity -> {
                    activity.someImagesUploadSuccess(infoBlockList)
                }
            }
        }
    }

    fun uploadInfoBlocks(
        activity: Activity,
        infoBlockList: ArrayList<InfoBlock>,
        collection: String
    ) {
        for (i in 0 until infoBlockList.size) {
            if (!infoBlockList[i].is_img) {
                infoBlockList[i].text.trim()
            }
            infoBlockList[i].image_uri = null
            mFireStore.collection(collection).document()
                .set(infoBlockList[i], SetOptions.merge())
                .addOnSuccessListener {
                    if (i == infoBlockList.size-1) {
                        when (activity) {
                            is AddLectureActivity -> {
                                activity.infoBlocksUploadSuccess()
                            }
                            is AddTaskActivity -> {
                                activity.infoBlocksUploadSuccess()
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    when (activity) {
                        is AddLectureActivity -> {
                            activity.hideProgressDialog()
                        }
                        is AddTaskActivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while uploading the product details.",
                        e
                    )
                }
        }
    }

    fun setTaskSolved(mTaskId: String) {
        mFireStore.collection(Constants.TASKS).document(mTaskId).update("solved", true)
    }
}