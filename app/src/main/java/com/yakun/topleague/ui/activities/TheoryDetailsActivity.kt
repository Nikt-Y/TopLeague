package com.yakun.topleague.ui.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.yakun.topleague.R
import com.yakun.topleague.firestore.FirestoreClass
import com.yakun.topleague.models.InfoBlock
import com.yakun.topleague.models.Lecture
import com.yakun.topleague.ui.adapters.MyInfoBlockListAdapter
import com.yakun.topleague.utils.Constants
import kotlinx.android.synthetic.main.activity_theory_details.*

class TheoryDetailsActivity : BaseActivity() {
    private lateinit var mLectureDetails: Lecture

    // A global variable for lecture id.
    private var mLectureId: String = ""

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_theory_details)

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mLectureId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            tv_title.text = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        setupActionBar()

        getInfoBlocks()
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {
        iv_back.setOnClickListener{onBackPressed()}
    }

    /**
     * A function to call the firestore class function that will get the product details from cloud firestore based on the product id.
     */
    private fun getInfoBlocks() {

        // Show the product dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of FirestoreClass to get the product details.
        FirestoreClass().getInfoBlocksForRecord(this@TheoryDetailsActivity,
            mLectureId,
            Constants.INFO_BLOCK_LECTURE)
    }

    /**
     * A function to notify the success result of the product details based on the product id.
     *
     * @param lecture A model class with product details.
     */
    fun theoryDetailsSuccess(infoBlockList: ArrayList<InfoBlock>) {
        hideProgressDialog()

        rv.layoutManager = LinearLayoutManager(this)
        rv.setHasFixedSize(true)

        val adapterInfoBlocks = MyInfoBlockListAdapter(
            this,
            this,
            infoBlockList,
            false
        )
        rv.adapter = adapterInfoBlocks
    }
}