package com.example.trueorfalseapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    var unlockMananger = UnlocksManager
    private var currentImageView: ImageView? = null
    private var selectedTitle: String? = null
    private var userName: String? = null
    private var selectedAvatarResId: Int = R.drawable.defaultavatar
    private val titles = listOf(
        "Novice",
        "Wise Owl",
        "Noble Stag",
        "Mystic Serpent",
        "Lone Wolf",
        "Persistent Pilgrim",
        "Rising Star",
        "Senior",
        "Veteran",
        "Heritage Holder"
    )
    private val r2title = "Lucky"
    private lateinit var scrollViewAvatar: ScrollView
    private lateinit var imageViewAvatar: ImageView
    private lateinit var avatarImageViews: Array<ImageView>
    private val avatarResIds = arrayOf(
        R.drawable.avatar_1,
        R.drawable.avatar_2,
        R.drawable.avatar_3,
        R.drawable.avatar_4,
        R.drawable.avatar_5,
        R.drawable.avatar_6,
        R.drawable.c1,
        R.drawable.c2,
        R.drawable.c3,
        R.drawable.c4,
        R.drawable.c5,
        R.drawable.c6,
        R.drawable.c7,
        R.drawable.uc1,
        R.drawable.uc2,
        R.drawable.uc3,
        R.drawable.r1
    )
    private lateinit var changeAvatarButton: Button
    private lateinit var changeTitleButton: Button
    private lateinit var changeNameButton: Button
    private lateinit var achievementButton: Button
    private lateinit var confirmButton: Button
    private lateinit var titleLinearLayout: LinearLayout
    private lateinit var textViewProfileTitle: TextView
    private lateinit var titleScrollView: ScrollView
    private lateinit var textViewProfileName: TextView
    private lateinit var editTextName: EditText
    private lateinit var buttonConfirmName: Button
    private lateinit var gridLayoutNameChange: GridLayout
    private lateinit var r2Title: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        titleLinearLayout = findViewById(R.id.titleLinearLayout)
        confirmButton = findViewById(R.id.confirmButton)
        scrollViewAvatar = findViewById(R.id.ScrollViewAvatar)
        changeAvatarButton = findViewById(R.id.buttonAvatar)
        imageViewAvatar = findViewById(R.id.imageViewAvatar)
        achievementButton = findViewById(R.id.buttonAchievements)
        textViewProfileTitle = findViewById(R.id.textViewProfileTitle)
        changeTitleButton = findViewById(R.id.buttonTitle)
        titleScrollView = findViewById(R.id.titleScrollView)
        changeNameButton = findViewById(R.id.buttonChangeName)
        textViewProfileName = findViewById(R.id.textViewProfileName)
        editTextName = findViewById(R.id.editTextViewName)
        buttonConfirmName = findViewById(R.id.buttonConfirmName)
        gridLayoutNameChange = findViewById(R.id.gridlayoutNameChange)

        avatarImageViews = arrayOf(
            findViewById(R.id.imageViewAvatar1),
            findViewById(R.id.imageViewAvatar2),
            findViewById(R.id.imageViewAvatar3),
            findViewById(R.id.imageViewAvatar4),
            findViewById(R.id.imageViewAvatar5),
            findViewById(R.id.imageViewAvatar6),
            findViewById(R.id.imageViewAvatarc1),
            findViewById(R.id.imageViewAvatarc2),
            findViewById(R.id.imageViewAvatarc3),
            findViewById(R.id.imageViewAvatarc4),
            findViewById(R.id.imageViewAvatarc5),
            findViewById(R.id.imageViewAvatarc6),
            findViewById(R.id.imageViewAvatarc7),
            findViewById(R.id.imageViewAvataruc1),
            findViewById(R.id.imageViewAvataruc2),
            findViewById(R.id.imageViewAvataruc3),
            findViewById(R.id.imageViewAvatarr1)
        )

        loadSavedAvatar()
        loadSavedTitle()
        loadtitles()
        loadUserName()
        checkUnlocks()

        for ((index, imageView) in avatarImageViews.withIndex()) {
            imageView.setOnClickListener {
                Log.d("ProfileActivity", "ImageView clicked: ${avatarResIds[index]}")
                selectAvatar(avatarResIds[index], imageView)
            }
        }
        changeNameButton.setOnClickListener {
            hideAllButtons()
            changeNameVisibility(gridLayoutNameChange)
        }

        buttonConfirmName.setOnClickListener {
            val userInput = editTextName.text.toString().trim()
            if (userInput.isNotEmpty()) {
                textViewProfileName.text = editTextName.text.toString()
                saveUserName(userInput)
                changeNameVisibility(gridLayoutNameChange)
                showAllButtons()
            } else {
                Toast.makeText(this, "Blank Text is not allowed", Toast.LENGTH_SHORT).show()
            }
        }


        changeTitleButton.setOnClickListener {
            hideAllButtons()
            changeTitleVisibility(titleScrollView)
        }

        confirmButton.setOnClickListener {
            Log.d("ProfileActivity", "Confirm clicked")
            changeMainImage()
            changeAvatarListVisibility(scrollViewAvatar)
            saveSelectedAvatar()
            showAllButtons()
        }

        changeAvatarButton.setOnClickListener {
            hideAllButtons()
            changeAvatarListVisibility(scrollViewAvatar)
        }
        achievementButton.setOnClickListener {
            val intent = Intent(this, AchievementActivity::class.java)
            startActivity(intent)
        }
    }
    private fun hideAllButtons(){
        changeAvatarButton.visibility=View.GONE
        changeTitleButton.visibility=View.GONE
        changeNameButton.visibility=View.GONE
        achievementButton.visibility=View.GONE
    }
    private fun showAllButtons(){
        changeAvatarButton.visibility=View.VISIBLE
        changeTitleButton.visibility=View.VISIBLE
        changeNameButton.visibility=View.VISIBLE
        achievementButton.visibility=View.VISIBLE
    }
    private fun selectAvatar(avatarResId: Int, imageView: ImageView) {
        // Remove border from previously selected ImageView
        if (currentImageView != null && currentImageView != imageView) {
            currentImageView?.setBackgroundResource(0)
            Log.d("ProfileActivity", "Border removed from previous ImageView")
        }

        // Toggle border and set selected avatar
        if (currentImageView == imageView) {
            imageView.setBackgroundResource(0) // Remove border if the same ImageView is clicked again
            currentImageView = null
            selectedAvatarResId = R.drawable.defaultavatar // Reset to default avatar
            Log.d("ProfileActivity", "Border removed from current ImageView")
        } else {
            imageView.setBackgroundResource(R.drawable.border)
            currentImageView = imageView
            selectedAvatarResId = avatarResId
            Log.d(
                "ProfileActivity",
                "Border added to current ImageView, selected avatar: $selectedAvatarResId"
            )
        }
    }

    private fun changeMainImage() {
        imageViewAvatar.setImageResource(selectedAvatarResId)
        Log.d("ProfileActivity", "Main ImageView updated with avatar: $selectedAvatarResId")
    }

    private fun changeNameVisibility(gridlayout: GridLayout) {
        if (gridlayout.visibility == View.VISIBLE) {
            gridlayout.visibility = View.INVISIBLE
        } else {
            gridlayout.visibility = View.VISIBLE
        }
    }

    private fun changeAvatarListVisibility(scrollViewAvatar: ScrollView) {
        if (scrollViewAvatar.visibility == View.VISIBLE) {
            scrollViewAvatar.visibility = View.INVISIBLE
        } else {
            scrollViewAvatar.visibility = View.VISIBLE
        }
    }

    private fun changeTitleVisibility(titleScrollView: ScrollView) {
        if (titleScrollView.visibility == View.VISIBLE) {
            titleScrollView.visibility = View.INVISIBLE
            showAllButtons()
        } else {
            titleScrollView.visibility = View.VISIBLE
        }
    }

    private fun loadtitles() {
        val titleLinearLayout: LinearLayout = findViewById(R.id.titleLinearLayout)

        // Dynamically add TextViews
        for (title in titles) {
            val textView = TextView(this).apply {
                text = title
                textSize = 25f
                setPadding(0, 80, 0, 0)
                setOnClickListener {
                    // Clear previous selections
                    for (i in 0 until titleLinearLayout.childCount) {
                        val child = titleLinearLayout.getChildAt(i) as? TextView
                        child?.setBackgroundColor(Color.TRANSPARENT)
                    }
                    // Set background color to indicate selection
                    setBackgroundColor(Color.LTGRAY)
                    textViewProfileTitle.text = title
                    saveSelectedTitle(title)
                    changeTitleVisibility(titleScrollView)
                }
            }
            titleLinearLayout.addView(textView)
        }
        r2Title = TextView(this).apply {
            textSize = 25f
            text="Lucky"
            setPadding(0, 80, 0, 0)
            visibility = View.GONE
            textColors.defaultColor
            setOnClickListener {
                // Clear previous selections
                for (i in 0 until titleLinearLayout.childCount) {
                    val child = titleLinearLayout.getChildAt(i) as? TextView
                    child?.setBackgroundColor(Color.TRANSPARENT)
                }
                // Set background color to indicate selection
                setBackgroundColor(Color.LTGRAY)
                textViewProfileTitle.text = "Lucky"
                saveSelectedTitle(r2title)
                changeTitleVisibility(titleScrollView)
            }
        }
        titleLinearLayout.addView(r2Title)
    }

    private fun saveSelectedTitle(title: String) {
        val sharedPreferences = getSharedPreferences("ProfilePreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("selectedTitle", title).apply()
        Log.d("ProfileActivity", "Selected title saved: $title")
    }

    private fun loadSavedTitle() {
        val sharedPreferences = getSharedPreferences("ProfilePreferences", Context.MODE_PRIVATE)
        selectedTitle = sharedPreferences.getString("selectedTitle", null)
        textViewProfileTitle.text = selectedTitle
        textViewProfileTitle.setTextColor(Color.BLACK)
        textViewProfileTitle.setTypeface(null,Typeface.BOLD)
        Log.d("ProfileActivity", "Loaded saved title: $selectedTitle")
        // Handle setting the loaded title to the UI, if needed
    }

    private fun saveUserName(username: String) {
        val sharedPreferences = getSharedPreferences("ProfilePreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("Username", username).apply()
        Log.d("CurrentUsername", "Current Username: $username")
    }

    private fun loadUserName() {
        val sharedPreferences = getSharedPreferences("ProfilePreferences", Context.MODE_PRIVATE)
        userName = sharedPreferences.getString("Username", "AverageJoe")
        textViewProfileName.text = userName
        textViewProfileName.setTextColor(Color.BLACK)
        textViewProfileName.setTypeface(null,Typeface.BOLD)
        Log.d("ProfileActivity", "Loaded Username:$userName")
    }

    private fun saveSelectedAvatar() {
        val sharedPreferences = getSharedPreferences("ProfilePreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("selectedAvatarResId", selectedAvatarResId)
            apply()
        }
        Log.d("ProfileActivity", "Selected avatar saved: $selectedAvatarResId")
    }


    private fun loadSavedAvatar() {
        val sharedPreferences = getSharedPreferences("ProfilePreferences", Context.MODE_PRIVATE)
        selectedAvatarResId =
            sharedPreferences.getInt("selectedAvatarResId", R.drawable.defaultavatar)
        imageViewAvatar.setImageResource(selectedAvatarResId)
        Log.d("ProfileActivity", "Loaded saved avatar: $selectedAvatarResId")

        // Set the border to the corresponding ImageView if the avatar was saved
        for ((index, resId) in avatarResIds.withIndex()) {
            if (selectedAvatarResId == resId) {
                avatarImageViews[index].setBackgroundResource(R.drawable.border)
                currentImageView = avatarImageViews[index]
                break
            }
        }
    }

    private fun checkUnlocks() {
        val status = unlockMananger.getUnlock(this)
        val unlockKeys = arrayOf(
            "c1",
            "c2",
            "c3",
            "c4",
            "c5",
            "c6",
            "c7",
            "uc1",
            "uc2",
            "uc3",
            "r1",
            "r2",
            "s1"
        )
        val unlockStatuses = unlockKeys.map { key ->
            val unlocked=status?.get(key) ?: false
            Log.d("ProfileActivity", "$key unlocked: $unlocked")
            unlocked
        }
        for (i in 6 until avatarImageViews.size) {
            avatarImageViews[i].visibility = if (unlockStatuses[i - 6]) View.VISIBLE else View.GONE
        }
        if (unlockStatuses[11]) {
            r2Title.visibility = View.VISIBLE
            Log.d("ProfileActivity", "r2Title visibility set to VISIBLE")
        } else {
            r2Title.visibility = View.GONE
            Log.d("ProfileActivity", "r2Title visibility set to GONE")
        }
        if (unlockStatuses[12]) {
            textViewProfileName.setTextColor(Color.parseColor("#DAA520"))
        }
    }
}


