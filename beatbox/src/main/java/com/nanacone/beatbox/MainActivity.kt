package com.nanacone.beatbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanacone.beatbox.databinding.ActivityMainBinding
import com.nanacone.beatbox.databinding.ListItemSoundBinding

class MainActivity : AppCompatActivity() {

    private lateinit var beatBox: BeatBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        beatBox = BeatBox(assets)
//        beatBox.loadSounds()

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = SoundAdapter(beatBox.sounds)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        beatBox.release()
    }
    // list_item_sound.xml 과 연결
    private inner class SoundHolder(private val binding: ListItemSoundBinding) :
            RecyclerView.ViewHolder(binding.root) {
                init {
                    // list_item_sound.xml 에 viewmodel <data> 선언으로 ListItemSoundBinding 에서 viewmodel 을 속성으로 가짐
                    binding.viewModel = SoundViewModel(beatBox) // 초기화

                }

                fun bind(sound: Sound) {
                    binding.apply {
                        viewModel?.sound = sound
                        executePendingBindings()
                    }
                }
            }

    // SoundHolder와 연결되는 Adapter 생성
    private inner class SoundAdapter(private val sounds: List<Sound>) : RecyclerView.Adapter<SoundHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundHolder {
            val binding = DataBindingUtil.inflate<ListItemSoundBinding>(
                layoutInflater,
                R.layout.list_item_sound,
                parent,
                false
            )
            return SoundHolder(binding)
        }

        override fun onBindViewHolder(holder: SoundHolder, position: Int) {
            val sound = sounds[position]
            // 이미 SoundAdapter 생성되면서 createViewHolder 에서 holder 를 return 해주고 그 return 된 holder가 매개변수로 받은 holder.
            // 이 bind 함수를 호출하여 뷰모델의 각 Sound 인스턴스(viewModel?.sound)를 SoundHolder 인스턴스와 연결
            holder.bind(sound)
        }

        override fun getItemCount(): Int = sounds.size

    }
}