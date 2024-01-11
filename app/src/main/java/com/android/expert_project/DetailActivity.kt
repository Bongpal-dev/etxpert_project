package com.android.expert_project

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.android.expert_project.adapter.decimal
import com.android.expert_project.data.Item
import com.android.expert_project.databinding.ActivityDetailBinding
import com.google.android.material.snackbar.Snackbar

const val TAG = "expert_project_log"

class DetailActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    private lateinit var item: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUp()
    }

    private fun setUp() {
        getValue()
        initView()
        initInput()
    }

    private fun getValue() {
        item = intent.getParcelableExtra<Item>(EXTRA_ITEM) ?: Item()
    }

    private fun initView() {
        with(binding) {
            tvDetailTitle.text = item.title
            tvDetailAddress.text = item.address
            tvDetailIntro.text = item.intro
            tvDetailUserName.text = item.userName
            ivDetailMainImage.setImageResource(item.image)
            tvDetailPrice.text = item.price.decimal()
            tvDetailCategory.text = item.category
            checkHeart(item, btnDetailHeart)
        }
    }

    private fun initInput() {
        clickHeart()
        clickBack()
    }

    private fun clickHeart() {
        val user = item.userName
        val heart = binding.layoutHeart

        heart.setOnClickListener {
            Items.findByUser(user).run {
                heartOn = heartOn.not()
                checkHeart(this, binding.btnDetailHeart)

                if (heartOn) {
                    Items.plusHeart(user)
                    Snackbar.make(binding.root, "관심 목록에 추가되었습니다.", Snackbar.LENGTH_SHORT).show()
                } else {
                    Items.minusHeart(user)
                }
            }
        }
    }

    private fun clickBack() {
        binding.btnDetailBack.setOnClickListener {
            finish()
        }
    }
}

fun checkHeart(item: Item, heart: ImageView) {
    if (item.heartOn) {
        heart.setImageResource(R.drawable.heart_on)
        return
    }
    heart.setImageResource(R.drawable.heart)
}