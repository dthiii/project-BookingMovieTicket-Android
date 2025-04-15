package com.example.project_android_booking_movietickets.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_android_booking_movietickets.R
import com.example.project_android_booking_movietickets.model.FoodItem
import com.example.project_android_booking_movietickets.model.OrderItem
import java.text.DecimalFormat
import java.text.NumberFormat

class FoodAdapter(
    private val context: Context,
    private val foodList: List<FoodItem>,
    private val totalPriceTextView: TextView,
    private var totalSeatPrice: Int,
    private val listItem: MutableList<OrderItem>,
    private val onTotalPriceUpdated: (Int) -> Unit // Callback
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    private var totalPrice: Int = totalSeatPrice

    class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFood: ImageView = view.findViewById(R.id.imgFood)
        val tvFoodName: TextView = view.findViewById(R.id.tvFoodName)
        val tvFoodDescription: TextView = view.findViewById(R.id.tvFoodDescription)
        val tvFoodPrice: TextView = view.findViewById(R.id.tvFoodPrice)
        val imvIncrease: ImageView = view.findViewById(R.id.imvIncrease)
        val imvDecrease: ImageView = view.findViewById(R.id.imvDecrease)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false)
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

        var quantity = food.selectedQuantity
        holder.tvQuantity.text = quantity.toString()

        // Hàm cập nhật tổng tiền
        fun updateTotalPrice() {
            val foodTotal = foodList.sumOf { it.selectedQuantity * it.GiaBan }
            totalPrice = totalSeatPrice + foodTotal  // Tổng tiền = giá ghế + giá đồ ăn
            totalPriceTextView.text = formatPrice(totalPrice)
            onTotalPriceUpdated(totalPrice)
        }

        // Cập nhật màu nút -
        fun updateDecreaseButton() {
            if (quantity == 0) {
                holder.imvDecrease.setImageResource(R.drawable.btn_decrease_dark)
            } else {
                holder.imvDecrease.setImageResource(R.drawable.btn_decrease_green)
            }
        }

        updateDecreaseButton()

        holder.imvIncrease.setOnClickListener {
            quantity++
            food.selectedQuantity = quantity
            holder.tvQuantity.text = quantity.toString()
            updateDecreaseButton()
            updateTotalPrice()
            updateListItem(food, quantity)
        }

        holder.imvDecrease.setOnClickListener {
            if (quantity > 0) {
                quantity--
                food.selectedQuantity = quantity
                holder.tvQuantity.text = quantity.toString()
                updateDecreaseButton()
                updateTotalPrice()
                updateListItem(food, quantity)
            }
        }
    }

    override fun getItemCount() = foodList.size

    // Hàm định dạng tiền tệ
    private fun formatPrice(price: Int): String {
        val formatter: NumberFormat = DecimalFormat("#,###đ")
        return formatter.format(price)
    }

    private fun updateListItem(food: FoodItem, quantity: Int) {
        val existingItem = listItem.find { it.ten == food.TenDoAn }
        if (quantity > 0) {
            if (existingItem != null) {
                existingItem.soLuong = quantity
                existingItem.thanhTien = quantity * food.GiaBan
            } else {
                listItem.add(OrderItem(food.TenDoAn, quantity, food.GiaBan, food.GiaBan * quantity))
            }
        } else {
            listItem.remove(existingItem)
        }
    }
}
