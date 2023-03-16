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
    private lateinit var soundViewModel: SoundViewModel

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

        binding.seekbar.progress = 5
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
//                seekBar?.progress =
//                    if(seekBar?.progress!! % 10 < 5) (seekBar.progress/10) * 10 // progress 는 5 = 0.5 / 12 -> 1 * 10 = 10
//                    else (seekBar.progress/10) * 15 // 17 -> 1 * 15 = 15

                // text 설정
                binding.seekbarTitle.text = String.format(getString(R.string.seekbar_title), "${(seekBar?.progress ?: 5)/10.0}")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) { // seekbar 조작이 끝났을 때 작동
                // text 설정
                binding.seekbarTitle.text = String.format(getString(R.string.seekbar_title), "${(seekBar?.progress ?: 10)/10.0}")
//                soundViewModel.sound?.let { beatBox.play(sound = it, seekBar?.progress) }

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
                    soundViewModel = SoundViewModel(beatBox)
//                    binding.viewModel = SoundViewModel(beatBox) // 초기화
                    binding.viewModel = soundViewModel // 초기화

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