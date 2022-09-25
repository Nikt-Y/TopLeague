package com.yakun.topleague.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yakun.topleague.R
import com.yakun.topleague.firestore.FirestoreClass
import com.yakun.topleague.models.InfoBlock
import com.yakun.topleague.models.Task
import com.yakun.topleague.ui.adapters.MyInfoBlockListAdapter
import com.yakun.topleague.utils.Constants
import kotlinx.android.synthetic.main.activity_add_task.*

class AddTaskActivity : BaseActivity(), View.OnClickListener {

    private var selectedCourseID = ""

    // A global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    private var selectedItemNum: Int = 0
    private var infoBlockList: ArrayList<InfoBlock> = ArrayList()
    private var curNum: Int = 0
    private var hintNum: Int = 0
    lateinit var adapterInfoBlocks: RecyclerView.Adapter<RecyclerView.ViewHolder>
    var imgCount = -1
    private var tw: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        if (intent.hasExtra(Constants.SELECTED_COURSE)) {
            // Get the user details from intent as a ParcelableExtra.
            selectedCourseID = intent.getStringExtra(Constants.SELECTED_COURSE)!!
        }

        setupActionBar()

        infoBlockList.add(InfoBlock(number = -2, is_img = false))
        infoBlockList.add(InfoBlock(number = -1, is_img = true))
        infoBlockList.add(InfoBlock(number = -3, is_img = false))

        rv.layoutManager = LinearLayoutManager(this)
        rv.setHasFixedSize(true)

        adapterInfoBlocks = MyInfoBlockListAdapter(
            this,
            this,
            infoBlockList,
            true
        )
        rv.adapter = adapterInfoBlocks
        btn_add_text_qwe.setOnClickListener(this)
        btn_add_img_qwe.setOnClickListener(this)
        btn_add_hint_text.setOnClickListener(this)
        btn_add_hint_img.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
        setSelectedInfoBlockNum(0)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_add_text_qwe -> {
                    addText()
                }

                R.id.btn_add_img_qwe -> {
                    addImg()
                }

                R.id.btn_add_hint_text -> {
                    addHintText()
                }

                R.id.btn_add_hint_img -> {
                    addHintImg()
                }

