package com.yakun.topleague.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yakun.topleague.R
import com.yakun.topleague.firestore.FirestoreClass
import com.yakun.topleague.models.InfoBlock
import com.yakun.topleague.ui.adapters.MyInfoBlockListAdapter
import com.yakun.topleague.utils.Constants
import kotlinx.android.synthetic.main.activity_task_details.*

class TaskDetailsActivity : BaseActivity(), View.OnClickListener {
    //    A global variable for lecture id.
    private var mTaskId: String = ""
    private var answer: String = ""
    private var infoBlockList: ArrayList<InfoBlock> = ArrayList()
    private val hintList: ArrayList<InfoBlock> = ArrayList()
    private var hintCounter: Int = 0
    lateinit var adapterInfoBlocks: RecyclerView.Adapter<RecyclerView.ViewHolder>

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_task_details)

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mTaskId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }
        if (intent.hasExtra(Constants.ANSWER)) {
            answer = intent.getStringExtra(Constants.ANSWER)!!
        }
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            tv_title.text = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        setupActionBar()

        getInfoBlocks()
        btn_check_answ.setOnClickListener(this)
        btn_get_hint.setOnClickListener(this)
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {
        iv_back.setOnClickListener { onBackPressed() }
    }

    /**
     * A function to call the firestore class function that will get the product details from cloud firestore based on the product id.
     */
    private fun getInfoBlocks() {

        // Show the product dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of FirestoreClass to get the product details.
        FirestoreClass().getInfoBlocksForRecord(this@TaskDetailsActivity,
            mTaskId,
            Constants.INFO_BLOCK_TASKS)
    }

    /**
     * A function to notify the success result of the product details based on the product id.
     *
     * @param lecture A model class with product details.
     */
    fun taskDetailsSuccess(taskDetails: ArrayList<InfoBlock>) {
        hideProgressDialog()
        for (i in 0 until taskDetails.size) {
            if (taskDetails[i].number >= 10000) {
                hintList.add(taskDetails[i])
            } else {
                infoBlockList.add(taskDetails[i])
            }
        }

        rv.layoutManager = LinearLayoutManager(this)
        rv.setHasFixedSize(true)

        adapterInfoBlocks = MyInfoBlockListAdapter(
            this,
            this,
            infoBlockList,
            false
        )
        rv.adapter = adapterInfoBlocks
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_get_hint -> {
                    if (hintCounter < hintList.size) {
                        infoBlockList.add(hintList[hintCounter])
                        hintCounter++
                        adapterInfoBlocks.notifyItemInserted(infoBlockList.size - 1)
                        rv.post(Runnable { rv.smoothScrollToPosition(infoBlockList.size) })
                    } else {
                        showErrorSnackBar("Unfortunately, there are no more hints", true)
                    }
                }

                R.id.btn_check_answ -> {
                    if (et_text.text.toString().trim() == answer.trim()) {
                        showErrorSnackBar("Well done!", false)
                        FirestoreClass().setTaskSolved(mTaskId)
                    } else {
                        showErrorSnackBar("The answer is incorrect.", true)
                    }
                }
            }
        }
    }
}