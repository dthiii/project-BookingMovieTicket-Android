package com.example.project_android_booking_movietickets.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.model.FoodItem
import java.text.DecimalFormat
import java.text.NumberFormat

class FoodInfoAdapter(
    private val context: Context,
    private val foodList: MutableList<FoodItem>,
    private val onEditClick: (FoodItem) -> Unit,
    private val onDeleteClick: (FoodItem) -> Unit
) : RecyclerView.Adapter<FoodInfoAdapter.FoodViewHolder>() {

    class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFood: ImageView = view.findViewById(R.id.imgFood)
        val tvFoodName: TextView = view.findViewById(R.id.tvFoodName)
        val tvFoodDescription: TextView = view.findViewById(R.id.tvFoodDescription)
        val tvFoodPrice: TextView = view.findViewById(R.id.tvFoodPrice)
        val btnEdit: ImageView = view.findViewById(R.id.imvEdit)
        val btnDelete: ImageView = view.findViewById(R.id.imvDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_food_info, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]
        holder.tvFoodName.text = food.TenDoAn
        holder.tvFoodDescription.text = food.MoTa
        holder.tvFoodPrice.text = formatPrice(food.GiaBan)

        // Load ảnh poster bằng Glide
        Glide.with(context)
            .load(food.AnhMinhHoa)
            .into(holder.imgFood)

        // Xử lý sự kiện click vào nút "Edit"
        holder.btnEdit.setOnClickListener {
            onEditClick(food)
        }

        // Xử lý sự kiện click vào nút "Delete"
        holder.btnDelete.setOnClickListener {
            onDeleteClick(food)
        }
    }

    override fun getItemCount() = foodList.size

    fun updateData(newList: List<FoodItem>) {
        foodList.clear()
        foodList.addAll(newList)
        notifyDataSetChanged()
    }

    // Hàm định dạng tiền tệ
    private fun formatPrice(price: Int): String {
        val formatter: NumberFormat = DecimalFormat("#,###đ")
        return formatter.format(price)
    }
}
