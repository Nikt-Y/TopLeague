package com.yakun.topleague.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.yakun.topleague.R
import com.yakun.topleague.models.Course
import com.yakun.topleague.ui.fragments.TasksFragment
import com.yakun.topleague.ui.fragments.TheoryFragment
import com.yakun.topleague.utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*

/**
 * A adapter class for products list items.
 */
open class MyCoursesListAdapter(
    private val context: Context,
    private var list: ArrayList<Course>,
    private val fragment: Fragment,
    private val admin: Int
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
                R.layout.item_list_layout,
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
            GlideLoader(context).loadRecordPicture(model.image, holder.itemView.iv_item_image)

            holder.itemView.tv_item_name.text = model.title

            holder.itemView.ib_delete_product.setOnClickListener {
                when (fragment) {
                    is TheoryFragment -> {
                        fragment.deleteCourse(model.course_id)
                    }
                    is TasksFragment -> {
                        fragment.deleteCourse(model.course_id)
                    }
                }
            }
            if (admin > 0) {
                holder.itemView.ib_delete_product.visibility = View.VISIBLE
            }

            holder.itemView.setOnClickListener {
                when (fragment) {
                    is TheoryFragment -> {
                        fragment.depthDown(model.course_id, model.title)
                    }
                    is TasksFragment -> {
                        fragment.depthDown(model.course_id, model.title)
                    }
                }
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