                R.id.btn_submit -> {
                    if (validateLectureDetails()) {
                        uploadTaskImage()
                    }
                }
            }
        }
    }

    private fun addText() {
        curNum++
        val newInfBlock = InfoBlock(number = curNum)
        infoBlockList.add(newInfBlock)
        setSelectedInfoBlockNum(infoBlockList.size - 1)
        adapterInfoBlocks.notifyItemInserted(infoBlockList.size - 1)
        rv.post(Runnable { rv.smoothScrollToPosition(infoBlockList.size) })
    }

    private fun addImg() {
        curNum++
        val newInfBlock = InfoBlock(number = curNum, is_img = true)
        infoBlockList.add(newInfBlock)
        adapterInfoBlocks.notifyItemInserted(infoBlockList.size - 1)
        rv.post(Runnable { rv.smoothScrollToPosition(infoBlockList.size) })
    }

    private fun addHintText() {
        hintNum++
        val newInfBlock = InfoBlock(number = hintNum + 10000)
        infoBlockList.add(newInfBlock)
        setSelectedInfoBlockNum(infoBlockList.size - 1)
        adapterInfoBlocks.notifyItemInserted(infoBlockList.size - 1)
        rv.post(Runnable { rv.smoothScrollToPosition(infoBlockList.size) })
    }


    private fun addHintImg() {
        hintNum++
        val newInfBlock = InfoBlock(number = hintNum + 10000, is_img = true)
        infoBlockList.add(newInfBlock)
        adapterInfoBlocks.notifyItemInserted(infoBlockList.size - 1)
        rv.post(Runnable { rv.smoothScrollToPosition(infoBlockList.size) })
    }

    private fun setNewSelectedTextItem(selectedItem: Int) {
        if (tw != null) {
            et_text.removeTextChangedListener(tw)
        }
        tw = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                infoBlockList[selectedItem].text = et_text.text.toString()
                adapterInfoBlocks.notifyItemChanged(selectedItem)
            }
        }
        et_text.addTextChangedListener(tw)
        if (infoBlockList[selectedItem].number == -2) {
            til_product_title.hint = "Enter text for Title"
        } else if (infoBlockList[selectedItem].number > 10000) {
            til_product_title.hint =
                "Enter text for Hint " + infoBlockList[selectedItem].number % 10000
        } else if (infoBlockList[selectedItem].number == -3) {
            til_product_title.hint = "Enter text for Answer"
        } else {
            til_product_title.hint = "Enter text for Block " + infoBlockList[selectedItem].number
        }
        et_text.setText(infoBlockList[selectedItem].text)
    }

    fun setSelectedInfoBlockNum(position: Int) {
        selectedItemNum = position
        if (infoBlockList[position].is_img == false) {
            setNewSelectedTextItem(position)
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this@AddTaskActivity)
            } else {
                /*Requests permissions to be granted to this application. These permissions
                 must be requested in your manifest, they should not be granted to your app,
                 and they should have protection level*/
                ActivityCompat.requestPermissions(
                    this@AddTaskActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
    }

    /**
     * This function will identify the result of runtime permission after the user allows or deny permission based on the unique code.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddTaskActivity)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            // The uri of selection image from phone storage.
            infoBlockList[selectedItemNum].image_uri = data.data!!
            imgCount++

            adapterInfoBlocks.notifyItemChanged(selectedItemNum)
        }
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_add_product_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_add_product_activity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * A function to validate the product details.
     */
    private fun validateLectureDetails(): Boolean {
        if (infoBlockList[0].text.trim().isEmpty()) {
            showErrorSnackBar("Please enter Title of Task.", true)
            return false
        }

        if (infoBlockList[1].image_uri == null) {
            showErrorSnackBar("Please enter Image of Task.", true)
            return false
        }

        if (infoBlockList[2].text.trim().isEmpty()) {
            showErrorSnackBar("Please enter Answer of Task.", true)
            return false
        }

        if (infoBlockList.size == 3) {
            showErrorSnackBar("Please add text/image block.", true)
            return false
        }

        for (i in 3 until infoBlockList.size) {
            if (infoBlockList[i].is_img) {
                if (infoBlockList[i].image_uri == null) {
                    if (infoBlockList[i].number >= 10000) {
                        showErrorSnackBar("Please insert image into image hint ${infoBlockList[i].number % 10000}.", true)
                    } else {
                        showErrorSnackBar("Please insert image into image block ${infoBlockList[i].number}.", true)
                    }
                    return false
                }
            } else {
                if (infoBlockList[i].text.trim().isEmpty()) {
                    if (infoBlockList[i].number >= 10000) {
                        showErrorSnackBar("Please enter text into text hint ${infoBlockList[i].number % 10000}.", true)
                    } else {
                        showErrorSnackBar("Please enter text into text block ${infoBlockList[i].number}.", true)
                    }
                    return false
                }
            }
        }

        return true
    }

    /**
     * A function to upload the selected product image to firebase cloud storage.
     */
    private fun uploadTaskImage() {

        showProgressDialog("Creating Task...")

        FirestoreClass().uploadImageToCloudStorage(
            this@AddTaskActivity,
            infoBlockList[1].image_uri,
            Constants.TASK_IMAGE
        )
    }

    /**
     * A function to get the successful result of product image upload.
     */
    fun imageUploadSuccess(imageURL: String) {
        uploadTask(imageURL)
    }

    private fun uploadTask(imageURL: String) {
        // Here we get the text from editText and trim the space
        val task = Task(
            selectedCourseID,
            infoBlockList[0].text.trim(),
            imageURL,
            infoBlockList[2].text.trim()
        )

        FirestoreClass().uploadRecords(this@AddTaskActivity, task, Constants.TASKS)
    }

    /**
     * A function to return the successful result of Product upload.
     */
    fun taskUploadSuccess(id: String) {
        infoBlockList.removeAt(0)
        infoBlockList.removeAt(0)
        infoBlockList.removeAt(0)
        for (infoBlock in infoBlockList) {
            infoBlock.record_id = id
        }
        // Hide the progress dialog
        hideProgressDialog()
        showProgressDialog("Adding images...")


        FirestoreClass().uploadSomeImagesToCloudStorage(
            this,
            infoBlockList,
            Constants.INFO_BLOCK_TASKS,
            imgCount
        )
    }

    fun someImagesUploadSuccess(newInfoBlockList: ArrayList<InfoBlock>) {
        hideProgressDialog()
        showProgressDialog("Adding blocks...")
        FirestoreClass().uploadInfoBlocks(
            this@AddTaskActivity,
            newInfoBlockList,
            Constants.INFO_BLOCK_TASKS
        )
    }

    fun infoBlocksUploadSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@AddTaskActivity,
            "Success!",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    fun deleteBlock(position: Int) {
        if (infoBlockList[position].image_uri != null) {
            imgCount--
        }
        infoBlockList.removeAt(position)
        adapterInfoBlocks.notifyItemRemoved(position)
        setSelectedInfoBlockNum(0)
    }
}