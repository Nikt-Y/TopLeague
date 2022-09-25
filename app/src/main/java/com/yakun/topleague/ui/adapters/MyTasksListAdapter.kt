package com.yakun.topleague.ui.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yakun.topleague.R
import com.yakun.topleague.models.Lecture
import com.yakun.topleague.models.Task
import com.yakun.topleague.ui.activities.TaskDetailsActivity
import com.yakun.topleague.ui.activities.TheoryDetailsActivity
import com.yakun.topleague.ui.fragments.TasksFragment
import com.yakun.topleague.ui.fragments.TheoryFragment
import com.yakun.topleague.utils.Constants
import com.yakun.topleague.utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*

open class MyTasksListAdapter(
    private val context: Context,
    private var list: ArrayList<Task>,
    private val fragment: TasksFragment,
    private val admin: Int,
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

            if (admin > 0) {
                holder.itemView.ib_delete_product.visibility = View.VISIBLE
                holder.itemView.ib_delete_product.setOnClickListener {
                    fragment.deleteTask(model.task_id)
                }
            } else {
                holder.itemView.ib_delete_product.visibility = View.GONE
            }
//            if (model.solved) {
//                holder.itemView.item_cl.setBackgroundColor(context.resources.getColor(R.color.colorOrderStatusDelivered))
//            }

            holder.itemView.setOnClickListener {
                Log.e("My", model.task_id)
                // Launch Product details screen.
                val intent = Intent(context, TaskDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.task_id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.title)
                intent.putExtra(Constants.ANSWER, model.answer)
                context.startActivity(intent)
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