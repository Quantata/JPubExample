package com.nanacone.beatbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.SeekBar
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

        binding.seekbar.progress = 10
        binding.seekbarTitle.text = String.format(getString(R.string.seekbar_title), binding.seekbar.progress/10.0)


        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { // seekbar 조작하고 있는 중에 작동
                if(progress <= 5) {
                    seekBar?.progress = 5 // 5 이하로는 안떨어지게
                }
                seekBar?.progress =
                    if(progress % 5 == 0) progress
                    else if(progress % 10 < 5) (progress/10) * 10 // progress 는 5 = 0.5 / 12 -> 1 * 10 = 10
                    else (progress/10) * 10 + 5 // 17 -> 1*10 + 5 = 15

                binding.seekbarTitle.text  = String.format(getString(R.string.seekbar_title), seekBar?.progress!!/10.0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { // seekbar 조작이 시작했을 때 작동
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) { // seekbar 조작이 끝났을 때 작동
                // 모든 SoundViewModel에는 beatbox가 들어가 있음.
                // 해당 beatbox는 단 하나의 객체
                // item마다 갖고 있는 각 SoundViewModel에서 바라보는 beatBox는 하나
                // beatBox의 palySpeed가 달라지면 SoundViewModel에서 이 sound로 beatbox에서 호출해 주세요~
                // 할때 설정된 beatBox의 재생 속도로 재생됨.
                beatBox.playSpeed = seekBar?.progress!!/10.0.toFloat()
//                soundViewModel.setPlaySpeed(seekBar?.progress!!/10.0.toFloat())
//                soundViewModel.sound?.let { beatBox.play(it, seekBar?.progress!!/ 10.0.toFloat()) }
            }
        })
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
                    binding.viewModel = SoundViewModel(beatBox) // 초기화 // sound 마다 viewModel 이 있는 것임
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