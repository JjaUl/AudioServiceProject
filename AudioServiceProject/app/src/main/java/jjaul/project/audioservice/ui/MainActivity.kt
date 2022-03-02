package jjaul.project.audioservice.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import jjaul.project.audioservice.R
import jjaul.project.audioservice.databinding.ActivityMainBinding
import jjaul.project.audioservice.ui.fragment.DetailPlayFragment
import jjaul.project.audioservice.ui.fragment.MediaControllerFragment
import jjaul.project.audioservice.ui.fragment.PlayListFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        attachControllerFragment()
        attachPlayListFragment()
    }

    private fun attachControllerFragment() {
        supportFragmentManager.beginTransaction().replace(binding.controllerFrame.id, MediaControllerFragment()).commit()
    }

    public fun attachPlayListFragment() {
        supportFragmentManager.beginTransaction().replace(binding.listsFrame.id, PlayListFragment()).commit()
    }

    public fun attachDetailFragment() {
        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_from_right,R.anim.slide_to_left).replace(binding.detailFrame.id, DetailPlayFragment()).commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}