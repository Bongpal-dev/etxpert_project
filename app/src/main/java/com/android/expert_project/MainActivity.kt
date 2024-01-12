package com.android.expert_project

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.expert_project.adapter.MainAdapter
import com.android.expert_project.data.Item
import com.android.expert_project.databinding.ActivityMainBinding

const val EXTRA_ITEM = "item"

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUp()
    }

    private fun setUp() {
        initRecyclerAdapter()
        initNotiButton()
        initRetrunButton()
    }

    private fun initRecyclerAdapter() {
        val mainAdapter = MainAdapter(Items.getAll())

        binding.mainRecyclerView.apply {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        }
        mainAdapter.initLongClickItem()
        mainAdapter.initClickItem()
    }

    fun MainAdapter.initLongClickItem() {
        object : MainAdapter.ItemLongClick {
            override fun onLongClick(item: Item, position: Int) {
                buildDialog(
                    "상품삭제",
                    "상품을 정말로 삭제하시겠습니까?",
                    "확인",
                    "취소"
                ) {
                    Items.removeUser(item.userName)
                    binding.mainRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
        }.also { itemLongClick = it }
    }

    fun MainAdapter.initClickItem() {
        object : MainAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)

                intent.putExtra(EXTRA_ITEM, Items.getIndex(position))
                startActivity(intent)
            }
        }.also { itemClick = it }
    }

    fun initNotiButton() {
        binding.btnAlarm.setOnClickListener {
            callNotification("new-keyword", "Channel New Keyword")
        }
    }

    fun callNotification(id: String, name: String) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder = makeBuilder(id, name, manager)
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent
            .getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        checkPermission()
        builder.apply {
            setSmallIcon(R.drawable.heart_on)
            setWhen(System.currentTimeMillis())
            setContentTitle("키워드 알림")
            setContentText("설정한 키워드에 대한 알림이 도착했습니다!!")
            setContentIntent(pendingIntent)
            setAutoCancel(true)
            setOngoing(false)
        }.also { manager.notify(77, it.build()) }
    }

    private fun makeBuilder(
        id: String,
        name: String,
        manager: NotificationManager
    ): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder().run {
                setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                setUsage(AudioAttributes.USAGE_ALARM)
                build()
            }
            val channel = NotificationChannel(id, name, importance).apply {
                description = "Channel New Keyword Description"
                setSound(uri, audioAttributes)
                setShowBadge(true)
                enableVibration(true)
            }

            manager.createNotificationChannel(channel)
            return NotificationCompat.Builder(this, id)
        } else {
            return NotificationCompat.Builder(this)
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
                startActivity(intent)
            }
        }
    }

    private fun initRetrunButton() {
        val fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        val recycler = binding.mainRecyclerView
        val fabReturn = binding.fabMainRetrunToTop

        recycler.setOnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            with(fabReturn) {
                if (scrollY > oldScrollY && visibility == View.GONE) {
                    startAnimation(fadeInAnim)
                    visibility = View.VISIBLE
                }

                if (scrollY + 2 < oldScrollY && visibility == View.VISIBLE) {
                    startAnimation(fadeOutAnim)
                    visibility = View.GONE
                }
                setOnClickListener {
                    recycler.smoothScrollToPosition(0)
                }
            }
        }
    }

    override fun onBackPressed() {
        buildDialog(
            "종료",
            "종료 하시겠습니까?",
            "종료",
            "취소"
        ) {
            finish()
        }
    }

    private fun buildDialog(
        title: String,
        msg: String,
        positive: String,
        negative: String,
        positiveAction: () -> Unit
    ) {
        val listener = DialogInterface.OnClickListener { _, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                positiveAction()
            }
        }

        AlertDialog.Builder(this).run {
            setTitle(title)
            setMessage(msg)
            setIcon(R.drawable.chat)
            setPositiveButton(positive, listener)
            setNegativeButton(negative, listener)
            show()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mainRecyclerView.adapter?.notifyDataSetChanged()
    }
}