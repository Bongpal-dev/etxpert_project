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
        itemLongClick = object : MainAdapter.ItemLongClick {
            override fun longClick(item: Item) {
                val listener = object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                Items.removeUser(item.userName)
                                binding.mainRecyclerView.adapter?.notifyDataSetChanged()
                            }

                            DialogInterface.BUTTON_NEGATIVE -> {
                                return
                            }
                        }
                    }
                }
                var builder = AlertDialog.Builder(this@MainActivity)

                with(builder) {
                    setTitle("상품삭제")
                    setMessage("상품을 정말로 삭제하시겠습니까?")
                    setIcon(R.drawable.chat)
                    setPositiveButton("확인", listener)
                    setNegativeButton("취소", listener)
                    show()
                }
            }
        }
    }

    fun MainAdapter.initClickItem() {
        val intent = Intent(this@MainActivity, DetailActivity::class.java)

        itemClick = object : MainAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                intent.putExtra(EXTRA_ITEM, Items.getIndex(position))
                startActivity(intent)
            }
        }
    }

    fun initNotiButton() {
        binding.btnAlarm.setOnClickListener {
            notification("new-keyword", "Channel New Keyword")
        }
    }

    fun notification(id: String, name: String) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder

        checkPermission()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance).apply {
                val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()

                description = "Channel New Keyword Description"
                setSound(uri, audioAttributes)
                setShowBadge(true)
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
            builder = NotificationCompat.Builder(this, id)
        } else {
            builder = NotificationCompat.Builder(this)
        }
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent
            .getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        builder.run {
            setSmallIcon(R.drawable.heart_on)
            setWhen(System.currentTimeMillis())
            setContentTitle("키워드 알림")
            setContentText("설정한 키워드에 대한 알림이 도착했습니다!!")
            setContentIntent(pendingIntent)
            setAutoCancel(true)
            setOngoing(false)
        }
        manager.notify(77, builder.build())
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
        binding.mainRecyclerView.setOnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            val fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            val fadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.fade_out)

            with(binding.fabMainRetrunToTop) {
                if (scrollY > oldScrollY && visibility == View.GONE) {
                    startAnimation(fadeInAnim)
                    visibility = View.VISIBLE
                }

                if (scrollY + 2 < oldScrollY && visibility == View.VISIBLE) {
                    startAnimation(fadeOutAnim)
                    visibility = View.GONE
                }
            }
        }
        binding.fabMainRetrunToTop.setOnClickListener {
            binding.mainRecyclerView.smoothScrollToPosition(0)
        }
    }

    override fun onBackPressed() {
        var builder = AlertDialog.Builder(this)
        val listener = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> finish()
                    DialogInterface.BUTTON_NEGATIVE -> return
                }
            }
        }

        with(builder) {
            setTitle("종료")
            setMessage("종료 하시겠습니까?")
            setIcon(R.drawable.chat)
            setPositiveButton("종료", listener)
            setNegativeButton("취소", listener)
            show()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mainRecyclerView.adapter?.notifyDataSetChanged()
    }
}