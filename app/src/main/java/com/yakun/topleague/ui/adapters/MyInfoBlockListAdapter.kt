package com.yakun.topleague.ui.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yakun.topleague.R
import com.yakun.topleague.models.InfoBlock
import com.yakun.topleague.ui.activities.AddLectureActivity
import com.yakun.topleague.ui.activities.AddTaskActivity
import com.yakun.topleague.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_list_info_block.view.*
import kotlinx.android.synthetic.main.item_list_layout.view.*
import java.io.IOException

class MyInfoBlockListAdapter(
    private val context: Context,
    private val activity: Activity,
    private var list: ArrayList<InfoBlock>,
    private val isConstructor: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_info_block,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            if (isConstructor) {
                holder.itemView.header_title.visibility = View.VISIBLE
                if (model.number >= 0) {
                    holder.itemView.ib_delete.visibility = View.VISIBLE
                    holder.itemView.ib_delete.setOnClickListener{
                        when (activity) {
                            is AddLectureActivity -> {
                                activity.deleteBlock(position)
                            }
                            is AddTaskActivity -> {
                                activity.deleteBlock(position)
                            }
                        }
                    }

                    if (model.number > 10000) {
                        holder.itemView.header_title.text = "Hint " + model.number % 10000
                    } else {
                        holder.itemView.header_title.text = "Block " + model.number
                    }
                } else {
                    holder.itemView.ib_delete.visibility = View.GONE
                    if (model.number == -2) {
                        when (activity) {
                            is AddLectureActivity -> {
                                holder.itemView.header_title.text = "Title of Lecture"
                            }
                            is AddTaskActivity -> {
                                holder.itemView.header_title.text = "Title of Task"
                            }
                        }
                    }
                    if (model.number == -1) {
                        when (activity) {
                            is AddLectureActivity -> {
                                holder.itemView.header_title.text = "Image of Lecture"
                            }
                            is AddTaskActivity -> {
                                holder.itemView.header_title.text = "Image of Task"
                            }
                        }
                    }
                    if (model.number == -3) {
                        holder.itemView.header_title.text = "Answer to the Task"
                    }
                }
            } else {
                holder.itemView.header_title.visibility = View.GONE
                holder.itemView.ib_delete.visibility = View.GONE
            }

            if (model.is_img) {
                holder.itemView.math_view.visibility = View.GONE
                if (isConstructor) {
                    if (model.image_uri != null) {
                        holder.itemView.iv_add_update_product.setImageDrawable(
                            ContextCompat.getDrawable(
                                activity,
                                R.drawable.ic_vector_edit
                            )
                        )

                        try {
                            // Load the product image in the ImageView.
                            GlideLoader(activity).loadRecordPicture(
                                model.image_uri!!,
                                holder.itemView.iv_block_image
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    holder.itemView.fl_product_image.visibility = View.VISIBLE
                    holder.itemView.iv_add_update_product.setOnClickListener {
                        when (activity) {
                            is AddLectureActivity -> {
                                activity.setSelectedInfoBlockNum(position)
                            }
                            is AddTaskActivity -> {
                                activity.setSelectedInfoBlockNum(position)
                            }
                        }
                    }
                } else {
                    holder.itemView.fl_product_image.visibility = View.VISIBLE
                    holder.itemView.iv_add_update_product.visibility = View.GONE

                    GlideLoader(context).loadRecordPicture(model.image, holder.itemView.iv_block_image)
                }
            } else {
                holder.itemView.math_view.visibility = View.VISIBLE
                holder.itemView.fl_product_image.visibility = View.GONE
                if (isConstructor) {
                    holder.itemView.hit_box.setOnClickListener {
                        when (activity) {
                            is AddLectureActivity -> {
                                activity.setSelectedInfoBlockNum(position)
                            }
                            is AddTaskActivity -> {
                                activity.setSelectedInfoBlockNum(position)
                            }
                        }
                    }
                }
                holder.itemView.math_view.text = model.text
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}