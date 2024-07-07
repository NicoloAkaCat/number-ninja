package dev.nicoloakacat.numberninja

import android.view.View
import androidx.constraintlayout.widget.Group

val show = { v: Group -> v.visibility = View.VISIBLE }
val hide = { v: Group -> v.visibility = View.GONE }
val getFlagUri = { nationality: String ->
    "dev.nicoloakacat.numberninja:drawable/flag_" +
            Nationality.valueOf(nationality.replace(" ","_")).code.lowercase()
}