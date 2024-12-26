package com.example.movietime.domain.usecase

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestOptions
import com.example.movietime.databinding.ActivityMainBinding
import com.example.movietime.databinding.SlideItemContainerBinding
import com.example.movietime.domain.model.SliderItem

class SliderAdapter(private val sliderItems: MutableList<SliderItem>,
                    private val viewpager2: ViewPager2):
    RecyclerView.Adapter<SliderAdapter.SliderViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SliderAdapter.SliderViewHolder {
        val context = parent.context
        val view = SlideItemContainerBinding.inflate(LayoutInflater.from(context), parent, false)
        return SliderViewHolder(view.root)
    }

    override fun onBindViewHolder(holder: SliderAdapter.SliderViewHolder, position: Int) {
        holder.bind(sliderItems[position])
        if (position == sliderItems.size - 2) {
            viewpager2.post(runnable)
        }
    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }

    class SliderViewHolder(private val imageView: ImageView) : RecyclerView.ViewHolder(imageView) {
        fun bind(sliderItem: SliderItem) {
            val requestOptions = RequestOptions()
                .transforms(CenterCrop(), RoundedCorners(60))

            Glide.with(itemView.context)
                .load(sliderItem.image) // Використання властивості замість getImage()
                .apply(requestOptions)
                .into(imageView) // Використання imageView напряму
        }
    }

    private val runnable = Runnable {
        sliderItems.addAll(sliderItems)
        notifyDataSetChanged()
    }


}