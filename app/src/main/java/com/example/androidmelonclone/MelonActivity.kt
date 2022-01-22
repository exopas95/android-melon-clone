package com.example.androidmelonclone

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.androidmelonclone.databinding.ActivityMelonBinding
import com.example.androidmelonclone.databinding.ItemRecyclerMelonContentViewBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MelonActivity : AppCompatActivity() {
    lateinit var glide: RequestManager
    lateinit var binding: ActivityMelonBinding
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMelonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        glide = Glide.with(this)

        (application as MasterApplication).service.getSongList()
            .enqueue(object : Callback<ArrayList<Melon>> {

                override fun onResponse(
                    call: Call<ArrayList<Melon>>,
                    response: Response<ArrayList<Melon>>
                ) {
                    if (response.isSuccessful) {
                        val melonList = response.body()
                        val adapter = melonList?.let {
                            MelonAdapter(
                                it,
                                glide
                            )
                        }
                        binding.contentList.adapter = adapter
                        binding.contentList.layoutManager =
                            LinearLayoutManager(
                                this@MelonActivity,
                                LinearLayoutManager.VERTICAL, false
                            )
                    }
                }

                override fun onFailure(call: Call<ArrayList<Melon>>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun onPause() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        super.onPause()
    }
}

class MelonAdapter(
    private var melonList: ArrayList<Melon>,
    var glide: RequestManager
) : RecyclerView.Adapter<MelonAdapter.MelonHolder>() {

    private var mediaPlayer: MediaPlayer? = null

    inner class MelonHolder(binding: ItemRecyclerMelonContentViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val melonTitle: TextView = binding.title
        val melonThumbnail: ImageView = binding.thumbnail
        val melonPlay: ImageView = binding.btnPlay
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MelonHolder {
        val binding = ItemRecyclerMelonContentViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MelonHolder(binding)
    }

    override fun onBindViewHolder(holder: MelonHolder, position: Int) {
        holder.melonTitle.text = melonList[position].title
        glide.load(melonList[position].thumbnail).into(holder.melonThumbnail)

        holder.melonPlay.setOnClickListener {
            val path = melonList[position].song

            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                mediaPlayer = MediaPlayer.create(holder.melonPlay.context, Uri.parse(path))
                mediaPlayer?.start()
            } catch (e: Exception) {

            }
        }
    }

    override fun getItemCount(): Int = melonList.size
}