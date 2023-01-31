package com.nexusinfinity.electronicsportal.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nexusinfinity.electronicsportal.R
import com.nexusinfinity.electronicsportal.model.Product

class ProductsViewAdapter(
    private val response: ProductClickResponse,
    private val response2: ButtonClicks
) : ListAdapter<Product, ProductsViewAdapter.ViewHolder>(DiffUtil()) {

    class DiffUtil() : androidx.recyclerview.widget.DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productId == newItem.productId &&
                    oldItem.productName == newItem.productName &&
                    oldItem.productModel == newItem.productModel &&
                    oldItem.productQnt == newItem.productQnt &&
                    oldItem.productTarget == newItem.productTarget &&
                    oldItem.buyingPrice == newItem.buyingPrice &&
                    oldItem.sellingPrice == newItem.sellingPrice
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_view, parent, false)
        val viewHolder = ViewHolder(view)
        view.setOnClickListener {
            response.onClick(getItem(viewHolder.adapterPosition), view)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentProduct = getItem(position)

        holder.productName.text = currentProduct.productName
        holder.productModel.text = currentProduct.productModel
        holder.buyingPrice.text = currentProduct.buyingPrice
        holder.sellingPrice.text = currentProduct.sellingPrice

        holder.available.text = currentProduct.productQnt
        holder.target.text = currentProduct.productTarget
        if (Integer.valueOf(holder.available.text.toString()) < Integer.valueOf(holder.target.text.toString())) {
            holder.available.setTextColor(Color.RED)
        } else if (Integer.valueOf(holder.available.text.toString()) == Integer.valueOf(holder.target.text.toString())) {
            holder.available.setTextColor(Color.YELLOW)
        }
        holder.sold.text = currentProduct.sold
        holder.profitText.text = "₹ ${
            (Integer.valueOf(currentProduct.sellingPrice) - Integer.valueOf(currentProduct.buyingPrice)) * Integer.valueOf(
                currentProduct.sold
            )
        }"
        holder.id.text = "${currentProduct.productId}."

        holder.editBtn.setOnClickListener {
            response2.onEditClick(currentProduct)
        }

        holder.deleteBtn.setOnClickListener {
            holder.progressDialog.visibility = View.VISIBLE
            response2.onDeleteClick(currentProduct, holder.progressDialog)
        }

        holder.shareBtn.setOnClickListener {
            response2.onShareClick(currentProduct)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //TextViews
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productModel: TextView = itemView.findViewById(R.id.productModel)
        val available: TextView = itemView.findViewById(R.id.availableText)
        val target: TextView = itemView.findViewById(R.id.targetText)
        val sold: TextView = itemView.findViewById(R.id.soldText)
        val buyingPrice: TextView = itemView.findViewById(R.id.buyingPrice)
        val sellingPrice: TextView = itemView.findViewById(R.id.sellingPrice)
        val profitText: TextView = itemView.findViewById(R.id.profitText)
        val id: TextView = itemView.findViewById(R.id.id)

        val progressDialog: RelativeLayout = itemView.findViewById(R.id.progressDialog)

        //Bottom Buttons
        val editBtn: Button = itemView.findViewById(R.id.editBtn)
        val deleteBtn: Button = itemView.findViewById(R.id.deleteBtn)
        val shareBtn: Button = itemView.findViewById(R.id.shareBtn)
    }
}

interface ProductClickResponse {
    fun onClick(product: Product, view: View)
}

interface ButtonClicks {
    fun onEditClick(product: Product)
    fun onDeleteClick(product: Product, progressDialog: RelativeLayout)
    fun onShareClick(product: Product)
}