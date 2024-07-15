package com.student.competishun.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.student.competishun.R
import com.student.competishun.databinding.ActivityMainBinding
import com.student.competishun.databinding.ActivityOnBoardingBinding
import com.student.competishun.ui.adapter.OnboardingPagerAdapter
import com.student.competishun.ui.fragment.OnBoardingFragment

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        val fragments = listOf(
            OnBoardingFragment.newInstance(R.layout.fragment_on_boarding),
            OnBoardingFragment.newInstance(R.layout.onboarding_2),
            OnBoardingFragment.newInstance(R.layout.onboarding_3),
            OnBoardingFragment.newInstance(R.layout.onboarding_4),
        )
        val adapter = OnboardingPagerAdapter(this, fragments)
        viewPager.adapter = adapter
    }
}