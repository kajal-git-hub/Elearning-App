package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentDrawerVisibilityBinding

open class DrawerVisibility : Fragment() {

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var binding : FragmentDrawerVisibilityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawerLayout = requireActivity().findViewById(R.id.drwaer_layout)

    }

    override fun onResume() {
        super.onResume()
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun onPause() {
        super.onPause()
        // Unlock the drawer when leaving the fragment
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

}