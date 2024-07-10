package dev.nicoloakacat.numberninja

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.Group
import com.google.android.material.chip.Chip

val show = { v: Group -> v.visibility = View.VISIBLE }
val hide = { v: Group -> v.visibility = View.GONE }
val getFlagUri = { nationality: String ->
    "dev.nicoloakacat.numberninja:drawable/flag_" +
            Nationality.valueOf(nationality.replace(" ","_")).code.lowercase()
}

val showResultMessage = { resMsg: Chip, context: Context ->
    resMsg.visibility = View.VISIBLE
    resMsg.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
    resMsg.postDelayed({
        resMsg.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
        resMsg.visibility = View.INVISIBLE
    },3000)
}