package com.example.project_android_booking_movietickets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.project_android_booking_movietickets.fragment.home.HomeFragment
import com.example.project_android_booking_movietickets.fragment.home.HomeManagementFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, HomeManagementFragment.OnFragmentInteractionListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navigationView: NavigationView
    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        navigationView = findViewById(R.id.nav_view)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)

        // Lấy role người dùng từ Intent
        userRole = intent.getStringExtra("USER_ROLE")

        // Mở Drawer khi click vào Menu
        btnMenu.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START)
            } else {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        // Đăng ký NavigationView listener
        navigationView.setNavigationItemSelectedListener(this)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null

            when (item.itemId) {
                R.id.nav_home -> {
                    selectedFragment = if (userRole == "Nhân viên") {
                        HomeManagementFragment()
                    } else {
                        HomeFragment()
                    }
                }
                R.id.nav_logout -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }

            // Thay thế Fragment hiện tại bằng Fragment đã chọn
            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }

            // Đóng Drawer (nếu có)
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Kiểm tra và hiển thị fragment theo userRole
        if (savedInstanceState == null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            val fragment = when (userRole) {
                "Nhân viên" -> HomeManagementFragment()
                else -> HomeFragment()
            }
            fragmentTransaction.replace(R.id.fragment_container, fragment).commit()
        }
    }


    override fun onFragmentChange(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var selectedFragment: Fragment? = null

        // Kiểm tra item đã chọn
        when (item.itemId) {
            R.id.nav_trangchu -> {
                selectedFragment = if (userRole == "Nhân viên") {
                    HomeManagementFragment()
                } else {
                    HomeFragment()
                }
            }
            R.id.nav_logout -> {
                // Đăng xuất và chuyển về màn hình đăng nhập
                Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show()

                // Xóa thông tin người dùng đã lưu
                val sharedPref = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.clear() // Xóa dữ liệu người dùng
                editor.apply()

                // Chuyển sang màn hình đăng nhập
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                return true // Để ngừng xử lý thêm
            }
        }

        // Nếu có selectedFragment, thay thế fragment hiện tại
        selectedFragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, it)
                .commit()
        }

        // Đóng menu sau khi chọn
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}